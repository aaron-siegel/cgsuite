package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.ElaborationException
import org.cgsuite.lang._
import org.cgsuite.lang.node.LoopNode._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree

import scala.collection.mutable


object LoopNode {

  def apply(tree: Tree): LoopNode = {

    val loopType = tree.getType match {
      case DO => Do
      case YIELD | LISTOF => YieldList
      case MAPOF => YieldMap
      case SETOF => YieldSet
      case TABLEOF => YieldTable
      case SUMOF => YieldSum
    }
    val body = EvalNode(tree.children.last)
    val loopSpecs = tree.children dropRight 1
    assert(loopSpecs.forall { _.getType == LOOP_SPEC })

    def makeLoopNode(loopSpecTree: Tree, nextNode: EvalNode): LoopNode = {
      LoopNode(
        loopSpecTree,
        loopType,
        loopSpecTree.children find { _.getType == FOR   } map { t => IdentifierNode(t.head) },
        loopSpecTree.children find { _.getType == IN    } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == FROM  } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == TO    } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == BY    } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == WHILE } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == WHERE } map { t => EvalNode(t.head) },
        nextNode
      )
    }

    loopSpecs.foldRight(body)(makeLoopNode).asInstanceOf[LoopNode]

  }

  sealed trait LoopType
  case object Do extends LoopType
  case object YieldList extends LoopType
  case object YieldMap extends LoopType
  case object YieldSet extends LoopType
  case object YieldTable extends LoopType
  case object YieldSum extends LoopType

}

case class LoopNode(
  tree    : Tree,
  loopType: LoopNode.LoopType,
  forId   : Option[IdentifierNode],
  in      : Option[EvalNode],
  from    : Option[EvalNode],
  to      : Option[EvalNode],
  by      : Option[EvalNode],
  `while` : Option[EvalNode],
  where   : Option[EvalNode],
  body    : EvalNode
  ) extends EvalNode {

  override val children = forId.toSeq ++ in ++ from ++ to ++ by ++ `while` ++ where :+ body

  private val isYield: Boolean = loopType match {
    case LoopNode.Do => false
    case LoopNode.YieldList | LoopNode.YieldMap | LoopNode.YieldSet | LoopNode.YieldTable | LoopNode.YieldSum => true
  }

  private val pushDownYield: Option[LoopNode] = (isYield, body) match {
    case (true, loopBody: LoopNode) if loopType != LoopNode.YieldSum =>
      assert(loopBody.isYield)
      Some(loopBody)
    case _ => None
  }

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    val inType = in map { _.ensureElaborated(domain) }
    val fromType = from map { _.ensureElaborated(domain) }
    val toType = to map { _.ensureElaborated(domain) }
    val byType = by map { _.ensureElaborated(domain) }

    // TODO Validate to/by

    val mostGeneralCollection = CgscriptType(CgscriptClass.Collection, Vector(CgscriptType(CgscriptClass.Object)))

    inType foreach { typ =>
      if (!(typ matches mostGeneralCollection))
        throw ElaborationException(s"Object of type `${typ.qualifiedName}` cannot be expanded as a Collection", in.get.tree)
    }

    forId match {

      case Some(idNode) =>
        assert(fromType.isDefined || inType.isDefined)      // Guaranteed by parser
        assert(inType forall { _.typeArguments.nonEmpty }, inType)  // Since it extends Collection - TODO - we should really look at the Collection ancestor
        val forIdType = fromType getOrElse inType.get.typeArguments.head
        domain.pushScope()
        domain.insertId(idNode.id, forIdType)

      case None =>

    }

    val whileType = `while` map { _.ensureElaborated(domain) }
    val whereType = where map { _.ensureElaborated(domain) }

    // TODO Validate where/while as Boolean

    val bodyType = body.ensureElaborated(domain)

    forId match {
      case Some(_) => domain.popScope()
      case None =>
    }

    pushDownYield match {
      case Some(loopNode) => loopNode.elaboratedType
      case None =>
        loopType match {
          case LoopNode.Do => CgscriptType(CgscriptClass.NothingClass)
          case LoopNode.YieldList => CgscriptType(CgscriptClass.List, Vector(bodyType))
          case LoopNode.YieldSet => CgscriptType(CgscriptClass.Set, Vector(bodyType))
          case LoopNode.YieldMap => sys.error("TODO")   // TODO
          case LoopNode.YieldTable =>
            if (!bodyType.baseClass.ancestors.contains(CgscriptClass.List))
              throw ElaborationException(
                s"Table row of type `${bodyType.qualifiedName}` cannot be converted to class `cgsuite.lang.List`",
                tree
              )
            else
              CgscriptType(CgscriptClass.Table)
          case LoopNode.YieldSum =>
            var closureType = bodyType
            if (closureType.baseClass == CgscriptClass.NothingClass) {
              CgscriptType(CgscriptClass.Integer)
            } else {
              var done = false
              while (!done) {
                val closureOp = closureType.resolveMethod(Symbol("+"), Vector(closureType)) getOrElse {
                  throw ElaborationException(
                    s"Operation `+` is not closed over arguments of type `${closureType.qualifiedName}`",
                    tree
                  )
                }
                val nextClosureType = closureType join closureOp.ensureElaborated()
                if (closureType == nextClosureType)
                  done = true
                else
                  closureType = nextClosureType
              }
              closureType
            }
        }
    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitScalaCode(context, emitter, None)
  }

  def emitScalaCode(context: CompileContext, emitter: Emitter, pushdownYieldVar: Option[String]): Unit = {

    val continueVar = context.newTempId()
    val tempResultVar = context.newTempId()
    val byVarOpt = by map { _ => context.newTempId() }
    val loopVar = {
      if (forId.isDefined)
        forId.get.id.name
      else if (from.isDefined)
        context.newTempId()
      else
        ""
    }
    val iteratorVar = {
      if (in.isDefined)
        context.newTempId()
      else
        ""
    }
    val yieldResultVar = {
      if (isYield)
        pushdownYieldVar getOrElse context.newTempId()
      else
        ""
    }

    emitter println "{  // Begin loop"
    emitter.indent()

    // Emit initialization code

    emitter println s"var $continueVar = true"

    from foreach { fromNode =>
      // TODO Validate loopVar is Integer
      emitter print s"var $loopVar: org.cgsuite.core.Integer = "
      fromNode.emitScalaCode(context, emitter)
      emitter println ""
      by foreach { byNode =>
        emitter print s"var ${byVarOpt.get} = "
        byNode.emitScalaCode(context, emitter)
        emitter println ""
      }
    }

    in foreach { inNode =>
      emitter print s"val $iteratorVar = "
      inNode.emitScalaCode(context, emitter)
      emitter println ".iterator"
    }

    if (isYield && pushdownYieldVar.isEmpty) {
      loopType match {
        case YieldSum =>
          emitter println s"var $yieldResultVar: ${elaboratedType.scalaTypeName} = null"
        case YieldTable =>
          emitter println s"val $yieldResultVar = new scala.collection.mutable.ArrayBuffer[IndexedSeq[_]]"
        case _ =>
          val yieldType = elaboratedType.typeArguments.head
          emitter println s"val $yieldResultVar = new scala.collection.mutable.ArrayBuffer[${yieldType.scalaTypeName}]"
      }
    }

    // Emit main loop

    emitter println s"while ($continueVar) {"
    emitter.indent()
    emitter println "if (Thread.interrupted())"
    emitter.indent()
    emitter println """throw org.cgsuite.exception.CalculationCanceledException("Calculation canceled by user."/*, token = Some(token)*/)"""
    emitter.indent(-1)

    // Emit code to check if from-loop is done

    to foreach { toNode =>
      byVarOpt match {
        case Some(byVar) =>
          emitter print s"$continueVar = if ($byVar < org.cgsuite.core.Values.zero) $loopVar >= "
          toNode.emitScalaCode(context, emitter)
          emitter print s" else $loopVar <= "
          toNode.emitScalaCode(context, emitter)
        case None =>
          emitter println s"$continueVar = $loopVar <= "
          toNode.emitScalaCode(context, emitter)
      }
      emitter println ""
    }

    // Emit code to check if in-loop is done

    in foreach { _ =>
      emitter println s"$continueVar = $iteratorVar.hasNext"
    }

    emitter println s"if ($continueVar) {"
    emitter.indent()

    // Emit code to increment the iterator

    in foreach { _ =>
      emitter println s"val $loopVar = $iteratorVar.next();"
    }

    // Emit code to check if while condition is met

    `while` foreach { whileNode =>
      emitter print s"$continueVar = "
      whileNode.emitScalaCode(context, emitter)
      emitter println ""
      emitter println s"if ($continueVar) {"
      emitter.indent()
    }

    // Emit code to check if where condition is met

    where foreach { whereNode =>
      emitter print "if ("
      whereNode.emitScalaCode(context, emitter)
      emitter println ") {"
      emitter.indent()
    }

    // Emit loop body

    if (isYield) {
      emitter println s"var $tempResultVar = {"
      emitter.indent()
    }

    pushDownYield match {
      case Some(loopBody) => loopBody.emitScalaCode(context, emitter, Some(yieldResultVar))
      case None => body.emitScalaCode(context, emitter)
    }

    emitter println ""

    if (isYield) {
      emitter.indent(-1)
      emitter println "}"
    }

    // Emit code for yield-update

    if (isYield && loopType == LoopNode.YieldSum) {
      emitter println s"$yieldResultVar = if ($yieldResultVar == null) $tempResultVar else $yieldResultVar + $tempResultVar"
    } else if (isYield && pushDownYield.isEmpty) {
      emitter println s"$yieldResultVar += $tempResultVar"
    }

    if (where.isDefined) {
      emitter.indent(-1)
      emitter println "}"
    }

    // Emit increment code

    if (from.isDefined) {
      emitter print s"$loopVar = $loopVar + "
      by match {
        case Some(byNode) => byNode.emitScalaCode(context, emitter)
        case None => emitter print "org.cgsuite.core.Values.one"
      }
      emitter println ""
    }

    // Close brackets

    if (`while`.isDefined) {
      emitter.indent(-1)
      emitter println "}"
    }

    emitter.indent(-1)
    emitter println "}"

    emitter.indent(-1)
    emitter println "}"

    // Create return value

    emitter println {
      loopType match {
        case LoopNode.Do => "null"
        case LoopNode.YieldList => s"$yieldResultVar.toVector"
        case LoopNode.YieldMap => s"$yieldResultVar.asInstanceOf[scala.collection.mutable.ArrayBuffer[(Any, Any)]].toMap"
        case LoopNode.YieldSet => s"$yieldResultVar.toSet"
        case LoopNode.YieldTable => s"org.cgsuite.lang.Table($yieldResultVar.toIndexedSeq)(org.cgsuite.lang.CgscriptClass.instanceToOutput)"
        case LoopNode.YieldSum => s"if ($yieldResultVar == null) org.cgsuite.core.Values.zero else $yieldResultVar"
      }
    }

    emitter.indent(-1)
    emitter println "}  // End loop"

    /*
    s"""{
       |  var $continueVar = true
       |  $initCode
       |  $yieldInitCode
       |  while ($continueVar) {
       |    if (Thread.interrupted())
       |      throw org.cgsuite.exception.CalculationCanceledException("Calculation canceled by user."/*, token = Some(token)*/)
       |    $checkIfDoneCode
       |    if ($continueVar) {
       |      $iterateCode
       |      $whileCode
       |      if ($continueVar) {
       |        if ($whereCode) {
       |          val $tempResultVar = {
       |            $bodyCode
       |          }
       |          $yieldUpdateCode
       |        }
       |        $incrementCode
       |      }
       |    }
       |  }
       |  $yieldReturnCode
       |}
       |""".stripMargin
     */

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    children foreach { node => if (!(forId contains node)) node.collectMentionedClasses(classes) }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loopTypeStr = loopType match {
      case LoopNode.Do => "do"
      case LoopNode.YieldList => "yield"
      case LoopNode.YieldMap => "mapof"
      case LoopNode.YieldSet => "setof"
      case LoopNode.YieldTable => "tableof"
      case LoopNode.YieldSum => "sumof"
    }
    val antecedent = Seq(
      forId map { "for " + _.id.name },
      in map { "in " + _.toNodeString },
      from map { "from " + _.toNodeString },
      to map { "to " + _.toNodeString },
      by map { "by " + _.toNodeString },
      `while` map { "while " + _.toNodeString },
      where map { "where " + _.toNodeString },
      Some(loopTypeStr)
    ).flatten.mkString(" ")
    antecedent + " " + body.toNodeString + " end"
  }

}

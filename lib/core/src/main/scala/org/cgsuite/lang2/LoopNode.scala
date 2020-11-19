package org.cgsuite.lang2

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang2.LoopNode._
import org.cgsuite.lang2.Node.treeToRichTree

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

    val mostGeneralCollection = CgscriptType(CgscriptClass.Collection, Vector(CgscriptType(CgscriptClass.Object)))

    inType foreach { typ =>
      if (!(typ matches mostGeneralCollection))
        throw EvalException(s"Object of type `${typ.qualifiedName}` cannot be expanded as a Collection", in.get.tree)
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
              throw EvalException(s"Table row of type `${bodyType.qualifiedName}` cannot be converted to class `cgsuite.lang.List`")
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
                  throw EvalException(s"Operation `+` is not closed over arguments of type `${closureType.qualifiedName}`")
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

  override def toScalaCode(context: CompileContext): String = toScalaCode(context, None)

  def toScalaCode(context: CompileContext, pushdownYieldVar: Option[String]): String = {

    val continueVar = context.newTempId()
    val tempResultVar = context.newTempId()
    val byVar = context.newTempId()
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
    val initCode = {
      if (from.isDefined) {
        val fromCode = from.get.toScalaCode(context)
        val byCode = by map { _.toScalaCode(context) } getOrElse "org.cgsuite.core.Values.one"
        s"""var $loopVar: org.cgsuite.core.RationalNumber = $fromCode
           |var $byVar = $byCode
           """
      } else if (in.isDefined) {
        val inCode = in.get.toScalaCode(context)
        s"val $iteratorVar = $inCode.iterator"
      } else
        ""
    }
    val yieldInitCode = {
      if (isYield && pushdownYieldVar.isEmpty) {
        loopType match {
          case YieldSum =>
            s"var $yieldResultVar: ${elaboratedType.scalaTypeName} = null"
          case YieldTable =>
            s"val $yieldResultVar = new scala.collection.mutable.ArrayBuffer[IndexedSeq[_]]"
          case _ =>
            val yieldType = elaboratedType.typeArguments.head
            s"val $yieldResultVar = new scala.collection.mutable.ArrayBuffer[${yieldType.scalaTypeName}]"
        }
      } else {
        ""
      }
    }
    val checkIfDoneCode = {
      if (to.isDefined) {
        val toCode = to.get.toScalaCode(context)
        s"$continueVar = if ($byVar < org.cgsuite.core.Values.zero) $loopVar >= $toCode else $loopVar <= $toCode"
      } else if (in.isDefined)
        s"$continueVar = $iteratorVar.hasNext"
      else
        ""
    }
    val iterateCode = {
      if (in.isDefined)
        s"val $loopVar = $iteratorVar.next()"
      else
        ""
    }
    val byCode = by map { _.toScalaCode(context) } getOrElse "org.cgsuite.core.Values.one"
    val incrementCode = if (from.isDefined) s"$loopVar = $loopVar + $byCode" else ""
    val whileCode = `while` map { s"$continueVar = " + _.toScalaCode(context) } getOrElse ""
    val whereCode = where map { _.toScalaCode(context) } getOrElse "true"
    val bodyCode = pushDownYield match {
      case Some(loopBody) => loopBody.toScalaCode(context, Some(yieldResultVar))
      case None => body.toScalaCode(context)
    }
    val yieldUpdateCode = {
      if (isYield && loopType == LoopNode.YieldSum) {
        s"$yieldResultVar = if ($yieldResultVar == null) $tempResultVar else $yieldResultVar + $tempResultVar"
      } else if (isYield && pushDownYield.isEmpty) {
        s"$yieldResultVar += $tempResultVar"
      } else {
        ""
      }
    }
    val yieldReturnCode = loopType match {
      case LoopNode.Do => "null"
      case LoopNode.YieldList => s"$yieldResultVar.toVector"
      case LoopNode.YieldMap => s"$yieldResultVar.asInstanceOf[scala.collection.mutable.ArrayBuffer[(Any, Any)]].toMap"
      case LoopNode.YieldSet => s"$yieldResultVar.toSet"
      case LoopNode.YieldTable => s"org.cgsuite.lang2.Table($yieldResultVar.toIndexedSeq)(org.cgsuite.lang2.CgscriptClass.instanceToOutput)"
      case LoopNode.YieldSum => s"if ($yieldResultVar == null) org.cgsuite.core.Values.zero else $yieldResultVar"
    }

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

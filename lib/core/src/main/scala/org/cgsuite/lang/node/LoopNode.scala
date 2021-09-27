package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.core.{Integer, Values}
import org.cgsuite.core.Values.one
import org.cgsuite.exception.CalculationCanceledException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.lang.{ElaborationDomain, EvaluationDomain, Ops, Profiler}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object LoopNode {

  def apply(tree: Tree): LoopNode = {

    val loopType = tree.getType match {
      case DO => Do
      case YIELD => YieldList
      case YIELD_MAP => YieldMap
      case YIELD_SET => YieldSet
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

  override val children = forId.toVector ++ in ++ from ++ to ++ by ++ `while` ++ where :+ body

  private val prepareLoop = Symbol(s"PrepareLoop [${tree.location}]")
  private val loop = Symbol(s"Loop [${tree.location}]")
  private val loopBody = Symbol(s"LoopBody [${tree.location}]")

  private val isYield: Boolean = loopType match {
    case LoopNode.Do => false
    case LoopNode.YieldList | LoopNode.YieldMap | LoopNode.YieldSet => true
  }
  private val pushDownYield: Option[LoopNode] = (isYield, body) match {
    case (true, loopBody: LoopNode) =>
      assert(loopBody.isYield)
      Some(loopBody)
    case _ => None
  }

  override def elaborate(scope: ElaborationDomain): Unit = {
    forId match {
      case Some(idNode) =>
        scope.pushScope()
        scope.insertId(idNode)
      case None =>
    }
    super.elaborate(scope)
    if (forId.isDefined) {
      scope.popScope()
    }
  }

  override def evaluate(domain: EvaluationDomain): Any = {

    val buffer: ArrayBuffer[Any] = loopType match {
      case LoopNode.YieldList | LoopNode.YieldMap | LoopNode.YieldSet => ArrayBuffer[Any]()
      case LoopNode.Do => null
    }

    evaluate(domain, buffer)

    loopType match {
      case LoopNode.Do => null
      case LoopNode.YieldList => buffer.toVector
      case LoopNode.YieldMap => buffer.asInstanceOf[mutable.ArrayBuffer[(Any,Any)]].toMap
      case LoopNode.YieldSet => buffer.toSet
    }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loopTypeStr = loopType match {
      case LoopNode.Do => "do"
      case LoopNode.YieldList => "yield"
      case LoopNode.YieldMap => "yield as Map"
      case LoopNode.YieldSet => "yield as Set"
    }
    val antecedent = Vector(
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

  private def evaluate(domain: EvaluationDomain, yieldResult: ArrayBuffer[Any]): Null = {

    Profiler.start(prepareLoop)

    val forIndex = if (forId.isDefined) forId.get.localVariableReference.index else -1
    var counter = if (from.isDefined) from.get.evaluate(domain) else null
    val toVal = if (to.isDefined) to.get.evaluate(domain) else null
    val byVal = if (by.isDefined) by.get.evaluate(domain) else one
    val checkLeq = byVal match {
      case x: Integer => x >= Values.zero
      case _ => true
    }
    val iterator = if (in.isDefined) in.get.evaluateAsIterator(domain) else null

    Profiler.stop(prepareLoop)

    Profiler.start(loop)

    var continue = true

    while (continue) {

      if (Thread.interrupted())
        throw CalculationCanceledException("Calculation canceled by user.", token = Some(token))

      if (iterator == null) {
        if (forIndex >= 0)
          domain.localScope(forIndex) = counter
        continue = toVal == null || (checkLeq && Ops.leq(counter, toVal)) || (!checkLeq && Ops.leq(toVal, counter))
      } else {
        if (iterator.hasNext)
          domain.localScope(forIndex) = iterator.next()
        else
          continue = false
      }

      if (continue) {
        continue = `while`.isEmpty || `while`.get.evaluateAsBoolean(domain)
      }

      if (continue) {
        val whereCond = where.isEmpty || where.get.evaluateAsBoolean(domain)
        if (whereCond) {
          Profiler.start(loopBody)
          pushDownYield match {
            case Some(pushDown) => pushDown.evaluate(domain, yieldResult)
            case None =>
              val r = body.evaluate(domain)
              if (yieldResult != null) {
                yieldResult += r
              }
          }
          Profiler.stop(loopBody)
        }
        if (counter != null)
          counter = Ops.Plus(tree, counter, byVal)
      }

    }

    Profiler.stop(loop)

    null

  }

}

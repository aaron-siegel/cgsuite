package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.core._
import org.cgsuite.exception.{CgsuiteException, EvalException}
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.lang.{ElaborationDomain, EvaluationDomain}

case class GameSpecNode(tree: Tree, lo: Vector[EvalNode], ro: Vector[EvalNode], forceExplicit: Boolean) extends EvalNode {

  override val children = lo ++ ro

  override def evaluate(domain: EvaluationDomain) = {
    try {
      val leval = lo map { _.evaluate(domain) }
      val reval = ro map { _.evaluate(domain) }
      if (isListOf[Game](leval) && isListOf[Game](reval)) {
        val gl = castAs[Game](leval)
        val gr = castAs[Game](reval)
        if (!forceExplicit && (gl ++ gr).forall { _.isInstanceOf[CanonicalShortGame] }) {
          CanonicalShortGame(gl map { _.asInstanceOf[CanonicalShortGame] }, gr map { _.asInstanceOf[CanonicalShortGame] })
        } else if (!forceExplicit && (gl ++ gr).forall { _.isInstanceOf[CanonicalStopper] }) {
          CanonicalStopper(gl map { _.asInstanceOf[CanonicalStopper] }, gr map { _.asInstanceOf[CanonicalStopper] })
        } else {
          ExplicitGame(gl, gr)
        }
      } else if (isListOf[SidedValue](leval) && isListOf[SidedValue](reval)) {
        if (forceExplicit) {
          sys error "can't be force explicit - need better error msg here"
        } else {
          val gl = castAs[SidedValue](leval)
          val gr = castAs[SidedValue](reval)
          val glonside = gl map { _.onsideSimplified }
          val gronside = gr map { _.onsideSimplified }
          val gloffside = gl map { _.offsideSimplified }
          val groffside = gr map { _.offsideSimplified }
          SidedValue(SimplifiedLoopyGame.constructLoopyGame(glonside, gronside), SimplifiedLoopyGame.constructLoopyGame(gloffside, groffside))
        }
      } else {
        throw EvalException("Invalid game specifier: objects must be of type `Game` or `SidedValue`", tree)
      }
    } catch {
      case exc: CgsuiteException =>
        if (exc.tokenStack.isEmpty)
          exc.addToken(tree.token)
        throw exc
    }
  }

  def isListOf[T](objects: Iterable[_])(implicit mf: Manifest[T]): Boolean = {
    objects forall {
      case _: T => true
      case sublist: Iterable[_] => sublist forall {
        case _: T => true
        case _ => false
      }
      case _ => false
    }
  }

  def castAs[T](objects: Iterable[_])(implicit mf: Manifest[T]): Iterable[T] = {
    objects flatMap {
      case g: T => Iterable(g)
      case sublist: Iterable[_] => sublist map { _.asInstanceOf[T] }
      case _ => sys error "cannot be cast (this should never happen due to prior call to isListOf)"
    }
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loStr = lo map { _.toNodeString } mkString ","
    val roStr = ro map { _.toNodeString } mkString ","
    s"{$loStr | $roStr}"
  }

}

object LoopyGameSpecNode {

  def apply(tree: Tree): EvalNode = {
    tree.getType match {
      case NODE_LABEL => make(tree, Some(IdentifierNode(tree.head)), tree.children(1))
      case SLASHES => make(tree, None, tree)
      case _ => EvalNode(tree)
    }
  }

  def make(tree: Tree, nodeLabel: Option[IdentifierNode], body: Tree): LoopyGameSpecNode = {
    assert(body.getType == SLASHES)
    val (loPass, lo) = loopyGameOptions(body.head)
    val (roPass, ro) = loopyGameOptions(body.children(1))
    LoopyGameSpecNode(tree, nodeLabel, lo, ro, loPass, roPass)
  }

  private def loopyGameOptions(tree: Tree): (Boolean, Vector[EvalNode]) = {
    tree.getType match {
      case SLASHES => (false, Vector(LoopyGameSpecNode(tree)))
      case EXPRESSION_LIST =>
        val (pass, opts) = tree.children partition { _.getType == PASS }
        (pass.nonEmpty, opts map { LoopyGameSpecNode(_) })
      case _ => (false, Vector(EvalNode(tree)))
    }
  }
}

case class LoopyGameSpecNode(
  tree: Tree,
  nodeLabel: Option[IdentifierNode],
  lo: Vector[EvalNode],
  ro: Vector[EvalNode],
  loPass: Boolean,
  roPass: Boolean
) extends EvalNode {

  override val children = nodeLabel.toVector ++ lo ++ ro

  override def elaborate(scope: ElaborationDomain): Unit = {
    nodeLabel foreach { idNode =>
      scope.pushScope()
      scope.insertId(idNode)
    }
    super.elaborate(scope)
    nodeLabel foreach { _ => scope.popScope() }
  }

  override def evaluate(domain: EvaluationDomain) = {
    val thisNode = evaluateLoopy(domain).head
    val loopyGame = new LoopyGame(thisNode)
    SidedValue(loopyGame)
  }

  override def evaluateLoopy(domain: EvaluationDomain): Iterable[LoopyGame.Node] = {
    val thisNode = new LoopyGame.Node()
    if (nodeLabel.isDefined)
      domain.localScope(nodeLabel.get.localVariableReference.index) = thisNode
    lo flatMap { _.evaluateLoopy(domain) } foreach { thisNode.addLeftEdge }
    ro flatMap { _.evaluateLoopy(domain) } foreach { thisNode.addRightEdge }
    if (loPass)
      thisNode.addLeftEdge(thisNode)
    if (roPass)
      thisNode.addRightEdge(thisNode)
    if (nodeLabel.isDefined)
      domain.localScope(nodeLabel.get.localVariableReference.index) = null
    Iterable(thisNode)
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loStr = (lo map { _.toNodeString }) ++ (if (loPass) Some("pass") else None) mkString ","
    val roStr = (ro map { _.toNodeString }) ++ (if (roPass) Some("pass") else None) mkString ","
    s"{$loStr | $roStr}"
  }

}

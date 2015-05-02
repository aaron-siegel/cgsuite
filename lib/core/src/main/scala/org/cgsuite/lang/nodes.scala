package org.cgsuite.lang

import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.lang.Ops._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.CgsuiteTree
import scala.collection.JavaConversions._

object Node {

  def apply(tree: CgsuiteTree): Node = {

    tree.getType match {

      // Constants

      case TRUE => ConstantNode(tree, true)
      case FALSE => ConstantNode(tree, false)
      case INTEGER => ConstantNode(tree, Integer.parseInteger(tree.getText))
      case INF => ConstantNode(tree, positiveInfinity)
      case STRING => ConstantNode(tree, tree.getText.drop(1).dropRight(1))
      case NIL => ConstantNode(tree, Nil)

      // Identifier

      case IDENTIFIER => IdentifierNode(tree)

      // Ops

      case UNARY_PLUS => UnOpNode(tree, Pos)
      case UNARY_MINUS => UnOpNode(tree, Neg)
      case PLUSMINUS => UnOpNode(tree, PlusMinus)

      case PLUS => BinOpNode(tree, Plus)
      case MINUS => BinOpNode(tree, Minus)
      case AST => BinOpNode(tree, Times)
      case FSLASH => BinOpNode(tree, Div)
      case PERCENT => BinOpNode(tree, Mod)
      case EXP => BinOpNode(tree, Exp)

      case NOT => UnOpNode(tree, Not)
      case AND => BinOpNode(tree, And)
      case OR => BinOpNode(tree, Or)

      // Relations

      case EQUALS => BinOpNode(tree, Equals)
      case NEQ => BinOpNode(tree, Neq)
      case LEQ => BinOpNode(tree, Leq)
      case GEQ => BinOpNode(tree, Geq)
      case LT => BinOpNode(tree, Lt)
      case GT => BinOpNode(tree, Gt)
      case LCONFUSED => BinOpNode(tree, LConfused)
      case GCONFUSED => BinOpNode(tree, GConfused)
      case REFEQUALS => BinOpNode(tree, RefEquals)
      case REFNEQ => BinOpNode(tree, RefNeq)

      // * and ^

      case UNARY_AST => nimber(tree)
      case CARET | MULTI_CARET | VEE | MULTI_VEE => upMultiple(tree)

      // Collection constructors

      case COORDINATES => BinOpNode(tree, MakeCoordinates)
      case EXPLICIT_LIST => MultiOpNode(tree, MakeList)
      case EXPLICIT_SET => MultiOpNode(tree, MakeSet)
      case EXPLICIT_MAP => MultiOpNode(tree, MakeMap)
      case BIGRARROW => MapPairNode(tree, Node(tree.getChild(0)), Node(tree.getChild(1)))

      // Game construction

      case SLASHES => GameSpecNode(tree, gameOptions(tree.getChild(0)), gameOptions(tree.getChild(1)), forceExplicit = false)
      case SQUOTE => GameSpecNode(tree, gameOptions(tree.getChild(0).getChild(0)), gameOptions(tree.getChild(0).getChild(1)), forceExplicit = true)

      // Control flow

      case IF | ELSEIF => IfNode(
        tree,
        Node(tree.getChild(0)),
        StatementSequenceNode(tree.getChild(1)),
        Option(tree.getChild(2)).map { StatementSequenceNode(_) }
      )
      case ERROR => ErrorNode(tree, Node(tree.getChild(0)))
      case DO | YIELD => LoopNode(tree)

      // Resolvers

      case DOT => DotNode(tree, Node(tree.getChild(0)), IdentifierNode(tree.getChild(1)))
      case FUNCTION_CALL => FunctionCallNode(tree)
      case ARRAY_REFERENCE => BinOpNode(tree, ArrayReference)

      // Assignment

      case ASSIGN => AssignToNode(tree, IdentifierNode(tree.getChild(0)), Node(tree.getChild(1)))

      // Suppressor

      case SEMI => ConstantNode(tree, Nil)

      // Statement sequence

      case STATEMENT_SEQUENCE => StatementSequenceNode(tree)

    }

  }

  private def gameOptions(tree: CgsuiteTree): Seq[Node] = {
    tree.getType match {
      case SLASHES => Seq(Node(tree))
      case EXPRESSION_LIST => tree.getChildren.map { Node(_) }
    }
  }

  private def nimber(tree: CgsuiteTree): Node = {
    if (tree.getChildCount == 0) {
      ConstantNode(tree, star)
    } else {
      UnOpNode(tree, MakeNimber)
    }
  }

  private def upMultiple(tree: CgsuiteTree): Node = {
    val (upMultipleTree, nimberTree) = tree.getChildCount match {
      case 0 => (None, None)
      case 1 => tree.getChild(0).getType match {
        case UNARY_AST => (None, Some(tree.getChild(0)))
        case _ => (Some(tree.getChild(0)), None)
      }
      case _ => (Some(tree.getChild(0)), Some(tree.getChild(1)))
    }
    val upMultipleNode = upMultipleTree map { Node(_) } getOrElse { ConstantNode(tree, Integer(tree.getText.length)) }
    val nimberNode = nimberTree map { t =>
      if (t.getChildCount == 0) ConstantNode(t, one) else Node(t.getChild(0))
    } getOrElse { ConstantNode(null, zero) }
    tree.getType match {
      case CARET | MULTI_CARET => BinOpNode(tree, MakeUpMultiple, upMultipleNode, nimberNode)
      case VEE | MULTI_VEE => BinOpNode(tree, MakeDownMultiple, upMultipleNode, nimberNode)
    }
  }

}

trait Node {

  def tree: CgsuiteTree
  def token = tree.getToken
  def ttype = token.getType

}

case class ConstantNode(tree: CgsuiteTree, constantValue: Any) extends Node

object IdentifierNode {
  def apply(tree: CgsuiteTree): IdentifierNode = IdentifierNode(tree, Symbol(tree.getText))
}

case class IdentifierNode(tree: CgsuiteTree, id: Symbol) extends Node

object UnOpNode {
  def apply(tree: CgsuiteTree, op: UnOp): UnOpNode = UnOpNode(tree, op, Node(tree.getChild(0)))
}

case class UnOpNode(tree: CgsuiteTree, op: UnOp, operand: Node) extends Node

object BinOpNode {
  def apply(tree: CgsuiteTree, op: BinOp): BinOpNode = BinOpNode(tree, op, Node(tree.getChild(0)), Node(tree.getChild(1)))
}

case class BinOpNode(tree: CgsuiteTree, op: BinOp, operand1: Node, operand2: Node) extends Node

object MultiOpNode {
  def apply(tree: CgsuiteTree, op: MultiOp): MultiOpNode = MultiOpNode(tree, op, tree.getChildren.map { Node(_) })
}

case class MultiOpNode(tree: CgsuiteTree, op: MultiOp, operands: Seq[Node]) extends Node

case class MapPairNode(tree: CgsuiteTree, from: Node, to: Node) extends Node

case class GameSpecNode(tree: CgsuiteTree, lo: Seq[Node], ro: Seq[Node], forceExplicit: Boolean) extends Node

case class IfNode(tree: CgsuiteTree, condition: Node, ifNode: StatementSequenceNode, elseNode: Option[StatementSequenceNode]) extends Node

object LoopNode {

  def apply(tree: CgsuiteTree): LoopNode = {

    val isYield = tree.getType == YIELD
    val body = Node(tree.getChildren.last)
    val loopSpecs = tree.getChildren.dropRight(1)
    assert(loopSpecs.forall { _.getType == LOOP_SPEC })

    def makeLoopNode(loopSpecTree: CgsuiteTree, nextNode: Node): LoopNode = {
      LoopNode(
        loopSpecTree,
        isYield,
        loopSpecTree.getChildren.find { _.getType == FOR   }.map { t => IdentifierNode(t.getChild(0)) },
        loopSpecTree.getChildren.find { _.getType == IN    }.map { t => Node(t.getChild(0)) },
        loopSpecTree.getChildren.find { _.getType == FROM  }.map { t => Node(t.getChild(0)) },
        loopSpecTree.getChildren.find { _.getType == TO    }.map { t => Node(t.getChild(0)) },
        loopSpecTree.getChildren.find { _.getType == BY    }.map { t => Node(t.getChild(0)) },
        loopSpecTree.getChildren.find { _.getType == WHILE }.map { t => Node(t.getChild(0)) },
        loopSpecTree.getChildren.find { _.getType == WHERE }.map { t => Node(t.getChild(0)) },
        nextNode
      )
    }

    loopSpecs.foldRight(body)(makeLoopNode).asInstanceOf[LoopNode]

  }
}

case class LoopNode(
  tree   : CgsuiteTree,
  isYield: Boolean,
  forId  : Option[IdentifierNode],
  in     : Option[Node],
  from   : Option[Node],
  to     : Option[Node],
  by     : Option[Node],
  `while`: Option[Node],
  where  : Option[Node],
  body   : Node
) extends Node

case class ErrorNode(tree: CgsuiteTree, msg: Node) extends Node

case class DotNode(tree: CgsuiteTree, obj: Node, idNode: IdentifierNode) extends Node {
  val asQualifiedClassName: Option[String] = obj match {
    case IdentifierNode(_, antecedentId) => Some(antecedentId.name + "." + idNode.id.name)
    case node: DotNode => node.asQualifiedClassName.map { _ + "." + idNode.id.name }
    case _ => None
  }
}

case class FunctionCallNode(tree: CgsuiteTree) extends Node {
  val callSite = Node(tree.getChild(0))
  val args: Seq[Node] = tree.getChild(1).getChildren.filterNot { _.getType == BIGRARROW }.map { Node(_) }
  val optArgs: Map[IdentifierNode, Node] = {
    tree
      .getChild(1)
      .getChildren
      .filter { _.getType == BIGRARROW }
      .map { t => (IdentifierNode(t.getChild(0)), Node(t.getChild(1))) }
      .toMap
  }
}

case class AssignToNode(tree: CgsuiteTree, id: IdentifierNode, expr: Node) extends Node

object StatementSequenceNode {
  def apply(tree: CgsuiteTree): StatementSequenceNode = StatementSequenceNode(tree, tree.getChildren.map { Node(_) })
}

case class StatementSequenceNode(tree: CgsuiteTree, statements: Seq[Node]) extends Node

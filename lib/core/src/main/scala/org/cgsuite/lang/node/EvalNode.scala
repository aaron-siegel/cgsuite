package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.{CalculationCanceledException, CgsuiteException, EvalException}
import org.cgsuite.lang.Ops._
import org.cgsuite.lang._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.output.EmptyOutput

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object EvalNode {

  def apply(tree: Tree): EvalNode = {

    tree.getType match {

      // Constants

      case TRUE => ConstantNode(tree, true)
      case FALSE => ConstantNode(tree, false)
      case INTEGER => ConstantNode(tree, Integer.parseInteger(tree.getText))
      case STRING => ConstantNode(tree, tree.getText.drop(1).dropRight(1).replaceAll("\\\\\"", "\""))

      // This

      case THIS => ThisNode(tree)

      // Identifier

      case IDENTIFIER => IdentifierNode(tree)

      // Ops

      case UNARY_PLUS => node.UnOpNode(tree, Pos)
      case UNARY_MINUS => node.UnOpNode(tree, Neg)
      case PLUSMINUS => node.UnOpNode(tree, PlusMinus)

      case PLUS => node.BinOpNode(tree, Plus)
      case MINUS => node.BinOpNode(tree, Minus)
      case COLON => node.BinOpNode(tree, OrdinalPlus)
      case AST => node.BinOpNode(tree, Times)
      case FSLASH => node.BinOpNode(tree, Div)
      case PERCENT => node.BinOpNode(tree, Mod)
      case EXP => node.BinOpNode(tree, Exp)

      case NOT => node.UnOpNode(tree, Not)
      case AND => node.BinOpNode(tree, And)
      case OR => node.BinOpNode(tree, Or)

      case IS => node.BinOpNode(tree, Is)

      case INFIX_OP => InfixOpNode(tree)

      // Relations

      case EQUALS => node.BinOpNode(tree, Equals)
      case NEQ => node.BinOpNode(tree, Neq)
      case LEQ => node.BinOpNode(tree, Leq)
      case GEQ => node.BinOpNode(tree, Geq)
      case LT => node.BinOpNode(tree, Lt)
      case GT => node.BinOpNode(tree, Gt)
      case CONFUSED => node.BinOpNode(tree, Confused)
      case LCONFUSED => node.BinOpNode(tree, LConfused)
      case GCONFUSED => node.BinOpNode(tree, GConfused)
      case REFEQUALS => node.BinOpNode(tree, RefEquals)
      case REFNEQ => node.BinOpNode(tree, RefNeq)

      // * and ^

      case UNARY_AST => nimber(tree)
      case CARET | MULTI_CARET | VEE | MULTI_VEE => upMultiple(tree)

      // Collection constructors

      case COORDINATES => node.BinOpNode(tree, MakeCoordinates)
      case EXPLICIT_LIST => ListNode(tree)
      case EXPLICIT_SET => SetNode(tree)
      case EXPLICIT_MAP => MapNode(tree)
      case DOTDOT => node.BinOpNode(tree, Range)

      // Game construction

      case SLASHES =>
        if (tree.head.getType == EXPRESSION_LIST && tree.head.children.exists { _.getType == PASS } ||
            tree.children(1).getType == EXPRESSION_LIST && tree.children(1).children.exists { _.getType == PASS })
          LoopyGameSpecNode(tree)
        else
          GameSpecNode(tree, gameOptions(tree.head), gameOptions(tree.children(1)), forceExplicit = false)
      case SQUOTE => GameSpecNode(tree, gameOptions(tree.head.head), gameOptions(tree.head.children(1)), forceExplicit = true)
      case NODE_LABEL => LoopyGameSpecNode(tree)
      case AMPERSAND => node.BinOpNode(tree, MakeSides)
      case PASS => throw EvalException("Unexpected `pass`.", tree)

      // Control flow

      case IF | ELSEIF => IfNode(
        tree,
        EvalNode(tree.head),
        StatementSequenceNode(tree.children(1)),
        if (tree.children.size > 2) Some(EvalNode(tree.children(2))) else None
      )
      case ERROR => ErrorNode(tree, EvalNode(tree.head))
      case DO | YIELD | YIELD_SET | YIELD_MAP => LoopNode(tree)

      // Functions

      case RARROW => FunctionDefNode(tree, None)

      // Map entry

      case BIGRARROW => MapEntryNode(tree)

      // Resolvers

      case DOT => DotNode(tree, EvalNode(tree.head), IdentifierNode(tree.children(1)))
      case FUNCTION_CALL => FunctionCallNode(tree)
      case ARRAY_REFERENCE => node.BinOpNode(tree, ArrayReference, EvalNode(tree.head), EvalNode(tree.children(1).head))

      // Assignment

      case ASSIGN =>
        if (tree.head.getType != IDENTIFIER)
          throw EvalException("Syntax error.", tree)
        AssignToNode(tree, IdentifierNode(tree.head), EvalNode(tree.children(1)), AssignmentDeclType.Ordinary)
      case VAR => VarNode(tree)

      // Function def

      case DEF => DefNode(tree)

      // Statement sequence

      case STATEMENT_SEQUENCE => StatementSequenceNode(tree)

    }

  }

  private def gameOptions(tree: Tree): Vector[EvalNode] = {
    tree.getType match {
      case SLASHES => Vector(EvalNode(tree))
      case EXPRESSION_LIST => tree.children map { EvalNode(_) }
    }
  }

  private def nimber(tree: Tree): EvalNode = {
    if (tree.children.isEmpty) {
      ConstantNode(tree, star)
    } else {
      node.UnOpNode(tree, MakeNimber)
    }
  }

  private def upMultiple(tree: Tree): EvalNode = {
    val (upMultipleTree, nimberTree) = tree.children.size match {
      case 0 => (None, None)
      case 1 => tree.head.getType match {
        case UNARY_AST => (None, Some(tree.head))
        case _ => (Some(tree.head), None)
      }
      case _ => (Some(tree.head), Some(tree.children(1)))
    }
    val upMultipleNode = upMultipleTree map { EvalNode(_) } getOrElse { ConstantNode(tree, Integer(tree.getText.length)) }
    val nimberNode = nimberTree map { t =>
      if (t.children.isEmpty) ConstantNode(t, one) else EvalNode(t.head)
    } getOrElse { ConstantNode(null, zero) }
    tree.getType match {
      case CARET | MULTI_CARET => node.BinOpNode(tree, MakeUpMultiple, upMultipleNode, nimberNode)
      case VEE | MULTI_VEE => node.BinOpNode(tree, MakeDownMultiple, upMultipleNode, nimberNode)
    }
  }

}

trait EvalNode extends Node {

  def children: Vector[EvalNode]
  def evaluate(domain: EvaluationDomain): Any
  final def toNodeString: String = toNodeStringPrec(Int.MaxValue)
  def toNodeStringPrec(enclosingPrecedence: Int): String

  def evaluateAsBoolean(domain: EvaluationDomain): Boolean = {
    evaluate(domain) match {
      case x: Boolean => x
      case _ => sys.error("not a bool")   // TODO Improve these errors
    }
  }

  def evaluateAsIterator(domain: EvaluationDomain): Iterator[_] = {
    val result = evaluate(domain)
    result match {
      case x: Iterable[_] => x.iterator
      case _ => sys.error(s"not a collection: $result")
    }
  }

  def evaluateLoopy(domain: EvaluationDomain): Iterable[LoopyGame.Node] = {
    evaluate(domain) match {
      case g: CanonicalShortGame => Iterable(new LoopyGame.Node(g))
      case g: CanonicalStopper => Iterable(new LoopyGame.Node(g.loopyGame))   // TODO Refactor LoopyGame to consolidate w/ above
      case node: LoopyGame.Node => Iterable(node)
      case sublist: Iterable[_] =>
        sublist map {
          case g: CanonicalShortGame => new LoopyGame.Node(g)
          case g: CanonicalStopper => new LoopyGame.Node(g.loopyGame)   // TODO Refactor LoopyGame to consolidate w/ above
          case node: LoopyGame.Node => node
          case _ => sys.error("must be a list of games")
        }
      case _ => sys.error("must be a list of games")
    }
  }

  def elaborate(scope: ElaborationDomain): Unit = {
    children foreach { _.elaborate(scope) }
  }

}

case class ConstantNode(tree: Tree, constantValue: Any) extends EvalNode {
  override val children = Vector.empty
  override def evaluate(domain: EvaluationDomain) = constantValue
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    CgscriptClass.instanceToOutput(constantValue).toString
  }
}

case class ThisNode(tree: Tree) extends EvalNode {
  override val children = Vector.empty
  override def evaluate(domain: EvaluationDomain) = domain.contextObject.getOrElse { sys.error("invalid `this`") }
  def toNodeStringPrec(enclosingPrecedence: Int) = "this"
}

object UnOpNode {
  def apply(tree: Tree, op: UnOp): UnOpNode = UnOpNode(tree, op, EvalNode(tree.head))
}

case class UnOpNode(tree: Tree, op: UnOp, operand: EvalNode) extends EvalNode {
  override val children = Vector(operand)
  override def evaluate(domain: EvaluationDomain) = {
    try {
      op(tree, operand.evaluate(domain))
    } catch {
      case exc: CgsuiteException =>
        // We only add a token for ops if no subexpression has generated a token.
        if (exc.tokenStack.isEmpty)
          exc addToken token
        throw exc
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val opStr = operand.toNodeStringPrec(op.precedence)
    if (op.precedence <= enclosingPrecedence)
      op.toOpString(opStr)
    else
      s"(${op.toOpString(opStr)})"
  }
}

object BinOpNode {
  def apply(tree: Tree, op: BinOp): BinOpNode = BinOpNode(tree, op, EvalNode(tree.head), EvalNode(tree.children(1)))
}

case class BinOpNode(tree: Tree, op: BinOp, operand1: EvalNode, operand2: EvalNode) extends EvalNode {
  override val children = Vector(operand1, operand2)
  override def evaluate(domain: EvaluationDomain) = {
    try {
      if (op.precedence == OperatorPrecedence.And) {
        operand1.evaluateAsBoolean(domain) && operand2.evaluateAsBoolean(domain)
      } else if (op.precedence == OperatorPrecedence.Or) {
        operand1.evaluateAsBoolean(domain) || operand2.evaluateAsBoolean(domain)
      } else {
        op(tree, operand1.evaluate(domain), operand2.evaluate(domain))
      }
    } catch {
      case exc: CgsuiteException =>
        // We only add a token for ops if no subexpression has generated a token.
        if (exc.tokenStack.isEmpty)
          exc addToken token
        throw exc
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val op1str = operand1.toNodeStringPrec(op.precedence)
    val op2str = operand2.toNodeStringPrec(op.precedence)
    if (op.precedence <= enclosingPrecedence) {
      op.toOpString(op1str, op2str)
    } else {
      s"(${op.toOpString(op1str, op2str)})"
    }
  }
}

object ListNode {
  def apply(tree: Tree): ListNode = {
    assert(tree.getType == EXPLICIT_LIST)
    ListNode(tree, tree.children.map { EvalNode(_) }.toVector)
  }
}

case class ListNode(tree: Tree, elements: Vector[EvalNode]) extends EvalNode {
  override val children = elements
  override def evaluate(domain: EvaluationDomain): Vector[_] = {
    elements.size match {
      // This is to avoid closures and get optimal performance on small collections.
      case 0 => Vector.empty
      case 1 => Vector(elements(0).evaluate(domain))
      case 2 => Vector(elements(0).evaluate(domain), elements(1).evaluate(domain))
      case 3 => Vector(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain))
      case 4 => Vector(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain), elements(3).evaluate(domain))
      case _ => elements map { _.evaluate(domain) }
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "[" + (elements map {  _.toNodeString } mkString ", ") + "]"
  }
}

object SetNode {
  def apply(tree: Tree): SetNode = {
    assert(tree.getType == EXPLICIT_SET)
    SetNode(tree, tree.children map { EvalNode(_) })
  }
}

case class SetNode(tree: Tree, elements: Vector[EvalNode]) extends EvalNode {
  override val children = elements
  override def evaluate(domain: EvaluationDomain): Set[_] = {
    elements.size match {
      // This is to avoid closures and get optimal performance on small collections.
      case 0 => Set.empty
      case 1 => Set(elements(0).evaluate(domain))
      case 2 => Set(elements(0).evaluate(domain), elements(1).evaluate(domain))
      case 3 => Set(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain))
      case 4 => Set(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain), elements(3).evaluate(domain))
      case _ => elements.map { _.evaluate(domain) }.toSet
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "{" + (elements map { _.toNodeString } mkString ", ") + "}"
  }
}

object MapNode {
  def apply(tree: Tree): MapNode = {
    assert(tree.getType == EXPLICIT_MAP)
    val mapEntryNodes = tree.children map { MapEntryNode(_) }
    MapNode(tree, mapEntryNodes)
  }
}

case class MapNode(tree: Tree, elements: Vector[MapEntryNode]) extends EvalNode {
  override val children = elements
  override def evaluate(domain: EvaluationDomain): Map[_, _] = {
    elements.size match {
      // This is to avoid closures and get optimal performance on small collections.
      case 0 => Map.empty
      case 1 => Map(elements(0).evaluate(domain))
      case 2 => Map(elements(0).evaluate(domain), elements(1).evaluate(domain))
      case 3 => Map(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain))
      case 4 => Map(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain), elements(3).evaluate(domain))
      case _ => elements.map { _.evaluate(domain) }.toMap
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    if (elements.isEmpty)
      "{=>}"
    else
      "{" + (elements map { _.toNodeString } mkString ", ") + "}"
  }
}

object MapEntryNode {
  def apply(tree: Tree): MapEntryNode = {
    MapEntryNode(tree, EvalNode(tree.head), EvalNode(tree.children(1)))
  }
}

case class MapEntryNode(tree: Tree, from: EvalNode, to: EvalNode) extends EvalNode {
  override val children = Vector(from, to)
  override def evaluate(domain: EvaluationDomain): (Any, Any) = from.evaluate(domain) -> to.evaluate(domain)
  def toNodeStringPrec(enclosingPrecedence: Int) = s"${from.toNodeString} => ${to.toNodeString}"
}

case class IfNode(tree: Tree, condition: EvalNode, ifNode: StatementSequenceNode, elseNode: Option[EvalNode]) extends EvalNode {
  override val children = Vector(condition, ifNode) ++ elseNode
  override def evaluate(domain: EvaluationDomain) = {
    if (condition.evaluateAsBoolean(domain))
      ifNode.evaluate(domain)
    else
      elseNode.map { _.evaluate(domain) }.orNull
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = s"if ${condition.toNodeString} then ${ifNode.toNodeString}" +
    (elseNode map { " else " + _.toNodeString } getOrElse "") + " end"
}

object FunctionDefNode {
  def apply(tree: Tree, pkg: Option[CgscriptPackage]): FunctionDefNode = {
    val parameters = ParametersNode(tree.head, pkg).toParameters
    FunctionDefNode(tree, parameters, EvalNode(tree.children(1)))
  }
}

case class FunctionDefNode(tree: Tree, parameters: Vector[Parameter], body: EvalNode) extends EvalNode {
  override val children = (parameters flatMap { _.defaultValue }) :+ body
  override def elaborate(scope: ElaborationDomain): Unit = {
    val newScope = new ElaborationDomain(scope.pkg, scope.classVars, Some(scope))
    parameters foreach { param =>
      param.methodScopeIndex = newScope.insertId(param.idNode)
      param.defaultValue foreach { _.elaborate(newScope) }
    }
    body.elaborate(newScope)
    localVariableCount = newScope.localVariableCount
  }
  private[lang] val knownValidArgs: mutable.LongMap[Unit] = mutable.LongMap()
  override def evaluate(domain: EvaluationDomain) = Function(this, domain)
  val ordinal = CallSite.newCallSiteOrdinal
  var localVariableCount: Int = 0
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val paramStr = {
      if (parameters.length == 1)
        parameters.head.id.name
      else
        "(" + (parameters map { _.id.name } mkString ", ") + ")"
    }
    if (OperatorPrecedence.FunctionDef <= enclosingPrecedence)
      s"$paramStr -> ${body.toNodeStringPrec(OperatorPrecedence.FunctionDef)}"
    else
      s"($paramStr -> ${body.toNodeStringPrec(OperatorPrecedence.FunctionDef)})"
  }
}

case class ErrorNode(tree: Tree, msg: EvalNode) extends EvalNode {
  override val children = Vector(msg)
  override def evaluate(domain: EvaluationDomain) = {
    throw EvalException(msg.evaluate(domain).toString)
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = s"error(${msg.toNodeString})"
}

object InfixOpNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSiteNode = DotNode(tree, EvalNode(tree.children.head), IdentifierNode(tree))
    FunctionCallNode(tree, callSiteNode, Vector(EvalNode(tree.children(1))), Vector(None))
  }
}

object DefNode {

  def apply(tree: Tree): AssignToNode = {

    AssignToNode(
      tree,
      IdentifierNode(tree.head),
      FunctionDefNode(tree, ParametersNode(tree.children(1), None).toParameters, EvalNode(tree.children(2))),
      AssignmentDeclType.Ordinary
    )

  }

}

object StatementSequenceNode {
  def apply(tree: Tree): StatementSequenceNode = {
    // Filter out the semicolons (we only care about the last one)
    val filteredChildren = tree.children filterNot { _.getType == SEMI }
    val suppressOutput = tree.children.isEmpty || tree.children.last.getType == SEMI
    StatementSequenceNode(tree, filteredChildren map { EvalNode(_) }, suppressOutput)
  }
}

case class StatementSequenceNode(tree: Tree, statements: Vector[EvalNode], suppressOutput: Boolean) extends EvalNode {
  assert(tree.getType == STATEMENT_SEQUENCE, tree.getType)
  override val children = statements
  override def elaborate(scope: ElaborationDomain): Unit = {
    scope.pushScope()
    statements.foreach { _.elaborate(scope) }
    scope.popScope()
  }
  override def evaluate(domain: EvaluationDomain) = {
    var result: Any = null
    val iterator = statements.iterator
    while (iterator.hasNext) {
      result = iterator.next().evaluate(domain)
    }
    result
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val seqStr = statements map { _.toNodeStringPrec(OperatorPrecedence.StatementSeq) } mkString "; "
    if (OperatorPrecedence.StatementSeq <= enclosingPrecedence)
      seqStr
    else
      s"begin $seqStr end"
  }
}

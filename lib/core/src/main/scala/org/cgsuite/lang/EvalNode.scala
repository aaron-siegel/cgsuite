package org.cgsuite.lang

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.{CalculationCanceledException, CgsuiteException, EvalException}
import org.cgsuite.lang.Node.treeToRichTree
import org.cgsuite.lang.Ops._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.output.EmptyOutput

import scala.collection.generic.Growable
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

      case UNARY_PLUS => UnOpNode(tree, Pos)
      case UNARY_MINUS => UnOpNode(tree, Neg)
      case PLUSMINUS => UnOpNode(tree, PlusMinus)

      case PLUS => BinOpNode(tree, Plus)
      case MINUS => BinOpNode(tree, Minus)
      case COLON => BinOpNode(tree, OrdinalPlus)
      case AST => BinOpNode(tree, Times)
      case FSLASH => BinOpNode(tree, Div)
      case PERCENT => BinOpNode(tree, Mod)
      case EXP => BinOpNode(tree, Exp)

      case NOT => UnOpNode(tree, Not)
      case AND => BinOpNode(tree, And)
      case OR => BinOpNode(tree, Or)

      case IS => BinOpNode(tree, Is)

      case INFIX_OP => InfixOpNode(tree)

      // Relations

      case EQUALS => BinOpNode(tree, Equals)
      case NEQ => BinOpNode(tree, Neq)
      case LEQ => BinOpNode(tree, Leq)
      case GEQ => BinOpNode(tree, Geq)
      case LT => BinOpNode(tree, Lt)
      case GT => BinOpNode(tree, Gt)
      case CONFUSED => BinOpNode(tree, Confused)
      case LCONFUSED => BinOpNode(tree, LConfused)
      case GCONFUSED => BinOpNode(tree, GConfused)
      case REFEQUALS => BinOpNode(tree, RefEquals)
      case REFNEQ => BinOpNode(tree, RefNeq)

      // * and ^

      case UNARY_AST => nimber(tree)
      case CARET | MULTI_CARET | VEE | MULTI_VEE => upMultiple(tree)

      // Collection constructors

      case COORDINATES => BinOpNode(tree, MakeCoordinates)
      case EXPLICIT_LIST => ListNode(tree)
      case EXPLICIT_SET => SetNode(tree)
      case EXPLICIT_MAP => MapNode(tree)
      case DOTDOT => BinOpNode(tree, Range)

      // Game construction

      case SLASHES =>
        if (tree.head.getType == EXPRESSION_LIST && tree.head.children.exists { _.getType == PASS } ||
            tree.children(1).getType == EXPRESSION_LIST && tree.children(1).children.exists { _.getType == PASS })
          LoopyGameSpecNode(tree)
        else
          GameSpecNode(tree, gameOptions(tree.head), gameOptions(tree.children(1)), forceExplicit = false)
      case SQUOTE => GameSpecNode(tree, gameOptions(tree.head.head), gameOptions(tree.head.children(1)), forceExplicit = true)
      case NODE_LABEL => LoopyGameSpecNode(tree)
      case AMPERSAND => BinOpNode(tree, MakeSides)
      case PASS => throw EvalException("Unexpected `pass`.", tree)

      // Control flow

      case IF | ELSEIF => IfNode(
        tree,
        EvalNode(tree.head),
        StatementSequenceNode(tree.children(1)),
        if (tree.children.size > 2) Some(EvalNode(tree.children(2))) else None
      )
      case ERROR => ErrorNode(tree, EvalNode(tree.head))
      case DO | YIELD | LISTOF | MAPOF | SETOF | TABLEOF | SUMOF => LoopNode(tree)

      // Procedures

      case RARROW => ProcedureNode(tree, None)

      // Map entry

      case BIGRARROW => MapPairNode(tree)

      // Resolvers

      case DOT => DotNode(tree, EvalNode(tree.head), IdentifierNode(tree.children(1)))
      case FUNCTION_CALL => FunctionCallNode(tree)
      case ARRAY_REFERENCE => BinOpNode(tree, ArrayReference, EvalNode(tree.head), EvalNode(tree.children(1).head))

      // Assignment

      case ASSIGN =>
        if (tree.head.getType != IDENTIFIER)
          throw EvalException("Syntax error.", tree)
        AssignToNode(tree, IdentifierNode(tree.head), EvalNode(tree.children(1)), AssignmentDeclType.Ordinary)
      case VAR => VarNode(tree)

      // Statement sequence

      case STATEMENT_SEQUENCE => StatementSequenceNode(tree)

    }

  }

  private def gameOptions(tree: Tree): Seq[EvalNode] = {
    tree.getType match {
      case SLASHES => Seq(EvalNode(tree))
      case EXPRESSION_LIST => tree.children map { EvalNode(_) }
    }
  }

  private def nimber(tree: Tree): EvalNode = {
    if (tree.children.isEmpty) {
      ConstantNode(tree, star)
    } else {
      UnOpNode(tree, MakeNimber)
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
      case CARET | MULTI_CARET => BinOpNode(tree, MakeUpMultiple, upMultipleNode, nimberNode)
      case VEE | MULTI_VEE => BinOpNode(tree, MakeDownMultiple, upMultipleNode, nimberNode)
    }
  }

}

trait EvalNode extends Node {

  def children: Iterable[EvalNode]
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
  override val children = Seq.empty
  override def evaluate(domain: EvaluationDomain) = constantValue
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    CgscriptClass.instanceToOutput(constantValue).toString
  }
}

case class ThisNode(tree: Tree) extends EvalNode {
  override val children = Seq.empty
  override def evaluate(domain: EvaluationDomain) = domain.contextObject.getOrElse { sys.error("invalid `this`") }
  def toNodeStringPrec(enclosingPrecedence: Int) = "this"
}

object IdentifierNode {
  def apply(tree: Tree): IdentifierNode = {
    assert(tree.getType == IDENTIFIER || tree.getType == DECL_ID || tree.getType == INFIX_OP, tree.toStringTree)
    IdentifierNode(tree, Symbol(tree.getText))
  }
}

case class IdentifierNode(tree: Tree, id: Symbol) extends EvalNode {

  var resolver: Resolver = Resolver.forId(id)
  var constantResolution: Resolution = _
  var classResolution: Option[Any] = None

  // We cache these separately - that provides for faster resolution than
  // using a matcher.
  var localVariableReference: LocalVariableReference = _
  var classVariableReference: ClassVariableReference = _

  override val children = Seq.empty

  override def elaborate(scope: ElaborationDomain) {
    // Can this be resolved as a Class name? Check first in local package scope, then in default package scope
    scope.pkg flatMap { _ lookupClass id } orElse (CgscriptPackage lookupClass id) match {
      case Some(cls) => classResolution = {
        Some(if (cls.isScript) cls.scriptObject else if (cls.isSingleton) cls.singletonInstance else cls.classObject)
      }
      case None =>
    }
    // Can this be resolved as a scoped variable?
    scope lookup id match {
      case Some(l@LocalVariableReference(_, _)) => localVariableReference = l
      case Some(c@ClassVariableReference(_, _)) => classVariableReference = c
      case None =>
    }
    // Can this be resolved as a constant? Check first in local package scope, then in default package scope
    scope.pkg flatMap { _ lookupConstant id } orElse (CgscriptPackage lookupConstant id) match {
      case Some(res) => constantResolution = res
      case None =>
    }
    // If there's no possible resolution and we're inside a package (i.e., not at Worksheet scope),
    // we can throw an exception now
    if (classResolution.isEmpty && localVariableReference == null && classVariableReference == null &&
      constantResolution == null && scope.pkg.isDefined) {
      throw EvalException(s"That variable is not defined: `${id.name}`", token = Some(token))
    }
  }

  override def evaluate(domain: EvaluationDomain) = {
    // Try resolving in the following precedence order:
    // (1) As a class name;
    // (2) As a local (method-scope) variable;
    // (3) [Inside a package] As a member variable of the context object or
    //     [Worksheet scope] As a global (Worksheet) variable;
    // (4) As a package constant
    if (classResolution.isDefined) {
      // Class name
      classResolution.get
    } else if (localVariableReference != null) {
      // Local var
      domain backref localVariableReference.domainHops localScope localVariableReference.index
    } else {
      lookupLocally(domain) match {
        case Some(x) => x     // Member/Worksheet var
        case None =>
          if (constantResolution != null) {
            // Constant
            constantResolution evaluateFor (constantResolution.cls.singletonInstance, token)
          } else {
            throw EvalException(s"That variable is not defined: `${id.name}`", token = Some(token))
          }
      }
    }
  }

  private[this] def lookupLocally(domain: EvaluationDomain): Option[Any] = {
    if (domain.isOuterDomain) {
      domain getDynamicVar id
    } else if (classVariableReference != null) {
      val backrefObject = domain nestingBackrefContextObject classVariableReference.nestingHops
      Some(classVariableReference.resolver resolve (backrefObject, token))
    } else {
      None
    }
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = id.name

}

object UnOpNode {
  def apply(tree: Tree, op: UnOp): UnOpNode = UnOpNode(tree, op, EvalNode(tree.head))
}

case class UnOpNode(tree: Tree, op: UnOp, operand: EvalNode) extends EvalNode {
  override val children = Seq(operand)
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
  override val children = Seq(operand1, operand2)
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
    ListNode(tree, tree.children.map { EvalNode(_) }.toList)
  }
}

case class ListNode(tree: Tree, elements: List[EvalNode]) extends EvalNode {
  override val children = elements
  override def evaluate(domain: EvaluationDomain): List[_] = {
    elements.size match {
      // This is to avoid closures and get optimal performance on small collections.
      case 0 => Nil
      case 1 => List(elements(0).evaluate(domain))
      case 2 => List(elements(0).evaluate(domain), elements(1).evaluate(domain))
      case 3 => List(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain))
      case 4 => List(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain), elements(3).evaluate(domain))
      case _ => elements.map { _.evaluate(domain) }
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "[" + (elements map {  _.toNodeString } mkString ", ") + "]"
  }
}

object SetNode {
  def apply(tree: Tree): SetNode = {
    assert(tree.getType == EXPLICIT_SET)
    SetNode(tree, tree.children.map { EvalNode(_) }.toIndexedSeq)
  }
}

case class SetNode(tree: Tree, elements: IndexedSeq[EvalNode]) extends EvalNode {
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
    val mapPairNodes = tree.children map { MapPairNode(_) }
    MapNode(tree, mapPairNodes.toIndexedSeq)
  }
}

case class MapNode(tree: Tree, elements: IndexedSeq[MapPairNode]) extends EvalNode {
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

object MapPairNode {
  def apply(tree: Tree): MapPairNode = {
    MapPairNode(tree, EvalNode(tree.head), EvalNode(tree.children(1)))
  }
}

case class MapPairNode(tree: Tree, from: EvalNode, to: EvalNode) extends EvalNode {
  override val children = Seq(from, to)
  override def evaluate(domain: EvaluationDomain): (Any, Any) = from.evaluate(domain) -> to.evaluate(domain)
  def toNodeStringPrec(enclosingPrecedence: Int) = s"${from.toNodeString} => ${to.toNodeString}"
}

case class GameSpecNode(tree: Tree, lo: Seq[EvalNode], ro: Seq[EvalNode], forceExplicit: Boolean) extends EvalNode {

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
          val glonside = gl map { _.onside }
          val gronside = gr map { _.onside }
          val gloffside = gl map { _.offside }
          val groffside = gr map { _.offside }
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

  private def loopyGameOptions(tree: Tree): (Boolean, Seq[EvalNode]) = {
    tree.getType match {
      case SLASHES => (false, Seq(LoopyGameSpecNode(tree)))
      case EXPRESSION_LIST =>
        val (pass, opts) = tree.children partition { _.getType == PASS }
        (pass.nonEmpty, opts map { LoopyGameSpecNode(_) })
      case _ => (false, Seq(EvalNode(tree)))
    }
  }
}

case class LoopyGameSpecNode(
  tree: Tree,
  nodeLabel: Option[IdentifierNode],
  lo: Seq[EvalNode],
  ro: Seq[EvalNode],
  loPass: Boolean,
  roPass: Boolean
  ) extends EvalNode {

  override val children = nodeLabel ++ lo ++ ro

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

case class IfNode(tree: Tree, condition: EvalNode, ifNode: StatementSequenceNode, elseNode: Option[EvalNode]) extends EvalNode {
  override val children = Seq(condition, ifNode) ++ elseNode
  override def evaluate(domain: EvaluationDomain) = {
    if (condition.evaluateAsBoolean(domain))
      ifNode.evaluate(domain)
    else
      elseNode.map { _.evaluate(domain) }.orNull
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = s"if ${condition.toNodeString} then ${ifNode.toNodeString}" +
    (elseNode map { " else " + _.toNodeString } getOrElse "") + " end"
}

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

  private val prepareLoop = Symbol(s"PrepareLoop [${tree.location}]")
  private val loop = Symbol(s"Loop [${tree.location}]")
  private val loopBody = Symbol(s"LoopBody [${tree.location}]")

  private val isYield: Boolean = loopType match {
    case LoopNode.Do | LoopNode.YieldSum => false
    case LoopNode.YieldList | LoopNode.YieldMap | LoopNode.YieldSet | LoopNode.YieldTable => true
  }
  private val pushDownYield: Option[LoopNode] = (isYield, body) match {
    case (true, loopBody: LoopNode) =>
      assert(loopBody.isYield)
      Some(loopBody)
    case _ => None
  }

  override def elaborate(scope: ElaborationDomain) {
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

    val yieldResult: Iterable[Any] with Growable[Any] = loopType match {
      case LoopNode.YieldList | LoopNode.YieldTable => ArrayBuffer[Any]()
      case LoopNode.YieldMap => mutable.HashMap[Any,Any]().asInstanceOf[Iterable[Any] with Growable[Any]]
      case LoopNode.YieldSet => mutable.HashSet[Any]()
      case LoopNode.YieldSum | LoopNode.Do => null
    }
    val r = evaluate(domain, yieldResult)
    loopType match {
      case LoopNode.Do => null
      case LoopNode.YieldList => if (yieldResult.isEmpty) Nil else yieldResult.toVector
      case LoopNode.YieldMap => yieldResult.asInstanceOf[mutable.HashMap[Any,Any]].toMap
      case LoopNode.YieldSet => yieldResult.toSet
      case LoopNode.YieldTable => Table { yieldResult.toSeq map {
        case list: Seq[_] => list
        case _ => throw EvalException("A `tableof` expression must generate exclusively objects of type `cgsuite.lang.List`.")
      } } (OutputBuilder.toOutput)
      case LoopNode.YieldSum => r
    }

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

  private def evaluate(domain: EvaluationDomain, yieldResult: Growable[Any]): Any = {

    Profiler.start(prepareLoop)

    val forIndex = if (forId.isDefined) forId.get.localVariableReference.index else -1
    var counter = if (from.isDefined) from.get.evaluate(domain) else null
    val toVal = if (to.isDefined) to.get.evaluate(domain) else null
    val byVal = if (by.isDefined) by.get.evaluate(domain) else one
    val checkLeq = byVal match {
      case x: Integer => x >= zero
      case _ => true
    }
    val iterator = if (in.isDefined) in.get.evaluateAsIterator(domain) else null
    var sum: Any = null

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
              loopType match {
                case LoopNode.YieldList | LoopNode.YieldSet =>
                  r match {
                    case it: Iterable[_] => yieldResult ++= it
                    case x => yieldResult += x
                  }
                case LoopNode.YieldMap | LoopNode.YieldTable => yieldResult += r
                case LoopNode.YieldSum => sum = if (sum == null) r else Ops.Plus(tree, sum, r)
                case LoopNode.Do => // Nothing
              }
          }
          Profiler.stop(loopBody)
        }
        if (counter != null)
          counter = Ops.Plus(tree, counter, byVal)
      }

    }

    Profiler.stop(loop)

    if (loopType == LoopNode.YieldSum && sum == null)
      zero
    else
      sum

  }

}

object ProcedureNode {
  def apply(tree: Tree, pkg: Option[CgscriptPackage]): ProcedureNode = {
    val parameters = ParametersNode(tree.head, pkg).toParameters
    ProcedureNode(tree, parameters, EvalNode(tree.children(1)))
  }
}

case class ProcedureNode(tree: Tree, parameters: Seq[Parameter], body: EvalNode) extends EvalNode {
  override val children = (parameters flatMap { _.defaultValue }) :+ body
  override def elaborate(scope: ElaborationDomain): Unit = {
    val newScope = ElaborationDomain(scope.pkg, scope.classVars, Some(scope))
    parameters foreach { param =>
      param.methodScopeIndex = newScope.insertId(param.idNode)
      param.defaultValue foreach { _.elaborate(newScope) }
    }
    body.elaborate(newScope)
    localVariableCount = newScope.localVariableCount
  }
  private[lang] val knownValidArgs: mutable.LongMap[Unit] = mutable.LongMap()
  override def evaluate(domain: EvaluationDomain) = Procedure(this, domain)
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
  override val children = Seq(msg)
  override def evaluate(domain: EvaluationDomain) = {
    throw EvalException(msg.evaluate(domain).toString)
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = s"error(${msg.toNodeString})"
}

case class DotNode(tree: Tree, obj: EvalNode, idNode: IdentifierNode) extends EvalNode {
  override val children = Seq(obj, idNode)
  val antecedentAsPackagePath: Option[Seq[String]] = obj match {
    case IdentifierNode(_, antecedentId) => Some(Seq(antecedentId.name))
    case node: DotNode => node.antecedentAsPackagePath.map { _ :+ node.idNode.id.name }
    case _ => None
  }
  val antecedentAsPackage: Option[CgscriptPackage] = antecedentAsPackagePath flatMap { CgscriptPackage.root.lookupSubpackage }
  var isElaborated = false
  var classResolution: CgscriptClass = _
  var constantResolution: Resolution = _
  override def elaborate(scope: ElaborationDomain) {
    antecedentAsPackage flatMap { _.lookupClass(idNode.id) } match {
      case Some(cls) => classResolution = cls
      case None =>
        antecedentAsPackage flatMap { _.lookupConstant(idNode.id) } match {
          case Some(res) => constantResolution = res
          case None => obj.elaborate(scope)     // Deliberately bypass idNode
        }
    }
    isElaborated = true
  }
  override def evaluate(domain: EvaluationDomain): Any = {
    if (!isElaborated) {
      sys error s"Node has not been elaborated: $this"
    }
    if (classResolution != null) {
      if (classResolution.isSingleton) classResolution.singletonInstance else classResolution.classObject
    } else if (constantResolution != null) {
      constantResolution evaluateFor (constantResolution.cls.singletonInstance, token)
    } else {
      val x = obj.evaluate(domain)
      try {
        val resolution = idNode.resolver.findResolution(x)
        if (resolution.isResolvable) {
          resolution.evaluateFor(x, token)
        } else {
          throw EvalException(
            s"Not a method or member variable: `${idNode.id.name}` (in object of class `${(CgscriptClass of x).qualifiedName}`)",
            token = Some(token)
          )
        }
      } catch {
        case exc: CgsuiteException if exc.tokenStack.isEmpty =>
          exc.tokenStack += token
          throw exc
      }
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${obj.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString}"
    else
      s"(${obj.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString})"
  }
}

object InfixOpNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSiteNode = DotNode(tree, EvalNode(tree.children.head), IdentifierNode(tree))
    FunctionCallNode(tree, callSiteNode, IndexedSeq(EvalNode(tree.children(1))), IndexedSeq(None))
  }
}

object FunctionCallNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSite = EvalNode(tree.head)
    val argsWithNames = tree.children(1).children.map { t =>
      t.getType match {
        case BIGRARROW => (EvalNode(t.children(1)), Some(IdentifierNode(t.head)))
        case _ => (EvalNode(t), None)
      }
    }
    val (args, argNames) = argsWithNames.unzip
    FunctionCallNode(tree, callSite, args.toIndexedSeq, argNames.toIndexedSeq)
  }
}

case class FunctionCallNode(
  tree: Tree,
  callSiteNode: EvalNode,
  argNodes: IndexedSeq[EvalNode],
  argNames: IndexedSeq[Option[IdentifierNode]]
  ) extends EvalNode {

  var resolutions: mutable.LongMap[FunctionCallResolution] = mutable.LongMap()

  // Some profiler keys
  val prepareCallSite = Symbol(s"PrepareCallSite [${tree.location}]")
  val prepareCallArgs = Symbol(s"PrepareCallArgs [${tree.location}]")
  val functionCall = Symbol(s"FunctionCall [${tree.location}]")

  override val children = (callSiteNode +: argNodes) ++ argNames.flatten

  override def elaborate(scope: ElaborationDomain): Unit = {
    callSiteNode elaborate scope
    argNodes foreach { _ elaborate scope }
  }

  override def evaluate(domain: EvaluationDomain) = {

    if (Thread.interrupted())
      throw CalculationCanceledException("Calculation canceled by user.", token = Some(token))

    val obj = callSiteNode.evaluate(domain)
    val callSite: CallSite = obj match {
      case cs: CallSite => cs
      case co: ClassObject => co.forClass.constructor match {
        case Some(ctor) => ctor
        case None => throw EvalException(
          s"The class `${co.forClass.qualifiedName}` has no constructor and cannot be directly instantiated.",
          token = Some(token)
        )
      }
      case scr: Script => ScriptCaller(domain, scr)
      case x =>
        val evalMethod = try {
          CgscriptClass.of(x).classInfo.evalMethod
        } catch {
          case exc: CgsuiteException =>
            exc addToken token
            throw exc
        }
        InstanceMethod(x, evalMethod)
    }

    val res = {
      try {
        resolutions.getOrElseUpdate(callSite.ordinal, makeNewResolution(callSite))
      } catch {
        case exc: CgsuiteException =>
          exc addToken token
          throw exc
      }
    }
    val args = new Array[Any](callSite.parameters.length)
    var i = 0
    while (i < args.length) {
      if (res.expandedLastParameter && i == args.length - 1) {
        args(i) = argNodes.slice(i, argNodes.length) map { _.evaluate(domain) }
      } else {
        if (res.parameterToArgsMapping(i) >= 0)
          args(i) = argNodes(res.parameterToArgsMapping(i)).evaluate(domain)
        else
          args(i) = callSite.parameters(i).defaultValue.get.evaluate(domain)
      }
      i += 1
    }
    try {
      callSite call args
    } catch {
      case exc: CgsuiteException =>
        exc addToken token
        throw exc
      case err: StackOverflowError =>
        throw EvalException("Possible infinite recursion.", token = Some(token))
    }

  }

  case class ScriptCaller(domain: EvaluationDomain, script: Script) extends CallSite {

    override def parameters: Seq[Parameter] = Seq.empty

    override def call(args: Array[Any]): Any = {
      val scriptDomain = new EvaluationDomain(new Array[Any](script.scope.localVariableCount), dynamicVarMap = domain.dynamicVarMap)
      val result = script.node evaluate scriptDomain
      if (script.node.suppressOutput)
        EmptyOutput
      else
        result
    }

    override def ordinal: Int = -1

    override def referenceToken = None

    override def locationMessage: String = ???

  }

  private def makeNewResolution(callSite: CallSite) = {
    FunctionCallResolution(callSite, argNames, token)
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val argStr = argNodes map { _.toNodeString } mkString ", "
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr)"
    else
      s"(${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr))"
  }

}

object FunctionCallResolution {

  def apply(callSite: CallSite, argNames: IndexedSeq[Option[IdentifierNode]], referenceToken: Token): FunctionCallResolution = {

    val params = callSite.parameters

    // Last parameter is expandable only if there are no named args
    val expandedLastParameter = params.nonEmpty && params.last.isExpandable && argNames.forall { _.isEmpty }

    // Check for too many arguments
    if (!expandedLastParameter && argNames.length > params.length) {
      throw EvalException(
        s"Too many arguments (${callSite.locationMessage}): ${argNames.length} (expecting at most ${params.length})",
        token = Some(referenceToken)
      )
    }

    // Check for named args in earlier position than ordinary args
    val lastOrdinaryArgIndex = argNames lastIndexWhere { _.isEmpty }
    argNames take (lastOrdinaryArgIndex+1) foreach {
      case None =>
      case Some(idNode) => throw EvalException(
        s"Named parameter `${idNode.id.name}` (${callSite.locationMessage}) " +
          "appears in earlier position than an ordinary argument",
        token = Some(referenceToken)
      )
    }

    val parameterToArgsMapping = new Array[Int](if (expandedLastParameter) params.length - 1 else params.length)
    java.util.Arrays.fill(parameterToArgsMapping, -1)
    argNames.zipWithIndex foreach {
      case (None, index) =>
        if (index < parameterToArgsMapping.length)
          parameterToArgsMapping(index) = index
      case (Some(idNode), index) =>
        val namedIndex = params indexWhere { _.id == idNode.id }
        if (namedIndex == -1)
          throw EvalException(
            s"Invalid parameter name (${callSite.locationMessage}): `${idNode.id.name}`",
            token = Some(referenceToken)
          )
        if (parameterToArgsMapping(namedIndex) != -1)
          throw EvalException(
            s"Duplicate named parameter (${callSite.locationMessage}): `${idNode.id.name}`",
            token = Some(referenceToken)
          )
        parameterToArgsMapping(namedIndex) = index
    }

    // Check that all required args are present
    params zip parameterToArgsMapping foreach { case (param, index) =>
      if (param.defaultValue.isEmpty && index == -1)
        throw EvalException(
          s"Missing required parameter (${callSite.locationMessage}): `${param.id.name}`",
          token = Some(referenceToken)
        )
    }

    FunctionCallResolution(parameterToArgsMapping, expandedLastParameter)

  }
}

case class FunctionCallResolution(parameterToArgsMapping: Array[Int], expandedLastParameter: Boolean)

object VarNode {
  def apply(tree: Tree): AssignToNode = {
    assert(tree.getType == VAR && tree.children.size == 1)
    val t = tree.children.head
    t.getType match {
      case IDENTIFIER => AssignToNode(t, IdentifierNode(t), ConstantNode(null, null), AssignmentDeclType.VarDecl)
      case ASSIGN => AssignToNode(t, IdentifierNode(t.head), EvalNode(t.children(1)), AssignmentDeclType.VarDecl)
    }
  }
}

case class AssignToNode(tree: Tree, idNode: IdentifierNode, expr: EvalNode, declType: AssignmentDeclType.Value) extends EvalNode {
  // TODO Catch illegal assignment to temporary loop variable (during elaboration)
  // TODO Catch illegal assignment to immutable object member (during elaboration)
  // TODO Catch illegal assignment to constant
  override val children = Seq(idNode, expr)
  override def elaborate(scope: ElaborationDomain) {
    // If we're package-external (Worksheet/REPL scope) and scopeStack has size one (we're not
    // in any nested subscope), then we treat idNode as a dynamic var.
    if (declType == AssignmentDeclType.VarDecl && !scope.isToplevelWorksheet) {
      scope.insertId(idNode)
    }
    super.elaborate(scope)
  }
  override def evaluate(domain: EvaluationDomain) = {
    val newValue = expr.evaluate(domain)
    if (idNode.classResolution.isDefined) {
      throw EvalException(s"Cannot assign to class name as variable: `${idNode.id.name}`", token = Some(token))
    } else if (idNode.localVariableReference != null) {
      val refDomain = domain backref idNode.localVariableReference.domainHops
      refDomain.localScope(idNode.localVariableReference.index) = newValue
    } else if (domain.isOuterDomain) {
      domain.putDynamicVar(idNode.id, newValue)
    } else {
      // TODO Nested classes
      val res = idNode.resolver.findResolution(domain.contextObject.get)
      if (res.classScopeIndex >= 0) {
        if (declType == AssignmentDeclType.Ordinary && !res.isMutableVar)
          throw EvalException(s"Cannot reassign to immutable var: `${idNode.id.name}`", token = Some(token))
        val stdObj = domain.contextObject.get.asInstanceOf[StandardObject]
        if (!stdObj.cls.isMutable && (CgscriptClass of newValue).isMutable)
          throw EvalException(
            s"Cannot assign mutable object to var `${idNode.id.name}` of immutable class `${stdObj.cls.qualifiedName}`",
            token = Some(token)
          )
        stdObj.vars(res.classScopeIndex) = newValue.asInstanceOf[AnyRef]
      } else
        throw EvalException(s"Unknown variable for assignment: `${idNode.id.name}`", token = Some(token))
    }
    newValue
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val varStr = if (declType != AssignmentDeclType.Ordinary) "var " else ""
    val assignStr = s"$varStr${idNode.toNodeString} := ${expr.toNodeStringPrec(OperatorPrecedence.Assign)}"
    if (OperatorPrecedence.Assign <= enclosingPrecedence)
      assignStr
    else
      s"($assignStr)"
  }
}

object AssignmentDeclType extends Enumeration {
  val Ordinary, VarDecl, ClassVarDecl = Value
}

object StatementSequenceNode {
  def apply(tree: Tree): StatementSequenceNode = {
    // Filter out the semicolons (we only care about the last one)
    val filteredChildren = tree.children filterNot { _.getType == SEMI }
    val suppressOutput = tree.children.isEmpty || tree.children.last.getType == SEMI
    StatementSequenceNode(tree, filteredChildren map { EvalNode(_) }, suppressOutput)
  }
}

case class StatementSequenceNode(tree: Tree, statements: Seq[EvalNode], suppressOutput: Boolean) extends EvalNode {
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

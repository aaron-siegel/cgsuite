package org.cgsuite.lang

import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.Ops._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.CgsuiteTree
import org.cgsuite.util.Profiler
import scala.collection.JavaConversions._
import scala.collection.mutable

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

      case PLUS => NewBinOpNode(tree, NewPlus)
      case MINUS => BinOpNode(tree, Minus)
      case AST => BinOpNode(tree, Times)
      case FSLASH => BinOpNode(tree, Div)
      case PERCENT => BinOpNode(tree, Mod)
      case EXP => BinOpNode(tree, Exp)

      case NOT => UnOpNode(tree, Not)
      case AND => BinOpNode(tree, And)
      case OR => BinOpNode(tree, Or)

      case IS => BinOpNode(tree, Is)

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
        Option(tree.getChild(2)).map { Node(_) }
      )
      case ERROR => ErrorNode(tree, Node(tree.getChild(0)))
      case DO | YIELD => LoopNode(tree)

      // Resolvers

      case DOT => DotNode(tree, Node(tree.getChild(0)), IdentifierNode(tree.getChild(1)))
      case FUNCTION_CALL => FunctionCallNode(tree)
      case ARRAY_REFERENCE => BinOpNode(tree, ArrayReference, Node(tree.getChild(0)), Node(tree.getChild(1).getChild(0)))

      // Assignment

      case ASSIGN => AssignToNode(tree, IdentifierNode(tree.getChild(0)), Node(tree.getChild(1)), isVarDeclaration = false)

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

  def evaluate(domain: Domain): Any = {
    sys.error("Node cannot be evaluated: " + this)
  }

  def evaluateAsBoolean(domain: Domain): Boolean = {
    evaluate(domain) match {
      case x: Boolean => x
      case _ => sys.error("not a bool")
    }
  }

  def evaluateAsIterator(domain: Domain): Iterator[_] = {
    val result = evaluate(domain)
    result match {
      case x: Iterable[_] => x.iterator
      case _ => sys.error(s"not a collection: $result")
    }
  }

  def evaluateAsGame(domain: Domain): Iterable[Game] = {
    evaluate(domain) match {
      case g: Game => Iterable(g)
      case sublist: Iterable[_] =>
        sublist map { x =>
          x match {
            case g: Game => g
            case _ => sys.error("must be a list of games")
          }
        }
      case _ => sys.error("must be a list of games")
    }
  }

}

case class ConstantNode(tree: CgsuiteTree, constantValue: Any) extends Node {
  override def evaluate(domain: Domain) = constantValue
}

object IdentifierNode {
  def apply(tree: CgsuiteTree): IdentifierNode = IdentifierNode(tree, Symbol(tree.getText))
}

case class IdentifierNode(tree: CgsuiteTree, id: Symbol) extends Node {
  override def evaluate(domain: Domain) = domain.lookup(id, tree.token)
}

object UnOpNode {
  def apply(tree: CgsuiteTree, op: UnOp): UnOpNode = UnOpNode(tree, op, Node(tree.getChild(0)))
}

case class UnOpNode(tree: CgsuiteTree, op: UnOp, operand: Node) extends Node {
  override def evaluate(domain: Domain) = op(operand.evaluate(domain))
}

object BinOpNode {
  def apply(tree: CgsuiteTree, op: BinOp): BinOpNode = BinOpNode(tree, op, Node(tree.getChild(0)), Node(tree.getChild(1)))
}

case class BinOpNode(tree: CgsuiteTree, op: BinOp, operand1: Node, operand2: Node) extends Node {
  override def evaluate(domain: Domain) = op(operand1.evaluate(domain), operand2.evaluate(domain))
}

object NewBinOpNode {
  def apply(tree: CgsuiteTree, op: NewBinOp): NewBinOpNode = NewBinOpNode(tree, op, Node(tree.getChild(0)), Node(tree.getChild(1)))
}

case class NewBinOpNode(tree: CgsuiteTree, op: NewBinOp, operand1: Node, operand2: Node) extends Node {
  override def evaluate(domain: Domain) = op(operand1.evaluate(domain), operand2.evaluate(domain))
}

object MultiOpNode {
  def apply(tree: CgsuiteTree, op: MultiOp): MultiOpNode = MultiOpNode(tree, op, tree.getChildren.map { Node(_) })
}

case class MultiOpNode(tree: CgsuiteTree, op: MultiOp, operands: Seq[Node]) extends Node {
  override def evaluate(domain: Domain) = op(operands.map { _.evaluate(domain) })
}

case class MapPairNode(tree: CgsuiteTree, from: Node, to: Node) extends Node {
  override def evaluate(domain: Domain) = from.evaluate(domain) -> to.evaluate(domain)
}

case class GameSpecNode(tree: CgsuiteTree, lo: Seq[Node], ro: Seq[Node], forceExplicit: Boolean) extends Node {
  override def evaluate(domain: Domain) = {
    val gl = lo.flatMap { _.evaluateAsGame(domain) }
    val gr = ro.flatMap { _.evaluateAsGame(domain) }
    if (!forceExplicit && (gl ++ gr).forall { _.isInstanceOf[CanonicalShortGame] }) {
      CanonicalShortGame(gl map { _.asInstanceOf[CanonicalShortGame] }, gr map { _.asInstanceOf[CanonicalShortGame] })
    } else {
      ExplicitGame(gl, gr)
    }
  }
}

case class IfNode(tree: CgsuiteTree, condition: Node, ifNode: StatementSequenceNode, elseNode: Option[Node]) extends Node {
  override def evaluate(domain: Domain) = {
    if (condition.evaluateAsBoolean(domain))
      ifNode.evaluate(domain)
    else
      elseNode.map { _.evaluate(domain) }.getOrElse(Nil)
  }
}

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
) extends Node {

  private val prepareLoop = Symbol(s"PrepareLoop [${tree.location()}]")
  private val loop = Symbol(s"Loop [${tree.location()}]")
  private val loopBody = Symbol(s"LoopBody [${tree.location()}]")

  override def evaluate(domain: Domain) = {

    Profiler.start(prepareLoop)

    val forId = this.forId.map { _.id }
    var counter = from.map { _.evaluate(domain) }.orNull
    val toVal = to.map { _.evaluate(domain) }
    val byVal = by.map { _.evaluate(domain) }.getOrElse(one)
    val iterator = in.map { _.evaluateAsIterator(domain) }

    Profiler.stop(prepareLoop)

    Profiler.start(loop)

    val result = {
      if (isYield) {
        mutable.MutableList[Any]()
      } else {
        null
      }
    }

    var continue = true

    while (continue) {

      iterator match {
        case Some(it) =>
          if (it.hasNext)
            domain.namespace.put(forId.get, it.next(), declare = true)
          else
            continue = false
        case None =>
          if (forId.nonEmpty)
            domain.namespace.put(forId.get, counter, declare = true)
          continue = toVal.isEmpty || Ops.Leq(counter, toVal.get)
      }

      if (continue) {
        continue = `while`.isEmpty || `while`.get.evaluateAsBoolean(domain)
      }

      if (continue) {
        val whereCond = where.isEmpty || where.get.evaluateAsBoolean(domain)
        if (whereCond) {
          Profiler.start(loopBody)
          val r = body.evaluate(domain)
          Profiler.stop(loopBody)
          if (isYield) {
            r match {
              case it: Iterable[_] => result ++= it
              case x => result += x
            }
          }
          if (counter != null)
            counter = Ops.NewPlus(counter, byVal)
        }
      }

    }

    Profiler.stop(loop)

    if (isYield) {
      result.toSeq
    } else {
      Nil
    }

  }

}

case class ErrorNode(tree: CgsuiteTree, msg: Node) extends Node {
  override def evaluate(domain: Domain) = {
    throw InputException(domain.toStringOutput(msg.evaluate(domain)))
  }
}

case class DotNode(tree: CgsuiteTree, obj: Node, idNode: IdentifierNode) extends Node {
  val asQualifiedClassName: Option[Symbol] = obj match {
    case IdentifierNode(_, antecedentId) => Some(Symbol(antecedentId.name + "." + idNode.id.name))
    case node: DotNode => node.asQualifiedClassName.map { next => Symbol(next.name + "." + idNode.id.name) }
    case _ => None
  }
  override def evaluate(domain: Domain) = {
    val x = obj.evaluate(domain)
    domain.resolve(x, idNode.id) getOrElse {
      throw InputException(s"Member not found: ${idNode.id.name} (in object of type ${CgsuiteClass.of(x)})")
    }
  }
}

case class FunctionCallNode(tree: CgsuiteTree) extends Node {
  val prepareCallSite = Symbol(s"PrepareCallSite [${tree.location()}]")
  val prepareCallArgs = Symbol(s"PrepareCallArgs [${tree.location()}]")
  val functionCall = Symbol(s"FunctionCall [${tree.location()}]")
  val callSite = Node(tree.getChild(0))
  val args: IndexedSeq[Node] = tree.getChild(1).getChildren.filterNot { _.getType == BIGRARROW }.map { Node(_) }.toIndexedSeq
  val optArgs: Map[IdentifierNode, Node] = {
    tree
      .getChild(1)
      .getChildren
      .filter { _.getType == BIGRARROW }
      .map { t => (IdentifierNode(t.getChild(0)), Node(t.getChild(1))) }
      .toMap
  }
  override def evaluate(domain: Domain) = {
    Profiler.start(prepareCallSite)
    val callSite = this.callSite.evaluate(domain)
    Profiler.stop(prepareCallSite)
    Profiler.start(prepareCallArgs)
    val args = {
      val argsArray = new Array[Any](this.args.length)
      var i = 0
      while (i < argsArray.length) {
        argsArray(i) = this.args(i).evaluate(domain)
        i += 1
      }
      argsArray.toSeq
    }
    val optArgs = this.optArgs.map { case (IdentifierNode(_, id), value) => (id, value.evaluate(domain)) }
    Profiler.stop(prepareCallArgs)
    Profiler.start(functionCall)
    val result = callSite match {
      case site: CallSite => site.call(args, optArgs)
      case _ => throw InputException("That is not a method or procedure.", token = Some(tree.token))
    }
    Profiler.stop(functionCall)
    result
  }
}

case class AssignToNode(tree: CgsuiteTree, id: IdentifierNode, expr: Node, isVarDeclaration: Boolean) extends Node {
  override def evaluate(domain: Domain) = {
    domain.assignTo(id.id, expr.evaluate(domain), isVarDeclaration || domain.contextObject.isEmpty, tree.token)
  }
}

object StatementSequenceNode {
  def apply(tree: CgsuiteTree): StatementSequenceNode = StatementSequenceNode(tree, tree.getChildren.map { Node(_) })
}

case class StatementSequenceNode(tree: CgsuiteTree, statements: Seq[Node]) extends Node {
  assert(tree.getType == STATEMENT_SEQUENCE, tree.getType)
  override def evaluate(domain: Domain) = {
    statements.foldLeft[Any](Nil) { (retval, n) => n.evaluate(domain) }
  }
}

object ClassDeclarationNode {
  def apply(tree: CgsuiteTree): ClassDeclarationNode = {
    val isEnum = tree.getType == ENUM
    val modifiers = ModifierNodes(tree.getChild(0))
    val id = IdentifierNode(tree.getChild(1))
    val extendsClause = tree.getChildren.find { _.getType == EXTENDS } match {
      case Some(t) => t.getChildren.map { Node(_) }
      case None => Seq.empty
    }
    val constructorParams = tree.getChildren.find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) }
    val declarations = tree.getChildren.find { _.getType == DECLARATIONS }.get.getChildren.flatMap { DeclarationNode(_) }
    val methodDeclarations = declarations collect {
      case x: MethodDeclarationNode => x
    }
    val staticInitializers = declarations collect {
      case x: InitializerNode if x.isStatic => x
    }
    val ordinaryInitializers = declarations collect {
      case x: InitializerNode if !x.isStatic => x
    }
    val enumElements = tree.getChildren.find { _.getType == ENUM_ELEMENT_LIST } map { t =>
      t.getChildren.map { u => EnumElementNode(u, IdentifierNode(u.getChild(1)), ModifierNodes(u.getChild(0))) }
    }
    ClassDeclarationNode(
      tree,
      id,
      isEnum,
      modifiers,
      extendsClause,
      constructorParams,
      methodDeclarations,
      staticInitializers,
      ordinaryInitializers,
      enumElements
    )
  }
}

case class ClassDeclarationNode(
  tree: CgsuiteTree,
  id: IdentifierNode,
  isEnum: Boolean,
  modifiers: Seq[ModifierNode],
  extendsClause: Seq[Node],
  constructorParams: Option[ParametersNode],
  methodDeclarations: Seq[MethodDeclarationNode],
  staticInitializers: Seq[InitializerNode],
  ordinaryInitializers: Seq[InitializerNode],
  enumElements: Option[Seq[EnumElementNode]]
) extends Node {

  def isMutable = modifiers.exists { _.modifier == Modifier.Mutable }
  def isSystem = modifiers.exists { _.modifier == Modifier.System }

}

object ParametersNode {
  def apply(tree: CgsuiteTree): ParametersNode = {
    assert(tree.getType == METHOD_PARAMETER_LIST)
    val parameters = tree.getChildren.map { t =>
      assert(t.getType == METHOD_PARAMETER)
      ParameterNode(
        t,
        IdentifierNode(t.getChild(0)),
        IdentifierNode(t.getChild(1)),
        t.getChildren.find { _.getType == QUESTION } map { u => Node(u.getChild(0)) },
        t.getChildren.exists { _.getType == DOTDOTDOT }
      )
    }
    ParametersNode(tree, parameters)
  }
}

case class ParametersNode(tree: CgsuiteTree, parameters: Seq[ParameterNode])

case class ParameterNode(
  tree: CgsuiteTree,
  id: IdentifierNode,
  classId: IdentifierNode,
  defaultValue: Option[Node],
  isExpandable: Boolean
)

object DeclarationNode {

  def apply(tree: CgsuiteTree): Iterable[Node] = {

    tree.getType match {

      case DEF =>
        Iterable(MethodDeclarationNode(
          tree,
          IdentifierNode(tree.getChild(1)),
          ModifierNodes(tree.getChild(0)),
          tree.getChildren.find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) },
          tree.getChildren.find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
        ))

      case STATIC => Iterable(InitializerNode(tree, Node(tree.getChild(0)), isStatic = true))

      case VAR =>
        val modifiers = tree.getChild(0).getChildren.map { t => ModifierNode(t, Modifier.fromString(t.getText)) }
        tree.getChildren.tail map { t =>
          val assignToNode = t.getType match {
            case IDENTIFIER => AssignToNode(t, IdentifierNode(t), ConstantNode(null, Nil), isVarDeclaration = true)
            case ASSIGN => AssignToNode(t, IdentifierNode(t.getChild(0)), Node(t.getChild(1)), isVarDeclaration = true)
          }
          InitializerNode(tree, assignToNode, isStatic = modifiers.exists { _.modifier == Modifier.Static })
        }

      case _ => Iterable(InitializerNode(tree, Node(tree), isStatic = false))

    }

  }

}

case class MethodDeclarationNode(
  tree: CgsuiteTree,
  idNode: IdentifierNode,
  modifiers: Seq[ModifierNode],
  parameters: Option[ParametersNode],
  body: Option[StatementSequenceNode]
) extends Node {

  val isExternal = modifiers.exists { _.modifier == Modifier.External }
  val isOverride = modifiers.exists { _.modifier == Modifier.Override }
  val isStatic = modifiers.exists { _.modifier == Modifier.Static }

}

case class VarDeclarationNode(tree: CgsuiteTree, modifiers: Seq[ModifierNode], id: IdentifierNode) extends Node {
  val isStatic = modifiers.exists { _.modifier == Modifier.Static }
}

case class InitializerNode(tree: CgsuiteTree, body: Node, isStatic: Boolean) extends Node

object ModifierNodes {
  def apply(tree: CgsuiteTree): Seq[ModifierNode] = {
    assert(tree.getType == MODIFIERS)
    tree.getChildren.map { t => ModifierNode(t, Modifier.fromString(t.getText)) }
  }

}

case class EnumElementNode(tree: CgsuiteTree, id: IdentifierNode, modifiers: Seq[ModifierNode])

case class ModifierNode(tree: CgsuiteTree, modifier: Modifier.Value) extends Node

object Modifier extends Enumeration {
  type Modifier = Value
  val External, Mutable, Override, Static, System = Value
  def fromString(str: String) = str match {
    case "external" => External
    case "mutable" => Mutable
    case "override" => Override
    case "static" => Static
    case "system" => System
  }
}
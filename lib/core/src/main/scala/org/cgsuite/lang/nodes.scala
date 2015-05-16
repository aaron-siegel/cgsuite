package org.cgsuite.lang

import org.antlr.runtime.tree.{CommonTree, Tree}
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.Node.treeToRichTree
import org.cgsuite.lang.Ops._
import org.cgsuite.lang.parser.CgsuiteLexer._

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Node {

  implicit def treeToRichTree(tree: Tree): RichTree = new RichTree(tree)

  class RichTree(tree: Tree) {
    lazy val token = tree.asInstanceOf[CommonTree].token
    lazy val children: Seq[Tree] = {
      val jChildren = tree.asInstanceOf[CommonTree].getChildren
      if (jChildren == null)  // Really dumb ANTLR semantics
        Seq.empty
      else
        jChildren.toSeq map { _.asInstanceOf[Tree] }
    }
    def location = {
      if (token.getInputStream == null)
        throw new RuntimeException(tree.toStringTree)
      token.getInputStream.getSourceName + ":" + token.getLine + ":" + token.getCharPositionInLine
    }
  }

}

object EvalNode {

  def apply(tree: Tree): EvalNode = {

    tree.getType match {

      // Constants

      case TRUE => ConstantNode(tree, true)
      case FALSE => ConstantNode(tree, false)
      case INTEGER => ConstantNode(tree, Integer.parseInteger(tree.getText))
      case INF => ConstantNode(tree, positiveInfinity)
      case STRING => ConstantNode(tree, tree.getText.drop(1).dropRight(1).replaceAll("\\\\\"", "\""))
      case NIL => ConstantNode(tree, Nil)

      // This

      case THIS => ThisNode(tree)

      // Identifier

      case IDENTIFIER => IdentifierNode(tree)

      // Ops

      case UNARY_PLUS => UnOpNode(tree, Pos)
      case UNARY_MINUS => UnOpNode(tree, Neg)
      case PLUSMINUS => UnOpNode(tree, PlusMinus)

      case PLUS => BinOpNode(tree, NewPlus)
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
      case EXPLICIT_LIST => ListNode(tree)
      case EXPLICIT_SET => SetNode(tree)
      case EXPLICIT_MAP => MapNode(tree)

      // Game construction

      case SLASHES => GameSpecNode(tree, gameOptions(tree.getChild(0)), gameOptions(tree.getChild(1)), forceExplicit = false)
      case SQUOTE => GameSpecNode(tree, gameOptions(tree.getChild(0).getChild(0)), gameOptions(tree.getChild(0).getChild(1)), forceExplicit = true)

      // Control flow

      case IF | ELSEIF => IfNode(
        tree,
        EvalNode(tree.getChild(0)),
        StatementSequenceNode(tree.getChild(1)),
        Option(tree.getChild(2)).map { EvalNode(_) }
      )
      case ERROR => ErrorNode(tree, EvalNode(tree.getChild(0)))
      case DO | YIELD | LISTOF | SETOF => LoopNode(tree)

      // Procedures

      case RARROW => ProcedureNode(tree)

      // Resolvers

      case DOT => DotNode(tree, EvalNode(tree.getChild(0)), IdentifierNode(tree.getChild(1)))
      case FUNCTION_CALL => FunctionCallNode(tree)
      case ARRAY_REFERENCE => BinOpNode(tree, ArrayReference, EvalNode(tree.getChild(0)), EvalNode(tree.getChild(1).getChild(0)))

      // Assignment

      case ASSIGN => AssignToNode(tree, IdentifierNode(tree.getChild(0)), EvalNode(tree.getChild(1)), isVarDeclaration = false)
      case VAR =>
        val (modifiers, nodes) = VarNode(tree)
        assert(modifiers.isEmpty && nodes.size == 1)
        nodes.head

      // Suppressor

      case SEMI => ConstantNode(tree, Nil)

      // Statement sequence

      case STATEMENT_SEQUENCE => StatementSequenceNode(tree)

    }

  }

  private def gameOptions(tree: Tree): Seq[EvalNode] = {
    tree.getType match {
      case SLASHES => Seq(EvalNode(tree))
      case EXPRESSION_LIST => tree.children.map { EvalNode(_) }
    }
  }

  private def nimber(tree: Tree): EvalNode = {
    if (tree.getChildCount == 0) {
      ConstantNode(tree, star)
    } else {
      UnOpNode(tree, MakeNimber)
    }
  }

  private def upMultiple(tree: Tree): EvalNode = {
    val (upMultipleTree, nimberTree) = tree.getChildCount match {
      case 0 => (None, None)
      case 1 => tree.getChild(0).getType match {
        case UNARY_AST => (None, Some(tree.getChild(0)))
        case _ => (Some(tree.getChild(0)), None)
      }
      case _ => (Some(tree.getChild(0)), Some(tree.getChild(1)))
    }
    val upMultipleNode = upMultipleTree map { EvalNode(_) } getOrElse { ConstantNode(tree, Integer(tree.getText.length)) }
    val nimberNode = nimberTree map { t =>
      if (t.getChildCount == 0) ConstantNode(t, one) else EvalNode(t.getChild(0))
    } getOrElse { ConstantNode(null, zero) }
    tree.getType match {
      case CARET | MULTI_CARET => BinOpNode(tree, MakeUpMultiple, upMultipleNode, nimberNode)
      case VEE | MULTI_VEE => BinOpNode(tree, MakeDownMultiple, upMultipleNode, nimberNode)
    }
  }

}

trait Node {

  def tree: Tree
  def token = tree.asInstanceOf[CommonTree].token
  def ttype = tree.getType

  def children: Iterable[Node]

}

trait EvalNode extends Node {

  def children: Iterable[EvalNode]
  def evaluate(domain: Domain): Any
  def toNodeString: String

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
        sublist map {
          case g: Game => g
          case _ => sys.error("must be a list of games")
        }
      case _ => sys.error("must be a list of games")
    }
  }

  def elaborate(scope: Scope): Unit = {
    children foreach { _.elaborate(scope) }
  }

}

private[lang] object Scope {
  def apply(pkg: Option[CgsuitePackage], classVars: Set[Symbol]) = {
    new Scope(pkg, classVars, mutable.AnyRefMap(), mutable.Stack())
  }
}

private[lang] class Scope(
  val pkg: Option[CgsuitePackage],    // None = "external" (Worksheet/REPL) scope
  val classVars: Set[Symbol],
  val varMap: mutable.AnyRefMap[Symbol, Int],
  val scopeStack: mutable.Stack[mutable.HashSet[Symbol]]
  ) {
  def contains(id: Symbol) = classVars.contains(id) || scopeStack.exists { _.contains(id) }
  def insertId(id: Symbol) {
    if (contains(id)) {
      throw InputException(s"Duplicate var: ${id.name}")
    } else {
      scopeStack.top += id
      varMap.getOrElseUpdate(id, varMap.size)
    }
  }
}

case class ConstantNode(tree: Tree, constantValue: Any) extends EvalNode {
  override val children = Seq.empty
  override def evaluate(domain: Domain) = constantValue
  def toNodeString = {
    if (constantValue == Nil)
      "nil"
    else
      constantValue.toString
  }
}

case class ThisNode(tree: Tree) extends EvalNode {
  override val children = Seq.empty
  override def evaluate(domain: Domain) = domain.contextObject.getOrElse { sys.error("invalid `this`") }
  def toNodeString = "this"
}

object IdentifierNode {
  def apply(tree: Tree): IdentifierNode = IdentifierNode(tree, Symbol(tree.getText))
}

case class IdentifierNode(tree: Tree, id: Symbol) extends EvalNode {

  // Cache the resolver here
  val resolver = Resolver.forId(id)
  var classResolution: ClassObject = _
  var methodScopeIndex: Int = -1

  override val children = Seq.empty

  override def elaborate(scope: Scope) {
    // Try looking up as a Class in local package scope
    scope.pkg.flatMap { _.lookupClass(id) } match {
      case Some(cls) => classResolution = cls.classObject
      case None =>
        // Try looking up as a Class in default package scope
        CgsuitePackage.lookupClass(id) match {
          case Some(cls) => classResolution = cls.classObject
          case None =>
            // Try looking up in local (method) scope
            if (scope.scopeStack.exists { _.contains(id) }) {
              methodScopeIndex = scope.varMap(id)
            } else if (scope.pkg.isDefined && !scope.classVars.contains(id)) {
              // Unless we're at Worksheet scope, it's illegal to refer to an undefined variable.
              throw InputException(s"Undefined variable: ${id.name}", token = Some(token))
            }
        }
    }
  }

  override def evaluate(domain: Domain) = {
    if (methodScopeIndex >= 0) {
      val x = domain.localScope(methodScopeIndex)
      val result = if (x == null) Nil else x
      result
    } else if (classResolution != null) {
      classResolution
    } else if (domain.isOuterDomain) {
      domain getDynamicVar id getOrElse {
        throw InputException(s"Undefined variable: ${id.name}", token = Some(token))
      }
    } else {
      val y = resolver.resolve(domain.contextObject.get)
      if (y == null)
        throw InputException(s"Undefined variable: ${id.name}", token = Some(token))
      else
        y
    }
  }

  def toNodeString = id.name

}

object UnOpNode {
  def apply(tree: Tree, op: UnOp): UnOpNode = UnOpNode(tree, op, EvalNode(tree.getChild(0)))
}

case class UnOpNode(tree: Tree, op: UnOp, operand: EvalNode) extends EvalNode {
  override val children = Seq(operand)
  override def evaluate(domain: Domain) = op(operand.evaluate(domain))
  def toNodeString = op.name + operand.toNodeString
}

object BinOpNode {
  def apply(tree: Tree, op: BinOp): BinOpNode = BinOpNode(tree, op, EvalNode(tree.getChild(0)), EvalNode(tree.getChild(1)))
}

case class BinOpNode(tree: Tree, op: BinOp, operand1: EvalNode, operand2: EvalNode) extends EvalNode {
  override val children = Seq(operand1, operand2)
  override def evaluate(domain: Domain) = op(operand1.evaluate(domain), operand2.evaluate(domain))
  def toNodeString = s"(${operand1.toNodeString} ${op.name} ${operand2.toNodeString})"
}

object ListNode {
  def apply(tree: Tree): ListNode = {
    assert(tree.getType == EXPLICIT_LIST)
    ListNode(tree, tree.children.map { EvalNode(_) }.toIndexedSeq)
  }
}

case class ListNode(tree: Tree, elements: IndexedSeq[EvalNode]) extends EvalNode {
  override val children = elements
  override def evaluate(domain: Domain): IndexedSeq[_] = {
    elements.size match {
      // This is to avoid closures and get optimal performance on small collections.
      case 0 => IndexedSeq.empty
      case 1 => IndexedSeq(elements(0).evaluate(domain))
      case 2 => IndexedSeq(elements(0).evaluate(domain), elements(1).evaluate(domain))
      case 3 => IndexedSeq(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain))
      case 4 => IndexedSeq(elements(0).evaluate(domain), elements(1).evaluate(domain), elements(2).evaluate(domain), elements(3).evaluate(domain))
      case _ => elements.map { _.evaluate(domain) }
    }
  }
  def toNodeString = {
    if (elements.isEmpty)
      "nil"
    else
      "[" + (elements map {  _.toNodeString } mkString ",") + "]"
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
  override def evaluate(domain: Domain): Set[_] = {
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
  def toNodeString = "{" + (elements map { _.toNodeString } mkString ", ") + "}"
}

object MapNode {
  def apply(tree: Tree): MapNode = {
    assert(tree.getType == EXPLICIT_MAP)
    val mapPairNodes = tree.children.map { t => MapPairNode(t, EvalNode(t.getChild(0)), EvalNode(t.getChild(1))) }
    MapNode(tree, mapPairNodes.toIndexedSeq)
  }
}

case class MapNode(tree: Tree, elements: IndexedSeq[MapPairNode]) extends EvalNode {
  override val children = elements
  override def evaluate(domain: Domain): Map[_, _] = {
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
  def toNodeString = {
    if (elements.isEmpty)
      "{=>}"
    else
      "{" + (elements map { _.toNodeString } mkString ",") + "}"
  }
}

case class MapPairNode(tree: Tree, from: EvalNode, to: EvalNode) extends EvalNode {
  override val children = Seq(from, to)
  override def evaluate(domain: Domain): (Any, Any) = from.evaluate(domain) -> to.evaluate(domain)
  def toNodeString = s"${from.toNodeString} => ${to.toNodeString}"
}

case class GameSpecNode(tree: Tree, lo: Seq[EvalNode], ro: Seq[EvalNode], forceExplicit: Boolean) extends EvalNode {
  override val children = lo ++ ro
  override def evaluate(domain: Domain) = {
    val gl = lo.flatMap { _.evaluateAsGame(domain) }
    val gr = ro.flatMap { _.evaluateAsGame(domain) }
    if (!forceExplicit && (gl ++ gr).forall { _.isInstanceOf[CanonicalShortGame] }) {
      CanonicalShortGame(gl map { _.asInstanceOf[CanonicalShortGame] }, gr map { _.asInstanceOf[CanonicalShortGame] })
    } else {
      ExplicitGame(gl, gr)
    }
  }
  def toNodeString = {
    val loStr = lo map { _.toNodeString } mkString ","
    val roStr = ro map { _.toNodeString } mkString ","
    s"{$loStr | $roStr}"
  }
}

case class IfNode(tree: Tree, condition: EvalNode, ifNode: StatementSequenceNode, elseNode: Option[EvalNode]) extends EvalNode {
  override val children = Seq(condition, ifNode) ++ elseNode
  override def evaluate(domain: Domain) = {
    if (condition.evaluateAsBoolean(domain))
      ifNode.evaluate(domain)
    else
      elseNode.map { _.evaluate(domain) }.getOrElse(Nil)
  }
  def toNodeString = s"if ${condition.toNodeString} then ${ifNode.toNodeString}" +
    (elseNode map { " " + _.toNodeString } getOrElse "") + " end"
}

object LoopNode {

  def apply(tree: Tree): LoopNode = {

    val loopType = tree.getType match {
      case DO => Do
      case YIELD | LISTOF => YieldList
      case SETOF => YieldSet
      case TABLEOF => YieldTable
      case SUMOF => YieldSum
    }
    val body = EvalNode(tree.children.last)
    val loopSpecs = tree.children.dropRight(1)
    assert(loopSpecs.forall { _.getType == LOOP_SPEC })

    def makeLoopNode(loopSpecTree: Tree, nextNode: EvalNode): LoopNode = {
      LoopNode(
        loopSpecTree,
        loopType,
        loopSpecTree.children.find { _.getType == FOR   }.map { t => IdentifierNode(t.getChild(0)) },
        loopSpecTree.children.find { _.getType == IN    }.map { t => EvalNode(t.getChild(0)) },
        loopSpecTree.children.find { _.getType == FROM  }.map { t => EvalNode(t.getChild(0)) },
        loopSpecTree.children.find { _.getType == TO    }.map { t => EvalNode(t.getChild(0)) },
        loopSpecTree.children.find { _.getType == BY    }.map { t => EvalNode(t.getChild(0)) },
        loopSpecTree.children.find { _.getType == WHILE }.map { t => EvalNode(t.getChild(0)) },
        loopSpecTree.children.find { _.getType == WHERE }.map { t => EvalNode(t.getChild(0)) },
        nextNode
      )
    }

    loopSpecs.foldRight(body)(makeLoopNode).asInstanceOf[LoopNode]

  }

  sealed trait LoopType
  case object Do extends LoopType
  case object YieldList extends LoopType
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
    case LoopNode.YieldList | LoopNode.YieldSet | LoopNode.YieldTable => true
  }
  private val pushDownYield: Option[LoopNode] = (isYield, body) match {
    case (true, loopBody: LoopNode) =>
      assert(loopBody.isYield)
      Some(loopBody)
    case _ => None
  }

  override def elaborate(scope: Scope) {
    forId match {
      case Some(idNode) =>
        scope.scopeStack.push(new mutable.HashSet())
        scope.insertId(idNode.id)
      case None =>
    }
    super.elaborate(scope)
    if (forId.isDefined) {
      scope.scopeStack.pop()
    }
  }

  override def evaluate(domain: Domain): Any = {

    val yieldResult = if (isYield) ArrayBuffer[Any]() else null
    evaluate(domain, yieldResult)
    loopType match {
      case LoopNode.Do => Nil
      case LoopNode.YieldList => yieldResult
      case LoopNode.YieldSet => yieldResult.toSet   // TODO(?) This could be made more efficient
      case _ => sys.error("not implemented yet")
    }

  }

  def toNodeString = {
    val loopTypeStr = loopType match {
      case LoopNode.Do => "do"
      case LoopNode.YieldList => "yield"
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

  def evaluate(domain: Domain, yieldResult: ArrayBuffer[Any]): Unit = {

    Profiler.start(prepareLoop)

    val forIndex = if (forId.isDefined) forId.get.methodScopeIndex else -1
    var counter = if (from.isDefined) from.get.evaluate(domain) else null
    val toVal = if (to.isDefined) to.get.evaluate(domain) else null
    val byVal = if (by.isDefined) by.get.evaluate(domain) else one
    val iterator = if (in.isDefined) in.get.evaluateAsIterator(domain) else null

    Profiler.stop(prepareLoop)

    Profiler.start(loop)

    var continue = true

    while (continue) {

      if (iterator == null) {
        if (forIndex >= 0)
          domain.localScope(forIndex) = counter
        continue = toVal == null || Ops.leq(counter, toVal)
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
          if (pushDownYield.isEmpty) {
            val r = body.evaluate(domain)
            if (isYield) {
              r match {
                case it: Iterable[_] => yieldResult ++= it
                case x => yieldResult += x
              }
            }
          } else {
            pushDownYield.get.evaluate(domain, yieldResult)
          }
          Profiler.stop(loopBody)
        }
        if (counter != null)
          counter = Ops.NewPlus(counter, byVal)
      }

    }

    Profiler.stop(loop)

  }

}

object ProcedureNode {
  def apply(tree: Tree): ProcedureNode = {
    // TODO Parse arguments properly (types & defaults) - requires some refactoring
    val parameters = tree.getChild(0).children map { paramTree =>
      assert(paramTree.getType == METHOD_PARAMETER)
      val id = Symbol(paramTree.getChild(0).getText)
      MethodParameter(id, CgsuiteClass.Object, None)
    }
    ProcedureNode(tree, parameters, EvalNode(tree.getChild(1)))
  }
}

case class ProcedureNode(tree: Tree, parameters: Seq[MethodParameter], body: EvalNode) extends EvalNode {
  override val children = (parameters flatMap { _.defaultValue }) :+ body
  override def elaborate(scope: Scope) = {
    val newScope = Scope(scope.pkg, scope.classVars)
    newScope.scopeStack.push(mutable.HashSet())
    parameters foreach { param =>
      newScope.insertId(param.id)
      param.methodScopeIndex = newScope.varMap(param.id)
      param.defaultValue foreach { _.elaborate(newScope) }
    }
    body.elaborate(newScope)
    newScope.scopeStack.pop()
    localVariableCount = newScope.varMap.size
  }
  override def evaluate(domain: Domain) = Procedure(this, domain)
  val ordinal = CallSite.newCallSiteOrdinal
  var localVariableCount: Int = 0
  def toNodeString = {
    val paramStr = {
      if (parameters.length == 1)
        parameters.head.id.name
      else
        "(" + (parameters map { _.id.name } mkString ", ") + ")"
    }
    paramStr + " -> " + body.toNodeString
  }
}

case class ErrorNode(tree: Tree, msg: EvalNode) extends EvalNode {
  override val children = Seq(msg)
  override def evaluate(domain: Domain) = {
    throw InputException(msg.evaluate(domain).toString)
  }
  def toNodeString = s"error(${msg.toNodeString})"
}

case class DotNode(tree: Tree, obj: EvalNode, idNode: IdentifierNode) extends EvalNode {
  override val children = Seq(obj, idNode)
  val asQualifiedClassName: Option[Symbol] = obj match {
    case IdentifierNode(_, antecedentId) => Some(Symbol(antecedentId.name + "." + idNode.id.name))
    case node: DotNode => node.asQualifiedClassName.map { next => Symbol(next.name + "." + idNode.id.name) }
    case _ => None
  }
  var classResolution: CgsuiteClass = _
  override def elaborate(scope: Scope) {
    asQualifiedClassName flatMap CgsuitePackage.lookupClass match {
      case Some(cls) => classResolution = cls
      case None => obj.elaborate(scope)     // Deliberately bypass idNode
    }
  }
  override def evaluate(domain: Domain): Any = {
    if (classResolution != null)
      classResolution.classObject
    else {
      val x = obj.evaluate(domain)
      val y = idNode.resolver.resolve(x)
      if (y == null)
        throw InputException(
          s"Not a member variable: ${idNode.id.name} (in object of type ${CgsuiteClass.of(x).qualifiedName.name})",
          token = Some(token)
        )
      else
        y
    }
  }
  def toNodeString = {
    obj match {
      case _: ConstantNode | _: IdentifierNode | _: DotNode | _: ListNode | _: SetNode | _: FunctionCallNode |
        _: GameSpecNode | _: MapNode | _: ThisNode => s"${obj.toNodeString}.${idNode.toNodeString}"
      case _ => s"(${obj.toNodeString}).${idNode.toNodeString})"
    }
  }
}

object FunctionCallNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSite = EvalNode(tree.getChild(0))
    val argsWithNames = tree.getChild(1).children.map { t =>
      t.getType match {
        case BIGRARROW => (EvalNode(t.getChild(1)), Some(IdentifierNode(t.getChild(0))))
        case _ => (EvalNode(t), None)
      }
    }
    val (args, argNames) = argsWithNames.unzip
    FunctionCallNode(tree, callSite, args.toIndexedSeq, argNames.toIndexedSeq)
  }
}

case class FunctionCallNode(
  tree: Tree,
  callSite: EvalNode,
  args: IndexedSeq[EvalNode],
  argNames: IndexedSeq[Option[IdentifierNode]]
  ) extends EvalNode {

  var resolutions: mutable.LongMap[FunctionCallResolution] = mutable.LongMap()

  // Some profiler keys
  val prepareCallSite = Symbol(s"PrepareCallSite [${tree.location}]")
  val prepareCallArgs = Symbol(s"PrepareCallArgs [${tree.location}]")
  val functionCall = Symbol(s"FunctionCall [${tree.location}]")

  override val children = (callSite +: args) ++ argNames.flatten

  override def elaborate(scope: Scope): Unit = {
    callSite elaborate scope
    args foreach { _ elaborate scope }
  }

  override def evaluate(domain: Domain) = {

    val obj = this.callSite.evaluate(domain)
    val callSite: CallSite = obj match {
      case im: InstanceMethod => im
      case proc: Procedure => proc
      case co: ClassObject => co.forClass.constructor match {
        case Some(ctor) => ctor
        case None => throw InputException(
          s"The class ${co.forClass.id.name} cannot be directly instantiated.",
          token = Some(token)
        )
      }
      case _ => throw InputException("That is not a method or procedure: " + obj, token = Some(token))
    }

    val res = resolutions.getOrElseUpdate(callSite.ordinal, makeNewResolution(callSite))
    val args = new Array[Any](res.parameterToArgsMapping.length)
    var i = 0
    while (i < res.parameterToArgsMapping.length) {
      if (res.parameterToArgsMapping(i) >= 0)
        args(i) = this.args(res.parameterToArgsMapping(i)).evaluate(domain)
      else
        args(i) = callSite.parameters(i).defaultValue.get.evaluate(domain)
      i += 1
    }
    callSite.call(args)

  }

  private def makeNewResolution(callSite: CallSite) = {
    if (argNames.length > callSite.parameters.length)
      throw InputException(s"Too many arguments for `$callSite`: ${argNames.length}")
    FunctionCallResolution(callSite.parameters, argNames)
  }

  def toNodeString = {
    val argStr = args map { _.toNodeString } mkString ", "
    callSite match {
      case _: ConstantNode | _: IdentifierNode | _: DotNode | _: ListNode | _: SetNode | _: FunctionCallNode |
           _: GameSpecNode | _: MapNode | _: ThisNode => s"${callSite.toNodeString}($argStr)"
      case _ => s"(${callSite.toNodeString})($argStr)"
    }
  }

}

object FunctionCallResolution {
  def apply(params: Seq[MethodParameter], argNames: IndexedSeq[Option[IdentifierNode]]): FunctionCallResolution = {

    val parameterToArgsMapping = new Array[Int](params.length)
    java.util.Arrays.fill(parameterToArgsMapping, -1)
    argNames.zipWithIndex.foreach {
      case (None, index) => parameterToArgsMapping(index) = index
      case (Some(idNode), index) =>
        val namedIndex = params.indexWhere { _.id == idNode.id }
        parameterToArgsMapping(namedIndex) = index
    }
    FunctionCallResolution(parameterToArgsMapping)
  }
}

case class FunctionCallResolution(parameterToArgsMapping: Array[Int])

case class AssignToNode(tree: Tree, id: IdentifierNode, expr: EvalNode, isVarDeclaration: Boolean) extends EvalNode {
  // TODO Catch illegal assignment to temporary loop variable (during elaboration)
  // TODO Catch illegal assignment to immutable object member (during elaboration)
  override val children = Seq(id, expr)
  override def elaborate(scope: Scope) {
    if (isVarDeclaration) {
      scope.insertId(id.id)
    }
    super.elaborate(scope)
  }
  override def evaluate(domain: Domain) = {
    val newValue = expr.evaluate(domain)
    if (id.methodScopeIndex >= 0) {
      domain.localScope(id.methodScopeIndex) = newValue
    } else if (id.classResolution != null) {
      throw InputException(s"Cannot assign to class name as variable: ${id.id.name}", token = Some(token))
    } else if (domain.isOuterDomain) {
      domain.putDynamicVar(id.id, newValue)
    } else {
      val res = id.resolver.findResolution(domain.contextObject.get)
      if (res.classScopeIndex >= 0)
        domain.contextObject.get.asInstanceOf[StandardObject].vars(res.classScopeIndex) = newValue.asInstanceOf[AnyRef]
      else
        throw InputException(s"Unknown variable for assignment: `${id.id.name}`", token = Some(token))
    }
    newValue
  }
  def toNodeString = {
    val varStr = if (isVarDeclaration) "var " else ""
    varStr + id.toNodeString + " := " + expr.toNodeString
  }
}

object StatementSequenceNode {
  def apply(tree: Tree): StatementSequenceNode = StatementSequenceNode(tree, tree.children.map { EvalNode(_) })
}

case class StatementSequenceNode(tree: Tree, statements: Seq[EvalNode]) extends EvalNode {
  assert(tree.getType == STATEMENT_SEQUENCE, tree.getType)
  override val children = statements
  override def elaborate(scope: Scope) = {
    scope.scopeStack.push(mutable.HashSet())
    statements.foreach { _.elaborate(scope) }
    scope.scopeStack.pop()
  }
  override def evaluate(domain: Domain) = {
    var result: Any = Nil
    val iterator = statements.iterator
    while (iterator.hasNext) {
      result = iterator.next().evaluate(domain)
    }
    result
  }
  def toNodeString = {
    "begin " + (statements map { _.toNodeString } mkString "; ") + " end"
  }
}

object ClassDeclarationNode {
  def apply(tree: Tree): ClassDeclarationNode = {
    val isEnum = tree.getType == ENUM
    val modifiers = ModifierNodes(tree.getChild(0))
    val id = IdentifierNode(tree.getChild(1))
    val extendsClause = tree.children.find { _.getType == EXTENDS } match {
      case Some(t) => t.children.map { EvalNode(_) }
      case None => Seq.empty
    }
    val constructorParams = tree.children.find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) }
    val declarations = tree.children.filter { _.getType == DECLARATIONS }.flatMap { _.children.flatMap { DeclarationNode(_) } }
    val methodDeclarations = declarations collect {
      case x: MethodDeclarationNode => x
    }
    val staticInitializers = declarations collect {
      case x: InitializerNode if x.isStatic => x
    }
    val ordinaryInitializers = declarations collect {
      case x: InitializerNode if !x.isStatic => x
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
      ordinaryInitializers
    )
  }
}

case class ClassDeclarationNode(
  tree: Tree,
  id: IdentifierNode,
  isEnum: Boolean,
  modifiers: Seq[ModifierNode],
  extendsClause: Seq[Node],
  constructorParams: Option[ParametersNode],
  methodDeclarations: Seq[MethodDeclarationNode],
  staticInitializers: Seq[InitializerNode],
  ordinaryInitializers: Seq[InitializerNode]
) extends Node {

  val children = (id +: modifiers) ++ extendsClause ++ constructorParams.toSeq ++
    methodDeclarations ++ staticInitializers ++ ordinaryInitializers
  def isMutable = modifiers.exists { _.modifier == Modifier.Mutable }
  def isSystem = modifiers.exists { _.modifier == Modifier.System }

}

object ParametersNode {
  def apply(tree: Tree): ParametersNode = {
    assert(tree.getType == METHOD_PARAMETER_LIST)
    val parameters = tree.children.map { t =>
      assert(t.getType == METHOD_PARAMETER)
      ParameterNode(
        t,
        IdentifierNode(t.getChild(0)),
        IdentifierNode(t.getChild(1)),
        t.children.find { _.getType == QUESTION } map { u => EvalNode(u.getChild(0)) },
        t.children.exists { _.getType == DOTDOTDOT }
      )
    }
    ParametersNode(tree, parameters)
  }
}

case class ParametersNode(tree: Tree, parameters: Seq[ParameterNode]) extends Node {
  override val children = parameters
}

case class ParameterNode(
  tree: Tree,
  id: IdentifierNode,
  classId: IdentifierNode,
  defaultValue: Option[EvalNode],
  isExpandable: Boolean
) extends Node {
  override val children = Seq(id, classId) ++ defaultValue
}

object VarNode {
  def apply(tree: Tree): (Seq[ModifierNode], Seq[AssignToNode]) = {
    assert(tree.getType == VAR || tree.getType == ENUM_ELEMENT)
    val modifiers = tree.getChild(0).children.map { t => ModifierNode(t, Modifier.fromString(t.getText)) }
    val nodes = tree.children.tail.map { t =>
      t.getType match {
        case IDENTIFIER => AssignToNode(t, IdentifierNode(t), ConstantNode(null, Nil), isVarDeclaration = true)
        case ASSIGN => AssignToNode(t, IdentifierNode(t.getChild(0)), EvalNode(t.getChild(1)), isVarDeclaration = true)
      }
    }
    (modifiers, nodes)
  }
}

object DeclarationNode {

  def apply(tree: Tree): Iterable[Node] = {

    tree.getType match {

      case DEF =>
        Iterable(MethodDeclarationNode(
          tree,
          IdentifierNode(tree.getChild(1)),
          ModifierNodes(tree.getChild(0)),
          tree.children.find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) },
          tree.children.find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
        ))

      case STATIC => Iterable(InitializerNode(tree, EvalNode(tree.getChild(0)), isStatic = true, isExternal = false))

      case VAR | ENUM_ELEMENT =>
        val (modifiers, nodes) = VarNode(tree)
        nodes.map { node =>
          InitializerNode(
            tree,
            node,
            isStatic = tree.getType == ENUM_ELEMENT || modifiers.exists { _.modifier == Modifier.Static },
            isExternal = modifiers.exists { _.modifier == Modifier.External }
          )
        }

      case _ => Iterable(InitializerNode(tree, EvalNode(tree), isStatic = false, isExternal = false))

    }

  }

}

case class MethodDeclarationNode(
  tree: Tree,
  idNode: IdentifierNode,
  modifiers: Seq[ModifierNode],
  parameters: Option[ParametersNode],
  body: Option[StatementSequenceNode]
) extends Node {

  val children = (idNode +: modifiers) ++ parameters ++ body
  val isExternal = modifiers.exists { _.modifier == Modifier.External }
  val isOverride = modifiers.exists { _.modifier == Modifier.Override }
  val isStatic = modifiers.exists { _.modifier == Modifier.Static }

}

case class InitializerNode(tree: Tree, body: EvalNode, isStatic: Boolean, isExternal: Boolean) extends Node {
  val children = Seq(body)
}

object ModifierNodes {
  def apply(tree: Tree): Seq[ModifierNode] = {
    assert(tree.getType == MODIFIERS)
    tree.children.map { t => ModifierNode(t, Modifier.fromString(t.getText)) }
  }
}

case class EnumElementNode(tree: Tree, id: IdentifierNode, modifiers: Seq[ModifierNode]) extends Node {
  val children = id +: modifiers
}

case class ModifierNode(tree: Tree, modifier: Modifier.Value) extends Node {
  val children = Seq.empty
}

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

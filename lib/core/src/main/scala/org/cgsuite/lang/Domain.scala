package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.{MalformedParseTreeException, CgsuiteTree}
import scala.collection.JavaConversions._
import scala.collection.mutable
import org.cgsuite.util.{Grid, Coordinates}

class Domain(
  args: Map[String, Any] = Map.empty,
  contextObject: Option[Any] = None,
  contextMethod: Option[CgsuiteClass#Method] = None
  ) {

  val namespace = mutable.Map[String, Any]()

  def statementSequence(tree: CgsuiteTree): Any = {

    assert(tree.getType == STATEMENT_SEQUENCE)

    tree.getChildren.foldLeft[Any](Nil) { (retval, t) => statement(t) }

  }

  def statement(tree: CgsuiteTree): Any = {

    tree.getType match {

      case SEMI => Nil
      case _ => expression(tree)

    }

  }

  def expression(tree: CgsuiteTree): Any = {

    tree.getType match {

      case STATEMENT_SEQUENCE => statementSequence(tree)

      // Constants

      case TRUE => true
      case FALSE => false
      case INTEGER => Integer.parseInteger(tree.getText)
      case INF => positiveInfinity
      case STRING => tree.getText.drop(1).dropRight(1)
      case NIL => Nil

      // Identifier

      case IDENTIFIER => lookup(tree.getToken)

      // Operations

      case UNARY_PLUS => unop(tree) {
        case (x: Game) => +x
      }

      case UNARY_MINUS => unop(tree) {
        case (x: Game) => -x
      }

      case PLUS => binop(tree) { plus }

      case MINUS => binop(tree) {
        case (x: Game, _: Zero) => x
        case (_: Zero, y: Game) => y
        case (x: Integer, y: Integer) => x - y
        case (x: RationalNumber, y: RationalNumber) => x - y
        case (x: NumberUpStar, y: NumberUpStar) => x - y
        case (x: CanonicalShortGame, y: CanonicalShortGame) => x - y
        case (x: Game, y: Game) => x - y
        case (x: Coordinates, y: Coordinates) => x - y
      }

      case PLUSMINUS => unop(tree) {
        case (x: CanonicalShortGame) => CanonicalShortGame(x)(-x)
        case (x: Game) => ExplicitGame(x)(-x)
      }

      case AST => binop(tree) {
        case (_: Zero, _: Game) => zero
        case (x: Integer, y: Integer) => x * y
        case (x: RationalNumber, y: RationalNumber) => x * y
        case (x: Integer, y: Game) => x * y
        case (x: Coordinates, y: Integer) => x * y
        case (x: Integer, y: Coordinates) => y * x
      }

      case FSLASH => binop(tree) {
        case (x: RationalNumber, y: RationalNumber) => x / y
      }

      case PERCENT => binop(tree) {
        case (x: Integer, y: Integer) => x % y
        case (x: RationalNumber, y: RationalNumber) => x % y
      }

      case EXP => binop(tree) {
        case (x: RationalNumber, y: Integer) => x.pow(y)
      }

      case UNARY_AST => starExpression(tree)

      case CARET | VEE | MULTI_CARET | MULTI_VEE => upExpression(tree)

      // Relations

      case EQUALS => binop(tree) {
        case (x: CanonicalStopperSidedGame, y: CanonicalStopperSidedGame) => x == y
        case (x: Game, y: Game) => leq(x, y) && leq(y, x)
        case (x, y) => x == y
      }
      case NEQ => binop(tree) {
        case (x: CanonicalStopperSidedGame, y: CanonicalStopperSidedGame) => x != y
        case (x: Game, y: Game) => !leq(x, y) && !leq(y, x)
        case (x, y) => x != y
      }
      case REFEQUALS => binop(tree) { _ == _ }
      case REFNEQ => binop(tree) { _ != _ }
      case LEQ => binop(tree) { leq }
      case GEQ => binop(tree) { (a, b) => leq(b, a) }
      case LT => binop(tree) { (a, b) => leq(a, b) && !leq(b, a) }
      case GT => binop(tree) { (a, b) => !leq(a, b) && leq(b, a) }
      case LCONFUSED => binop(tree) { (a, b) => !leq(b, a) }
      case GCONFUSED => binop(tree) { (a, b) => !leq(a, b) }
      case COMPARE => binop(tree) { (a, b) => (leq(a, b), leq(b, a)) match {
        case (true, true) => zero
        case (true, false) => negativeOne
        case (false, true) => one
        case (false, false) => star
      }}

      // Boolean combinations

      case AND => binop(tree) { case (x: Boolean, y: Boolean) => x && y }
      case OR => binop(tree) { case (x: Boolean, y: Boolean) => x || y }
      case NOT => unop(tree) { case x: Boolean => !x }

      // Explicit collection construction

      case COORDINATES => Coordinates(integer(tree.getChild(0)), integer(tree.getChild(1)))
      case EXPLICIT_LIST => tree.getChildren.map(expression).toSeq
      case EXPLICIT_SET => tree.getChildren.map(expression).toSet
      case EXPLICIT_MAP => tree.getChildren.map { child =>
        expression(child.getChild(0)) -> expression(child.getChild(1))
      }.toMap

      // Game construction

      case SLASHES => gameSpec(tree, forceExplicit = false)
      case SQUOTE => gameSpec(tree.getChild(0), forceExplicit = true)

      // Control flow

      case IF | ELSEIF =>
        if (boolean(tree.getChild(0)))
          statementSequence(tree.getChild(1))
        else if (tree.getChildCount > 2)
          expression(tree.getChild(2))
        else
          Nil

      case ELSE => statementSequence(tree.getChild(0))

      case DO | YIELD => doLoop(tree)

      case ERROR =>
        val msg = toStringOutput(statementSequence(tree.getChild(0)))
        throw InputException(msg, token = Some(tree.token))

      // Resolvers

      case DOT =>
        val obj = expression(tree.getChild(0))
        val id = tree.getChild(1).getText
        resolve(obj, id) getOrElse {
          throw InputException(s"Member not found: $id (in object of type ${CgsuiteClass.of(obj)})")
        }

      case FUNCTION_CALL =>
        val x = expression(tree.getChild(0))
        val (optParams, params) = tree.getChild(1).getChildren.partition { _.getType == BIGRARROW }
        val args = params.map(expression)
        val optArgs = optParams.map { child => (child.getChild(0).getText, expression(child.getChild(1))) }.toMap
        x match {
          case site: CallSite => site.call(args, optArgs)
          case _ => throw InputException("That is not a method or procedure.", token = Some(tree.getToken))
        }

      case ARRAY_REFERENCE =>
        val x = expression(tree.getChild(0))
        assert(tree.getChild(1).getType == ARRAY_INDEX_LIST)
        val indices = tree.getChild(1).getChildren map { expression }
        x match {
          case grid: Grid => grid.get(indices(0).asInstanceOf[Coordinates])
        }

      // Assignment

      case ASSIGN => assignTo(tree.getChild(0), expression(tree.getChild(1)))

    }

  }

  def boolean(tree: CgsuiteTree): Boolean = {
    expression(tree) match {
      case (x: Boolean) => x
      case _ => sys.error("not a bool")
    }
  }

  def smallInteger(tree: CgsuiteTree): Int = {
    expression(tree) match {
      case (x: SmallInteger) => x.intValue
      case (x: Integer) => sys.error("overflow")
      case _ => sys.error("not an int")
    }
  }

  def integer(tree: CgsuiteTree): Integer = {
    expression(tree) match {
      case (x: Integer) => x
      case _ => sys.error("not an int")
    }
  }

  def iterator(tree: CgsuiteTree): Iterator[_] = {
    expression(tree) match {
      case (x: Iterable[_]) => x.iterator
      case _ => sys.error("not a collection")
    }
  }

  def plus(a: Any, b: Any): Any = (a, b) match {
    case (x: Game, _: Zero) => x
    case (_: Zero, y: Game) => y
    case (x: Integer, y: Integer) => x + y
    case (x: RationalNumber, y: RationalNumber) => x + y
    case (x: NumberUpStar, y: NumberUpStar) => x + y
    case (x: CanonicalShortGame, y: CanonicalShortGame) => x + y
    case (x: Game, y: Game) => x + y
    case (x: Coordinates, y: Coordinates) => x + y
    case (x: String, y) => x + y.toString
  }

  def doLoop(tree: CgsuiteTree): Any = {

    val isYield = tree.getType == YIELD
    val forTree = tree.getChildren.find { _.getType == FOR }
    val fromTree = tree.getChildren.find { _.getType == FROM }
    val toTree = tree.getChildren.find { _.getType == TO }
    val byTree = tree.getChildren.find { _.getType == BY }
    val whileTree = tree.getChildren.find { _.getType == WHILE }
    val whereTree = tree.getChildren.find { _.getType == WHERE }
    val inTree = tree.getChildren.find { _.getType == IN }
    val bodyTree = tree.getChildren.last

    val forVar = forTree map { _.getChild(0).getText }
    var counter: Any = fromTree match {
      case Some(t) => expression(t.getChild(0))
      case None => forVar match {
        case Some(id) => lookup(id) match {
          case Some(v) => v
          case None =>
            if (inTree.isEmpty)
              throw InputException("Undefined variable in for loop with no \"from\" clause: " + forVar, token = Some(tree.token))
            else one
        }
        case None => one
      }
    }
    val toVal = toTree map { t => expression(t.getChild(0)) }
    val byVal = byTree map { t => expression(t.getChild(0)) } getOrElse { one }
    val it = inTree map { t => iterator(t.getChild(0)) }
    var result: mutable.MutableList[Any] = null
    if (isYield) {
      result = mutable.MutableList[Any]()
    }

    var continue = true
    do {

      it match {
        case Some(i) => {
          if (i.hasNext)
            forVar foreach { id => put(id, i.next()) }
          else
            continue = false
        }
        case None =>
          forVar foreach { id => put(id, counter) }
          continue = toVal forall { v => leq(counter, v) }
      }

      if (continue) {
        continue = whileTree.forall { t => boolean(t.getChild(0)) }
      }

      if (continue && whereTree.forall { t => boolean(t.getChild(0)) }) {
        val r = statementSequence(bodyTree)
        if (isYield) {
          r match {
            case i: Iterable[_] => result ++= i
            case x => result += x
          }
        }
        counter = plus(counter, byVal)
      }

    } while (continue)

    if (isYield) {
      result.toSeq
    } else {
      Nil
    }

  }

  def lookup(token: Token): Any = {

    val opt = try {
      lookup(token.getText)
    } catch {
      case exc: InputException =>
        exc.addToken(token)
        throw exc
    }
    opt match {
      case Some(x) => x
      case None => throw InputException("That variable is not defined: " + token.getText, token = Option(token))
    }

  }

  def lookup(id: String): Option[Any] = {
    Package.lookupClass(id).map { _.classObject }
      .orElse(args.get(id))
      .orElse(namespace.get(id))
      .orElse(contextObject flatMap { resolve(_, id) })
  }

  def leq(a: Any, b: Any): Boolean = (a, b) match {
    case (x: RationalNumber, y: RationalNumber) => x <= y
    case (x: CanonicalShortGame, y: CanonicalShortGame) => x <= y
  }

  def unop(tree: CgsuiteTree)(op: Any => Any) = {
    val x = expression(tree.getChild(0))
    op(x)
  }

  def binop(tree: CgsuiteTree)(op: (Any, Any) => Any) = {
    val x = expression(tree.getChild(0))
    val y = expression(tree.getChild(1))
    op(x, y)
  }

  def starExpression(tree: CgsuiteTree) = tree.getChildCount match {
    case 0 => star
    case 1 => Nimber(smallInteger(tree.getChild(0)))
  }

  def upExpression(tree: CgsuiteTree) = {

    val (upMultipleTree, nimberTree) = tree.getChildCount match {
      case 0 => (None, None)
      case 1 => tree.getChild(0).getType match {
        case UNARY_AST => (None, Some(tree.getChild(0)))
        case _ => (Some(tree.getChild(0)), None)
      }
      case _ => (Some(tree.getChild(0)), Some(tree.getChild(1)))
    }

    val upMultipleTreeEval = upMultipleTree map { smallInteger } getOrElse 1

    val nimberTreeEval = nimberTree map { tree =>
      tree.getChildCount match {
        case 0 => 1
        case 1 => smallInteger(tree.getChild(0))
      }
    } getOrElse 0

    tree.getType match {
      case CARET => NumberUpStar(zero, upMultipleTreeEval, nimberTreeEval)
      case VEE => NumberUpStar(zero, -upMultipleTreeEval, nimberTreeEval)
      case MULTI_CARET => NumberUpStar(zero, tree.getText.length, nimberTreeEval)
      case MULTI_VEE => NumberUpStar(zero, -tree.getText.length, nimberTreeEval)
    }

  }

  def gameSpec(tree: CgsuiteTree, forceExplicit: Boolean) = {

    assert(tree.getType == SLASHES)

    val lo = gameOptions(tree.getChild(0))
    val ro = gameOptions(tree.getChild(1))

    if (!forceExplicit && (lo ++ ro).forall { _.isInstanceOf[CanonicalShortGame] }) {
      CanonicalShortGame(lo map { _.asInstanceOf[CanonicalShortGame] }, ro map { _.asInstanceOf[CanonicalShortGame] })
    } else {
      ExplicitGame(lo, ro)
    }

  }

  def gameOptions(tree: CgsuiteTree): Iterable[Game] = {

    tree.getType match {

      case SLASHES => Iterable(gameSpec(tree, forceExplicit = false))
      case EXPRESSION_LIST => gameList(tree)
      case _ => throw new MalformedParseTreeException(tree)

    }

  }

  def gameList(tree: CgsuiteTree): Iterable[Game] = {

    assert(tree.getType == EXPRESSION_LIST)

    tree.getChildren flatMap { child =>

      expression(child) match {
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

  def resolve(x: Any, id: String): Option[Any] = {

    /*
    // Shortcut resolver
    MethodRegistry.lookup(id, x) match {
      case Some(y) => y
      case None => sys.error("TODO: dynamic resolve")
    }
    */

    x match {
      case so: StandardObject => so.lookup(id)
      case _ =>
        CgsuiteClass.of(x).lookupMethod(id).map { method =>
          if (method.autoinvoke)
            method.call(x, Seq.empty, Map.empty)
          else
            InstanceMethod(x, method)
        }
    }

  }

  def assignTo(tree: CgsuiteTree, x: Any): Any = {

    tree.getType match {

      case IDENTIFIER =>
        val id = tree.getText
        try {
          put(id, x)
        } catch {
          case exc: InputException =>
            exc.addToken(tree.token)
            throw exc
        }

      case _ => throw InputException("Not a valid assignment antecedent.", token = Option(tree.getToken))

    }

  }

  def put(id: String, x: Any): Any = {
    namespace.put(id, x)
    x
  }

  def toStringOutput(x: Any) = x.toString

}

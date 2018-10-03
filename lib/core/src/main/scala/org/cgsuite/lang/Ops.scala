package org.cgsuite.lang

import java.util

import org.antlr.runtime.tree.Tree
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.dsl.IntegerIsIntegral
import org.cgsuite.exception.EvalException
import org.cgsuite.output.{Output, StyledTextOutput}
import org.cgsuite.util.{Coordinates, Grid, Strip}

import scala.collection.immutable.NumericRange
import scala.collection.mutable

object OperatorPrecedence {

  val Primary, Nim, Postfix, Exp, Neg, Mult, OrdinalSum, Sidle, Plus, Range, Infix, Relational,
      Is, Not, And, Or, FunctionDef, Assign, StatementSeq = next

  private var prec = 0

  private def next = {
    prec += 1
    prec
  }

}

object Ops {

  type MultiOp = Iterable[Any] => Any

  val Pos = UnOp("pos", OperatorPrecedence.Neg) {
    case x: Game => +x
    case x: SidedValue => +x
    case x: SurrealNumber => +x
  }

  val Neg = UnOp("neg", OperatorPrecedence.Neg) {
    case x: Game => -x
    case x: SidedValue => -x
    case x: SurrealNumber => -x
  }

  val PlusMinus = UnOp("+-", OperatorPrecedence.Neg) {
    case x: CanonicalShortGame => CanonicalShortGame(x)(-x)
    case x: CanonicalStopper => CanonicalStopper(x)(-x)
    case x: Set[_] if x forall { _.isInstanceOf[CanonicalShortGame] } =>
      CanonicalShortGame(x.asInstanceOf[Set[CanonicalShortGame]], x.asInstanceOf[Set[CanonicalShortGame]] map { -_ })
    case x: Set[_] if x forall { _.isInstanceOf[CanonicalStopper] } =>
      CanonicalStopper(x.asInstanceOf[Set[CanonicalStopper]], x.asInstanceOf[Set[CanonicalStopper]] map { -_ })
    case x: Game => ExplicitGame(x)(-x)
  }

  val Plus = CachingBinOp("+", OperatorPrecedence.Plus) {
    case (_: Game, _: Zero) => (x: Game, _: Zero) => x
    case (_: Zero, _: Game) => (_: Zero, y: Game) => y
    case (_: Integer, _: Integer) => (x: Integer, y: Integer) => x + y
    case (_: GeneralizedOrdinal, _: GeneralizedOrdinal) => (x: GeneralizedOrdinal, y: GeneralizedOrdinal) => x + y
    case (_: RationalNumber, _: RationalNumber) => (x: RationalNumber, y: RationalNumber) => x + y
    case (_: SurrealNumber, _: SurrealNumber) => (x: SurrealNumber, y: SurrealNumber) => x + y
    case (_: Uptimal, _: Uptimal) => (x: Uptimal, y: Uptimal) => x + y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x + y
    case (_: CanonicalStopper, _: CanonicalStopper) => (x: CanonicalStopper, y: CanonicalStopper) => x + y
    case (_: StopperSidedValue, _: StopperSidedValue) => (x: StopperSidedValue, y: StopperSidedValue) => x + y
    case (_: SidedValue, _: SidedValue) => (x: SidedValue, y: SidedValue) => x + y
    case (_: MisereCanonicalGame, _: MisereCanonicalGame) => (x: MisereCanonicalGame, y: MisereCanonicalGame) => x + y
    case (_: ImpartialGame, _: ImpartialGame) => (x: ImpartialGame, y: ImpartialGame) => x + y
    case (_: Game, _: Game) => (x: Game, y: Game) => x + y
    case (_: Coordinates, _: Coordinates) => (x: Coordinates, y: Coordinates) => x + y
    case (_: String, _: String) => (x: String, y: String) => x + y
    case (_: Output, _: String) => (x: Output, y: String) => outputSum(x, toOutput(y))
    case (_: String, _: Output) => (x: String, y: Output) => outputSum(toOutput(x), y)
    case (_: Output, _: Output) => (x: Output, y: Output) => outputSum(x, y)
  }

  def toOutput(str: String) = new StyledTextOutput(util.EnumSet.of(StyledTextOutput.Style.FACE_MATH), str)

  def outputSum(o1: Output, o2: Output) = {
    val result = new StyledTextOutput()
    result.appendOutput(o1)
    result.appendOutput(o2)
    result
  }

  val Minus = CachingBinOp("-", OperatorPrecedence.Plus) {
    case (_: Game, _:Zero) => (x: Game, _: Zero) => x
    case (_: Zero, _:Game) => (_: Zero, y: Game) => y
    case (_: Integer, _:Integer) => (x: Integer, y: Integer) => x - y
    case (_: GeneralizedOrdinal, _: GeneralizedOrdinal) => (x: GeneralizedOrdinal, y: GeneralizedOrdinal) => x - y
    case (_: RationalNumber, _:RationalNumber) => (x: RationalNumber, y: RationalNumber) => x - y
    case (_: SurrealNumber, _: SurrealNumber) => (x: SurrealNumber, y: SurrealNumber) => x - y
    case (_: Uptimal, _:Uptimal) => (x: Uptimal, y: Uptimal) => x - y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x - y
    case (_: CanonicalStopper, _: CanonicalStopper) => (x: CanonicalStopper, y: CanonicalStopper) => x - y
    case (_: StopperSidedValue, _: StopperSidedValue) => (x: StopperSidedValue, y: StopperSidedValue) => x - y
    case (_: SidedValue, _: SidedValue) => (x: SidedValue, y: SidedValue) => x - y
    case (_: Game, _:Game) => (x: Game, y: Game) => x - y
    case (_: Coordinates, _:Coordinates) => (x: Coordinates, y: Coordinates) => x - y
  }

  val OrdinalPlus = CachingBinOp(":", OperatorPrecedence.OrdinalSum) {
    case (_: Nimber, _: Nimber) => (x: Nimber, y: Nimber) => x ordinalSum y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x ordinalSum y
    case (_: CanonicalStopper, _: CanonicalStopper) => (x: CanonicalStopper, y: CanonicalStopper) => x ordinalSum y
    case (_: ImpartialGame, _: ImpartialGame) => (x: ImpartialGame, y: ImpartialGame) => CompoundImpartialGame(OrdinalSum, x, y)
    case (_: Game, _: Game) => (x: Game, y: Game) => CompoundGame(OrdinalSum, x, y)
  }

  val Times = CachingBinOp("*", OperatorPrecedence.Mult) {
    case (_: Zero, _: Game) => (_: Zero, _: Game) => zero
    case (_: Integer, _: Integer) => (x: Integer, y: Integer) => x * y
    case (_: GeneralizedOrdinal, _: GeneralizedOrdinal) => (x: GeneralizedOrdinal, y: GeneralizedOrdinal) => x * y
    case (_: RationalNumber, _: RationalNumber) => (x: RationalNumber, y: RationalNumber) => x * y
    case (_: SurrealNumber, _: SurrealNumber) => (x: SurrealNumber, y: SurrealNumber) => x * y
    case (_: Integer, _: SidedValue) => (x: Integer, y: SidedValue) => x * y
    case (_: SidedValue, _: Integer) => (x: SidedValue, y: Integer) => y * x
    case (_: Integer, _: Game) => (x: Integer, y: Game) => CompoundGame(ConwayProduct, x, y)
    case (_: Game, _: Integer) => (x: Game, y: Integer) => CompoundGame(ConwayProduct, x, y)
    case (_: Coordinates, _: Integer) => (x: Coordinates, y: Integer) => x * y
    case (_: Integer, _: Coordinates) => (x: Integer, y: Coordinates) => y * x
  }

  val Div = BinOp("/", OperatorPrecedence.Mult) {
    case (x: RationalNumber, y: RationalNumber) => x / y
    case (x: SurrealNumber, y: SurrealNumber) => x / y
  }

  val Mod = BinOp("%", OperatorPrecedence.Mult) {
    case (x: Integer, y: Integer) => x % y
    case (x: RationalNumber, y: RationalNumber) => x % y
  }

  val Exp = BinOp("^", OperatorPrecedence.Exp) {
    case (x: RationalNumber, y: Integer) => x pow y
    case (x: GeneralizedOrdinal, y: GeneralizedOrdinal) if x.isOmega => y.omegaPower
    case (x: SurrealNumber, y: Integer) => x pow y
  }

  val leq: PartialFunction[(Any, Any), Boolean] = {
    case (x: RationalNumber, y: RationalNumber) => x <= y
    case (x: SurrealNumber, y: SurrealNumber) => x <= y
    case (x: CanonicalShortGame, y: CanonicalShortGame) => x <= y
    case (x: CanonicalStopper, y: CanonicalStopper) => x <= y
    case (x: StopperSidedValue, y: StopperSidedValue) => x <= y
    case (x: SidedValue, y: SidedValue) => x <= y
    case (x: Coordinates, y: Coordinates) => x <= y
  }

  val Equals = BinOp("==", OperatorPrecedence.Relational) {
    case (x: StopperSidedValue, y: StopperSidedValue) => x == y
    case (x: Game, y: Game) => leq(x, y) && leq(y, x)
    case (x, y) => x == y
  }

  val Neq = BinOp("!=", OperatorPrecedence.Relational) {
    case (x: StopperSidedValue, y: StopperSidedValue) => x != y
    case (x: Game, y: Game) => !leq(x, y) || !leq(y, x)
    case (x, y) => x != y
  }

  val RefEquals = BinOp("===", OperatorPrecedence.Relational) { case (a, b) => a == b }
  val RefNeq = BinOp("!==", OperatorPrecedence.Relational) { case (a, b) => a != b }
  val Leq = BinOp("<=", OperatorPrecedence.Relational) { case (a, b) => leq(a, b) }
  val Geq = BinOp(">=", OperatorPrecedence.Relational) { case (a, b) => leq(b, a) }
  val Lt = BinOp("<", OperatorPrecedence.Relational) { case (a, b) => leq(a, b) && !leq(b, a) }
  val Gt = BinOp(">", OperatorPrecedence.Relational) { case (a, b) => !leq(a, b) && leq(b, a) }
  val Confused = BinOp("<>", OperatorPrecedence.Relational) { case (a, b) => !leq(a, b) && !leq(b, a) }
  val LConfused = BinOp("<|", OperatorPrecedence.Relational) { case (a, b) => !leq(b, a) }
  val GConfused = BinOp("|>", OperatorPrecedence.Relational) { case (a, b) => !leq(a, b) }
  val Compare = BinOp("<=>", OperatorPrecedence.Relational) { case (a, b) => (leq(a, b), leq(b, a)) match {
    case (true, true) => zero
    case (true, false) => negativeOne
    case (false, true) => one
    case (false, false) => star
  }}

  val Not = UnOp("not", OperatorPrecedence.Not) { case x: Boolean => !x }
  val And = BinOp("and", OperatorPrecedence.And) { case (x: Boolean, y: Boolean) => x && y }
  val Or = BinOp("or", OperatorPrecedence.Or) { case (x: Boolean, y: Boolean) => x || y }

  val Is = BinOp("is", OperatorPrecedence.Is) { case (x: Any, y: ClassObject) =>
    CgscriptClass.of(x).ancestors.contains(y.forClass)
  }

  val MakeNimber = UnOp("nim", OperatorPrecedence.Nim) {
    case x: SmallInteger => Nimber(x)
    case collection: Iterable[_] => MisereCanonicalGame(collection)
  }

  val MakeUpMultiple = BinOp("upstar", OperatorPrecedence.Nim) {
    case (x: SmallInteger, y: SmallInteger) => Uptimal(zero, x.intValue, y.intValue)
  }

  val MakeDownMultiple = BinOp("downstar", OperatorPrecedence.Nim) {
    case (x: SmallInteger, y: SmallInteger) => Uptimal(zero, -x.intValue, y.intValue)
  }

  val MakeSides = BinOp("&", OperatorPrecedence.Sidle) {
    case (x: CanonicalStopper, y: CanonicalStopper) => StopperSidedValue(x, y)
  }

  val MakeCoordinates = BinOp("(,)", OperatorPrecedence.Primary) {
    case (x: Integer, y: Integer) => Coordinates(x.intValue, y.intValue)
  }

  val Range = BinOp("..", OperatorPrecedence.Range) {
    case (x: Integer, y: Integer) => NumericRange.inclusive(x, y, Values.one)
    case (x: NumericRange[_], y: Integer) => x.asInstanceOf[NumericRange[Integer]] by y
  }

  val ArrayReference = BinOp("[]", OperatorPrecedence.Postfix) {
    case (seq: Seq[_], index: Integer) => seq(index.intValue-1)
    case (map: Map[Any @unchecked, _], key: Any) => map(key)
    case (grid: Grid, coord: Coordinates) => grid.get(coord)
    case (strip: Strip, index: Integer) => strip.get(index)
  }

}

trait UnOp {
  def name: String
  def precedence: Int
  def apply(tree: Tree, x: Any): Any
  def throwEvalException(tree: Tree, x: Any): Unit = {
    val xClass = CgscriptClass.of(x).qualifiedName
    throw EvalException(s"No operation `$name` for argument of type `$xClass`", tree)
  }
}

object UnOp {
  def apply(name: String, precedence: Int)(resolver: Any => Any) = new SimpleUnOp(name, precedence)(resolver)
}

class SimpleUnOp(val name: String, val precedence: Int)(resolver: Any => Any) extends UnOp {
  def apply(tree: Tree, x: Any): Any = {
    try {
      resolver(x)
    } catch {
      case err: MatchError => throwEvalException(tree, x)
    }
  }
}

class CachingUnOp(val name: String, val precedence: Int)(resolver: Any => _ => Any) extends UnOp {

  val classLookupCache = mutable.AnyRefMap[Class[_], Any => Any]()

  def apply(tree: Tree, x: Any): Any = {
    try {
      val fn = classLookupCache.getOrElseUpdate(x.getClass, resolver(x).asInstanceOf[Any => Any])
      fn(x)
    } catch {
      case err: MatchError => throwEvalException(tree, x)
    }
  }

}

trait BinOp {
  def name: String
  def precedence: Int
  def apply(tree: Tree, x: Any, y: Any): Any
  def throwEvalException(tree: Tree, x: Any, y: Any): Unit = {
    val xClass = CgscriptClass.of(x).qualifiedName
    val yClass = CgscriptClass.of(y).qualifiedName
    throw EvalException(s"No operation `$name` for arguments of types `$xClass`, `$yClass`", tree)
  }
}

object BinOp {
  def apply(name: String, precedence: Int)(resolver: PartialFunction[(Any, Any), Any]) = new SimpleBinOp(name, precedence)(resolver)
}

class SimpleBinOp(val name: String, val precedence: Int)(resolver: PartialFunction[(Any, Any), Any]) extends BinOp {
  override def apply(tree: Tree, x: Any, y: Any) = {
    try {
      resolver(x, y)
    } catch {
      case err: MatchError => throwEvalException(tree, x, y)
    }
  }
}

object CachingBinOp {
  def apply(name: String, precedence: Int)(resolver: PartialFunction[(Any, Any), (_, _) => Any]) = new CachingBinOp(name, precedence)(resolver)
}

class CachingBinOp(val name: String, val precedence: Int)(resolver: PartialFunction[(Any, Any), (_, _) => Any]) extends BinOp {

  val classLookupCache = mutable.AnyRefMap[(Class[_], Class[_]), (Any, Any) => Any]()

  override def apply(tree: Tree, x: Any, y: Any): Any = {
    try {
      val classPair: (Class[_], Class[_]) = (classOfOrNull(x), classOfOrNull(y))
      val fn = classLookupCache.getOrElseUpdate(classPair, resolver(x, y).asInstanceOf[(Any, Any) => Any])
      fn(x, y)
    } catch {
      case err: MatchError => throwEvalException(tree, x, y)
    }
  }

  private def classOfOrNull(x: Any): Class[_] = {
    if (x == null) classOf[Null] else x.getClass
  }

}

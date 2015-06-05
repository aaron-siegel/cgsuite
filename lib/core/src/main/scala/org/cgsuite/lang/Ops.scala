package org.cgsuite.lang

import java.util

import org.cgsuite.core._
import org.cgsuite.dsl.IntegerIsIntegral
import org.cgsuite.exception.InputException
import org.cgsuite.output.{StyledTextOutput, Output}
import org.cgsuite.util.{Strip, Grid, Coordinates}
import org.cgsuite.core.Values._
import scala.collection.immutable.NumericRange
import scala.collection.mutable

object Ops {

  type MultiOp = Iterable[Any] => Any

  val Pos = UnOp("pos") {
    case (x: Game) => +x
    case (x: RationalNumber) => +x
  }

  val Neg = UnOp("neg") {
    case (x: Game) => -x
    case (x: RationalNumber) => -x
  }

  val PlusMinus = UnOp("+-") {
    case (x: CanonicalShortGame) => CanonicalShortGame(x)(-x)
    case (x: CanonicalStopperGame) => CanonicalStopperGame(x)(-x)
    case (x: Set[_]) if x forall { _.isInstanceOf[CanonicalShortGame] } =>
      CanonicalShortGame(x.asInstanceOf[Set[CanonicalShortGame]], x.asInstanceOf[Set[CanonicalShortGame]] map { -_ })
    case (x: Set[_]) if x forall { _.isInstanceOf[CanonicalStopperGame] } =>
      CanonicalStopperGame(x.asInstanceOf[Set[CanonicalStopperGame]], x.asInstanceOf[Set[CanonicalStopperGame]] map { -_ })
    case (x: Game) => ExplicitGame(x)(-x)
  }

  val Plus = CachingBinOp("+") {
    case (_: Game, _: Zero) => (x: Game, _: Zero) => x
    case (_: Zero, _: Game) => (_: Zero, y: Game) => y
    case (_: Integer, _: Integer) => (x: Integer, y: Integer) => x + y
    case (_: RationalNumber, _: RationalNumber) => (x: RationalNumber, y: RationalNumber) => x + y
    case (_: Uptimal, _: Uptimal) => (x: Uptimal, y: Uptimal) => x + y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x + y
    case (_: CanonicalStopperGame, _: CanonicalStopperGame) => (x: CanonicalStopperGame, y: CanonicalStopperGame) => x + y
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

  val Minus = CachingBinOp("-") {
    case (_: Game, _:Zero) => (x: Game, _: Zero) => x
    case (_: Zero, _:Game) => (_: Zero, y: Game) => y
    case (_: Integer, _:Integer) => (x: Integer, y: Integer) => x - y
    case (_: RationalNumber, _:RationalNumber) => (x: RationalNumber, y: RationalNumber) => x - y
    case (_: Uptimal, _:Uptimal) => (x: Uptimal, y: Uptimal) => x - y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x - y
    case (_: CanonicalStopperGame, _: CanonicalStopperGame) => (x: CanonicalStopperGame, y: CanonicalStopperGame) => x - y
    case (_: Game, _:Game) => (x: Game, y: Game) => x - y
    case (_: Coordinates, _:Coordinates) => (x: Coordinates, y: Coordinates) => x - y
  }

  val OrdinalPlus = CachingBinOp(":") {
    case (_: Nimber, _: Nimber) => (x: Nimber, y: Nimber) => x ordinalSum y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x ordinalSum y
    case (_: CanonicalStopperGame, _: CanonicalStopperGame) => (x: CanonicalStopperGame, y: CanonicalStopperGame) => x ordinalSum y
  }

  val Times = CachingBinOp("*") {
    case (_: Zero, _:Game) => (_: Zero, _: Game) => zero
    case (_: Integer, _:Integer) => (x: Integer, y: Integer) => x * y
    case (_: RationalNumber, _:RationalNumber) => (x: RationalNumber, y: RationalNumber) => x * y
    case (_: Integer, _:Game) => (x: Integer, y: Game) => x * y
    case (_: Coordinates, _:Integer) => (x: Coordinates, y: Integer) => x * y
    case (_: Integer, _:Coordinates) => (x: Integer, y: Coordinates) => y * x
  }

  val Div = BinOp("/") {
    case (x: RationalNumber, y: RationalNumber) => x / y
  }

  val Mod = BinOp("%") {
    case (x: Integer, y: Integer) => x % y
    case (x: RationalNumber, y: RationalNumber) => x % y
  }

  val Exp = BinOp("^") {
    case (x: RationalNumber, y: Integer) => x.pow(y)
  }

  val leq: (Any, Any) => Boolean = {
    case (x: RationalNumber, y: RationalNumber) => x <= y
    case (x: CanonicalShortGame, y: CanonicalShortGame) => x <= y
    case (x: CanonicalStopperGame, y: CanonicalStopperGame) => x <= y
  }

  val Equals = BinOp("==") {
    case (x: CanonicalStopperSidedGame, y: CanonicalStopperSidedGame) => x == y
    case (x: Game, y: Game) => leq(x, y) && leq(y, x)
    case (x, y) => x == y
  }

  val Neq = BinOp("!=") {
    case (x: CanonicalStopperSidedGame, y: CanonicalStopperSidedGame) => x != y
    case (x: Game, y: Game) => !leq(x, y) || !leq(y, x)
    case (x, y) => x != y
  }

  val RefEquals = BinOp("===") { _ == _ }
  val RefNeq = BinOp("!==") { _ != _ }
  val Leq = BinOp("<=") { (a, b) => leq(a, b) }
  val Geq = BinOp(">=") { (a, b) => leq(b, a) }
  val Lt = BinOp("<") { (a, b) => leq(a, b) && !leq(b, a) }
  val Gt = BinOp(">") { (a, b) => !leq(a, b) && leq(b, a) }
  val Confused = BinOp("<>") { (a, b) => !leq(a, b) && !leq(b, a) }
  val LConfused = BinOp("<|") { (a, b) => !leq(b, a) }
  val GConfused = BinOp("|>") { (a, b) => !leq(a, b) }
  val Compare = BinOp("<=>") { (a, b) => (leq(a, b), leq(b, a)) match {
    case (true, true) => zero
    case (true, false) => negativeOne
    case (false, true) => one
    case (false, false) => star
  }}

  val Not = UnOp("not") { case x: Boolean => !x }
  val And = BinOp("and") { case (x: Boolean, y: Boolean) => x && y }
  val Or = BinOp("or") { case (x: Boolean, y: Boolean) => x || y }

  val Is = BinOp("is") { case (x: Any, y: ClassObject) =>
    CgscriptClass.of(x).ancestors.contains(y.forClass)
  }

  val MakeNimber = UnOp("nim") {
    case x: SmallInteger => Nimber(x.intValue)
  }

  val MakeUpMultiple = BinOp("up") {
    case (x: SmallInteger, y: SmallInteger) => Uptimal(zero, x.intValue, y.intValue)
  }

  val MakeDownMultiple = BinOp("down") {
    case (x: SmallInteger, y: SmallInteger) => Uptimal(zero, -x.intValue, y.intValue)
  }

  val MakeCoordinates = BinOp("(,)") {
    case (x: Integer, y: Integer) => Coordinates(x.intValue, y.intValue)
  }

  val Range = BinOp("..") {
    case (x: Integer, y: Integer) => NumericRange.inclusive(x, y, Values.one)
    case (x: NumericRange[_], y: Integer) => x.asInstanceOf[NumericRange[Integer]] by y
  }

  val ArrayReference = BinOp("[]") {
    case (seq: Seq[_], index: Integer) => seq(index.intValue-1)
    case (grid: Grid, coord: Coordinates) => grid.get(coord)
    case (strip: Strip, index: Integer) => strip.get(index)
  }

}

trait UnOp {
  def name: String
  def apply(x: Any): Any
}

object UnOp {
  def apply(name: String)(resolver: Any => Any) = new SimpleUnOp(name)(resolver)
}

class SimpleUnOp(val name: String)(resolver: Any => Any) extends UnOp {
  def apply(x: Any): Any = resolver(x)
}

class CachingUnOp(val name: String)(resolver: Any => _ => Any) extends UnOp {

  val classLookupCache = mutable.AnyRefMap[Class[_], Any => Any]()

  def apply(x: Any): Any = {
    val fn = classLookupCache.getOrElseUpdate(x.getClass, resolver(x).asInstanceOf[Any => Any])
    fn(x)
  }

}

trait BinOp {
  def name: String
  def apply(x: Any, y: Any): Any
  def throwInputException(x: Any, y: Any): Unit = {
    val xClass = CgscriptClass.of(x).qualifiedName
    val yClass = CgscriptClass.of(y).qualifiedName
    throw InputException(s"No operation `$name` for arguments of types `$xClass`, `$yClass`")
  }
}

object BinOp {
  def apply(name: String)(resolver: (Any, Any) => Any) = new SimpleBinOp(name)(resolver)
}

class SimpleBinOp(val name: String)(resolver: (Any, Any) => Any) extends BinOp {
  def apply(x: Any, y: Any) = {
    try {
      resolver(x, y)
    } catch {
      case err: MatchError => throwInputException(x, y)
    }
  }
}

object CachingBinOp {
  def apply(name: String)(resolver: (Any, Any) => (_, _) => Any) = new CachingBinOp(name)(resolver)
}

class CachingBinOp(val name: String)(resolver: (Any, Any) => (_, _) => Any) extends BinOp {

  val classLookupCache = mutable.AnyRefMap[(Class[_], Class[_]), (Any, Any) => Any]()

  def apply(x: Any, y: Any): Any = {
    try {
      val fn = classLookupCache.getOrElseUpdate((x.getClass, y.getClass), resolver(x, y).asInstanceOf[(Any, Any) => Any])
      fn(x, y)
    } catch {
      case err: MatchError => throwInputException(x, y)
    }
  }

}

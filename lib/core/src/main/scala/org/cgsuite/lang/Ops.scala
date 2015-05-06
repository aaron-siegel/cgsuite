package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.util.{Grid, Coordinates}
import org.cgsuite.core.Values._
import scala.collection.mutable

object Ops {

  type MultiOp = Iterable[Any] => Any

  val Pos = UnOp("pos") {
    case (x: Game) => +x
  }

  val Neg = UnOp("neg") {
    case (x: Game) => -x
  }

  val PlusMinus = UnOp("+-") {
    case (x: CanonicalShortGame) => CanonicalShortGame(x)(-x)
    case (x: Game) => ExplicitGame(x)(-x)
  }

  val NewPlus = CachingBinOp("+") {
    case (_: Game, _: Zero) => (x: Game, _: Zero) => x
    case (_: Zero, _: Game) => (_: Zero, y: Game) => y
    case (_: Integer, _: Integer) => (x: Integer, y: Integer) => x + y
    case (_: RationalNumber, _: RationalNumber) => (x: RationalNumber, y: RationalNumber) => x + y
    case (_: NumberUpStar, _: NumberUpStar) => (x: NumberUpStar, y: NumberUpStar) => x + y
    case (_: CanonicalShortGame, _: CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x + y
    case (_: Game, _: Game) => (x: Game, y: Game) => x + y
    case (_: Coordinates, _: Coordinates) => (x: Coordinates, y: Coordinates) => x + y
    case (_: String, _) => (x: String, y: Any) => x + y.toString
  }

  val Minus = CachingBinOp("-") {
    case (_: Game, _:Zero) => (x: Game, _: Zero) => x
    case (_: Zero, _:Game) => (_: Zero, y: Game) => y
    case (_: Integer, _:Integer) => (x: Integer, y: Integer) => x - y
    case (_: RationalNumber, _:RationalNumber) => (x: RationalNumber, y: RationalNumber) => x - y
    case (_: NumberUpStar, _:NumberUpStar) => (x: NumberUpStar, y: NumberUpStar) => x - y
    case (_: CanonicalShortGame, _:CanonicalShortGame) => (x: CanonicalShortGame, y: CanonicalShortGame) => x - y
    case (_: Game, _:Game) => (x: Game, y: Game) => x - y
    case (_: Coordinates, _:Coordinates) => (x: Coordinates, y: Coordinates) => x - y
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
  val LConfused = BinOp("<|") { (a, b) => !leq(b, a) }
  val GConfused = BinOp("|>") { (a, b) => !leq(a, b) }
  val Compare = BinOp("<>") { (a, b) => (leq(a, b), leq(b, a)) match {
    case (true, true) => zero
    case (true, false) => negativeOne
    case (false, true) => one
    case (false, false) => star
  }}

  val Not = UnOp("not") { case x: Boolean => !x }
  val And = BinOp("and") { case (x: Boolean, y: Boolean) => x && y }
  val Or = BinOp("or") { case (x: Boolean, y: Boolean) => x || y }

  val Is = BinOp("is") { case (x: Any, y: ClassObject) =>
    CgsuiteClass.of(x).ancestors.contains(y.forClass)
  }

  val MakeNimber = UnOp("nim") {
    case x: SmallInteger => Nimber(x.intValue)
  }

  val MakeUpMultiple = BinOp("up") {
    case (x: SmallInteger, y: SmallInteger) => NumberUpStar(zero, x.intValue, y.intValue)
  }

  val MakeDownMultiple = BinOp("down") {
    case (x: SmallInteger, y: SmallInteger) => NumberUpStar(zero, -x.intValue, y.intValue)
  }

  val MakeCoordinates = BinOp("(,)") {
    case (x: Integer, y: Integer) => Coordinates(x.intValue, y.intValue)
  }

  val MakeList: MultiOp = { _.toSeq }
  val MakeSet: MultiOp = { _.toSet }
  val MakeMap: MultiOp = { _.asInstanceOf[Iterable[(_, _)]].toMap }

  val ArrayReference = BinOp("[]") {
    case (grid: Grid, coord: Coordinates) => grid.get(coord)
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
}

object BinOp {
  def apply(name: String)(resolver: (Any, Any) => Any) = new SimpleBinOp(name)(resolver)
}

class SimpleBinOp(val name: String)(resolver: (Any, Any) => Any) extends BinOp {
  def apply(x: Any, y: Any) = resolver(x, y)
}

object CachingBinOp {
  def apply(name: String)(resolver: (Any, Any) => (_, _) => Any) = new CachingBinOp(name)(resolver)
}

class CachingBinOp(val name: String)(resolver: (Any, Any) => (_, _) => Any) extends BinOp {

  val classLookupCache = mutable.AnyRefMap[(Class[_], Class[_]), (Any, Any) => Any]()

  def apply(x: Any, y: Any): Any = {
    val fn = classLookupCache.getOrElseUpdate((x.getClass, y.getClass), resolver(x, y).asInstanceOf[(Any, Any) => Any])
    fn(x, y)
  }

}
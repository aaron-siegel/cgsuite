package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.util.{Grid, Coordinates}
import org.cgsuite.core.Values._
import scala.collection.mutable

object Ops {

  type UnOp = Any => Any
  type BinOp = (Any, Any) => Any
  type MultiOp = Iterable[Any] => Any

  val Pos: UnOp = {
    case (x: Game) => +x
  }

  val Neg: UnOp = {
    case (x: Game) => -x
  }

  val PlusMinus: UnOp = {
    case (x: CanonicalShortGame) => CanonicalShortGame(x)(-x)
    case (x: Game) => ExplicitGame(x)(-x)
  }

  val Plus: BinOp = {
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

  val NewPlus: NewBinOp = NewBinOp("+") {
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

  val Minus: BinOp = {
    case (x: Game, _: Zero) => x
    case (_: Zero, y: Game) => y
    case (x: Integer, y: Integer) => x - y
    case (x: RationalNumber, y: RationalNumber) => x - y
    case (x: NumberUpStar, y: NumberUpStar) => x - y
    case (x: CanonicalShortGame, y: CanonicalShortGame) => x - y
    case (x: Game, y: Game) => x - y
    case (x: Coordinates, y: Coordinates) => x - y
  }

  val Times: BinOp = {
    case (_: Zero, _: Game) => zero
    case (x: Integer, y: Integer) => x * y
    case (x: RationalNumber, y: RationalNumber) => x * y
    case (x: Integer, y: Game) => x * y
    case (x: Coordinates, y: Integer) => x * y
    case (x: Integer, y: Coordinates) => y * x
  }

  val Div: BinOp = {
    case (x: RationalNumber, y: RationalNumber) => x / y
  }

  val Mod: BinOp = {
    case (x: Integer, y: Integer) => x % y
    case (x: RationalNumber, y: RationalNumber) => x % y
  }

  val Exp: BinOp = {
    case (x: RationalNumber, y: Integer) => x.pow(y)
  }

  val Equals: BinOp = {
    case (x: CanonicalStopperSidedGame, y: CanonicalStopperSidedGame) => x == y
    case (x: Game, y: Game) => Leq(x, y) && Leq(y, x)
    case (x, y) => x == y
  }

  val Neq: BinOp = {
    case (x: CanonicalStopperSidedGame, y: CanonicalStopperSidedGame) => x != y
    case (x: Game, y: Game) => !Leq(x, y) || !Leq(y, x)
    case (x, y) => x != y
  }

  val RefEquals: BinOp = { _ == _ }
  val RefNeq: BinOp = { _ != _ }
  val Leq: (Any, Any) => Boolean = {
    case (x: RationalNumber, y: RationalNumber) => x <= y
    case (x: CanonicalShortGame, y: CanonicalShortGame) => x <= y
  }
  val Geq: BinOp = { (a, b) => Leq(b, a) }
  val Lt: BinOp = { (a, b) => Leq(a, b) && !Leq(b, a) }
  val Gt: BinOp = { (a, b) => !Leq(a, b) && Leq(b, a) }
  val LConfused: BinOp = { (a, b) => !Leq(b, a) }
  val GConfused: BinOp = { (a, b) => !Leq(a, b) }
  val Compare: BinOp = { (a, b) => (Leq(a, b), Leq(b, a)) match {
    case (true, true) => zero
    case (true, false) => negativeOne
    case (false, true) => one
    case (false, false) => star
  }}

  val Not: UnOp = { case x: Boolean => !x }
  val And: BinOp = { case (x: Boolean, y: Boolean) => x && y }
  val Or: BinOp = { case (x: Boolean, y: Boolean) => x || y }

  val Is: BinOp = { case (x: Any, y: ClassObject) =>
    CgsuiteClass.of(x).ancestors.contains(y.forClass)
  }

  val MakeNimber: UnOp = {
    case x: SmallInteger => Nimber(x.intValue)
  }
  val MakeUpMultiple: BinOp = {
    case (x: SmallInteger, y: SmallInteger) => NumberUpStar(zero, x.intValue, y.intValue)
  }

  val MakeDownMultiple: BinOp = {
    case (x: SmallInteger, y: SmallInteger) => NumberUpStar(zero, -x.intValue, y.intValue)
  }

  val MakeCoordinates: BinOp = {
    case (x: Integer, y: Integer) => Coordinates(x.intValue, y.intValue)
  }

  val MakeList: MultiOp = { _.toSeq }
  val MakeSet: MultiOp = { _.toSet }
  val MakeMap: MultiOp = { _.asInstanceOf[Iterable[(_, _)]].toMap }

  val ArrayReference: BinOp = {
    case (grid: Grid, coord: Coordinates) => grid.get(coord)
  }

}

object NewBinOp {

  def apply(name: String)(resolver: (Any, Any) => (_, _) => Any) = new NewBinOp(name)(resolver)

}

class NewBinOp(name: String)(resolver: (Any, Any) => (_, _) => Any) {

  val classLookupCache = mutable.Map[(Class[_], Class[_]), (Any, Any) => Any]()

  def apply(x: Any, y: Any): Any = {
    val fn = classLookupCache.getOrElseUpdate((x.getClass, y.getClass), resolver(x, y).asInstanceOf[(Any, Any) => Any])
    fn(x, y)
  }

}
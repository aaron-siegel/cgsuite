package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.exception.EvalException
import org.cgsuite.output.{EmptyOutput, Output, OutputTarget, StyledTextOutput}

object CgscriptImplicits extends LowPriorityCgscriptImplicits {

  // TODO Intelligent type conversion with good error messages

  implicit def sidedValueToStopperSidedValue(x: SidedValue): StopperSidedValue = {
    castSafely(x, "game.SidedValue", "game.StopperSidedValue") { _.asInstanceOf[StopperSidedValue] }
  }

  implicit def canonicalShortGameToUptimal(x: CanonicalShortGame): Uptimal = {
    castSafely(x, "game.CanonicalShortGame", "game.Uptimal") { _.asInstanceOf[Uptimal] }
  }

  implicit def rationalToDyadicRational(x: RationalNumber): DyadicRationalNumber = x.asInstanceOf[DyadicRationalNumber]

  implicit def surrealNumberToGeneralizedOrdinal(x: SurrealNumber): GeneralizedOrdinal = x.asInstanceOf[GeneralizedOrdinal]

  implicit def integerToInt(x: Integer): Int = x.intValue

  implicit def universalOrdering[T]: Ordering[T] = UniversalOrdering.asInstanceOf[Ordering[T]]

  implicit def unitToRichUnit(unit: Unit): RichUnit.type = RichUnit

  implicit def collectionToRichCollection[T](collection: Iterable[T]): RichCollection[T] = RichCollection(collection)

  implicit def listToRichList[T](list: IndexedSeq[T]): RichList[T] = RichList(list)

  // TODO Output enrichment wouldn't be necessary if Output were recoded in scala
  implicit def outputToRichOutput(output: Output): RichOutput = RichOutput(output)

  implicit def procedureToFunction1[T, R](procedure: Procedure[T, R]): T => R = procedure.fn

  implicit def procedureToFunction2[T1, T2, R](procedure: Procedure[(T1, T2), R]): (T1, T2) => R = {
    (x1, x2) => procedure.fn((x1, x2))
  }

  implicit def procedureToFunction3[T1, T2, T3, R](procedure: Procedure[(T1, T2, T3), R]): (T1, T2, T3) => R = {
    (x1, x2, x3) => procedure.fn((x1, x2, x3))
  }

}

trait LowPriorityCgscriptImplicits extends LowestPriorityCgscriptImplicits {

  implicit def rationalToInteger(x: RationalNumber): Integer = x.asInstanceOf[Integer]

  implicit def sidedValueToCanonicalStopper(x: SidedValue): CanonicalStopper = x.asInstanceOf[CanonicalStopper]

  //implicit def canonicalShortGameToDyadicRational(x: CanonicalShortGame): DyadicRationalNumber = x.asInstanceOf[DyadicRationalNumber]

}

trait LowestPriorityCgscriptImplicits {

  implicit def sidedValueToPseudonumber(x: SidedValue): Pseudonumber = x.asInstanceOf[Pseudonumber]

  def castSafely[T, U](x: T, tType: String, uType: String)(cast: T => U): U = {
    try {
      cast(x)
    } catch {
      case _: java.lang.ClassCastException =>
        throw EvalException(s"That `$tType` is not of type `$uType`.")
    }
  }

  implicit def anyToRichAny(x: Any): RichAny = RichAny(x)

  //implicit def canonicalShortGameToInteger(x: CanonicalShortGame): Integer = x.asInstanceOf[Integer]

}

case class RichCollection[T](collection: Iterable[T]) {

  def toNimber: MisereCanonicalGame = MisereCanonicalGame(collection)

}

case class RichList[T](list: IndexedSeq[T]) {

  def _lookup(index: org.cgsuite.core.Integer): T = {
    list(index.intValue - 1)
  }

  def mkOutput(sep: String, parens: String = ""): StyledTextOutput = {
    val output = new StyledTextOutput
    if (parens.length >= 1)
      output appendMath parens.substring(0, 1)
    var first = true
    list foreach { x =>
      if (!first)
        output appendMath sep
      first = false
      output append CgscriptClass.instanceToOutput(x)
    }
    if (parens.length >= 2)
      output appendMath parens.substring(1, 2)
    output
  }

}

case class RichMap[K, V](map: Map[K, V]) {

  def _lookup(key: K) = map(key)

}

object RichUnit {

  def toOutput = EmptyOutput

}

case class RichOutput(output: Output) {

  def +(that: Output) = {
    val outputSum = new StyledTextOutput
    outputSum.append(output)
    outputSum.append(that)
    outputSum
  }

}

case class RichAny(any: Any) {

  def toOutput = {
    any match {
      case ot: OutputTarget => ot.toOutput
      case _ => new StyledTextOutput(any.toString)
    }
  }

}

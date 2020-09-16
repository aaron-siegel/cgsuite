package org.cgsuite.lang2

import org.cgsuite.core._
import org.cgsuite.lang.CgscriptClass
import org.cgsuite.output.{EmptyOutput, StyledTextOutput}

object CgscriptImplicits extends LowPriorityCgscriptImplicits {

  // TODO Intelligent type conversion with good error messages

  implicit def rationalToDyadicRational(x: RationalNumber): DyadicRationalNumber = x.asInstanceOf[DyadicRationalNumber]

  implicit def surrealNumberToGeneralizedOrdinal(x: SurrealNumber): GeneralizedOrdinal = x.asInstanceOf[GeneralizedOrdinal]

  implicit def integerToInt(x: Integer): Int = x.intValue

  implicit def universalOrdering[T]: Ordering[T] = UniversalOrdering.asInstanceOf[Ordering[T]]

  implicit def unitToRichUnit(unit: Unit): RichUnit.type = RichUnit

  implicit def listToRichList[T](list: IndexedSeq[T]): RichList[T] = RichList(list)

  implicit def procedureToFunction1[T, R](procedure: Procedure[T, R]): T => R = procedure.fn

  implicit def procedureToFunction2[T1, T2, R](procedure: Procedure[(T1, T2), R]): (T1, T2) => R = {
    (x1, x2) => procedure.fn((x1, x2))
  }

  implicit def procedureToFunction3[T1, T2, T3, R](procedure: Procedure[(T1, T2, T3), R]): (T1, T2, T3) => R = {
    (x1, x2, x3) => procedure.fn((x1, x2, x3))
  }

}

trait LowPriorityCgscriptImplicits {

  implicit def rationalToInteger(x: RationalNumber): Integer = x.asInstanceOf[Integer]

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

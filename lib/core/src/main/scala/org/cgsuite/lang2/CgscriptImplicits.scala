package org.cgsuite.lang2

import org.cgsuite.core._
import org.cgsuite.lang.CgscriptClass
import org.cgsuite.output.StyledTextOutput

object CgscriptImplicits extends LowPriorityCgscriptImplicits {

  // TODO Intelligent type conversion with good error messages

  implicit def rationalToDyadicRational(x: RationalNumber): DyadicRationalNumber = x.asInstanceOf[DyadicRationalNumber]

  implicit def surrealNumberToGeneralizedOrdinal(x: SurrealNumber): GeneralizedOrdinal = x.asInstanceOf[GeneralizedOrdinal]

  implicit def integerToInt(x: Integer): Int = x.intValue

  implicit def universalOrdering[T]: Ordering[T] = UniversalOrdering.asInstanceOf[Ordering[T]]

}

trait LowPriorityCgscriptImplicits {

  implicit def rationalToInteger(x: RationalNumber): Integer = x.asInstanceOf[Integer]

}

case class RichList[T](list: IndexedSeq[T]) {

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

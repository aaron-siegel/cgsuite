package org.cgsuite.lang2

import org.cgsuite.core._

object CgscriptImplicits extends LowPriorityCgscriptImplicits {

  // TODO Intelligent type conversion with good error messages

  implicit def rationalToDyadicRational(x: RationalNumber): DyadicRationalNumber = x.asInstanceOf[DyadicRationalNumber]

  implicit def surrealNumberToGeneralizedOrdinal(x: SurrealNumber): GeneralizedOrdinal = x.asInstanceOf[GeneralizedOrdinal]

}

trait LowPriorityCgscriptImplicits {

  implicit def rationalToInteger(x: RationalNumber): org.cgsuite.core.Integer = x.asInstanceOf[org.cgsuite.core.Integer]

}

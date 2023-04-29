package org.cgsuite.core.impartial.arithmetic

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class NimFieldCalculatorTest extends AnyFlatSpec with Matchers {

  def checkCalculations(index: Int): Unit = {
    val p = NimFieldConstants.primes(index)
    NimFieldCalculator.qSet(p) shouldBe NimFieldConstants.qSet(index)
    NimFieldCalculator.excess(p) shouldBe NimFieldConstants.excess(index)
  }

  "NimFieldCalculator" should "compute excess and Q-sets correctly for p <= 43" in {
    0 to NimFieldConstants.primes.indexOf(43) foreach checkCalculations
  }

  it should "compute excess and Q-set correctly for p = 229" in {
    // (This is an unusual case because it has a component, alpha_19, for
    // which the excess is > 1. The case p = 191 also has this property, but
    // takes longer to compute.)
    NimFieldCalculator.preloadTo(227)
    checkCalculations(NimFieldConstants.primes.indexOf(229))
  }

}

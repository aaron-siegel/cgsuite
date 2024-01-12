package org.cgsuite.util

import org.cgsuite.core.Integer
import org.cgsuite.core.Values.one
import org.cgsuite.exception.EvalException

class Random(val seed: Integer) {

  private val random = new scala.util.Random(seed.longValue)

  def nextInteger(upperBound: Integer): Integer = {
    if (upperBound < one) {
      throw EvalException("Upper bound for `NextInteger` must be >= 1.")
    }
    val bits = (upperBound - one).lb.intValue + 1
    var result: Integer = Integer(BigInt(bits, random))
    while (result >= upperBound) {
      result = Integer(BigInt(bits, random))
    }
    result
  }

}

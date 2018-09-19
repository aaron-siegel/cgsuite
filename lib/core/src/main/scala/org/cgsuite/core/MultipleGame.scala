package org.cgsuite.core

object MultipleGame {

  def binarySum[T](n: Int, t: T, zero: T)(op: (T, T) => T): T = {

    assert(n >= 0)
    var ctr = n
    var result = zero
    var square = t

    while (ctr != 0) {
      if ((ctr & 1) != 0)
        result = op(result, square)
      ctr >>= 1
      if (ctr != 0)
        square = op(square, square)
    }

    result

  }

}

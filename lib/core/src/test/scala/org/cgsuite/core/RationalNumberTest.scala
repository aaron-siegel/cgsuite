/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.exception.ArithmeticException
import org.scalatest.{FlatSpec, Matchers}
  
class RationalNumberTest extends FlatSpec with Matchers {
  
  "RationalNumber" should "construct objects of the correct class" in {
      
    RationalNumber(Integer(0), Integer(17)) shouldBe ZeroImpl
    RationalNumber(Integer(5), Integer(1)).getClass shouldBe classOf[SmallIntegerImpl]
    RationalNumber(Integer(1L << 37), Integer(1 << 10)).getClass shouldBe classOf[SmallIntegerImpl]
    RationalNumber(Integer(1), Integer(1 << 10)).getClass shouldBe classOf[DyadicRationalNumberImpl]
    RationalNumber(Integer(1L << 37), Integer(3)).getClass shouldBe classOf[RationalNumberImpl]
    RationalNumber(Integer(-1), Integer(0)).getClass shouldBe classOf[RationalNumberImpl]
    the [ArithmeticException] thrownBy RationalNumber(Integer(0), Integer(0)) should have message "/ by zero"

  }

  it should "should simplify correctly to lowest terms" in {

    val numden = { (x: Int, y: Int) =>
      val r = RationalNumber(Integer(x), Integer(y))
      (r.numerator.bigIntValue.intValue(), r.denominator.bigIntValue.intValue())
    }

    numden(0, 135813) shouldBe (0, 1)
    numden(0, -124814) shouldBe (0, 1)

    numden(10810368, 13056) shouldBe (828, 1)
    numden(-10810368, 13056) shouldBe (-828, 1)
    numden(-10810368, -13056) shouldBe (828, 1)
    numden(10810368, -13056) shouldBe (-828, 1)

    numden(10810368, 65280) shouldBe (828, 5)
    numden(-10810368, 65280) shouldBe (-828, 5)
    numden(-10810368, -65280) shouldBe (828, 5)
    numden(10810368, -65280) shouldBe (-828, 5)

    numden(14212, 0) shouldBe (1, 0)
    numden(-12344, 0) shouldBe (-1, 0)

  }

  it should "should give correct options" in {

    DyadicRationalNumber(1, 2).options(Left) shouldBe Set(Integer(0))
    DyadicRationalNumber(1, 2).options(Right) shouldBe Set(Integer(1))
    DyadicRationalNumber(-75, 256).options(Left) shouldBe Set(RationalNumber(-19, 64))
    DyadicRationalNumber(-75, 256).options(Right) shouldBe Set(RationalNumber(-37, 128))

  }
  
}

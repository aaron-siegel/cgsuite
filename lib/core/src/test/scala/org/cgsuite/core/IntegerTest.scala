/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.scalatest.{FlatSpec, FunSpec, Matchers}
  
class IntegerTest extends FlatSpec with Matchers {
  
  "Integer" should "construct objects of the correct class" in {
    
    Integer(0) shouldBe ZeroImpl
    Integer(1).getClass shouldBe classOf[SmallIntegerImpl]
    Integer(-1L << 31).getClass shouldBe classOf[SmallIntegerImpl]
    Integer(1L << 31).getClass shouldBe classOf[IntegerImpl]

  }

  it should "should give correct answers to basic operations" in {

    Integer(5) + Integer(7) shouldBe Integer(12)
    Integer(5) * Integer(7) shouldBe Integer(35)
    val big = Integer(1L << 37)
    big * Integer(2) shouldBe Integer(1L << 38)
    big / Integer(1 << 10) shouldBe Integer(1 << 27)
    Integer(35) div Integer(6) shouldBe Integer(5)

  }

  it should "should give correct results for gcd" in {

    Integer(35) gcd Integer(25) shouldBe Integer(5)
    Integer(35) gcd Integer(-25) shouldBe Integer(5)
    Integer(-35) gcd Integer(-25) shouldBe Integer(5)
    Integer(-35) gcd Integer(25) shouldBe Integer(5)

    Integer((1L << 40) * 729) gcd Integer(60) shouldBe Integer(12)
    Integer(-(1L << 40) * 729) gcd Integer(60) shouldBe Integer(12)
    Integer(-(1L << 40) * 729) gcd Integer(-60) shouldBe Integer(12)
    Integer((1L << 40) * 729) gcd Integer(-60) shouldBe Integer(12)

  }

  it should "should simplify to the correct class" in {

    val one = Integer(1)
    val x = Integer(1L << 31)

    (x - one).getClass shouldBe classOf[SmallIntegerImpl]
    (x - x) shouldBe ZeroImpl
    (one - one) shouldBe ZeroImpl

  }
  
}

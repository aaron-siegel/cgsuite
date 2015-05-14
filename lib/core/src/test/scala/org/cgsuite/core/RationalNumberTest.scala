/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._
  
@RunWith(classOf[JUnitRunner])
class RationalNumberTest extends Specification {
  
  "RationalNumber" should {
    
    "construct objects of the correct class" in {
      
      RationalNumber(Integer(0), Integer(17)) must_== ZeroImpl
      RationalNumber(Integer(5), Integer(1)).getClass must_== classOf[SmallIntegerImpl]
      RationalNumber(Integer(1L << 37), Integer(1 << 10)).getClass must_== classOf[SmallIntegerImpl]
      RationalNumber(Integer(1), Integer(1 << 10)).getClass must_== classOf[DyadicRationalNumberImpl]
      RationalNumber(Integer(1L << 37), Integer(3)).getClass must_== classOf[RationalNumberImpl]
      RationalNumber(Integer(-1), Integer(0)).getClass must_== classOf[RationalNumberImpl]
      RationalNumber(Integer(0), Integer(0)).getClass must_== classOf[RationalNumberImpl]
      
    }
    
    "simplify correctly to lowest terms" in {
      
      val numden = { (x: Int, y: Int) =>
        val r = RationalNumber(Integer(x), Integer(y))
        (r.numerator.bigIntValue.intValue, r.denominator.bigIntValue.intValue)
      }
      
      numden(0, 135813) must_== (0, 1)
      numden(0, -124814) must_== (0, 1)
      
      numden(10810368, 13056) must_== (828, 1)
      numden(-10810368, 13056) must_== (-828, 1)
      numden(-10810368, -13056) must_== (828, 1)
      numden(10810368, -13056) must_== (-828, 1)
      
      numden(10810368, 65280) must_== (828, 5)
      numden(-10810368, 65280) must_== (-828, 5)
      numden(-10810368, -65280) must_== (828, 5)
      numden(10810368, -65280) must_== (-828, 5)
      
      numden(14212, 0) must_== (1, 0)
      numden(-12344, 0) must_== (-1, 0)
      numden(0, 0) must_== (0, 0)
      
    }
    
    "give correct options" in {
      
      DyadicRationalNumber(1, 2).options(Left) must_== Iterable(Integer(0))
      DyadicRationalNumber(1, 2).options(Right) must_== Iterable(Integer(1))
      DyadicRationalNumber(-75, 256).options(Left) must_== Iterable(RationalNumber(-19, 64))
      DyadicRationalNumber(-75, 256).options(Right) must_== Iterable(RationalNumber(-37, 128))
      DyadicRationalNumber(1, 7).options(Left) must throwA[Throwable]   // TODO Better exception
      
    }
    
  }
  
}

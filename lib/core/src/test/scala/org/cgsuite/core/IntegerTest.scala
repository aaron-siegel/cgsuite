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
class IntegerTest extends Specification {
  
  "Integer" should {
    
    "construct objects of the correct class" in {
      
      Integer(0) must_== ZeroImpl
      Integer(1).getClass must_== classOf[SmallIntegerImpl]
      Integer(-1L << 31).getClass must_== classOf[SmallIntegerImpl]
      Integer(1L << 31).getClass must_== classOf[IntegerImpl]
      
    }
    
    "give correct answers to basic operations" in {
      
      Integer(5) + Integer(7) must_== Integer(12)
      Integer(5) * Integer(7) must_== Integer(35)
      val big = Integer(1L << 37)
      big * Integer(2) must_== Integer(1L << 38)
      big / Integer(1 << 10) must_== Integer(1 << 27)
      Integer(35) div Integer(6) must_== Integer(5)
      
    }
    
    "give correct results for gcd" in {
      
      Integer(35) gcd Integer(25) must_== Integer(5)
      Integer(35) gcd Integer(-25) must_== Integer(5)
      Integer(-35) gcd Integer(-25) must_== Integer(5)
      Integer(-35) gcd Integer(25) must_== Integer(5)
      
      Integer((1L << 40) * 729) gcd Integer(60) must_== Integer(12)
      Integer(-(1L << 40) * 729) gcd Integer(60) must_== Integer(12)
      Integer(-(1L << 40) * 729) gcd Integer(-60) must_== Integer(12)
      Integer((1L << 40) * 729) gcd Integer(-60) must_== Integer(12)
      
    }
    
    "simplify to the correct class" in {
      
      val one = Integer(1)
      val x = Integer(1L << 31)
      
      (x - one).getClass must_== classOf[SmallIntegerImpl]
      (x - x) must_== ZeroImpl
      (one - one) must_== ZeroImpl
      
    }
    
  }
  
}

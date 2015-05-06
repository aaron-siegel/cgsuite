/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.dsl._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class CanonicalShortGameTest extends Specification {

  "CanonicalShortGame" should {

    val switch = CanonicalShortGame(Integer(3))(CanonicalShortGame(Integer(2))(Integer(1)))

    "return the correct options" in {

      switch.options(Left) must_== Iterable(Integer(3))
      switch.options(Right) must_== Iterable(CanonicalShortGame(Integer(2))(Integer(1)))

    }

    "compute sums and negatives correctly" in {
      
      val doubleUp = up + up
      doubleUp.options(Left) must_== Iterable(zero)
      doubleUp.options(Right) must_== Iterable(upStar)

      val tripleUp = doubleUp + up
      tripleUp.options(Right) must_== Iterable(doubleUp + star)
      
      val doubled = switch + switch
      doubled.options(Left) must_== Iterable(Integer(5))
      doubled.options(Right).head.options(Right) must_== Iterable(Integer(3))
      doubled.options(Right).head.options(Left) must_== Iterable(Integer(4), CanonicalShortGame(Integer(5))(Integer(4)))

      switch must_== doubled - switch
      switch must_== doubled + (-switch)
      
    }

    "compute mean and temperature correctly" in {

      switch.mean must_== DyadicRationalNumber(9, 4)
      switch.temperature must_== DyadicRationalNumber(3, 4)

    }

  }

}

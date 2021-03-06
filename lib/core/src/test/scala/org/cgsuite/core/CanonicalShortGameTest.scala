/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.dsl._
import org.scalatest.{FlatSpec, Matchers}

class CanonicalShortGameTest extends FlatSpec with Matchers {

  lazy val switch = CanonicalShortGame(Integer(3))(CanonicalShortGame(Integer(2))(Integer(1)))

  "CanonicalShortGame" should "return the correct options" in {

    switch.optionsFor(Left) shouldBe Set(Integer(3))
    switch.optionsFor(Right) shouldBe Set(CanonicalShortGame(Integer(2))(Integer(1)))

  }

  it should "compute sums and negatives correctly" in {

    val doubleUp = up + up
    doubleUp.optionsFor(Left) shouldBe Set(zero)
    doubleUp.optionsFor(Right) shouldBe Set(upStar)

    val tripleUp = doubleUp + up
    tripleUp.optionsFor(Right) shouldBe Set(doubleUp + star)

    val doubled = switch + switch
    doubled.optionsFor(Left) shouldBe Set(Integer(5))
    doubled.optionsFor(Right).head.optionsFor(Right) shouldBe Set(Integer(3))
    doubled.optionsFor(Right).head.optionsFor(Left) shouldBe Set(Integer(4), CanonicalShortGame(Integer(5))(Integer(4)))

    switch shouldBe doubled - switch
    switch shouldBe doubled + (-switch)

  }

  it should "give the correct properties for {3||2|1}" in {

    switch should have (
      'birthday (Integer(4)),
      'leftStop (Integer(3)),
      'mean (DyadicRationalNumber(9, 4)),
      'outcomeClass (OutcomeClass.L),
      'rightStop (Integer(2)),
      'stopCount (Integer(3)),
      'temperature (DyadicRationalNumber(3, 4))
    )

  }

}

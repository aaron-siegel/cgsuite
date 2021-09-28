/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.dsl._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CanonicalShortGameTest extends AnyFlatSpec with Matchers {

  lazy val switch = CanonicalShortGame(Integer(3))(CanonicalShortGame(Integer(2))(Integer(1)))

  "CanonicalShortGame" should "return the correct options" in {

    switch.options(Left) shouldBe Set(Integer(3))
    switch.options(Right) shouldBe Set(CanonicalShortGame(Integer(2))(Integer(1)))

  }

  it should "compute sums and negatives correctly" in {

    val doubleUp = up + up
    doubleUp.options(Left) shouldBe Set(zero)
    doubleUp.options(Right) shouldBe Set(upStar)

    val tripleUp = doubleUp + up
    tripleUp.options(Right) shouldBe Set(doubleUp + star)

    val doubled = switch + switch
    doubled.options(Left) shouldBe Set(Integer(5))
    doubled.options(Right).head.options(Right) shouldBe Set(Integer(3))
    doubled.options(Right).head.options(Left) shouldBe Set(Integer(4), CanonicalShortGame(Integer(5))(Integer(4)))

    switch shouldBe doubled - switch
    switch shouldBe doubled + (-switch)

  }

  it should "give the correct properties for {3||2|1}" in {

    switch should have (
      Symbol("birthday") (Integer(4)),
      Symbol("leftStop") (Integer(3)),
      Symbol("mean") (DyadicRationalNumber(9, 4)),
      Symbol("outcomeClass") (OutcomeClass.L),
      Symbol("rightStop") (Integer(2)),
      Symbol("stopCount") (Integer(3)),
      Symbol("temperature") (DyadicRationalNumber(3, 4))
    )

  }

}

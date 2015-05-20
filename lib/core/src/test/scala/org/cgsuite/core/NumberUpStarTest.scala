package org.cgsuite.core

import org.cgsuite.dsl._
import org.scalatest.{FlatSpec, Matchers}

class NumberUpStarTest extends FlatSpec with Matchers {

  "NumberUpStar" should "return correct options" in {

    up.options(Left) shouldBe Set(zero)
    up.options(Right) shouldBe Set(star)
    upStar.options(Left) shouldBe Set(zero, star)
    upStar.options(Right) shouldBe Set(zero)

  }

}

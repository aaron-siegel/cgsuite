package org.cgsuite.core

import org.cgsuite.dsl._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class NumberUpStarTest extends Specification {

  "NumberUpStar" should {

    "return correct options" in {

      up.options(Left) must_== Iterable(zero)
      up.options(Right) must_== Iterable(star)
      upStar.options(Left) must_== Iterable(zero, star)
      upStar.options(Right) must_== Iterable(zero)

    }

  }

}

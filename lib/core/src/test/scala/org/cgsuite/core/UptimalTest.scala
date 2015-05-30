package org.cgsuite.core

import org.cgsuite.dsl._
import org.scalatest.{FlatSpec, Matchers}

class UptimalTest extends FlatSpec with Matchers {

  "UptimalExpansion" should "agree with sums of ups and stars" in {

    val multiples = (-2 to 2) map { Integer(_) }
    val up2 = CanonicalShortGame(zero)(downStar)
    val up3 = CanonicalShortGame(zero)(downStar - up2)
    val up4 = CanonicalShortGame(zero)(downStar - up2 - up3)

    for {
      number <- Seq(zero, one, negativeOne)
      nimber <- Seq[CanonicalShortGame](zero, star)
      a1 <- multiples
      a2 <- multiples
      a3 <- multiples
      a4 <- multiples
    } yield {

      val g = number.asInstanceOf[CanonicalShortGame] + nimber + a1 * up + a2 * up2 + a3 * up3 + a4 * up4
      val expected = new UptimalExpansion(number, nimber == star, a1.intValue, a2.intValue, a3.intValue, a4.intValue)
      g.uptimalExpansion shouldBe expected

    }

  }

}

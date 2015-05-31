package org.cgsuite.core

import org.cgsuite.dsl._
import org.cgsuite.exception.InputException
import org.scalatest.{FlatSpec, Matchers}

class UptimalTest extends FlatSpec with Matchers {

  "UptimalExpansion" should "agree with sums of ups and stars" in {

    val multiples = (-3 to 3) map { Integer(_) }
    val up2 = CanonicalShortGame(zero)(downStar)
    val up3 = CanonicalShortGame(zero)(downStar - up2)
    val up4 = CanonicalShortGame(zero)(downStar - up2 - up3)

    for {
      number <- Seq(zero, one, DyadicRationalNumber(-5, 4))
      nimber <- Seq(zero, star, Nimber(2), Nimber(3), Nimber(4))
      a1 <- multiples
      a2 <- multiples
      a3 <- multiples
      a4 <- multiples
    } yield {

      val g = number.asInstanceOf[CanonicalShortGame] + nimber + a1 * up + a2 * up2 + a3 * up3 + a4 * up4
      val expected = new UptimalExpansion(number, nimber.nimValue, a1.intValue, a2.intValue, a3.intValue, a4.intValue)
      try {
        g.uptimalExpansion shouldBe expected
      } catch {
        case exc: InputException =>
          println("Got exception on " + expected)
          throw exc
      }

    }

  }

}

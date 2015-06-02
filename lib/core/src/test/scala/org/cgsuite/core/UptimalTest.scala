package org.cgsuite.core

import org.cgsuite.dsl._
import org.cgsuite.exception.InputException
import org.scalatest.{FlatSpec, Matchers}

class UptimalTest extends FlatSpec with Matchers {

  "Uptimal" should "return correct options" in {

    up.options(Left) shouldBe Set(zero)
    up.options(Right) shouldBe Set(star)
    upStar.options(Left) shouldBe Set(zero, star)
    upStar.options(Right) shouldBe Set(zero)

  }

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
        (g, g.asInstanceOf[Uptimal].uptimalExpansion) shouldBe (g, expected)
      } catch {
        case exc: InputException =>
          println("Got exception on " + expected)
          throw exc
      }

    }

  }

}

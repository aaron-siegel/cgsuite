package org.cgsuite.core.misere

object MisereValues {

  val zero = MisereCanonicalGame()
  val star = MisereCanonicalGame(zero)
  val starTwo = MisereCanonicalGame(zero, star)
  val starTwoSharp = MisereCanonicalGame(starTwo)

}

package org.cgsuite.core.impartial

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class TBCodeTest extends FlatSpec with Matchers {

  val normalizations = Map(
    "04.30" -> "4.3",
    "04.00[30]" -> "4.00[30]",
    "04.00[00]" -> "4.0",
    "00.00[00]" -> "0.0",
    "0.{1,2!}" -> "0.6!",
    "0.{0!,2!,3!}" -> "0.D!",
    "0.{0!,2!,3}" -> "0.{0,2!,3}",
    "0.{0!,2!,3!+}" -> "0.{0,2+}!",
    "0.{0!,2,3!+}" -> "0.{0,2,3!+}",
    "0.{0!,3+}" -> "0.{0,3+}",
    "0.{0,2!,4!+}" -> "0.{0,2,4+}!",
    "0.{0!,2!,4+}" -> "0.{0,2!,4+}",
    "0.{0!,2!,4?+}" -> "0.{0,2!,4?+}",      // Do we also want to convert 2! to 2?
    "0.{0!,2?,4?+}" -> "0.{0,2,4+}?",
    "0.{0!,2=,4?+}" -> "0.{0,2=,4?+}",
    "0.{0!,2=,3?,4?+}" -> "0.{0,2=,3?+}",
    "0.{0!,2?,3?,4?+}" -> "0.{0,2+}?",
    "0.{0,1!,2,3,4+}" -> "0.*",
    "0.{0,1,2!,3!,4!+}" -> "0.*!"
  )

  val traversals = Map(
    ("0.3", 9) -> "8",
    ("0.7", 9) -> "8  1,7  2,6  3,5  4,4",
    ("0.77", 9) -> "8  1,7  2,6  3,5  4,4  7  1,6  2,5  3,4",
    ("4.0", 8) -> "1,7  2,6  3,5  4,4",
    ("4!.0", 8) -> "1,7  2,6  3,5",
    ("0.3F", 9) -> "8  7  1,6  2,5  3,4  1,1,5  1,2,4  1,3,3  2,2,3",
    ("0.3F?", 9) -> "8  7  1,6  2,5  3,4  1,1,5  1,2,4  1,3,3  2,2,3",
    ("0.3F!", 9) -> "8  7  1,6  2,5  3,4  1,2,4",
    ("0.3f", 11) -> "10  9  1,8  2,7  3,6  4,5  1,1,7  1,2,6  1,3,5  1,4,4  2,2,5  2,3,4  3,3,3",
    ("0.3f?", 11) -> "10  9  1,8  2,7  3,6  4,5  1,1,7  1,2,6  1,3,5  1,4,4  2,2,5  2,3,4",
    ("0.3f!", 11) -> "10  9  1,8  2,7  3,6  4,5  1,2,6  1,3,5  2,3,4",
    ("0.*3", 7) -> "6  1,5  2,4  3,3  1,1,4  1,2,3  2,2,2  1,1,1,3  1,1,2,2  1,1,1,1,2  1,1,1,1,1,1  5",
    ("0.*?3", 7) -> "6  1,5  2,4  1,1,4  1,2,3  1,1,1,3  1,1,2,2  1,1,1,1,2  5",
    ("0.*!3", 7) -> "6  1,5  2,4  1,2,3  5",
    ("0.[3]", 7) -> "6  5  4  3  2  1  -",
    ("0.[2]", 7) -> "6  5  4  3  2  1",
    ("0.3[1]", 7) -> "6  -",
    ("0.7[03]", 7) -> "6  1,5  2,4  3,3  4  2  -",
    ("80.0", 7) -> "1,1,6  1,2,5  1,3,4  2,2,4  2,3,3",
    ("0.*=", 11) -> "10  5,5  2,2,2,2,2  1,1,1,1,1,1,1,1,1,1",
    // Split into at least two equal sized piles
    ("{2+}=.0", 10) -> "5,5  2,2,2,2,2  1,1,1,1,1,1,1,1,1,1",
    // Take 1 token, optionally splitting the remainder into at least three heaps
    ("0.{0,1,3+}", 1) -> "-",
    ("0.{0,1,3+}", 2) -> "1",
    ("0.{0,1,3+}", 7) -> "6  1,1,4  1,2,3  2,2,2  1,1,1,3  1,1,2,2  1,1,1,1,2  1,1,1,1,1,1",
    // Take 1 token, optionally splitting the remainder into at least three heaps, but leaving at least one token
    ("0.{1,3+}", 1) -> "",
    // Take 1 token and split the remainder into two heaps (unconstrained) or at least three heaps, not all equal
    ("0.{2,3?+}", 7) -> "1,5  2,4  3,3  1,1,4  1,2,3  1,1,1,3  1,1,2,2  1,1,1,1,2"
  )

  "TBCode" should "normalize codes correctly" in {

    normalizations foreach { case (orig, normal) =>

      TBCode(orig).toString shouldBe normal

    }

  }

  it should "traverse options correctly" in {

    traversals foreach { case ((code, heapSize), expectedResultString) =>

      val traversal = TBCode(code).traversal(heapSize)
      val result = mutable.MutableList[IndexedSeq[Int]]()
      while (traversal.advance()) {
        result += (0 until traversal.currentLength) map traversal.currentPart
      }
      val resultString = result map { heaps => if (heaps.isEmpty) "-" else heaps mkString "," } mkString "  "
      resultString shouldBe expectedResultString

    }

  }

}

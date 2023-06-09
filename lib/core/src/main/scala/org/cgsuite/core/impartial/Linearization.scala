package org.cgsuite.core.impartial

import org.cgsuite.core.{ImpartialGame, Integer}
import org.cgsuite.exception.NotShortGameException

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class Linearization(games: Iterable[ImpartialGame]) extends HeapRuleset {

  val (positions, indices) = {
    val positionsBuf = ArrayBuffer[ImpartialGame]()
    val indicesBuf = mutable.Map[ImpartialGame, Int]()
    games foreach { addPositions(positionsBuf, indicesBuf, _) }
    (positionsBuf.toVector, indicesBuf.toMap)
  }

  private def addPositions(positionsBuf: ArrayBuffer[ImpartialGame], indicesBuf: mutable.Map[ImpartialGame, Int], g: ImpartialGame): Unit = {
    if (!indicesBuf.contains(g)) {
      g.options foreach { addPositions(positionsBuf, indicesBuf, _) }
      if (indicesBuf.contains(g)) {
        throw NotShortGameException("That is not a short game.")
      }
      indicesBuf(g) = positionsBuf.size
      positionsBuf += g
    }
  }

  override def heapOptions(heapSize: Integer): Iterable[IndexedSeq[Integer]] = {
    if (heapSize.intValue < positions.length) {
      positions(heapSize.intValue).options map { option =>
        IndexedSeq(Integer(indices(option)))
      }
    } else {
      Iterable.empty
    }
  }

}

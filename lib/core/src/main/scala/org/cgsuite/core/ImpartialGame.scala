package org.cgsuite.core

import org.cgsuite.lang.NotShortGameException
import org.cgsuite.util.TranspositionTable
import org.cgsuite.core.ImpartialGame.mex

import scala.collection.{BitSet, mutable}

/**
 * Created by asiegel on 8/25/15.
 */
object ImpartialGame {

  def mex(set: Iterable[Int]): Int = {
    val bitset = BitSet.empty ++ set
    var m = 0
    while (m <= bitset.max && (bitset contains m)) {
      m += 1
    }
    m
  }

}

trait ImpartialGame extends Game {

  override def options(player: Player): Iterable[ImpartialGame]

  override def canonicalForm: CanonicalShortGame = Nimber(nimValue)

  override def canonicalForm(tt: TranspositionTable): CanonicalShortGame = Nimber(nimValue(tt))

  def nimValue: Integer = {
    nimValue(new TranspositionTable())
  }

  def nimValue(tt: TranspositionTable): Integer = {
    SmallInteger(nimValue(tt, mutable.HashSet[ImpartialGame]()))
  }

  private def nimValue(tt: TranspositionTable, visited: mutable.Set[ImpartialGame]): Int = {
    val decomp = decomposition
    if (decomp.size == 1 && decomp.head == this) {
      nimValueR(tt, visited)
    } else {
      var result: Int = 0
      val it = decomp.iterator
      while (it.hasNext) {
        val component = it.next() match {
          case g: ImpartialGame => g.nimValueR(tt, visited)
        }
        result ^= component
      }
      result
    }
  }

  private def nimValueR(tt: TranspositionTable, visited: mutable.Set[ImpartialGame]): Int = {
    tt.get(this) match {
      case Some(x: Int) => x
      case None if !visited.contains(this) =>
        visited += this
        try {
          val opts = options(Left) map { _.nimValue(tt, visited) }
          val m = mex(opts)
          tt.put(this, m)
          m
        } finally {
          visited -= this
        }
      case _ =>
        throw NotShortGameException(s"That is not a short game. If that is intentional, try `GameValue` in place of `CanonicalForm`.")
    }
  }

}

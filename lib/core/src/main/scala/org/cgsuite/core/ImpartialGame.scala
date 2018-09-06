package org.cgsuite.core

import org.cgsuite.core.ImpartialGame.mex
import org.cgsuite.core.misere.{Genus, MisereCanonicalGame, MisereValues}
import org.cgsuite.exception.NotShortGameException
import org.cgsuite.util.TranspositionTable

import scala.collection.{BitSet, mutable}

object ImpartialGame {

  def mex(set: Iterable[Int]): Int = {
    if (set.isEmpty) {
      0
    } else {
      val bitset = BitSet.empty ++ set
      var m = 0
      while (m <= bitset.max && (bitset contains m)) {
        m += 1
      }
      m
    }
  }

}

trait ImpartialGame extends Game {

  override def unary_- : ImpartialGame = this

  def +(other: ImpartialGame): ImpartialGame = CompoundImpartialGame(CompoundType.Disjunctive, this, other)

  def -(other: ImpartialGame): ImpartialGame = this + this

  override def options(player: Player): Iterable[ImpartialGame] = options

  def options: Iterable[ImpartialGame]

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
          val opts = options map { _.nimValue(tt, visited) }
          val m = mex(opts)
          tt.put(this, m)
          m
        } finally {
          visited -= this
        }
      case _ =>
        throw NotShortGameException(s"That is not a short game. If that is intentional, try `GameValue` in place of `NimValue`.")
    }
  }

  // TODO Improve this for tame genera
  def genus: Genus = misereCanonicalForm.genus

  def misereNimValue: Integer = Integer(genus.misereNimValue());

  def misereCanonicalForm: MisereCanonicalGame = {
    misereCanonicalForm(new TranspositionTable())
  }

  def misereCanonicalForm(tt: TranspositionTable): MisereCanonicalGame = {
    misereCanonicalForm(tt, mutable.HashSet[ImpartialGame]())
  }

  private def misereCanonicalForm(tt: TranspositionTable, visited: mutable.Set[ImpartialGame]): MisereCanonicalGame = {
    val decomp = decomposition
    if (decomp.size == 1 && decomp.head == this) {
      misereCanonicalFormR(tt, visited)
    } else {
      var result: MisereCanonicalGame = MisereValues.zero
      val it = decomp.iterator
      while (it.hasNext) {
        val component = it.next() match {
          case g: ImpartialGame => g.misereCanonicalFormR(tt, visited)
        }
        result = result + component
      }
      result
    }
  }

  private def misereCanonicalFormR(tt: TranspositionTable, visited: mutable.Set[ImpartialGame]): MisereCanonicalGame = {
    tt.get(this) match {
      case Some(x: MisereCanonicalGame) => x
      case None if !visited.contains(this) =>
        visited += this
        try {
          val opts = options map { _.misereCanonicalForm(tt, visited) }
          val result = MisereCanonicalGame(opts.toSeq : _*)
          tt.put(this, result)
          result
        } finally {
          visited -= this
        }
      case _ =>
        throw NotShortGameException(s"That is not a short game.")
    }
  }

}

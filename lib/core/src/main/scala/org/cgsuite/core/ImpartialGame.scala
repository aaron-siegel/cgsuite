package org.cgsuite.core

import org.cgsuite.core.ImpartialGame.mex
import org.cgsuite.core.misere.{Genus, MisereCanonicalGame, MisereValues}
import org.cgsuite.exception.NotShortGameException
import org.cgsuite.util.{TranspositionCache, TranspositionTable}

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

  override def canonicalForm(tc: TranspositionCache): CanonicalShortGame = Nimber(nimValue(tc))

  def nimValue: Integer = {
    nimValue(new TranspositionCache())
  }

  def nimValue(tc: TranspositionCache): Integer = {
    SmallInteger(NimValueReducer.reduce(this, tc.tableFor[Int]('NimValue)))
  }

  def genus: Genus = misereCanonicalForm.genus

  def misereNimValue: Integer = SmallInteger(genus.misereNimValue())

  def misereCanonicalForm: MisereCanonicalGame = {
    misereCanonicalForm(new TranspositionCache())
  }

  def misereCanonicalForm(tc: TranspositionCache): MisereCanonicalGame = {
    MisereCanonicalGameReducer.reduce(this, tc.tableFor[MisereCanonicalGame]('MisereCanonicalGame))
  }

}

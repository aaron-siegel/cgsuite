/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.output.OutputTarget
import org.cgsuite.util.TranspositionTable

trait Game extends OutputTarget {

  def unary_+ : Game = this
  def unary_- : Game = NegativeGame(this)

  def +(other: Game): Game = CompoundGame(CompoundType.Disjunctive, this, other)
  def -(other: Game): Game = this + (-other)

  def options(player: Player): Iterable[Game]

  def gameValue: SidedValue = canonicalForm

  def canonicalForm: CanonicalShortGame = canonicalForm(new TranspositionTable())

  def canonicalForm(tt: TranspositionTable): CanonicalShortGame = shortCanonicalForm(tt)

  def shortCanonicalForm(tt: TranspositionTable): CanonicalShortGame = {
    var result: CanonicalShortGame = Values.zero
    val it = decomposition.iterator
    while (it.hasNext) {
      result += it.next().asInstanceOf[Game].shortCanonicalFormR(tt)
    }
    result
  }

  def shortCanonicalFormR(tt: TranspositionTable): CanonicalShortGame = {
    tt.get(this) match {
      case Some(x) => x.asInstanceOf[CanonicalShortGame]
      case _ =>
        val lo = options(Left ) map { _.shortCanonicalForm(tt) }
        val ro = options(Right) map { _.shortCanonicalForm(tt) }
        val canonicalForm = CanonicalShortGame(lo, ro)
        tt.put(this, canonicalForm)
        canonicalForm
    }
  }

  def leftOptions: Iterable[Game] = options(Left)
  
  def rightOptions: Iterable[Game] = options(Right)

  def decomposition: Seq[_] = Seq(this)

}

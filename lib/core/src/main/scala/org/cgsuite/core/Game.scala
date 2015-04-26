/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

sealed trait Player {
  def sign: Int
  def opponent: Player
}

case object Left extends Player {
  val sign = 1
  val opponent = Right
}

case object Right extends Player {
  val sign = -1
  val opponent = Left
}

trait Game {

  def unary_+ : Game = this
  def unary_- : Game = NegativeGame(this)

  def +(other: Game): Game = CompoundGame(CompoundType.Disjunctive, this, other)
  def -(other: Game): Game = this + (-other)
  def nCopies(n: Integer): Game = MultipleGame(n, this)
  
  def options(player: Player): Iterable[Game]
  /*
  def leftOptions: Iterable[Game] = options(Left)
  
  def rightOptions: Iterable[Game] = options(Right)
  */
}

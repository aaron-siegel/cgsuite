/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.core.{CanonicalShortGameOps => ops}

object CanonicalShortGame {
  
  private[cgsuite] def apply(gameId: Int): CanonicalShortGame = {
    if (ops.isNumberUpStar(gameId)) {
      NumberUpStar(ops.getNumberPart(gameId), ops.getUpMultiplePart(gameId), ops.getNimberPart(gameId))
    } else {
      CanonicalShortGameImpl(gameId)
    }
  }
  
  def apply(leftOptions: Iterable[CanonicalShortGame], rightOptions: Iterable[CanonicalShortGame]): CanonicalShortGame = {
    val leftIds: Array[Int] = leftOptions.map { _.gameId }.toArray
    val rightIds: Array[Int] = rightOptions.map { _.gameId }.toArray
    CanonicalShortGame(ops.constructFromOptions(leftIds, rightIds))
  }
  
  def apply(leftOptions: CanonicalShortGame*)(rightOptions: CanonicalShortGame*): CanonicalShortGame = {
    apply(leftOptions.toIterable, rightOptions.toIterable)
  }
  
}

trait CanonicalShortGame extends CanonicalStopperGame {

  def gameId: Int
  
  override def unary_- = CanonicalShortGame(ops.getNegative(gameId))
  def +(other: CanonicalShortGame) = CanonicalShortGame(ops.add(gameId, other.gameId))
  def -(other: CanonicalShortGame) = CanonicalShortGame(ops.subtract(gameId, other.gameId))
  override def nCopies(n: Integer) = n.nortonMultiply(this)

  def <=(other: CanonicalShortGame) = ops.leq(gameId, other.gameId)
  def >=(other: CanonicalShortGame) = other <= this
  def < (other: CanonicalShortGame) = this <= other && !(other <= this)
  def > (other: CanonicalShortGame) = !(this <= other) && other <= this

  override def options(player: Player): Iterable[CanonicalShortGame] = {
    player match {
      case Left => (0 until ops.getNumLeftOptions(gameId)) map { n =>
          CanonicalShortGame(ops.getLeftOption(gameId, n))
        }
      case Right => (0 until ops.getNumRightOptions(gameId)) map { n =>
          CanonicalShortGame(ops.getRightOption(gameId, n))
        }
    }
  }

  def birthday: Integer = SmallInteger(ops.birthday(gameId))
  def cool(t: DyadicRationalNumber): CanonicalShortGame = CanonicalShortGame(ops.cool(gameId, t, t.gameId))
  def isInfinitesimal: Boolean = leftStop == Values.zero && rightStop == Values.zero
  def isNimber: Boolean = ops.isNimber(gameId)
  def isNumber: Boolean = ops.isNumber(gameId)
  def isNumberish: Boolean = leftStop == rightStop
  def isNumberTiny: Boolean = {
    val gl = options(Left)
    val gr = options(Right)
    if (gl.size == 1 && gr.size == 1 && gl.head.isNumber) {
      val grl = gr.head.options(Left)
      val grr = gr.head.options(Right)
      grl.size == 1 && grr.size == 1 && gl.head == grl.head && grr.head.leftStop < gl.head.leftStop
    } else {
      false
    }
  }
  def isNumberUpStar: Boolean = ops.isNumberUpStar(gameId)
  def leftStop: DyadicRationalNumber = ops.leftStop(gameId)
  def mean: DyadicRationalNumber = ops.mean(gameId)
  def nortonMultiply(that: CanonicalShortGame) = CanonicalShortGame(ops.nortonMultiply(gameId, that.gameId))
  def rightStop: DyadicRationalNumber = ops.rightStop(gameId)
  def temperature: DyadicRationalNumber = ops.temperature(gameId)
  def thermograph: Thermograph = ops.thermograph(gameId)

}

case class CanonicalShortGameImpl(gameId: Int) extends CanonicalShortGame {
  
  assert(!isNumberUpStar)
  
}

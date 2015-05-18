/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.core.{CanonicalShortGameOps => ops}

object Values extends ValuesTrait

private[cgsuite] trait ValuesTrait {

  val zero = Zero()
  val one = SmallInteger(1)
  val negativeOne = SmallInteger(-1)
  val two = SmallInteger(2)

  val star = Nimber(1)
  val starTwo = Nimber(2)
  
  val up = NumberUpStar(zero, 1, 0)
  val upStar = NumberUpStar(zero, 1, 1)
  val down = NumberUpStar(zero, -1, 0)
  val downStar = NumberUpStar(zero, -1, 1)
  
  val positiveInfinity = RationalNumber(1, 0)
  val negativeInfinity = RationalNumber(-1, 0)
  val nan = RationalNumber(0, 0)

  val on = CanonicalStopperGame(LoopyGame.ON)
  val off = CanonicalStopperGame(LoopyGame.OFF)
  val over = CanonicalStopperGame(LoopyGame.OVER)
  val under = CanonicalStopperGame(LoopyGame.UNDER)

}

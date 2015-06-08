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
  val three = SmallInteger(3)

  val star = Nimber(1)
  val starTwo = Nimber(2)
  
  val up = Uptimal(zero, 1, 0)
  val upStar = Uptimal(zero, 1, 1)
  val down = Uptimal(zero, -1, 0)
  val downStar = Uptimal(zero, -1, 1)
  
  val positiveInfinity = RationalNumber(1, 0)
  val negativeInfinity = RationalNumber(-1, 0)

  val on = Pseudonumber(1)
  val off = Pseudonumber(-1)
  val over = Pseudonumber(zero, 1)
  val under = Pseudonumber(zero, -1)

}

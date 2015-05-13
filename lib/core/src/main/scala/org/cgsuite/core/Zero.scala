/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.output.OutputTarget

object Zero {
  
  def apply(): Zero = ZeroImpl
  
}

trait Zero extends SmallInteger with Nimber {
  
  override val intValue = 0
  val nimValue = 0

  override lazy val gameId = CanonicalShortGameOps.ZERO_ID
  override val numberPart = this
  override val upMultiplePart = 0
  override val nimberPart = 0

  override def unary_- = this
  
  override def options(player: Player): Iterable[Nothing] = Iterable.empty
  
}

case object ZeroImpl extends Zero

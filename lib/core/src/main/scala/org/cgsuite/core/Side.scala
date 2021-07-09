package org.cgsuite.core

object Side {
  val Onside = org.cgsuite.core.Onside
  val Offside = org.cgsuite.core.Offside
}

sealed trait Side {
  def sign: Int
  def unary_- : Side
  private[core] def jConst: Int
}

case object Onside extends Side {
  val sign = 1
  def unary_- = Offside
  private[core] val jConst = LoopyGame.ONSIDE
}

case object Offside extends Side {
  val sign = -1
  def unary_- = Onside
  private[core] val jConst = LoopyGame.OFFSIDE
}

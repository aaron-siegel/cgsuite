package org.cgsuite.core


sealed trait Side {
  def sign: Int
  private[core] def jConst: Int
}

case object Onside extends Side {
  val sign = 1
  private[core] val jConst = LoopyGame.ONSIDE
}

case object Offside extends Side {
  val sign = -1
  private[core] val jConst = LoopyGame.OFFSIDE
}

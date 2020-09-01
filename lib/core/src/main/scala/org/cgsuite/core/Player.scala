package org.cgsuite.core

object Player {
  val Left = org.cgsuite.core.Left
  val Right = org.cgsuite.core.Right
}

sealed trait Player {
  def sign: Int
  def opponent: Player
  def ordinal: Int
}

case object Left extends Player {
  val sign = 1
  val opponent = Right
  val ordinal = 1
}

case object Right extends Player {
  val sign = -1
  val opponent = Left
  val ordinal = 2
}

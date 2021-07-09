package org.cgsuite.core

object Player {
  val Left = org.cgsuite.core.Left
  val Right = org.cgsuite.core.Right
}

sealed trait Player {
  def sign: Integer
  def opponent: Player
  def ordinal: Integer
}

case object Left extends Player {
  val sign = Values.one
  val opponent = Right
  val ordinal = Values.one
}

case object Right extends Player {
  val sign = Values.negativeOne
  val opponent = Left
  val ordinal = Values.two
}

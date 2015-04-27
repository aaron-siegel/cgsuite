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

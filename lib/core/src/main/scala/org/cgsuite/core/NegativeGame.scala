package org.cgsuite.core

case class NegativeGame(g: Game) extends Game {

  override def unary_- = g

  def options(player: Player) = g.options(player.opponent) map { -_ }

}

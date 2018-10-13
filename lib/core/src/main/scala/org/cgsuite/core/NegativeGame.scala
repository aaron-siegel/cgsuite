package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput

case class NegativeGame(g: Game) extends Game {

  override def unary_- = g

  def optionsFor(player: Player) = g.optionsFor(player.opponent) map { -_ }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendMath("-")
    sto.appendOutput(g.toOutput)
    sto
  }

}

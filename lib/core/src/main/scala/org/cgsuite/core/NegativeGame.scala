package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput

case class NegativeGame(g: Game) extends Game {

  override def unary_- = g

  def options(player: Player) = g.options(player.opponent) map { -_ }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendMath("-")
    g match {
      case _: CompoundGame =>
        sto.appendMath("(")
        sto.appendOutput(g.toOutput)
        sto.appendMath(")")
      case _ => sto.appendOutput(g.toOutput)
    }
    sto
  }

}

package org.cgsuite.core

import org.cgsuite.dsl._
import org.cgsuite.output.StyledTextOutput

case class MultipleGame(n: Integer, g: Game) extends Game {

  assert(n > zero)

  override def unary_- = MultipleGame(n, -g)
  def options(player: Player) = {
    g.options(player) map { go => (n - one) * g + go }
  }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendMath(n.toString)
    sto.appendMath(" * (")
    sto.appendOutput(g.toOutput)
    sto.appendMath(")")
    sto
  }

}

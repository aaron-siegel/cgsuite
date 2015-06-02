package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput


object ExplicitGame {

  def apply(lo: Game*)(ro: Game*): ExplicitGame = apply(Iterable(lo : _*), Iterable(ro : _*))

}

case class ExplicitGame(lo: Iterable[Game], ro: Iterable[Game]) extends Game {

  override def unary_- = ExplicitGame(ro map { -_ }, lo map { -_ })
  override def options(player: Player) = player match {
    case Left => lo
    case Right => ro
  }
  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendMath("'{")
    sto.appendOutput(lo.head.toOutput)
    lo.tail foreach { gl =>
      sto.appendMath(",")
      sto.appendOutput(gl.toOutput)
    }
    sto.appendMath("|")
    sto.appendOutput(ro.head.toOutput)
    ro.tail foreach { gr =>
      sto.appendMath(",")
      sto.appendOutput(gr.toOutput)
    }
    sto.appendMath("}'")
    sto
  }

}

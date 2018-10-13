package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput


object ExplicitGame {

  def apply(lo: Game*)(ro: Game*): ExplicitGame = apply(Iterable(lo : _*), Iterable(ro : _*))

}

case class ExplicitGame(lo: Iterable[Game], ro: Iterable[Game]) extends Game {

  override def unary_- = ExplicitGame(ro map { -_ }, lo map { -_ })

  override def optionsFor(player: Player) = player match {
    case Left => lo
    case Right => ro
  }

  override def depthHint: Int = {
    val loMax = (lo map { _.depthHint }).max
    val roMax = (ro map { _.depthHint }).max
    loMax + roMax
  }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendMath("'{")
    if (lo.nonEmpty) {
      sto.appendOutput(lo.head.toOutput)
      lo.tail foreach { gl =>
        sto.appendMath(",")
        sto.appendOutput(gl.toOutput)
      }
    }
    sto.appendMath("|")
    if (ro.nonEmpty) {
      sto.appendOutput(ro.head.toOutput)
      ro.tail foreach { gr =>
        sto.appendMath(",")
        sto.appendOutput(gr.toOutput)
      }
    }
    sto.appendMath("}'")
    sto
  }

}

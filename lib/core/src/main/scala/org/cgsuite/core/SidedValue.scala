package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput

object SidedValue {

  def apply(g: LoopyGame): SidedValue = {
    if (g.isStopper) {
      CanonicalStopper(g)
    } else {
      val onside = new LoopyGame
      onside.graph = LoopyGame.simplifyGraph(g.graph, null, LoopyGame.ONSIDE, g.startVertex)
      onside.startVertex = 0
      val offside = new LoopyGame
      offside.graph = LoopyGame.simplifyGraph(g.graph, null, LoopyGame.OFFSIDE, g.startVertex)
      offside.startVertex = 0
      if (onside.isStopper && offside.isStopper) {
        StopperSidedValue(CanonicalStopper(onside), CanonicalStopper(offside))
      } else {
        SidedValueImpl(SimplifiedLoopyGame(onside, Onside), SimplifiedLoopyGame(offside, Offside))
      }
    }
  }

}

trait SidedValue extends NormalValue {

  def onside: SimplifiedLoopyGame
  def offside: SimplifiedLoopyGame

  override def isStopperSided = onside.loopyGame.isStopper && offside.loopyGame.isStopper

  // TODO When we figure out how to handle SimplifiedLoopyGame in cgscript, move this
  // into native cgscript

  def toOutput: StyledTextOutput = {

    val sto = new StyledTextOutput
    sto.append(onside.toOutput)
    sto.appendMath(" & ")
    sto.append(offside.toOutput)
    sto

  }

}

case class SidedValueImpl private[core] (onside: SimplifiedLoopyGame, offside: SimplifiedLoopyGame) extends SidedValue {

  assert(!isStopperSided)

}

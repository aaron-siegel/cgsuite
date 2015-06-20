package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.output.StyledTextOutput

object SidedValue {

  def apply(onside: LoopyGame.Node, offside: LoopyGame.Node) = {
    val simplifiedOnside = LoopyGame.constructSimplifiedGame(onside, Onside.jConst)
    val simplifiedOffside = LoopyGame.constructSimplifiedGame(offside, Offside.jConst)
    if (simplifiedOnside.isStopper && simplifiedOffside.isStopper) {
      StopperSidedValue(CanonicalStopper(simplifiedOnside), CanonicalStopper(simplifiedOffside))
    } else {
      SidedValueImpl(SimplifiedLoopyGame(simplifiedOnside, Onside), SimplifiedLoopyGame(simplifiedOffside, Offside))
    }
  }

  def apply(g: LoopyGame): SidedValue = {
    if (g.isStopper) {
      CanonicalStopper(g)
    } else {
      apply(g, g)
    }
  }

  def apply(onside: LoopyGame, offside: LoopyGame) = {
    val simplifiedOnside = new LoopyGame
    simplifiedOnside.graph = LoopyGame.simplifyGraph(onside.graph, null, LoopyGame.ONSIDE, onside.startVertex)
    simplifiedOnside.startVertex = 0
    val simplifiedOffside = new LoopyGame
    simplifiedOffside.graph = LoopyGame.simplifyGraph(offside.graph, null, LoopyGame.OFFSIDE, offside.startVertex)
    simplifiedOffside.startVertex = 0
    if (simplifiedOnside.isStopper && simplifiedOffside.isStopper) {
      StopperSidedValue(CanonicalStopper(simplifiedOnside), CanonicalStopper(simplifiedOffside))
    } else {
      SidedValueImpl(SimplifiedLoopyGame(simplifiedOnside, Onside), SimplifiedLoopyGame(simplifiedOffside, Offside))
    }
  }

}

trait SidedValue extends NormalValue {

  def onside: SimplifiedLoopyGame

  def offside: SimplifiedLoopyGame

  def side(side: Side): SimplifiedLoopyGame = {
    side match {
      case Onside => onside
      case Offside => offside
    }
  }

  def unary_+ : SidedValue = this

  def unary_- : SidedValue = SidedValueImpl(-offside, -onside)

  def +(that: SidedValue): SidedValue = {
    SidedValue(onside.loopyGame.add(that.onside.loopyGame), offside.loopyGame.add(that.offside.loopyGame))
  }

  def -(that: SidedValue): SidedValue = this + (-that)

  def <=(that: SidedValue): Boolean = {
    onside.loopyGame.leq(that.onside.loopyGame, true) && offside.loopyGame.leq(that.offside.loopyGame, false)
  }

  def nCopies(n: Integer): SidedValue = {
    if (n < zero) -nCopies(-n) else MultipleGame.binarySum(n.intValue, this, zero) { _ + _ }
  }

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

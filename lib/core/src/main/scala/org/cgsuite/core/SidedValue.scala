package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

object SidedValue {

  def apply(onside: LoopyGame.Node, offside: LoopyGame.Node) = {
    val simplifiedOnside = LoopyGame.constructSimplifiedGame(onside, Onside.jConst)
    val simplifiedOffside = LoopyGame.constructSimplifiedGame(offside, Offside.jConst)
    applyToSimplified(simplifiedOnside, simplifiedOffside)
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
    applyToSimplified(simplifiedOnside, simplifiedOffside)
  }

  private[cgsuite] def applyToSimplified(simplifiedOnside: LoopyGame, simplifiedOffside: LoopyGame) = {
    if (simplifiedOnside.isStopper && simplifiedOffside.isStopper) {
      StopperSidedValue(CanonicalStopper(simplifiedOnside), CanonicalStopper(simplifiedOffside))
    } else {
      SidedValueImpl(SimplifiedLoopyGame(simplifiedOnside, Onside), SimplifiedLoopyGame(simplifiedOffside, Offside))
    }
  }

  def apply(lo: SidedValue*)(ro: SidedValue*): SidedValue = {
    // TODO Optimize if arguments are all stoppers?
    val lOnside = lo map { _.onsideSimplified }
    val rOnside = ro map { _.onsideSimplified }
    val lOffside = lo map { _.offsideSimplified }
    val rOffside = ro map { _.offsideSimplified }
    SidedValue(SimplifiedLoopyGame.constructLoopyGame(lOnside, rOnside), SimplifiedLoopyGame.constructLoopyGame(lOffside, rOffside))
  }

  private lazy val zeroAsLoopyGame = new LoopyGame(zero)

}

trait SidedValue extends NormalValue with OutputTarget {

  def onsideSimplified: SimplifiedLoopyGame

  def offsideSimplified: SimplifiedLoopyGame

  def sideSimplified(side: Side): SimplifiedLoopyGame = {
    side match {
      case Onside => onsideSimplified
      case Offside => offsideSimplified
    }
  }

  def unary_+ : SidedValue = this

  def unary_- : SidedValue = SidedValueImpl(-offsideSimplified, -onsideSimplified)

  def switch: SidedValue = SidedValue(this)(-this)

  def +(that: SidedValue): SidedValue = {
    SidedValue(onsideSimplified.loopyGame.add(that.onsideSimplified.loopyGame), offsideSimplified.loopyGame.add(that.offsideSimplified.loopyGame))
  }

  def -(that: SidedValue): SidedValue = this + (-that)

  def <=(that: SidedValue): Boolean = {
    onsideSimplified.loopyGame.leq(that.onsideSimplified.loopyGame, true) && offsideSimplified.loopyGame.leq(that.offsideSimplified.loopyGame, false)
  }

  def sidedOutcomeClass(side: Side): OutcomeClass = {
    val geqZero = SidedValue.zeroAsLoopyGame.leq(this.sideSimplified(side).loopyGame, side == Onside)
    val leqZero = this.sideSimplified(side).loopyGame.leq(SidedValue.zeroAsLoopyGame, side == Onside)
    (geqZero, leqZero) match {
      case (true, false) => OutcomeClass.L
      case (false, false) => OutcomeClass.N
      case (true, true) => OutcomeClass.P
      case (false, true) => OutcomeClass.R
    }
  }

  override def outcomeClass: LoopyOutcomeClass = {
    import OutcomeClass._
    (sidedOutcomeClass(Onside), sidedOutcomeClass(Offside)) match {
      case (L, L) => L
      case (L, N) => NHat
      case (L, P) => PHat
      case (L, R) => D
      case (N, N) => N
      case (N, R) => NCheck
      case (P, P) => P
      case (P, R) => PCheck
      case (R, R) => R
      case _ => sys error "onside.outcomeClass <| offside.outcomeClass"
    }
  }

  def nCopies(n: Integer): SidedValue = {
    if (n < zero) -nCopies(-n) else MultipleGame.binarySum(n.intValue, this, zero) { _ + _ }
  }

  override def isFinite = true

  override def isIdempotent = this + this == this

  override def isStopperSided = onsideSimplified.loopyGame.isStopper && offsideSimplified.loopyGame.isStopper

  override def toOutput: StyledTextOutput = {

    val sto = new StyledTextOutput
    if (onsideSimplified == on && offsideSimplified == off) {
      sto appendMath "dud"
    } else {
      sto append onsideSimplified.toOutput
      sto appendMath " & "
      sto append offsideSimplified.toOutput
    }
    sto

  }

}

case class SidedValueImpl private[core] (onsideSimplified: SimplifiedLoopyGame, offsideSimplified: SimplifiedLoopyGame) extends SidedValue {

  assert(!isStopperSided)

}

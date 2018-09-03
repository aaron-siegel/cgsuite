package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.exception.InvalidArgumentException


object StopperSidedValue {

  def apply(onside: CanonicalStopper, offside: CanonicalStopper): StopperSidedValue = {
    if (onside == offside) {
      onside
    } else if (offside <= onside) {
      StopperSidedValueImpl(onside, offside)
    } else {
      throw InvalidArgumentException(s"offside is not <= onside: $offside |> $onside")
    }
  }

}

trait StopperSidedValue extends SidedValue {

  override def unary_+ : StopperSidedValue = this

  override def unary_- : StopperSidedValue = StopperSidedValue(-offside, -onside)

  def +(that: StopperSidedValue) = StopperSidedValue(onside upsum that.onside, offside downsum that.offside)

  def -(that: StopperSidedValue) = this + (-that)

  def <=(that: StopperSidedValue): Boolean = onside <= that.onside && offside <= that.offside

  override def sidedOutcomeClass(side: Side): OutcomeClass = {
    this.side(side).outcomeClass
  }

  override def isIdempotent = this + this == this

  override def isInfinitesimal = onside.isInfinitesimal && offside.isInfinitesimal

  override def isNumberish = onside.strongStop(Left) == offside.strongStop(Right)

  override def isStopper = onside == offside

  override def isStopperSided = true

  override def nCopies(n: Integer): StopperSidedValue = {
    if (n < zero) -nCopies(-n) else MultipleGame.binarySum(n.intValue, this, zero) { _ + _ }
  }

  def offside: CanonicalStopper

  def onside: CanonicalStopper

  override def side(side: Side): CanonicalStopper = {
    side match {
      case Onside => onside
      case Offside => offside
    }
  }

}

case class StopperSidedValueImpl(onside: CanonicalStopper, offside: CanonicalStopper) extends StopperSidedValue {

  assert(!isStopper)

}

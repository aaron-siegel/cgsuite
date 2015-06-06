package org.cgsuite.core


trait StopperSidedValue extends NormalValue {

  override def isStopper = onside == offside

  def offside: CanonicalStopper

  def onside: CanonicalStopper

}

case class StopperSidedValueImpl(onside: CanonicalStopper, offside: CanonicalStopper) extends StopperSidedValue {

  assert(!isStopper)

}

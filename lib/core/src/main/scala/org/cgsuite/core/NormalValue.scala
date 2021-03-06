package org.cgsuite.core

trait NormalValue {

  def isAllSmall = false

  def isFinite = false

  def isIdempotent = false

  def isInfinitesimal = false

  def isInteger = false

  def isLoopfree = false

  def isNimber = false

  def isNumber = false

  def isNumberish = false

  def isNumberTiny = false

  def isOrdinal = false

  def isPlumtree = false

  def isPseudonumber = false

  def isStopper = false

  def isStopperSided = false

  def isUptimal = false

  def isZero = false

  def outcomeClass: LoopyOutcomeClass

}

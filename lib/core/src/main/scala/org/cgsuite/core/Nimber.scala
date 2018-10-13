/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.exception.InvalidArgumentException

import scala.language.postfixOps

object Nimber {

  def apply(nimValue: Integer): Nimber = Nimber(nimValue.intValue)

  def apply(nimValue: Int): Nimber = nimValue match {
    case 0 => ZeroImpl
    case m if m < 0 => throw InvalidArgumentException(s"Nim value is negative: $m")
    case _ => NimberImpl(nimValue)
  }
  
}

trait Nimber extends ImpartialGame with Uptimal {
  
  def nimValue: Integer
  def intNimValue: Int

  override lazy val uptimalExpansion = new UptimalExpansion(Values.zero, intNimValue)
  override def numberPart = Values.zero
  override def nimberPart = intNimValue
  def ordinalSum(that: Nimber) = Nimber(intNimValue + that.intNimValue)
  override def uptimalLength = 0
  override def uptimalCoefficient(n: Int) = 0

  def +(other: Nimber) = Nimber(intNimValue ^ other.intNimValue)
  def -(other: Nimber) = Nimber(intNimValue ^ other.intNimValue)
  override def unary_- = this

  override def optionsFor(player: Player): Iterable[Nimber] = options
  override def options: Iterable[Nimber] = {
    (0 until intNimValue) map { Nimber(_) } toSet
  }

  override def outcomeClass: ImpartialOutcomeClass = {
    if (nimValue.isZero) OutcomeClass.P else OutcomeClass.N
  }
  
}

case class NimberImpl(intNimValue: Int) extends Nimber {

  assert(intNimValue > 0)

  override def nimValue = SmallInteger(intNimValue)

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.exception.{ArithmeticException, InvalidArgumentException}

object DyadicRationalNumber {
  
  def apply(numerator: Integer, denominator: Integer): DyadicRationalNumber = {
    RationalNumber(numerator, denominator).asInstanceOf[DyadicRationalNumber]
  }

  def apply(numerator: Int, denominator: Int): DyadicRationalNumber = {
    apply(SmallInteger(numerator), SmallInteger(denominator))
  }

  def fromSection(left: Option[DyadicRationalNumber], right: Option[DyadicRationalNumber]): DyadicRationalNumber = {
    (left, right) match {
      case (None, None) => zero
      case (Some(l), None) if l < zero => zero
      case (None, Some(r)) if r > zero => zero
      case (Some(l), Some(r)) if l < zero && r > zero => zero
      case (Some(l), None) => l.floor + one
      case (Some(l), Some(r)) if l.floor + one < r => l.floor + one
      case (None, Some(r)) => r.ceiling - one
      case (Some(l), Some(r)) if r.ceiling - one > l => r.ceiling - one
      case (Some(l), Some(r)) => fromSection(l, r)
    }
  }

  private def fromSection(left: DyadicRationalNumber, right: DyadicRationalNumber): DyadicRationalNumber = {
    if (left >= right) {
      throw ArithmeticException("Left section must be < Right section")
    } else {
      var ls = left.step(1)
      if (ls < right) {
        while (ls.step(1) < right)
          ls = ls.step(1)
        ls
      } else {
        var rs = right.step(-1)
        if (rs > left) {
          while (rs.step(-1) > left)
            rs = rs.step(-1)
          rs
        } else {
          left.mean(right)
        }
      }
    }
  }
  
  val minSmall = apply(SmallInteger.minSmall, one)
  val maxSmall = apply(SmallInteger.maxSmall, one)

}

trait DyadicRationalNumber extends Uptimal with Pseudonumber with RationalNumber {

  override lazy val uptimalExpansion = new UptimalExpansion(numberPart, 0)
  override def numberPart = this
  override def nimberPart = 0
  override def uptimalLength = 0
  override def uptimalCoefficient(n: Int) = 0

  override def optionsFor(player: Player): Iterable[DyadicRationalNumber] = Set(step(-player.sign))

  // We need to do this to resolve ambiguities in inheriting compare
  def <=(other: DyadicRationalNumber) = (this compare other) <= 0
  def >=(other: DyadicRationalNumber) = (this compare other) >= 0
  def < (other: DyadicRationalNumber) = (this compare other) < 0
  def > (other: DyadicRationalNumber) = (this compare other) > 0

  override def unary_+ : DyadicRationalNumber = this

  override def unary_- : DyadicRationalNumber = DyadicRationalNumber(-numerator, denominator)

  def +(other: DyadicRationalNumber): DyadicRationalNumber = DyadicRationalNumber(
    numerator * other.denominator + denominator * other.numerator,
    denominator * other.denominator
    )
    
  def -(other: DyadicRationalNumber): DyadicRationalNumber = DyadicRationalNumber(
    numerator * other.denominator - denominator * other.numerator,
    denominator * other.denominator
  )
  
  def *(other: DyadicRationalNumber): DyadicRationalNumber = DyadicRationalNumber(
    numerator * other.numerator,
    denominator * other.denominator
  )

  override def birthday: Integer = {
    if (this >= zero)
      ceiling + SmallInteger(denominatorExponent)
    else
      floor.abs + SmallInteger(denominatorExponent)
  }

  override def incentives: Iterable[DyadicRationalNumber] = {
    if (isZero) Set.empty
    else Set(DyadicRationalNumber(negativeOne, denominator))
  }
  override def incentives(player: Player): Iterable[DyadicRationalNumber] = {
    if (isInteger && sign != player.sign) {
      Set.empty           // This includes 0
    } else {
      Set(DyadicRationalNumber(negativeOne, denominator))
    }
  }
  override def isInfinitesimal = numerator == zero
  override def isInteger = denominator == one
  override def isNimber = numerator == zero
  override def isNumber = true
  override def isNumberish = true
  override def isNumberTiny = true
  override def leftStop = this
  override def mean = this
  override def rightStop = this
  override def stopCount = one
  override def temperature = DyadicRationalNumber(negativeOne, denominator)

  override def outcomeClass: OutcomeClass = super[RationalNumber].outcomeClass

  def blowup: DyadicRationalNumber = {
    if (this <= zero) {
      throw InvalidArgumentException("Exponent must be a nonnegative pseudonumber.")
    } else if (isInteger) {
      this - one
    } else if (step(-1) == zero) {
      DyadicRationalNumber.fromSection(None, Some(step(1).blowup))
    } else {
      DyadicRationalNumber.fromSection(step(-1).blowup, step(1).blowup)
    }
  }

  def min(other: DyadicRationalNumber) = if (this < other) this else other
  def max(other: DyadicRationalNumber) = if (this > other) this else other
  def mean(other: DyadicRationalNumber) = ((this + other) / two).asInstanceOf[DyadicRationalNumber]

  // TODO Make more efficient for smalls
  def denominatorExponent: Int = denominator.bigIntValue.lowestSetBit
  
  override def abs: DyadicRationalNumber = DyadicRationalNumber(numerator.abs, denominator)

  override def step(n: Int): DyadicRationalNumber = step(SmallInteger(n))
  override def step(n: Integer): DyadicRationalNumber = DyadicRationalNumber(numerator + n, denominator)

  override def toOutput = super[RationalNumber].toOutput

}

case class DyadicRationalNumberImpl(numerator: Integer, denominator: Integer) extends DyadicRationalNumber {

  assert(isDyadic && denominator != one)
  
}

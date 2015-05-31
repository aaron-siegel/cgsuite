/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

object DyadicRationalNumber {
  
  def apply(numerator: Integer, denominator: Integer): DyadicRationalNumber = {
    RationalNumber(numerator, denominator).asInstanceOf[DyadicRationalNumber]
  }

  def apply(numerator: Int, denominator: Int): DyadicRationalNumber = {
    apply(SmallInteger(numerator), SmallInteger(denominator))
  }
  
  val minSmall = apply(SmallInteger.minSmall, Values.one)
  val maxSmall = apply(SmallInteger.maxSmall, Values.one)

}

trait DyadicRationalNumber extends Uptimal with RationalNumber {

  def numberPart = this
  def upMultiplePart = 0
  def nimberPart = 0

  override def options(player: Player): Iterable[DyadicRationalNumber] = Set(step(-player.sign))

  // We need to do this to resolve ambiguities in inheriting compare
  def <=(other: DyadicRationalNumber) = super[RationalNumber].<=(other)
  def >=(other: DyadicRationalNumber) = super[RationalNumber].>=(other)
  def < (other: DyadicRationalNumber) = super[RationalNumber].< (other)
  def > (other: DyadicRationalNumber) = super[RationalNumber].> (other)

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

  override def birthday = {
    if (this >= Values.zero)
      ceiling + SmallInteger(denominatorExponent)
    else
      floor.abs + SmallInteger(denominatorExponent)
  }
  override def isInfinitesimal = numerator == Values.zero
  override def isInteger = denominator == Values.one
  override def isNimber = numerator == Values.zero
  override def isNumber = true
  override def isNumberish = true
  override def isNumberTiny = true
  override def leftStop = this
  override def mean = this
  override def rightStop = this
  override def stopCount = Values.one
  override def temperature = DyadicRationalNumber(Values.negativeOne, denominator)
  def min(other: DyadicRationalNumber) = if (this < other) this else other
  def max(other: DyadicRationalNumber) = if (this > other) this else other
  def mean(other: DyadicRationalNumber) = ((this + other) / Values.two).asInstanceOf[DyadicRationalNumber]

  // TODO Make more efficient for smalls
  def denominatorExponent: Int = denominator.bigIntValue.lowestSetBit
  
  override def abs: DyadicRationalNumber = DyadicRationalNumber(numerator.abs, denominator)

  override def step(n: Int): DyadicRationalNumber = step(SmallInteger(n))
  override def step(n: Integer): DyadicRationalNumber = DyadicRationalNumber(numerator + n, denominator)

  override def toOutput = super[RationalNumber].toOutput

}

case class DyadicRationalNumberImpl(numerator: Integer, denominator: Integer) extends DyadicRationalNumber {

  assert(isDyadic && denominator != Values.one)
  
}

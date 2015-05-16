/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

object Integer {
  
  private[core] val minInt: BigInt = BigInt(Int.MinValue)
  private[core] val maxInt: BigInt = BigInt(Int.MaxValue)
  private[core] val oneAsBigInt = BigInt(1)
  
  def apply(x: Long): Integer = {
    if (x >= Int.MinValue && x <= Int.MaxValue) {
      SmallInteger(x.toInt)
    } else {
      IntegerImpl(x)
    }
  }
  
  def apply(x: BigInt): Integer = {
    if (x >= minInt && x <= maxInt) {
      SmallInteger(x.toInt)
    } else {
      IntegerImpl(x)
    }
  }

  def parseInteger(str: String) = {
    if (str.length() <= 9) {
      SmallInteger(str.toInt)
    } else {
      Integer(BigInt(str))
    }
  }
  
}

trait Integer extends DyadicRationalNumber {
  
  def bigIntValue: BigInt
  override def intValue = bigIntValue.intValue()
  def byteValue = bigIntValue.byteValue()
  
  override def options(player: Player): Iterable[Integer] = {
    (player, bigIntValue.signum) match {
      case (Left, 1) => Set(Integer(bigIntValue-1))
      case (Right, -1) => Set(Integer(bigIntValue+1))
      case _ => Set.empty
    }
  }
  
  override def compare(other: RationalNumber) = other match {
    case i: Integer => bigIntValue.compare(i.bigIntValue)
    case _ => super.compare(other)
  }
  
  override def unary_- = Integer(-bigIntValue)
  
  def +(other: Integer) = Integer(bigIntValue + other.bigIntValue)
  def -(other: Integer) = Integer(bigIntValue - other.bigIntValue)
  def *(other: Integer) = Integer(bigIntValue * other.bigIntValue)
  def %(other: Integer) = Integer(bigIntValue % other.bigIntValue)

  def *(other: Game) = other.nCopies(this)
  
  def min(other: Integer) = if (this < other) this else other
  def max(other: Integer) = if (this > other) this else other

  def isEven = bigIntValue.testBit(0)
  override def isInteger = true
  def isSmallInteger = (bigIntValue >= Integer.minInt && bigIntValue <= Integer.maxInt)
  override def abs: Integer = Integer(bigIntValue.abs)
  
  def div(other: Integer) = Integer(bigIntValue / other.bigIntValue)

  override def pow(other: Integer): Integer = Integer(bigIntValue.pow(other.intValue))

  def gcd(other: Integer) = Integer(bigIntValue.gcd(other.bigIntValue))
  
  def sign = Integer(bigIntValue.signum)

  def isTwoPower = bigIntValue >= Integer.oneAsBigInt && bigIntValue.bitCount == 1
  
  def compare(other: Integer) = bigIntValue.compare(other.bigIntValue)
  
  override def numerator = this
  override def denominator = Values.one
  override def denominatorExponent = 0
  
}

case class IntegerImpl(bigIntValue: BigInt) extends Integer {
  
  assert(!isSmallInteger)
  
}

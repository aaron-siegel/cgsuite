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

  def pow2NimProduct(xExp: Int, yExp: Int): Integer = {
    var x = Values.two integerPow SmallInteger(xExp ^ yExp)
    val dup = xExp & yExp
    val bitLength = 32 - java.lang.Integer.numberOfLeadingZeros(dup)
    var n = 0
    while (n < bitLength) {
      val twoPow = dup & (1 << n)
      if (twoPow != 0)
        x = x.nimProduct(Values.three * Values.two.integerPow(SmallInteger(twoPow)) div Values.two)
      n += 1
    }
    x
  }
  
}

trait Integer extends DyadicRationalNumber {
  
  def bigIntValue: BigInt
  override def intValue = bigIntValue.intValue()
  def longValue = bigIntValue.longValue()
  def floatValue = bigIntValue.floatValue()
  def doubleValue = bigIntValue.doubleValue()
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

  def compare(other: Integer) = bigIntValue.compare(other.bigIntValue)

  override def unary_- = Integer(-bigIntValue)
  
  def +(other: Integer) = Integer(bigIntValue + other.bigIntValue)
  def -(other: Integer) = Integer(bigIntValue - other.bigIntValue)
  def *(other: Integer) = Integer(bigIntValue * other.bigIntValue)
  def %(other: Integer) = Integer(bigIntValue % other.bigIntValue)

  def *(other: CanonicalShortGame) = other.nCopies(this)
  def *(other: Game) = other.nCopies(this)

  override def abs: Integer = Integer(bigIntValue.abs)

  def div(other: Integer) = Integer(bigIntValue / other.bigIntValue)

  def gcd(other: Integer) = Integer(bigIntValue.gcd(other.bigIntValue))

  def integerPow(other: Integer): Integer = Integer(bigIntValue.pow(other.intValue))

  override def isEven = !bigIntValue.testBit(0)

  override def isInteger = true

  override def isOdd = bigIntValue.testBit(0)

  def isSmallInteger = bigIntValue >= Integer.minInt && bigIntValue <= Integer.maxInt

  def isTwoPower = bigIntValue >= Integer.oneAsBigInt && bigIntValue.bitCount == 1

  def lb = SmallInteger(bigIntValue.bitLength - 1)

  def min(other: Integer) = if (this < other) this else other

  def max(other: Integer) = if (this > other) this else other

  def nimProduct(other: Integer): Integer = {
    if (bigIntValue < 0 || other.bigIntValue < 0)
      throw new ArithmeticException("NimProduct applies only to nonnegative integers.")
    var m = 0
    var result: Integer = Values.zero
    while (m < bigIntValue.bitLength) {
      if (bigIntValue.testBit(m)) {
        var n = 0
        while (n < other.bigIntValue.bitLength) {
          if (other.bigIntValue.testBit(n)) {
            result = result.nimSum(Integer.pow2NimProduct(m, n))
          }
          n += 1
        }
      }
      m += 1
    }
    result
  }

  def nimSum(other: Integer) = {
    if (bigIntValue < 0 || other.bigIntValue < 0)
      throw new ArithmeticException("NimSum applies only to nonnegative integers.")
    Integer(bigIntValue ^ other.bigIntValue)
  }

  def sign = Integer(bigIntValue.signum)

  override def numerator = this
  override def denominator = Values.one
  override def denominatorExponent = 0

  override def toString = bigIntValue.toString
  
}

case class IntegerImpl(bigIntValue: BigInt) extends Integer {

  assert(!isSmallInteger)

}

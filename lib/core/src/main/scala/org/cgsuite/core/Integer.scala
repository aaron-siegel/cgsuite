/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.core.GeneralizedOrdinal.Term
import org.cgsuite.core.Values._
import org.cgsuite.exception.{ArithmeticException, EvalException, OverflowException}
import org.cgsuite.util.Coordinates

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
    var x = twoPower(xExp ^ yExp)
    val dup = xExp & yExp
    val bitLength = 32 - java.lang.Integer.numberOfLeadingZeros(dup)
    var n = 0
    while (n < bitLength) {
      val exponent = dup & (1 << n)
      if (exponent != 0)
        x = x.nimProduct(Values.three * twoPower(exponent) div Values.two)
      n += 1
    }
    x
  }

  def pow2UglyProduct(xExp: Int, yExp: Int): Integer = {
    if (xExp == 0)
      twoPower(yExp)
    else if (yExp == 0)
      twoPower(xExp)
    else if (xExp > yExp)
      twoPower(xExp) + twoPower(yExp - 1)
    else if (xExp < yExp)
      twoPower(yExp) + twoPower(xExp - 1)
    else // xExp == yExp
      twoPower(xExp - 1)
  }

  private[Integer] def twoPower(exponent: Int): Integer = {
    assert(exponent >= 0)
    if (exponent <= 30)
      SmallInteger(1 << exponent)
    else
      Integer(oneAsBigInt << exponent)
  }
  
}

trait Integer extends DyadicRationalNumber with GeneralizedOrdinal {
  
  def bigIntValue: BigInt
  override def intValue = {
    if (bigIntValue.bigInteger.bitLength() <= 32)
      bigIntValue.intValue()
    else
      throw OverflowException("Overflow.")
  }
  def longValue = bigIntValue.longValue()
  def floatValue = bigIntValue.floatValue()
  def doubleValue = bigIntValue.doubleValue()
  def byteValue = bigIntValue.byteValue()

  override def terms: IndexedSeq[Term] = {
    if (isZero)
      Vector.empty
    else
      Vector(Term(this, ZeroImpl))
  }
  
  override def options(player: Player): Iterable[Integer] = {
    (player, bigIntValue.signum) match {
      case (Left, 1) => Set(Integer(bigIntValue-1))
      case (Right, -1) => Set(Integer(bigIntValue+1))
      case _ => Set.empty
    }
  }
  
  override def compare(that: SurrealNumber): Int = {
    that match {
      case thatInteger: Integer => bigIntValue compare thatInteger.bigIntValue
      case _ => super.compare(that)
    }
  }

  override def unary_- = Integer(-bigIntValue)
  
  def +(other: Integer) = Integer(bigIntValue + other.bigIntValue)
  def -(other: Integer) = Integer(bigIntValue - other.bigIntValue)
  def *(other: Integer) = Integer(bigIntValue * other.bigIntValue)
  def %(other: Integer) = Integer(bigIntValue % other.bigIntValue)
  def ^(other: Integer) = Integer(bigIntValue ^ other.bigIntValue)

  // This override is necessary to resolve ambiguities:
  override def *(other: DyadicRationalNumber): DyadicRationalNumber = super[DyadicRationalNumber].*(other)

  def *(other: CanonicalShortGame): CanonicalShortGame = other.nCopies(this)
  def *(other: CanonicalStopper): StopperSidedValue = other.nCopies(this)
  def *(other: SidedValue): SidedValue = other.nCopies(this)
  def *(other: Coordinates): Coordinates = other * this
  def *(g: Game): CompoundGame = CompoundGame(ConwayProduct, this, g)

  override def exp(exponent: Integer) = super[DyadicRationalNumber].exp(exponent)

  override def abs: Integer = Integer(bigIntValue.abs)

  override def birthday: Integer = abs

  def div(other: Integer) = Integer(bigIntValue / other.bigIntValue)

  def gcd(other: Integer) = Integer(bigIntValue.gcd(other.bigIntValue))

  override def followerCount: Integer = abs + Values.one

  private[core] def intExp(other: Integer): Integer = {
    assert(other >= zero)
    if (other.isSmallInteger)
      Integer(bigIntValue.pow(other.intValue))
    else
      throw new EvalException("Overflow.")
  }

  override def isEven = !bigIntValue.testBit(0)

  def isEvil = bigIntValue.bitCount % 2 == 0

  override def isInteger = true

  def isOdious = bigIntValue.bitCount % 2 == 1

  override def isOrdinal = this >= Values.zero

  override def isOdd = bigIntValue.testBit(0)

  def isSmallInteger = bigIntValue >= Integer.minInt && bigIntValue <= Integer.maxInt

  def isTwoPower = bigIntValue >= Integer.oneAsBigInt && bigIntValue.bitCount == 1

  def isqrt: Integer = {
    if (this < zero)
      throw ArithmeticException(s"Argument to Isqrt is negative: $bigIntValue")
    else if (this < two)
      this
    else {
      val small = (this div four).isqrt * two
      val large = small + one
      if (large * large > this)
        small
      else
        large
    }
  }

  def lb = {
    if (bigIntValue <= 0)
      throw ArithmeticException(s"Argument to Lb is not strictly positive: $bigIntValue")
    else
      SmallInteger(bigIntValue.bitLength - 1)
  }

  def min(other: Integer) = if (this < other) this else other

  def max(other: Integer) = if (this > other) this else other

  def nimProduct(other: Integer): Integer = {
    if (bigIntValue < 0 || other.bigIntValue < 0)
      throw ArithmeticException("NimProduct applies only to nonnegative integers.")
    var result: Integer = Values.zero
    var m = 0
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
      throw ArithmeticException("NimSum applies only to nonnegative integers.")
    Integer(bigIntValue ^ other.bigIntValue)
  }

  override def sign = Integer(bigIntValue.signum)

  def uglyProduct(that: Integer) = {

    val a = this.bigIntValue
    val b = that.bigIntValue
    val thatIsOdious = that.isOdious

    if (a < 0 || b < 0)
      throw ArithmeticException("UglyProduct applies only to nonnegative integers.")

    var result: Integer = Values.zero
    var m = 0
    while (m < a.bitLength) {
      if (a testBit m) {
        var n = 0
        while (n < b.bitLength) {
          if (b testBit n) {
            result = result nimSum Integer.pow2UglyProduct(m, n)
          }
          n += 1
        }
      }
      m += 1
    }
    result

  }

  override def numerator = this
  override def denominator = Values.one
  override def denominatorExponent = 0

  override def toString = bigIntValue.toString

  override def toOutput = super[DyadicRationalNumber].toOutput

  // TODO There should be overflow validation here
  // TODO Expressions like ^(*3) parse, but shouldn't

  def toNimber = Nimber(this)

  def toUp = Uptimal(zero, intValue, 0)

  def toDown = Uptimal(zero, -intValue, 0)
  
}

case class IntegerImpl(bigIntValue: BigInt) extends Integer {

  assert(!isSmallInteger)

}

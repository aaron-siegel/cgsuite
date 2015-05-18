/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

trait CanonicalStopperSidedGame extends Game {

  def isZero = this == ZeroImpl

}

/*
sealed trait Integer extends DyadicRationalNumber with Ordered[Integer] {
  
  def smallValue: Int
  def bigValue: BigInt
  
  def +(n: Integer): Integer
  def -(n: Integer): Integer
  def *(n: Integer): Integer
  def div(n: Integer): Integer
  def gcd(n: Integer): Integer
  def sign: Integer

  def compare(n: Integer): Int
  
  def numerator = this
  def denominator = Integer.one

}

sealed trait Zero extends Integer with Nimber

object Integer {
    
  val zero = Integer(0)
  val one = Integer(1)
  
  private val minInt: BigInt = BigInt(Int.MinValue)
  private val maxInt: BigInt = BigInt(Int.MaxValue)
  
  def apply(x: Int): Integer = {
    if (x == 0) {
      ZeroImpl
    } else {
      SmallIntegerImpl(x)
    }
  }
  
  def apply(x: Long): Integer = {
    if (x >= Int.MinValue && x <= Int.MaxValue) {
      apply(x.toInt)
    } else {
      BigIntegerImpl(x)
    }
  }
  
  def apply(x: BigInt): Integer = {
    if (x >= minInt && x <= maxInt) {
      apply(x.toInt)
    } else {
      BigIntegerImpl(x)
    }
  }
  
  private[lang2] def smallGcd(a: Long, b: Long): Long = if (a == 0) b else smallGcd(b, a % b)
  
}

object ZeroImpl extends Zero {
  
  val smallValue = 0
  val bigValue = BigInt(0)
  
  def compare(n: Integer): Int = n match {
    case ZeroImpl => 0
    case SmallIntegerImpl(ns) => ns.signum
    case BigIntegerImpl(nb) => nb.signum
  }
  
  def +(n: Integer) = n
  def -(n: Integer) = n
  def *(n: Integer) = this
  def div(n: Integer) = n match {
    case ZeroImpl => throw new ArithmeticException("/ by zero")
    case _ => this
  }
  def gcd(n: Integer) = this
  val sign = this
  
}

case class SmallIntegerImpl(smallValue: Int) extends Integer {
  
  assert(smallValue != 0)
  
  def bigValue = BigInt(smallValue)
  
  def compare(n: Integer): Int = n match {
    case ZeroImpl => smallValue.signum
    case SmallIntegerImpl(ns) => smallValue.compare(ns)
    case BigIntegerImpl(nb) => nb.signum
  }
  
  def +(n: Integer) = protectedOp(n) { _ + smallValue } { _ + bigValue }
  def -(n: Integer) = protectedOp(n) { _ - smallValue } { _ - bigValue }
  def *(n: Integer) = protectedOp(n) { _ * smallValue } { _ * bigValue }
  def div(n: Integer) = protectedOp(n) { smallValue / _ } { bigValue / _ }
  def gcd(n: Integer) = protectedOp(n) { ns => Integer.smallGcd(smallValue, ns) } { nb => bigValue.gcd(nb) }
  def sign = Integer(smallValue.signum)
  
  private def protectedOp[S](n: Integer)(smallOp: Long => Long)(bigOp: BigInt => BigInt): Integer = {
    n match {
      case ZeroImpl => Integer(smallOp(0L))
      case SmallIntegerImpl(ns) => Integer(smallOp(ns.toLong))
      case BigIntegerImpl(nb) => Integer(bigOp(nb))
    }
  }
  
}

case class BigIntegerImpl(bigValue: BigInt) extends Integer {
  
  def smallValue = sys.error("no small vaue")
  
  def signum = bigValue.signum
  
  def compare(n: Integer): Int = n match {
    case BigIntegerImpl(nb) => bigValue.compare(nb)
    case _ => signum
  }
  
  def +(other: Integer) = Integer(bigValue + other.bigValue)
  def -(other: Integer) = Integer(bigValue - other.bigValue)
  def *(other: Integer) = Integer(bigValue * other.bigValue)
  def div(other: Integer) = Integer(bigValue / other.bigValue)
  def gcd(other: Integer) = Integer(bigValue.gcd(other.bigValue))
  def sign = Integer(bigValue.signum)
  
}
*/
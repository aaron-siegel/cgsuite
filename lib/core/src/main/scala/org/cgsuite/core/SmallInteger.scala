/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

object SmallInteger {

  def apply(x: Int): SmallInteger = x match {
    case 0 => ZeroImpl
    case _ => SmallIntegerImpl(x)
  }
  
  def gcd(a: Int, b: Int): Int = gcdR(a.abs, b.abs)
  
  private def gcdR(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  
  val minSmall = apply(Int.MinValue)
  val maxSmall = apply(Int.MaxValue)
  
}

trait SmallInteger extends Integer {
  
  def intValue: Int
  override def byteValue: Byte = intValue.toByte
  override def longValue: Long = intValue.toLong
  override def floatValue: Float = intValue.toFloat
  override def doubleValue: Double = intValue.toDouble
  def bigIntValue = BigInt(intValue)
  
  override def options(player: Player): Iterable[SmallInteger] = (player, intValue.signum) match {
    case (Left, 1) => Set(SmallInteger(intValue-1))
    case (Right, -1) => Set(SmallInteger(intValue+1))
    case _ => Set.empty
  }

  override def compare(other: RationalNumber) = other match {
    case small: SmallInteger => intValue.compare(small.intValue)
    case _ => super.compare(other)
  }
  
  override def compare(other: Integer) = other match {
    case small: SmallInteger => intValue.compare(small.intValue)
    case _ => super.compare(other)
  }

  override def unary_- = Integer(-longValue)
  
  override def +(other: Integer) = other match {
    case small: SmallInteger => Integer(longValue + small.longValue)
    case _ => super.+(other)
  }
  
  override def -(other: Integer) = other match {
    case small: SmallInteger => Integer(longValue - small.longValue)
    case _ => super.-(other)
  }
  
  override def *(other: Integer) = other match {
    case small: SmallInteger => Integer(longValue * small.longValue)
    case _ => super.*(other)
  }

  override def %(other: Integer) = other match {
    case small: SmallInteger => SmallInteger(intValue % other.intValue)
    case _ => super.%(other)
  }
    
  override def abs: SmallInteger = SmallInteger(intValue.abs)
  
  override def div(other: Integer) = other match {
    case small: SmallInteger => SmallInteger(intValue / small.intValue)
    case _ => super.div(other)
  }
  
  override def gcd(other: Integer) = other match {
    case small: SmallInteger => SmallInteger(SmallInteger.gcd(intValue, small.intValue))
    case _ => super.gcd(other)
  }

  override def lb = SmallInteger(31 - java.lang.Integer.numberOfLeadingZeros(intValue))

  override def nimSum(other: Integer) = other match {
    case small: SmallInteger =>
      if (intValue < 0 || other.intValue < 0)
        throw new ArithmeticException("NimSum applies only to nonnegative integers.")
      SmallInteger(intValue ^ other.intValue)
    case _ => super.nimSum(other)
  }

  override def isEven = intValue % 2 == 0

  override def isSmallInteger = true
  
  override def sign = SmallInteger(intValue.signum)
  
  override def isTwoPower = intValue >= 1 && java.lang.Integer.bitCount(intValue) == 1

  override def toString = intValue.toString

}

case class SmallIntegerImpl(override val intValue: Int) extends SmallInteger {
  
  assert(intValue != 0)
  
}

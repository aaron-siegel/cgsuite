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
  
  val minSmall = apply(java.lang.Integer.MIN_VALUE)
  val maxSmall = apply(java.lang.Integer.MAX_VALUE)
  
}

trait SmallInteger extends Integer {
  
  def intValue: Int
  override def byteValue: Byte = intValue.toByte
  def longValue: Long = intValue.toLong
  def bigIntValue = BigInt(intValue)
  
  override def options(player: Player): Iterable[SmallInteger] = (player, intValue.signum) match {
    case (Left, 1) => Iterable(SmallInteger(intValue-1))
    case (Right, -1) => Iterable(SmallInteger(intValue+1))
    case _ => Iterable.empty
  }
  
  override def compare(other: Integer) = other match {
    case small: SmallInteger => intValue - small.intValue
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

  override def isSmallInteger = true
  
  override def sign = SmallInteger(intValue.signum)
  
  override def isTwoPower = intValue >= 1 && java.lang.Integer.bitCount(intValue) == 1

  override def toString = intValue.toString

}

case class SmallIntegerImpl(override val intValue: Int) extends SmallInteger {
  
  assert(intValue != 0)
  
}

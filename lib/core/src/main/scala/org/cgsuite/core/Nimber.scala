/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

object Nimber {
  
  def apply(nimValue: Int): Nimber = nimValue match {
    case 0 => ZeroImpl
    case m if m < 0 => sys.error("nim value must be a positive integer")
    case _ => NimberImpl(nimValue)
  }
  
}

trait Nimber extends Uptimal {
  
  def nimValue: Int

  override lazy val uptimalExpansion = new UptimalExpansion(Values.zero, nimValue)
  override def numberPart = Values.zero
  override def nimberPart = nimValue
  def ordinalSum(that: Nimber) = Nimber(nimValue + that.nimValue)
  override def uptimalLength = 0
  override def uptimalCoefficient(n: Int) = 0

  def +(other: Nimber) = Nimber(nimValue ^ other.nimValue)
  def -(other: Nimber) = Nimber(nimValue ^ other.nimValue)
  override def unary_- = this

  override def options(player: Player): Iterable[Nimber] = {
    (0 until nimValue) map { Nimber(_) } toSet
  }
  
}

case class NimberImpl(nimValue: Int) extends Nimber {

  assert(nimValue > 0)
  
}

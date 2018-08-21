/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import java.util

import org.cgsuite.core.Values._
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

object RationalNumber {
  
  def apply(numerator: Integer, denominator: Integer): RationalNumber = {

    (numerator, denominator) match {
      case (_, ZeroImpl) =>
        if (numerator == zero)   // 0/0
          throw new ArithmeticException("/ by zero")
        else
          RationalNumberImpl(numerator.sign, denominator)
      case (ZeroImpl, _) => ZeroImpl
      case _ =>
        val gcd = numerator gcd denominator
        val newNumerator = (numerator div gcd) * denominator.sign
        val newDenominator = (denominator div gcd) * denominator.sign
        if (newDenominator == one) {
          newNumerator
        } else if (newDenominator.isTwoPower) {
          DyadicRationalNumberImpl(newNumerator, newDenominator)
        } else {
          RationalNumberImpl(newNumerator, newDenominator)
        }
    }
    
  }
  
  def apply(numerator: Int, denominator: Int): RationalNumber = {
    apply(SmallInteger(numerator), SmallInteger(denominator))
  }
  
}

trait RationalNumber extends SurrealNumber with OutputTarget {
  
  override def numerator: Integer

  override def denominator: Integer
  
  override def compare(that: SurrealNumber): Int = {
    that match {
      case thatRationalNumber: RationalNumber => compareRationalNumber(thatRationalNumber)
      case _ => super.compare(that)
    }
  }

  def compareRationalNumber(that: RationalNumber): Int = {
    if (hasZeroDenominator && that.hasZeroDenominator)
      numerator compare that.numerator
    else
      (numerator * that.denominator) compare (denominator * that.numerator)
  }

  override def unary_+ : RationalNumber = this

  override def unary_- : RationalNumber = RationalNumber(-numerator, denominator)

  def +(other: RationalNumber): RationalNumber = RationalNumber(
    numerator * other.denominator + denominator * other.numerator,
    denominator * other.denominator
    )

  def -(other: RationalNumber): RationalNumber = RationalNumber(
    numerator * other.denominator - denominator * other.numerator,
    denominator * other.denominator
  )

  def *(other: RationalNumber): RationalNumber = RationalNumber(
    numerator * other.numerator,
    denominator * other.denominator
  )

  def /(other: RationalNumber): RationalNumber = RationalNumber(
    numerator * other.denominator,
    denominator * other.numerator
  )

  def %(other: RationalNumber): RationalNumber = RationalNumber(
    (numerator * other.denominator) % (denominator * other.numerator),
    denominator * other.denominator
  )

  def intValue: Int = floor.intValue

  def min(other: RationalNumber) = if (this < other) this else other
  def max(other: RationalNumber) = if (this > other) this else other
  def mean(other: RationalNumber) = (this + other) / Values.two

  override def pow(other: Integer): RationalNumber = {
    if (other >= Values.zero) {
      RationalNumber(numerator.integerPow(other), denominator.integerPow(other))
    } else {
      RationalNumber(denominator.integerPow(-other), numerator.integerPow(-other))
    }
  }

  override def birthday: GeneralizedOrdinal = {
    assert(!isDyadic)
    if (denominator == zero)
      sys.error("inf has no birthday")
    else
      omega
  }

  override def reciprocal = RationalNumber(denominator, numerator)

  def isDyadic = denominator.isTwoPower
  def hasZeroDenominator = denominator == Values.zero

  override def abs: RationalNumber = if (this < zero) -this else this
  def floor: Integer = {
    if (isInteger)
      numerator
    else if (numerator >= Values.zero)
      numerator div denominator
    else
      ceiling - Values.one
  }
  def ceiling: Integer = {
    if (isInteger)
      numerator
    else if (numerator <= Values.zero)
      numerator div denominator
    else
      floor + Values.one
  }

  def step(n: Int): RationalNumber = step(SmallInteger(n))
  def step(n: Integer): RationalNumber = RationalNumber(numerator + n, denominator)

  override def toOutput: StyledTextOutput = {

    val output = new StyledTextOutput()

    if (hasZeroDenominator) {
      if (numerator < zero)
        output.appendMath("-")
      output.appendSymbol(StyledTextOutput.Symbol.INFINITY)
    } else if (isInteger) {
      output.appendMath(numerator.toString)
    } else {
      if (numerator < zero)
        output.appendMath("-")
      output.appendText(
        util.EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_NUMERATOR),
        numerator.abs.toString
      )
      output.appendMath("/")
      output.appendText(
        util.EnumSet.of(StyledTextOutput.Style.FACE_MATH, StyledTextOutput.Style.LOCATION_DENOMINATOR),
        denominator.toString
      )
    }

    output

  }
  
}

case class RationalNumberImpl(numerator: Integer, denominator: Integer) extends RationalNumber {

  assert(!isDyadic)
  
}

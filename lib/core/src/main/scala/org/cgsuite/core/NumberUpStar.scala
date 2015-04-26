package org.cgsuite.core

object NumberUpStar {

  def apply(number: DyadicRationalNumber, upMultiple: Int, nimber: Int): NumberUpStar = {
    if (upMultiple == 0 && nimber == 0) {
      number
    } else if (number == Values.zero && upMultiple == 0) {
      Nimber(nimber)
    } else if (nimber < 0) {
      sys.error("nim value must be a positive integer")
    } else {
      NumberUpStarImpl(number, upMultiple, nimber)
    }
  }

}

trait NumberUpStar extends CanonicalShortGame {

  def numberPart: DyadicRationalNumber
  def upMultiplePart: Int
  def nimberPart: Int

  def gameId = CanonicalShortGameOps.constructNus(numberPart, upMultiplePart, nimberPart)

  override def unary_- : NumberUpStar = NumberUpStar(-numberPart, -upMultiplePart, nimberPart)
  def +(other: NumberUpStar): NumberUpStar = {
    NumberUpStar(numberPart + other.numberPart, upMultiplePart + other.upMultiplePart, nimberPart ^ other.nimberPart)
  }
  def -(other: NumberUpStar): NumberUpStar = {
    NumberUpStar(numberPart - other.numberPart, upMultiplePart - other.upMultiplePart, nimberPart ^ other.nimberPart)
  }

  override def isInfinitesimal = numberPart == Values.zero
  override def isNimber = isInfinitesimal && upMultiplePart == 0
  override def isNumber = upMultiplePart == 0 && nimberPart == 0
  override def isNumberish = true
  override def isNumberTiny = false
  override def isNumberUpStar = true
  override def leftStop = numberPart
  override def rightStop = numberPart

  override def toString = {
    val s1 = numberPart match {
      case _: Zero => ""
      case _ => numberPart.toString
    }
    val s2 = upMultiplePart match {
      case 0 => ""
      case 1 => "^"
      case 2 => "^^"
      case x if x > 0 => "^" + x
      case -1 => "v"
      case -2 => "vv"
      case x if x < 0 => "v" + -x
    }
    val s3 = nimberPart match {
      case 0 => ""
      case 1 => "*"
      case x => "*" + x
    }
    s1 + s2 + s3
  }

}

case class NumberUpStarImpl(numberPart: DyadicRationalNumber, upMultiplePart: Int, nimberPart: Int) extends NumberUpStar {

  assert(!isNumber)
  assert(!isNimber)

}

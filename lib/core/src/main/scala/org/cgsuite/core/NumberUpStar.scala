package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput
import org.cgsuite.output.StyledTextOutput.Symbol._

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
  override def isUptimal = true
  override def leftStop = numberPart
  override def rightStop = numberPart
  override def uptimalExpansion: UptimalExpansion = new UptimalExpansion(numberPart, nimberPart, upMultiplePart)

  override private[core] def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {

    if (forceParens && !isNumber && !isNimber && !(numberPart == Values.zero && nimberPart == 0)) {
      // Not a number, nimber, or up multiple.  Force parens to clarify.
      output.appendMath("(")
    }
    if (numberPart != Values.zero || (nimberPart == 0 && upMultiplePart == 0)) {
      output.appendOutput(numberPart.toOutput)
    }
    if (upMultiplePart != 0) {
      val upSymbol = upMultiplePart match {
        case 2 => DOUBLE_UP
        case -2 => DOUBLE_DOWN
        case x if x > 0 => UP
        case _ => DOWN
      }
      output.appendSymbol(upSymbol)
      if (upMultiplePart.abs > 2) {
        output.appendMath(upMultiplePart.abs.toString)
      }
    }
    if (nimberPart != 0) {
      output.appendSymbol(STAR)
      if (nimberPart > 1) {
        output.appendMath(nimberPart.toString)
      }
    }
    if (forceParens && !isNumber && !isNimber && !(numberPart == Values.zero && nimberPart == 0)) {
      // Not a number, nimber, or up multiple.  Force parens to clarify.
      output.appendMath(")")
    }
    0

  }

}

case class NumberUpStarImpl(numberPart: DyadicRationalNumber, upMultiplePart: Int, nimberPart: Int) extends NumberUpStar {

  assert(!isNumber)
  assert(!isNimber)

}

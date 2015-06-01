package org.cgsuite.core

import org.cgsuite.exception.InputException
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.output.StyledTextOutput.Symbol._

object Uptimal {

  def apply(number: DyadicRationalNumber, upMultiple: Int, nimber: Int): Uptimal = {
    if (upMultiple == 0 && nimber == 0) {
      number
    } else if (number == Values.zero && upMultiple == 0) {
      Nimber(nimber)
    } else if (nimber < 0) {
      sys.error("nim value must be a positive integer")
    } else {
      UptimalImpl(number, upMultiple, nimber)
    }
  }

}

trait Uptimal extends CanonicalShortGame {

  def numberPart: DyadicRationalNumber
  def upMultiplePart: Int
  def nimberPart: Int

  def gameId = CanonicalShortGameOps.constructNus(numberPart, upMultiplePart, nimberPart)

  override def unary_- : Uptimal = Uptimal(-numberPart, -upMultiplePart, nimberPart)
  def +(other: Uptimal): Uptimal = {
    Uptimal(numberPart + other.numberPart, upMultiplePart + other.upMultiplePart, nimberPart ^ other.nimberPart)
  }
  def -(other: Uptimal): Uptimal = {
    Uptimal(numberPart - other.numberPart, upMultiplePart - other.upMultiplePart, nimberPart ^ other.nimberPart)
  }
          /*
  override def atomicWeight = {
    if (numberPart.isZero)
      SmallInteger(upMultiplePart)
    else
      throw InputException("That game is not atomic.")
  }      */
  override def companion = {
    if (numberPart.isZero && nimberPart <= 1)
      Uptimal(numberPart, upMultiplePart, nimberPart ^ 1)
    else
      this
  }
  override def isAllSmall = numberPart.isZero
  override def isAtomic = numberPart.isZero
  override def isEvenTempered = upMultiplePart == 0 && nimberPart == 0
  override def isInfinitesimal = numberPart.isZero
  override def isNimber = isInfinitesimal && upMultiplePart == 0
  override def isNumber = upMultiplePart == 0 && nimberPart == 0
  override def isNumberish = true
  override def isNumberTiny = false
  override def isNumberUpStar = true
  override def isOddTempered = upMultiplePart == 0 && nimberPart == 1
  override def isUptimal = true
  override def leftStop = numberPart
  override def reducedCanonicalForm = numberPart
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

case class UptimalImpl(numberPart: DyadicRationalNumber, upMultiplePart: Int, nimberPart: Int) extends Uptimal {

  assert(!isNumber)
  assert(!isNimber)

}

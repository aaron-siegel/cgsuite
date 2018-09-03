package org.cgsuite.core

import java.util

import org.cgsuite.exception.EvalException
import org.cgsuite.output.{Output, StyledTextOutput}
import org.cgsuite.output.StyledTextOutput.Style._
import org.cgsuite.output.StyledTextOutput.Symbol._

object Uptimal {

  def apply(uptimalExpansion: UptimalExpansion): Uptimal = {
    if (uptimalExpansion.length == 0 && uptimalExpansion.nimberPart == 0) {
      uptimalExpansion.getNumberPart
    } else if (uptimalExpansion.length == 0 && uptimalExpansion.getNumberPart.isZero) {
      Nimber(uptimalExpansion.nimberPart)
    } else {
      UptimalImpl(uptimalExpansion)
    }
  }

  def apply(number: DyadicRationalNumber, upMultiple: Int, nimber: Int): Uptimal = {
    if (upMultiple == 0 && nimber == 0) {
      number
    } else if (number == Values.zero && upMultiple == 0) {
      Nimber(nimber)
    } else if (nimber < 0) {
      sys.error("nim value must be a positive integer")
    } else {
      UptimalImpl(new UptimalExpansion(number, nimber, upMultiple))
    }
  }

}

trait Uptimal extends CanonicalShortGame {

  def uptimalExpansion: UptimalExpansion
  def numberPart = uptimalExpansion.getNumberPart
  def nimberPart = uptimalExpansion.nimberPart
  def uptimalLength = uptimalExpansion.length
  def uptimalCoefficient(n: Int) = uptimalExpansion.getCoefficient(n)

  lazy val gameId: Int = {
    if (uptimalExpansion.length() <= 1) {
      CanonicalShortGameOps.constructNus(numberPart, uptimalCoefficient(1), nimberPart)
    } else {
      CanonicalShortGameOps.constructUptimal(uptimalExpansion)
    }
  }

  override def unary_- : Uptimal = Uptimal(uptimalExpansion.negate)
  def +(other: Uptimal): Uptimal = Uptimal(uptimalExpansion add other.uptimalExpansion)
  def -(other: Uptimal): Uptimal = Uptimal(uptimalExpansion add other.uptimalExpansion.negate)

  override def atomicWeight = {
    if (numberPart.isZero)
      SmallInteger(uptimalCoefficient(1))
    else
      throw EvalException("That game is not atomic.")
  }
  override def companion = {
    if (numberPart.isZero && nimberPart <= 1)
      Uptimal(uptimalExpansion addNimber 1)
    else
      this
  }
  override def freeze = this
  override def isAllSmall = numberPart.isZero
  override def isAtomic = numberPart.isZero
  override def isEven: Boolean = {
    uptimalLength == 0 && nimberPart <= 1 && {
      numberPart match {
        case x: Integer => x.isEven == (nimberPart == 0)
        case _ => false
      }
    }
  }
  override def isEvenTempered = uptimalLength == 0 && nimberPart == 0
  override def isInfinitesimal = numberPart.isZero
  override def isNimber = isInfinitesimal && uptimalLength == 0
  override def isNumber = uptimalLength == 0 && nimberPart == 0
  override def isNumberish = true
  override def isNumberTiny: Boolean = false
  override def isNumberUpStar = true
  override def isOdd: Boolean = {
    uptimalLength == 0 && nimberPart <= 1 && {
      numberPart match {
        case x: Integer => x.isOdd == (nimberPart == 0)
        case _ => false
      }
    }
  }
  override def isOddTempered = uptimalLength == 0 && nimberPart == 1
  override def isUptimal = true
  override def leftStop = numberPart
  override def nCopies(n: Integer) = Uptimal(uptimalExpansion.nCopies(n))
  override def reducedCanonicalForm = numberPart
  override def rightStop = numberPart

  override private[core] def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {

    val isUnit = uptimalExpansion.isUnit
    val isUnitSum = uptimalExpansion.isUnitSum

    if (uptimalLength <= 1 || isUnit || isUnitSum) {

      val isCompact = isNumber || isNimber || numberPart.isZero && nimberPart == 0 && {
        uptimalLength == 1 || isUnit || isUnitSum
      }
      if (forceParens && !isCompact) {
        output.appendMath("(")
      }
      if (!numberPart.isZero || (nimberPart == 0 && uptimalLength == 0)) {
        output.appendOutput(numberPart.toOutput)
      }
      if (uptimalLength == 1) {
        val upSymbol = uptimalCoefficient(1) match {
          case 2 => DOUBLE_UP
          case -2 => DOUBLE_DOWN
          case x if x > 0 => UP
          case _ => DOWN
        }
        output.appendSymbol(upSymbol)
        if (uptimalCoefficient(1).abs > 2) {
          output.appendMath(uptimalCoefficient(1).abs.toString)
        }
      } else if (uptimalLength > 1 && (isUnit || isUnitSum)) {
        val (upSymbol, location) = {
          if (uptimalCoefficient(uptimalLength) > 0)
            (UP, LOCATION_SUBSCRIPT)
          else
            (DOWN, LOCATION_SUBSCRIPT)
        }
        val style = util.EnumSet.of(location, FACE_MATH)
        val bracketModes = if (isUnit) util.EnumSet.of(Output.Mode.PLAIN_TEXT) else util.EnumSet.allOf(classOf[Output.Mode])
        output.appendSymbol(upSymbol)
        output.appendText(style, bracketModes, if (isUnit) "<" else "[")
        output.appendText(style, uptimalLength.toString)
        output.appendText(style, bracketModes, if (isUnit) ">" else "]")
      }
      if (nimberPart != 0) {
        output.appendSymbol(STAR)
        if (nimberPart > 1) {
          output.appendMath(nimberPart.toString)
        }
      }
      if (forceParens && !isCompact) {
        output.appendMath(")")
      }
      0

    } else {
      super.appendTo(output, forceBrackets, forceParens)
    }
  }

}

case class UptimalImpl(uptimalExpansion: UptimalExpansion) extends Uptimal {

  assert(!isNumber)
  assert(!isNimber)

}

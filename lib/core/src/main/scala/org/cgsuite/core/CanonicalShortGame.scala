/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import java.util

import org.cgsuite.core.Values._
import org.cgsuite.core.{CanonicalShortGameOps => ops}
import org.cgsuite.exception.InputException
import org.cgsuite.output.StyledTextOutput.Symbol._
import org.cgsuite.output.{Output, StyledTextOutput}

object CanonicalShortGame {
  
  private[cgsuite] def apply(gameId: Int): CanonicalShortGame = {
    if (ops.isNumberUpStar(gameId)) {
      NumberUpStar(ops.getNumberPart(gameId), ops.getUpMultiplePart(gameId), ops.getNimberPart(gameId))
    } else {
      CanonicalShortGameImpl(gameId)
    }
  }
  
  def apply(leftOptions: Iterable[CanonicalShortGame], rightOptions: Iterable[CanonicalShortGame]): CanonicalShortGame = {
    val leftIds: Array[Int] = leftOptions.map { _.gameId }.toArray
    val rightIds: Array[Int] = rightOptions.map { _.gameId }.toArray
    CanonicalShortGame(ops.constructFromOptions(leftIds, rightIds))
  }
  
  def apply(leftOptions: CanonicalShortGame*)(rightOptions: CanonicalShortGame*): CanonicalShortGame = {
    apply(leftOptions.toIterable, rightOptions.toIterable)
  }

  val maxSlashes = 5
  val slashString: IndexedSeq[String] = (0 to maxSlashes) map { n =>
    (1 to n) map { _ => "|" } mkString ""
  }

}

trait CanonicalShortGame extends CanonicalStopperGame {

  def gameId: Int

  override def unary_- = CanonicalShortGame(ops.getNegative(gameId))

  def +(other: CanonicalShortGame) = CanonicalShortGame(ops.add(gameId, other.gameId))

  def -(other: CanonicalShortGame) = CanonicalShortGame(ops.subtract(gameId, other.gameId))

  override def nCopies(n: Integer) = n.nortonMultiply(this)

  def <=(other: CanonicalShortGame) = ops.leq(gameId, other.gameId)

  def >=(other: CanonicalShortGame) = other <= this

  def <(other: CanonicalShortGame) = this <= other && !(other <= this)

  def >(other: CanonicalShortGame) = !(this <= other) && other <= this

  override def options(player: Player): Iterable[CanonicalShortGame] = {
    player match {
      case Left => (0 until ops.getNumLeftOptions(gameId)) map { n =>
        CanonicalShortGame(ops.getLeftOption(gameId, n))
      }
      case Right => (0 until ops.getNumRightOptions(gameId)) map { n =>
        CanonicalShortGame(ops.getRightOption(gameId, n))
      }
    }
  }

  def sortedOptions(player: Player): Seq[CanonicalShortGame] = {
    options(player).toSeq
  }

  def birthday: Integer = SmallInteger(ops.birthday(gameId))

  def cool(t: DyadicRationalNumber): CanonicalShortGame = CanonicalShortGame(ops.cool(gameId, t, t.gameId))

  def isInfinitesimal: Boolean = leftStop == Values.zero && rightStop == Values.zero

  def isInteger: Boolean = ops.isInteger(gameId)

  def isNimber: Boolean = ops.isNimber(gameId)

  def isNumber: Boolean = ops.isNumber(gameId)

  def isNumberish: Boolean = leftStop == rightStop

  def isNumberTiny: Boolean = {
    val gl = options(Left)
    val gr = options(Right)
    if (gl.size == 1 && gr.size == 1 && gl.head.isNumber) {
      val grl = gr.head.options(Left)
      val grr = gr.head.options(Right)
      grl.size == 1 && grr.size == 1 && gl.head == grl.head && grr.head.leftStop < gl.head.leftStop
    } else {
      false
    }
  }

  def isNumberUpStar: Boolean = ops.isNumberUpStar(gameId)

  def isSwitch: Boolean = this == -this

  def leftStop: DyadicRationalNumber = ops.leftStop(gameId)

  def mean: DyadicRationalNumber = ops.mean(gameId)

  def nortonMultiply(that: CanonicalShortGame) = CanonicalShortGame(ops.nortonMultiply(gameId, that.gameId))

  def rightStop: DyadicRationalNumber = ops.rightStop(gameId)

  def stopCount: Integer = Integer(ops.stopCount(gameId))

  def temperature: DyadicRationalNumber = ops.temperature(gameId)

  def thermograph: Thermograph = ops.thermograph(gameId)

  override def toString = toOutput.toString

  def toOutput: StyledTextOutput = {
    val sto = new StyledTextOutput()
    appendTo(sto, true, false)
    sto
  }

  protected def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {

    if (Thread.interrupted()) {
      throw new InputException("Calculation canceled by user.")
    }

    val negative = -this
    val lo = sortedOptions(Left)
    val ro = sortedOptions(Right)

    if (isSwitch) {

      output.appendSymbol(PLUS_MINUS)
      if (lo.size > 1)
        output.appendMath("{")
      lo.head.appendTo(output, true, lo.size == 1)
      lo.tail.foreach { gl => output.appendMath(","); gl.appendTo(output, true, false) }
      if (lo.size > 1)
        output.appendMath("}")
      0

    } else if (isNumberTiny || negative.isNumberTiny) {

      val (str, translate, subscript) = {
        if (isNumberTiny)
          ("Tiny", lo.head, -ro.head.options(Right).head + lo.head)
        else
          ("Miny", ro.head, lo.head.options(Left).head - ro.head)
      }
      if (forceParens)
        output.appendMath("(")
      if (translate != zero) {
        translate.appendTo(output, false, false)
        output.appendText(Output.Mode.PLAIN_TEXT, "+")
      }
      val sub = new StyledTextOutput()
      subscript.appendTo(sub, true, true)
      val styles = sub.allStyles()
      styles.retainAll(StyledTextOutput.Style.TRUE_LOCATIONS)
      if (styles.isEmpty) {
        if (subscript.isNumber && !subscript.isInteger) {
          output.appendText(Output.Mode.PLAIN_TEXT, "(")
        }
        output.appendSymbol(
          util.EnumSet.noneOf(classOf[StyledTextOutput.Style]),
          util.EnumSet.complementOf(util.EnumSet.of(Output.Mode.PLAIN_TEXT)),
          if (isNumberTiny) TINY else MINY
        )
        output.appendOutput(util.EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT), sub)
        if (subscript.isNumber && !subscript.isInteger) {
          output.appendText(Output.Mode.PLAIN_TEXT, ")")
        }
        output.appendText(Output.Mode.PLAIN_TEXT, "." + str)
      } else {
        output.appendOutput(sub)
        output.appendMath("." + str)
      }
      if (forceParens) {
        output.appendMath(")")
      }
      0

    } else {

      // TODO Uptimals

      val leftOutput = new StyledTextOutput()
      val rightOutput = new StyledTextOutput()

      // First we build the left & right OS's and calculate the number of slashes.
      // There are several cases.

      val numSlashesL1 = lo.head.appendTo(leftOutput, lo.size > 1, false) + 1
      val numSlashesL = lo.tail.foldLeft(numSlashesL1) { (numSlashes, gl) =>
        leftOutput.appendMath(",")
        numSlashes.max(gl.appendTo(leftOutput, lo.size > 1, false) + 1)
      }

      val numSlashesR1 = ro.head.appendTo(rightOutput, ro.size > 1, false) + 1
      val numSlashesR = ro.tail.foldLeft(numSlashesR1) { (numSlashes, gr) =>
        rightOutput.appendMath(",")
        numSlashes.max(gr.appendTo(rightOutput, ro.size > 1, false) + 1)
      }

      val numSlashes = numSlashesL.max(numSlashesR)
      if (forceBrackets || numSlashes == CanonicalShortGame.maxSlashes) {
        output.appendMath("{")
      }
      output.appendOutput(leftOutput)
      output.appendMath(CanonicalShortGame.slashString(numSlashes))
      output.appendOutput(rightOutput)
      if (forceBrackets || numSlashes == CanonicalShortGame.maxSlashes) {
        output.appendMath("}")
        0
      } else {
        numSlashes
      }

    }

  }

}

case class CanonicalShortGameImpl(gameId: Int) extends CanonicalShortGame {
  
  assert(!isNumberUpStar)
  
}

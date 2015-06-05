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

import scala.collection.mutable

object CanonicalShortGame {
  
  private[cgsuite] def apply(gameId: Int): CanonicalShortGame = {
    if (ops.isNumberUpStar(gameId)) {
      Uptimal(ops.getNumberPart(gameId), ops.getUpMultiplePart(gameId), ops.getNimberPart(gameId))
    } else {
      val uptimalExpansion = ops.uptimalExpansion(gameId)
      if (uptimalExpansion != null)
        Uptimal(uptimalExpansion)
      else
        CanonicalShortGameImpl(gameId)
    }
  }
  
  def apply(lo: Iterable[CanonicalShortGame], ro: Iterable[CanonicalShortGame]): CanonicalShortGame = {
    val leftIds: Array[Int] = lo.map { _.gameId }.toArray
    val rightIds: Array[Int] = ro.map { _.gameId }.toArray
    CanonicalShortGame(ops.constructFromOptions(leftIds, rightIds))
  }
  
  def apply(lo: CanonicalShortGame*)(ro: CanonicalShortGame*): CanonicalShortGame = apply(lo, ro)

  val maxSlashes = 4
  val slashString: IndexedSeq[String] = (0 to maxSlashes) map { n =>
    (1 to n) map { _ => "|" } mkString ""
  }

  object DeterministicOrdering extends Ordering[CanonicalShortGame] {

    def compare(g: CanonicalShortGame, h: CanonicalShortGame): Int = {
      (g, h) match {
        case (a: DyadicRationalNumber, b: DyadicRationalNumber) => a compare b
        case (_: DyadicRationalNumber, _) => -1
        case (_, _: DyadicRationalNumber) => 1
        case (a: Nimber, b: Nimber) => a.nimValue - b.nimValue
        case (_: Nimber, _) => -1
        case (_, _: Nimber) => 1
        case (a: Uptimal, b: Uptimal) => a.uptimalExpansion compareTo b.uptimalExpansion
        case (a: Uptimal, _) => -1
        case (_, b: Uptimal) => 1
        case (_, _) =>
          val cmp = compareLists(g.sortedOptions(Left), h.sortedOptions(Left))
          if (cmp != 0)
            cmp
          else
            compareLists(g.sortedOptions(Right), h.sortedOptions(Right))
      }
    }

    def compareLists(a: Iterable[CanonicalShortGame], b: Iterable[CanonicalShortGame]): Int = {
      var cmp = 0
      val aIt = a.iterator
      val bIt = b.iterator
      while (cmp == 0 && (aIt.hasNext || bIt.hasNext)) {
        if (aIt.hasNext) {
          if (bIt.hasNext) {
            cmp = compare(aIt.next(), bIt.next())
          } else {
            cmp = 1
          }
        } else {
          cmp = -1
        }
      }
      cmp
    }

  }
}

trait CanonicalShortGame extends CanonicalStopperGame {

  def gameId: Int

  def loopyGame = new LoopyGame(this)

  override def unary_- = CanonicalShortGame(ops.getNegative(gameId))

  override def +(other: CanonicalShortGame) = CanonicalShortGame(ops.add(gameId, other.gameId))

  override def -(other: CanonicalShortGame) = CanonicalShortGame(ops.subtract(gameId, other.gameId))

  override def nCopies(n: Integer) = n.nortonMultiply(this)

  def <=(other: CanonicalShortGame) = ops.leq(gameId, other.gameId)

  def >=(other: CanonicalShortGame) = other <= this

  def <(other: CanonicalShortGame) = this <= other && !(other <= this)

  def >(other: CanonicalShortGame) = !(this <= other) && other <= this

  override def options(player: Player): Iterable[CanonicalShortGame] = {
    player match {
      case Left => (0 until ops.getNumLeftOptions(gameId)) map { n =>
        CanonicalShortGame(ops.getLeftOption(gameId, n))
      } toSet
      case Right => (0 until ops.getNumRightOptions(gameId)) map { n =>
        CanonicalShortGame(ops.getRightOption(gameId, n))
      } toSet
    }
  }

  override def sortedOptions(player: Player): Seq[CanonicalShortGame] = {
    options(player).toSeq.sorted(CanonicalShortGame.DeterministicOrdering)
  }

  def atomicWeight = atomicWeightOpt getOrElse { throw InputException("That game is not atomic.") }

  private[cgsuite] lazy val atomicWeightOpt: Option[CanonicalShortGame] = {
    if (!isInfinitesimal)
      None
    else {
      val naiveAtomicWeightId = ops.naiveAtomicWeight(gameId)
      val isAtomic = isAllSmall || {
        val difference = ops.subtract(gameId, ops.nortonMultiply(naiveAtomicWeightId, ops.UP_ID))
        val farStar = ops.farStar(gameId)
        var nextPow2 = 2
        while (nextPow2 < farStar)
          nextPow2 <<= 1
        val redKite = ops.ordinalSum(ops.constructNus(zero, 0, nextPow2), ops.NEGATIVE_ONE_ID)
        val epsilon = ops.add(ops.UP_STAR_ID, redKite)
        ops.leq(difference, epsilon) && ops.leq(ops.getNegative(epsilon), difference)
      }
      if (isAtomic)
        Some(CanonicalShortGame(naiveAtomicWeightId))
      else
        None
    }
  }

  def birthday: Integer = SmallInteger(ops.birthday(gameId))

  def companion: CanonicalShortGame = CanonicalShortGame(ops.companion(gameId))

  def conwayMultiply(h: CanonicalShortGame) = CanonicalShortGame(ops.conwayMultiply(gameId, h.gameId))

  def cool(t: DyadicRationalNumber): CanonicalShortGame = {
    if (t <= Values.negativeOne) {
      throw InputException(s"Invalid cooling temperature (must be > -1): $t")
    }
    CanonicalShortGame(ops.cool(gameId, t, t.gameId))
  }

  def heat(t: CanonicalShortGame): CanonicalShortGame = CanonicalShortGame(ops.heat(gameId, t.gameId))

  def incentives: Iterable[CanonicalShortGame] = {
    ops.incentives(gameId, true, true) map { CanonicalShortGame(_) } toSet
  }

  def incentives(player: Player): Iterable[CanonicalShortGame] = {
    ops.incentives(gameId, player == Left, player == Right) map { CanonicalShortGame(_) } toSet
  }

  def isAllSmall: Boolean = ops.isAllSmall(gameId)

  def isAtomic: Boolean = atomicWeightOpt.isDefined

  def isEven: Boolean = ops.isEven(gameId)

  def isEvenTempered: Boolean = ops.isEvenTempered(gameId)

  def isInfinitesimal: Boolean = leftStop.isZero && rightStop.isZero

  override def isInteger: Boolean = ops.isInteger(gameId)

  override def isLoopfree = true

  def isNimber: Boolean = ops.isNimber(gameId)

  override def isNumber: Boolean = ops.isNumber(gameId)

  def isNumberish: Boolean = leftStop == rightStop

  override def isNumberTiny: Boolean = {
    val lo = options(Left)
    val ro = options(Right)
    lo.size == 1 && ro.size == 1 && (
      lo.head.isNumber && {
        val rlo = ro.head.options(Left)
        val rro = ro.head.options(Right)
        rlo.size == 1 && rro.size == 1 && lo.head == rlo.head && rro.head.mean < lo.head.mean
      } ||
      ro.head.isNumber && {
        val llo = lo.head.options(Left)
        val lro = lo.head.options(Right)
        lro.size == 1 && llo.size == 1 && ro.head == lro.head && llo.head.mean > ro.head.mean
      }
    )
  }

  def isNumberUpStar: Boolean = ops.isNumberUpStar(gameId)

  def isOdd: Boolean = ops.isOdd(gameId)

  def isOddTempered: Boolean = ops.isOddTempered(gameId)

  def isSwitch: Boolean = this == -this

  def isUptimal: Boolean = ops.uptimalExpansion(gameId) != null

  def leftIncentives = incentives(Left)

  def leftStop: DyadicRationalNumber = ops.leftStop(gameId)

  def mean: DyadicRationalNumber = ops.mean(gameId)

  def nortonMultiply(that: CanonicalShortGame) = CanonicalShortGame(ops.nortonMultiply(gameId, that.gameId))

  def ordinalSum(that: CanonicalShortGame) = CanonicalShortGame(ops.ordinalSum(gameId, that.gameId))

  def overheat(s: CanonicalShortGame, t: CanonicalShortGame): CanonicalShortGame = {
    CanonicalShortGame(ops.overheat(gameId, s.gameId, t.gameId))
  }

  def pow(n: Int) = CanonicalShortGame(ops.pow(gameId, n))

  def powTo(n: Int) = CanonicalShortGame(ops.powTo(gameId, n))

  def reducedCanonicalForm: CanonicalShortGame = CanonicalShortGame(ops.rcf(gameId))

  def rightIncentives = incentives(Right)

  def rightStop: DyadicRationalNumber = ops.rightStop(gameId)

  def stopCount: Integer = Integer(ops.stopCount(gameId))

  def temperature: DyadicRationalNumber = ops.temperature(gameId)

  def thermograph: Thermograph = ops.thermograph(gameId)

  override def toOutput: StyledTextOutput = {
    val sto = new StyledTextOutput()
    appendTo(sto, true, false)
    sto
  }

  override private[core] def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {

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

    } else if (isNumberTiny) {

      val (str, translate, subscript) = {
        if (lo.head.isNumber)
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

  override private[core] def appendTo(
    output: StyledTextOutput,
    forceBrackets: Boolean,
    forceParens: Boolean,
    nodeStack: mutable.Map[CanonicalStopperGame, Option[String]],
    numNamedNodes: Array[Int]
    ): Int = {

      appendTo(output, forceBrackets, forceParens)

  }

}

case class CanonicalShortGameImpl(gameId: Int) extends CanonicalShortGame {

  assert(!isNumberUpStar)

}

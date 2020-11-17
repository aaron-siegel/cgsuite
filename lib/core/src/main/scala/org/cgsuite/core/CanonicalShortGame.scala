/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import java.util

import org.cgsuite.core.Values._
import org.cgsuite.core.{CanonicalShortGameOps => ops}
import org.cgsuite.exception.{CalculationCanceledException, InvalidOperationException, NotAtomicException, NotUptimalException}
import org.cgsuite.output.StyledTextOutput.Symbol._
import org.cgsuite.output.{Output, StyledTextOutput}
import org.cgsuite.util.TranspositionCache

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.language.postfixOps

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

  def gameCount = CanonicalShortGameOps.getNumGames

  object DeterministicOrdering extends Ordering[CanonicalShortGame] {

    def compare(g: CanonicalShortGame, h: CanonicalShortGame): Int = {
      (g, h) match {
        case (a: DyadicRationalNumber, b: DyadicRationalNumber) => a compare b
        case (_: DyadicRationalNumber, _) => -1
        case (_, _: DyadicRationalNumber) => 1
        case (a: Nimber, b: Nimber) => a.intNimValue - b.intNimValue
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

trait CanonicalShortGame extends CanonicalStopper {

  def gameId: Int

  def loopyGame = new LoopyGame(this)

  override def unary_- = CanonicalShortGame(ops.getNegative(gameId))

  override def +(other: CanonicalShortGame) = CanonicalShortGame(ops.add(gameId, other.gameId))

  override def -(other: CanonicalShortGame) = CanonicalShortGame(ops.subtract(gameId, other.gameId))

  override def nCopies(n: Integer): CanonicalShortGame = n nortonMultiply this

  def <=(other: CanonicalShortGame) = ops.leq(gameId, other.gameId)

  def >=(other: CanonicalShortGame) = other <= this

  def <(other: CanonicalShortGame) = this <= other && !(other <= this)

  def >(other: CanonicalShortGame) = !(this <= other) && other <= this

  override def optionsFor(player: Player): Iterable[CanonicalShortGame] = {
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
    optionsFor(player).toSeq.sorted(CanonicalShortGame.DeterministicOrdering)
  }

  override def canonicalForm = this

  override def canonicalForm(tc: TranspositionCache) = this

  def atomicWeight = {
    atomicWeightOpt getOrElse {
      throw NotAtomicException(s"That game is not atomic.")
    }
  }

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

  def conwayProduct(that: CanonicalShortGame): CanonicalShortGame = CanonicalShortGame(ops.conwayMultiply(gameId, that.gameId))

  override def conwayProduct(that: Game): Game = {
    that match {
      case thatCanonicalShortGame: CanonicalShortGame => conwayProduct(thatCanonicalShortGame)
      case _ => super.conwayProduct(that)
    }
  }

  def cool(t: DyadicRationalNumber): CanonicalShortGame = {
    if (t <= Values.negativeOne) {
      throw InvalidOperationException(s"Invalid cooling temperature (must be > -1): $t")
    }
    CanonicalShortGame(ops.cool(gameId, t, t.gameId))
  }

  override def degree = zero

  override def followerCount: Integer = SmallInteger(ops.followerCount(gameId))

  override def followers = ops.followerIds(gameId) map { CanonicalShortGame(_) } toSet

  def freeze = cool(temperature)

  def heat(t: CanonicalShortGame): CanonicalShortGame = CanonicalShortGame(ops.heat(gameId, t.gameId))

  def incentives: Iterable[CanonicalShortGame] = {
    ops.incentives(gameId, true, true) map { CanonicalShortGame(_) } toSet
  }

  def incentives(player: Player): Iterable[CanonicalShortGame] = {
    ops.incentives(gameId, player == Left, player == Right) map { CanonicalShortGame(_) } toSet
  }

  override def isAllSmall: Boolean = ops.isAllSmall(gameId)

  def isAtomic: Boolean = atomicWeightOpt.isDefined

  def isEven: Boolean = ops.isEven(gameId)

  def isEvenTempered: Boolean = ops.isEvenTempered(gameId)

  override def isIdempotent = isZero      // 0 is the only loopfree idempotent

  override def isInfinitesimal = leftStop == Values.zero && rightStop == Values.zero

  override def isInteger: Boolean = ops.isInteger(gameId)

  override def isLoopfree = true

  override def isNimber: Boolean = ops.isNimber(gameId)

  override def isNumber: Boolean = ops.isNumber(gameId)

  override def isNumberish: Boolean = leftStop == rightStop

  override def isNumberTiny: Boolean = {
    val lo = optionsFor(Left)
    val ro = optionsFor(Right)
    lo.size == 1 && ro.size == 1 && (
      lo.head.isNumber && {
        val rlo = ro.head.optionsFor(Left)
        val rro = ro.head.optionsFor(Right)
        rlo.size == 1 && rro.size == 1 && lo.head == rlo.head && rro.head.mean < lo.head.mean
      } ||
      ro.head.isNumber && {
        val llo = lo.head.optionsFor(Left)
        val lro = lo.head.optionsFor(Right)
        lro.size == 1 && llo.size == 1 && ro.head == lro.head && llo.head.mean > ro.head.mean
      }
    )
  }

  def isNumberUpStar: Boolean = ops.isNumberUpStar(gameId)

  def isOdd: Boolean = ops.isOdd(gameId)

  def isOddTempered: Boolean = ops.isOddTempered(gameId)

  override def isPlumtree = true

  override def isUptimal: Boolean = ops.uptimalExpansion(gameId) != null

  def leftIncentives = incentives(Left)

  override def leftStop: DyadicRationalNumber = ops.leftStop(gameId)

  def mean: DyadicRationalNumber = ops.mean(gameId)

  def nortonMultiply(that: CanonicalShortGame) = CanonicalShortGame(ops.nortonMultiply(gameId, that.gameId))

  def ordinalSum(that: CanonicalShortGame) = CanonicalShortGame(ops.ordinalSum(gameId, that.gameId))

  def overheat(s: CanonicalShortGame, t: CanonicalShortGame): CanonicalShortGame = {
    CanonicalShortGame(ops.overheat(gameId, s.gameId, t.gameId))
  }

  def pow(x: Pseudonumber): CanonicalStopper = {
    val lo = optionsFor(Left)
    val ro = optionsFor(Right)
    if (lo.size == 1 && ro.size == 1 && lo.head == zero) {
      x match {
        case r: DyadicRationalNumber => CanonicalShortGame(zero)(-powTo(r - one) + ro.head)
        case _ => CanonicalStopper(zero)(-powTo((x - one).asInstanceOf[Pseudonumber]) + ro.head)
      }
    } else {
      throw InvalidOperationException(s"Invalid base for `Pow` operation (base must be of the form {0|H}).")
    }
  }

  def powTo(x: Pseudonumber): CanonicalStopper = {
    x match {
      case r: DyadicRationalNumber => powTo(r)
      case _ => ordinalSum(x.blowup)
    }
  }

  def powTo(x: DyadicRationalNumber): CanonicalShortGame = {
    if (x.isZero) zero else ordinalSum(x.blowup)
  }

  def reducedCanonicalForm: CanonicalShortGame = CanonicalShortGame(ops.rcf(gameId))

  def rightIncentives = incentives(Right)

  override def rightStop: DyadicRationalNumber = ops.rightStop(gameId)

  def stopCount: Integer = Integer(ops.stopCount(gameId))

  override def switch: CanonicalShortGame = CanonicalShortGame(this)(-this)

  def temperature: DyadicRationalNumber = ops.temperature(gameId)

  def thermograph: Thermograph = ops.thermograph(gameId)

  def trajectory(player: Player): Trajectory = if (player == Left) thermograph.getLeftWall else thermograph.getRightWall

  override def variety = zero

  override def toString: String = toOutput.toString

  override def toOutput: StyledTextOutput = {
    val sto = new StyledTextOutput()
    appendTo(sto, true, false)
    sto
  }

  override private[core] def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {

    if (Thread.interrupted()) {
      throw CalculationCanceledException("Calculation canceled by user.")
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

      val (str, symbol, translate, subscript) = {
        if (lo.head.isNumber)
          ("Tiny", TINY, lo.head, -ro.head.optionsFor(Right).head + lo.head)
        else
          ("Miny", MINY, ro.head, lo.head.optionsFor(Left).head - ro.head)
      }
      if (forceParens)
        output.appendMath("(")
      if (translate != zero)
        translate.appendTo(output, false, false)
      val sub = new StyledTextOutput()
      subscript.appendTo(sub, true, false)
      val styles = sub.allStyles()
      styles.retainAll(StyledTextOutput.Style.TRUE_LOCATIONS)
      if (styles.isEmpty) {
        output.appendSymbol(
          util.EnumSet.of(StyledTextOutput.Style.FACE_MATH),
          util.EnumSet.of(Output.Mode.GRAPHICAL),
          symbol
        )
        output.appendText(
          util.EnumSet.of(StyledTextOutput.Style.FACE_MATH),
          util.EnumSet.of(Output.Mode.PLAIN_TEXT),
          str + "("
        )
        output.appendOutput(util.EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT), sub)
        output.appendText(
          util.EnumSet.of(StyledTextOutput.Style.FACE_MATH),
          util.EnumSet.of(Output.Mode.PLAIN_TEXT),
          ")"
        )
      } else {
        output.appendMath(str + "(")
        output.appendOutput(sub)
        output.appendMath(")")
      }
      if (forceParens)
        output.appendMath(")")
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
    nodeStack: mutable.Map[SimplifiedLoopyGame, Option[String]],
    numNamedNodes: Array[Int]
    ): Int = {

      appendTo(output, forceBrackets, forceParens)

  }

}

case class CanonicalShortGameImpl(gameId: Int) extends CanonicalShortGame {

  assert(gameId >= 0, gameId)
  assert(!isNumberUpStar)

}

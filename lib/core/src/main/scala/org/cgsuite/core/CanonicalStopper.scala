package org.cgsuite.core

import java.util

import org.cgsuite.core.CanonicalStopper._
import org.cgsuite.core.Values._
import org.cgsuite.exception.{CounterexampleException, InvalidOperationException, NotStopperException}
import org.cgsuite.output.StyledTextOutput.Symbol._
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}
import org.cgsuite.util.TranspositionCache

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object CanonicalStopper {

  def apply(loopyGame: LoopyGame): CanonicalStopper = {
    if (loopyGame.isLoopfree) {
      toCanonicalShortGame(loopyGame)
    } else if (loopyGame.isStopper) {
      val canonical = loopyGame.canonicalizeStopperInternal()
      if (canonical.isLoopfree)
        toCanonicalShortGame(canonical)
      else {
        Option(canonical.asPseudonumber) match {
          case Some(p) => p
          case None => CanonicalStopperImpl(canonical)
        }
      }
    } else {
      throw NotStopperException(s"Not a stopper: $loopyGame")
    }
  }

  def apply(lo: Iterable[CanonicalStopper], ro: Iterable[CanonicalStopper], withPass: Option[Player] = None): CanonicalStopper = {
    val loopyGame = SimplifiedLoopyGame.constructLoopyGame(lo, ro, withPass.toSet)
    CanonicalStopper(loopyGame)
  }

  def apply(lo: CanonicalStopper*)(ro: CanonicalStopper*): CanonicalStopper = apply(lo, ro)

  private[core] def nthName(n: Int) = {
    if (n < 26)
      ('a' + n).toChar.toString
    else
      "N" + (n - 25)
  }

  private[core] def toCanonicalShortGame(loopyGame: LoopyGame): CanonicalShortGame = {
    toCanonicalShortGame(loopyGame, new Array[CanonicalShortGame](loopyGame.graph.getNumVertices))
  }

  private[core] def toCanonicalShortGame(loopyGame: LoopyGame, cache: Array[CanonicalShortGame]): CanonicalShortGame = {
    if (cache(loopyGame.startVertex) != null)
      cache(loopyGame.startVertex)
    else {
      val g = CanonicalShortGame(
        loopyGame.getLeftOptions.asScala map { toCanonicalShortGame(_, cache) },
        loopyGame.getRightOptions.asScala map { toCanonicalShortGame(_, cache) }
      )
      cache(loopyGame.startVertex) = g
      g
    }
  }

  private object UponType extends Enumeration { val Upon, Uponth, Upover, Upoverth = Value }

  private case class UponForm(uponType: UponType.Value, numberPart: DyadicRationalNumber, nimberPart: Int, negated: Boolean)

}

trait CanonicalStopper extends SimplifiedLoopyGame with StopperSidedValue with OutputTarget {

  def loopyGame: LoopyGame

  private[cgsuite] override def simplifiedSide = Onside   // They're equivalent for stoppers

  override def gameValue(tc: TranspositionCache) = this

  override def options(player: Player): Iterable[CanonicalStopper] = {
    val lgOpts = player match {
      case Left => loopyGame.getLeftOptions
      case Right => loopyGame.getRightOptions
    }
    val lgOptsScala = lgOpts.asScala.toSet
    lgOptsScala map { CanonicalStopper(_) }
  }

  override def sortedOptions(player: Player): Seq[CanonicalStopper] = {
    options(player).toSeq.sorted(SimplifiedLoopyGame.SemideterministicOrdering)
  }

  def +(that: CanonicalStopper): StopperSidedValue = {
    val sum = loopyGame.add(that.loopyGame)
    if (sum.isStopper) {
      CanonicalStopper(sum)
    } else {
      SidedValue(sum) match {
        case ssv: StopperSidedValue => ssv
        case _ => throw CounterexampleException(
          "Congratulations! You've found a counterexample to the Finite Sides Conjecture. " +
            "Please report this finding to asiegel@users.sourceforge.net."
        )
      }
    }
  }

  def +(that: CanonicalShortGame): CanonicalStopper = {
    CanonicalStopper(loopyGame.add(new LoopyGame(that)))
  }

  def -(that: CanonicalStopper): StopperSidedValue = this + (-that)

  def -(that: CanonicalShortGame): CanonicalStopper = this + (-that)

  override def unary_+ : CanonicalStopper = this

  override def unary_- : CanonicalStopper = CanonicalStopper(loopyGame.negative)

  override def switch: CanonicalStopper = CanonicalStopper(this)(-this)

  def <=(that: CanonicalStopper) = loopyGame.leq(that.loopyGame, true)

  def >=(that: CanonicalStopper) = that <= this

  def < (that: CanonicalStopper) = this <= that && !(that <= this)

  def > (that: CanonicalStopper) = that <= this && !(this <= that)

  def & (that: CanonicalStopper) = StopperSidedValue(this, that)

  override def outcomeClass: OutcomeClass = {
    if (isZero)
      OutcomeClass.P
    else {
      (this >= zero, this <= zero) match {
        case (false, false) => OutcomeClass.N
        case (true, false) => OutcomeClass.L
        case (false, true) => OutcomeClass.R
        case (true, true) => sys error "this shouldn't happen; we checked for isZero above"
      }
    }
  }

  def degree: CanonicalStopper = if (isLoopfree) zero else upsum(-this)

  def downsum(that: CanonicalStopper): CanonicalStopper = (this + that).offside

  def downsumVariety(deg: CanonicalStopper): CanonicalStopper = {
    if (deg.isIdempotent)
      downsum((-this).upsum(deg))
    else
      throw InvalidOperationException("Variety degree must be an idempotent.")
  }

  def followerCount: Integer = SmallInteger(loopyGame.getGraph.getNumVertices)

  def followers: Iterable[CanonicalStopper] = {
    (0 until loopyGame.getGraph.getNumVertices).map { n =>
      // The stopper simplification algorithms guarantee that elements of
      // this Iterable will be distinct.
      CanonicalStopper(loopyGame.deriveGame(n))
    }
  }

  override def isIdempotent = this + this == this

  override def isInfinitesimal: Boolean = strongStop(Left).isZero && strongStop(Right).isZero

  override def isNumberish: Boolean = {
    val stop = strongStop(Left)
    stop.isNumber && stop == strongStop(Right)
  }

  override def isNumberTiny: Boolean = {

    val lo = options(Left)
    val ro = options(Right)

    lo.size == 1 && ro.size == 1 && (
      lo.head.isNumber && {
        val rlo = ro.head.options(Left)
        val rro = ro.head.options(Right)
        rlo.size == 1 && rro.size == 1 && lo.head == rlo.head && rro.head - lo.head.asInstanceOf[DyadicRationalNumber] <= under
      } ||
        ro.head.isNumber && {
          val llo = lo.head.options(Left)
          val lro = lo.head.options(Right)
          lro.size == 1 && llo.size == 1 && ro.head == lro.head && ro.head.asInstanceOf[DyadicRationalNumber] - llo.head <= under
        }
      )

  }

  override def isLoopfree = loopyGame.isLoopfree

  override def isPlumtree = loopyGame.isPlumtree

  override def isPseudonumber = {
    isNumber || loopyGame.isOn || loopyGame.isOff || isPlumtree && {
      val lo = options(Left)
      val ro = options(Right)
      (lo == Set(this) && ro.size == 1 && ro.head.isNumber) ||
      (ro == Set(this) && lo.size == 1 && lo.head.isNumber)
    }
  }

  override def isStopper = true

  def isSwitch: Boolean = this == -this

  override def leftOptions: Iterable[CanonicalStopper] = options(Left)

  override def offside = this

  override def onside = this

  def ordinalSum(that: CanonicalStopper): CanonicalStopper = CanonicalStopper(loopyGame.ordinalSum(that.loopyGame))

  override def rightOptions: Iterable[CanonicalStopper] = options(Right)

  def subordinate(base: Game): CanonicalStopper = {
    val node = subordinateR(
      base.leftOptions.map { _.canonicalForm }.toSet,
      base.rightOptions.map { _.canonicalForm }.toSet,
      mutable.Map()
    )
    CanonicalStopper(new LoopyGame(node))
  }

  private def subordinateR(
    loBase: Set[CanonicalStopper],
    roBase: Set[CanonicalStopper],
    visited: mutable.Map[CanonicalStopper, LoopyGame.Node]
  ): LoopyGame.Node = {

    visited.get(this) match {
      case Some(node) => node
      case None =>
        val reduction = new LoopyGame.Node()
        visited(this) = reduction
        val lo = leftOptions
        val ro = rightOptions
        for (bl <- loBase) {
          if (!lo.exists(_ >= bl) && !bl.rightOptions.exists(this >= _)) {
            throw InvalidOperationException("That game cannot be subordinated to the specified base.")
          }
        }
        for (br <- roBase) {
          if (!ro.exists(_ <= br) && !br.leftOptions.exists(this <= _)) {
            throw InvalidOperationException("That game cannot be subordinated to the specified base.")
          }
        }
        val loSub = lo collect {
          case gl if !loBase.contains(gl) => gl.subordinateR(loBase, roBase, visited)
        }
        val roSub = ro collect {
          case gr if !roBase.contains(gr) => gr.subordinateR(loBase, roBase, visited)
        }
        loSub.foreach(reduction.addLeftEdge)
        roSub.foreach(reduction.addRightEdge)
        reduction
    }

  }

  def upsum(that: CanonicalStopper): CanonicalStopper = (this + that).onside

  def upsumVariety(deg: CanonicalStopper): CanonicalStopper = {
    if (deg.isIdempotent)
      upsum((-this).downsum(-deg))
    else
      throw InvalidOperationException("Variety degree must be an idempotent.")
  }

  def variety: CanonicalStopper = {
    val deg = degree
    val v = upsumVariety(deg)
    if (v == downsumVariety(deg))
      v
    else
      throw CounterexampleException(
        "Congratulations! You've found a counterexample to the Stability Conjecture. " +
          "Please report this finding to asiegel@users.sourceforge.net."
      )
  }

  def stop(player: Player): Pseudonumber = {
    player match {
      case Left => leftStop
      case Right => rightStop
    }
  }

  def strongStop(player: Player): Pseudonumber = {
    stop(player) match {
      case OverNumberImpl(x, _) => x
      case x => x
    }
  }

  def leftStop: Pseudonumber = {
    options(Left) map { _.rightStop } reduce { _ max _ }
  }

  def rightStop: Pseudonumber = {
    options(Right) map { _.leftStop } reduce { _ min _ }
  }

  private def uponForm: Option[UponForm] = {

    uponForm(true) orElse {
      (-this).uponForm(true) map { uf =>
        uf.copy(numberPart = -uf.numberPart, negated = true)
      }
    }

  }

  private def uponForm(checkUponth: Boolean): Option[UponForm] = {

    val lo = options(Left)
    val ro = options(Right)

    val x = if (ro.size == 1 && lo.size <= 2) {
      ro.head match {
        case roUptimal: Uptimal if roUptimal.uptimalLength == 0 =>
          if (roUptimal.nimberPart == 0 && lo == Set(this, roUptimal.numberPart) ||
              roUptimal.nimberPart != 0 && lo == Set(this)) {
            Some(UponForm(UponType.Upon, roUptimal.numberPart, roUptimal.nimberPart ^ 1, negated = false))
          } else {
            None
          }
        case _ => None
      }
    } else {
      None
    }

    x orElse {
      if (checkUponth && ro.size == 1) {
        (-ro.head).uponForm(false) match {
          case Some(UponForm(UponType.Upon, numberPart, nimberPart, false))
            if lo == (0 to (nimberPart ^ 1)).map { n => Uptimal(-numberPart, 0, n) }.toSet =>
            Some(UponForm(UponType.Uponth, -numberPart, nimberPart ^ 1, negated = false))
          case _ => None
        }
      } else {
        None
      }
    }

  }

  // Disambiguate

  override def toOutput = super[SimplifiedLoopyGame].toOutput

  // TODO Consolidate w/ CanonicalShortGame.

  private[core] override def appendTo(
    output: StyledTextOutput,
    forceBrackets: Boolean,
    forceParens: Boolean,
    nodeStack: mutable.Map[SimplifiedLoopyGame, Option[String]],
    numNamedNodes: Array[Int]
    ): Int = {

    val lo = sortedOptions(Left)
    val ro = sortedOptions(Right)

    if (isSwitch) {

      output.appendSymbol(PLUS_MINUS)
      if (lo.size > 1)
        output.appendMath("{")
      lo.head.appendTo(output, forceBrackets = true, forceParens = lo.size == 1)
      lo.tail.foreach { gl => output.appendMath(","); gl.appendTo(output, forceBrackets = true, forceParens = false) }
      if (lo.size > 1)
        output.appendMath("}")
      0

    } else if (isPlumtree && isNumberTiny) {

      val (str, symbol, translate, subscript) = {
        if (lo.head.isNumber)
          ("Tiny", TINY, lo.head.asInstanceOf[CanonicalShortGame], -ro.head.options(Right).head + lo.head.asInstanceOf[CanonicalShortGame])
        else
          ("Miny", MINY, ro.head.asInstanceOf[CanonicalShortGame], lo.head.options(Left).head - ro.head.asInstanceOf[CanonicalShortGame])
      }
      if (forceParens)
        output.appendMath("(")
      if (translate != zero)
        translate.appendTo(output, forceBrackets = false, forceParens = false)
      val sub = new StyledTextOutput()
      subscript.appendTo(sub, forceBrackets = true, forceParens = false)
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

    } else uponForm match {

      case Some(UponForm(uponType, numberPart, nimberPart, negated)) =>
        if (numberPart != zero)
          output.appendOutput(numberPart.toOutput)
        if (negated)
          output.appendSymbol(StyledTextOutput.Symbol.DOWN)
        else
          output.appendSymbol(StyledTextOutput.Symbol.UP)
        val exponentStyle = util.EnumSet.of(
          StyledTextOutput.Style.FACE_MATH,
          if (negated) StyledTextOutput.Style.LOCATION_SUBSCRIPT else StyledTextOutput.Style.LOCATION_SUPERSCRIPT
        )
        uponType match {
          case UponType.Upon => output.appendText(exponentStyle, "[on]")
          case UponType.Uponth =>
            output.appendText(exponentStyle, util.EnumSet.of(Output.Mode.PLAIN_TEXT), "<")
            output.appendText(exponentStyle, "on")
            output.appendText(exponentStyle, util.EnumSet.of(Output.Mode.PLAIN_TEXT), ">")
          case UponType.Upover => output.appendText(exponentStyle, "[over]")
          case UponType.Upoverth =>
            output.appendText(exponentStyle, util.EnumSet.of(Output.Mode.PLAIN_TEXT), "<")
            output.appendText(exponentStyle, "over")
            output.appendText(exponentStyle, util.EnumSet.of(Output.Mode.PLAIN_TEXT), ">")
        }
        if (nimberPart >= 1) {
          output.appendSymbol(StyledTextOutput.Symbol.STAR)
          if (nimberPart >= 2) {
            output.appendMath(nimberPart.toString)
          }
        }
        0

      case None => super.appendTo(output, forceBrackets, forceParens, nodeStack, numNamedNodes)

    }

  }

}

case class CanonicalStopperImpl private[core] (loopyGame: LoopyGame) extends CanonicalStopper {

  assert (!isLoopfree)

}

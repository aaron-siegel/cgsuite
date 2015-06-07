package org.cgsuite.core

import java.util

import org.cgsuite.core.CanonicalStopper._
import org.cgsuite.core.Values._
import org.cgsuite.exception.InputException
import org.cgsuite.output.StyledTextOutput.Symbol._
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

import scala.collection.JavaConversions._
import scala.collection.mutable

object CanonicalStopper {

  def apply(loopyGame: LoopyGame): CanonicalStopper = {
    if (loopyGame.isLoopfree) {
      toCanonicalShortGame(loopyGame)
    } else if (loopyGame.isStopper) {
      val canonical = loopyGame.canonicalizeStopperInternal()
      if (canonical.isLoopfree)
        toCanonicalShortGame(canonical)
      else
        CanonicalStopperImpl(canonical)
    } else {
      throw new InputException(s"not a stopper: $loopyGame")
    }
  }

  def apply(lo: Iterable[CanonicalStopper], ro: Iterable[CanonicalStopper]): CanonicalStopper = {
    val thisNode = new LoopyGame.Node()
    lo foreach { gl => thisNode.addLeftEdge(gl.loopyGame) }
    ro foreach { gr => thisNode.addRightEdge(gr.loopyGame) }
    CanonicalStopper(new LoopyGame(thisNode))
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
        loopyGame.getLeftOptions  map { toCanonicalShortGame(_, cache) },
        loopyGame.getRightOptions map { toCanonicalShortGame(_, cache) }
      )
      cache(loopyGame.startVertex) = g
      g
    }
  }

  object SemideterministicOrdering extends Ordering[CanonicalStopper] {

    def compare(g: CanonicalStopper, h: CanonicalStopper): Int = {
      (g, h) match {
        case (a: CanonicalShortGame, b: CanonicalShortGame) => CanonicalShortGame.DeterministicOrdering.compare(a, b)
        case (a: CanonicalShortGame, _) => -1
        case (_, b: CanonicalShortGame) => 1
        case (_, _) => g.hashCode - h.hashCode      // At least these are isomorphism-invariant.
      }
    }

  }

  private object UponType extends Enumeration { val Upon, Uponth, Upover, Upoverth = Value }

  private case class UponForm(uponType: UponType.Value, numberPart: DyadicRationalNumber, nimberPart: Int, negated: Boolean)

}

trait CanonicalStopper extends Game with StopperSidedValue with OutputTarget {

  def loopyGame: LoopyGame

  def options(player: Player): Iterable[CanonicalStopper] = {
    val lgOpts = player match {
      case Left => loopyGame.getLeftOptions
      case Right => loopyGame.getRightOptions
    }
    lgOpts map { CanonicalStopper(_) } toSet
  }

  def sortedOptions(player: Player): Seq[CanonicalStopper] = {
    options(player).toSeq.sorted(CanonicalStopper.SemideterministicOrdering)
  }

  def +(that: CanonicalStopper): StopperSidedValue = {
    val sum = loopyGame.add(that.loopyGame)
    if (sum.isStopper) {
      CanonicalStopper(sum)
    } else {
      StopperSidedValue(sum.onside, sum.offside)
    }
  }

  def +(that: CanonicalShortGame): CanonicalStopper = {
    CanonicalStopper(loopyGame.add(new LoopyGame(that)))
  }

  def -(that: CanonicalStopper): StopperSidedValue = this + (-that)

  def -(that: CanonicalShortGame): CanonicalStopper = this + (-that)

  override def unary_+ : CanonicalStopper = this

  override def unary_- : CanonicalStopper = CanonicalStopper(loopyGame.negative)

  def <=(that: CanonicalStopper) = loopyGame.leq(that.loopyGame, true)

  def degree: CanonicalStopper = {
    if (loopyGame.graph.isCycleFree) {
      zero
    } else {
      upsum(-this)
    }
  }

  def downsum(that: CanonicalStopper): CanonicalStopper = loopyGame.add(that.loopyGame).offside()

  def downsumVariety(deg: CanonicalStopper): CanonicalStopper = {
    if (deg.isIdempotent)
      downsum((-this).upsum(deg))
    else
      throw InputException("Degree must be an idempotent.")
  }

  override def isIdempotent = this + this == this

  override def isInfinitesimal: Boolean = leftStop.isZero && rightStop.isZero

  override def isNumberish: Boolean = {
    val stop = leftStop
    stop.isNumber && stop == rightStop
  }

  override def isLoopfree = loopyGame.isLoopfree

  override def isPlumtree = loopyGame.isPlumtree

  override def isStopper = true

  def isSwitch: Boolean = this == -this

  override def offside = this

  override def onside = this

  def ordinalSum(that: CanonicalStopper): CanonicalStopper = CanonicalStopper(loopyGame.ordinalSum(that.loopyGame))

  def upsum(that: CanonicalStopper): CanonicalStopper = loopyGame.add(that.loopyGame).onside()

  def upsumVariety(deg: CanonicalStopper): CanonicalStopper = {
    if (deg.isIdempotent)
      upsum((-this).downsum(-deg))
    else
      throw InputException("Degree must be an idempotent.")
  }

  def variety: CanonicalStopper = {
    val deg = degree
    val v = upsumVariety(deg)
    if (v == downsumVariety(deg))
      v
    else
      throw InputException("Congratulations!  You've found a counterexample to the Stability Conjecture.  Please report this finding to asiegel@users.sourceforge.net.")
  }

  def stop(player: Player) = {
    player match {
      case Left => leftStop
      case Right => rightStop
    }
  }

  def leftStop: CanonicalStopper = {
    if (loopyGame.isOn || loopyGame.isOff)
      this
    else
      options(Left) map { _.rightStop } reduce { (g, h) => if (g <= h) h else g }
  }

  def rightStop: CanonicalStopper = {
    if (loopyGame.isOn || loopyGame.isOff)
      this
    else
      options(Right) map { _.leftStop } reduce { (g, h) => if (g <= h) g else h }
  }

  override def toOutput: StyledTextOutput = {
    val output: StyledTextOutput = new StyledTextOutput
    appendTo(output, true, false)
    output
  }

  override def isNumberTiny = {

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

  private def uponForm: Option[UponForm] = {
    uponForm(true) match {
      case Some((uponType, number)) => Some(UponForm(uponType, number, 0, false))
      case None =>
        (this + star).uponForm(true) match {
          case Some((uponType, number)) => Some(UponForm(uponType, number, 1, false))
          case None =>
            (-this).uponForm(true) match {
              case Some((uponType, number)) => Some(UponForm(uponType, -number, 0, true))
              case None =>
                (-this + star).uponForm(true) match {
                  case Some((uponType, number)) => Some(UponForm(uponType, -number, 1, true))
                  case None => None
                }
            }
        }
    }
  }

  private def uponForm(checkUponth: Boolean): Option[(UponType.Value, DyadicRationalNumber)] = {

    val lo = options(Left)
    val ro = options(Right)

    if (lo.size == 1 && ro.size == 1) {
      val gl = lo.head
      val gr = ro.head
      if (this == gl && gr.isLoopfree && (gr + star).isNumber) {
        Some((UponType.Upon, gr.asInstanceOf[CanonicalShortGame].leftStop))
      } else if (checkUponth && gl.isNumber) {
        (-gr + star).uponForm(false) match {
          case Some((UponType.Upon, number)) if number == -gl.asInstanceOf[CanonicalShortGame].leftStop => Some((UponType.Uponth, -number))
          case _ => None
        }
      } else {
        None
      }
    } else {
      None
    }

  }

  // TODO Pretty sure this can be consolidated w/ CanonicalShortGame.toOutput. That will pick up other
  // nice things like loopy switches.

  private[core] def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {
    appendTo(output, forceBrackets, forceParens, mutable.Map(), new Array[Int](1))
  }

  private[core] def appendTo(
    output: StyledTextOutput,
    forceBrackets: Boolean,
    forceParens: Boolean,
    nodeStack: mutable.Map[CanonicalStopper, Option[String]],
    numNamedNodes: Array[Int]
    ): Int = {

    val lo = sortedOptions(Left)
    val ro = sortedOptions(Right)

    if (nodeStack.contains(this)) {

      val name = nodeStack(this) match {
        case Some(x) => x
        case None =>
          val x = CanonicalStopper.nthName(numNamedNodes(0))
          nodeStack.put(this, Some(x))
          numNamedNodes(0) += 1
          x
      }
      output.appendMath(name); 0

    } else if (lo.size == 1 && ro.size == 0 && lo.head == this) {

      output.appendMath("on"); 0

    } else if (lo.size == 0 && ro.size == 1 && ro.head == this) {

      output.appendMath("off"); 0

    } else if (lo.size == 1 && ro.size == 1 && lo.head.isNumber && ro.head == this) {

      if (lo.head != zero)
        output.appendOutput(lo.head.toOutput)
      output.appendMath("over"); 0

    } else if (lo.size == 1 && ro.size == 1 && lo.head == this && ro.head.isNumber) {

      if (ro.head != zero)
        output.appendOutput(ro.head.toOutput)
      output.appendMath("under"); 0

    } else if (isSwitch) {

      output.appendSymbol(PLUS_MINUS)
      if (lo.size > 1)
        output.appendMath("{")
      lo.head.appendTo(output, true, lo.size == 1)
      lo.tail.foreach { gl => output.appendMath(","); gl.appendTo(output, true, false) }
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
          case UponType.Uponth => output.appendText(exponentStyle, "<on>")
          case UponType.Upover => output.appendText(exponentStyle, "[over]")
          case UponType.Upoverth => output.appendText(exponentStyle, "<over>")
        }
        nimberPart match {
          case 0 =>
          case 1 => output.appendSymbol(StyledTextOutput.Symbol.STAR)
        }
        0

      case None =>
        nodeStack.put(this, None)
        val leftOutput = new StyledTextOutput
        var first = true
        var numSlashes = 0
        lo foreach { gl =>
          if (first)
            first = false
          else
            leftOutput.appendMath(",")
          if (gl == this)
            leftOutput.appendMath("pass")
          else
            numSlashes = numSlashes max gl.appendTo(leftOutput, lo.size > 1, false, nodeStack, numNamedNodes)
        }
        val rightOutput = new StyledTextOutput
        first = true
        ro foreach { gr =>
          if (first)
            first = false
          else
            rightOutput.appendMath(",")
          if (gr == this)
            rightOutput.appendMath("pass")
          else
            numSlashes = numSlashes max gr.appendTo(rightOutput, ro.size > 1, false, nodeStack, numNamedNodes)
        }
        val isNamed = nodeStack.remove(this) match {
          case Some(Some(name)) => output.appendMath(name); true
          case _ => false
        }
        numSlashes += 1
        val showBrackets =
          forceBrackets || isNamed || lo.isEmpty || ro.isEmpty || numSlashes == CanonicalShortGame.maxSlashes
        if (showBrackets)
          output.appendMath("{")
        output.appendOutput(leftOutput)
        output.appendMath(CanonicalShortGame.slashString(numSlashes))
        output.appendOutput(rightOutput)
        if (showBrackets)
          output.appendMath("}")

        if (showBrackets) 0 else numSlashes

    }

  }

}

case class CanonicalStopperImpl private[core] (loopyGame: LoopyGame) extends CanonicalStopper {

  assert (!isLoopfree)

}

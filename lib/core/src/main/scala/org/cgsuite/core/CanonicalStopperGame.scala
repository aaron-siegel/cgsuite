package org.cgsuite.core

import java.util

import org.cgsuite.core.CanonicalStopperGame._
import org.cgsuite.core.Values._
import org.cgsuite.exception.InputException
import org.cgsuite.output.StyledTextOutput.Symbol._
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

import scala.collection.JavaConversions._
import scala.collection.mutable

object CanonicalStopperGame {

  def apply(loopyGame: LoopyGame): CanonicalStopperGame = {
    if (loopyGame.isLoopfree) {
      toCanonicalShortGame(loopyGame)
    } else if (loopyGame.isStopper) {
      val canonical = loopyGame.canonicalizeStopperInternal()
      if (canonical.isLoopfree)
        toCanonicalShortGame(canonical)
      else
        CanonicalStopperGameImpl(canonical)
    } else {
      throw new InputException(s"not a stopper: $loopyGame")
    }
  }

  def apply(lo: Iterable[CanonicalStopperGame], ro: Iterable[CanonicalStopperGame]): CanonicalStopperGame = {
    val thisNode = new LoopyGame.Node()
    lo foreach { gl => thisNode.addLeftEdge(gl.loopyGame) }
    ro foreach { gr => thisNode.addRightEdge(gr.loopyGame) }
    CanonicalStopperGame(new LoopyGame(thisNode))
  }

  def apply(lo: CanonicalStopperGame*)(ro: CanonicalStopperGame*): CanonicalStopperGame = apply(lo, ro)

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

  object SemideterministicOrdering extends Ordering[CanonicalStopperGame] {

    def compare(g: CanonicalStopperGame, h: CanonicalStopperGame): Int = {
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

trait CanonicalStopperGame extends CanonicalStopperSidedGame with OutputTarget {

  def loopyGame: LoopyGame

  def options(player: Player): Iterable[CanonicalStopperGame] = {
    val lgOpts = player match {
      case Left => loopyGame.getLeftOptions
      case Right => loopyGame.getRightOptions
    }
    lgOpts map { CanonicalStopperGame(_) }
  }

  def sortedOptions(player: Player): Seq[CanonicalStopperGame] = {
    options(player).toSeq.sorted(CanonicalStopperGame.SemideterministicOrdering)
  }

  def +(that: CanonicalStopperGame): CanonicalStopperSidedGame = {
    CanonicalStopperGame(loopyGame.add(that.loopyGame))   // TODO
  }

  def +(that: CanonicalShortGame): CanonicalStopperGame = {
    CanonicalStopperGame(loopyGame.add(new LoopyGame(that)))
  }

  def -(that: CanonicalStopperGame): CanonicalStopperSidedGame = this + (-that)

  def -(that: CanonicalShortGame): CanonicalStopperGame = this + (-that)

  override def unary_- : CanonicalStopperGame = CanonicalStopperGame(loopyGame.negative)

  def <=(that: CanonicalStopperGame) = loopyGame.leq(that.loopyGame, true)

  def isInteger = false

  def isLoopfree = loopyGame.isLoopfree

  def isNumber = false

  def ordinalSum(that: CanonicalStopperGame) = CanonicalStopperGame(loopyGame.ordinalSum(that.loopyGame))
       /*
  def stop(player: Player) = player match {
    case Left => leftStop
    case Right => rightStop
  }

  def leftStop: CanonicalStopperGame = {
    if (isOn) this else options(Left).map { _.rightStop }.reduce { _ max _ }
    options(Left).map { _.rightStop }.max
  }

  def rightStop: CanonicalStopperGame = options(Right).map { _.leftStop }.min
         */
  override def toOutput: StyledTextOutput = {
    val output: StyledTextOutput = new StyledTextOutput
    appendTo(output, true, false)
    output
  }

  def isNumberTiny = {

    val lo = options(Left)
    val ro = options(Right)

    lo.size == 1 && ro.size == 1 && {
      val gl = lo.head
      val gr = ro.head
      val grlo = gl.options(Left)
      val grro = gr.options(Right)
      gl.isNumber && gr.isNumber && grlo.size == 1 && grro.size == 1 && {
        val grl = grlo.head
        val grr = grro.head
        gl == grl && grr - gl.asInstanceOf[CanonicalShortGame] <= under
      }
    }

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
    nodeStack: mutable.Map[CanonicalStopperGame, Option[String]],
    numNamedNodes: Array[Int]
    ): Int = {

    val lo = sortedOptions(Left)
    val ro = sortedOptions(Right)

    if (nodeStack.contains(this)) {

      val name = nodeStack(this) match {
        case Some(x) => x
        case None =>
          val x = CanonicalStopperGame.nthName(numNamedNodes(0))
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

    } else if (isNumberTiny || (-this).isNumberTiny) {

      val (str, translate, subscript) = {
        if (isNumberTiny)
          ("Tiny", lo.head.asInstanceOf[CanonicalShortGame], -ro.head.options(Right).head + lo.head.asInstanceOf[CanonicalShortGame])
        else
          ("Miny", ro.head.asInstanceOf[CanonicalShortGame], lo.head.options(Left).head - ro.head.asInstanceOf[CanonicalShortGame])
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

case class CanonicalStopperGameImpl private[core] (loopyGame: LoopyGame) extends CanonicalStopperGame

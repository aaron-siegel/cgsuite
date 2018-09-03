package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.exception.EvalException
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.TranspositionTable

import scala.collection.mutable

object Pseudonumber {

  def apply(x: DyadicRationalNumber, overSign: Int): Pseudonumber = {
    if (overSign == 0)
      x
    else
      OverNumberImpl(x, overSign.signum)
  }

  def apply(onSign: Int): Pseudonumber = {
    onSign.signum match {
      case  1 => OnImpl
      case -1 => OffImpl
      case  0 => throw new IllegalArgumentException("onSign == 0")
    }
  }

}

trait Pseudonumber extends CanonicalStopper {

  override def options(player: Player): Iterable[Pseudonumber] = sys.error("should get overriden by implementations")

  override def unary_- : Pseudonumber = sys.error("should get overriden by implementations")

  override def isPseudonumber = true

  override def leftStop = this

  override def rightStop = this

  def min(that: Pseudonumber): Pseudonumber = if (this <= that) this else that

  def max(that: Pseudonumber): Pseudonumber = if (this <= that) that else this

  def abs: Pseudonumber = if (this < zero) -this else this

  def blowup: Pseudonumber

  private[core] override def appendTo(
    output: StyledTextOutput,
    forceBrackets: Boolean,
    forceParens: Boolean,
    nodeStack: mutable.Map[SimplifiedLoopyGame, Option[String]],
    numNamedNodes: Array[Int]
    ): Int = {

    appendTo(output, forceBrackets, forceParens)

  }

}

case object OnImpl extends Pseudonumber {

  def loopyGame = LoopyGame.ON

  override def unary_- : Pseudonumber = off

  override def options(player: Player): Iterable[Pseudonumber] = {
    player match {
      case Left => Set(this)
      case Right => Set.empty
    }
  }

  def blowup = this

  override def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {
    output.appendMath("on"); 0
  }

}

case object OffImpl extends Pseudonumber {

  def loopyGame = LoopyGame.OFF

  override def unary_- : Pseudonumber = on

  override def options(player: Player): Iterable[Pseudonumber] = {
    player match {
      case Left => Set.empty
      case Right => Set(this)
    }
  }

  def blowup = throw EvalException("Exponent must be nonnegative.")

  override def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {
    output.appendMath("off"); 0
  }

}

case class OverNumberImpl private[core] (x: DyadicRationalNumber, overSign: Int) extends Pseudonumber {

  assert(overSign == 1 || overSign == -1)

  def loopyGame = {
    val thisNode = new LoopyGame.Node()
    if (overSign > 0) {
      thisNode.addLeftEdge(x)
      thisNode.addRightEdge(thisNode)
    } else {
      thisNode.addLeftEdge(thisNode)
      thisNode.addRightEdge(x)
    }
    new LoopyGame(thisNode)
  }

  override def options(player: Player): Iterable[Pseudonumber] = {
    player match {
      case Left => if (overSign > 0) Set(x) else Set(this)
      case Right => if (overSign < 0) Set(x) else Set(this)
    }
  }

  override def unary_- : Pseudonumber = OverNumberImpl(-x, -overSign)

  def blowup = {
    if (x.isZero && overSign == 1) {
      off
    } else if (x > zero) {
      OverNumberImpl(x.blowup, overSign)
    } else {
      throw EvalException("Exponent must be nonnegative.")
    }
  }

  override def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {
    if (!x.isZero) {
      if (forceParens)
        output.appendMath("(")
      output.append(x.toOutput)
    }
    output.appendMath(if (overSign > 0) "over" else "under")
    if (forceParens && !x.isZero)
      output.appendMath(")")
    0
  }

}

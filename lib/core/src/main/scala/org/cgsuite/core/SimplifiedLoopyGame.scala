package org.cgsuite.core

import org.cgsuite.lang.NotShortGameException
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.TranspositionTable

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.language.postfixOps

object SimplifiedLoopyGame {

  def apply(loopyGame: LoopyGame, simplifiedSide: Side): SimplifiedLoopyGame = {
    if (loopyGame.isStopper) {
      CanonicalStopper(loopyGame)
    } else {
      SimplifiedLoopyGameImpl(loopyGame, simplifiedSide)
    }
  }

  object SemideterministicOrdering extends Ordering[SimplifiedLoopyGame] {

    def compare(g: SimplifiedLoopyGame, h: SimplifiedLoopyGame): Int = {
      (g, h) match {
        case (a: CanonicalShortGame, b: CanonicalShortGame) => CanonicalShortGame.DeterministicOrdering.compare(a, b)
        case (a: CanonicalShortGame, _) => -1
        case (_, b: CanonicalShortGame) => 1
        case (_, _) => g.hashCode - h.hashCode      // At least these are isomorphism-invariant.
      }
    }

  }

}

trait SimplifiedLoopyGame extends Game {

  def loopyGame: LoopyGame
  private[cgsuite] def simplifiedSide: Side

  override def unary_- : SimplifiedLoopyGame = SimplifiedLoopyGameImpl(loopyGame.negative(), -simplifiedSide)

  override def canonicalForm(tt: TranspositionTable): CanonicalShortGame = {
    throw NotShortGameException("That is a loopy game.")
  }

  override def gameValue(tt: TranspositionTable): SidedValue = SidedValue(loopyGame)

  def options(player: Player): Iterable[SimplifiedLoopyGame] = {
    val lgOpts = player match {
      case Left => loopyGame.getLeftOptions
      case Right => loopyGame.getRightOptions
    }
    lgOpts map { SimplifiedLoopyGame(_, simplifiedSide) } toSet
  }

  def sortedOptions(player: Player): Seq[SimplifiedLoopyGame] = {
    options(player).toSeq.sorted(SimplifiedLoopyGame.SemideterministicOrdering)
  }

  override def toOutput: StyledTextOutput = {
    val output: StyledTextOutput = new StyledTextOutput
    appendTo(output, true, false)
    output
  }

  private[core] def appendTo(output: StyledTextOutput, forceBrackets: Boolean, forceParens: Boolean): Int = {
    appendTo(output, forceBrackets, forceParens, mutable.Map(), new Array[Int](1))
  }

  private[core] def appendTo(
    output: StyledTextOutput,
    forceBrackets: Boolean,
    forceParens: Boolean,
    nodeStack: mutable.Map[SimplifiedLoopyGame, Option[String]],
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
      output.appendMath(name)
      0

    } else {

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

case class SimplifiedLoopyGameImpl private[core] (loopyGame: LoopyGame, simplifiedSide: Side) extends SimplifiedLoopyGame

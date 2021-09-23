package org.cgsuite.core

import org.cgsuite.exception.NotShortGameException
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.TranspositionCache

import scala.collection.{JavaConverters, mutable}

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
        case (_: CanonicalShortGame, _) => -1
        case (_, _: CanonicalShortGame) => 1
        case (_, _) => g.hashCode - h.hashCode      // At least these are isomorphism-invariant.
      }
    }

  }

  def apply(lo: Iterable[SimplifiedLoopyGame], ro: Iterable[SimplifiedLoopyGame], withPass: Set[Player] = Set.empty): SimplifiedLoopyGame = {
    val options = lo ++ ro
    val side = {
      if (options.isEmpty)
        Onside
      else
        options.head.simplifiedSide
    }
    // All must be on the same side
    if (options exists { _.simplifiedSide != side })
      sys error "args contain options on different sides - illegal simplification"

    val loopyGame = constructLoopyGame(lo, ro, withPass)
    SimplifiedLoopyGame(loopyGame, side)
  }

  def constructLoopyGame(lo: Iterable[SimplifiedLoopyGame], ro: Iterable[SimplifiedLoopyGame], withPass: Set[Player] = Set.empty) = {
    val thisNode = new LoopyGame.Node()
    lo foreach { gl => thisNode.addLeftEdge(gl.loopyGame) }
    ro foreach { gr => thisNode.addRightEdge(gr.loopyGame) }
    withPass foreach {
      case Left => thisNode.addLeftEdge(thisNode)
      case Right => thisNode.addRightEdge(thisNode)
    }
    new LoopyGame(thisNode)
  }

}

trait SimplifiedLoopyGame extends Game {

  def loopyGame: LoopyGame
  private[cgsuite] def simplifiedSide: Side

  override def unary_- : SimplifiedLoopyGame = SimplifiedLoopyGameImpl(loopyGame.negative(), -simplifiedSide)

  override def canonicalForm(tc: TranspositionCache): CanonicalShortGame = {
    throw NotShortGameException("That is a loopy game.")
  }

  override def gameValue(tc: TranspositionCache): SidedValue = SidedValue(loopyGame)

  def options(player: Player): Iterable[SimplifiedLoopyGame] = {
    val lgOpts = player match {
      case Left => loopyGame.getLeftOptions
      case Right => loopyGame.getRightOptions
    }
    JavaConverters.asScalaSet(lgOpts) map { SimplifiedLoopyGame(_, simplifiedSide) }
  }

  def sortedOptions(player: Player): Seq[SimplifiedLoopyGame] = {
    options(player).toSeq.sorted(SimplifiedLoopyGame.SemideterministicOrdering)
  }

  override def depthHint = 0

  override def toOutput: StyledTextOutput = {
    val output: StyledTextOutput = new StyledTextOutput
    appendTo(output, forceBrackets = true, forceParens = false)
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
          numSlashes = numSlashes max gl.appendTo(leftOutput, forceBrackets = lo.size > 1, forceParens = false, nodeStack, numNamedNodes)
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
          numSlashes = numSlashes max gr.appendTo(rightOutput, forceBrackets = ro.size > 1, forceParens = false, nodeStack, numNamedNodes)
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

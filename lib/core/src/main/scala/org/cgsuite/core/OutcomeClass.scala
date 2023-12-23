package org.cgsuite.core

import org.cgsuite.core.Outcome._
import org.cgsuite.core.OutcomeClass._
import org.cgsuite.exception.NotShortGameException
import org.cgsuite.util.PosetElement

object OutcomeClass {

  case object P extends ImpartialOutcomeClass
  case object N extends ImpartialOutcomeClass
  case object L extends OutcomeClass
  case object R extends OutcomeClass
  case object D extends ImpartialLoopyOutcomeClass
  case object PHat extends LoopyOutcomeClass
  case object PCheck extends LoopyOutcomeClass
  case object NHat extends LoopyOutcomeClass
  case object NCheck extends LoopyOutcomeClass

  def apply(leftOutcome: Outcome, rightOutcome: Outcome): OutcomeClass = {
    LoopyOutcomeClass(leftOutcome, rightOutcome) match {
      case oc: OutcomeClass => oc
      case _ => throw NotShortGameException("That is not the outcome class of a short game.")
    }
  }

}

object LoopyOutcomeClass {

  def apply(leftOutcome: Outcome, rightOutcome: Outcome): LoopyOutcomeClass = {
    (leftOutcome, rightOutcome) match {
      case (RightWins, LeftWins) => P
      case (LeftWins, RightWins) => N
      case (LeftWins, LeftWins) => L
      case (RightWins, RightWins) => R
      case (Draw, Draw) => D
      case (LeftWins, Draw) => NHat
      case (RightWins, Draw) => PCheck
      case (Draw, LeftWins) => PHat
      case (Draw, RightWins) => NCheck
    }
  }

}

sealed trait ImpartialOutcomeClass extends OutcomeClass with ImpartialLoopyOutcomeClass

sealed trait OutcomeClass extends LoopyOutcomeClass

sealed trait ImpartialLoopyOutcomeClass extends LoopyOutcomeClass

sealed trait LoopyOutcomeClass extends PosetElement[LoopyOutcomeClass] {

  def leftOutcome: Outcome = {
    this match {
      case L | N | NHat => LeftWins
      case D | NCheck | PHat => Draw
      case R | P | PCheck => RightWins
    }
  }

  def rightOutcome: Outcome = {
    this match {
      case R | N | NCheck => RightWins
      case D | NHat | PCheck => Draw
      case L | P | PHat => LeftWins
    }
  }

  def >=(that: LoopyOutcomeClass): Boolean = {
    leftOutcome >= that.leftOutcome && rightOutcome >= that.rightOutcome
  }

}

object Outcome {
  case object LeftWins extends Outcome { def ord = 1 }
  case object Draw extends Outcome { def ord = 0 }
  case object RightWins extends Outcome { def ord = -1 }

  def winner(player: Player) = {
    player match {
      case Left => LeftWins
      case Right => RightWins
    }
  }
}

sealed trait Outcome extends Ordered[Outcome] {
  def ord: Int
  override def compare(that: Outcome): Int = ord - that.ord
}

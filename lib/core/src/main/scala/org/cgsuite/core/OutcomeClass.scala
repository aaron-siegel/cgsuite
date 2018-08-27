package org.cgsuite.core

import org.cgsuite.core.Outcome._
import org.cgsuite.core.OutcomeClass._
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
  case object LeftWins extends Outcome(1)
  case object Draw extends Outcome(0)
  case object RightWins extends Outcome(-1)
}

sealed class Outcome private (val ord: Int) extends Ordered[Outcome] {
  override def compare(that: Outcome): Int = ord - that.ord
}

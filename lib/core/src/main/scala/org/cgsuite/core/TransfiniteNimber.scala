package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.core.impartial.arithmetic.FieldTable
import org.cgsuite.exception.InvalidArgumentException
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

object TransfiniteNimber {

  def apply(nimValue: GeneralizedOrdinal): TransfiniteNimber = {
    if (nimValue.isOrdinal) {
      TransfiniteNimberImpl(nimValue)
    } else {
      throw InvalidArgumentException(s"Nim value is not an ordinal: $nimValue")
    }
  }

}

trait TransfiniteNimber extends NormalValue with OutputTarget {

  def nimValue: GeneralizedOrdinal

  override def outcomeClass: ImpartialOutcomeClass = {
    if (isZero) OutcomeClass.P else OutcomeClass.N
  }

  def +(other: TransfiniteNimber): TransfiniteNimber = {
    TransfiniteNimber(nimValue nimSum other.nimValue)
  }

  def *(other: TransfiniteNimber): TransfiniteNimber = {
    TransfiniteNimber(nimValue nimProduct other.nimValue)
  }

  override def toOutput: StyledTextOutput = {
    val useParens = nimValue.terms.size > 1 ||
      (nimValue.terms.head.exponent != zero && nimValue.terms.head.coefficient != one)
    val sto = new StyledTextOutput()
    sto.appendMath("*")
    if (useParens) sto.appendMath("(")
    sto.append(nimValue.toOutput)
    if (useParens) sto.appendMath(")")
    sto
  }

}

case class TransfiniteNimberImpl private[cgsuite] (nimValue: GeneralizedOrdinal) extends TransfiniteNimber {
  assert(nimValue.isOrdinal)
}

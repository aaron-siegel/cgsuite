package org.cgsuite.core

import java.util

import org.cgsuite.core.GeneralizedOrdinal.Term
import org.cgsuite.core.Values._
import org.cgsuite.output.StyledTextOutput.Style
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

object GeneralizedOrdinal {

  def apply(terms: Term*): GeneralizedOrdinal = {
    val grouped = terms groupBy { _.exponent } mapValues { _ map { _.coefficient} }
    val reduced = grouped mapValues { _ reduce { _ + _ } }
    val filtered = reduced collect {
      case (exponent, coefficient) if coefficient != zero => Term(coefficient, exponent)
    }
    if (filtered.isEmpty) {
      zero
    } else if (filtered.size == 1 && filtered.head.exponent == zero) {
      filtered.head.coefficient
    } else {
      GeneralizedOrdinalImpl(filtered.toVector sortBy { -_.exponent : SurrealNumber })
    }
  }

  case class Term(coefficient: Integer, exponent: GeneralizedOrdinal) extends Ordered[Term] {

    assert(exponent.isOrdinal)

    def unary_- : Term = Term(-coefficient, exponent)

    def *(that: Term): Term = Term(coefficient * that.coefficient, exponent + that.exponent)

    override def compare(that: Term): Int = {
      if (coefficient == zero || that.coefficient == zero) {
        // If one (or both) of the coefficients is zero, we can ignore the exponents
        coefficient compare that.coefficient
      } else {
        val cmp = exponent compare that.exponent
        if (cmp > 0)
          coefficient compare zero
        else if (cmp < 0)
          zero compare that.coefficient
        else
          coefficient compare that.coefficient
      }
    }

  }

}

trait GeneralizedOrdinal extends SurrealNumber with OutputTarget {

  def terms: IndexedSeq[Term]

  override def numerator: GeneralizedOrdinal = this

  override def denominator: GeneralizedOrdinal = one

  override def isOrdinal: Boolean = terms forall { _.coefficient > zero }

  def isOmega: Boolean = terms.length == 1 && terms.head == Term(one, one)

  def omegaPower: SurrealNumber = {
    val (positiveTerms, negativeTerms) = terms partition { _.coefficient > zero }
    SurrealNumber(
      GeneralizedOrdinal(Term(one, GeneralizedOrdinal(positiveTerms : _*))),
      GeneralizedOrdinal(Term(one, GeneralizedOrdinal(negativeTerms map { -_ } : _*)))
    )
  }

  override def sign: Integer = terms.head.coefficient.sign

  override def unary_- : GeneralizedOrdinal = {
    GeneralizedOrdinal(terms map { -_ } : _*)
  }

  def +(that: GeneralizedOrdinal): GeneralizedOrdinal = {
    GeneralizedOrdinal(terms ++ that.terms : _*)
  }

  def -(that: GeneralizedOrdinal): GeneralizedOrdinal = {
    GeneralizedOrdinal(terms ++ (that.terms map { -_ }) : _*)
  }

  def *(that: GeneralizedOrdinal): GeneralizedOrdinal = {
    val prodTerms = {
      for {
        thisTerm <- this.terms
        thatTerm <- that.terms
      } yield {
        thisTerm * thatTerm
      }
    }
    GeneralizedOrdinal(prodTerms : _*)
  }

  override def pow(n: Integer): SurrealNumber = {
    if (n.isZero)
      one
    else if (n < zero)
      pow(-n).reciprocal
    else
      this * pow(n - one)
  }

  override def abs: GeneralizedOrdinal = if (this < zero) -this else this

  override def birthday: GeneralizedOrdinal = {
    if (isOrdinal)
      this
    else
      GeneralizedOrdinal(terms map { case Term(coefficient, exponent) => Term(coefficient.abs, exponent) } : _*)
  }

  override def compare(that: SurrealNumber): Int = {
    that match {
      case thatGeneralizedOrdinal: GeneralizedOrdinal => compareGeneralizedOrdinal(thatGeneralizedOrdinal)
      case _ => super.compare(that)
    }
  }

  def compareGeneralizedOrdinal(that: GeneralizedOrdinal): Int = {
    val pairs: IndexedSeq[(Term, Term)] = terms zipAll (that.terms, Term(zero, zero), Term(zero, zero))
    pairs find { case (thisTerm, thatTerm) => thisTerm != thatTerm } match {
      case None => 0
      case Some((thisTerm, thatTerm)) => thisTerm compare thatTerm
    }
  }

  override def toOutput: StyledTextOutput = toOutput(exponentSpot = 0)

  // exponentSpot
  // 0 = base
  // 1 = first exponent
  // 2 = second exponent
  // -1 = don't use exponents
  def toOutput(exponentSpot: Int): StyledTextOutput = {

    val output = new StyledTextOutput()
    appendToOutput(output, exponentSpot)
    output

  }

  private[cgsuite] def appendToOutput(output: StyledTextOutput, exponentSpot: Int): Unit = {
    assert(terms.nonEmpty)
    appendTermToOutput(output, terms.head, head = true, exponentSpot)
    terms.tail foreach { appendTermToOutput(output, _, head = false, exponentSpot) }
  }

  private[cgsuite] def appendTermToOutput(output: StyledTextOutput, term: Term, head: Boolean, exponentSpot: Int): Unit = {

    val baseStyles = exponentSpot match {
      case 1 => util.EnumSet.of(Style.FACE_MATH, Style.LOCATION_SUPERSCRIPT)
      case 2 => util.EnumSet.of(Style.FACE_MATH, Style.LOCATION_SUPERSUPERSCRIPT)
      case _ => util.EnumSet.of(Style.FACE_MATH)
    }

    val caretModes = exponentSpot match {
      case -1 | 2 => util.EnumSet.of(Output.Mode.PLAIN_TEXT, Output.Mode.GRAPHICAL)
      case _ => util.EnumSet.of(Output.Mode.PLAIN_TEXT)
    }

    val nextExponent = exponentSpot match {
      case 0 | 1 => exponentSpot + 1
      case _ => exponentSpot
    }

    (head, term.coefficient.sign.intValue) match {
      case (_, -1) => output appendText (baseStyles, "-")
      case (true, 1) =>
      case (false, 1) => output appendText (baseStyles, "+")
      case _ => sys.error("coefficient cannot be 0")
    }

    if (term.exponent.terms.isEmpty || term.coefficient.abs != one)
      output appendText (baseStyles, term.coefficient.abs.toString)

    if (term.exponent.terms.nonEmpty) {

      output appendSymbol (baseStyles, StyledTextOutput.Symbol.OMEGA)

      if (term.exponent != one) {
        output appendText (baseStyles, caretModes, "^")
        val requireParensForExponent = {
          term.exponent.terms.size > 1 ||
            (term.exponent.terms.head.coefficient != one && term.exponent.terms.head.exponent != zero)
        }
        if (requireParensForExponent)
          output appendText (baseStyles, caretModes, "(")
        term.exponent.appendToOutput(output, nextExponent)
        if (requireParensForExponent)
          output appendText (baseStyles, caretModes, ")")
      }

    }

  }

}

case class GeneralizedOrdinalImpl private[cgsuite](terms: Vector[Term]) extends GeneralizedOrdinal {

  assert(terms forall { _.coefficient != zero })

}
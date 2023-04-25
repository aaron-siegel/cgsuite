package org.cgsuite.core

import java.util

import org.cgsuite.core.GeneralizedOrdinal.Term
import org.cgsuite.core.impartial.arithmetic.{AlphaCalc, FieldTable}
import org.cgsuite.dsl._
import org.cgsuite.exception.ArithmeticException
import org.cgsuite.output.StyledTextOutput.Style
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

import scala.math.Ordered.orderingToOrdered

object GeneralizedOrdinal {

  def apply(terms: Term*): GeneralizedOrdinal = {
    val grouped = terms.groupBy { _.exponent }.view.mapValues { _ map { _.coefficient} }
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

  private val kMax = Integer(FieldTable.excess.size - 1)

  case class Term(coefficient: Integer, exponent: GeneralizedOrdinal) extends Ordered[Term] {

    assert(exponent.isOrdinal)

    def unary_- : Term = Term(-coefficient, exponent)

    def *(that: Term): Term = Term(coefficient * that.coefficient, exponent + that.exponent)

    def nimProduct(that: Term): GeneralizedOrdinal = {
      if (coefficient < zero || that.coefficient < zero)
        throw ArithmeticException("NimProduct applies only to ordinals.")
      GeneralizedOrdinal.reduceComponents((this.kappaComponents ++ that.kappaComponents).sorted, Nil, this.coefficient.nimProduct(that.coefficient))
    }

    private[GeneralizedOrdinal] def kappaComponents: List[KappaComponent] = {
      exponent.terms.toList flatMap { case Term(coefficient, exponent) =>
        if (exponent > kMax)
          throw ArithmeticException("NimProduct out of range.")
        val k = exponent.asInstanceOf[Integer].intValue
        val p = Integer(FieldTable.primes(k + 1))
        var n = 1
        var pn = p
        while (pn <= coefficient) {
          n += 1
          pn *= p
        }
        for (i <- 0 until n) yield {
          KappaComponent(k, i, ((coefficient div p.intExp(Integer(i))) % p).intValue)
        }
      }
    }

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

  private def reduceComponents(components: List[KappaComponent], processedComponents: List[KappaComponent], coefficient: Integer): GeneralizedOrdinal = {
    components match {
      case Nil => toOrdinal(processedComponents, coefficient)
      case a::b::tail if a.k == b.k && a.n == b.n =>
        if (a.exponent + b.exponent < a.p) {
          reduceComponents(KappaComponent(a.k, a.n, a.exponent + b.exponent)::tail, processedComponents, coefficient)
        } else {
          val newSharedComponent = {
            if (a.exponent + b.exponent == a.p) {
              List()
            } else {
              List(KappaComponent(a.k, a.n, a.exponent + b.exponent - a.p))
            }
          }
          if (a.n > 0) {
            val newComponents = newSharedComponent :+ KappaComponent(a.k, a.n - 1, 1)
            reduceComponents((newComponents ++ tail).sorted, processedComponents, coefficient)
          } else {
            val alpha = alphaSeq(a.k + 1)
            val productParts = alpha.terms map { term =>
              if (term.exponent.isZero) {
                reduceComponents(newSharedComponent ++ tail, processedComponents, coefficient.nimProduct(term.coefficient))
              } else {
                reduceComponents((newSharedComponent ++ term.kappaComponents ++ tail).sorted, processedComponents, coefficient)
              }
            }
            productParts reduce { _ nimSum _ }
          }
        }
      case a::tail => reduceComponents(tail, a::processedComponents, coefficient)
    }
  }

  private def toOrdinal(components: List[KappaComponent], coefficient: Integer): GeneralizedOrdinal = {
    val terms = components map { component =>
      val p = FieldTable.primes(component.k + 1)
      Term(Integer(p).intExp(Integer(component.n)) * Integer(component.exponent), Integer(component.k))
    }
    GeneralizedOrdinal(terms : _*).ordOmegaPower * coefficient
  }

  private case class KappaComponent(k: Int, n: Int, exponent: Int) extends Ordered[KappaComponent] {

    assert(exponent >= 1)
    assert(exponent < FieldTable.primes(k + 1))

    def p = FieldTable.primes(k + 1)

    override def compare(that: KappaComponent): Int = (-k, -n, exponent) compare (-that.k, -that.n, that.exponent)

  }

  val alphaSeq: Vector[GeneralizedOrdinal] = {
    FieldTable.qSet zip FieldTable.excess map { case (qSet, excess) =>
      qSet.map(kappa).sum + Integer(excess)
    }
  }

  def alpha(p: Int): GeneralizedOrdinal = alphaSeq(FieldTable.primes.indexOf(p))

  def kappa(q: Int): GeneralizedOrdinal = {
    val (p, n) = AlphaCalc.primePower(q)
    if (p == 2) {
      one << (one << Integer(n - 1))
    } else {
      val k = FieldTable.primes.indexOf(p)
      val exponent = Integer(k - 1).ordOmegaPower * Integer(p).intExp(Integer(n - 1))
      exponent.ordOmegaPower
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
    if (isOrdinal) {
      ordOmegaPower
    } else {
      val (positiveTerms, negativeTerms) = terms partition { _.coefficient > zero }
      SurrealNumber(
        GeneralizedOrdinal(Term(one, GeneralizedOrdinal(positiveTerms : _*))),
        GeneralizedOrdinal(Term(one, GeneralizedOrdinal(negativeTerms map { -_ } : _*)))
      )
    }
  }

  private[core] def ordOmegaPower: GeneralizedOrdinal = {
    GeneralizedOrdinal(Term(one, this))
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

  override def exp(n: Integer): SurrealNumber = {
    if (n.isZero)
      one
    else if (n < zero)
      exp(-n).reciprocal
    else
      this * exp(n - one)
  }

  override def abs: GeneralizedOrdinal = if (this < zero) -this else this

  override def birthday: GeneralizedOrdinal = {
    if (isOrdinal)
      this
    else
      GeneralizedOrdinal(terms map { case Term(coefficient, exponent) => Term(coefficient.abs, exponent) } : _*)
  }

  def nimSum(that: GeneralizedOrdinal): GeneralizedOrdinal = {
    if (!isOrdinal || !that.isOrdinal)
      throw ArithmeticException("NimSum applies only to ordinals.")
    val termsByExponent = (terms ++ that.terms).groupBy(_.exponent)
    val newTerms = termsByExponent.values.map {
      case IndexedSeq(term) => term
      case IndexedSeq(x, y) => Term(x.coefficient nimSum y.coefficient, x.exponent)
    }
    GeneralizedOrdinal(newTerms.toSeq : _*)
  }

  def nimProduct(that: GeneralizedOrdinal): GeneralizedOrdinal = {
    if (!isOrdinal || !that.isOrdinal)
      throw ArithmeticException("NimProduct applies only to ordinals.")
    val termProducts = {
      for (thisTerm <- this.terms; thatTerm <- that.terms) yield {
        thisTerm.nimProduct(thatTerm)
      }
    }
    termProducts.nimSum
  }

  // This is more efficient than this.nimProduct(this), taking advantage of characteristic 2
  def nimSquare: GeneralizedOrdinal = {
    val termSquares = {
      for (term <- terms) yield {
        term.nimProduct(term)
      }
    }
    termSquares.nimSum
  }

  def nimExp(n: Integer): GeneralizedOrdinal = {
    var bigInt: BigInt = n.bigIntValue
    var curpow: GeneralizedOrdinal = this
    var pow: GeneralizedOrdinal = one
    while (bigInt.bitLength > 0) {
      if (bigInt.testBit(0)) {
        pow = pow nimProduct curpow
      }
      curpow = curpow.nimSquare
      bigInt >>= 1
    }
    pow
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

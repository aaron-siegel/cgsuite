package org.cgsuite.core

import java.util.EnumSet

import cc.redberry.rings.bigint.BigInteger
import cc.redberry.rings.poly.multivar.MultivariateGCD
import cc.redberry.rings.scaladsl._
import cc.redberry.rings.scaladsl.syntax._
import org.cgsuite.core.GeneralizedOrdinal.Term
import org.cgsuite.core.Values._
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

import scala.collection.mutable
import scala.jdk.CollectionConverters._

object SurrealNumber {

  def apply(numerator: GeneralizedOrdinal, denominator: GeneralizedOrdinal): SurrealNumber = {
    construct(numerator, denominator)
  }

  private def construct(numerator: GeneralizedOrdinal, denominator: GeneralizedOrdinal, isReduced: Boolean = false): SurrealNumber = {
    (numerator, denominator) match {
      case (intNum: Integer, intDen: Integer) => RationalNumber(intNum, intDen)
      case _ if denominator < zero => construct(-numerator, -denominator, isReduced)
      case (_, Values.one) => numerator
      case (_, Values.zero) => RationalNumber(numerator.sign, zero)
      case _ if isReduced => SurrealNumberImpl(numerator, denominator)
      case _ =>
        val (reducedNumerator, reducedDenominator) = reduce(numerator, denominator)
        construct(reducedNumerator, reducedDenominator, isReduced = true)
    }
  }

  private val rings = mutable.Map[Int, MultivariateRing[BigInteger]]()

  private def ringForNTerms(n: Int): MultivariateRing[BigInteger] = {
    rings getOrElseUpdate (n, MultivariateRing(Z, (0 until n map { i => s"x$i" }).toArray))
  }

  // This can probably be made faster.
  // TODO Ensure reduced denominator is positive.
  private def reduce(x: GeneralizedOrdinal, y: GeneralizedOrdinal): (GeneralizedOrdinal, GeneralizedOrdinal) = {

    // Collect all "exponents of exponents".
    val allTerms = (expexp(x) ++ expexp(y)).toVector

    val termIndices = allTerms.zipWithIndex.toMap

    val ring = ringForNTerms(allTerms.length)

    val xPoly = toPoly(x, ring, termIndices)
    val yPoly = toPoly(y, ring, termIndices)

    val gcd = MultivariateGCD.PolynomialGCD(xPoly, yPoly)
    val xReduced = (xPoly /% gcd)._1
    val yReduced = (yPoly /% gcd)._1
    (toGeneralizedOrdinal(xReduced, allTerms), toGeneralizedOrdinal(yReduced, allTerms))

  }

  private def expexp(x: GeneralizedOrdinal): Set[GeneralizedOrdinal] = {
    x.terms.toSet flatMap { t: Term => t.exponent.terms } map { _.exponent }
  }

  private def toPoly(x: GeneralizedOrdinal, ring: MultivariateRing[BigInteger], termIndices: Map[GeneralizedOrdinal, Int]): MultivariatePolynomial[BigInteger] = {

    val monomials = x.terms map { case Term(coefficient, exponent) =>
      val varStr = exponent.terms map { case Term(eCoefficient, eExponent) =>
        assert(eCoefficient > zero)
        val index = termIndices(eExponent)
        s"x$index^$eCoefficient"
      } mkString "*"
      if (varStr == "") coefficient.toString
      else s"$coefficient*$varStr"
    }
    ring(monomials mkString "+")

  }
//(omega^(omega+1)-omega^omega-omega+1)/(omega-1)
  private def toGeneralizedOrdinal(poly: MultivariatePolynomial[BigInteger], allTerms: Vector[GeneralizedOrdinal]): GeneralizedOrdinal = {
    val terms = poly.collection.asScala map { monomial =>
      val coefficient = Integer(BigInt(monomial.coefficient.toByteArray))
      val exponents = monomial.exponents.zipWithIndex collect {
        case (exponent, index) if exponent != 0 => Term(Integer(exponent), allTerms(index))
      }
      Term(coefficient, GeneralizedOrdinal(exponents : _*))
    }
    GeneralizedOrdinal(terms.toSeq : _*)
  }

}

trait SurrealNumber extends NormalValue with OutputTarget with Ordered[SurrealNumber] {

  def numerator: GeneralizedOrdinal

  def denominator: GeneralizedOrdinal

  def unary_+ : SurrealNumber = this

  def unary_- : SurrealNumber = SurrealNumber(-numerator, denominator)

  def reciprocal: SurrealNumber = SurrealNumber(denominator, numerator)

  def abs: SurrealNumber = if (this < zero) -this else this

  def birthday: GeneralizedOrdinal = ???

  def sign: Integer = numerator.sign

  override def isLoopfree = true
  override def isNumber = true
  override def isNumberish = true
  override def isNumberTiny = true
  override def isPlumtree = true
  override def isPseudonumber = true
  override def isStopper = true
  override def isStopperSided = true
  override def isUptimal = true

  override def outcomeClass: OutcomeClass = {
    if (this > zero) OutcomeClass.L
    else if (this < zero) OutcomeClass.R
    else OutcomeClass.P
  }

  def +(other: SurrealNumber): SurrealNumber = SurrealNumber(
    numerator * other.denominator + denominator * other.numerator,
    denominator * other.denominator
  )

  def -(other: SurrealNumber): SurrealNumber = SurrealNumber(
    numerator * other.denominator - denominator * other.numerator,
    denominator * other.denominator
  )

  def *(other: SurrealNumber): SurrealNumber = SurrealNumber(
    numerator * other.numerator,
    denominator * other.denominator
  )

  def /(other: SurrealNumber): SurrealNumber = SurrealNumber(
    numerator * other.denominator,
    denominator * other.numerator
  )

  override def <=(other: SurrealNumber): Boolean = super[Ordered].<=(other)

  def exp(n: Integer): SurrealNumber = {
    (numerator exp n) / (denominator exp n)
  }

  def compare(that: SurrealNumber): Int = {
    this.numerator * that.denominator compare this.denominator * that.numerator
  }

  override def toOutput: StyledTextOutput = {

    val output = new StyledTextOutput()
    val numeratorOutput = numerator.toOutput(exponentSpot = -1)
    val denominatorOutput = denominator.toOutput(exponentSpot = -1)
    if (numerator.terms.size > 1)
      output.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH), EnumSet.of(Output.Mode.PLAIN_TEXT), "(")
    output.appendOutput(EnumSet.of(StyledTextOutput.Style.LOCATION_SUPERSCRIPT), numeratorOutput)
    if (numerator.terms.size > 1)
      output.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH), EnumSet.of(Output.Mode.PLAIN_TEXT), ")")
    output.appendMath("/")
    if (denominator.terms.size > 1)
      output.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH), EnumSet.of(Output.Mode.PLAIN_TEXT), "(")
    output.appendOutput(EnumSet.of(StyledTextOutput.Style.LOCATION_SUBSCRIPT), denominatorOutput)
    if (denominator.terms.size > 1)
      output.appendText(EnumSet.of(StyledTextOutput.Style.FACE_MATH), EnumSet.of(Output.Mode.PLAIN_TEXT), ")")
    output

  }


}

case class SurrealNumberImpl private[cgsuite] (numerator: GeneralizedOrdinal, denominator: GeneralizedOrdinal)
  extends SurrealNumber {
  assert(denominator != one)
  assert(!(numerator.isFinite && denominator.isFinite))
}

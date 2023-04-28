package org.cgsuite.core.impartial.arithmetic

import java.util

import com.typesafe.scalalogging.LazyLogging
import org.cgsuite.core.SmallInteger
import org.cgsuite.core.impartial.arithmetic.ImpartialTermAlgebra.Element

import scala.collection.mutable

object ImpartialTermAlgebra {

  def apply(qComponents: IndexedSeq[Int]): ImpartialTermAlgebra = {
    new ImpartialTermAlgebra(qComponents.distinct.sortBy(NimFieldCalculator.primePower).toArray)
  }

  object Element {

    def apply(terms: Seq[Int]): Element = new Element(terms.sorted)

  }

  case class Element private (terms: Seq[Int]) {

    def isOne: Boolean = {
      terms.size == 1 && terms.head == 0
    }

  }

}

class ImpartialTermAlgebra private (val qComponents: Array[Int]) extends LazyLogging {

  // TODO Validate that qComponents is closed under q/p

  val qDegrees = qComponents map { NimFieldCalculator.primePower(_)._1 }
  val degreeProducts = (0 to qDegrees.length).map { qDegrees.take(_).product }.toArray
  val termCount = degreeProducts.last
  val accumulator = new Array[Long]((termCount + 63) >> 6)

  // Associates to each component kappa_q the element given by
  // kappa_q^p (where p is the prime divisor of q).
  val kappaTable: IndexedSeq[Element] = qComponents.indices map { index =>
    (qComponents(index), qDegrees(index)) match {
      case (_, 2) => Element(Seq(degreeProducts(index), degreeProducts(index) - 1))
      case (q, p) if q == p =>
        val qSet = NimFieldCalculator.qSet(p)
        val excess = NimFieldCalculator.excess(p)
        assert(excess == 0 || excess == 1 || SmallInteger.isTwoPower(excess))
        val kappaBlocks = qSet.map { q2 =>
          val index = qComponents.indexOf(q2)
          assert(index >= 0, s"$q2 not in ${qComponents.toSeq}")
          degreeProducts(index)
        }
        Element(excess match {
          case 0 => kappaBlocks
          case _ => kappaBlocks :+ SmallInteger.lb(excess)
        })
      case _ => Element(Seq(degreeProducts(index - 1)))
    }
  }

  logger.info {
    val ktStrings = kappaTable map { element => s"{${element.terms.sorted mkString ","}}" }
    s"(q, kappa_q^p)   =   ${qComponents zip ktStrings map { case (q, ktString) => s"$q -> $ktString"} mkString "   "}"
  }

  //for (index <- qComponents.indices) { println(s"${qComponents(index)}: ${kappaTable(index) mkString ","}") }

  val qPowerTimesTermTable = new Array[Array[Array[Element]]](qComponents.length)
  qPowerTimesTermTable.indices foreach { index =>
    qPowerTimesTermTable(index) = new Array[Array[Element]](qDegrees(index))
    1 until qDegrees(index) foreach { qExponent =>
      qPowerTimesTermTable(index)(qExponent) = new Array[Element](termCount)
    }
  }

  for (qIndex <- qComponents.indices;
       qExponent <- 1 until qDegrees(qIndex);
       term <- 0 until termCount) {
    qPowerTimesTerm(qIndex, qExponent, term)
  }

  def qPowerTimesTerm(qIndex: Int, qExponent: Int, term: Int): Element = {

    val cached = qPowerTimesTermTable(qIndex)(qExponent)(term)
    if (cached != null) {
      cached
    } else {
      val result = qPowerTimesTermCalc(qIndex, qExponent, term)
      qPowerTimesTermTable(qIndex)(qExponent)(term) = result
      result
    }

  }

  def qPowerTimesTermCalc(qIndex: Int, qExponent: Int, term: Int): Element = {

    val p = qDegrees(qIndex)
    val qExponentInTerm = (term % degreeProducts(qIndex + 1)) / degreeProducts(qIndex)
    val qExponentNew = qExponent + qExponentInTerm

    if (qExponentNew < p) {
      Element(Seq(term + qExponent * degreeProducts(qIndex)))
    } else {

      assert(qExponentNew < 2 * p)
      val highOrderPart: Int = (term / degreeProducts(qIndex + 1)) * degreeProducts(qIndex + 1) + (qExponentNew % p) * degreeProducts(qIndex)
      val lowOrderPart: Int = term % degreeProducts(qIndex)
      logger.debug("parts = " + highOrderPart + "," + lowOrderPart)
      // We want to return highOrderPart * (lowOrderPart * kappa^p).
      // Retrieve the summands of kappa^p.
      val kappaExpansion = kappaTable(qIndex)
      logger.debug("kexp = " + kappaExpansion)
      // Compute the summands of lowOrderPart * kappa^p.
      val terms = mutable.Set[Int]()
      for (term <- kappaExpansion.terms) {
        for (productTerm <- termTimesTerm(lowOrderPart, term).terms) {
          if (terms.contains(productTerm))
            terms.remove(productTerm)
          else
            terms.add(productTerm)
        }
      }
      Element(terms.toSeq.sorted map { highOrderPart + _ })

    }

  }

  def termTimesTerm(x: Int, y: Int): Element = {
    assert(x >= 0)
    assert(y >= 0)
    var terms = mutable.Set(y)
    for (xIndex <- qComponents.length - 1 to 0 by -1) {
      val xExponent = (x % degreeProducts(xIndex + 1)) / degreeProducts(xIndex)
      if (xExponent > 0) {
        val newTerms = mutable.Set[Int]()
        for (term <- terms) {
          for (productTerm <- qPowerTimesTerm(xIndex, xExponent, term).terms) {
            if (newTerms.contains(productTerm)) newTerms.remove(productTerm)
            else newTerms.add(productTerm)
          }
        }
        terms = newTerms
      }
    }
    Element(terms.toSeq.sorted)
  }

  @inline
  private def flipAccumulatorTerm(x: Int): Unit = {
    accumulator(x / 64) ^= 1L << x
  }

  private def accumulatorContains(x: Int): Boolean = {
    (accumulator(x / 64) & (1L << x)) != 0
  }

  def accumulateTermProduct(x: Int, y: Int): Unit = {
    if (y == 0) {
      flipAccumulatorTerm(x)
    } else {
      // Take the highest-order remaining qComponent from y.
      var index = qComponents.length
      while (y / degreeProducts(index) == 0) {
        index -= 1
      }
      // Multiply that component by x to get a sum of terms.
      val product = qPowerTimesTerm(index, y / degreeProducts(index), x)
      // Apply each component of the sum.
      for (term <- product.terms) {
        accumulateTermProduct(term, y % degreeProducts(index))
      }
    }
  }

  def multiply(a: Element, b: Element): Element = {
    util.Arrays.fill(accumulator, 0)
    for (x <- a.terms; y <- b.terms) {
      accumulateTermProduct(x, y)
    }
    Element(0 until termCount collect {
      case x if accumulatorContains(x) => x
    })
  }

  // We can take advantage of characteristic 2 to provide an implementation
  // of square(a) that is faster than multiply(a, a).
  def square(a: Element): Element = {
    util.Arrays.fill(accumulator, 0)
    for (x <- a.terms) {
      accumulateTermProduct(x, x)
    }
    Element(0 until termCount collect {
      case x if accumulatorContains(x) => x
    })
  }

  def pow(x: Element, n: BigInt): Element = {
    var curpow: Element = x
    var result: Element = Element(Seq(0))
    var i = n
    var checkpointTime = System.currentTimeMillis()
    while (i != BigInt(0)) {
      if (i.testBit(0)) {
        result = multiply(result, curpow)
      }
      curpow = square(curpow)
      if (System.currentTimeMillis() - checkpointTime >= 60000L) {
        val bitsComplete = n.bitLength - i.bitLength
        logger.info(f"$bitsComplete/${n.bitLength} bits complete (${bitsComplete.toDouble/n.bitLength}%2.1f%%); curpow/result has ${curpow.terms.size}/${result.terms.size} terms")
        checkpointTime = System.currentTimeMillis()
      }
      i >>= 1
    }
    result
  }

}

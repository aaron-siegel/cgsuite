package org.cgsuite.core.impartial.arithmetic

import java.util

import ch.qos.logback.classic.{Level, Logger}
import com.typesafe.scalalogging.LazyLogging
import org.cgsuite.core.{GeneralizedOrdinal, Integer, SmallInteger, Values}
import org.cgsuite.dsl._
import org.slf4j.LoggerFactory
import better.files._

import scala.collection.mutable

object AlphaCalc extends LazyLogging {

  def main(args: Array[String]): Unit = {
    run()
  }

  private val excessCache = mutable.Map[Int, Int]()
  private val qSetCache = mutable.Map[Int, Array[Int]]()

  def run(from: Int = 3, preload: Boolean = false): Unit = {
    if (preload) {
      FieldTable.excess.indices.drop(1) foreach { k => excessCache.put(FieldTable.primes(k), FieldTable.excess(k)) }
      FieldTable.qSet.indices.drop(1) foreach { k => qSetCache.put(FieldTable.primes(k), FieldTable.qSet(k).toArray) }
    }
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.WARN)
    println()
    println(f"${"p"}%4s ${"f(p)"}%4s ${"Q(f(p))"}%15s ${"exponent"}%7s excess ${"alpha_p"}%20s ${"t(sec)"}%8s     alpha_seq")
    var k = FieldTable.primes.indexOf(from)
    while (true) {
      val startTime = System.currentTimeMillis()
      val p = FieldTable.primes(k)
      print(f"$p%4d ${f(p)}%4d")
      val qSet = this.qSet(p)
      val qSetStr = s"{${qSet mkString ","}}"
      val exponent = ImpartialTermAlgebra((qSet flatMap componentsOf).distinct).termCount
      print(f" $qSetStr%15s $exponent%8d")
      val excess = this.excess(p)
      print(f" $excess%6d ${alphap(p)}%20s")
      val alphaSeq = (1 to k) map { i => this.excess(FieldTable.primes(i)) } mkString ","
      val durationSecs = (System.currentTimeMillis() - startTime) / 1000.0
      println(f" $durationSecs%8.1f     $alphaSeq")
      val qSetSeq = (1 to k) map { i =>
        s"{${this.qSet(FieldTable.primes(i)) mkString ","}}"
      } mkString ", "
      val file = "local" / "alpha-calc.txt"
      file appendLine alphaSeq
      file appendLine qSetSeq
      k += 1
    }
  }

  def f(n: Int) = {
    var result = 0
    var twoPower = 1
    do {
      result += 1
      twoPower = (twoPower << 1) % n
    } while (twoPower != 1)
    result
  }

  def dividesTwoPowerMinus1(n: Int, pow: Int): Boolean = {
    var twoPower = 1
    for (_ <- 1 to pow) {
      twoPower = (twoPower << 1) % n
    }
    twoPower == 1
  }

  def componentsOf(q: Int): Set[Int] = {
    val (p, _) = primePower(q)
    var pn = 1
    val components = mutable.Set[Int]()
    while (pn < q) {
      pn *= p
      components += pn
    }
    components ++= (qSet(p) flatMap componentsOf)
    val pExcess = excess(p)
    if (pExcess == 2 || pExcess == 4) {
      components += pExcess
    } else if (pExcess > 1) {
      sys.error("not handled yet")
    }
    components.toSet
  }

  def excess(p: Int): Int = {

    if (p == 2)
      return 0
    if (excessCache contains p)
      return excessCache(p)

    val qs = qSet(p)
    logger.info(s"Computing excess for p = $p (Q-Set = {${qs mkString ","}}).")
    var components = (qs flatMap componentsOf).distinct
    var algebra = ImpartialTermAlgebra(components)
    var m = 0L
    var done = false
    while (!done) {
      if (m >= (1 << 30))
        sys.error("m >= 2^30")
      val alpha = mutable.Set[Int]()
      if (m > 0) {
        if (m > 1) {
          val q = 1 << (SmallInteger.lb(SmallInteger.lb(m.toInt)) + 1)
          if (!components.contains(q)) {
            components = components :+ q
            algebra = ImpartialTermAlgebra(components)
          }
        }
        0 to SmallInteger.lb(m.toInt) foreach { i =>
          if ((m & (1L << i)) != 0)
            alpha += i
        }
      }
      for (q <- qSet(p)) {
        alpha += algebra.degreeProducts(algebra.qComponents.indexOf(q))
      }
      logger.info(s"[p = $p] Trying m = $m, alpha = ${alpha.toSeq.sorted} (${algebra.termCount} terms)")
      if (dividesTwoPowerMinus1(p, algebra.termCount)) {
        assert(((BigInt(1) << algebra.termCount) - BigInt(1)) % BigInt(p) == BigInt(0))
        val testpow = ((BigInt(1) << algebra.termCount) - BigInt(1)) / BigInt(p)
        val alphaTerm = Element(alpha.toSeq.sorted)
        val pow = algebra.pow(alphaTerm, testpow)
        if (!pow.isOne) {
          done = true
        }
      }
      if (!done) {
        m += 1
      }
    }

    excessCache(p) = m.toInt
    m.toInt

  }

  def qSet(p: Int): Array[Int] = {

    if (p == 2)
      return Array.empty
    if (qSetCache contains p)
      return qSetCache(p)

    val result = kappa(f(p)).sortBy(primePower)
    qSetCache(p) = result
    result

  }

  def kappa(h: Int): Array[Int] = {

    // Find the smallest prime divisor of n
    val p = FieldTable.primes.find { h % _ == 0 } getOrElse {
      throw new IllegalArgumentException(h.toString)
    }

    // Find the largest power of p that divides n
    var q = p
    while (h % (q * p) == 0) {
      q *= p
    }

    if (h == q) {
      Array(q)
    } else {

      val g = h / q
      val kappag = kappa(g)
      val components = (kappag flatMap componentsOf).distinct.sortBy(primePower)
      logger.info(s"Computing the degree of kappa($g) (components = {${components mkString ","}}).")
      val algebra = ImpartialTermAlgebra(components)
      logger.info(s"Field has exponent ${algebra.termCount}.")
      val kappagInAlgebra = Element(kappag map { r => algebra.degreeProducts(algebra.qComponents.indexOf(r))})

      var pow = kappagInAlgebra
      var degree = 0
      do {
        pow = algebra.multiply(pow, pow)
        degree += 1
      } while (pow != kappagInAlgebra)

      logger.info(s"Degree is $degree.")

      if (degree % q == 0)
        kappag
      else
        kappag :+ q

    }

  }

  def alphap(p: Int): GeneralizedOrdinal = {
    val qSet = this.qSet(p)
    val excess = this.excess(p)
    val terms = qSet map kappaq
    Integer(excess) + terms.sum
  }

  def kappaq(q: Int): GeneralizedOrdinal = {
    val (p, n) = primePower(q)
    if (p == 2) {
      Values.two.exp(Values.two.exp(Integer(n - 1)).asInstanceOf[Integer]).asInstanceOf[Integer]
    } else {
      val k = FieldTable.primes.indexOf(p)
      // Value is omega^(omega^(k-1) * p^(n-1))
      val exponent = Integer(k - 1).omegaPower.asInstanceOf[GeneralizedOrdinal] * Integer(p).exp(Integer(n - 1)).asInstanceOf[GeneralizedOrdinal]
      exponent.omegaPower.asInstanceOf[GeneralizedOrdinal]
    }
  }

  // Returns (p, n), where q = p^n, or throws an exception if
  // q is not of the form p^n for n >= 1.
  def primePower(q: Int): (Int, Int) = {
    if (q <= 1) {
      throw new IllegalArgumentException(s"Not a prime power: $q")
    }
    val p = FieldTable.primes.find(q % _ == 0).getOrElse {
      throw new IllegalArgumentException(s"Base out of range: $q")
    }
    var n = 1
    var pn = p.toLong
    while (pn < q) {
      n += 1
      pn *= p
    }
    if (pn != q) {
      throw new IllegalArgumentException(s"Not a prime power: $q")
    }
    (p, n)
  }

}

object ImpartialTermAlgebra {

  def apply(qComponents: Seq[Int]): ImpartialTermAlgebra = {
    new ImpartialTermAlgebra(qComponents.sortBy(AlphaCalc.primePower).toArray)
  }

}

class ImpartialTermAlgebra private (val qComponents: Array[Int]) extends LazyLogging {

  // TODO Validate that qComponents is closed under q/p

  val qDegrees = qComponents map { AlphaCalc.primePower(_)._1 }
  val degreeProducts = (0 to qDegrees.length).map { qDegrees.take(_).product }.toArray
  val termCount = degreeProducts.last
  val accumulator = new Array[Long]((termCount + 63) >> 6)

  // Associates to each component kappa_q the element given by
  // kappa_q^p (where p is the prime divisor of q).
  val kappaTable: IndexedSeq[Element] = qComponents.indices map { index =>
    (qComponents(index), qDegrees(index)) match {
      case (_, 2) => Element(Seq(degreeProducts(index), degreeProducts(index) - 1))
      case (q, p) if q == p =>
        val qSet = AlphaCalc.qSet(p)
        val excess = AlphaCalc.excess(p)
        assert(excess == 0 || excess == 1 || SmallInteger.isTwoPower(excess))
        val kappaBlocks = qSet.toSeq.map { q2 =>
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

  def pow(x: Element, n: BigInt): Element = {
    var curpow: Element = x
    var result: Element = Element(Seq(0))
    var i = n
    while (i != BigInt(0)) {
      if (i.testBit(0)) {
        result = multiply(result, curpow)
      }
      curpow = multiply(curpow, curpow)
      i >>= 1
    }
    result
  }

}

object Element {

  def apply(terms: Seq[Int]): Element = new Element(terms.sorted)

}

case class Element private (terms: Seq[Int]) {

  def isOne: Boolean = {
    terms.size == 1 && terms.head == 0
  }

}

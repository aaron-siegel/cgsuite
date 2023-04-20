package org.cgsuite.core.impartial.arithmetic

import java.util

import ch.qos.logback.classic.Logger
import com.typesafe.scalalogging.LazyLogging
import org.cgsuite.core.{GeneralizedOrdinal, Integer, SmallInteger, Values}
import org.cgsuite.dsl._
import org.slf4j.LoggerFactory
import better.files._
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.{ConsoleAppender, OutputStreamAppender}

import scala.collection.mutable

object AlphaCalc extends LazyLogging {

  def main(args: Array[String]): Unit = {
    run(from = 263, preloadTo = 1009)
  }

  private val excessCache = mutable.Map[Int, Int]()
  private val qSetCache = mutable.Map[Int, IndexedSeq[Int]]()

  def run(from: Int = 3, preloadTo: Int = 0): Unit = {
    val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger]
    val consoleAppender = rootLogger.getAppender("STDOUT")
    rootLogger.detachAppender("STDOUT")
    val appender = new OutputStreamAppender[ILoggingEvent]
    appender.setOutputStream("local/alpha-calc.log".toFile.newOutputStream)
    appender.setEncoder(consoleAppender.asInstanceOf[ConsoleAppender[ILoggingEvent]].getEncoder)
    appender.start()
    rootLogger.addAppender(appender)
    val kPreload = FieldTable.primes.indexOf(preloadTo)
    FieldTable.excess.indices.slice(1, kPreload + 1) foreach { k => excessCache.put(FieldTable.primes(k), FieldTable.excess(k)) }
    FieldTable.qSet.indices.slice(1, kPreload + 1) foreach { k => qSetCache.put(FieldTable.primes(k), FieldTable.qSet(k)) }
    println()
    println(f"${"p"}%4s ${"f(p)"}%4s ${"Q(f(p))"}%15s ${"exponent"}%7s excess ${"alpha_p"}%20s ${"t(sec)"}%8s     alpha_seq")
    var k = FieldTable.primes.indexOf(from)
    while (true) {
      val startTime = System.currentTimeMillis()
      val p = FieldTable.primes(k)
      print(f"$p%4d ${f(p)}%4d")
      val qSet = this.qSet(p)
      val qSetStr = s"{${qSet mkString ","}}"
      val exponent = ImpartialTermAlgebra((qSet flatMap primitiveComponents).distinct).termCount
      print(f" $qSetStr%15s $exponent%8d")
      val excess = this.excess(p)
      print(f" $excess%6d ${alphap(p)}%20s")
      val excessSeq = (1 to k) map { i =>
        this.excessCache.getOrElse(FieldTable.primes(i), "??")
      } mkString ","
      val durationSecs = (System.currentTimeMillis() - startTime) / 1000.0
      println(f" $durationSecs%8.1f     $excessSeq")
      val qSetSeq = (1 to k) map { i =>
        this.qSetCache.get(i) match {
          case Some(qSet) => s"{${qSet mkString ","}}"
          case None => "??"
        }
      } mkString ", "
      val file = "local" / "alpha-calc.txt"
      file appendLine excessSeq
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

  def primitiveComponents(q: Int): Set[Int] = {
    val (p, _) = primePower(q)
    var pn = 1
    val components = mutable.Set[Int]()
    while (pn < q) {
      pn *= p
      components += pn
    }
    components ++= (qSet(p) flatMap primitiveComponents)
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

    // The Q-set of p is the collection of prime powers q such that kappa_q
    // is a summand of kappa_{f(p)}.
    val qSet = this.qSet(p)
    logger.info(s"[p = $p] Computing excess (Q-Set = {${qSet mkString ","}}).")

    // Now we construct the set of all primitive components of kappa_{f(p)}:
    // That is, all prime powers q such that kappa_q appears in the field closure
    // of kappa_{f(p)}.
    var components = qSet.flatMap(primitiveComponents).distinct.sortBy(primePower)

    // The finite summand of kappa_{f(p)}. If the Q-set contains an even q, then
    // this will be 2^(q/2); otherwise 0.
    // (It's 2^(q/2) because kappa_{2^n} = 2^2^(n-1).)
    val finitePart: Integer = qSet.find(_ % 2 == 0) match {
      case Some(n) => two.intExp(Integer(n) div two)
      case None => zero
    }

    var excess = {
      val fp = f(p)
      if (fp != 2 && fp % 2 == 0 && SmallInteger.isThreePower(fp / 2)) {
        4       // f(p) = 2*3^k
      } else if (qSet.size == 1 && qSet.head % 2 != 0) {
        1       // Q-set is a single odd prime power
      } else {
        0
      }
    }
    var done = false
    while (!done) {

      val adjustedFinitePart = finitePart + Integer(excess)
      // Determine any new field components needed to represent the excess;
      // these are all the q-values corresponding to
      // Fermat 2-powers that are <= finitePart + m
      val finiteComponents = {
        if (adjustedFinitePart <= one) {
          IndexedSeq.empty
        } else {
          for (k <- 0 to adjustedFinitePart.lb.lb.intValue)
            yield 1 << (k + 1)
        }
      }

      // Construct the term algebra generated by all the relevant components
      val algebra = ImpartialTermAlgebra(components ++ finiteComponents)

      // Construct the representation of alpha_p in the term algebra
      val alphaFiniteTerms = {
        if (adjustedFinitePart.isZero) {
          IndexedSeq.empty
        } else {
          (0 to adjustedFinitePart.lb.intValue) filter { adjustedFinitePart.bigIntValue.testBit(_) }
        }
      }
      val alphaLargeTerms = qSet collect {
        case q if q % 2 != 0 => algebra.degreeProducts(algebra.qComponents.indexOf(q))
      }
      val alphaTerms = (alphaFiniteTerms ++ alphaLargeTerms).sorted
      val alpha = Element(alphaTerms)

      // Finally, check whether alpha has an existing pth root. Since alpha^(2^E - 1) = 1,
      // where E is the exponent (`termCount`) of the algebra, this is equivalent to checking
      // that alpha^((2^E - 1) / p) = 1.
      logger.info(s"[p = $p] Trying excess = $excess, alpha = $alphaTerms (${algebra.termCount} terms)")
      if (dividesTwoPowerMinus1(p, algebra.termCount)) {
        val testpow = ((BigInt(1) << algebra.termCount) - BigInt(1)) / BigInt(p)
        val pow = algebra.pow(alpha, testpow)
        if (!pow.isOne) {
          done = true
        }
      } else {
        logger.info(s"[p = $p] dividesTwoPowerMinus1 failed.")
        done = true
      }

      if (!done) {
        excess += 1
      }

    }

    excessCache(p) = excess
    excess

  }

  def qSet(p: Int): IndexedSeq[Int] = {

    if (p == 2)
      return Vector.empty
    if (qSetCache contains p)
      return qSetCache(p)

    val result = kappa(f(p))
    qSetCache(p) = result
    result

  }

  def kappa(h: Int): IndexedSeq[Int] = {

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
      Vector(q)
    } else {

      val g = h / q
      val kappag = kappa(g)
      val components = (kappag flatMap primitiveComponents).distinct.sortBy(primePower)
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
        (kappag :+ q).sortBy(primePower)

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

  def apply(qComponents: IndexedSeq[Int]): ImpartialTermAlgebra = {
    new ImpartialTermAlgebra(qComponents.distinct.sortBy(AlphaCalc.primePower).toArray)
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

  def pow(x: Element, n: BigInt): Element = {
    var curpow: Element = x
    var result: Element = Element(Seq(0))
    var i = n
    var checkpointTime = System.currentTimeMillis()
    while (i != BigInt(0)) {
      if (i.testBit(0)) {
        result = multiply(result, curpow)
      }
      curpow = multiply(curpow, curpow)
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

object Element {

  def apply(terms: Seq[Int]): Element = new Element(terms.sorted)

}

case class Element private (terms: Seq[Int]) {

  def isOne: Boolean = {
    terms.size == 1 && terms.head == 0
  }

}

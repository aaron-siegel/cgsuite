package org.cgsuite.core.impartial.arithmetic

import better.files._
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.{ConsoleAppender, OutputStreamAppender}
import com.typesafe.scalalogging.LazyLogging
import org.cgsuite.core.impartial.arithmetic.ImpartialTermAlgebra.Element
import org.cgsuite.core.{GeneralizedOrdinal, Integer, SmallInteger, Values}
import org.cgsuite.dsl._
import org.slf4j.LoggerFactory

import scala.collection.mutable

/* A tool for calculating Lenstra's constants Q(p) and excess(p) for odd primes p.
 *
 * Multiplication in transfinite nim arithmetic depends on the values of Q(p) and
 * excess(p). Calculating those values is computationally expensive, so in CGSuite's
 * implementation of transfinite nim arithmetic, they are defined as constants
 * (cf. NimFieldConstants.scala). This tool can be used to recompute them - thus
 * documenting their provenance - or, given sufficient compute resources, to extend
 * the list.
 *
 * The tool is essentially a separate implementation of transfinite nim arithmetic,
 * specifically optimized for computing Q(p) and excess(p). The strategy is to
 * construct a complete materialized representation of the minimal finite field
 * containing kappa_{f(p)} (Lenstra's notation) and carry out the requisite operations
 * within it.
 *
 * The notation and approach follow Lenstra (1977), On the algebraic closure of two.
 */
object NimFieldCalculator extends LazyLogging {

  def main(args: Array[String]): Unit = {
    run()
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
    this.preloadTo(preloadTo)
    println()
    println(f"${"p"}%4s ${"f(p)"}%4s ${"Q(f(p))"}%15s ${"exponent"}%7s excess ${"alpha_p"}%20s ${"t(sec)"}%8s     alpha_seq")
    var k = NimFieldConstants.primes.indexOf(from)
    while (true) {
      val startTime = System.currentTimeMillis()
      val p = NimFieldConstants.primes(k)
      print(f"$p%4d ${f(p)}%4d")
      val qSet = this.qSet(p)
      val qSetStr = s"{${qSet mkString ","}}"
      val exponent = ImpartialTermAlgebra((qSet flatMap primitiveComponents).distinct).termCount
      print(f" $qSetStr%15s $exponent%8d")
      val excess = this.excess(p)
      print(f" $excess%6d ${alpha(p)}%20s")
      val excessSeq = (1 to k) map { i =>
        this.excessCache.getOrElse(NimFieldConstants.primes(i), "??")
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

  def preloadTo(p: Int): Unit = {
    val kPreload = NimFieldConstants.primes.indexOf(p)
    NimFieldConstants.excess.indices.slice(1, kPreload + 1) foreach { k => excessCache.put(NimFieldConstants.primes(k), NimFieldConstants.excess(k)) }
    NimFieldConstants.qSet.indices.slice(1, kPreload + 1) foreach { k => qSetCache.put(NimFieldConstants.primes(k), NimFieldConstants.qSet(k)) }
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

  // true if   2^pow === 1 (mod n).
  def dividesTwoPowerMinus1(n: Int, pow: Int): Boolean = {
    var twoPower = 1
    for (_ <- 1 to pow) {
      twoPower = (twoPower << 1) % n
    }
    twoPower == 1
  }

  // The set of all prime powers r such that kappa_r appears
  // in the field closure of kappa_q.
  def primitiveComponents(q: Int): Set[Int] = {

    val (p, _) = primePower(q)
    val components = mutable.Set[Int]()

    // Add kappa_{p^i} for all p^i <= q
    var pn = 1
    while (pn < q) {
      pn *= p
      components += pn
    }

    // Recursively add all primitive components of the Q-set
    // for p (as those are summands of alpha_p)
    components ++= (qSet(p) flatMap primitiveComponents)

    // Now we need to ensure that the finite part of alpha_p is
    // represented, including the excess.
    components ++= finiteComponents(p, Integer(excess(p)))

    components.toSet

  }

  // Determines the finite kappa-components of the ordinal
  // kappa_{f(p)} + excess
  // That is, determines the set of 2-powers q such that
  // kappa_q appears in the field closure of kappa_{f(p)} + excess.
  // If excess = excess(p), then this will be the set of 2-powers q
  // such that kappa_q appears in the field closure of alpha_p.
  // But excess is given a parameter, because this method is necessary
  // for *computing* excess(p).
  def finiteComponents(p: Int, excess: Integer): IndexedSeq[Int] = {

    val finiteSummand = this.finiteSummand(p, excess)

    if (finiteSummand <= one) {
      IndexedSeq.empty
    } else {
      for (k <- 0 to finiteSummand.lb.lb.intValue)
        yield 1 << (k + 1)
    }

  }

  // Determines the finite part of the ordinal kappa_{f(p)} + excess
  // That is, the integer n such that
  // kappa_{f(p)} + excess = omega*alpha + n
  def finiteSummand(p: Int, excess: Integer): Integer = {

    val qSet = this.qSet(p)

    // The finite summand of kappa_{f(p)}. If the Q-set contains an even q, then
    // this will be 2^(q/2); otherwise 0.
    // (It's 2^(q/2) because kappa_{2^n} = 2^2^(n-1).)
    val baseFiniteSummand: Integer = qSet.find(_ % 2 == 0) match {
      case Some(n) => two.intExp(Integer(n) div two)
      case None => zero
    }

    baseFiniteSummand + excess

  }

  // The Q-set of p. Lenstra showed that for all p, we have
  // alpha_p = kappa_{f(p)} + m, where
  // kappa_{f(p)} = kappa_{q_1} + ... + kappa_{q_k} for distinct maximal
  // prime-power divisors q_1, ..., q_k of f(p), and
  // m is a nonnegative integer.
  // The set {q_1, ..., q_k} is the "Q-set" for p, and the integer m is
  // the "excess". This method computes the Q-set.
  def qSet(p: Int): IndexedSeq[Int] = {

    if (p == 2)
      return Vector.empty
    if (qSetCache contains p)
      return qSetCache(p)

    val result = kappaSet(f(p))
    qSetCache(p) = result
    result

  }

  // The set {q_1, ..., q_k} such that
  // kappa_h = kappa_{q_1}, ..., kappa_{q_k}.
  def kappaSet(h: Int): IndexedSeq[Int] = {

    // Find the smallest prime divisor of n
    val p = NimFieldConstants.primes.find { h % _ == 0 } getOrElse {
      throw new IllegalArgumentException(h.toString)
    }

    // Find the largest power of p that divides n
    var q = p
    while (h % (q * p) == 0) {
      q *= p
    }

    if (h == q) {
      // h is a prime power; the answer is just {h}
      Vector(h)
    } else {

      val g = h / q
      // Now kappa_h = kappa_g if q divides deg(kappa_g); kappa_g + kappa_q otherwise.
      // (Lenstra, Theorem 2.1)
      val kappagSet = kappaSet(g)
      // Determine all the kappa_q's appearing in the field generated by kappa_g
      val components = (kappagSet flatMap primitiveComponents).distinct.sortBy(primePower)
      logger.info(s"Computing the degree of kappa($g) (components = {${components mkString ","}}).")
      // Construct the associated term algebra
      val algebra = ImpartialTermAlgebra(components)
      logger.info(s"Field has exponent ${algebra.termCount}.")
      // Determine the element in the term algebra that represents kappag
      val kappagInAlgebra = Element(kappagSet map { r => algebra.basis(algebra.qComponents.indexOf(r)) })

      // Now determine deg(kappa_g). This is equal to the least n such that
      // (kappa_g)^(2^n) == kappa_g. (Lenstra, proof of Theorem 3.5).
      var pow = kappagInAlgebra
      var degree = 0
      do {
        pow = algebra.square(pow)
        degree += 1
      } while (pow != kappagInAlgebra)

      logger.info(s"Degree is $degree.")

      if (degree % q == 0)
        kappagSet
      else
        (kappagSet :+ q).sortBy(primePower)

    }

  }

  // The Lenstra excess of p. This is equal to the least integer m such that
  // kappa_{f(p)} + m  has no pth root in kappa_p.
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
    val components = qSet.flatMap(primitiveComponents).distinct.sortBy(primePower)

    // Now try each possible value of m = 0, 1, ..., until we find one for which
    // kappa_{f(p)} has no pth root.
    // By Lenstra eq. (4.3)-(4.4), we can start with m = 1 when the
    // Q-set is {q} for some odd q, and we can start with m = 4 when f(p) has
    // the exact form 2*3^k, k >= 1.
    var excess: Integer = {
      val fp = f(p)
      if (fp != 2 && fp % 2 == 0 && SmallInteger.isThreePower(fp / 2)) {
        four       // f(p) = 2*3^k
      } else if (qSet.size == 1 && qSet.head % 2 != 0) {
        one        // Q-set is a single odd prime power
      } else {
        zero
      }
    }

    var done = false
    while (!done) {

      // Construct the term algebra corresponding to the finite field generated
      // by kappa_{f(p)} + excess. This consists of the components for kappa_{f(p)}
      // computed above, plus any new finite components introduced by adding the excess.
      val finiteSummand = this.finiteSummand(p, excess)
      val finiteComponents = this.finiteComponents(p, excess)
      val algebra = ImpartialTermAlgebra(components ++ finiteComponents)

      // Construct the representation of alpha_p in the term algebra.
      val alphaFiniteTerms = {
        if (finiteSummand.isZero) {
          IndexedSeq.empty
        } else {
          (0 to finiteSummand.lb.intValue) filter { finiteSummand.bigIntValue.testBit(_) }
        }
      }
      val alphaLargeTerms = qSet collect {
        case q if q % 2 != 0 => algebra.basis(algebra.qComponents.indexOf(q))
      }
      val alphaTerms = (alphaFiniteTerms ++ alphaLargeTerms).sorted
      val alpha = Element(alphaTerms)

      // Finally, check whether alpha has an existing pth root. Since alpha^(2^E - 1) = 1,
      // where E is the exponent (`termCount`) of the algebra, this is equivalent to checking
      // that alpha^((2^E - 1) / p) = 1.
      // If alpha has no existing pth root - either because p does not divide 2^E - 1, or because
      // alpha^((2^E - 1) / p) != 1 - then set done = true.
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
        excess += one
      }

    }

    excessCache(p) = excess.intValue
    excess.intValue

  }

  // The ordinal alpha_p. This is used for human-readable logging.
  def alpha(p: Int): GeneralizedOrdinal = {
    val qSet = this.qSet(p)
    val excess = this.excess(p)
    val terms = qSet map kappa
    Integer(excess) + terms.sum
  }

  // The ordinal kappa_q. This is used for human-readable logging.
  def kappa(q: Int): GeneralizedOrdinal = {
    val (p, n) = primePower(q)
    if (p == 2) {
      two.intExp(two.intExp(Integer(n - 1)))
    } else {
      val k = NimFieldConstants.primes.indexOf(p)
      // Value is omega^(omega^(k-1) * p^(n-1))
      val exponent = Integer(k - 1).ordOmegaPower * Integer(p).intExp(Integer(n - 1))
      exponent.ordOmegaPower
    }
  }

  // Returns (p, n), where q = p^n, or throws an exception if
  // q is not of the form p^n for n >= 1.
  def primePower(q: Int): (Int, Int) = {
    if (q <= 1) {
      throw new IllegalArgumentException(s"Not a prime power: $q")
    }
    val p = NimFieldConstants.primes.find(q % _ == 0).getOrElse {
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

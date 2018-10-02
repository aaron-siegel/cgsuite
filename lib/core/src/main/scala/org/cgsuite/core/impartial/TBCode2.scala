package org.cgsuite.core.impartial

import org.cgsuite.exception.MalformedCodeException

import scala.collection.mutable

object TBCode2 {

  val base32Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUV"

  def apply(str: String): TBCode2 = parse(str)

  def parse(str: String): TBCode2 = {

    var index = 0
    var inAdditivePart = true
    var inPeriodPart = false
    val digits = mutable.MutableList[Digit2]()
    val additiveDigits = mutable.MutableList[Digit2]()
    var preperiod = -1

    while (index < str.length) {

      var digit: Digit2 = null

      str.charAt(index) match {

        case '.' =>
          if (!inAdditivePart) throw MalformedCodeException(str)
          inAdditivePart = false

        case '[' =>
          if (inPeriodPart || inAdditivePart) throw MalformedCodeException(str)
          inPeriodPart = true
          preperiod = digits.length

        case ']' =>
          if (!inPeriodPart || index != str.length - 1) throw MalformedCodeException(str)
          inPeriodPart = false

        case '*' =>
          val (constraint, incr) = parseConstraint(str, index + 1)
          digit = Digit2(Vector(constraint), allowMore = true)
          index += incr

        case '{' =>
          val endIndex = str.indexOf('}', index)
          if (endIndex == -1)
            throw MalformedCodeException(str)
          val (globalConstraint, incr) = parseConstraint(str, endIndex + 1)
          digit = parseConstraintList(str, str.substring(index + 1, endIndex), globalConstraint)
          index = endIndex + incr

        case '&' =>
          val endIndex = str.indexOf(';', index)
          if (endIndex == -1)
            throw MalformedCodeException(str)
          val base = try {
            str.substring(index + 1, endIndex).toLong
          } catch {
            case exc: NumberFormatException => throw MalformedCodeException(str)
          }
          val (constraint, incr) = parseConstraint(str, endIndex + 1)
          digit = Digit2(base, constraint)
          index = endIndex + incr

        case ch =>
          val base = base32Chars indexOf ch.toUpper
          if (base == -1) throw MalformedCodeException(str)
          val (constraint, incr) = parseConstraint(str, index + 1)
          digit = Digit2(base, constraint)
          index += incr

      }

      if (digit != null) {
        if (inAdditivePart)
          additiveDigits += digit
        else
          digits += digit
      }

      index += 1

    }

    if (additiveDigits.isEmpty || inAdditivePart || inPeriodPart)
      throw MalformedCodeException(str)

    TBCode2(
      additiveDigits.last +: digits.toVector,
      additiveDigits.toVector.reverse.tail,
      if (preperiod == -1) 0 else digits.length - preperiod
    )

  }

  def parseConstraint(str: String, index: Int): (DigitConstraint.Value, Int) = {

    if (index >= str.length) {
      (DigitConstraint.Unconstrained, 0)
    } else {
      str.charAt(index) match {
        case '!' => (DigitConstraint.PairwiseUnequal, 1)
        case '?' => (DigitConstraint.SomeUnequal, 1)
        case '=' => (DigitConstraint.AllEqual, 1)
        case _ => (DigitConstraint.Unconstrained, 0)
      }
    }

  }

  def parseConstraintList(codeString: String, digitString: String, defaultConstraint: DigitConstraint.Value): Digit2 = {

    val (allowMore, baseStr) = {
      if (digitString endsWith "+")
        (true, digitString dropRight 1)
      else
        (false, digitString)
    }

    val tokens = baseStr.split(",")

    val indicesWithConstraints = tokens map { token =>
      val (explicitConstraint, incr) = parseConstraint(token, token.length - 1)
      if (explicitConstraint == DigitConstraint.Unconstrained)
        (token.toInt, defaultConstraint)
      else {
        if (defaultConstraint != DigitConstraint.Unconstrained)
          throw MalformedCodeException(codeString)
        (token.dropRight(incr).toInt, explicitConstraint)
      }
    }

    val maxExplicitHeaps = indicesWithConstraints.map { _._1 }.max
    if (maxExplicitHeaps > 63) {
      throw MalformedCodeException(codeString)
    }

    val constraintArray = Array.fill(maxExplicitHeaps + 1)(DigitConstraint.Disallowed)
    indicesWithConstraints foreach { case (index, constraint) => constraintArray(index) = constraint }
    Digit2(constraintArray.toVector, allowMore)

  }

  def apply(digits: IndexedSeq[Digit2], additiveDigits: IndexedSeq[Digit2], period: Int = 0): TBCode2 = {

    if (period < 0) throw new IllegalArgumentException("period < 0")
    if (period > digits.length) throw new IllegalArgumentException("period > digits.length")

    // Normalize the TBCode.
    val additiveDigitsNormalized = removeTrailingZeroes(additiveDigits)
    val (periodNormalized, digitsNormalized1) = {
      if (digits takeRight period forall { _.isZero })
        (0, digits dropRight period)
      else
        (period, digits)
    }
    val digitsNormalized2 = {
      if (periodNormalized > 0)
        digitsNormalized1
      else
        removeTrailingZeroes(digitsNormalized1)
    }
    val digitsNormalized3 = {
      if (digitsNormalized2.isEmpty)
        Vector(Digit2.zero)
      else
        digitsNormalized2
    }
    new TBCode2(digitsNormalized3, additiveDigitsNormalized, periodNormalized)

  }

  private def removeTrailingZeroes(digits: IndexedSeq[Digit2]): IndexedSeq[Digit2] = {

    val lastNonzero = digits lastIndexWhere { !_.isZero }
    digits take (lastNonzero + 1)

  }

}

case class TBCode2(
  digits: IndexedSeq[Digit2],
  additiveDigits: IndexedSeq[Digit2] = Vector.empty,
  period: Int = 0
  ) {

  assert(digits.nonEmpty)
  assert(period < digits.length)

  val preperiod = digits.length - period

  val maxHeaps = (digits ++ additiveDigits).map { _.maxHeaps }.max

  val maxTokensRemoved = {
    if (period > 0)
      Int.MaxValue
    else
      digits.length - 1
  }

  def digit(tokensRemoved: Int): Digit2 = {
    if (tokensRemoved < 0) {
      if (-tokensRemoved - 1 < additiveDigits.length) {
        additiveDigits(-tokensRemoved - 1)
      } else {
        Digit2.zero
      }
    } else {
      if (tokensRemoved < digits.length) {
        digits(tokensRemoved)
      } else if (period > 0) {
        val index = (tokensRemoved - preperiod) % period + preperiod
        digits(index)
      } else {
        Digit2.zero
      }
    }
  }

  def traversal(heapSize: Int): Traversal = new TBTraversal(heapSize)

  def isOctal = digits forall { _.isOctalDigit }

  def isGeneralizedOctal = digits forall { _.isGeneralizedOctalDigit }

  def isHexadecimal = digits forall { _.isHexadecimalDigit }

  def isGeneralizedHexadecimal = digits forall { _.isGeneralizedHexadecimalDigit }

  def periodicityChecker: PeriodicityChecker = {
    val checker = new PeriodicityChecker
    checker.setLinearCriteria(maxHeaps, maxHeaps, digits.length - 1 + maxHeaps)
    checker.setMaxSaltus(0)
    checker
  }

  override def toString = {
    val prefix = (additiveDigits.reverse :+ digits.head) mkString ""
    val suffix = digits.tail take (preperiod - 1) mkString ""
    val periodicPart = digits takeRight period mkString ""
    if (suffix.isEmpty && periodicPart.isEmpty)
      s"$prefix.0"
    else if (periodicPart.isEmpty)
      s"$prefix.$suffix"
    else
      s"$prefix.$suffix[$periodicPart]"
  }

  private class TBTraversal(heapSize: Int) extends Traversal {

    val heapBuffer = new Array[Int](heapSize min maxHeaps)
    val maxTokensRemovedInTraversal = maxTokensRemoved min heapSize

    var initialized = false
    var curTokensRemoved = -additiveDigits.length
    var curTokensLeft = heapSize - curTokensRemoved
    var curDigit = digit(curTokensRemoved)
    var curHeapCount = -1
    var curConstraint = DigitConstraint.Disallowed

    override def advance(): Boolean = {

      if (curTokensRemoved > maxTokensRemovedInTraversal)
        return false

      var done = false

      if (initialized && curHeapCount >= 2 && curConstraint != DigitConstraint.AllEqual) {

        // Find the rightmost incrementable heap, increment it, and redistribute.

        var heapIndex = curHeapCount - 2
        var tokensToDistribute = heapBuffer(curHeapCount - 1)
        while (!done && heapIndex >= 0) {
          tokensToDistribute += heapBuffer(heapIndex)
          val next = nextValidHeapSize(heapIndex, tokensToDistribute)
          if (next >= 0) {
            distributeTokens(heapIndex, next, tokensToDistribute)
            done = true
          }
          heapIndex -= 1
        }

      }

      while (!done && curTokensRemoved <= maxTokensRemovedInTraversal) {

        // Either we've not yet initialized, or there are no more distributions
        // at the current heap count. Set up the next heap count.

        done = setupNextHeapCount()
        if (!done) {
          curTokensRemoved += 1
          curTokensLeft = heapSize - curTokensRemoved
          curDigit = digit(curTokensRemoved)
          curHeapCount = -1
        }

      }

      initialized = true

      curTokensRemoved <= maxTokensRemovedInTraversal

    }

    private def setupNextHeapCount(): Boolean = {

      var heapCountOk = false
      val maxHeaps = curTokensLeft min curDigit.maxHeaps

      while (!heapCountOk && curHeapCount < maxHeaps) {

        curHeapCount += 1
        curConstraint = curDigit.constraint(curHeapCount)

        if (curConstraint != DigitConstraint.Disallowed) {

          if (curHeapCount == 0) {

            heapCountOk = curTokensLeft == 0

          } else {

            heapBuffer(0) = 0
            val initHeapSize = nextValidHeapSize(0, curTokensLeft)
            if (initHeapSize >= 0) {
              heapCountOk = true
              distributeTokens(0, initHeapSize, curTokensLeft)
            }

          }

        }

      }

      heapCountOk

    }

    private def nextValidHeapSize(heapIndex: Int, tokensToDistribute: Int): Int = {

      curConstraint match {

        case DigitConstraint.Unconstrained =>
          val nextHeapSize = heapBuffer(heapIndex) + 1
          if (nextHeapSize * (curHeapCount - heapIndex) <= tokensToDistribute)
            nextHeapSize
          else
            -1

        case DigitConstraint.SomeUnequal =>
          val nextHeapSize = heapBuffer(heapIndex) + 1
          val minTokensDistributed = nextHeapSize * (curHeapCount - heapIndex)
          if (minTokensDistributed <= tokensToDistribute && (heapIndex > 0 || minTokensDistributed < tokensToDistribute))
            nextHeapSize
          else
            -1

        case DigitConstraint.PairwiseUnequal =>
          val nextHeapSize = heapBuffer(heapIndex) + 1
          val incrementThrough = nextHeapSize + (curHeapCount - heapIndex)
          if (incrementThrough * (incrementThrough - 1) / 2 - nextHeapSize * (nextHeapSize - 1) / 2 <= tokensToDistribute)
            nextHeapSize
          else
            -1

        case DigitConstraint.AllEqual =>
          val onlyHeapSize = tokensToDistribute / curHeapCount
          if (heapIndex == 0 && tokensToDistribute % curHeapCount == 0 && heapBuffer(heapIndex) < onlyHeapSize)
            onlyHeapSize
          else
            -1

        case DigitConstraint.Disallowed => -1

      }

    }

    private def distributeTokens(heapIndex: Int, heapSize: Int, tokensToDistribute: Int): Unit = {

      var i = heapIndex
      var tokensDistributed = 0
      while (i < curHeapCount - 1) {
        val tokens = {
          if (curConstraint == DigitConstraint.PairwiseUnequal)
            heapSize + (i - heapIndex)
          else
            heapSize
        }
        heapBuffer(i) = tokens
        tokensDistributed += tokens
        i += 1
      }
      heapBuffer(curHeapCount - 1) = tokensToDistribute - tokensDistributed
      assert(curHeapCount <= 1 || heapBuffer(curHeapCount - 1) >= heapBuffer(curHeapCount - 2))

    }

    override def currentLength: Int = curHeapCount

    override def currentPart(i: Int): Int = heapBuffer(i)

  }

}

object Digit2 {

  val zero = Digit2(Vector.empty)

  def apply(splitmask: Long, constraint: DigitConstraint.Value): Digit2 = {

    val last = 63 - java.lang.Long.numberOfLeadingZeros(splitmask)
    val constraints = {
      (0 to last).toVector map { i =>
        if ((splitmask & (1L << i)) == 0)
          DigitConstraint.Disallowed
        else
          constraint
      }
    }
    Digit2(constraints)

  }

  def apply(constraints: Vector[DigitConstraint.Value], allowMore: Boolean = false): Digit2 = {

    if (allowMore) assert(constraints.nonEmpty && constraints.last != DigitConstraint.Disallowed)

    val cleanedConstraints = {
      val lastAllowed = constraints lastIndexWhere { _ != DigitConstraint.Disallowed }
      constraints take (lastAllowed + 1)
    }

    val paddedConstraints = {
      // Pad to at least 4 if allowMore
      if (allowMore && cleanedConstraints.length < 4)
        cleanedConstraints ++ Vector.fill(4 - cleanedConstraints.length)(cleanedConstraints.last)
      else
        cleanedConstraints
    }

    val effectiveConstraints = (paddedConstraints drop 2 filterNot { _ == DigitConstraint.Disallowed }).toSet

    val normalizedConstraints = {
      paddedConstraints.zipWithIndex map { case (c, index) =>
        if (c == DigitConstraint.Disallowed)
          c
        else if (effectiveConstraints.size == 1)
          // Exactly one constraint type for >= 2 heaps, so set constraint type to match.
          effectiveConstraints.head
        else if (index == 0 || index == 1)
          // Multiple constraint types for >= 2 heaps OR only 0 or 1 heaps allowed (in which case effectiveConstraints is empty).
          // In either case, remove any constraints IF this is a 0 or 1 heaps constraint.
          DigitConstraint.Unconstrained
        else
          c
      }
    }

    val trimmedConstraints = {
      if (allowMore) {
        val lastMatching = normalizedConstraints lastIndexWhere { _ != normalizedConstraints.last }
        normalizedConstraints take (lastMatching + 2)
      } else {
        normalizedConstraints
      }
    }

    new Digit2(trimmedConstraints, allowMore)

  }

}

case class Digit2(constraints: Vector[DigitConstraint.Value], allowMore: Boolean = false) {

  assert(constraints.isEmpty || constraints.last != DigitConstraint.Disallowed)

  val maxHeaps = {
    if (allowMore)
      Int.MaxValue
    else
      constraints.length
  }

  def constraint(heapCount: Int): DigitConstraint.Value = {
    if (heapCount < constraints.length) {
      constraints(heapCount)
    } else if (allowMore) {
      constraints.last
    } else {
      DigitConstraint.Disallowed
    }
  }

  def isZero = constraints.isEmpty

  def isOctalDigit = isGeneralizedOctalDigit && isFullyUnconstrained

  def isGeneralizedOctalDigit = constraints.length <= 2 && !allowMore

  def isHexadecimalDigit = isGeneralizedHexadecimalDigit && isFullyUnconstrained

  def isGeneralizedHexadecimalDigit = constraints.length <= 3 && !allowMore

  def isFullyUnconstrained = constraints forall { c => c == DigitConstraint.Unconstrained || c == DigitConstraint.Disallowed }

  override def toString = {

    if (constraints.isEmpty)
      "0"
    else {

      val lastConstraint = constraints.last
      val consistentConstraints = constraints forall { c => c == lastConstraint || c == DigitConstraint.Disallowed }

      (consistentConstraints, allowMore) match {

        case (true, false) =>
          val base = constraints.zipWithIndex.map { case (c, index) => if (c == DigitConstraint.Disallowed) 0L else 1L << index }.sum
          val baseStr = if (base < 32) TBCode2.base32Chars.charAt(base.toInt).toString else s"&$base;"
          baseStr + toConstraintString(lastConstraint)

        case (true, true) =>
          val baseStr = constraints.size match {
            case 1 => "*"
            case _ =>
              val allowedList = constraints.zipWithIndex filter { case (c, _) => c == lastConstraint } map { case (_, index) => index }
              s"{${allowedList mkString ","}+}"
          }
          baseStr + toConstraintString(lastConstraint)

        case (false, _) =>
          val allowedList = constraints.zipWithIndex filter { case (c, _) => c != DigitConstraint.Disallowed }
          val allowedStrs = allowedList map { case (c, index) => s"$index${toConstraintString(c)}" }
          val moreStr = if (allowMore) "+" else ""
          s"{${allowedStrs mkString ","}$moreStr}"

      }

    }

  }

  def toConstraintString(constraint: DigitConstraint.Value) = {
    constraint match {
      case DigitConstraint.Unconstrained => ""
      case DigitConstraint.AllEqual => "="
      case DigitConstraint.SomeUnequal => "?"
      case DigitConstraint.PairwiseUnequal => "!"
    }
  }

}

case object DigitConstraint extends Enumeration {
  val Unconstrained, SomeUnequal, PairwiseUnequal, AllEqual, Disallowed = Value
}

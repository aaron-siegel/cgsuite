package org.cgsuite.core.impartial

import org.cgsuite.core._
import org.cgsuite.core.impartial.Spawning.Constraint
import org.cgsuite.exception.MalformedCodeException

import scala.collection.mutable

object Spawning {

  object Constraint extends Enumeration {
    val Consecutive, EquallySpaced, Symmetrical, None = Value
  }

  def apply(code: String): Spawning = {

    val paren = code.indexOf('(')
    val preparen = if (paren == -1) code else code.substring(0, paren)
    val (allowMore, permittedStr) = {
      if (preparen.nonEmpty && preparen.last == '+')
        (true, preparen dropRight 1)
      else
        (false, preparen)
    }
    val permitted = try {
      permittedStr split "," map { _.toInt }
    } catch {
      case exc: NumberFormatException => throw MalformedCodeException(code)
    }
    var maxSeparation = Int.MaxValue
    var requireFirst = false
    var spacingConstraint = Constraint.None
    if (paren >= 0) {
      if (!(code endsWith ")"))
        throw MalformedCodeException(code)
      val modifiers = code.substring(paren + 1, code.length - 1)
      modifiers.toUpperCase foreach {
        case 'C' => spacingConstraint = Constraint.Consecutive
        case 'S' => spacingConstraint = Constraint.Symmetrical
        case 'E' => spacingConstraint = Constraint.EquallySpaced
        case 'F' => requireFirst = true
        case n if n.isDigit => maxSeparation = n.toString.toInt
        case _ => throw MalformedCodeException(code)
      }
    }
    Spawning(permitted, allowMore, maxSeparation, requireFirst, spacingConstraint)

  }

}

case class Spawning(
  permitted: IndexedSeq[Int],
  allowMore: Boolean = false,
  maxSeparation: Int = Int.MaxValue,
  requireFirst: Boolean = false,
  spacingConstraint: Constraint.Value = Constraint.None) extends HeapRuleset {

  val maxHeapCount = {
    maxSeparation min {
      if (allowMore)
        Int.MaxValue
      else
        permitted.max - 1
    }
  }

  override def heapOptions(heapSize: Integer): IndexedSeq[IndexedSeq[Integer]] = {
    val tr = traversal(heapSize.intValue)
    val result = mutable.ArrayBuffer[IndexedSeq[Integer]]()
    while (tr.advance()) {
      result += (0 until tr.currentLength) map { n => Integer(tr.currentPart(n)) }
    }
    result
  }

  override def traversal(heapSize: Int): Traversal = new SpawningTraversal(heapSize)

  private class SpawningTraversal(heapSize: Int) extends Traversal {

    val maxHeapCountInTraversal = maxHeapCount min heapSize

    var curHeapCountIndex = -1
    var curHeapCount = -1
    var initialized = false
    val heapBuffer = new Array[Int](maxHeapCountInTraversal)

    override def advance(): Boolean = {

      if (curHeapCount > maxHeapCountInTraversal)
        return false

      var done = false

      if (initialized) {

        // Increment the rightmost incrementable heap, and redistribute the rest.
        val minimumIncrementableHeapIndex = if (requireFirst) 1 else 0
        val maximumIncrementableHeapIndex = spacingConstraint match {
          case Constraint.None => curHeapCount - 1
          case Constraint.EquallySpaced => 0
          case Constraint.Consecutive => -1
          case Constraint.Symmetrical => (curHeapCount + 1) / 2 - 1
        }

        var heapIndex = maximumIncrementableHeapIndex
        while (!done && heapIndex >= minimumIncrementableHeapIndex) {
          done = tryIncrementHeap(heapIndex)
          heapIndex -= 1
        }

      }

      while (!done && curHeapCount <= maxHeapCountInTraversal) {

        if (curHeapCountIndex < permitted.length - 1) {
          curHeapCountIndex += 1
          curHeapCount = permitted(curHeapCountIndex) - 1
        } else {
          curHeapCount += 1
        }

        if (curHeapCount <= maxHeapCountInTraversal) {
          done = setupHeapCount()
        }

      }

      initialized = true

      curHeapCount <= maxHeapCountInTraversal

    }

    private def setupHeapCount(): Boolean = {

      if (curHeapCount >= heapSize)
        return false

      assert(curHeapCount < maxSeparation)

      var success = false

      if (spacingConstraint == Constraint.Symmetrical) {

        val earliest = 1 max (heapSize - maxSeparation + 1)
        // If heapCount is even, then initial = earliest.
        // If heapCount is odd, then we need odd separation between first and last.
        val initial = {
          if (curHeapCount % 2 == 0 && (earliest + heapSize) % 2 == 1)
            earliest + 1
          else
            earliest
        }

        if (initial == 1 || !requireFirst) {
          var i = 0
          while (i < (curHeapCount + 1) / 2) {
            heapBuffer(i) = initial + i
            if (i > 0)
              heapBuffer(curHeapCount - i) = heapSize - i
            i += 1
          }
          if (curHeapCount % 2 == 0) {
            assert((heapSize + initial) % 2 == 0)
            heapBuffer(curHeapCount / 2) = (heapSize + initial) / 2
          }
          success = true
        }

      } else {

        val (initial: Int, increment: Int) = spacingConstraint match {

          case Constraint.None =>
            (1 max (heapSize - maxSeparation + 1), 1)

          case Constraint.EquallySpaced =>
            val earliest = 1 max (heapSize - maxSeparation + 1)
            val spacing = (heapSize - earliest) / curHeapCount
            (heapSize - spacing * curHeapCount, spacing)

          case Constraint.Consecutive =>
            (heapSize - curHeapCount, 1)

        }

        if (initial == 1 || !requireFirst) {
          var i = 0
          while (i < curHeapCount) {
            heapBuffer(i) = initial + increment * i
            i += 1
          }
          success = true
        }

      }

      success

    }

    private def tryIncrementHeap(heapIndex: Int): Boolean = {

      var success = false

      spacingConstraint match {

        case Constraint.None =>
          if (heapBuffer(heapIndex) < heapSize - (curHeapCount - heapIndex)) {
            val value = heapBuffer(heapIndex) + 1
            var i = heapIndex
            while (i < curHeapCount) {
              heapBuffer(i) = value + (i - heapIndex)
              i += 1
            }
            success = true
          }

        case Constraint.EquallySpaced =>
          assert(heapIndex == 0)
          val spacing = (heapSize - heapBuffer(0)) / curHeapCount
          if (spacing > 1) {
            val newIncrement = spacing - 1
            val initial = heapSize - newIncrement * curHeapCount
            var i = 0
            while (i < curHeapCount) {
              heapBuffer(i) = initial + i * newIncrement
              i += 1
            }
            success = true
          }

        case Constraint.Consecutive =>
          // Never possible

        case Constraint.Symmetrical =>
          assert(heapIndex < (curHeapCount + 1) / 2)
          if (heapIndex == 0) {
            if (heapBuffer(0) < heapSize - curHeapCount) {
              val initial = heapBuffer(0) + (if (curHeapCount % 2 == 0) 2 else 1)
              var i = 0
              while (i < (curHeapCount + 1) / 2) {
                heapBuffer(i) = initial + i
                if (i > 0)
                  heapBuffer(curHeapCount - i) = heapSize - i
                i += 1
              }
              if (curHeapCount % 2 == 0) {
                assert((heapSize + initial) % 2 == 0)
                heapBuffer(curHeapCount / 2) = (heapSize + initial) / 2
              }
              success = true
            }
          } else {
            val center = (heapBuffer(0) + heapSize + 1) / 2
            val remainingTokenCountBelowCenter = (curHeapCount + 1) / 2 - heapIndex
            val maxAllowableHeap = center - remainingTokenCountBelowCenter
            if (heapBuffer(heapIndex) < maxAllowableHeap) {
              val newValue = heapBuffer(heapIndex) + 1
              var i = heapIndex
              while (i < (curHeapCount + 1) / 2) {
                heapBuffer(i) = newValue + (i - heapIndex)
                heapBuffer(curHeapCount - i) = heapSize - (heapBuffer(i) - heapBuffer(0))
                i += 1
              }
              success = true
            }
          }

      }

      success

    }

    override def currentLength = curHeapCount

    override def currentPart(i: Int) = heapBuffer(i)

  }

}

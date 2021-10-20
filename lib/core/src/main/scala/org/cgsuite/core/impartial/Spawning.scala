package org.cgsuite.core.impartial

import org.cgsuite.core._
import org.cgsuite.core.impartial.Spawning.SpacingConstraint
import org.cgsuite.exception.MalformedCodeException

import scala.collection.mutable

object Spawning {

  object SpacingConstraint extends Enumeration {
    val Consecutive, EquallySpaced, Symmetrical, None = Value
  }

  case class Element(
    minHeapsAffected: Int,
    maxHeapsAffected: Int,
    maxSeparation: Int,
    spacingConstraint: SpacingConstraint.Value,
    requireFirst: Boolean
  )

  def apply(code: String): Spawning = {

    val elements = try {

      code.toUpperCase split ',' flatMap { elementStr =>

        val modifiersStart = elementStr indexWhere { ch =>
          !(ch.isDigit || ch == '+' || ch == '-')
        } match {
          case -1 => elementStr.length
          case i => i
        }
        val rangeStr = elementStr.substring(0, modifiersStart)
        val (minHeapsAffected, maxHeapsAffected) = {
          if (rangeStr.last == '+') {
            (rangeStr.dropRight(1).toInt, Int.MaxValue)
          } else if (rangeStr contains '-') {
            val range = rangeStr split '-'
            (range(0).toInt, range(1).toInt)
          } else {
            (rangeStr.toInt, rangeStr.toInt)
          }
        }

        var maxSeparation = Int.MaxValue
        var requireFirst = false
        var spacingConstraint = SpacingConstraint.None
        var i = modifiersStart

        while (i < elementStr.length) {
          elementStr(i) match {
            case 'C' =>
              if (i + 1 < elementStr.length && elementStr(i + 1) == '(') {
                val closeParen = elementStr.indexOf(')', i + 1)
                if (closeParen == -1)
                  throw MalformedCodeException(code)
                maxSeparation = elementStr.substring(i + 2, closeParen).toInt
                i = closeParen + 1
              } else {
                spacingConstraint = SpacingConstraint.Consecutive;
                i += 1
              }
            case 'S' => spacingConstraint = SpacingConstraint.Symmetrical; i += 1
            case 'E' => spacingConstraint = SpacingConstraint.EquallySpaced; i += 1
            case 'F' => requireFirst = true; i += 1
            case _ => throw MalformedCodeException(code)
          }
        }

        if (maxSeparation < minHeapsAffected)
          None
        else
          Some(Element(minHeapsAffected, maxHeapsAffected min maxSeparation, maxSeparation, spacingConstraint, requireFirst))

      }

    } catch {
      case exc: Exception => throw MalformedCodeException(code, exc)
    }

    Spawning(code, elements.toVector)

  }

}

case class Spawning(
  code: String,
  elements: Vector[Spawning.Element]
) extends HeapRuleset {

  val maxHeapCount: Int = elements.map { _.maxHeapsAffected }.max

  override def heapOptions(heapSize: Integer): IndexedSeq[IndexedSeq[Integer]] = {
    val tr = traversal(heapSize.intValue)
    val result = mutable.ArrayBuffer[IndexedSeq[Integer]]()
    while (tr.advance()) {
      result += (0 until tr.currentLength) map { n => Integer(tr.currentPart(n)) }
    }
    result.toIndexedSeq
  }

  override def traversal(heapSize: Int): Traversal = new SpawningTraversal(heapSize)

  class SpawningTraversal(heapSize: Int) extends Traversal {

    var curRulesetElementIndex = -1
    var curRulesetElement: Spawning.Element = null
    var curHeapCount = -1
    var initialized = false
    val heapBuffer = new Array[Int](maxHeapCount min heapSize)

    override def advance(): Boolean = {

      if (curRulesetElementIndex >= elements.length)
        return false

      var done = false

      if (initialized) {

        // Increment the rightmost incrementable heap, and redistribute the rest.
        val minimumIncrementableHeapIndex = if (curRulesetElement.requireFirst) 1 else 0
        val maximumIncrementableHeapIndex = curRulesetElement.spacingConstraint match {
          case SpacingConstraint.None => curHeapCount - 1
          case SpacingConstraint.EquallySpaced => 0
          case SpacingConstraint.Consecutive => -1
          case SpacingConstraint.Symmetrical => (curHeapCount + 1) / 2 - 1
        }

        var heapIndex = maximumIncrementableHeapIndex
        while (!done && heapIndex >= minimumIncrementableHeapIndex) {
          done = tryIncrementHeap(heapIndex)
          heapIndex -= 1
        }

        while (!done && curHeapCount < curRulesetElement.maxHeapsAffected.min(heapSize)) {
          curHeapCount += 1
          if (curHeapCount < curRulesetElement.maxHeapsAffected) {
            done = setupHeapCount()
          }
        }

      }

      while (!done && curRulesetElementIndex < elements.length) {
        curRulesetElementIndex += 1
        if (curRulesetElementIndex < elements.length) {
          curRulesetElement = elements(curRulesetElementIndex)
          curHeapCount = curRulesetElement.minHeapsAffected - 1
          done = setupHeapCount()
        }
      }

      initialized = true

      curRulesetElementIndex < elements.length

    }

    private def setupHeapCount(): Boolean = {

      if (curHeapCount >= heapSize)
        return false

      assert(curHeapCount < curRulesetElement.maxSeparation)

      var success = false

      if (curRulesetElement.spacingConstraint == SpacingConstraint.Symmetrical) {

        val earliest = 1 max (heapSize - curRulesetElement.maxSeparation + 1)
        // If heapCount is even, then initial = earliest.
        // If heapCount is odd, then we need odd separation between first and last.
        val initial = {
          if (curHeapCount % 2 == 0 && (earliest + heapSize) % 2 == 1)
            earliest + 1
          else
            earliest
        }

        if (initial == 1 || !curRulesetElement.requireFirst) {
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

        val (initial: Int, increment: Int) = curRulesetElement.spacingConstraint match {

          case SpacingConstraint.None =>
            (1 max (heapSize - curRulesetElement.maxSeparation + 1), 1)

          case SpacingConstraint.EquallySpaced =>
            val earliest = 1 max (heapSize - curRulesetElement.maxSeparation + 1)
            val spacing = (heapSize - earliest) / curHeapCount
            (heapSize - spacing * curHeapCount, spacing)

          case SpacingConstraint.Consecutive =>
            (heapSize - curHeapCount, 1)

        }

        if (initial == 1 || !curRulesetElement.requireFirst) {
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

      curRulesetElement.spacingConstraint match {

        case SpacingConstraint.None =>
          if (heapBuffer(heapIndex) < heapSize - (curHeapCount - heapIndex)) {
            val value = heapBuffer(heapIndex) + 1
            var i = heapIndex
            while (i < curHeapCount) {
              heapBuffer(i) = value + (i - heapIndex)
              i += 1
            }
            success = true
          }

        case SpacingConstraint.EquallySpaced =>
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

        case SpacingConstraint.Consecutive =>
          // Never possible

        case SpacingConstraint.Symmetrical =>
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

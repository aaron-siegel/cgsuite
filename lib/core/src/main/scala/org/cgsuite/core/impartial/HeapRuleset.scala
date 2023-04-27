package org.cgsuite.core.impartial

import org.cgsuite.core.{Integer, SmallInteger}

import scala.collection.immutable.ArraySeq

trait HeapRuleset {

  val sequence = new NimValueSequence(this)

  def heapNimValue(heapSize: Integer): Integer = SmallInteger(sequence nimValue heapSize.intValue)

  def nimValueSequence(toHeapSize: Integer): IndexedSeq[Integer] = {
    ArraySeq.unsafeWrapArray(sequence nimValues toHeapSize.intValue) map { SmallInteger(_) }
  }

  def heapOptions(heapSize: Integer): Iterable[IndexedSeq[Integer]]

  def traversal(heapSize: Int): Traversal = new Traversal {

    val it = heapOptions(SmallInteger(heapSize)).iterator
    var current: Seq[Integer] = null

    override def advance(): Boolean = {
      if (it.hasNext) {
        current = it.next()
        true
      } else {
        false
      }
    }

    override def currentLength: Int = current.length

    override def currentPart(i: Int): Int = current(i).intValue

  }

  def periodicityChecker: Option[PeriodicityChecker] = None

  def checkPeriodicity(toHeapSize: Integer): Periodicity = sequence checkPeriodicity toHeapSize.intValue

}

case class Periodicity(period: Int, preperiod: Int, saltus: Int)

trait Traversal {

  def advance(): Boolean
  def currentLength: Int
  def currentPart(i: Int): Int

}

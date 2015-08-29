package org.cgsuite.core.impartial

import org.cgsuite.core.{SmallInteger, Integer}

trait HeapRuleset {

  val sequence = new NimValueSequence(this)

  def heapNimValue(heapSize: Integer): Integer = SmallInteger(sequence nimValue heapSize.intValue)

  def nimValueSequence(toHeapSize: Integer): Iterable[Integer] = {
    sequence nimValues toHeapSize.intValue map { SmallInteger(_) }
  }

  def heapOptions(heapSize: Integer): Iterable[Iterable[Integer]]

  def traversal(heapSize: Int): Traversal = new Traversal {

    val it = heapOptions(SmallInteger(heapSize)).iterator
    var current: Seq[Integer] = null

    override def advance(): Boolean = {
      if (it.hasNext) {
        current = it.next().toSeq
        true
      } else {
        false
      }
    }

    override def currentLength: Int = current.length

    override def currentPart(i: Int): Int = current(i).intValue

  }

  def apChecker: Option[APChecker] = None

  def checkPeriodicity(toHeapSize: Integer): APInfo = sequence checkPeriodicity toHeapSize.intValue

}

trait Traversal {

  def advance(): Boolean
  def currentLength: Int
  def currentPart(i: Int): Int

}

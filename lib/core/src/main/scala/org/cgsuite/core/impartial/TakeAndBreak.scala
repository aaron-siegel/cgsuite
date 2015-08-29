package org.cgsuite.core.impartial

import org.cgsuite.core.Integer

import scala.collection.mutable

case class TakeAndBreak(code: String) extends HeapRuleset {

  val tbCode = new TBCode(code)

  override def traversal(heapSize: Int): Traversal = tbCode traversal heapSize

  override def heapOptions(heapSize: Integer): Iterable[Iterable[Integer]] = {
    val tr = traversal(heapSize.intValue)
    val result = mutable.MutableList[Iterable[Integer]]()
    while (tr.advance()) {
      result += (0 until tr.currentLength) map { n => Integer(tr.currentPart(n)) }
    }
    result
  }

  override def periodicityChecker: Option[PeriodicityChecker] = Some(tbCode.getAPChecker)

}

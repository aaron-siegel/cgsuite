package org.cgsuite.core.impartial

import org.cgsuite.core.Integer

import scala.collection.mutable

case class TakeAndBreak(code: String) extends HeapRuleset {

  val tbCode = new TBCode(code)

  override def heapOptions(heapSize: Integer): Iterable[Iterable[Integer]] = {
    val traversal = tbCode traversal heapSize.intValue
    val result = mutable.MutableList[Iterable[Integer]]()
    while (traversal.advance()) {
      result += (0 until traversal.currentLength) map { n => Integer(traversal.currentPart(n)) }
    }
    result
  }

}

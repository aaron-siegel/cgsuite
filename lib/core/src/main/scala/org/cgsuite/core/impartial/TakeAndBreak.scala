package org.cgsuite.core.impartial

import org.cgsuite.core.Integer
import org.cgsuite.exception.InvalidArgumentException

import scala.collection.mutable

object TakeAndBreak {

  def fromSubtractionSet(subtset: Iterable[_], codeDigit: String, allbut: java.lang.Boolean): TakeAndBreak = {
    val mapped = subtset map {
      case n: Int => n
      case n: Integer => n.intValue
      case _ => throw InvalidArgumentException("Subtraction set must be a set of `Integer`s.")
    }
    val set = mapped.toSet
    val prefix = {
      val min = set.min
      if (min < 1) {
        if (allbut)
          throw InvalidArgumentException("If `allbut` == true, then subtraction set must contain strictly positive `Integer`s.")
        val digits = (min to 0) map { n => if (set contains n) codeDigit else "0" }
        digits mkString ""
      } else {
        "0"
      }
    }
    val suffix = {
      val max = set.max
      if (max >= 1) {
        val digits = (1 to max) map { n => if ((set contains n) != allbut) codeDigit else "0" }
        digits mkString ""
      } else {
        if (allbut) "" else "0"
      }
    }
    val allbutSuffix = if (allbut) s"[$codeDigit]" else ""
    TakeAndBreak(s"$prefix.$suffix$allbutSuffix")
  }

}

case class TakeAndBreak(code: String) extends HeapRuleset {

  val tbCode = TBCode2(code)

  override def traversal(heapSize: Int): Traversal = tbCode traversal heapSize

  override def heapOptions(heapSize: Integer): Iterable[Iterable[Integer]] = {
    val tr = traversal(heapSize.intValue)
    val result = mutable.MutableList[Iterable[Integer]]()
    while (tr.advance()) {
      result += (0 until tr.currentLength) map { n => Integer(tr.currentPart(n)) }
    }
    result
  }

  override def periodicityChecker: Option[PeriodicityChecker] = Some(tbCode.periodicityChecker)

}

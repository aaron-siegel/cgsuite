package org.cgsuite.util

import java.util

import org.cgsuite.core.{Integer, SmallInteger}
import org.cgsuite.exception.GridParseException

import scala.collection.mutable.ArrayBuffer

object Strip {

  def apply(length: Int) = new Strip(new Array[Byte](length))

  def parse(str: String, charMap: String): Strip = {
    val bytes = str map { ch =>
      charMap.indexOf(ch.toLower) match {
        case -1 => throw GridParseException("The position may only contain the following characters: " + charMap)
        case n => n.toByte
      }
    }
    new Strip(bytes.toArray)
  }

}

class Strip private[util] (private val values: Array[Byte]) {

  def length = values.length

  def toGrid = new Grid(1, length, values)

  def apply(pos: Integer) = get(pos)

  def get(pos: Integer): SmallInteger = {
    val n = pos.intValue
    if (n >= 1 && n <= values.length)
      SmallInteger(values(n-1))
    else
      null
  }

  def updated(positions: scala.collection.Map[Integer, Integer]): Strip = {
    val newValues = new Array[Byte](values.length)
    System.arraycopy(values, 0, newValues, 0, values.length)
    val it = positions.iterator
    while (it.hasNext) {
      val (pos, newValue) = it.next()
      newValues(pos.intValue-1) = newValue.intValue.toByte
    }
    new Strip(newValues)
  }

  def findAll(value: Integer): IndexedSeq[Integer] = {
    val byte = value.intValue.toByte
    var cnt = 0
    var i = 0
    while (i < values.length) {
      if (values(i) == byte)
        cnt += 1
      i += 1
    }
    val result = new ArrayBuffer[Integer](cnt)
    i = 0
    while (i < values.length) {
      if (values(i) == byte)
        result += SmallInteger(i+1)
      i += 1
    }
    result
  }

  def substrip(first: Integer, last: Integer): Strip = {
    val iFirst = first.intValue
    val iLast = last.intValue
    val substrip = Strip(iLast-iFirst+1)
    System.arraycopy(values, iFirst-1, substrip, 0, iLast-iFirst+1)
    substrip
  }

  override def hashCode(): Int = util.Arrays.hashCode(values)

  override def equals(that: Any): Boolean = that match {
    case other: Strip => util.Arrays.equals(values, other.values)
    case _ => false
  }

  def toString(charMap: String): String = {
    SeqCharSequence(values map { charMap(_) }).toString()
  }

}

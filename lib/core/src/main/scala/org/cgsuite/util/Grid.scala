package org.cgsuite.util

import org.cgsuite.core._
import org.cgsuite.exception.InputException

import scala.collection.mutable.ArrayBuffer

object Grid {

  def apply(rowCount: Int, colCount: Int) = {
    new Grid(rowCount, colCount, new Array[Byte](rowCount * colCount))
  }

  def parse(str: String, charMap: String): Grid = {
    // Validate
    val strings = str.split('|').toIndexedSeq
    val rowCount = strings.length
    val colCount = if (rowCount == 0) 0 else strings(0).length
    if (strings exists { _.length != colCount }) {
      throw InputException("All rows of the position must have equal length.")
    }

    val compressed = str filterNot { _ == '|' }
    val bytes = compressed map { ch =>
      charMap.indexOf(ch.toLower) match {
        case -1 => throw InputException("The position may only contain the following characters: " + charMap)
        case n => n.toByte
      }
    }

    new Grid(rowCount, colCount, bytes.toArray)
  }

}

class Grid private (val rowCount: Int, val colCount: Int, val values: Array[Byte]) {

  def get(row: Int, col: Int): Byte = values((row-1)*colCount+(col-1))

  def get(coord: Coordinates): Any = {
    if (isInBounds(coord))
      SmallInteger(get(coord.row, coord.col))
    else
      Nil
  }

  def updated(row: Int, col: Int, newValue: Byte): Grid = {
    val newValues = new Array[Byte](values.length)
    System.arraycopy(values, 0, newValues, 0, values.length)
    newValues((row-1)*colCount+(col-1)) = newValue
    new Grid(rowCount, colCount, newValues)
  }

  def updated(coord: Coordinates, newValue: Integer): Grid = {
    if (!isInBounds(coord))
      sys.error("out of bounds")
    updated(coord.row, coord.col, newValue.intValue.toByte)
  }

  def updated(coords: Map[Coordinates, Integer]): Grid = {
    val newValues = new Array[Byte](values.length)
    System.arraycopy(values, 0, newValues, 0, values.length)
    val it = coords.iterator
    while (it.hasNext) {
      val (coord, newValue) = it.next()
      newValues((coord.row-1)*colCount+(coord.col-1)) = newValue.intValue.toByte
    }
    new Grid(rowCount, colCount, newValues)
  }

  def isInBounds(coord: Coordinates) = {
    val rowInt = coord.row
    val colInt = coord.col
    rowInt >= 1 && rowInt <= rowCount && colInt >= 1 && colInt <= colCount
  }

  def findAll(value: Integer): Seq[Coordinates] = {
    val byte = value.intValue.toByte
    var cnt = 0
    var i = 0
    while (i < values.length) {
      if (values(i) == byte)
        cnt += 1
      i += 1
    }
    val result = new ArrayBuffer[Coordinates](cnt)
    i = 0
    while (i < values.length) {
      if (values(i) == byte)
        result += Coordinates(i / colCount + 1, i % colCount + 1)
      i += 1
    }
    result
  }

  override def hashCode(): Int = java.util.Arrays.hashCode(values)

  override def equals(that: Any): Boolean = {
    if (!that.isInstanceOf[Grid])
      false
    else {
      val other = that.asInstanceOf[Grid]
      rowCount == other.rowCount && colCount == other.colCount && java.util.Arrays.equals(values, other.values)
    }
  }

  def toString(charMap: String) = {
    seqToCharSequence(values map { byte => charMap(byte) }).toString
  }

}

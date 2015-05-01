package org.cgsuite.util

import org.cgsuite.core._
import org.cgsuite.exception.InputException

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

case class Grid private (rowCount: Int, colCount: Int, values: IndexedSeq[Byte]) {

  def get(row: Int, col: Int): Byte = values((row-1)*colCount+(col-1))

  def get(coord: Coordinates): Any = {
    if (isInBounds(coord))
      SmallInteger(get(coord.row.intValue, coord.col.intValue))
    else
      Nil
  }

  def updated(row: Int, col: Int, newValue: Byte): Grid = {
    new Grid(rowCount, colCount, values.updated((row-1)*colCount+(col-1), newValue))
  }

  def updated(coord: Coordinates, newValue: Integer): Grid = {
    if (!isInBounds(coord))
      sys.error("out of bounds")
    updated(coord.row.intValue, coord.col.intValue, newValue.intValue.toByte)
  }

  def isInBounds(coord: Coordinates) = {
    val rowInt = coord.row.intValue
    val colInt = coord.col.intValue
    rowInt >= 1 && rowInt <= rowCount && colInt >= 1 && colInt <= colCount
  }

  def findAll(value: Integer): Seq[Coordinates] = {
    val byte = value.intValue.toByte
    values
      .zipWithIndex
      .filter { case (v, _) => v == byte }
      .map { case (_, index) =>
        Coordinates(index / colCount + 1, index % colCount + 1)
      }
  }

  def toString(charMap: String) = {
    seqToCharSequence(values map { byte => charMap(byte) }).toString
  }

}

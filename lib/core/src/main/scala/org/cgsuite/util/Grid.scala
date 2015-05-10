package org.cgsuite.util

import java.util

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

  private[util] var regionMarkers: Array[Int] = new Array[Int](0)
  private[util] var regionInfo: ArrayBuffer[RegionInfo] = new ArrayBuffer[RegionInfo]()

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

  def subgrid(northwest: Coordinates, southeast: Coordinates): Grid = {
    subgrid(northwest.row, southeast.row, northwest.col, southeast.col)
  }

  def subgrid(minRow: Int, maxRow: Int, minCol: Int, maxCol: Int): Grid = {
    val subgrid = Grid(maxRow-minRow+1, maxCol-minCol+1)
    var row = minRow
    while (row <= maxRow) {
      System.arraycopy(values, (row-1)*colCount+minCol-1, subgrid.values, (row-minRow)*subgrid.colCount, subgrid.colCount)
      row += 1
    }
    subgrid
  }

  def decomposition(boundaryValue: Integer, directions: Seq[Coordinates]): Seq[Grid] = {
    var orthogonal = true
    var diagonal = true
    val it = directions.iterator
    while (it.hasNext && diagonal) {
      val coord = it.next()
      if (!coord.isUnit) {
        diagonal = false
        orthogonal = false
      } else if (coord.col != 0 && coord.row != 0) {
        orthogonal = false
      }
    }
    if (diagonal) {
      decomposition(boundaryValue, !orthogonal)
    } else {
      Seq(this)
    }
  }

  def decomposition(boundaryValue: Integer, allowDiagonal: Boolean = false): Seq[Grid] = {
    val bv = boundaryValue.intValue.toByte
    if (Grid.regionMarkers.length < values.length)
      Grid.regionMarkers = new Array[Int](values.length)
    util.Arrays.fill(Grid.regionMarkers, -1)
    var i = 0
    var nextRegion = 0
    while (i < values.length) {
      if (Grid.regionMarkers(i) == -1 && values(i) != bv) {
        if (Grid.regionInfo.length <= nextRegion)
          Grid.regionInfo += new RegionInfo(nextRegion)
        val info = Grid.regionInfo(nextRegion)
        info.minRow = i / colCount + 1
        info.maxRow = i / colCount + 1
        info.minCol = i % colCount + 1
        info.maxCol = i % colCount + 1
        markRegion(bv, i, info, allowDiagonal)
        nextRegion += 1
      }
      i += 1
    }
    if (nextRegion == 0) {
      Seq.empty
    } else if (nextRegion == 1) {
      Seq(this)
    } else {
      (0 until nextRegion) map { n =>
        val info = Grid.regionInfo(n)
        val subgrid = this.subgrid(info.minRow, info.maxRow, info.minCol, info.maxCol)
        var j = 0
        while (j < subgrid.values.length) {
          if (Grid.regionMarkers(((j/subgrid.colCount)+info.minRow-1)*colCount + (j%subgrid.colCount)+info.minCol-1) != n)
            subgrid.values(j) = bv
          j += 1
        }
        subgrid
      }
    }
  }

  def markRegion(bv: Byte, i: Int, info: RegionInfo, allowDiagonal: Boolean): Unit = {
    if (Grid.regionMarkers(i) == -1 && values(i) != bv) {
      Grid.regionMarkers(i) = info.region
      info.minRow = info.minRow.min(i / colCount + 1)
      info.maxRow = info.maxRow.max(i / colCount + 1)
      info.minCol = info.minCol.min(i % colCount + 1)
      info.maxCol = info.maxCol.max(i % colCount + 1)
      if (i % colCount != 0) markRegion(bv, i - 1, info, allowDiagonal)
      if ((i + 1) % colCount != 0) markRegion(bv, i + 1, info, allowDiagonal)
      if (i - colCount >= 0) markRegion(bv, i - colCount, info, allowDiagonal)
      if (i + colCount < values.length) markRegion(bv, i + colCount, info, allowDiagonal)
      if (allowDiagonal) {
        if (i % colCount != 0) {
          if (i - colCount >= 0) markRegion(bv, i - colCount - 1, info, allowDiagonal)
          if (i + colCount < values.length) markRegion(bv, i + colCount - 1, info, allowDiagonal)
        }
        if ((i + 1) % colCount != 0) {
          if (i - colCount >= 0) markRegion(bv, i - colCount + 1, info, allowDiagonal)
          if (i + colCount < values.length) markRegion(bv, i + colCount + 1, info, allowDiagonal)
        }
      }
    }
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
    val grouped = values map { charMap(_) } grouped colCount map { SeqCharSequence(_) }
    grouped mkString "|"
  }

}

class RegionInfo(val region: Int) {
  var minRow: Int = 0
  var maxRow: Int = 0
  var minCol: Int = 0
  var maxCol: Int = 0
  override def toString = ((minRow,minCol),(maxRow,maxCol)).toString()
}

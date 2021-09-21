package org.cgsuite.util

import java.util

import org.cgsuite.core._
import org.cgsuite.exception.GridParseException

import scala.collection.mutable.ArrayBuffer

object Grid {

  def apply(rowCount: Int, colCount: Int) = {
    new Grid(rowCount, colCount, new Array[Byte](rowCount * colCount))
  }

  // TODO Better Int <-> Integer conversions?
  def empty(rowCount: Integer, colCount: Integer) = Grid(rowCount.intValue, colCount.intValue)

  def parse(str: String, charMap: String): Grid = {
    // Validate
    val strings = str.split('|').toIndexedSeq
    val rowCount = strings.length
    val colCount = if (rowCount == 0) 0 else strings(0).length
    if (strings exists { _.length != colCount }) {
      throw GridParseException("All rows of the position must have equal length.")
    }

    val compressed = str filterNot { _ == '|' }
    val bytes = compressed map { ch =>
      charMap.indexOf(ch.toLower) match {
        case -1 => throw GridParseException("The position may only contain the following characters: " + charMap)
        case n => n.toByte
      }
    }

    new Grid(rowCount, colCount, bytes.toArray)
  }

  private[util] var regionMarkers: Array[Int] = new Array[Int](0)
  private[util] var regionInfo: ArrayBuffer[RegionInfo] = new ArrayBuffer[RegionInfo]()

}

class Grid private[util] (val rowCount: Int, val colCount: Int, val values: Array[Byte]) extends Ordered[Grid] with Serializable {

  def get(row: Int, col: Int): Byte = values((row-1)*colCount+(col-1))

  def get(coord: Coordinates): Any = {
    if (isInBounds(coord)) {
      SmallInteger(get(coord.row, coord.col))
    }
    else
      null
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

  def updated(coords: scala.collection.Map[Coordinates, Integer]): Grid = {
    val newValues = new Array[Byte](values.length)
    System.arraycopy(values, 0, newValues, 0, values.length)
    val it = coords.iterator
    while (it.hasNext) {
      val (coord, newValue) = it.next()
      newValues((coord.row-1)*colCount+(coord.col-1)) = newValue.intValue.toByte
    }
    new Grid(rowCount, colCount, newValues)
  }

  def paste(grid: Grid, startRow: Int, startCol: Int, endRow: Int, endCol: Int, pasteRow: Int, pasteCol: Int): Grid = {
    val newValues = new Array[Byte](values.length)
    System.arraycopy(values, 0, newValues, 0, values.length)
    var row = pasteRow
    while (row < endRow - startRow + pasteRow) {
      var col = pasteCol
      while (col < endCol - startCol + pasteCol) {
        val value = grid get (startRow + row - pasteRow, startCol + col - pasteCol)
        newValues((row-1)*colCount+(col-1)) = value
        col += 1
      }
      row += 1
    }
    new Grid(rowCount, colCount, newValues)
  }

  def isInBounds(coord: Coordinates) = {
    val rowInt = coord.row
    val colInt = coord.col
    rowInt >= 1 && rowInt <= rowCount && colInt >= 1 && colInt <= colCount
  }

  def findAll(value: Integer): IndexedSeq[Any] = {
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

  def decomposition(boundaryValue: Integer, directions: IndexedSeq[Coordinates] = Coordinates.Orthogonal): IndexedSeq[Grid] = {
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
        markRegion(bv, i, info, directions)
        nextRegion += 1
      }
      i += 1
    }
    if (nextRegion == 0) {
      Vector.empty
    } else if (nextRegion == 1) {
      Vector(this)
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

  private def markRegion(bv: Byte, i: Int, info: RegionInfo, directions: IndexedSeq[Coordinates]): Unit = {
    if (Grid.regionMarkers(i) == -1 && values(i) != bv) {
      Grid.regionMarkers(i) = info.region
      info.minRow = info.minRow.min(i / colCount + 1)
      info.maxRow = info.maxRow.max(i / colCount + 1)
      info.minCol = info.minCol.min(i % colCount + 1)
      info.maxCol = info.maxCol.max(i % colCount + 1)
      val it = directions.iterator
      while (it.hasNext) {
        val direction = it.next()
        if ((i % colCount) + direction.col >= 0 && (i % colCount) + direction.col < colCount) {
          val newI = i + (direction.row * colCount) + direction.col
          if (newI >= 0 && newI < values.length) {
            markRegion(bv, newI, info, directions)
          }
        }
        // In order to correctly handle the case where directions is not closed under inversion
        // symmetry, we also try moving in the direction -D for each D in directions. This is
        // inefficient in the very common case where directions is closed under inversion
        // symmetry, but I'm not sure how to fix this without making it even less efficient.
        // (Idea: Introduce the concept of a DirectionSpace or some such with cached info such
        // as symmetric closure. Maybe in a later version.)
        if ((i % colCount) - direction.col >= 0 && (i % colCount) - direction.col < colCount) {
          val newI = i - (direction.row * colCount) - direction.col
          if (newI >= 0 && newI < values.length) {
            markRegion(bv, newI, info, directions)
          }
        }
      }
    }
  }

  def permute(symmetry: Symmetry): Grid = {
    val newRowCount = if (symmetry.isTranspose) colCount else rowCount
    val newColCount = if (symmetry.isTranspose) rowCount else colCount
    val newGrid = Grid(newRowCount, newColCount)
    var i = 0
    while (i < values.length) {
      val row = i / colCount
      val col = i % colCount
      val flippedRow = if (symmetry.isHorizontalFlip) rowCount-row-1 else row
      val flippedCol = if (symmetry.isVerticalFlip) colCount-col-1 else col
      val newRow = if (symmetry.isTranspose) flippedCol else flippedRow
      val newCol = if (symmetry.isTranspose) flippedRow else flippedCol
      newGrid.values(newRow * newColCount + newCol) = values(i)
      i += 1
    }
    newGrid
  }

  def symmetryInvariant(symmetries: IndexedSeq[Symmetry]): Grid = {
    // TODO We can be cleverer when rowCount != colCount
    var min = this
    symmetries foreach { symmetry =>
      if (symmetry != Symmetry.Identity) {
        val newGrid = min.permute(symmetry)
        if (newGrid < min)
          min = newGrid
      }
    }
    min
  }

  override def hashCode(): Int = util.Arrays.hashCode(values)

  override def equals(that: Any): Boolean = that match {
    case other: Grid =>
      rowCount == other.rowCount && util.Arrays.equals(values, other.values)
    case _ => false
  }

  def toString(charMap: String) = {
    val grouped = values map { charMap(_) } grouped colCount map { SeqCharSequence(_) }
    grouped mkString "|"
  }

  def compare(that: Grid): Int = {
    if (rowCount != that.rowCount)
      rowCount - that.rowCount
    else if (colCount != that.colCount)
      colCount - that.colCount
    else {
      var i = 0
      var cmp = 0
      while (cmp == 0 && i < values.length) {
        cmp = values(i) - that.values(i)
        i += 1
      }
      cmp
    }
  }

}

class RegionInfo(val region: Int) {
  var minRow: Int = 0
  var maxRow: Int = 0
  var minCol: Int = 0
  var maxCol: Int = 0
  override def toString = ((minRow,minCol),(maxRow,maxCol)).toString()
}

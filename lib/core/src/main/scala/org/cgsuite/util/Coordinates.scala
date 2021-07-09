package org.cgsuite.util

import java.util

import org.cgsuite.core.{Integer, Values}
import org.cgsuite.core.Values._
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

object Coordinates {

  val North     = Coordinates(-1,  0)
  val Northeast = Coordinates(-1,  1)
  val East      = Coordinates( 0,  1)
  val Southeast = Coordinates( 1,  1)
  val South     = Coordinates( 1,  0)
  val Southwest = Coordinates( 1, -1)
  val West      = Coordinates( 0, -1)
  val Northwest = Coordinates(-1, -1)

  val Orthogonal = Vector(North, East, South, West)
  val Diagonal = Vector(Northeast, Southeast, Southwest, Northwest)
  val Compass = Orthogonal ++ Diagonal

  def apply(row: Long, col: Long): Coordinates = {
    Coordinates(Integer(row), Integer(col))
  }

}

case class Coordinates(row: Integer, col: Integer) extends OutputTarget {

  def +(other: Coordinates) = Coordinates(row + other.row, col + other.col)

  def -(other: Coordinates) = Coordinates(row - other.row, col - other.col)

  def <=(other: Coordinates) = row <= other.row && col <= other.col

  def *(other: Integer) = Coordinates(row * other, col * other)

  def isUnit = row >= negativeOne && row <= one && col >= negativeOne && col <= one

  def swap = Coordinates(col, row)

  override def toOutput = new StyledTextOutput(util.EnumSet.of(StyledTextOutput.Style.FACE_MATH), s"($row,$col)")

}

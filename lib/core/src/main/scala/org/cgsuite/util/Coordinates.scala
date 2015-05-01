package org.cgsuite.util

import org.cgsuite.core.{SmallInteger, Integer}

object Coordinates {

  val North     = Coordinates(-1,  0)
  val Northeast = Coordinates(-1,  1)
  val East      = Coordinates( 0,  1)
  val Southeast = Coordinates( 1,  1)
  val South     = Coordinates( 1,  0)
  val Southwest = Coordinates( 1, -1)
  val West      = Coordinates( 0, -1)
  val Northwest = Coordinates(-1, -1)

  val Orthogonal = Seq(North, East, South, West)
  val Diagonal = Seq(Northeast, Southeast, Southwest, Northwest)
  val Compass = Orthogonal ++ Diagonal

  def apply(row: Int, col: Int): Coordinates = Coordinates(SmallInteger(row), SmallInteger(col))

}

case class Coordinates(row: Integer, col: Integer) {

  def +(other: Coordinates) = Coordinates(row + other.row, col + other.col)
  def -(other: Coordinates) = Coordinates(row - other.row, col - other.col)
  def *(other: Integer) = Coordinates(row * other, col * other)

  override def toString = s"($row,$col)"

}

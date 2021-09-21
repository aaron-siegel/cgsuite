package org.cgsuite.output

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.io.PrintWriter

import org.cgsuite.core.{RationalNumber, SurrealNumber}
import org.cgsuite.output.Output.Mode

case class IntensityPlotOutput(array: Seq[Seq[RationalNumber]], unitSize: Int = 8) extends AbstractOutput {

  val ord = implicitly[Ordering[SurrealNumber]]
  val rowCount = array.length
  val colCount = array.map { _.length }.max
  val minValue = array.map { _.min(ord) }.min(ord)
  val maxValue = array.map { _.max(ord) }.max(ord)
  val span = (maxValue - minValue).toFloat

  def write(out: PrintWriter, mode: Mode) {
    out println s"<$rowCount x $colCount IntensityPlot>"
  }

  def getSize(preferredWidth: Int): Dimension = new Dimension(colCount * unitSize, rowCount * unitSize)

  def paint(graphics: Graphics2D, preferredWidth: Int) {

    for (i <- array.indices; j <- array(i).indices) {
      val color: Float = (array(i)(j) - minValue).toFloat / span
      graphics.setColor(new Color(color, color, color))
      graphics.fillRect(j * unitSize, i * unitSize, unitSize, unitSize)
    }

  }

}

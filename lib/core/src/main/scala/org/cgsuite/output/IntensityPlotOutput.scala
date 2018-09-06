package org.cgsuite.output

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.io.PrintWriter

import org.cgsuite.output.Output.Mode

case class IntensityPlotOutput(array: Seq[Seq[Int]], unitSize: Int = 8) extends AbstractOutput {

  val rowCount = array.length
  val colCount = array.map { _.length }.max
  val maxValue = array.map { _.max }.max

  def write(out: PrintWriter, mode: Mode) {
    throw new UnsupportedOperationException("Not supported yet.")
  }

  def getSize(preferredWidth: Int): Dimension = new Dimension(colCount * unitSize, rowCount * unitSize)

  def paint(graphics: Graphics2D, preferredWidth: Int) {

    for (i <- array.indices; j <- array(i).indices) {
      val color: Float = array(i)(j).toFloat / maxValue.toFloat
      graphics.setColor(new Color(color, color, color))
      graphics.fillRect(j * unitSize, i * unitSize, unitSize, unitSize)
    }

  }

}

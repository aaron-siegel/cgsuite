package org.cgsuite.output

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.io.PrintWriter

import org.cgsuite.output.Output.Mode

class IntensityPlotOutput(array: Seq[Seq[Int]]) extends Output {

  val rowCount = array.length
  val colCount = array.map { _.length }.max
  val maxValue = array.map { _.max }.max

  def write(out: PrintWriter, mode: Mode) {
    throw new UnsupportedOperationException("Not supported yet.")
  }

  def getSize(preferredWidth: Int): Dimension = new Dimension(colCount * 8, rowCount * 8)

  def paint(graphics: Graphics2D, preferredWidth: Int) {

    for (i <- 0 to rowCount; j <- 0 to array(i).length) {
      val color: Float = array(i)(j).toFloat / maxValue.toFloat
      graphics.setColor(new Color(color, color, color))
      graphics.fillRect(j * 8, i * 8, 8, 8)
    }

  }

}

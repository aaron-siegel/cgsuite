package org.cgsuite.output

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Cursor, Dimension, Graphics2D}
import java.io.PrintWriter

import org.cgsuite.output.Output.Mode
import org.cgsuite.util.Coordinates

object ScatterPlotOutput {

  val tickGranularities = {
    for (multiplier <- Vector(1, 10, 100, 1000, 10000, 100000, 1000000);
         base <- Vector(1, 2, 5)) yield {
      base * multiplier
    }
  }

  def tickString(n: Int, shorten: Boolean) = {
    if (shorten && n % 1000 == 0) {
      s"${n / 1000}k"
    } else {
      n.toString
    }
  }

  val DashedStroke = new BasicStroke(
    1.0f,
    BasicStroke.CAP_BUTT,
    BasicStroke.JOIN_BEVEL,
    0,
    Array(3.0f),
    0
  )

}

case class ScatterPlotOutput(array: Iterable[Coordinates]) extends AbstractOutput {

  val width = 1024
  val height = 640
  var cachedImage: BufferedImage = _
  var mousePosition: (Int, Int) = _

  val maxRow = array.map { _.row }.max max 0
  val baseMinRow = array.map { _.row }.min min 0
  val minRow = baseMinRow min (-maxRow / 9)
  val rowCount = maxRow - minRow + 1

  val maxCol = array.map { _.col }.max max 0
  val baseMinCol = array.map { _.col }.min min 0
  val minCol = baseMinCol min (-maxCol / 9)
  val colCount = maxCol - minCol + 1

  val xScale = width.toDouble / colCount
  val yScale = height.toDouble / rowCount

  def write(out: PrintWriter, mode: Mode): Unit = {
    out print s"<$rowCount x $colCount ScatterPlot>"
  }

  def getSize(preferredWidth: Int): Dimension = {
    new Dimension(width, height)
  }

  val xGranularity = ScatterPlotOutput.tickGranularities.find { _ * 10 >= colCount }
  val yGranularity = ScatterPlotOutput.tickGranularities.find { _ * 8 >= rowCount }

  def paint(graphics: Graphics2D, preferredWidth: Int): Unit = {

    if (cachedImage == null) {
      cachedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
      paintBuffer(cachedImage.getGraphics.asInstanceOf[Graphics2D])
    }

    graphics.drawImage(cachedImage, 0, 0, null)

    if (mousePosition != null) {
      val (x, y) = mousePosition
      val col = (x / xScale + minCol).toInt
      val row = (-y / yScale + maxRow).toInt
      if (col >= baseMinCol && row >= baseMinRow) {
        graphics.setStroke(ScatterPlotOutput.DashedStroke)
        graphics.drawLine(0, y, width, y)
        graphics.drawLine(x, 0, x, height)
        graphics.setColor(Color.red)
        graphics.drawString(s"($col,$row)", x + 3, y - graphics.getFontMetrics.getAscent / 2)
      }
    }

  }

  def paintBuffer(graphics: Graphics2D): Unit = {

    // Background
    graphics.setColor(Color.white)

    graphics.fillRect(0, 0, width, height)

    val xZero = (-minCol * xScale).toInt
    val yZero = (maxRow * yScale).toInt

    // Axes
    graphics.setColor(Color.black)
    graphics.drawLine(0, yZero, width, yZero)
    graphics.drawLine(xZero, 0, xZero, height)

    val fm = graphics.getFontMetrics

    // x Ticks
    xGranularity match {
      case Some(granularity) =>
        val step = (granularity / 10) max 1
        val ticks = step to maxCol by step
        for (col <- ticks) {
          val x = ((col - minCol) * xScale).toInt
          val isMajorTick = col % granularity == 0
          graphics.drawLine(x, yZero, x, yZero + (if (isMajorTick) 15 else 5))
          if (isMajorTick) {
            val text = ScatterPlotOutput.tickString(col, granularity % 1000 == 0)
            if (x + fm.stringWidth(text) / 2 < width) {
              val xTick = x - fm.stringWidth(text) / 2
              graphics.drawString(text, xTick, yZero + 20 + fm.getAscent)
            }
          }
        }
      case None =>
    }

    // y Ticks
    yGranularity match {
      case Some(granularity) =>
        val step = (granularity / 10) max 1
        val ticks = step to maxRow by step
        val majorTicks = granularity to maxRow by granularity
        val maxStringWidth = majorTicks.map(ScatterPlotOutput.tickString(_, granularity % 1000 == 0)).map(fm.stringWidth).max
        for (row <- ticks) {
          val y = ((maxRow - row) * yScale).toInt
          val isMajorTick = row % granularity == 0
          graphics.drawLine(xZero, y, xZero - (if (isMajorTick) 15 else 5), y)
          if (isMajorTick) {
            val text = ScatterPlotOutput.tickString(row, granularity % 1000 == 0)
            if (y - fm.getAscent >= 0) {
              graphics.drawString(text, xZero - 25 - maxStringWidth, y + fm.getAscent / 3)
            }
          }
        }
      case None =>
    }

    // Points
    graphics.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER))
    for (coord <- array) {
      val x = ((coord.col - minCol) * xScale).toInt
      val y = ((maxRow - coord.row) * yScale).toInt
      graphics.drawLine(x, y, x, y)
    }

  }

  override def box(): OutputBox = {
    new OutputBox(this) {
      addMouseListener(new MouseAdapter {
        override def mouseExited(evt: MouseEvent): Unit = {
          mousePosition = null
          setCursor(Cursor.getDefaultCursor)
          repaint()
        }
      })
      addMouseMotionListener(new MouseAdapter {
        override def mouseMoved(evt: MouseEvent): Unit = {
          mousePosition = (evt.getX, evt.getY)
          val col = (evt.getX / xScale + minCol).toInt
          val row = (-evt.getY / yScale + maxRow).toInt
          if (col >= baseMinCol && row >= baseMinRow) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR))
          } else {
            setCursor(Cursor.getDefaultCursor)
          }
          repaint()
        }
      })
    }
  }

}

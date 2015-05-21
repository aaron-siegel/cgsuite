package org.cgsuite.output

import java.awt.{Color, Dimension, Graphics2D}
import java.io.PrintWriter

import org.cgsuite.lang.Table.Format

object TableOutput {

  def repchar(ch: Char, n: Int) = {
    SeqCharSequence((1 to n) map { _ => ch })
  }

}

case class TableOutput(table: Seq[Seq[Output]], format: Set[Format.Value], maxCellWidth: Int) extends AbstractOutput {

  val rowCount = table.size
  val colCount = table.map { _.size }.max
  val hSpace = if (format.contains(Format.HorizontalGridLines)) 6 else 0
  val vSpace = if (format.contains(Format.VerticalGridLines)) 6 else 0
  lazy val rowHeight = table map { _.map { _.getSize(0).height }.max }
  lazy val colWidth = (0 until colCount) map { n => table.map { row => if (row.length > n) row(n).getSize(0).width else 0 }.max }
  lazy val height = rowHeight.sum + vSpace * 2 * (rowCount - 1) + rowCount + 1
  lazy val width = colWidth.sum + hSpace * 2 * (colCount - 1) + colCount + 1

  def getSize(preferredWidth: Int) = new Dimension(width, height)

  def paint(g: Graphics2D, preferredWidth: Int) {

    g.setBackground(Color.white)
    g.setColor(Color.black)
    g.clearRect(0, 0, width, height)

    if (format.contains(Format.HorizontalGridLines)) {
      for (i <- 0 to rowCount) {
        val pos = rowHeight.take(i).sum + i * (vSpace * 2 + 1)
        g.drawLine(0, pos, width, pos)
      }
    }
    if (format.contains(Format.VerticalGridLines)) {
      for (j <- 0 to colCount) {
        val pos = colWidth.take(j).sum + j * (hSpace * 2 + 1)
        g.drawLine(pos, 0, pos, height)
      }
    }
    for (i <- 0 until table.size; j <- 0 until table(i).size) {
      val hPos = colWidth.take(j).sum + j * (hSpace * 2 + 1)
      val vPos = rowHeight.take(i).sum + i * (vSpace * 2 + 1)
      val entry = table(i)(j)
      if (entry != null && g.hitClip(hPos + hSpace + 1, vPos + vSpace + 1, colWidth(j), rowHeight(i))) {
        val cellSize = entry.getSize(0)
        val topEdge: Int = (rowHeight(i) - cellSize.height) / 2
        val leftEdge: Int = (colWidth(j) - cellSize.width) / 2
        if (g.hitClip(hPos + hSpace + 1 + leftEdge, vPos + vSpace + 1 + topEdge, cellSize.width, cellSize.height)) {
          entry.paint(g.create(hPos + hSpace + 1 + leftEdge, vPos + vSpace + 1 + topEdge, cellSize.width, cellSize.height).asInstanceOf[Graphics2D], 0)
        }
      }
    }

  }

  def write(out: PrintWriter, mode: Output.Mode) {

    assert(mode == Output.Mode.PLAIN_TEXT)
    val outputStrings = table map { _ map { _.toString } }
    val colStringWidth = (0 until colCount) map { n => outputStrings.map { row => if (row.length > n) row(n).length else 0 }.max }
    val formats = colStringWidth map { width => s"%${width}s" }
    val rowStrings = outputStrings map { row =>
      val paddedEntries = row zip (0 until row.size) map { case (entry, j) => formats(j).format(entry) }
      paddedEntries mkString " | "
    }
    val sep = colStringWidth map { TableOutput.repchar('-', _) } mkString "-+-"
    out print (rowStrings mkString ("\n" + sep + "\n"))

  }

}


package org.cgsuite.output

import java.awt.{Color, Dimension, Graphics2D}
import java.io.PrintWriter
import javax.swing.{Icon, ImageIcon}

import org.cgsuite.exception.InputException
import org.cgsuite.lang.{EnumObject, StandardObject}
import org.cgsuite.util.Grid

import scala.collection.mutable

/**
 * A grid to display as output.
 *
 * @see     Output
 * @author  Aaron Siegel
 * @version $Revision: 1.5 $ $Date: 2006/04/07 06:02:47 $
 */
object GridOutput {

  def iconDimensions(icons: Seq[Icon], forceSquares: Boolean): Dimension = {
    val maxHeight = icons.map { _.getIconHeight }.max
    val maxWidth = icons.map { _.getIconWidth }.max
    new Dimension(
      if (forceSquares) maxWidth max maxHeight else maxWidth,
      if (forceSquares) maxWidth max maxHeight else maxHeight
    )
  }

  private def imageDimensions(grid: Grid, cellSize: Dimension, gap: Int, lineThickness: Int): Dimension = {
    new Dimension(
      (grid.colCount * (cellSize.width + 2 * gap + lineThickness)) + lineThickness,
      (grid.rowCount * (cellSize.height + 2 * gap + lineThickness)) + lineThickness
    )
  }

  private val icons = mutable.Map[String, Icon]()

  def lookupIcon(name: String) = {
    icons.getOrElseUpdate(name, {
      val url =
        Option(classOf[GridOutput].getResource("resources/" + name + ".png")) orElse
        Option(classOf[GridOutput].getResource("resources/" + name + ".gif")) getOrElse {
          throw InputException(s"System error loading icon: `$name`")
        }
      new ImageIcon(url)
    })
  }

}

case class GridOutput(grid: Grid, iconEnumValues: Seq[_], alt: String) extends AbstractOutput {

  lazy val icons = iconEnumValues map {
    case x: EnumObject => GridOutput.lookupIcon(x.literal)
  }

  lazy val cellSize = GridOutput.iconDimensions(icons, false)
  lazy val size = GridOutput.imageDimensions(grid, cellSize, 1, 1)

  def write(out: PrintWriter, mode: Output.Mode) {
    mode match {
      case Output.Mode.PLAIN_TEXT => out.print(alt)
      case _ => throw new UnsupportedOperationException
    }
  }

  override def getSize(preferredWidth: Int): Dimension = size

  def paint(g: Graphics2D, preferredWidth: Int) {

    val totalSize: Dimension = GridOutput.imageDimensions(grid, cellSize, 1, 1)
    g.setBackground(Color.white)
    g.clearRect(0, 0, totalSize.width, totalSize.height)
    g.setColor(Color.black)
    for (row <- 0 to grid.rowCount) {
      g.drawLine(0, row * (3 + cellSize.height), grid.colCount * (3 + cellSize.width), row * (3 + cellSize.height))
    }
    for (col <- 0 to grid.colCount) {
      g.drawLine(col * (3 + cellSize.width), 0, col * (3 + cellSize.width), grid.rowCount * (3 + cellSize.height))
    }
    for (row <- 0 until grid.rowCount; col <- 0 until grid.colCount) {
      val icon = icons(grid.get(row + 1, col + 1))
      if (icon != null) {
        icon.paintIcon(null, g, col * (3 + cellSize.width) + 2 + (cellSize.width - icon.getIconWidth) / 2, row * (3 + cellSize.height) + 2 + (cellSize.height - icon.getIconHeight) / 2)
      }
    }

  }
}

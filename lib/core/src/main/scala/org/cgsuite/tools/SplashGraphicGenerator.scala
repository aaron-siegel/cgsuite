package org.cgsuite.tools

import java.awt.image.BufferedImage
import java.awt._
import java.io.File

import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}

object SplashGraphicGenerator {

  def main(args: Array[String]): Unit = {

    val panel = new LogoPanel
    panel.setBackground(background)

    val image = new BufferedImage(640, 458, BufferedImage.TYPE_INT_ARGB)
    renderToGraphics(image.createGraphics())
    ImageIO.write(image, "png", new File("splash.png"))

    val frame = new JFrame
    frame.setLayout(new BorderLayout())
    frame.add(panel, BorderLayout.CENTER)
    frame.setSize(new Dimension(640, 480))
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)

  }

  val background = new Color(250, 250, 250)
  val backgroundNoAlpha = new Color(250, 250, 250, 0)

  val l = new Color(0, 100, 255)
  val r = Color.red
  val e = new Color(0, 220, 0)
  val w = Color.white

  val C = Vector((10, 0, l), (0, 0, r), (0, 12, e), (10, 12, w))
  val G = Vector((10, 12, l), (0, 12, r), (0, 0, e), (10, 0, r), (10, 12, e), (10, 20, r), (1, 20, w))
  val S = Vector((10, 0, l), (0, 0, r), (0, 5, e), (10, 7, l), (10, 12, r), (0, 12, w))
  val U = Vector((0, 0, e), (0, 12, l), (10, 12, r), (10, 0, w))
  val I = Vector((0, 12, l), (0, 0, w), (0, -4, w))
  val T = Vector((0, 0, l), (4, 0, l), (8, 0, l), (4, 0, r), (4, -4, r), (4, 0, e), (4, 12, r), (8, 12, w))
  val E = Vector((0, 6, l), (10, 6, e), (10, 0, r), (0, 0, e), (0, 6, r), (0, 12, e), (10, 12, w))

  val scale = 6.0
  val nodeDiameter = 2
  val stalkSpacing = 2.0
  val stalkThickness = 1.25

  def renderToGraphics(g: Graphics2D): Unit = {

    g.setColor(background)
    g.fillRect(0, 0, 640, 458)
    g.translate(170, 85)
    g.scale(0.85, 0.85)
    renderThermograph(g)
    val paint = new LinearGradientPaint(
      0, 0, 0, 160,
      Array(0.0f, 0.15f, 0.85f, 1.0f),
      Array(backgroundNoAlpha, background, background, backgroundNoAlpha)
    )
    g.setPaint(paint)
    g.fillRect(0, 0, 640, 160)
    g.scale(1 / 0.85, 1 / 0.85)
    g.translate(-170, -85)

    g.translate(55, 120)
    renderLetter(g, C, 0)
    renderLetter(g, G, 15)
    renderLetter(g, S, 30)
    renderLetter(g, U, 45)
    renderLetter(g, I, 60)
    renderLetter(g, T, 65)
    renderLetter(g, E, 78)
    g.translate(-55, -120)

    g.setStroke(new BasicStroke(10.0f))
    g.drawRect(0, 0, 640, 458)

  }

  def renderLetter(g: Graphics2D, letter: Vector[(Int, Int, Color)], xShift: Int): Unit = {

    g.translate(xShift * scale, 0)
    for (i <- letter.indices) {
      val (x1, y1, color) = letter(i)
      g.setColor(Color.black)
      g.fillOval(
        math.round((x1 - nodeDiameter / 2) * scale).toInt,
        math.round((y1 - nodeDiameter / 2) * scale).toInt,
        math.round(nodeDiameter * scale).toInt,
        math.round(nodeDiameter * scale).toInt
      )
      if (i < letter.length - 1) {
        g.setColor(color)
        val (x2, y2, _) = letter(i + 1)
        val theta = math.atan2(y2 - y1, x2 - x1)
        val dist = math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
        val width = math.round((dist - stalkSpacing) * scale).toInt
        val height = math.round(stalkThickness * scale).toInt
        g.translate((x1 + (x2 - x1) / 2.0) * scale, (y1 + (y2 - y1) / 2.0) * scale)
        g.rotate(theta)
        g.fillRoundRect(
          -width / 2,
          -height / 2,
          width,
          height,
          1,
          1
        )
        g.rotate(-theta)
        g.translate(-(x1 + (x2 - x1) / 2.0) * scale, -(y1 + (y2 - y1) / 2.0) * scale)
      }
    }
    g.translate(-xShift * scale, 0)

  }

  def renderThermograph(g: Graphics2D): Unit = {

    g.setColor(Color.black)
    g.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
    g.drawLine(0, 400, 200, 200)
    g.drawLine(300, 400, 300, 300)
    g.drawLine(300, 300, 200, 200)
    g.drawLine(200, 200, 200, -50)
    g.drawLine(200, -60, 180, -40)
    g.drawLine(200, -60, 220, -40)

  }

}

class LogoPanel extends JPanel() {

  override def paint(g: Graphics): Unit = {

    super.paint(g)
    SplashGraphicGenerator.renderToGraphics(g.asInstanceOf[Graphics2D])

  }

}
package org.cgsuite.output

import java.awt.Graphics2D
import java.io.PrintWriter

object TextOutput {

  def apply(str: String): TextOutput = {
    TextOutput(new StyledTextOutput(str))
  }

}

// Scala wrapper for StyledTextOutput (temporary solution)
case class TextOutput(sto: StyledTextOutput) extends Output {

  override def write(out: PrintWriter, mode: Output.Mode): Unit = {
    sto.write(out, mode)
  }

  override def getSize(preferredWidth: Int) = sto.getSize(preferredWidth)

  override def paint(graphics: Graphics2D, preferredWidth: Int): Unit = {
    sto.paint(graphics, preferredWidth)
  }

  override def toOutput = this

  override def toString = sto.toString

}

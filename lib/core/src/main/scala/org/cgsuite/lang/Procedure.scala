package org.cgsuite.lang

import java.util

import org.cgsuite.output.StyledTextOutput.Style
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

case class Procedure[T, R](arity: Integer, nodeString: String)(val fn: T => R) extends OutputTarget {

  def apply(arguments: T): R = fn(arguments)

  override def toOutput: StyledTextOutput = new StyledTextOutput(util.EnumSet.of(Style.FACE_MONOSPACED), nodeString)

}

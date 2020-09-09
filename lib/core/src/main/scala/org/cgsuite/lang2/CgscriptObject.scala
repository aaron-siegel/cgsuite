package org.cgsuite.lang2

import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

trait CgscriptObject extends OutputTarget {

  override def toOutput: Output = new StyledTextOutput(toString)

  def _class: CgscriptClass

}

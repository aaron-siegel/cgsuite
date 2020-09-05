package org.cgsuite.lang2

import org.cgsuite.output.{OutputTarget, StyledTextOutput}

trait StandardObject extends OutputTarget {

  override def toOutput = new StyledTextOutput(toString)

}

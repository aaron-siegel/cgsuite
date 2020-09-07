package org.cgsuite.output


trait OutputTarget {

  def toOutput: Output = new StyledTextOutput("no output")

}

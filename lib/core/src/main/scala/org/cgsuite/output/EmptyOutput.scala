package org.cgsuite.output

case object EmptyOutput extends StyledTextOutput {
  appendText("Nothing")
}

class EmptyOutput

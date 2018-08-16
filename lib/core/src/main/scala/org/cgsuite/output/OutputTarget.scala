package org.cgsuite.output


trait OutputTarget {

  def toOutput: Output
  override def toString: String = toOutput.toString

}

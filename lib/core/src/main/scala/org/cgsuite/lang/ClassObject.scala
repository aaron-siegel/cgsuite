package org.cgsuite.lang

import org.cgsuite.output.StyledTextOutput

class ClassObject(val forClass: CgscriptClass)
  extends StandardObject(CgscriptClass.Class, Array.empty) {

  def name = forClass.name
  def ordinal = forClass.classOrdinal
  def qualifiedName = forClass.qualifiedName

  override def init(): Unit = {
    vars = new Array[Any](forClass.classInfo.staticVarSymbols.size)
  }

  override def toOutput: StyledTextOutput = {
    val sto = new StyledTextOutput
    sto appendMath s"\u27ea$qualifiedName\u27eb"
    sto
  }

}

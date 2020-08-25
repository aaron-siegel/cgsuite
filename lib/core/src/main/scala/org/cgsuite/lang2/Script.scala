package org.cgsuite.lang2

import org.cgsuite.output.StyledTextOutput

case class Script(cls: CgscriptClass, node: StatementSequenceNode, scope: ElaborationDomain) {

  def name: String = cls.name

  def ordinal: Int = cls.classOrdinal

  def qualifiedName: String = cls.qualifiedName

}

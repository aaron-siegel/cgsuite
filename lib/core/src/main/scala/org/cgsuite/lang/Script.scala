package org.cgsuite.lang

case class Script(cls: CgscriptClass, node: StatementSequenceNode, scope: ElaborationDomain) {

  def name: String = cls.name

  def ordinal: Int = cls.classOrdinal

  def qualifiedName: String = cls.qualifiedName

}

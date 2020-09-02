package org.cgsuite.lang2

case class Script(cls: CgscriptClass, node: StatementSequenceNode, domain: ElaborationDomain2) {

  def name: String = cls.name

  def ordinal: Int = cls.classOrdinal

  def qualifiedName: String = cls.qualifiedName

}

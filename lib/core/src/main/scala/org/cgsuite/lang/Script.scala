package org.cgsuite.lang

case class Script(cls: CgscriptClass, node: StatementSequenceNode, domain: ElaborationDomain) {

  def name: String = cls.nameInPackage

  def ordinal: Int = cls.classOrdinal

  def qualifiedName: String = cls.qualifiedName

}

package org.cgsuite.lang

trait Member extends MemberResolution {

  def declaringClass: CgscriptClass

  def declNode: Option[MemberDeclarationNode]

  def idNode: IdentifierNode

  def id = idNode.id

}

trait MemberResolution {

  def declaringClass: CgscriptClass

  def id: Symbol

}

case class Parameter(idNode: IdentifierNode, paramType: CgscriptClass, defaultValue: Option[EvalNode], isExpandable: Boolean) {

  val id = idNode.id

  val signature = paramType.qualifiedName + " " + id.name + (if (defaultValue.isDefined) "?" else "") + (if (isExpandable) "..." else "")

  var methodScopeIndex = -1

}
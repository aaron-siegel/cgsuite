package org.cgsuite.lang

import org.cgsuite.lang.node.{EvalNode, IdentifierNode, MemberDeclarationNode, Modifiers}

trait Member extends MemberResolution {

  def declaringClass: CgscriptClass

  def declNode: Option[MemberDeclarationNode]

  def idNode: IdentifierNode

  def modifiers: Modifiers

  def id = idNode.id

  def name = id.name

  def isMutable = modifiers.hasMutable

  def isSingleton = modifiers.hasSingleton

  def isStatic = modifiers.hasStatic

  def isSystem = modifiers.hasSystem

}

trait MemberResolution {

  private var valid = true

  def declaringClass: CgscriptClass

  def id: Symbol

  def isValid: Boolean = valid

  def invalidate(): Unit = valid = false

}

case class Parameter(idNode: IdentifierNode, paramType: CgscriptClass, defaultValue: Option[EvalNode], isExpandable: Boolean) {

  val id = idNode.id

  val signature = paramType.qualifiedName + " " + id.name + (if (defaultValue.isDefined) "?" else "") + (if (isExpandable) "..." else "")

  var methodScopeIndex = -1

}

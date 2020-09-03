package org.cgsuite.lang2


trait Member {

  def declaringClass: CgscriptClass
  def declNode: Option[MemberDeclarationNode]
  def idNode: IdentifierNode
  def id = idNode.id
  def mentionedClasses: Iterable[CgscriptClass] = Iterable.empty

  private var elaboratedResultTypeRef: CgscriptType = _

  def resultType = {
    if (elaboratedResultTypeRef == null)
      throw new RuntimeException(s"Member has not been elaborated: $this")
    else
      elaboratedResultTypeRef
  }

  def ensureElaborated(): CgscriptType = {
    if (elaboratedResultTypeRef == null) {
      declaringClass logDebug s"Elaborating member: ${id.name}"
      elaboratedResultTypeRef = elaborate()
    }
    elaboratedResultTypeRef
  }

  def elaborate(): CgscriptType

}

case class MethodSignature(id: Symbol, paramTypes: Vector[CgscriptType])

case class Parameter(idNode: IdentifierNode, paramType: CgscriptType, defaultValue: Option[EvalNode], isExpandable: Boolean) {

  val id = idNode.id

  val signature = {
    val optQuestionMark = if (defaultValue.isDefined) "?" else ""
    val optEllipsis = if (isExpandable) " ..." else ""
    s"${id.name} as ${paramType.qualifiedName}$optQuestionMark$optEllipsis"
  }

  var methodScopeIndex = -1

}
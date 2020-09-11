package org.cgsuite.lang2


trait Member extends MemberResolution {

  def declNode: Option[MemberDeclarationNode]
  def idNode: IdentifierNode
  def mentionedClasses: Iterable[CgscriptClass]

  override def id = idNode.id

  var isElaborating = false

  private var elaboratedResultTypeRef: CgscriptType = _

  def ensureElaborated(): CgscriptType = {
    if (elaboratedResultTypeRef == null) {
      declaringClass logDebug s"Elaborating member: ${id.name}"
      if (isElaborating)
        sys.error("already elaborating")
      isElaborating = true
      elaboratedResultTypeRef = elaborate()
      isElaborating = false
    }
    elaboratedResultTypeRef
  }

  def elaborate(): CgscriptType

}

trait MemberResolution {

  def declaringClass: CgscriptClass

  def id: Symbol

}

case class MethodSignature(id: Symbol, paramTypes: Vector[CgscriptType])

case class Parameter(idNode: IdentifierNode, paramType: CgscriptType, defaultValue: Option[EvalNode], isExpandable: Boolean) {

  val id = idNode.id

  val name = id.name

  val signature = {
    val optQuestionMark = if (defaultValue.isDefined) "?" else ""
    val optEllipsis = if (isExpandable) " ..." else ""
    s"${id.name} as ${paramType.qualifiedName}$optQuestionMark$optEllipsis"
  }

  var methodScopeIndex = -1

  def mentionedClasses: Iterable[CgscriptClass] = {
    paramType.mentionedClasses ++ (defaultValue.toIterable flatMap { _.mentionedClasses })
  }

  def toScalaCode(context: CompileContext): String = {
    val defaultValueClause = defaultValue match {
      case Some(node) => " = " + node.toScalaCode(context)
      case None => ""
    }
    s"$name: ${paramType.scalaTypeName}$defaultValueClause"
  }

}

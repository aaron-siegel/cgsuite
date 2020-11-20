package org.cgsuite.lang


trait Member extends MemberResolution {

  def declNode: Option[MemberDeclarationNode]

  def mentionedClasses: Iterable[CgscriptClass]

  def scalaName: String

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
      declaringClass logDebug s"Elaborated member `${id.name}` as type `${elaboratedResultTypeRef.qualifiedName}`"
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

object Parameter {

  def emitScalaCode(parameters: IndexedSeq[Parameter], context: CompileContext, emitter: Emitter): Unit = {
    for (i <- parameters.indices) {
      parameters(i).emitScalaCode(context, emitter)
      if (i < parameters.length - 1)
        emitter print ", "
    }
  }

}

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

  def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    emitter print s"$name: ${paramType.scalaTypeName}"

    defaultValue foreach { node =>
      emitter print " = "
      node.emitScalaCode(context, emitter)
    }

  }

}

package org.cgsuite.lang2

object CgscriptType {

  def apply(cls: CgscriptClass, typeParameters: Vector[CgscriptType] = Vector.empty): CgscriptType = {
    ConcreteType(cls, typeParameters)
  }

}

sealed trait CgscriptType {

  def qualifiedName: String

  def scalaTypeName: String

  def baseClass: CgscriptClass

  def typeParameters: Vector[CgscriptType]

  def substitute(variable: TypeVariable, substitution: CgscriptType): CgscriptType

  def <=(that: CgscriptType): Boolean

}

case class TypeVariable(id: Symbol) extends CgscriptType {

  override def qualifiedName = id.name

  override def scalaTypeName = id.name

  override def baseClass = sys.error("type variable cannot resolve to a class (this should never happen)")

  override def typeParameters = sys.error("type variable cannot resolve to a class (this should never happen)")

  override def substitute(variable: TypeVariable, substitution: CgscriptType): CgscriptType = {
    if (id == variable.id)
      substitution
    else
      this
  }

  def <=(that: CgscriptType) = {

    that match {
      case _: ConcreteType => false
      case _: IntersectionType => false
      case thatTypeVariable: TypeVariable => id == thatTypeVariable.id
    }

  }

}

case class ConcreteType(baseClass: CgscriptClass, typeParameters: Vector[CgscriptType] = Vector.empty) extends CgscriptType {

  def qualifiedName: String = {

    val baseName = baseClass.qualifiedName

    typeParameters.size match {
      case 0 => baseName
      case 1 =>
        val typeParamName = typeParameters.head.qualifiedName
        s"$typeParamName $baseName"
      case _ =>
        val typeParamNames = typeParameters map { _.qualifiedName } mkString ", "
        s"($typeParamNames) $baseName"

    }

  }

  def scalaTypeName: String = {
    val baseName = baseClass.scalaClassname
    // TODO This is a temporary hack
    if ((baseName endsWith "IndexedSeq") || (baseName endsWith "Set") || (baseName endsWith "Iterable")) {
      val typeParameter = typeParameters.headOption map { _.scalaTypeName } getOrElse "Any"
      s"$baseName[$typeParameter]"
    } else {
      baseName
    }
  }

  override def substitute(variable: TypeVariable, substitution: CgscriptType): ConcreteType = {
    ConcreteType(baseClass, typeParameters map { _.substitute(variable, substitution) })
  }

  def <=(that: CgscriptType): Boolean = {

    that match {
      case thatConcreteType: ConcreteType =>
        baseClass <= thatConcreteType.baseClass && {
//          assert(typeParameters.length == thatConcreteType.typeParameters.length, (this, that))
          typeParameters zip thatConcreteType.typeParameters forall { case (thisParam, thatParam) =>
            thisParam <= thatParam
          }
        }
      case _: TypeVariable => false
      case thatIntersectionType: IntersectionType =>
        thatIntersectionType.components forall { this <= _ }
    }
  }

}

case class IntersectionType(components: Vector[ConcreteType]) extends CgscriptType {

  override def qualifiedName = ???

  override def scalaTypeName = components map { _.scalaTypeName } mkString " with "

  override def baseClass = ???

  override def typeParameters = ???

  override def substitute(variable: TypeVariable, substitution: CgscriptType): IntersectionType = {
    IntersectionType(components map { _.substitute(variable, substitution) })
  }

  override def <=(that: CgscriptType) = {

    that match {
      case thatConcreteType: ConcreteType =>
        components exists { _ <= thatConcreteType }
      case _: TypeVariable => false
      case thatIntersectionType: IntersectionType =>
        components exists { _ <= thatIntersectionType }
    }

  }

}

case class CgscriptTypeList(types: Vector[CgscriptType]) {

  def <=(that: CgscriptTypeList): Boolean = {
    types zip that.types forall { case (thisType, thatType) => thisType <= thatType }
  }

}

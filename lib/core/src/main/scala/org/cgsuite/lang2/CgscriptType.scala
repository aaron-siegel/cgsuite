package org.cgsuite.lang2

object CgscriptType {

  def apply(cls: CgscriptClass, typeParameters: Vector[CgscriptType] = Vector.empty): CgscriptType = {
    ConcreteType(cls, typeParameters)
  }

  def reduceClasses(classes: Iterable[CgscriptClass]) = {
    classes filterNot { cls =>
      classes exists { thatClass => thatClass != cls && thatClass <= cls }
    }
  }

}

sealed trait CgscriptType {

  def qualifiedName: String

  def scalaTypeName: String

  def baseClass: CgscriptClass

  def typeArguments: Vector[CgscriptType]

  def substitute(variable: TypeVariable, substitution: CgscriptType): CgscriptType

  def join(that: CgscriptType): CgscriptType

  def lookupMethod(id: Symbol, argTypes: Vector[CgscriptType]): Option[CgscriptClass#Method]

  def <=(that: CgscriptType): Boolean

  def substituteAll(substitutions: Iterable[(TypeVariable, CgscriptType)]): CgscriptType = {
    substitutions.foldLeft (this) { case (currentType, (variable, substitution)) =>
      currentType.substitute(variable, substitution)
    }
  }

}

case class TypeVariable(id: Symbol) extends CgscriptType {

  override def qualifiedName = id.name

  override def scalaTypeName = s"__typevar_${id.name.drop(1)}"

  override def baseClass = sys.error("type variable cannot resolve to a class (this should never happen)")

  override def typeArguments = sys.error("type variable cannot resolve to a class (this should never happen)")

  override def lookupMethod(id: Symbol, argTypes: Vector[CgscriptType]) = sys.error("type variable cannot resolve to a class (this should never happen)")

  override def substitute(variable: TypeVariable, substitution: CgscriptType): CgscriptType = {
    if (id == variable.id)
      substitution
    else
      this
  }

  override def join(that: CgscriptType) = {
    if (that == this) this
    else CgscriptType(CgscriptClass.Object)
  }

  override def <=(that: CgscriptType) = {

    that match {
      case _: ConcreteType => false
      case _: IntersectionType => false
      case thatTypeVariable: TypeVariable => id == thatTypeVariable.id
    }

  }

}

case class ConcreteType(baseClass: CgscriptClass, typeArguments: Vector[CgscriptType] = Vector.empty) extends CgscriptType {

  if (baseClass.isDeclaredPhase1)
    assert(baseClass.typeParameters.length == typeArguments.length, this)

  def qualifiedName: String = {

    val baseName = baseClass.qualifiedName

    typeArguments.size match {
      case 0 => baseName
      case 1 =>
        val typeParamName = typeArguments.head.qualifiedName
        s"$typeParamName $baseName"
      case _ =>
        val typeParamNames = typeArguments map { _.qualifiedName } mkString ", "
        s"($typeParamNames) $baseName"

    }

  }

  def scalaTypeName: String = {
    val baseName = baseClass.scalaClassname
    // TODO This is a temporary hack
    if ((baseName endsWith "IndexedSeq") || (baseName endsWith "Set") || (baseName endsWith "Iterable")) {
      val typeParameter = typeArguments.headOption map { _.scalaTypeName } getOrElse "Any"
      s"$baseName[$typeParameter]"
    } else {
      baseName
    }
  }

  override def substitute(variable: TypeVariable, substitution: CgscriptType): ConcreteType = {
    ConcreteType(baseClass, typeArguments map { _.substitute(variable, substitution) })
  }

  // TODO Only works for parameterless types currently
  override def join(that: CgscriptType): CgscriptType = {
    that match {
      case thatConcreteType: ConcreteType =>
        val joinedTypes = reducedJoinedClasses(thatConcreteType) map { ConcreteType(_) }
        joinedTypes.size match {
          case 1 => joinedTypes.head
          case _ => IntersectionType(joinedTypes)
        }
      case thatIntersectionType: IntersectionType =>
        val joinedTypes = thatIntersectionType.components flatMap reducedJoinedClasses
        val mergedJoinedTypes = CgscriptType.reduceClasses(joinedTypes).toVector map { ConcreteType(_) }
        mergedJoinedTypes.size match {
          case 1 => mergedJoinedTypes.head
          case _ => IntersectionType(mergedJoinedTypes)
        }
      case _: TypeVariable => ConcreteType(CgscriptClass.Object)
    }
  }

  def reducedJoinedClasses(that: ConcreteType): Vector[CgscriptClass] = {
    val commonAncestors = baseClass.ancestors intersect that.baseClass.ancestors
    val reducedCommonAncestors = CgscriptType.reduceClasses(commonAncestors)
    reducedCommonAncestors.toVector
  }

  override def lookupMethod(id: Symbol, argTypes: Vector[CgscriptType]) = {
    baseClass.lookupMethod(id, argTypes, typeArguments)
  }

  override def <=(that: CgscriptType): Boolean = {

    that match {
      case thatConcreteType: ConcreteType =>
        baseClass <= thatConcreteType.baseClass && {
          // TODO This is not the right general logic for type variable inheritance
          typeArguments zip thatConcreteType.typeArguments forall { case (thisParam, thatParam) =>
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

  override def typeArguments = ???

  override def lookupMethod(id: Symbol, argTypes: Vector[CgscriptType]) = ???

  override def substitute(variable: TypeVariable, substitution: CgscriptType): IntersectionType = {
    IntersectionType(components map { _.substitute(variable, substitution) })
  }

  override def join(that: CgscriptType) = {

    that match {
      case thatConcreteType: ConcreteType => thatConcreteType join this
      case thatIntersectionType: IntersectionType =>
        val joinedTypes = components flatMap { thisComponent =>
          thatIntersectionType.components flatMap { thatComponent =>
            thisComponent reducedJoinedClasses thatComponent
          }
        }
        val mergedJoinedTypes = CgscriptType.reduceClasses(joinedTypes).toVector map { ConcreteType(_) }
        mergedJoinedTypes.size match {
          case 1 => mergedJoinedTypes.head
          case _ => IntersectionType(mergedJoinedTypes)
        }
    }

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

  def substituteAll(substitutions: Iterable[(TypeVariable, CgscriptType)]): CgscriptTypeList = {
    CgscriptTypeList(types map { _ substituteAll substitutions })
  }

  def <=(that: CgscriptTypeList): Boolean = {
    types zip that.types forall { case (thisType, thatType) => thisType <= thatType }
  }

}

package org.cgsuite.lang2

object CgscriptType {

  def apply(cls: CgscriptClass, typeParameters: Vector[CgscriptType] = Vector.empty): CgscriptType = {
    ConcreteType(cls, typeParameters)
  }

  def reduceClasses(classes: Set[CgscriptClass]) = {
    classes filterNot { cls =>
      classes exists { thatClass => thatClass != cls && thatClass <= cls }
    }
  }

  def reduceTypes(types: Set[ConcreteType]) = {
    types filterNot { typ =>
      types exists { thatType => thatType != typ && (thatType matches typ) }
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

  def matches(that: CgscriptType): Boolean

  def mentionedClasses: Iterable[CgscriptClass]

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

  override def mentionedClasses = Vector.empty

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

  override def matches(that: CgscriptType) = {

    that match {
      case concreteType: ConcreteType => concreteType.baseClass == CgscriptClass.Object
      case _: IntersectionType => false
      case _: TypeVariable => true
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
        val joinedTypes = reducedJoinedTypes(thatConcreteType)
        joinedTypes.size match {
          case 1 => joinedTypes.head
          case _ => IntersectionType(joinedTypes.toVector)
        }
      case thatIntersectionType: IntersectionType =>
        val joinedTypes = thatIntersectionType.components.toSet flatMap reducedJoinedTypes
        val mergedJoinedTypes = CgscriptType.reduceTypes(joinedTypes)
        mergedJoinedTypes.size match {
          case 1 => mergedJoinedTypes.head
          case _ => IntersectionType(mergedJoinedTypes.toVector)
        }
      case _: TypeVariable => ConcreteType(CgscriptClass.Object)
    }
  }

  def ancestorTypes: Vector[ConcreteType] = {
    baseClass.ancestors map { ancestor =>
      if (ancestor.typeParameters.isEmpty)
        ConcreteType(ancestor)
      else if (ancestor.typeParameters == baseClass.typeParameters)
        ConcreteType(ancestor, typeArguments)
      else
        sys.error("complex type parameter inheritance is not yet handled")
    }
  }

  def reducedJoinedTypes(that: ConcreteType): Set[ConcreteType] = {
    // This is currently pretty rudimentary in how it handles type parameters.
    // We assume trivial (non-mapped) type parameter inheritance.
    val commonAncestors = ancestorTypes.toSet intersect that.ancestorTypes.toSet
    val reducedCommonAncestors = CgscriptType.reduceTypes(commonAncestors)
    assert(reducedCommonAncestors.nonEmpty, "I'm surprised commonAncestors doesn't contain Object.")
    reducedCommonAncestors
  }

  def reducedJoinedClasses(that: ConcreteType): Set[CgscriptClass] = {
    val commonAncestors = baseClass.ancestors.toSet intersect that.baseClass.ancestors.toSet
    val reducedCommonAncestors = CgscriptType.reduceClasses(commonAncestors)
    reducedCommonAncestors
  }

  override def lookupMethod(id: Symbol, argTypes: Vector[CgscriptType]) = {
    baseClass.lookupMethod(id, argTypes, typeArguments)
  }

  override def matches(that: CgscriptType): Boolean = {

    that match {
      case thatConcreteType: ConcreteType =>
        baseClass <= thatConcreteType.baseClass && {
          // TODO This is not the right general logic for type variable inheritance
          typeArguments zip thatConcreteType.typeArguments forall { case (thisParam, thatParam) =>
            thisParam matches thatParam
          }
        }
      case _: TypeVariable => true
      case thatIntersectionType: IntersectionType =>
        thatIntersectionType.components forall { this matches _ }
    }
  }

  override def mentionedClasses = {
    baseClass +: (typeArguments flatMap { _.mentionedClasses })
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
        val joinedTypes = components.toSet flatMap { thisComponent: ConcreteType =>
          thatIntersectionType.components flatMap { thatComponent: ConcreteType =>
            thisComponent reducedJoinedTypes thatComponent
          }
        }
        val mergedJoinedTypes = CgscriptType.reduceTypes(joinedTypes).toVector
        mergedJoinedTypes.size match {
          case 1 => mergedJoinedTypes.head
          case _ => IntersectionType(mergedJoinedTypes)
        }
    }

  }

  override def matches(that: CgscriptType) = {

    that match {
      case thatConcreteType: ConcreteType =>
        components exists { _ matches thatConcreteType }
      case _: TypeVariable => true
      case thatIntersectionType: IntersectionType =>
        components exists { _ matches thatIntersectionType }
    }

  }

  override def mentionedClasses = {
    components flatMap { _.mentionedClasses }
  }

}

case class CgscriptTypeList(types: Vector[CgscriptType]) {

  def substituteAll(substitutions: Iterable[(TypeVariable, CgscriptType)]): CgscriptTypeList = {
    CgscriptTypeList(types map { _ substituteAll substitutions })
  }

  def <=(that: CgscriptTypeList): Boolean = {
    types zip that.types forall { case (thisType, thatType) => thisType matches thatType }
  }

}

package org.cgsuite.lang

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

  def substituteForUnboundTypeParameters(parameterTypes: Vector[CgscriptType], argumentTypes: Vector[CgscriptType]) = {
    val unboundTypeSubstitutions = parameterTypes zip argumentTypes flatMap { case (parameterType, argumentType) =>
      parameterType.unboundTypeSubstitutions(argumentType)
    }
    unboundTypeSubstitutions.groupBy { _._1 } mapValues { substitutions =>
      substitutions map { _._2 } reduce { _ join _ }
    }
    substituteAll(unboundTypeSubstitutions)
  }

  def allTypeVariables: Vector[TypeVariable]

  def unboundTypeSubstitutions(instanceType: CgscriptType): Map[TypeVariable, CgscriptType]

  def join(that: CgscriptType): CgscriptType

  def resolveMethod(id: Symbol, argTypes: Vector[CgscriptType]): Option[CgscriptClass#MethodProjection]

  def matches(that: CgscriptType): Boolean

  def mentionedClasses: Iterable[CgscriptClass]

  def substituteAll(substitutions: Iterable[(TypeVariable, CgscriptType)]): CgscriptType = {
    substitutions.foldLeft (this) { case (currentType, (variable, substitution)) =>
      currentType.substitute(variable, substitution)
    }
  }

}

case class TypeVariable(id: Symbol, isExpandable: Boolean = false) extends CgscriptType {

  override def qualifiedName = id.name

  override def scalaTypeName = s"__typevar_${id.name.drop(1)}"

  override def baseClass = CgscriptClass.Object

  override def typeArguments = Vector.empty

  override def resolveMethod(id: Symbol, argTypes: Vector[CgscriptType]) = sys.error("type variable cannot resolve to a class (this should never happen)")

  override def mentionedClasses = Vector.empty

  override def allTypeVariables = Vector(this)

  override def unboundTypeSubstitutions(instanceType: CgscriptType) = {
    Map(this -> instanceType)
  }

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

case class ConcreteType(
  baseClass: CgscriptClass,
  typeArguments: Vector[CgscriptType] = Vector.empty,
  nestProjection: Iterable[(TypeVariable, CgscriptType)] = Vector.empty
) extends CgscriptType {

  if (baseClass.isDeclaredPhase1 && (baseClass.typeParameters.isEmpty || !baseClass.typeParameters.head.isExpandable))
    assert(baseClass.typeParameters.length == typeArguments.length, (this, baseClass.typeParameters))

  def qualifiedName: String = {

    val baseName = baseClass.qualifiedName

    typeArguments.size match {
      case 0 => baseName
      case 1 =>
        val typeParamName = typeArguments.head.qualifiedName
        s"$baseName of $typeParamName"
      case _ =>
        val typeParamNames = typeArguments map { _.qualifiedName } mkString ", "
        s"$baseName of ($typeParamNames)"

    }

  }

  def scalaTypeName: String = {
    val baseName = baseClass.scalaTyperefName(nestProjection)
    // TODO This is a temporary hack
    if ((baseName endsWith "IndexedSeq") || (baseName endsWith "Set") || (baseName endsWith "Iterable")) {
      val typeArgument = typeArguments.headOption map { _.scalaTypeName } getOrElse "Any"
      s"$baseName[$typeArgument]"
    } else {
      val typeArgumentsBlock = {
        if (typeArguments.isEmpty)
          ""
        else {
          val typeArgumentsString = typeArguments map { _.scalaTypeName } mkString ", "
          s"[$typeArgumentsString]"
        }
      }
      s"$baseName$typeArgumentsBlock"
    }
  }

  override def allTypeVariables = (typeArguments flatMap { _.allTypeVariables }).distinct

  override def substitute(variable: TypeVariable, substitution: CgscriptType): ConcreteType = {
    ConcreteType(baseClass, typeArguments map { _.substitute(variable, substitution) })
  }

  override def substituteAll(substitutions: Iterable[(TypeVariable, CgscriptType)]): ConcreteType = {
    super.substituteAll(substitutions).asInstanceOf[ConcreteType]
  }

  override def unboundTypeSubstitutions(instanceType: CgscriptType) = {

    assert(instanceType.baseClass.ancestors contains baseClass, (this, instanceType))

    // TODO We need to properly manifest derived types as their generified ancestors
    assert(instanceType.typeArguments.length == typeArguments.length, (instanceType, typeArguments))
    val nestedSubstitutions = typeArguments zip instanceType.typeArguments flatMap { case (thisTypeArgument, thatTypeArgument) =>
      thisTypeArgument.unboundTypeSubstitutions(thatTypeArgument)
    }
    nestedSubstitutions.groupBy { _._1 } mapValues { substitutions =>
      substitutions map { _._2 } reduce { _ join _ }
    }

  }

  // TODO Currently only works for parameterless types
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
    baseClass.classInfo.ancestorTypes map { genericAncestorType =>
      genericAncestorType.substituteAll(baseClass.typeParameters zip typeArguments)
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

  override def resolveMethod(id: Symbol, argumentTypes: Vector[CgscriptType]): Option[CgscriptClass#MethodProjection] = {
    baseClass.lookupInstanceMethod(id, argumentTypes, Map.empty, Some(this))
  }

  override def matches(that: CgscriptType): Boolean = {

    that match {
      case thatConcreteType: ConcreteType =>
        baseClass <= thatConcreteType.baseClass && {
          // thatConcreteType.baseClass is an ancestor of baseClass. Now we need to "project" this type onto
          // the base class of thatConcreteType so that we can compare the type arguments.
          val genericProjection = baseClass.classInfo.ancestorTypes find { _.baseClass == thatConcreteType.baseClass } getOrElse {
            sys.error("this should never happen, since we already checked that baseClass <= thatConcreteType.baseClass")
          }
          val projection = genericProjection.substituteAll(baseClass.typeParameters zip this.typeArguments)
          projection.typeArguments zip thatConcreteType.typeArguments forall { case (thisArg, thatArg) =>
            // TODO We are currently assuming ALL parameters are covariant - not a great assumption!
            thisArg matches thatArg
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

  override def resolveMethod(id: Symbol, argTypes: Vector[CgscriptType]) = ???

  override def allTypeVariables = (components flatMap { _.allTypeVariables }).distinct

  override def substitute(variable: TypeVariable, substitution: CgscriptType): IntersectionType = {
    IntersectionType(components map { _.substitute(variable, substitution) })
  }

  override def unboundTypeSubstitutions(instanceType: CgscriptType) = ???

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

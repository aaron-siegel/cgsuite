package org.cgsuite.lang2

case class CgscriptType(baseClass: CgscriptClass, typeParameters: Vector[CgscriptType] = Vector.empty) {

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

  def <=(that: CgscriptType): Boolean = {
    baseClass <= that.baseClass
  }

}

case class CgscriptTypeList(types: Vector[CgscriptType]) {

  def <=(that: CgscriptTypeList): Boolean = {
    types zip that.types forall { case (thisType, thatType) => thisType <= thatType }
  }

}

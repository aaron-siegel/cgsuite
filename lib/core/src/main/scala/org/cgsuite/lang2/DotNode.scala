package org.cgsuite.lang2

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException

import scala.collection.mutable

case class DotNode(tree: Tree, antecedent: EvalNode, idNode: IdentifierNode) extends ClassSpecifierNode {

  override val children = Seq(antecedent, idNode)

  var elaboratedMember: Member = _
  var isElaboratedAsPackage: Boolean = _
  var antecedentType: Option[CgscriptType] = _

  def resolveAsPackage(domain: ElaborationDomain): Option[CgscriptPackage] = {

    val enclosingPackage: Option[CgscriptPackage] = {
      antecedent match {
        case identifierNode: IdentifierNode => identifierNode.resolveAsPackage(domain)
        case dotNode: DotNode => dotNode.resolveAsPackage(domain)
        case _ => None
      }
    }

    enclosingPackage flatMap { pkg =>
      pkg.lookupMember(idNode.id) match {
        case Some(_) => None        // Subpackage name is shadowed
        case None => pkg.lookupSubpackage(idNode.id)
      }
    }

  }

  def resolveAsPackageMember(domain: ElaborationDomain): Option[MemberResolution] = {

    val antecedentPackage = antecedent match {
      case identifierNode: IdentifierNode => identifierNode.resolveAsPackage(domain)
      case dotNode: DotNode => dotNode.resolveAsPackage(domain)
      case _ => None
    }

    antecedentPackage map { pkg =>
      antecedentType = None
      pkg.lookupConstantMember(idNode.id) orElse pkg.lookupClass(idNode.id) getOrElse {
        throw EvalException(s"No symbol `${idNode.id.name}` found in package `${pkg.qualifiedName}`")
      }
    }

  }

  def resolveAsObjectMember(domain: ElaborationDomain): MemberResolution = {

    val antecedentType = antecedent.ensureElaborated(domain)
    this.antecedentType = Some(antecedentType)

    antecedentType.baseClass match {

      case CgscriptClass.Class =>

        // This is a class object, so we need to look for a static resolution
        val cls = antecedentType.typeArguments.head.baseClass
        CgscriptClass.Class.resolveInstanceMember(idNode.id) orElse cls.resolveStaticMember(idNode.id) getOrElse {
          throw EvalException(s"No symbol `${idNode.id.name}` found in class `${cls.qualifiedName}`")
        }

      case _ =>
        antecedentType.baseClass.resolveInstanceMemberWithImplicits(idNode.id) getOrElse {
          throw EvalException(s"No symbol `${idNode.id.name}` found in class `${antecedentType.baseClass.qualifiedName}`")
        }

    }

  }

  def doResolutionForElaboration(domain: ElaborationDomain): MemberResolution = {
    resolveAsPackageMember(domain) match {
      case Some(member) =>
        isElaboratedAsPackage = true
        member
      case None =>
        isElaboratedAsPackage = false
        resolveAsObjectMember(domain)
    }
  }

  // TODO Code-consolidate with IdentifierNode? and/or member.ensureElaborated()
  def memberToElaboratedType(member: Member) = {

    member match {
      case method: CgscriptClass#Method =>
        val methodType = method.ensureElaborated()
        // TODO We need to do type-substitutions for vars and nested classes too.
        antecedentType match {
          case Some(typ) => methodType.substituteAll(method.declaringClass.typeParameters zip typ.typeArguments)
          case None => methodType
        }
      case variable: CgscriptClass#Var => variable.ensureElaborated()
      case cls: CgscriptClass if cls.isSingleton => CgscriptType(cls)
      case cls: CgscriptClass => CgscriptType(CgscriptClass.Class, Vector(CgscriptType(cls)))
    }

  }

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    elaboratedMember = {
      doResolutionForElaboration(domain) match {

        case methodGroup: CgscriptClass#MethodGroup =>
          methodGroup.autoinvokeMethod getOrElse {
            throw EvalException(s"Method `${methodGroup.qualifiedName}` requires arguments")
          }

        case member: Member => member

      }
    }

    memberToElaboratedType(elaboratedMember)

  }

  override def toScalaCode(context: CompileContext) = {

    elaboratedMember match {

      case cls: CgscriptClass => cls.scalaClassdefName

      case variable: CgscriptClass#Var =>
        if (isElaboratedAsPackage)
          variable.declaringClass.scalaClassdefName + "." + variable.id.name
        else {
          val objStr = antecedent.toScalaCode(context)
          s"($objStr).${variable.id.name}"
        }

      case method: CgscriptClass#Method =>
        if (isElaboratedAsPackage)
          method.declaringClass.scalaClassdefName + "." + method.scalaName
        else {
          val objStr = antecedent.toScalaCode(context)
          s"($objStr).${method.scalaName}"
        }

    }

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    if (!isElaboratedAsPackage) {
      antecedent.collectMentionedClasses(classes)
    }
    // There may be an implicit mention of a constants class, so we need to handle
    // that possibility separately.
    if (elaboratedMember.declaringClass != null) {
      classes += elaboratedMember.declaringClass
    }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${antecedent.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString}"
    else
      s"(${antecedent.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString})"
  }
}

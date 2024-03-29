package org.cgsuite.lang.node

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.lang.{CgscriptClass, CgscriptPackage, ClassResolutionScope, ElaborationDomain, Parameter}

object DeclarationNode {

  def apply(tree: Tree, pkg: CgscriptPackage): Iterable[Node] = {

    tree.getType match {

      case CLASS => Iterable(ClassDeclarationNode(tree, pkg))

      case DEF => Iterable(MethodDeclarationNode(tree, Some(pkg)))

      case STATIC => Iterable(InitializerBlockNode(tree, EvalNode(tree.head), Modifiers(static = Some(tree.token))))

      case CLASS_VAR =>
        val (modifiers, nodes) = ClassVarNode(tree)
        nodes map { VarDeclarationNode(tree, _, modifiers) }

      case ENUM_ELEMENT => Iterable(EnumElementNode(tree))

      case _ => Iterable(InitializerBlockNode(tree, EvalNode(tree), Modifiers.none))

    }

  }

}

object ClassDeclarationNode {

  def apply(tree: Tree, pkg: CgscriptPackage): ClassDeclarationNode = {

    val isEnum = tree.getType == ENUM
    val modifiers = Modifiers(tree.head, MUTABLE, SINGLETON, SYSTEM)
    val id = IdentifierNode(tree.children(1))
    val extendsClause = tree.children find { _.getType == EXTENDS } match {
      case Some(t) => t.children map { TypeSpecifierNode(_) }
      case None => Vector.empty
    }
    val constructorParams = tree.children find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_, Some(pkg)) }

    val declarations = tree.children filter { _.getType == DECLARATIONS } flatMap { _.children flatMap { DeclarationNode(_, pkg) } }
    val nestedClassDeclarations = declarations collect {
      case x: ClassDeclarationNode => x
    }
    val methodDeclarations = declarations collect {
      case x: MethodDeclarationNode => x
    }
    val staticInitializers = declarations collect {
      case x: InitializerNode if x.modifiers.hasStatic => x
    }
    val ordinaryInitializers = declarations collect {
      case x: InitializerNode if !x.modifiers.hasStatic => x
    }
    val enumElements = declarations collect {
      case x: EnumElementNode => x
    }

    ClassDeclarationNode(
      tree,
      id,
      isEnum,
      modifiers,
      extendsClause,
      constructorParams,
      nestedClassDeclarations,
      methodDeclarations,
      staticInitializers,
      ordinaryInitializers,
      enumElements
    )

  }

}

case class ClassDeclarationNode(
  tree: Tree,
  idNode: IdentifierNode,
  isEnum: Boolean,
  modifiers: Modifiers,
  extendsClause: Vector[TypeSpecifierNode],
  classParameterNodes: Option[ParametersNode],
  nestedClassDeclarations: Vector[ClassDeclarationNode],
  methodDeclarations: Vector[MethodDeclarationNode],
  staticInitializers: Vector[InitializerNode],
  ordinaryInitializers: Vector[InitializerNode],
  enumElements: Vector[EnumElementNode]
  ) extends MemberDeclarationNode {

  val children = Seq(idNode) ++ extendsClause ++ classParameterNodes ++
    nestedClassDeclarations ++ methodDeclarations ++ staticInitializers ++ ordinaryInitializers

}

sealed trait MemberDeclarationNode extends Node {
  def idNode: IdentifierNode
}

object ParametersNode {

  def apply(tree: Tree, pkg: Option[CgscriptPackage]): ParametersNode = {
    assert(tree.getType == METHOD_PARAMETER_LIST)
    val parameters = tree.children map { t =>
      assert(t.getType == METHOD_PARAMETER)
      ParameterNode(
        t,
        pkg,
        IdentifierNode(t.head),
        t.children find { _.getType == AS } map { u => TypeSpecifierNode(u.head) },
        t.children find { _.getType == QUESTION } map { u => EvalNode(u.head) },
        t.children exists { _.getType == DOTDOTDOT }
      )
    }
    val invalidExpandedParameter = parameters.dropRight(1).find { _.isExpandable }
    invalidExpandedParameter foreach { paramNode =>
      throw EvalException(
        s"Invalid expansion for parameter `${paramNode.idNode.id.name}`: must be in last position",
        paramNode.tree
      )
    }
    ParametersNode(tree, pkg, parameters)
  }

}

case class ParametersNode(tree: Tree, pkg: Option[CgscriptPackage], parameterNodes: Vector[ParameterNode]) extends Node {

  override val children = parameterNodes

  def toParameters: Vector[Parameter] = parameterNodes map { _.toParameter }

}

case class ParameterNode(
  tree: Tree,
  scope: Option[ClassResolutionScope],
  idNode: IdentifierNode,
  classId: Option[TypeSpecifierNode],
  defaultValue: Option[EvalNode],
  isExpandable: Boolean
  ) extends MemberDeclarationNode {

  override val children = Seq(idNode) ++ classId ++ defaultValue

  def toParameter: Parameter = {
    val ttype = classId match {
      case None => CgscriptClass.Object
      case Some(typeSpecifierNode) => typeSpecifierNode.resolveToType(scope) getOrElse {
        throw EvalException(s"Unknown class in parameter declaration: `${typeSpecifierNode.toNodeString}`", typeSpecifierNode.tree)
      }
    }
    Parameter(idNode, ttype, defaultValue, isExpandable)
  }

}

object ClassVarNode {

  def apply(tree: Tree): (Modifiers, Seq[AssignToNode]) = {
    assert(tree.getType == CLASS_VAR)
    val modifiers = Modifiers(tree.head, EXTERNAL, MUTABLE, STATIC, PRIVATE)
    val nodes = tree.children.tail map { t =>
      t.getType match {
        case DECL_ID => AssignToNode(t, IdentifierNode(t), ConstantNode(null, null), AssignmentDeclType.ClassVarDecl)
        case ASSIGN => AssignToNode(t, IdentifierNode(t.head), EvalNode(t.children(1)), AssignmentDeclType.ClassVarDecl)
      }
    }
    (modifiers, nodes)
  }

}

object EnumElementNode {

  def apply(tree: Tree): EnumElementNode = {
    assert(tree.getType == ENUM_ELEMENT)
    val modifiers = Modifiers(tree.head, EXTERNAL)
    EnumElementNode(tree, IdentifierNode(tree.children(1)), modifiers)
  }

}

case class EnumElementNode(tree: Tree, idNode: IdentifierNode, modifiers: Modifiers)
  extends MemberDeclarationNode {

  val children = Seq(idNode)

}

object MethodDeclarationNode {

  def apply(tree: Tree, pkg: Option[CgscriptPackage]): MethodDeclarationNode = {
    MethodDeclarationNode(
      tree,
      IdentifierNode(tree.children(1)),
      Modifiers(tree.head, EXTERNAL, OVERRIDE, STATIC, PRIVATE),
      tree.children find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_, pkg) },
      tree.children find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
    )
  }

}

case class MethodDeclarationNode(
  tree: Tree,
  idNode: IdentifierNode,
  modifiers: Modifiers,
  parameters: Option[ParametersNode],
  body: Option[StatementSequenceNode]
  ) extends MemberDeclarationNode {

  val children = Seq(idNode) ++ parameters ++ body

}

sealed trait InitializerNode extends Node {
  def tree: Tree
  def body: EvalNode
  def modifiers: Modifiers
  override lazy val children = Seq(body)
}

case class InitializerBlockNode(tree: Tree, body: EvalNode, modifiers: Modifiers) extends InitializerNode

case class VarDeclarationNode(tree: Tree, body: AssignToNode, modifiers: Modifiers)
  extends InitializerNode with MemberDeclarationNode {
  override def idNode: IdentifierNode = body.idNode
}

object Modifiers {

  val none: Modifiers = Modifiers()

  def apply(tree: Tree, allowed: Int*): Modifiers = {
    val tokens = tree.children map { _.token }
    tokens foreach { token =>
      if (!(allowed contains token.getType))
        throw EvalException(s"Modifier `${token.getText}` not permitted here", token = Some(token))
    }
    Modifiers(
      external = tokens find { _.getType == EXTERNAL },
      mutable = tokens find { _.getType == MUTABLE },
      `override` = tokens find { _.getType == OVERRIDE },
      singleton = tokens find { _.getType == SINGLETON },
      static = tokens find { _.getType == STATIC },
      system = tokens find { _.getType == SYSTEM },
      `private` = tokens find { _.getType == PRIVATE }
    )
  }

}

case class Modifiers(
  external: Option[Token] = None,
  mutable: Option[Token] = None,
  `override`: Option[Token] = None,
  singleton: Option[Token] = None,
  static: Option[Token] = None,
  system: Option[Token] = None,
  `private`: Option[Token] = None
  ) {

  val hasExternal = external.isDefined
  val hasMutable = mutable.isDefined
  val hasOverride = `override`.isDefined
  val hasSingleton = singleton.isDefined
  val hasStatic = static.isDefined
  val hasSystem = system.isDefined
  val hasPrivate = `private`.isDefined

  def allModifiers: Iterable[Token] = {
    external ++ mutable ++ `override` ++ singleton ++ static ++ system ++ `private`
  }

}

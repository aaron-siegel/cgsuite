package org.cgsuite.lang2

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang2.Node.treeToRichTree
import org.cgsuite.lang.parser.CgsuiteLexer._

object DeclarationNode {

  def apply(tree: Tree, pkg: CgscriptPackage): Iterable[Node] = {

    tree.getType match {

      case CLASS => Iterable(ClassDeclarationNode(tree, pkg))

      case DEF =>
        Iterable(MethodDeclarationNode(
          tree,
          IdentifierNode(tree.children(1)),
          Modifiers(tree.head, EXTERNAL, OVERRIDE, STATIC),
          tree.children find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_, Some(pkg)) },
          tree.children find { _.getType == AS } map { asTree => TypeSpecifierNode(asTree.children.head) },
          tree.children find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
        ))

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
    val id = IdentifierNode(tree.children.find { _.getType == DECL_ID }.get)
    val typeParameters = tree.children.find { _.getType == OF } match {
      case Some(t) => t.children map { TypeVariableNode(_) }
      case _ => Vector.empty
    }
    val extendsClause = tree.children find { _.getType == EXTENDS } match {
      case Some(t) => t.children map { EvalNode(_) }
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
    val initializers = declarations collect {
      case x: InitializerNode => x
    }
    val enumElements = declarations collect {
      case x: EnumElementNode => x
    }
    ClassDeclarationNode(
      tree,
      id,
      typeParameters,
      isEnum,
      modifiers,
      extendsClause,
      constructorParams,
      nestedClassDeclarations,
      methodDeclarations,
      initializers,
      enumElements
    )
  }
}

case class ClassDeclarationNode(
  tree: Tree,
  idNode: IdentifierNode,
  typeParameters: Vector[TypeVariableNode],
  isEnum: Boolean,
  modifiers: Modifiers,
  extendsClause: Vector[Node],
  constructorParams: Option[ParametersNode],
  nestedClassDeclarations: Vector[ClassDeclarationNode],
  methodDeclarations: Vector[MethodDeclarationNode],
  initializers: Vector[InitializerNode],
  enumElements: Vector[EnumElementNode]
  ) extends MemberDeclarationNode {

  val children = Vector(idNode) ++ extendsClause ++ constructorParams ++
    nestedClassDeclarations ++ methodDeclarations ++ initializers

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
        IdentifierNode(t.head),
        t.children find { _.getType == AS } map { u => TypeSpecifierNode(u.head) },
        t.children find { _.getType == QUESTION } map { u => EvalNode(u.head) },
        t.children exists { _.getType == DOTDOTDOT }
      )
    }
    val invalidExpandedParameter = parameters.dropRight(1).find { _.isExpandable }
    invalidExpandedParameter foreach { paramNode =>
      throw EvalException(
        s"Invalid expansion for parameter `${paramNode.id.id.name}`: must be in last position",
        paramNode.tree
      )
    }
    ParametersNode(tree, pkg, parameters)
  }
}

case class ParametersNode(tree: Tree, pkg: Option[CgscriptPackage], parameterNodes: Vector[ParameterNode]) extends Node {

  override val children = parameterNodes

  def toParameters(domain: ElaborationDomain): Vector[Parameter] = {

    parameterNodes.map { n =>
      val ttype = n.typeSpecifier match {
        case None => CgscriptType(CgscriptClass.Object)
        case Some(typeSpecNode) => typeSpecNode.toType(domain)
          /*
        case Some(idNode) => pkg flatMap { _ lookupClass idNode.id } orElse (CgscriptPackage lookupClass idNode.id) getOrElse {
          throw EvalException(s"Unknown class in parameter declaration: `${idNode.id.name}`", idNode.tree)
        }*/
      }
      Parameter(n.id, ttype, n.defaultValue, n.isExpandable)
    }

  }

}

case class ParameterNode(
  tree: Tree,
  id: IdentifierNode,
  typeSpecifier: Option[TypeSpecifierNode],
  defaultValue: Option[EvalNode],
  isExpandable: Boolean
  ) extends Node {
  override val children = Seq(id) ++ typeSpecifier ++ defaultValue
}

object ClassVarNode {
  def apply(tree: Tree): (Modifiers, Seq[AssignToNode]) = {
    assert(tree.getType == CLASS_VAR)
    val modifiers = Modifiers(tree.head, EXTERNAL, MUTABLE, STATIC)
    val nodes = tree.children.tail map { t =>
      t.getType match {
        case DECL_ID => AssignToNode(t, IdentifierNode(t), NullNode(null), AssignmentDeclType.ClassVarDecl)
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

case class MethodDeclarationNode(
  tree: Tree,
  idNode: IdentifierNode,
  modifiers: Modifiers,
  parameters: Option[ParametersNode],
  returnType: Option[TypeSpecifierNode],
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
      system = tokens find { _.getType == SYSTEM }
    )
  }
}

case class Modifiers(
  external: Option[Token] = None,
  mutable: Option[Token] = None,
  `override`: Option[Token] = None,
  singleton: Option[Token] = None,
  static: Option[Token] = None,
  system: Option[Token] = None
  ) {

  val hasExternal = external.isDefined
  val hasMutable = mutable.isDefined
  val hasOverride = `override`.isDefined
  val hasSingleton = singleton.isDefined
  val hasStatic = static.isDefined
  val hasSystem = system.isDefined

  def allModifiers = external ++ mutable ++ `override` ++ singleton ++ static ++ system

}

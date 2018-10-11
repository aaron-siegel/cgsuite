package org.cgsuite.lang

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.Node.treeToRichTree
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
          tree.children find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
        ))

      case STATIC => Iterable(InitializerNode(tree, EvalNode(tree.head), isVarDeclaration = false, Modifiers(static = Some(tree.token))))

      case CLASS_VAR =>
        val (modifiers, nodes) = ClassVarNode(tree)
        nodes.map { node =>
          InitializerNode(
            tree,
            node,
            isVarDeclaration = true,
            modifiers = modifiers
          )
        }

      case ENUM_ELEMENT => Iterable(EnumElementNode(tree))

      case _ => Iterable(InitializerNode(tree, EvalNode(tree), isVarDeclaration = false, Modifiers.none))

    }

  }

}

object ClassDeclarationNode {
  def apply(tree: Tree, pkg: CgscriptPackage): ClassDeclarationNode = {
    val isEnum = tree.getType == ENUM
    val modifiers = Modifiers(tree.head, MUTABLE, SINGLETON, SYSTEM)
    val id = IdentifierNode(tree.children(1))
    val extendsClause = tree.children find { _.getType == EXTENDS } match {
      case Some(t) => t.children map { EvalNode(_) }
      case None => Seq.empty
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
  extendsClause: Seq[Node],
  constructorParams: Option[ParametersNode],
  nestedClassDeclarations: Seq[ClassDeclarationNode],
  methodDeclarations: Seq[MethodDeclarationNode],
  staticInitializers: Seq[InitializerNode],
  ordinaryInitializers: Seq[InitializerNode],
  enumElements: Seq[EnumElementNode]
  ) extends MemberDeclarationNode {

  val children = Seq(idNode) ++ extendsClause ++ constructorParams ++
    nestedClassDeclarations ++ methodDeclarations ++ staticInitializers ++ ordinaryInitializers

}

trait MemberDeclarationNode extends Node {
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
        t.children find { _.getType == AS } map { u => IdentifierNode(u.head) },
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

case class ParametersNode(tree: Tree, pkg: Option[CgscriptPackage], parameterNodes: Seq[ParameterNode]) extends Node {

  override val children = parameterNodes

  def toParameters: Seq[Parameter] = {

    parameterNodes.map { n =>
      val ttype = n.classId match {
        case None => CgscriptClass.Object
        case Some(idNode) => pkg flatMap { _ lookupClass idNode.id } orElse (CgscriptPackage lookupClass idNode.id) getOrElse {
          throw EvalException(s"Unknown class in parameter declaration: `${idNode.id.name}`", idNode.tree)
        }
      }
      Parameter(n.id, ttype, n.defaultValue, n.isExpandable)
    }

  }

}

case class ParameterNode(
  tree: Tree,
  id: IdentifierNode,
  classId: Option[IdentifierNode],
  defaultValue: Option[EvalNode],
  isExpandable: Boolean
  ) extends Node {
  override val children = Seq(id) ++ classId ++ defaultValue
}

object ClassVarNode {
  def apply(tree: Tree): (Modifiers, Seq[AssignToNode]) = {
    assert(tree.getType == CLASS_VAR)
    val modifiers = Modifiers(tree.head, EXTERNAL, MUTABLE, STATIC)
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

case class EnumElementNode(tree: Tree, id: IdentifierNode, modifiers: Modifiers) extends Node {
  val children = Seq(id)
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

case class InitializerNode(tree: Tree, body: EvalNode, isVarDeclaration: Boolean, modifiers: Modifiers) extends Node {
  val children = Seq(body)
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

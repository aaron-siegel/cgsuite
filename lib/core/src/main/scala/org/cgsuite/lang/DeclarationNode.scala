package org.cgsuite.lang

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.lang.Node.treeToRichTree
import org.cgsuite.lang.parser.CgsuiteLexer._

object DeclarationNode {

  def apply(tree: Tree): Iterable[Node] = {

    tree.getType match {

      case CLASS => Iterable(ClassDeclarationNode(tree))

      case DEF =>
        Iterable(MethodDeclarationNode(
          tree,
          IdentifierNode(tree.getChild(1)),
          ModifierNodes(tree.getChild(0)),
          tree.children find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) },
          tree.children find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
        ))

      case STATIC => Iterable(InitializerNode(tree, EvalNode(tree.getChild(0)), isVarDeclaration = false, isStatic = true, isExternal = false))

      case CLASS_VAR =>
        val (modifiers, nodes) = ClassVarNode(tree)
        nodes.map { node =>
          InitializerNode(
            tree,
            node,
            isVarDeclaration = true,
            isStatic = tree.getType == ENUM_ELEMENT || modifiers.exists { _.modifier == Modifier.Static },
            isExternal = modifiers.exists { _.modifier == Modifier.External }
          )
        }

      case ENUM_ELEMENT => Iterable(EnumElementNode(tree))

      case _ => Iterable(InitializerNode(tree, EvalNode(tree), isVarDeclaration = false, isStatic = false, isExternal = false))

    }

  }

}

object ClassDeclarationNode {
  def apply(tree: Tree): ClassDeclarationNode = {
    val isEnum = tree.getType == ENUM
    val modifiers = ModifierNodes(tree.getChild(0))
    val id = IdentifierNode(tree.getChild(1))
    val extendsClause = tree.children.find { _.getType == EXTENDS } match {
      case Some(t) => t.children.map { EvalNode(_) }
      case None => Seq.empty
    }
    val constructorParams = tree.children.find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) }
    val declarations = tree.children.filter { _.getType == DECLARATIONS }.flatMap { _.children.flatMap { DeclarationNode(_) } }
    val nestedClassDeclarations = declarations collect {
      case x: ClassDeclarationNode => x
    }
    val methodDeclarations = declarations collect {
      case x: MethodDeclarationNode => x
    }
    val staticInitializers = declarations collect {
      case x: InitializerNode if x.isStatic => x
    }
    val ordinaryInitializers = declarations collect {
      case x: InitializerNode if !x.isStatic => x
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
  id: IdentifierNode,
  isEnum: Boolean,
  modifiers: Seq[ModifierNode],
  extendsClause: Seq[Node],
  constructorParams: Option[ParametersNode],
  nestedClassDeclarations: Seq[ClassDeclarationNode],
  methodDeclarations: Seq[MethodDeclarationNode],
  staticInitializers: Seq[InitializerNode],
  ordinaryInitializers: Seq[InitializerNode],
  enumElements: Seq[EnumElementNode]
  ) extends Node {

  val children = (id +: modifiers) ++ extendsClause ++ constructorParams.toSeq ++
    nestedClassDeclarations ++ methodDeclarations ++ staticInitializers ++ ordinaryInitializers
  def isMutable = modifiers.exists { _.modifier == Modifier.Mutable }
  def isSystem = modifiers.exists { _.modifier == Modifier.System }
  def isStatic = modifiers.exists { _.modifier == Modifier.Static }

}

object ParametersNode {
  def apply(tree: Tree): ParametersNode = {
    assert(tree.getType == METHOD_PARAMETER_LIST)
    val parameters = tree.children.map { t =>
      assert(t.getType == METHOD_PARAMETER)
      ParameterNode(
        t,
        IdentifierNode(t.getChild(0)),
        t.children.find { _.getType == AS } map { u => IdentifierNode(u.getChild(0)) },
        t.children.find { _.getType == QUESTION } map { u => EvalNode(u.getChild(0)) },
        t.children.exists { _.getType == DOTDOTDOT }
      )
    }
    ParametersNode(tree, parameters)
  }
}

case class ParametersNode(tree: Tree, parameters: Seq[ParameterNode]) extends Node {
  override val children = parameters
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
  def apply(tree: Tree): (Seq[ModifierNode], Seq[AssignToNode]) = {
    assert(tree.getType == CLASS_VAR)
    val modifiers = ModifierNodes(tree.getChild(0))
    val nodes = tree.children.tail map { t =>
      t.getType match {
        case IDENTIFIER => AssignToNode(t, IdentifierNode(t), ConstantNode(null, Nil), isVarDeclaration = false)
        case ASSIGN => AssignToNode(t, IdentifierNode(t.getChild(0)), EvalNode(t.getChild(1)), isVarDeclaration = false)
      }
    }
    (modifiers, nodes)
  }
}

object EnumElementNode {
  def apply(tree: Tree): EnumElementNode = {
    assert(tree.getType == ENUM_ELEMENT)
    val modifiers = ModifierNodes(tree.getChild(0))
    EnumElementNode(tree, IdentifierNode(tree.getChild(1)), modifiers.exists { _.modifier == Modifier.External })
  }
}

case class EnumElementNode(tree: Tree, id: IdentifierNode, isExternal: Boolean) extends Node {
  val children = Seq(id)
}

case class MethodDeclarationNode(
  tree: Tree,
  idNode: IdentifierNode,
  modifiers: Seq[ModifierNode],
  parameters: Option[ParametersNode],
  body: Option[StatementSequenceNode]
  ) extends Node {

  val children = (idNode +: modifiers) ++ parameters ++ body
  val isExternal = modifiers.exists { _.modifier == Modifier.External }
  val isOverride = modifiers.exists { _.modifier == Modifier.Override }
  val isStatic = modifiers.exists { _.modifier == Modifier.Static }

}

case class InitializerNode(tree: Tree, body: EvalNode, isVarDeclaration: Boolean, isStatic: Boolean, isExternal: Boolean) extends Node {
  val children = Seq(body)
}

object ModifierNodes {
  def apply(tree: Tree): Seq[ModifierNode] = {
    assert(tree.getType == MODIFIERS)
    tree.children map { t => ModifierNode(t, Modifier.fromToken(t.token)) }
  }
}

case class ModifierNode(tree: Tree, modifier: Modifier.Value) extends Node {
  val children = Seq.empty
}

object Modifier extends Enumeration {
  type Modifier = Value
  val External, Mutable, Override, Singleton, Static, System = Value
  def fromToken(token: Token) = token.getType match {
    case EXTERNAL => External
    case MUTABLE => Mutable
    case OVERRIDE => Override
    case SINGLETON => Singleton
    case STATIC => Static
    case SYSTEM => System
  }
}

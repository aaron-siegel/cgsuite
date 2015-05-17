package org.cgsuite.lang

import org.antlr.runtime.tree.Tree
import org.cgsuite.lang.Node.treeToRichTree
import org.cgsuite.lang.parser.CgsuiteLexer._

object DeclarationNode {

  def apply(tree: Tree): Iterable[Node] = {

    tree.getType match {

      case DEF =>
        Iterable(MethodDeclarationNode(
          tree,
          IdentifierNode(tree.getChild(1)),
          ModifierNodes(tree.getChild(0)),
          tree.children.find { _.getType == METHOD_PARAMETER_LIST } map { ParametersNode(_) },
          tree.children.find { _.getType == STATEMENT_SEQUENCE } map { StatementSequenceNode(_) }
        ))

      case STATIC => Iterable(InitializerNode(tree, EvalNode(tree.getChild(0)), isStatic = true, isExternal = false))

      case VAR | ENUM_ELEMENT =>
        val (modifiers, nodes) = VarNode(tree)
        nodes.map { node =>
          InitializerNode(
            tree,
            node,
            isStatic = tree.getType == ENUM_ELEMENT || modifiers.exists { _.modifier == Modifier.Static },
            isExternal = modifiers.exists { _.modifier == Modifier.External }
          )
        }

      case _ => Iterable(InitializerNode(tree, EvalNode(tree), isStatic = false, isExternal = false))

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
    val methodDeclarations = declarations collect {
      case x: MethodDeclarationNode => x
    }
    val staticInitializers = declarations collect {
      case x: InitializerNode if x.isStatic => x
    }
    val ordinaryInitializers = declarations collect {
      case x: InitializerNode if !x.isStatic => x
    }
    ClassDeclarationNode(
      tree,
      id,
      isEnum,
      modifiers,
      extendsClause,
      constructorParams,
      methodDeclarations,
      staticInitializers,
      ordinaryInitializers
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
  methodDeclarations: Seq[MethodDeclarationNode],
  staticInitializers: Seq[InitializerNode],
  ordinaryInitializers: Seq[InitializerNode]
  ) extends Node {

  val children = (id +: modifiers) ++ extendsClause ++ constructorParams.toSeq ++
    methodDeclarations ++ staticInitializers ++ ordinaryInitializers
  def isMutable = modifiers.exists { _.modifier == Modifier.Mutable }
  def isSystem = modifiers.exists { _.modifier == Modifier.System }

}

object ParametersNode {
  def apply(tree: Tree): ParametersNode = {
    assert(tree.getType == METHOD_PARAMETER_LIST)
    val parameters = tree.children.map { t =>
      assert(t.getType == METHOD_PARAMETER)
      ParameterNode(
        t,
        IdentifierNode(t.getChild(0)),
        IdentifierNode(t.getChild(1)),
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
  classId: IdentifierNode,
  defaultValue: Option[EvalNode],
  isExpandable: Boolean
  ) extends Node {
  override val children = Seq(id, classId) ++ defaultValue
}

object VarNode {
  def apply(tree: Tree): (Seq[ModifierNode], Seq[AssignToNode]) = {
    assert(tree.getType == VAR || tree.getType == ENUM_ELEMENT)
    val modifiers = tree.getChild(0).children.map { t => ModifierNode(t, Modifier.fromString(t.getText)) }
    val nodes = tree.children.tail.map { t =>
      t.getType match {
        case IDENTIFIER => AssignToNode(t, IdentifierNode(t), ConstantNode(null, Nil), isVarDeclaration = true)
        case ASSIGN => AssignToNode(t, IdentifierNode(t.getChild(0)), EvalNode(t.getChild(1)), isVarDeclaration = true)
      }
    }
    (modifiers, nodes)
  }
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

case class InitializerNode(tree: Tree, body: EvalNode, isStatic: Boolean, isExternal: Boolean) extends Node {
  val children = Seq(body)
}

object ModifierNodes {
  def apply(tree: Tree): Seq[ModifierNode] = {
    assert(tree.getType == MODIFIERS)
    tree.children.map { t => ModifierNode(t, Modifier.fromString(t.getText)) }
  }
}

case class EnumElementNode(tree: Tree, id: IdentifierNode, modifiers: Seq[ModifierNode]) extends Node {
  val children = id +: modifiers
}

case class ModifierNode(tree: Tree, modifier: Modifier.Value) extends Node {
  val children = Seq.empty
}

object Modifier extends Enumeration {
  type Modifier = Value
  val External, Mutable, Override, Static, System = Value
  def fromString(str: String) = str match {
    case "external" => External
    case "mutable" => Mutable
    case "override" => Override
    case "static" => Static
    case "system" => System
  }
}

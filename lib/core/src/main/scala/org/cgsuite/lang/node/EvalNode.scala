package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.apache.commons.text.StringEscapeUtils
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.Ops._
import org.cgsuite.lang._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree

import scala.collection.mutable

object EvalNode {

  def apply(tree: Tree): EvalNode = {

    tree.getType match {

      // Constants

      case TRUE => BooleanNode(tree, constantValue = true)
      case FALSE => BooleanNode(tree, constantValue = false)
      case INTEGER => IntegerNode(tree, Integer.parseInteger(tree.getText))
      case STRING => StringNode(tree, tree.getText.drop(1).dropRight(1).replaceAll("\\\\\"", "\""))

      // This

      case THIS => ThisNode(tree)

      // as

      case AS => AsNode(tree)

      // Identifier

      case IDENTIFIER => IdentifierNode(tree)

      // Ops

      case UNARY_PLUS => UnOpNode(tree, Pos)
      case UNARY_MINUS => UnOpNode(tree, Neg)
      case PLUSMINUS => UnOpNode(tree, PlusMinus)

      case PLUS => BinOpNode(tree, Plus)
      case MINUS => BinOpNode(tree, Minus)
      case COLON => BinOpNode(tree, OrdinalPlus)
      case AST => BinOpNode(tree, Times)
      case FSLASH => BinOpNode(tree, Div)
      case PERCENT => BinOpNode(tree, Mod)
      case EXP => BinOpNode(tree, Exp)

      case NOT => UnOpNode(tree, Not)
      case AND => BinOpNode(tree, And)
      case OR => BinOpNode(tree, Or)

      case IS => BinOpNode(tree, Is)

      case INFIX_OP => InfixOpNode(tree)

      // Relations

      case EQUALS => BinOpNode(tree, Equals)
      case NEQ => BinOpNode(tree, Neq)
      case LEQ => BinOpNode(tree, Leq)
      case GEQ => BinOpNode(tree, Geq)
      case LT => BinOpNode(tree, Lt)
      case GT => BinOpNode(tree, Gt)
      case CONFUSED => BinOpNode(tree, Confused)
      case LCONFUSED => BinOpNode(tree, LConfused)
      case GCONFUSED => BinOpNode(tree, GConfused)
      case REFEQUALS => BinOpNode(tree, RefEquals)
      case REFNEQ => BinOpNode(tree, RefNeq)

      // * and ^

      case UNARY_AST => nimber(tree)
      case CARET | MULTI_CARET | VEE | MULTI_VEE => upMultiple(tree)

      // Collection constructors

      case COORDINATES => CoordinatesNode(tree)
      case EXPLICIT_LIST => ListNode(tree)
      case EXPLICIT_SET => SetNode(tree)
      case EXPLICIT_MAP => MapNode(tree)
      case DOTDOT => BinOpNode(tree, Range)

      // Game construction

      case SLASHES =>
        if (tree.head.getType == EXPRESSION_LIST && tree.head.children.exists { _.getType == PASS } ||
            tree.children(1).getType == EXPRESSION_LIST && tree.children(1).children.exists { _.getType == PASS })
          LoopyGameSpecNode(tree)
        else
          GameSpecNode(tree, gameOptions(tree.head), gameOptions(tree.children(1)), forceExplicit = false)
      case SQUOTE => GameSpecNode(tree, gameOptions(tree.head.head), gameOptions(tree.head.children(1)), forceExplicit = true)
      case NODE_LABEL => LoopyGameSpecNode(tree)
      case AMPERSAND => BinOpNode(tree, MakeSides)
      case PASS => throw EvalException("Unexpected `pass`.", tree)

      // Control flow

      case IF | ELSEIF => IfNode(
        tree,
        EvalNode(tree.head),
        StatementSequenceNode(tree.children(1)),
        if (tree.children.size > 2) Some(EvalNode(tree.children(2))) else None
      )
      case ERROR => ErrorNode(tree, EvalNode(tree.head))
      case DO | YIELD | LISTOF | MAPOF | SETOF | TABLEOF | SUMOF => LoopNode(tree)

      // Procedures

      case RARROW => ProcedureNode(tree, None)

      // Map entry

      case BIGRARROW => MapPairNode(tree)

      // Resolvers

      case DOT => DotNode(tree, EvalNode(tree.head), IdentifierNode(tree.children(1)))
      case FUNCTION_CALL => FunctionCallNode(tree)
      case ARRAY_REFERENCE => BinOpNode(tree, ArrayReference, EvalNode(tree.head), EvalNode(tree.children(1).head))

      // Assignment

      case ASSIGN =>
        if (tree.head.getType != IDENTIFIER)
          throw EvalException("Syntax error.", tree)
        AssignToNode(tree, IdentifierNode(tree.head), EvalNode(tree.children(1)), AssignmentDeclType.Ordinary)
      case VAR => VarNode(tree)

      // Statement sequence

      case STATEMENT_SEQUENCE => StatementSequenceNode(tree)

    }

  }

  private def gameOptions(tree: Tree): Vector[EvalNode] = {
    tree.getType match {
      case SLASHES => Vector(EvalNode(tree))
      case EXPRESSION_LIST => tree.children map { EvalNode(_) }
    }
  }

  private def nimber(tree: Tree): EvalNode = {
    if (tree.children.isEmpty) {
      StarNode(tree)
    } else {
      UnOpNode(tree, MakeNimber)
    }
  }

  private def upMultiple(tree: Tree): EvalNode = {
    val (upMultipleExprTree, nimberTree) = tree.children.size match {
      case 0 => (None, None)
      case 1 => tree.head.getType match {
        case UNARY_AST => (None, Some(tree.head))
        case _ => (Some(tree.head), None)
      }
      case _ => (Some(tree.head), Some(tree.children(1)))
    }
    val upMultipleExprNode = upMultipleExprTree map { EvalNode(_) } getOrElse { IntegerNode(tree, Integer(tree.getText.length)) }
    val nimberNodeOpt = nimberTree map nimber
    val upMultipleNode = tree.getType match {
      case CARET | MULTI_CARET => UnOpNode(tree, MakeUpMultiple, upMultipleExprNode)
      case VEE | MULTI_VEE => UnOpNode(tree, MakeDownMultiple, upMultipleExprNode)
    }
    nimberNodeOpt match {
      case None => upMultipleNode
      case Some(node) => BinOpNode(tree, Plus, upMultipleNode, node)
    }
  }

}

trait EvalNode extends Node {

  def children: Iterable[EvalNode]

  private var elaboratedTypeRef: CgscriptType = _

  def elaboratedType = {
    if (elaboratedTypeRef == null)
      throw new RuntimeException(s"Node has not been elaborated: $this")
    else
      elaboratedTypeRef
  }

  def ensureElaborated(domain: ElaborationDomain): CgscriptType = {
    if (elaboratedTypeRef == null)
      elaboratedTypeRef = elaborateImpl(domain)
    elaboratedTypeRef
  }

  def elaborateImpl(domain: ElaborationDomain): CgscriptType

  def emitScalaCode(context: CompileContext, emitter: Emitter): Unit

  def mentionedClasses: Set[CgscriptClass] = {
    val classes = mutable.HashSet[CgscriptClass]()
    collectMentionedClasses(classes)
    classes.toSet
  }

  def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {
    addTypeToClasses(classes, elaboratedType)
    children foreach { _.collectMentionedClasses(classes) }
  }

  def addTypeToClasses(classes: mutable.HashSet[CgscriptClass], cgscriptType: CgscriptType): Unit = {
    cgscriptType match {
      case ConcreteType(cls, typeParameters, _) =>
        classes += cls
        typeParameters foreach { addTypeToClasses(classes, _) }
      case TypeVariable(_, _) =>
    }
  }

  final def toNodeString: String = toNodeStringPrec(Int.MaxValue)

  def toNodeStringPrec(enclosingPrecedence: Int): String

}

trait ClassSpecifierNode extends EvalNode

trait ConstantNode extends EvalNode {

  def constantValue: Any

  override val children = Seq.empty

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    CgscriptClass.instanceToOutput(constantValue).toString
  }

}

case class NullNode(tree: Tree) extends ConstantNode {

  override val constantValue = null

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.NothingClass)

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print "null"
  }

}

case class StarNode(tree: Tree) extends ConstantNode {

  override val constantValue = star

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.Nimber)

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print "org.cgsuite.core.Values.star"
  }

}

case class BooleanNode(tree: Tree, override val constantValue: Boolean) extends ConstantNode {

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.Boolean)

  override def emitScalaCode(context: CompileContext, emitter: Emitter) {
    emitter print constantValue.toString
  }

}

case class IntegerNode(tree: Tree, override val constantValue: Integer) extends ConstantNode {

  override def elaborateImpl(domain: ElaborationDomain) = {
    if (constantValue.isZero)
      CgscriptType(CgscriptClass.Zero)
    else
      CgscriptType(CgscriptClass.Integer)
  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    if (constantValue.isZero)
      emitter print s"org.cgsuite.core.Values.zero"
    else if (constantValue.isSmallInteger)
      emitter print s"org.cgsuite.core.Integer($constantValue)"
    else
      emitter print "org.cgsuite.core.Integer.parseInteger(\"" + constantValue + "\")"
  }

}

case class StringNode(tree: Tree, override val constantValue: String) extends ConstantNode {

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.String)

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print ("\"" + StringEscapeUtils.ESCAPE_JAVA.translate(constantValue) + "\"")
  }

}

case class ThisNode(tree: Tree) extends EvalNode {

  override val children = Seq.empty

  var literal: String = _

  override def elaborateImpl(domain: ElaborationDomain) = {
    val thisClass = domain.cls getOrElse { sys.error("invalid `this`") }
    literal = if (thisClass.isSystem) "_instance" else "this"
    CgscriptType(thisClass, thisClass.typeParameters)
  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print literal
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = "this"

}

object VarNode {
  def apply(tree: Tree): AssignToNode = {
    assert(tree.getType == VAR && tree.children.size == 1)
    val t = tree.children.head
    t.getType match {
      case IDENTIFIER => AssignToNode(t, IdentifierNode(t), NullNode(null), AssignmentDeclType.VarDecl)
      case ASSIGN => AssignToNode(t, IdentifierNode(t.head), EvalNode(t.children(1)), AssignmentDeclType.VarDecl)
    }
  }
}

object AsNode {

  def apply(tree: Tree): AsNode = AsNode(tree, EvalNode(tree.head), TypeSpecifierNode(tree.children(1)))

}

case class AsNode(tree: Tree, exprNode: EvalNode, typeSpecNode: TypeSpecifierNode) extends EvalNode {

  override val children = Vector(exprNode, typeSpecNode)

  override def toNodeStringPrec(enclosingPrecedence: Int) = {
    val exprStr = exprNode.toNodeStringPrec(OperatorPrecedence.As)
    val typeStr = typeSpecNode.toNodeStringPrec(OperatorPrecedence.As)
    if (OperatorPrecedence.As <= enclosingPrecedence) {
      s"$exprStr as $typeStr"
    } else {
      s"($exprStr as $typeStr)"
    }

  }

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    // TODO Validate that EvalNode actually might be castable to the desired class?
    exprNode.ensureElaborated(domain)
    typeSpecNode.toType(domain)

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    exprNode.emitScalaCode(context, emitter)
    val scalaTypeName = elaboratedType.scalaTypeName
    emitter print s".asInstanceOf[$scalaTypeName]"

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    exprNode.collectMentionedClasses(classes)

  }

}

object IdentifierNode {

  object IdentifierType extends Enumeration {
    val VarIdentifier, ClassIdentifier, PackageIdentifier, ConstantIdentifier, AutoinvokeMethodIdentifier = Value
  }

  def apply(tree: Tree): IdentifierNode = {
    tree.getType match {
      case IDENTIFIER | DECL_ID | INFIX_OP => IdentifierNode(tree, Symbol(tree.getText))
      case DECL_OP => declOpToIdentifier(tree.children.head)
    }
  }

  def declOpToIdentifier(tree: Tree): IdentifierNode = {
    val name = {
      tree.token.getType match {
        case UNARY => s"unary${tree.children.head.getText}"
        case _ => tree.getText
      }
    }
    IdentifierNode(tree, Symbol(name))
  }

}

case class IdentifierNode(tree: Tree, id: Symbol) extends ClassSpecifierNode {

  private var elaboratedMemberResolution: Option[MemberResolution] = _
  private var isElaboratedInLocalScope: Boolean = _

  def resolveAsPackage(domain: ElaborationDomain): Option[CgscriptPackage] = {

    // TODO: Fix this. We need a symbol table in CgscriptClass that
    // can be consulted *during* classInfo construction.
    val shadowingResolution: Option[MemberResolution] = None
    /*
      domain.typeOf(id) orElse
      resolveAsLocalMember(domain.cls) orElse
      resolveAsPackageMember(domain.cls)
    */
    shadowingResolution match {
      case Some(_) => None
      case None => CgscriptPackage.root.lookupSubpackage(id)
    }

  }

  def resolveAsLocalMember(classScope: Option[CgscriptClass]): Option[MemberResolution] = {

    classScope flatMap { _.resolveMember(id) }

  }

  def resolveAsPackageMember(classScope: Option[CgscriptClass]): Option[MemberResolution] = {

    val constantResolution =
      classScope flatMap { _.pkg.lookupConstantMember(id) } orElse
        CgscriptPackage.lookupConstantMember(id)

    constantResolution orElse {

      classScope flatMap { _.pkg.lookupClass(id) } orElse
        CgscriptPackage.lookupClassByName(id.name)

    }

  }

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    elaboratedMemberResolution = None

    domain.typeOf(id).flatten getOrElse {

      resolveAsLocalMember(domain.cls) match {

        case Some(memberResolution) =>
          isElaboratedInLocalScope = true
          elaboratedMemberResolution = Some(memberResolution)

        case None =>
          isElaboratedInLocalScope = false
          elaboratedMemberResolution = resolveAsPackageMember(domain.cls)

      }

      elaboratedMemberResolution match {

        case Some(methodGroup: CgscriptClass#MethodGroup) =>
          methodGroup.autoinvokeMethod match {
            case Some(methodProjection) => methodProjection.ensureElaborated()
            case None => throw EvalException(s"Method `${methodGroup.qualifiedName}` requires arguments")
          }

        case Some(variable: CgscriptClass#Var) => variable.ensureElaborated()

        case Some(cls: CgscriptClass) if cls.isSingleton => CgscriptType(cls)

        case Some(cls: CgscriptClass) => CgscriptType(CgscriptClass.Class, Vector(cls.mostGenericType))

        case None => throw EvalException(s"That variable is not defined: `${id.name}`", token = Some(token))

      }

    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    val scalaId = elaboratedMemberResolution match {

      case None => id.name        // Local variable

      case Some(methodGroup: CgscriptClass#MethodGroup) =>
        assert(methodGroup.autoinvokeMethod.isDefined, "should have been caught during elaboration")
        val method = methodGroup.autoinvokeMethod.get.method
        if (method.isExternal && method.methodName != "EnclosingObject")
          s"_instance.${method.scalaName}"
        else
          method.scalaName

      case Some(variable: CgscriptClass#Var) =>
        if (isElaboratedInLocalScope)
          variable.id.name
        else
          variable.declaringClass.scalaClassdefName + "." + variable.id.name

      case Some(cls: CgscriptClass) =>
        cls.scalaClassrefName

      case None => sys.error("this should have been caught during elaboration")

    }

    emitter print scalaId

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    super.collectMentionedClasses(classes)

    // There may be an implicit mention of a constants class, so we need to handle
    // that possibility separately.
    elaboratedMemberResolution match {
      case Some(memberResolution) if memberResolution.declaringClass != null =>
        classes += memberResolution.declaringClass
      case _ =>
    }

  }

  override val children = Seq.empty

  def toNodeStringPrec(enclosingPrecedence: Int) = id.name

}

object TypeSpecifierNode {

  def apply(tree: Tree): TypeSpecifierNode = {

    tree.getType match {

      case TYPE_VARIABLE =>
        TypeVariableNode(tree)

      case IDENTIFIER | DOT =>
        ConcreteTypeSpecifierNode(tree, ClassSpecifierNode(tree), Vector.empty)

      case OF =>
        val typeArguments = tree.children.drop(1) map { TypeSpecifierNode(_) }
        ConcreteTypeSpecifierNode(tree, ClassSpecifierNode(tree.head), typeArguments)

    }

  }

}

trait TypeSpecifierNode extends EvalNode {

  // If allowInstanceNestedClasses == true, then we allow this type to be resolved to a nested class
  // defined in the domain's scope class. If allowInstanceNestedClasses == false, then we only allow resolution
  // to other nested classes of the enclosing class (or no nested classes, if the scope class has no enclosing
  // class). The case allowInstanceNestedClasses == false is necessary mainly for parsing an `extends` clause.
  def toType(domain: ElaborationDomain, allowInstanceNestedClasses: Boolean = true): CgscriptType

  override def elaborateImpl(domain: ElaborationDomain) = ???

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = ???

}

object TypeVariableNode {

  def apply(tree: Tree): TypeVariableNode = {
    assert(tree.getType == TYPE_VARIABLE)
    assert(tree.children.isEmpty || tree.head.getType == AST)
    TypeVariableNode(tree, Symbol(tree.getText), isExpandable = tree.children.nonEmpty)
  }

}

case class TypeVariableNode(tree: Tree, id: Symbol, isExpandable: Boolean) extends TypeSpecifierNode {

  override def children = Vector.empty

  def toType = TypeVariable(id, isExpandable)

  override def toType(domain: ElaborationDomain, allowInstanceNestedClasses: Boolean = true) = toType

  override def toNodeStringPrec(enclosingPrecedence: Int) = {
    s"`${id.name}${if (isExpandable) "*" else ""}"
  }

}

case class ConcreteTypeSpecifierNode(tree: Tree, baseClassIdNode: ClassSpecifierNode, typeArgumentNodes: Vector[TypeSpecifierNode]) extends TypeSpecifierNode {

  override def children = baseClassIdNode +: typeArgumentNodes

  override def toType(domain: ElaborationDomain, allowInstanceNestedClasses: Boolean = true): ConcreteType = {

    val baseClass = {

      baseClassIdNode match {

        case IdentifierNode(_, id) =>
          val nestedClassResolution = {
            if (allowInstanceNestedClasses) {
              domain.cls flatMap { _.classInfo.allInstanceNestedClassesInScope.get(id) }
            } else {
              domain.cls flatMap { _.enclosingClass flatMap { _.classInfo.allInstanceNestedClassesInScope.get(id) } }
            }
          }
          val classResolution =
            nestedClassResolution orElse
              domain.pkg.lookupClass(id) orElse
              CgscriptPackage.lookupClassByName(id.name)
          classResolution getOrElse {
            throw EvalException(s"Unrecognized type: `${id.name}`", token = Some(baseClassIdNode.token))
          }

        case dotNode: DotNode =>
          dotNode.resolveAsPackageMember(domain) match {
            case Some(cls: CgscriptClass) => cls
            case _ =>
              throw EvalException(s"Unrecognized type: `${dotNode.toNodeStringPrec(0)}`", token = Some(baseClassIdNode.token))
          }

      }

    }

    // Check that type arguments are consistent with the base class definition.
    val typeArguments = typeArgumentNodes map { _.toType(domain, allowInstanceNestedClasses) }
    if (typeArguments.isEmpty && baseClass.typeParameters.nonEmpty) {
      throw EvalException(s"Class `${baseClass.qualifiedName}` requires type parameters")
    } else if (
      // If the number of parameters disagrees, it's an error, but we have to make
      // an exception for expandable type parameters (Procedures)
      (baseClass.typeParameters.isEmpty || !baseClass.typeParameters.head.isExpandable) &&
        baseClass.typeParameters.size != typeArguments.size) {
      throw EvalException(s"Incorrect number of type parameters for class: `${baseClass.qualifiedName}`")
    }

    ConcreteType(baseClass, typeArguments)

  }

  override def toNodeStringPrec(enclosingPrecedence: Int) = {
    // TODO Use elaborated type...
    val typeArgumentsStr = typeArgumentNodes.length match {
      case 0 => ""
      case 1 => " of " + typeArgumentNodes.head.toNodeStringPrec(enclosingPrecedence)
      case 2 => " of (" + typeArgumentNodes.map { _.toNodeStringPrec(enclosingPrecedence) }.mkString(", ") + ")"
    }
    baseClassIdNode.toNodeStringPrec(0) + typeArgumentsStr
  }

}

object UnOpNode {

  def apply(tree: Tree, op: UnOp): UnOpNode = UnOpNode(tree, op, EvalNode(tree.head))

}

case class UnOpNode(tree: Tree, op: UnOp, operand: EvalNode) extends EvalNode {

  override val children = Seq(operand)

  override def elaborateImpl(domain: ElaborationDomain) = {

    val operandType = operand.ensureElaborated(domain)
    val opMethod = FunctionCallNode.lookupMethodWithImplicits(operandType, op.id, Vector.empty)
    // TODO Unary opMethods need to be enforced as having no args
    opMethod match {
      case Some(methodProjection) => methodProjection.ensureElaborated()
      case _ => throw EvalException(s"No operation `${op.name}` for argument of type `${operandType.baseClass.qualifiedName}`", tree)
    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print "("
    op.emitScalaCode(context, emitter, operand)
    emitter print ")"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val opStr = operand.toNodeStringPrec(op.precedence)
    if (op.precedence <= enclosingPrecedence)
      op.toOpString(opStr)
    else
      s"(${op.toOpString(opStr)})"
  }

}

object BinOpNode {
  def apply(tree: Tree, op: BinOp): BinOpNode = BinOpNode(tree, op, EvalNode(tree.head), EvalNode(tree.children(1)))
}

case class BinOpNode(tree: Tree, op: BinOp, operand1: EvalNode, operand2: EvalNode) extends EvalNode {

  override val children = Seq(operand1, operand2)

  var operand1Type: CgscriptType = _

  override def elaborateImpl(domain: ElaborationDomain) = {

    operand1Type = operand1.ensureElaborated(domain)
    val operand2Type = operand2.ensureElaborated(domain)
    val opMethod = FunctionCallNode.lookupMethodWithImplicits(operand1Type, op.baseId, Vector(operand2Type))
    val result = opMethod match {
      case Some(method) =>
        val methodType = method.ensureElaborated()
        methodType.substituteAll(operand1Type.baseClass.typeParameters zip operand1Type.typeArguments)
      case None => throw EvalException(s"No operation `${op.baseId.name}` for arguments of types `${operand1Type.baseClass.qualifiedName}`, `${operand2Type.baseClass.qualifiedName}`", tree)
    }
    if (op.id != op.baseId) {
      val opMethod2 = FunctionCallNode.lookupMethodWithImplicits(operand2Type, op.baseId, Vector(operand1Type))
      val result2 = opMethod2 match {
        case Some(method) => method.ensureElaborated()
        case None => throw EvalException(s"No operation `${op.baseId.name}` for arguments of types `${operand2Type.baseClass.qualifiedName}`, `${operand1Type.baseClass.qualifiedName}`", tree)
      }
      // TODO Check result1 == result2 and they are Boolean (for relational ops)
    }
    result

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    if (operand1Type.baseClass == CgscriptClass.List && op.id.name == "[]") {
      emitter print "("
      operand1.emitScalaCode(context, emitter)
      emitter print ")._lookup("
      operand2.emitScalaCode(context, emitter)
      emitter print ")"
    } else {
      emitter print "("
      op.emitScalaCode(context, emitter, operand1, operand2)
      emitter print ")"
    }
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val op1str = operand1.toNodeStringPrec(op.precedence)
    val op2str = operand2.toNodeStringPrec(op.precedence)
    if (op.precedence <= enclosingPrecedence) {
      op.toOpString(op1str, op2str)
    } else {
      s"(${op.toOpString(op1str, op2str)})"
    }
  }

}

object CoordinatesNode {

  def apply(tree: Tree): CoordinatesNode = {
    assert(tree.getType == COORDINATES)
    CoordinatesNode(tree, EvalNode(tree.head), EvalNode(tree.children(1)))
  }

}

case class CoordinatesNode(tree: Tree, coord1: EvalNode, coord2: EvalNode) extends EvalNode {

  override val children = Vector(coord1, coord2)

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    val type1 = coord1.ensureElaborated(domain)
    val type2 = coord2.ensureElaborated(domain)

    // TODO Validate that they're integers - can this just become a FunctionCallNode?

    CgscriptType(CgscriptClass.Coordinates)

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    emitter print "org.cgsuite.util.Coordinates("
    coord1.emitScalaCode(context, emitter)
    emitter print ".intValue, "
    coord2.emitScalaCode(context, emitter)
    emitter print ".intValue)"

  }

  override def toNodeStringPrec(enclosingPrecedence: Int) = ???

}

trait CollectionNode extends EvalNode {

  def elements: Vector[EvalNode]

  def collectionClass: CgscriptClass

  def scalaCollectionClassName: String

  override def elaborateImpl(domain: ElaborationDomain) = {
    val elementTypes = elements map { _.ensureElaborated(domain) }
    val joinedElementType = {
      if (elementTypes.isEmpty)
        CgscriptType(CgscriptClass.NothingClass)
      else
        elementTypes reduce { _ join _ }
    }
    CgscriptType(collectionClass, Vector(joinedElementType))
  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print s"$scalaCollectionClassName("
    for (i <- elements.indices) {
      elements(i).emitScalaCode(context, emitter)
      if (i < elements.length - 1)
        emitter print ", "
    }
    emitter print ")"
  }

}

object ListNode {
  def apply(tree: Tree): ListNode = {
    assert(tree.getType == EXPLICIT_LIST)
    ListNode(tree, tree.children.map { EvalNode(_) }.toVector)
  }
}

case class ListNode(tree: Tree, elements: Vector[EvalNode]) extends CollectionNode {

  def collectionClass = CgscriptClass.List

  def scalaCollectionClassName = "Vector"

  override val children = elements

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "[" + (elements map {  _.toNodeString } mkString ", ") + "]"
  }

}

object SetNode {
  def apply(tree: Tree): SetNode = {
    assert(tree.getType == EXPLICIT_SET)
    SetNode(tree, tree.children.map { EvalNode(_) })
  }
}

case class SetNode(tree: Tree, elements: Vector[EvalNode]) extends CollectionNode {

  def collectionClass = CgscriptClass.Set

  def scalaCollectionClassName = "Set"

  override val children = elements

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "{" + (elements map { _.toNodeString } mkString ", ") + "}"
  }

}

object MapNode {

  def apply(tree: Tree): MapNode = {
    assert(tree.getType == EXPLICIT_MAP)
    val mapPairNodes = tree.children map { MapPairNode(_) }
    MapNode(tree, mapPairNodes)
  }

}

case class MapNode(tree: Tree, elements: Vector[MapPairNode]) extends EvalNode {

  override def children = elements

  override def elaborateImpl(domain: ElaborationDomain) = {
    val elementTypes = elements map { _.ensureElaborated(domain) }
    val kvTypes = elementTypes map { elementType =>
      assert(elementType.baseClass == CgscriptClass.MapEntry, elementType)  // Guaranteed by parser
      (elementType.typeArguments.head, elementType.typeArguments(1))
    }
    val (keyTypes, valueTypes) = kvTypes.unzip
    val joinedKeyType = {
      if (keyTypes.isEmpty)
        CgscriptType(CgscriptClass.NothingClass)
      else
        keyTypes reduce { _ join _ }
    }
    val joinedValueType = {
      if (valueTypes.isEmpty)
        CgscriptType(CgscriptClass.NothingClass)
      else
        valueTypes reduce { _ join _ }
    }
    CgscriptType(CgscriptClass.Map, Vector(joinedKeyType, joinedValueType))
  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print "Map("
    for (i <- elements.indices) {
      elements(i).emitScalaCode(context, emitter)
      if (i < elements.length - 1)
        emitter print ", "
    }
    emitter print ")"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    if (elements.isEmpty)
      "{=>}"
    else
      "{" + (elements map { _.toNodeString } mkString ", ") + "}"
  }

}

object MapPairNode {
  def apply(tree: Tree): MapPairNode = {
    MapPairNode(tree, EvalNode(tree.head), EvalNode(tree.children(1)))
  }
}

case class MapPairNode(tree: Tree, from: EvalNode, to: EvalNode) extends EvalNode {

  override val children = Seq(from, to)

  override def elaborateImpl(domain: ElaborationDomain) = {
    CgscriptType(
      CgscriptClass.MapEntry,
      Vector(from.ensureElaborated(domain), to.ensureElaborated(domain))
    )
  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print "("
    from.emitScalaCode(context, emitter)
    emitter print " -> "
    to.emitScalaCode(context, emitter)
    emitter print ")"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = s"${from.toNodeString} => ${to.toNodeString}"

}

case class GameSpecNode(tree: Tree, lo: Vector[EvalNode], ro: Vector[EvalNode], forceExplicit: Boolean) extends EvalNode {

  override def elaborateImpl(domain: ElaborationDomain) = {

    val optionTypes = (lo ++ ro) map { _.ensureElaborated(domain) }

    CgscriptType(
      if (optionTypes.isEmpty)
        CgscriptClass.Zero
      else if (!forceExplicit && allOfType(optionTypes, CgscriptClass.CanonicalShortGame))
        CgscriptClass.CanonicalShortGame
      else if (!forceExplicit && allOfType(optionTypes, CgscriptClass.CanonicalStopper))
        CgscriptClass.CanonicalStopper
      else if (!forceExplicit && allOfType(optionTypes, CgscriptClass.SidedValue))
        CgscriptClass.SidedValue
      else if (allOfType(optionTypes, CgscriptClass.Game))
        CgscriptClass.ExplicitGame
      else if (allOfType(optionTypes, CgscriptClass.SidedValue))
        sys error "can't be force explicit - need better error msg here"
      else
        throw EvalException("Invalid game specifier: objects must be of type `Game` or `SidedValue`", tree)
    )

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    val antecedent = {
      elaboratedType.baseClass match {
        case CgscriptClass.Zero => "org.cgsuite.core.Values.zero"
        case CgscriptClass.CanonicalShortGame => s"org.cgsuite.core.CanonicalShortGame"
        case CgscriptClass.CanonicalStopper => s"org.cgsuite.core.CanonicalStopper"
        case CgscriptClass.ExplicitGame => s"org.cgsuite.core.ExplicitGame"
        case CgscriptClass.SidedValue => s"org.cgsuite.core.SidedValue"
      }
    }

    emitter print antecedent
    if (elaboratedType.baseClass != CgscriptClass.Zero) {
      emitter print "("
      for (i <- lo.indices) {
        lo(i).emitScalaCode(context, emitter)
        if (i < lo.length - 1)
          emitter print ", "
      }
      emitter print ")("
      for (i <- ro.indices) {
        ro(i).emitScalaCode(context, emitter)
        if (i < ro.length - 1)
          emitter print ", "
      }
      emitter print ")"
    }

  }

  private def allOfType(types: Iterable[CgscriptType], target: CgscriptClass) = {
    // Allow implicit conversion of Rational => DyadicRational
    types forall { typ =>
      val convertedType = typ.baseClass match {
        case CgscriptClass.Rational => CgscriptType(CgscriptClass.DyadicRational)
        case _ => typ
      }
      convertedType.baseClass.classInfo.ancestors contains target
    }
  }

  override val children = lo ++ ro

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loStr = lo map { _.toNodeString } mkString ","
    val roStr = ro map { _.toNodeString } mkString ","
    s"{$loStr | $roStr}"
  }

}

object LoopyGameSpecNode {

  def apply(tree: Tree): EvalNode = {
    tree.getType match {
      case NODE_LABEL => make(tree, Some(IdentifierNode(tree.head)), tree.children(1))
      case SLASHES => make(tree, None, tree)
      case _ => EvalNode(tree)
    }
  }

  def make(tree: Tree, nodeLabel: Option[IdentifierNode], body: Tree): LoopyGameSpecNode = {
    assert(body.getType == SLASHES)
    val (loPass, lo) = loopyGameOptions(body.head)
    val (roPass, ro) = loopyGameOptions(body.children(1))
    LoopyGameSpecNode(tree, nodeLabel, lo, ro, loPass, roPass)
  }

  private def loopyGameOptions(tree: Tree): (Boolean, Seq[EvalNode]) = {
    tree.getType match {
      case SLASHES => (false, Seq(LoopyGameSpecNode(tree)))
      case EXPRESSION_LIST =>
        val (pass, opts) = tree.children partition { _.getType == PASS }
        (pass.nonEmpty, opts map { LoopyGameSpecNode(_) })
      case _ => (false, Seq(EvalNode(tree)))
    }
  }
}

case class LoopyGameSpecNode(
  tree: Tree,
  nodeLabel: Option[IdentifierNode],
  lo: Seq[EvalNode],
  ro: Seq[EvalNode],
  loPass: Boolean,
  roPass: Boolean
  ) extends EvalNode {

  override val children = nodeLabel ++ lo ++ ro

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    elaborateLoopy(domain, mutable.HashSet[Symbol]())

  }

  def elaborateLoopy(domain: ElaborationDomain, nodeLabels: mutable.HashSet[Symbol]): CgscriptType = {

    // TODO Check for duplicate or shadow node labels
    nodeLabel foreach { nodeLabels += _.id }
    lo foreach { validateLoopyOption(_, domain, nodeLabels) }
    ro foreach { validateLoopyOption(_, domain, nodeLabels) }
    nodeLabel foreach { nodeLabels -= _.id }

    CgscriptType(CgscriptClass.SidedValue)

  }

  private def validateLoopyOption(optionNode: EvalNode, domain: ElaborationDomain, nodeLabels: mutable.HashSet[Symbol]): Unit = {

    optionNode match {

      case loopyGameSpecNode: LoopyGameSpecNode =>
        loopyGameSpecNode.elaborateLoopy(domain, nodeLabels)

      case identifierNode: IdentifierNode if nodeLabels contains identifierNode.id =>

      case _ =>
        val optionType = optionNode.ensureElaborated(domain)
        if (!optionType.baseClass.ancestors.contains(CgscriptClass.CanonicalStopper) &&
            !optionType.baseClass.ancestors.contains(CgscriptClass.Rational))
          sys.error("must be list of games - need error message here")    // TODO

    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    emitter print "{ "
    emitter.indent()

    val nodeName = emitScalaCode(context, emitter, new mutable.HashSet[Symbol]())

    emitter println s"org.cgsuite.core.SidedValue(new org.cgsuite.core.LoopyGame($nodeName))"
    emitter.indent(-1)
    emitter print "}"

  }

  private def emitScalaCode(context: CompileContext, emitter: Emitter, nodeLabels: mutable.HashSet[Symbol]): String = {

    nodeLabel foreach { nodeLabels += _.id }

    val nodeName = context.newTempId()
    val labelName = nodeLabel map { _.id.name } getOrElse nodeName

    emitter print s"val $nodeName = "
    if (nodeLabel.isDefined) {
      emitter println "{"
      emitter.indent()
      emitter print s"val $labelName = "
    }
    emitter println "new org.cgsuite.core.LoopyGame.Node()"

    if (loPass) emitter println s"$labelName.addLeftEdge($labelName)"
    lo foreach { node =>
      loopyOptionToScalaCode(labelName, Left, node, context, emitter, nodeLabels)
    }
    if (roPass) emitter println s"$labelName.addRightEdge($labelName)"
    ro foreach { node =>
      loopyOptionToScalaCode(labelName, Right, node, context, emitter, nodeLabels)
    }

    if (nodeLabel.isDefined) {
      emitter println labelName
      emitter.indent(-1)
      emitter println "}"
    }

    nodeLabel foreach { nodeLabels -= _.id }

    nodeName

  }

  private def loopyOptionToScalaCode(
    labelName: String,
    player: Player,
    node: EvalNode,
    context: CompileContext,
    emitter: Emitter,
    nodeLabels: mutable.HashSet[Symbol]
    ): Unit = {

    node match {

      case loopyGameSpecNode: LoopyGameSpecNode =>
        val nodeName = loopyGameSpecNode.emitScalaCode(context, emitter, nodeLabels)
        emitter println s"$labelName.add${player}Edge($nodeName)"

      case identifierNode: IdentifierNode if nodeLabels contains identifierNode.id =>
        emitter println s"$labelName.add${player}Edge(${identifierNode.id.name})"

      case _ =>
        emitter println s"$labelName.add${player}Edge {"
        emitter.indent()
        node.emitScalaCode(context, emitter)
        emitter.indent(-1)
        emitter println "\n}"

    }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loStr = (lo map { _.toNodeString }) ++ (if (loPass) Some("pass") else None) mkString ","
    val roStr = (ro map { _.toNodeString }) ++ (if (roPass) Some("pass") else None) mkString ","
    s"{$loStr | $roStr}"
  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {
    addTypeToClasses(classes, elaboratedType)
    collectMentionedClassesLoopy(classes, mutable.HashSet[Symbol]())
  }

  def collectMentionedClassesLoopy(classes: mutable.HashSet[CgscriptClass], nodeLabels: mutable.HashSet[Symbol]): Unit = {
    nodeLabel foreach { nodeLabels += _.id }
    (lo ++ ro) foreach {
      case loopyGameSpecNode: LoopyGameSpecNode => loopyGameSpecNode.collectMentionedClassesLoopy(classes, nodeLabels)
      case identifierNode: IdentifierNode if nodeLabels contains identifierNode.id =>
      case node => node.collectMentionedClasses(classes)
    }
    nodeLabel foreach { nodeLabels -= _.id }
  }

}

case class IfNode(tree: Tree, condition: EvalNode, ifNode: StatementSequenceNode, elseNode: Option[EvalNode]) extends EvalNode {

  elseNode match {
    case None =>
    case Some(_: StatementSequenceNode) =>
    case Some(_: IfNode) =>
    case _ => assert(assertion = false, "unexpected node type for elseNode")
  }

  override val children = Seq(condition, ifNode) ++ elseNode

  override def elaborateImpl(domain: ElaborationDomain) = {

    // TODO Validate that condition is a Boolean
    val conditionType = condition.ensureElaborated(domain)

    domain.pushScope()
    val ifType = ifNode.ensureElaborated(domain)
    domain.popScope()

    val elseTypeOpt = elseNode map { node =>
      domain.pushScope()
      val typ = node.ensureElaborated(domain)
      domain.popScope()
      typ
    }

    elseTypeOpt match {
      case Some(elseType) => ifType join elseType
      case None => ifType
    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    emitter println "{"
    emitter.indent()
    emitter print "if ("
    condition.emitScalaCode(context, emitter)
    emitter print ") "
    ifNode.emitScalaCode(context, emitter)
    elseNode match {
      case Some(node) =>
        emitter print " else "
        node.emitScalaCode(context, emitter)
      case None =>
        emitter print " else null"
    }
    emitter println ""
    if (elaboratedType.baseClass == CgscriptClass.NothingClass) {
      emitter println "null"
    }
    emitter.indent(-1)
    emitter println "}"

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = s"if ${condition.toNodeString} then ${ifNode.toNodeString}" +
    (elseNode map { " else " + _.toNodeString } getOrElse "") + " end"

}

case class ErrorNode(tree: Tree, msg: EvalNode) extends EvalNode {

  override val children = Seq(msg)

  override def elaborateImpl(domain: ElaborationDomain) = {
    // TODO Type check or type convert String
    msg.ensureElaborated(domain)
    CgscriptType(CgscriptClass.NothingClass)
  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    emitter print "throw org.cgsuite.exception.EvalException("
    msg.emitScalaCode(context, emitter)
    emitter print ")"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = s"error(${msg.toNodeString})"

}

object ClassSpecifierNode {

  def apply(tree: Tree): ClassSpecifierNode = {

    tree.getType match {

      case DOT => DotNode(tree, EvalNode(tree.head), IdentifierNode(tree.children(1)))
      case IDENTIFIER => IdentifierNode(tree)

    }

  }

}

object StatementSequenceNode {
  def apply(tree: Tree, topLevel: Boolean = false): StatementSequenceNode = {
    // Filter out the semicolons (we only care about the last one)
    val filteredChildren = tree.children filterNot { _.getType == SEMI }
    val suppressOutput = tree.children.isEmpty || tree.children.last.getType == SEMI
    StatementSequenceNode(tree, filteredChildren map { EvalNode(_) }, suppressOutput, topLevel = topLevel)
  }
}

case class StatementSequenceNode(tree: Tree, statements: Vector[EvalNode], suppressOutput: Boolean, topLevel: Boolean) extends EvalNode {

  assert(tree.getType == STATEMENT_SEQUENCE, tree.getType)

  override val children = statements

  override def elaborateImpl(domain: ElaborationDomain) = {

    if (!topLevel)
      domain.pushScope()
    statements foreach { _.ensureElaborated(domain) }
    if (!topLevel)
      domain.popScope()
    statements.lastOption match {
      case Some(node) => node.elaboratedType
      case None => CgscriptType(CgscriptClass.NothingClass)
    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {
    if (statements.isEmpty) {
      emitter println "org.cgsuite.output.EmptyOutput"
    } else {
      emitter println "{"
      emitter.indent()
      statements foreach { node =>
        node.emitScalaCode(context, emitter)
        emitter println ";"       // The semicolon is necessary in various strange parsing situations with newlines
      }
      emitter.indent(-1)
      emitter print "}"
    }
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val seqStr = statements map { _.toNodeStringPrec(OperatorPrecedence.StatementSeq) } mkString "; "
    if (OperatorPrecedence.StatementSeq <= enclosingPrecedence)
      seqStr
    else
      s"begin $seqStr end"
  }

}

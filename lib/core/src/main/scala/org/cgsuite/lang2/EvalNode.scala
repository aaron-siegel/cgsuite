package org.cgsuite.lang2

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang2.Node.treeToRichTree
import org.cgsuite.lang2.Ops._

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

  private def gameOptions(tree: Tree): Seq[EvalNode] = {
    tree.getType match {
      case SLASHES => Seq(EvalNode(tree))
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

  def elaborateImpl(domain: ElaborationDomain): CgscriptType = ???

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
      case ConcreteType(cls, typeParameters) =>
        classes += cls
        typeParameters foreach { addTypeToClasses(classes, _) }
    }
  }

  def toScalaCode(context: CompileContext): String = ???

  final def toNodeString: String = toNodeStringPrec(Int.MaxValue)

  def toNodeStringPrec(enclosingPrecedence: Int): String

}

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

  override def toScalaCode(context: CompileContext) = "null"

}

case class StarNode(tree: Tree) extends ConstantNode {

  override val constantValue = star

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.Nimber)

  override def toScalaCode(context: CompileContext) = "org.cgsuite.core.Values.star"

}

case class BooleanNode(tree: Tree, override val constantValue: Boolean) extends ConstantNode {

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.Boolean)

  override def toScalaCode(context: CompileContext) = constantValue.toString

}

case class IntegerNode(tree: Tree, override val constantValue: Integer) extends ConstantNode {

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.Integer)

  override def toScalaCode(context: CompileContext) = {
    if (constantValue.isSmallInteger)
      s"org.cgsuite.core.Integer($constantValue)"
    else
      "org.cgsuite.core.Integer.parseInteger(\"" + constantValue + "\")"
  }

}

// TODO Escape strings
case class StringNode(tree: Tree, override val constantValue: String) extends ConstantNode {

  override def elaborateImpl(domain: ElaborationDomain) = CgscriptType(CgscriptClass.String)

  override def toScalaCode(context: CompileContext) = "\"" + constantValue + "\""

}

case class ThisNode(tree: Tree) extends EvalNode {

  override val children = Seq.empty

  override def elaborateImpl(domain: ElaborationDomain) = {
    CgscriptType {
      domain.cls getOrElse { sys.error("invalid `this`")}
    }
  }

  override def toScalaCode(context: CompileContext) = "this"

  def toNodeStringPrec(enclosingPrecedence: Int) = "this"

}

object AsNode {

  def apply(tree: Tree): AsNode = AsNode(tree, EvalNode(tree.head), TypeSpecifierNode(tree.children(1)))

}

case class AsNode(tree: Tree, exprNode: EvalNode, typeSpecNode: TypeSpecifierNode) extends EvalNode {

  override val children = Vector(exprNode, typeSpecNode)

  override def toNodeStringPrec(enclosingPrecedence: Int) = ???

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    // TODO Validate that EvalNode actually might be castable to the desired class?
    exprNode.ensureElaborated(domain)
    typeSpecNode.toType

  }

  override def toScalaCode(context: CompileContext) = {

    val exprCode = exprNode.toScalaCode(context)
    val scalaTypeName = elaboratedType.scalaTypeName
    s"$exprCode.asInstanceOf[$scalaTypeName]"

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

case class IdentifierNode(tree: Tree, id: Symbol) extends EvalNode {

  import org.cgsuite.lang2.IdentifierNode._

  private var idType: IdentifierType.Value = _
  private var constantVar: CgscriptClass#Var = _

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    domain.typeOf(id) match {

      case Some(typ) =>

        idType = IdentifierType.VarIdentifier
        typ.get

      case _ =>

        // Try to resolve as an autoinvoke method
        domain.cls flatMap { _.lookupMethod(id, Vector.empty) } match {

          case Some(method) if method.autoinvoke =>

            idType = IdentifierType.AutoinvokeMethodIdentifier
            method.ensureElaborated()

          case _ =>

            // Try to resolve as a classname
            CgscriptPackage.lookupClass(id) match {

              case Some(cls) =>
                idType = IdentifierType.ClassIdentifier
                if (cls.isSingleton)
                  CgscriptType(cls)
                else
                  CgscriptType(CgscriptClass.Class, Vector(CgscriptType(cls)))

              case None =>

                // Try to resolve as a constant
                CgscriptPackage.lookupConstantVar(id) match {

                  case Some(constantVar: CgscriptClass#Var) =>
                    idType = IdentifierType.ConstantIdentifier
                    this.constantVar = constantVar
                    constantVar.ensureElaborated()

                  // TODO Nested Classes of constants?

                  case None =>
                    throw EvalException(s"That variable is not defined: `${id.name}`", token = Some(token))

                }

            }

        }

    }

  }

  override def toScalaCode(context: CompileContext): String = {

    assert(idType != null, this)

    idType match {

      case IdentifierType.VarIdentifier => id.name

      case IdentifierType.ClassIdentifier =>
        CgscriptPackage.lookupClass(id) match {
          case Some(cls) => cls.scalaClassname
          case _ => sys.error("this can't happen")
        }

      case IdentifierType.ConstantIdentifier =>
        CgscriptPackage.lookupConstantVar(id) match {
          case Some(variable: CgscriptClass#Var) =>
            variable.declaringClass.scalaClassname + "." + variable.id.name
          case _ => sys.error("this can't happen")
        }

    }

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    super.collectMentionedClasses(classes)
    if (idType == IdentifierType.ConstantIdentifier)
      classes += constantVar.declaringClass     // constants class is implicitly mentioned

  }

  override val children = Seq.empty

  def toNodeStringPrec(enclosingPrecedence: Int) = id.name

}

object TypeSpecifierNode {

  def apply(tree: Tree): TypeSpecifierNode = {

    assert(tree.getType == TYPE_SPECIFIER)
    println(tree.toStringTree)

    if (tree.head.getType == TYPE_VARIABLE) {

      TypeVariableNode(tree.head)

    } else {

      val baseClassIdNode = IdentifierNode(tree.children.head)
      val typeParameterNodes = {
        if (tree.children.size == 1)
          Vector.empty
        else if (tree.children(1).getType == TYPE_PARAMETERS)
          tree.children(1).children.toVector map { TypeSpecifierNode(_) }
        else
          Vector(TypeSpecifierNode(tree.children(1)))
      }

      ConcreteTypeSpecifierNode(tree, baseClassIdNode, typeParameterNodes)

    }

  }

}

trait TypeSpecifierNode extends EvalNode {

  def toType: CgscriptType

  override def toNodeStringPrec(enclosingPrecedence: Int) = ???

}

object TypeVariableNode {

  def apply(tree: Tree): TypeVariableNode = TypeVariableNode(tree, Symbol(tree.getText))

}

case class TypeVariableNode(tree: Tree, id: Symbol) extends TypeSpecifierNode {

  override def children = Vector.empty

  override def toType = TypeVariable(id)

}

case class ConcreteTypeSpecifierNode(tree: Tree, baseClassIdNode: IdentifierNode, typeParameterNodes: Vector[TypeSpecifierNode]) extends TypeSpecifierNode {

  override def children = baseClassIdNode +: typeParameterNodes

  override def toType: CgscriptType = {

    val baseClass = CgscriptPackage.lookupClass(baseClassIdNode.id) getOrElse {
      sys.error("class not found (needs error msg)")  // TODO
    }
    val typeParameters = typeParameterNodes map { _.toType }
    CgscriptType(baseClass, typeParameters)

  }

}

object UnOpNode {

  def apply(tree: Tree, op: UnOp): UnOpNode = UnOpNode(tree, op, EvalNode(tree.head))

}

case class UnOpNode(tree: Tree, op: UnOp, operand: EvalNode) extends EvalNode {

  override val children = Seq(operand)

  override def elaborateImpl(domain: ElaborationDomain) = {

    val operandType = operand.ensureElaborated(domain)
    val opMethod = FunctionCallNode.lookupMethod(operandType, op.id, Vector.empty)
    opMethod match {
      case Some(method) => method.explicitReturnType.get    // TODO Method elaboration!
      case _ => throw EvalException(s"No operation `${op.name}` for argument of type `${operandType.baseClass}`", tree)
    }

  }

  override def toScalaCode(context: CompileContext) = {
    val opCode = op.toScalaCode(operand.toScalaCode(context))
    s"($opCode)"
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

  override def elaborateImpl(domain: ElaborationDomain) = {

    val operand1Type = operand1.ensureElaborated(domain)
    val operand2Type = operand2.ensureElaborated(domain)
    val opMethod = FunctionCallNode.lookupMethod(operand1Type, op.id, Vector(operand2Type))

    opMethod match {
      case Some(method) => method.explicitReturnType.get    // TODO Method elaboration!
      case None => throw EvalException(s"No operation `${op.name}` for arguments of types `${operand1Type.baseClass}`, `${operand2Type.baseClass}`", tree)
    }

  }

  override def toScalaCode(context: CompileContext) = {
    "(" + op.toScalaCode(operand1.toScalaCode(context), operand2.toScalaCode(context)) + ")"
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

  override def toScalaCode(context: CompileContext) = {

    s"org.cgsuite.util.Coordinates(${coord1.toScalaCode(context)}.intValue, ${coord2.toScalaCode(context)}.intValue)"

  }

  override def toNodeStringPrec(enclosingPrecedence: Int) = ???

}

object ListNode {
  def apply(tree: Tree): ListNode = {
    assert(tree.getType == EXPLICIT_LIST)
    ListNode(tree, tree.children.map { EvalNode(_) }.toVector)
  }
}

case class ListNode(tree: Tree, elements: IndexedSeq[EvalNode]) extends EvalNode {

  override val children = elements

  override def elaborateImpl(domain: ElaborationDomain) = {
    elements foreach { _.ensureElaborated(domain) }
    CgscriptType(CgscriptClass.List)
  }

  override def toScalaCode(context: CompileContext) = {
    val elementsCode = elements map { _.toScalaCode(context) } mkString ", "
    s"Vector($elementsCode)"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "[" + (elements map {  _.toNodeString } mkString ", ") + "]"
  }

}

object SetNode {
  def apply(tree: Tree): SetNode = {
    assert(tree.getType == EXPLICIT_SET)
    SetNode(tree, tree.children.map { EvalNode(_) }.toIndexedSeq)
  }
}

case class SetNode(tree: Tree, elements: IndexedSeq[EvalNode]) extends EvalNode {

  override val children = elements

  override def toScalaCode(context: CompileContext) = {
    val elementsCode = elements map { _.toScalaCode(context) } mkString ", "
    s"Set($elementsCode)"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    "{" + (elements map { _.toNodeString } mkString ", ") + "}"
  }

}

object MapNode {

  def apply(tree: Tree): MapNode = {
    assert(tree.getType == EXPLICIT_MAP)
    val mapPairNodes = tree.children map { MapPairNode(_) }
    MapNode(tree, mapPairNodes.toIndexedSeq)
  }

}

case class MapNode(tree: Tree, elements: IndexedSeq[MapPairNode]) extends EvalNode {

  override val children = elements

  override def toScalaCode(context: CompileContext) = {
    val elementsCode = elements map { _.toScalaCode(context) } mkString ", "
    s"Map($elementsCode)"
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

  override def toScalaCode(context: CompileContext) = {
    val fromCode = from.toScalaCode(context)
    val toCode = to.toScalaCode(context)
    s"($fromCode -> $toCode)"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = s"${from.toNodeString} => ${to.toNodeString}"

}

case class GameSpecNode(tree: Tree, lo: Seq[EvalNode], ro: Seq[EvalNode], forceExplicit: Boolean) extends EvalNode {

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

  override def toScalaCode(context: CompileContext) = {

    val loCode = lo map { _.toScalaCode(context) } mkString ", "
    val roCode = ro map { _.toScalaCode(context) } mkString ", "

    elaboratedType.baseClass match {
      case CgscriptClass.Zero => "org.cgsuite.core.Values.zero"
      case CgscriptClass.CanonicalShortGame => s"org.cgsuite.core.CanonicalShortGame($loCode)($roCode)"
      case CgscriptClass.CanonicalStopper => s"org.cgsuite.core.CanonicalStopper($loCode)($roCode)"
      case CgscriptClass.ExplicitGame => s"org.cgsuite.core.ExplicitGame($loCode)($roCode)"
      case CgscriptClass.SidedValue => s"org.cgsuite.core.SidedValue($loCode)($roCode)"
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
        if (!optionType.baseClass.ancestors.contains(CgscriptClass.CanonicalStopper))
          sys.error("must be list of games - need error message here")    // TODO

    }

  }

  override def toScalaCode(context: CompileContext): String = {
    val sb = new StringBuilder("{\n")
    val nodeCode = toScalaCode(sb, context, new mutable.HashSet[Symbol]())
    sb.toString + s"org.cgsuite.core.SidedValue(new org.cgsuite.core.LoopyGame($nodeCode))\n}"
  }

  private def toScalaCode(sb: StringBuilder, context: CompileContext, nodeLabels: mutable.HashSet[Symbol]): String = {

    val nodeName = context.newTempId()
    val labelName = nodeLabel map { _.id.name } getOrElse nodeName

    sb append s"val $nodeName = "
    if (nodeLabel.isDefined)
      sb append s"{ val $labelName = "
    sb append "new org.cgsuite.core.LoopyGame.Node()\n"

    if (loPass) sb append s"$labelName.addLeftEdge($labelName)\n"
    lo foreach { node =>
      val nodeCode = loopyOptionToScalaCode(node, sb, context, nodeLabels)
      sb append s"$labelName.addLeftEdge($nodeCode)\n"
    }
    if (roPass) sb append s"$labelName.addRightEdge($labelName)\n"
    ro foreach { node =>
      val nodeCode = loopyOptionToScalaCode(node, sb, context, nodeLabels)
      sb append s"$labelName.addRightEdge($nodeCode)\n"
    }

    if (nodeLabel.isDefined)
      sb append s"$labelName }\n"

    nodeName

  }

  def loopyOptionToScalaCode(node: EvalNode, sb: StringBuilder, context: CompileContext, nodeLabels: mutable.HashSet[Symbol]): String = {

    node match {

      case loopyGameSpecNode: LoopyGameSpecNode =>
        loopyGameSpecNode.toScalaCode(sb, context, nodeLabels)

      case identifierNode: IdentifierNode if nodeLabels contains identifierNode.id =>
        identifierNode.id.name

      case _ =>
        node.toScalaCode(context)

    }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loStr = (lo map { _.toNodeString }) ++ (if (loPass) Some("pass") else None) mkString ","
    val roStr = (ro map { _.toNodeString }) ++ (if (roPass) Some("pass") else None) mkString ","
    s"{$loStr | $roStr}"
  }

}

case class IfNode(tree: Tree, condition: EvalNode, ifNode: StatementSequenceNode, elseNode: Option[EvalNode]) extends EvalNode {

  override val children = Seq(condition, ifNode) ++ elseNode

  override def toScalaCode(context: CompileContext) = {
    val conditionCode = condition.toScalaCode(context)
    val ifCode = ifNode.toScalaCode(context)
    val elseCode = elseNode map { _.toScalaCode(context) }
    val elseClause = elseCode map { code => s"else $code" } getOrElse ""
    s"(if ($conditionCode) $ifCode $elseClause)"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = s"if ${condition.toNodeString} then ${ifNode.toNodeString}" +
    (elseNode map { " else " + _.toNodeString } getOrElse "") + " end"

}

object LoopNode {

  def apply(tree: Tree): LoopNode = {

    val loopType = tree.getType match {
      case DO => Do
      case YIELD | LISTOF => YieldList
      case MAPOF => YieldMap
      case SETOF => YieldSet
      case TABLEOF => YieldTable
      case SUMOF => YieldSum
    }
    val body = EvalNode(tree.children.last)
    val loopSpecs = tree.children dropRight 1
    assert(loopSpecs.forall { _.getType == LOOP_SPEC })

    def makeLoopNode(loopSpecTree: Tree, nextNode: EvalNode): LoopNode = {
      LoopNode(
        loopSpecTree,
        loopType,
        loopSpecTree.children find { _.getType == FOR   } map { t => IdentifierNode(t.head) },
        loopSpecTree.children find { _.getType == IN    } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == FROM  } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == TO    } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == BY    } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == WHILE } map { t => EvalNode(t.head) },
        loopSpecTree.children find { _.getType == WHERE } map { t => EvalNode(t.head) },
        nextNode
      )
    }

    loopSpecs.foldRight(body)(makeLoopNode).asInstanceOf[LoopNode]

  }

  sealed trait LoopType
  case object Do extends LoopType
  case object YieldList extends LoopType
  case object YieldMap extends LoopType
  case object YieldSet extends LoopType
  case object YieldTable extends LoopType
  case object YieldSum extends LoopType

}

case class LoopNode(
  tree    : Tree,
  loopType: LoopNode.LoopType,
  forId   : Option[IdentifierNode],
  in      : Option[EvalNode],
  from    : Option[EvalNode],
  to      : Option[EvalNode],
  by      : Option[EvalNode],
  `while` : Option[EvalNode],
  where   : Option[EvalNode],
  body    : EvalNode
) extends EvalNode {

  override val children = forId.toSeq ++ in ++ from ++ to ++ by ++ `while` ++ where :+ body

  private val prepareLoop = Symbol(s"PrepareLoop [${tree.location}]")
  private val loop = Symbol(s"Loop [${tree.location}]")
  private val loopBody = Symbol(s"LoopBody [${tree.location}]")

  private val isYield: Boolean = loopType match {
    case LoopNode.Do | LoopNode.YieldSum => false
    case LoopNode.YieldList | LoopNode.YieldMap | LoopNode.YieldSet | LoopNode.YieldTable => true
  }

  private val pushDownYield: Option[LoopNode] = (isYield, body) match {
    case (true, loopBody: LoopNode) =>
      assert(loopBody.isYield)
      Some(loopBody)
    case _ => None
  }

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    val inType = in map { _.ensureElaborated(domain) }
    val fromType = from map { _.ensureElaborated(domain) }
    val toType = to map { _.ensureElaborated(domain) }
    val byType = by map { _.ensureElaborated(domain) }

    inType foreach { typ =>
      if (!(typ <= CgscriptType(CgscriptClass.Collection)))
        sys.error("Need error msg for when in is not a collection")   // TODO
    }

    forId match {

      case Some(idNode) =>
        assert(fromType.isDefined || inType.isDefined)    // Guaranteed by parser
        // TODO Implement generics...
        val forIdType = fromType getOrElse CgscriptType(CgscriptClass.Object) // inType.get.typeParameters.head
        domain.pushScope()
        domain.insertId(idNode.id, forIdType)

      case None =>

    }

    val whileType = `while` map { _.ensureElaborated(domain) }
    val whereType = where map { _.ensureElaborated(domain) }

    // TODO Validate where/while as Boolean

    val bodyType = body.ensureElaborated(domain)

    forId match {

      case Some(idNode) =>
        domain.popScope()

      case None =>

    }

    pushDownYield match {
      case Some(loopNode) => loopNode.elaboratedType
      case None =>
        loopType match {
          case LoopNode.Do => CgscriptType(CgscriptClass.NothingClass)
          case LoopNode.YieldList => CgscriptType(CgscriptClass.List, Vector(bodyType))
          case LoopNode.YieldSet => CgscriptType(CgscriptClass.Set, Vector(bodyType))
          // TODO Others
        }
    }

  }

  override def toScalaCode(context: CompileContext): String = toScalaCode(context, None)

  def toScalaCode(context: CompileContext, pushdownYieldVar: Option[String]): String = {

    val continueVar = context.newTempId()
    val tempResultVar = context.newTempId()
    val loopVar = {
      if (forId.isDefined)
        forId.get.id.name
      else if (from.isDefined)
        context.newTempId()
      else
        ""
    }
    val iteratorVar = {
      if (in.isDefined)
        context.newTempId()
      else
        ""
    }
    val yieldResultVar = {
      if (isYield)
        pushdownYieldVar getOrElse context.newTempId()
      else
        ""
    }
    val initCode = {
      if (from.isDefined)
        s"var $loopVar = " + from.get.toScalaCode(context)
      else if (in.isDefined) {
        val inCode = in.get.toScalaCode(context)
        s"val $iteratorVar = $inCode.iterator"
      } else
        ""
    }
    val yieldInitCode = {
      if (isYield && pushdownYieldVar.isEmpty) {
        val yieldType = elaboratedType.typeParameters.head
        s"val $yieldResultVar = new scala.collection.mutable.ArrayBuffer[${yieldType.scalaTypeName}]"
      } else {
        ""
      }
    }
    val checkIfDoneCode = {
      if (to.isDefined)
        s"$continueVar = $loopVar <= " + to.get.toScalaCode(context)
      else if (in.isDefined)
        s"$continueVar = $iteratorVar.hasNext"
      else
        ""
    }
    val iterateCode = {
      if (in.isDefined)
        s"val $loopVar = $iteratorVar.next()"
      else
        ""
    }
    val byCode = by map { _.toScalaCode(context) } getOrElse "org.cgsuite.core.Values.one"
    val incrementCode = if (from.isDefined) s"$loopVar = $loopVar + $byCode" else ""
    val whileCode = `while` map { s"$continueVar = " + _.toScalaCode(context) } getOrElse ""
    val whereCode = where map { _.toScalaCode(context) } getOrElse "true"
    val bodyCode = pushDownYield match {
      case Some(loopBody) => loopBody.toScalaCode(context, Some(yieldResultVar))
      case None => body.toScalaCode(context)
    }
    val yieldUpdateCode = {
      if (isYield && pushDownYield.isEmpty)
        s"$yieldResultVar += $tempResultVar"
      else
        ""
    }
    val yieldReturnCode = loopType match {
      case LoopNode.Do => "null"
      case LoopNode.YieldList => s"$yieldResultVar.toVector"
      case LoopNode.YieldMap => s"$yieldResultVar.asInstanceOf[scala.collection.mutable.ArrayBuffer[(Any, Any)]].toMap"
      case LoopNode.YieldSet => s"$yieldResultVar.toSet"
        /*
      case LoopNode.YieldTable => Table { buffer.toIndexedSeq map {
        case list: IndexedSeq[_] => list
        case _ => throw EvalException("A `tableof` expression must generate exclusively objects of type `cgsuite.lang.List`.")
      } } (OutputBuilder.toOutput)
      case LoopNode.YieldSum => r
         */
    }

    s"""{
       |  var $continueVar = true
       |  $initCode
       |  $yieldInitCode
       |  while ($continueVar) {
       |    if (Thread.interrupted())
       |      throw org.cgsuite.exception.CalculationCanceledException("Calculation canceled by user."/*, token = Some(token)*/)
       |    $checkIfDoneCode
       |    if ($continueVar) {
       |      $iterateCode
       |      $whileCode
       |      if ($continueVar) {
       |        if ($whereCode) {
       |          val $tempResultVar = {
       |            $bodyCode
       |          }
       |          $yieldUpdateCode
       |        }
       |        $incrementCode
       |      }
       |    }
       |  }
       |  $yieldReturnCode
       |}
       |""".stripMargin

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    children foreach { node => if (!(forId contains node)) node.collectMentionedClasses(classes) }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val loopTypeStr = loopType match {
      case LoopNode.Do => "do"
      case LoopNode.YieldList => "yield"
      case LoopNode.YieldMap => "mapof"
      case LoopNode.YieldSet => "setof"
      case LoopNode.YieldTable => "tableof"
      case LoopNode.YieldSum => "sumof"
    }
    val antecedent = Seq(
      forId map { "for " + _.id.name },
      in map { "in " + _.toNodeString },
      from map { "from " + _.toNodeString },
      to map { "to " + _.toNodeString },
      by map { "by " + _.toNodeString },
      `while` map { "while " + _.toNodeString },
      where map { "where " + _.toNodeString },
      Some(loopTypeStr)
      ).flatten.mkString(" ")
    antecedent + " " + body.toNodeString + " end"
  }

}

object ProcedureNode {
  def apply(tree: Tree, pkg: Option[CgscriptPackage]): ProcedureNode = {
    val parameters = ParametersNode(tree.head, pkg).toParameters
    ProcedureNode(tree, parameters, EvalNode(tree.children(1)))
  }
}

case class ProcedureNode(tree: Tree, parameters: Vector[Parameter], body: EvalNode) extends EvalNode {

  override val children = (parameters flatMap { _.defaultValue }) :+ body

  override def toNodeStringPrec(enclosingPrecedence: Int) = ???

}

case class ErrorNode(tree: Tree, msg: EvalNode) extends EvalNode {

  override val children = Seq(msg)

  override def elaborateImpl(domain: ElaborationDomain) = {
    // TODO Type check or type convert String
    msg.ensureElaborated(domain)
    CgscriptType(CgscriptClass.NothingClass)
  }

  override def toScalaCode(context: CompileContext) = {
    val msgCode = msg.toScalaCode(context)
    s"throw org.cgsuite.exception.EvalException($msgCode.toString)"
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = s"error(${msg.toNodeString})"

}

case class DotNode(tree: Tree, obj: EvalNode, idNode: IdentifierNode) extends EvalNode {

  override val children = Seq(obj, idNode)

  val antecedentAsPackagePath: Option[Seq[String]] = obj match {
    case IdentifierNode(_, antecedentId) => Some(Seq(antecedentId.name))
    case node: DotNode => node.antecedentAsPackagePath.map { _ :+ node.idNode.id.name }
    case _ => None
  }

  val antecedentAsPackage: Option[CgscriptPackage] = antecedentAsPackagePath flatMap { CgscriptPackage.root.lookupSubpackage }
  var isElaboratedAsPackage: Boolean = _
  var externalName: String = _
  var constantVar: CgscriptClass#Var = _

  override def elaborateImpl(domain: ElaborationDomain): CgscriptType = {

    // TODO Don't elaborate as package if shadowed by a variable
    elaborateAsPackageReference(domain) match {
      case Some(typ) =>
        isElaboratedAsPackage = true
        typ
      case None =>
        isElaboratedAsPackage = false
        elaborateAsObjectReference(domain)
    }

  }

  private[lang2] def elaborateAsPackageReference(domain: ElaborationDomain): Option[CgscriptType] = {

    antecedentAsPackage flatMap { pkg =>

      pkg.lookupClass(idNode.id) match {

        case Some(cls) =>
          externalName = idNode.id.name
          Some(CgscriptType(CgscriptClass.Class, Vector(CgscriptType(cls))))

        case None =>

          pkg.lookupConstantVar(idNode.id) match {

            case Some(constantVar: CgscriptClass#Var) =>
              externalName = idNode.id.name.updated(0, idNode.id.name(0).toLower)
              this.constantVar = constantVar
              constantVar.ensureElaborated()
              Some(constantVar.resultType)

            // TODO Nested Classes

            case None => None

          }

      }

    }

  }

  private[lang2] def elaborateAsObjectReference(domain: ElaborationDomain): CgscriptType = {

    val objectType = obj.ensureElaborated(domain)

    resolveElaboratedMember(objectType) match {

      case Some(member) =>
        externalName = idNode.id.name.updated(0, idNode.id.name(0).toLower)
        member.ensureElaborated()

      case None =>

        val staticResolution = {
          if (objectType.baseClass == CgscriptClass.Class)
            resolveStaticMember(objectType.typeParameters.head)
          else
            None
        }

        staticResolution match {
          case Some(member) =>
            externalName = idNode.id.name
            member.ensureElaborated()
          case None =>
            sys.error(s"need error msg here: ${objectType.baseClass.qualifiedName}.${idNode.id.name}")
        }

    }

  }

  private def resolveElaboratedMember(objectType: CgscriptType): Option[Member] = {

    val objectMethod = objectType.baseClass.lookupMethod(idNode.id, Vector.empty)

    objectMethod match {
      case Some(method) if method.autoinvoke => Some(method)
      case None => objectType.baseClass.lookupVar(idNode.id)
    }

  }

  private def resolveStaticMember(classType: CgscriptType): Option[Member] = {

    // TODO static method
    // TODO refactor!!
    val staticVar = classType.baseClass.classInfo.staticVars find { _.id == idNode.id }

    staticVar orElse {
      classType.baseClass.classInfo.enumElements find { _.id == idNode.id }
    }

  }

  override def toScalaCode(context: CompileContext) = {

    assert(externalName != null, this)
    if (isElaboratedAsPackage) {
      if (constantVar != null) {
        s"${constantVar.declaringClass.scalaClassname}.$externalName"
      } else {
        elaboratedType.typeParameters.head.scalaTypeName
      }
    } else {
      val objStr = obj.toScalaCode(context)
      s"($objStr).$externalName"
    }

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    if (!isElaboratedAsPackage) {
      obj.collectMentionedClasses(classes)
    }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${obj.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString}"
    else
      s"(${obj.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString})"
  }
}

object InfixOpNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSiteNode = DotNode(tree, EvalNode(tree.children.head), IdentifierNode(tree))
    FunctionCallNode(tree, callSiteNode, Vector(EvalNode(tree.children(1))), Vector(None))
  }
}

object FunctionCallNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSite = EvalNode(tree.head)
    val argsWithNames = tree.children(1).children.map { t =>
      t.getType match {
        case BIGRARROW => (EvalNode(t.children(1)), Some(IdentifierNode(t.head)))
        case _ => (EvalNode(t), None)
      }
    }
    val (args, argNames) = argsWithNames.unzip
    FunctionCallNode(tree, callSite, args.toVector, argNames.toVector)
  }

  def lookupMethod(objectType: CgscriptType, methodId: Symbol, argTypes: Vector[CgscriptType]): Option[CgscriptClass#Method] = {

    objectType.baseClass.lookupMethod(methodId, argTypes) orElse {

      // Try various types of implicit conversions. This is a bit of a hack to handle
      // Rational -> DyadicRational -> Integer conversions in a few places. In later versions, this might be
      // replaced by a more elegant / general solution.

      argTypes.length match {

        case 0 =>
          val implicits = availableImplicits(objectType)
          val validImplicits = implicits find { implObjectType =>
            implObjectType.baseClass.lookupMethod(methodId, Vector.empty).isDefined
          }
          validImplicits flatMap { implObjectType =>
            implObjectType.baseClass.lookupMethod(methodId, Vector.empty)
          }

        case 1 =>
          val implicits = {
            for {
              implObjectType <- availableImplicits(objectType)
              implArgType <- availableImplicits(argTypes.head)
            } yield {
              (implObjectType, implArgType)
            }
          }
          val validImplicits = implicits find { case (implObjectType, implArgType) =>
            implObjectType.baseClass.lookupMethod(methodId, Vector(implArgType)).isDefined
          }
          validImplicits flatMap { case (implObjectType, implArgType) =>
            implObjectType.baseClass.lookupMethod(methodId, Vector(implArgType))
          }

        case _ =>
          None

      }

    }

  }

  def availableImplicits(typ: CgscriptType): Vector[CgscriptType] = {

    typ.baseClass match {
      case CgscriptClass.Rational => Vector(typ, CgscriptType(CgscriptClass.DyadicRational), CgscriptType(CgscriptClass.Integer))
      case CgscriptClass.DyadicRational => Vector(typ, CgscriptType(CgscriptClass.Integer))
      case _ => Vector(typ)
    }

  }

}

case class FunctionCallNode(
  tree: Tree,
  callSiteNode: EvalNode,
  argNodes: Vector[EvalNode],
  argNames: Vector[Option[IdentifierNode]]
  ) extends EvalNode {

  override val children = argNodes ++ argNames.flatten :+ callSiteNode

  var constantMethod: Option[CgscriptClass#Method] = None
  var localMethod: Option[CgscriptClass#Method] = None
  var isEval: Boolean = false
  var isCallSiteElaborated: Boolean = false

  override def elaborateImpl(domain: ElaborationDomain) = {

    val argTypes = argNodes map { _.ensureElaborated(domain) }

    val methodTypeOpt = callSiteNode match {

      case idNode: IdentifierNode =>

        // CASE 1:
        // A bare identifier. It could be a local method call, a method call to imported constants,
        // or just an object in scope whose Eval method is being called implicitly.

        // To be a local method call, we need to be in class scope.
        val localMethod = domain.cls flatMap { cls =>
          FunctionCallNode.lookupMethod(CgscriptType(cls), idNode.id, argTypes)
        }

        localMethod match {

          case Some(method) if !method.autoinvoke =>
            this.localMethod = localMethod
            Some(method.ensureElaborated())

          case _ =>

            // Now see if it's an imported constant. (TODO We really want to do this last -
            // that will require some refactoring)
            CgscriptPackage.lookupConstantMethod(idNode.id, argTypes) match {

              case Some(method) if !method.autoinvoke =>
                constantMethod = Some(method)
                Some(method.ensureElaborated())

              case _ =>
                None

            }

        }

      case dotNode: DotNode =>

        // CASE 2:
        // A dot node. It could be a method call on an object, a method call from
        // explicitly specified package constants, or just an object whose Eval method
        // is being called.

        // TODO Don't elaborate as package if shadowed by a variable

        dotNode.antecedentAsPackage match {

          case Some(pkg) =>

            pkg.lookupConstantMethod(dotNode.idNode.id, argTypes) match {

              case Some(method) if !method.autoinvoke =>
                constantMethod = Some(method)
                Some(method.ensureElaborated())

              case _ =>
                None

            }

          case None =>

            val objType = dotNode.obj.ensureElaborated(domain)
            val objMethod = objType.baseClass.lookupMethod(dotNode.idNode.id, argTypes)

            objMethod match {

              case Some(method) if !method.autoinvoke =>
                dotNode.externalName = method.scalaName
                Some(method.ensureElaborated())

              case _ =>
                None

            }

        }

      case _ =>
        None

    }

    methodTypeOpt match {

      case Some(methodType) => methodType

      case None =>

        isCallSiteElaborated = true
        val callSiteType = callSiteNode.ensureElaborated(domain)
        if (callSiteType.baseClass == CgscriptClass.Class) {
          callSiteType.typeParameters.head.baseClass.constructor match {
            case Some(constructor) => constructor.ensureElaborated()
            case None => throw EvalException(s"Class cannot be directly instantiated: ${callSiteType.typeParameters.head.baseClass.qualifiedName}")
          }
        } else {
          // Eval method
          val evalMethod = callSiteType.baseClass.lookupMethod('Eval, argTypes) getOrElse {
            throw EvalException("No method `Eval`") // TODO Better error msg
          }
          isEval = true
          evalMethod.ensureElaborated()
        }

    }

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    // Skip over dotNode or idNode  TODO We might not always want to do this
    val childNode = {
      if (isCallSiteElaborated)
        Some(callSiteNode)
      else {
        callSiteNode match {
          case _: IdentifierNode => None
          case _: DotNode if constantMethod.isDefined => None
          case dotNode: DotNode => Some(dotNode.obj)
          case _ => Some(callSiteNode)
        }
      }
    }
    (argNodes ++ childNode) foreach { _.collectMentionedClasses(classes) }
    constantMethod foreach { method =>
      addTypeToClasses(classes, CgscriptType(method.declaringClass))
    }

  }

  override def toScalaCode(context: CompileContext) = {

    // TODO arg names

    val functionCode = constantMethod match {
      case Some(method) => method.declaringClass.scalaClassname + "." + method.scalaName
      case None =>
        localMethod match {
          case Some(method) => method.scalaName
          case None => callSiteNode.toScalaCode(context)
        }
    }
    val evalCode = if (isEval) ".eval" else ""
    val argsCode = argNodes map { _.toScalaCode(context) } mkString ", "
    s"($functionCode$evalCode($argsCode))"

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val argStr = argNodes map { _.toNodeString } mkString ", "
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr)"
    else
      s"(${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr))"
  }

}

object FunctionCallResolution {

  def apply(callSite: CallSite, argNames: IndexedSeq[Option[IdentifierNode]], referenceToken: Token): FunctionCallResolution = {

    val params = callSite.parameters

    // Last parameter is expandable only if there are no named args
    val expandedLastParameter = params.nonEmpty && params.last.isExpandable && argNames.forall { _.isEmpty }

    // Check for too many arguments
    if (!expandedLastParameter && argNames.length > params.length) {
      throw EvalException(
        s"Too many arguments (${callSite.locationMessage}): ${argNames.length} (expecting at most ${params.length})",
        token = Some(referenceToken)
      )
    }

    // Check for named args in earlier position than ordinary args
    val lastOrdinaryArgIndex = argNames lastIndexWhere { _.isEmpty }
    argNames take (lastOrdinaryArgIndex+1) foreach {
      case None =>
      case Some(idNode) => throw EvalException(
        s"Named parameter `${idNode.id.name}` (${callSite.locationMessage}) " +
          "appears in earlier position than an ordinary argument",
        token = Some(referenceToken)
      )
    }

    val parameterToArgsMapping = new Array[Int](if (expandedLastParameter) params.length - 1 else params.length)
    java.util.Arrays.fill(parameterToArgsMapping, -1)
    argNames.zipWithIndex foreach {
      case (None, index) =>
        if (index < parameterToArgsMapping.length)
          parameterToArgsMapping(index) = index
      case (Some(idNode), index) =>
        val namedIndex = params indexWhere { _.id == idNode.id }
        if (namedIndex == -1)
          throw EvalException(
            s"Invalid parameter name (${callSite.locationMessage}): `${idNode.id.name}`",
            token = Some(referenceToken)
          )
        if (parameterToArgsMapping(namedIndex) != -1)
          throw EvalException(
            s"Duplicate named parameter (${callSite.locationMessage}): `${idNode.id.name}`",
            token = Some(referenceToken)
          )
        parameterToArgsMapping(namedIndex) = index
    }

    // Check that all required args are present
    params zip parameterToArgsMapping foreach { case (param, index) =>
      if (param.defaultValue.isEmpty && index == -1)
        throw EvalException(
          s"Missing required parameter (${callSite.locationMessage}): `${param.id.name}`",
          token = Some(referenceToken)
        )
    }

    FunctionCallResolution(parameterToArgsMapping, expandedLastParameter)

  }
}

case class FunctionCallResolution(parameterToArgsMapping: Array[Int], expandedLastParameter: Boolean)

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

case class AssignToNode(tree: Tree, idNode: IdentifierNode, expr: EvalNode, declType: AssignmentDeclType.Value) extends EvalNode {

  val varName = idNode.id.name

  override def elaborateImpl(domain: ElaborationDomain) = {

    val exprType = expr.ensureElaborated(domain)

    // If we're package-external (Worksheet/REPL scope) and scopeStack has size one (we're not
    // in any nested subscope), then we treat idNode as a dynamic var.
    if (declType == AssignmentDeclType.VarDecl || domain.isToplevelWorksheet) {
      domain.insertId(idNode.id, exprType)
    }

    exprType

  }

  override def toScalaCode(context: CompileContext) = {

    val exprCode = expr.toScalaCode(context)
    declType match {
      case AssignmentDeclType.VarDecl | AssignmentDeclType.ClassVarDecl =>
        s"var $varName: ${elaboratedType.scalaTypeName} = $exprCode; "
      case AssignmentDeclType.Ordinary =>
        s"{ $varName = $exprCode; $varName }"
    }

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {
    addTypeToClasses(classes, elaboratedType)
    expr.collectMentionedClasses(classes)
  }

  // TODO Catch illegal assignment to temporary loop variable (during elaboration)
  // TODO Catch illegal assignment to immutable object member (during elaboration)
  // TODO Catch illegal assignment to constant
  override val children = Seq(idNode, expr)

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val varStr = if (declType != AssignmentDeclType.Ordinary) "var " else ""
    val assignStr = s"$varStr${idNode.toNodeString} := ${expr.toNodeStringPrec(OperatorPrecedence.Assign)}"
    if (OperatorPrecedence.Assign <= enclosingPrecedence)
      assignStr
    else
      s"($assignStr)"
  }

}

object AssignmentDeclType extends Enumeration {
  val Ordinary, VarDecl, ClassVarDecl = Value
}

object StatementSequenceNode {
  def apply(tree: Tree, topLevel: Boolean = false): StatementSequenceNode = {
    // Filter out the semicolons (we only care about the last one)
    val filteredChildren = tree.children filterNot { _.getType == SEMI }
    val suppressOutput = tree.children.isEmpty || tree.children.last.getType == SEMI
    StatementSequenceNode(tree, filteredChildren map { EvalNode(_) }, suppressOutput, topLevel = topLevel)
  }
}

case class StatementSequenceNode(tree: Tree, statements: Seq[EvalNode], suppressOutput: Boolean, topLevel: Boolean) extends EvalNode {

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

  override def toScalaCode(context: CompileContext) = {
    statements map { _.toScalaCode(context) } mkString "\n"
  }

  def toScalaCodeWithVarDecls(context: CompileContext) = {
    statements map {
      case assignToNode: AssignToNode =>
        s"val ${assignToNode.idNode.id.name} = { ${assignToNode.expr.toScalaCode(context)} }"
      case node =>
        s"val __result = { ${node.toScalaCode(context)} }"
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

package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.lang._

object IdentifierNode {
  def apply(tree: Tree): IdentifierNode = {
    val idText = tree.getType match {
      case IDENTIFIER | DECL_ID | INFIX_OP => tree.getText
      case OP | DECL_OP =>
        tree.children.head.getType match {
          case UNARY => s"op unary${tree.children.head.children.head.getText}"
          case _ => s"op ${tree.children.head.getText}"
        }
      case _ => ""
    }
    assert(idText.nonEmpty, tree.getText)
    IdentifierNode(tree, Symbol(idText))
  }
}

case class IdentifierNode(tree: Tree, id: Symbol) extends EvalNode {

  var resolver: Resolver = Resolver.forId(id)
  var constantResolution: Resolution = _
  var classResolution: Option[Any] = None

  // We cache these separately - that provides for faster resolution than
  // using a matcher.
  var localVariableReference: LocalVariableReference = _
  var classVariableReference: ClassVariableReference = _

  override val children = Vector.empty

  override def elaborate(scope: ElaborationDomain): Unit = {
    // Can this be resolved as a Class name? Check first in local package scope, then in default package scope
    scope.pkg flatMap { _ lookupClass id } orElse (CgscriptPackage lookupClass id) match {
      case Some(cls) => classResolution = {
        Some(if (cls.isScript) cls.scriptObject else if (cls.isSingleton) cls.singletonInstance else cls.classObject)
      }
      case None =>
    }
    // Can this be resolved as a scoped variable?
    scope lookup id match {
      case Some(l@LocalVariableReference(_, _)) => localVariableReference = l
      case Some(c@ClassVariableReference(_, _)) => classVariableReference = c
      case None =>
    }
    // Can this be resolved as a constant? Check first in local package scope, then in default package scope
    scope.pkg flatMap { _ lookupConstant id } orElse (CgscriptPackage lookupConstant id) match {
      case Some(res) => constantResolution = res
      case None =>
    }
    // If there's no possible resolution and we're inside a package (i.e., not at Worksheet scope),
    // we can throw an exception now
    if (classResolution.isEmpty && localVariableReference == null && classVariableReference == null &&
      constantResolution == null && scope.pkg.isDefined) {
      throw EvalException(s"That variable is not defined: `${id.name}`", token = Some(token))
    }
  }

  override def evaluate(domain: EvaluationDomain) = evaluate(domain, asFunctionCallAntecedent = false)

  def evaluate(domain: EvaluationDomain, asFunctionCallAntecedent: Boolean) = {
    // Try resolving in the following precedence order:
    // (1) As a class name;
    // (2) As a local (method-scope) variable;
    // (3) [Inside a package] As a member variable of the context object or
    //     [Worksheet scope] As a global (Worksheet) variable;
    // (4) As a package constant
    if (classResolution.isDefined) {
      // Class name
      classResolution.get
    } else if (localVariableReference != null) {
      // Local var
      domain backref localVariableReference.domainHops localScope localVariableReference.index
    } else {
      lookupLocally(domain, asFunctionCallAntecedent) match {
        case Some(x) => x     // Member/Worksheet var
        case None =>
          if (constantResolution != null) {
            // Constant
            constantResolution evaluateFor (constantResolution.cls.singletonInstance, asFunctionCallAntecedent, token)
          } else {
            throw EvalException(s"That variable is not defined: `${id.name}`", token = Some(token))
          }
      }
    }
  }

  private[this] def lookupLocally(domain: EvaluationDomain, asFunctionCallAntecedent: Boolean): Option[Any] = {
    if (domain.isOuterDomain) {
      domain getDynamicVar id
    } else if (classVariableReference != null) {
      val backrefObject = domain nestingBackrefContextObject classVariableReference.nestingHops
      Some(classVariableReference.resolver resolve (backrefObject, asFunctionCallAntecedent, token))
    } else {
      None
    }
  }

  def toNodeStringPrec(enclosingPrecedence: Int) = id.name

}

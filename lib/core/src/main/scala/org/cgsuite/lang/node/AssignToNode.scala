package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang._

case class AssignToNode(tree: Tree, idNode: IdentifierNode, expr: EvalNode, declType: AssignmentDeclType.Value) extends EvalNode {
  // TODO Catch illegal assignment to temporary loop variable (during elaboration)
  // TODO Catch illegal assignment to immutable object member (during elaboration)
  // TODO Catch illegal assignment to constant
  override val children = Vector(idNode, expr)
  override def elaborate(scope: ElaborationDomain) {
    // If we're package-external (Worksheet/REPL scope) and scopeStack has size one (we're not
    // in any nested subscope), then we treat idNode as a dynamic var.
    if (declType == AssignmentDeclType.VarDecl && !scope.isToplevelWorksheet) {
      scope.insertId(idNode)
    }
    super.elaborate(scope)
  }
  override def evaluate(domain: EvaluationDomain) = {
    val newValue = expr.evaluate(domain)
    if (idNode.classResolution.isDefined) {
      throw EvalException(s"Cannot assign to class name as variable: `${idNode.id.name}`", token = Some(token))
    } else if (idNode.localVariableReference != null) {
      val refDomain = domain backref idNode.localVariableReference.domainHops
      refDomain.localScope(idNode.localVariableReference.index) = newValue
    } else if (domain.isOuterDomain) {
      domain.putDynamicVar(idNode.id, newValue)
    } else {
      // TODO Nested classes
      val res = idNode.resolver.findResolution(domain.contextObject.get)
      if (res.classScopeIndex >= 0) {
        if (declType == AssignmentDeclType.Ordinary && !res.isMutableVar)
          throw EvalException(s"Cannot reassign to immutable var: `${idNode.id.name}`", token = Some(token))
        val stdObj = domain.contextObject.get.asInstanceOf[StandardObject]
        if (!stdObj.cls.isMutable && (CgscriptClass of newValue).isMutable)
          throw EvalException(
            s"Cannot assign mutable object to var `${idNode.id.name}` of immutable class `${stdObj.cls.qualifiedName}`",
            token = Some(token)
          )
        stdObj.vars(res.classScopeIndex) = newValue.asInstanceOf[AnyRef]
      } else
        throw EvalException(s"Unknown variable for assignment: `${idNode.id.name}`", token = Some(token))
    }
    newValue
  }
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

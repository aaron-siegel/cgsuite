package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.{CgsuiteException, EvalException}
import org.cgsuite.lang._

case class DotNode(tree: Tree, obj: EvalNode, idNode: IdentifierNode) extends EvalNode {
  override val children = Vector(obj, idNode)
  val antecedentAsPackagePath: Option[Vector[String]] = obj match {
    case IdentifierNode(_, antecedentId) => Some(Vector(antecedentId.name))
    case node: DotNode => node.antecedentAsPackagePath.map { _ :+ node.idNode.id.name }
    case _ => None
  }
  val antecedentAsPackage: Option[CgscriptPackage] = antecedentAsPackagePath flatMap { CgscriptPackage.root.lookupSubpackage }
  var isElaborated = false
  var classResolution: CgscriptClass = _
  var constantResolution: Resolution = _
  override def elaborate(scope: ElaborationDomain) {
    antecedentAsPackage flatMap { _.lookupClass(idNode.id) } match {
      case Some(cls) => classResolution = cls
      case None =>
        antecedentAsPackage flatMap { _.lookupConstant(idNode.id) } match {
          case Some(res) => constantResolution = res
          case None => obj.elaborate(scope)     // Deliberately bypass idNode
        }
    }
    isElaborated = true
  }
  override def evaluate(domain: EvaluationDomain): Any = evaluate(domain, asFunctionCallAntecedent = false)
  def evaluate(domain: EvaluationDomain, asFunctionCallAntecedent: Boolean): Any = {
    if (!isElaborated) {
      sys error s"Node has not been elaborated: $this"
    }
    if (classResolution != null) {
      if (classResolution.isSingleton) classResolution.singletonInstance else classResolution.classObject
    } else if (constantResolution != null) {
      constantResolution evaluateFor (constantResolution.cls.singletonInstance, asFunctionCallAntecedent, token)
    } else {
      val x = obj.evaluate(domain)
      try {
        val resolution = idNode.resolver.findResolution(x)
        if (resolution.isResolvable) {
          resolution.evaluateFor(x, asFunctionCallAntecedent, token)
        } else {
          throw EvalException(
            s"Not a method or member variable: `${idNode.id.name}` (in object of class `${(CgscriptClass of x).qualifiedName}`)",
            token = Some(token)
          )
        }
      } catch {
        case exc: CgsuiteException if exc.tokenStack.isEmpty =>
          exc.tokenStack += token
          throw exc
      }
    }
  }
  def toNodeStringPrec(enclosingPrecedence: Int) = {
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${obj.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString}"
    else
      s"(${obj.toNodeStringPrec(OperatorPrecedence.Postfix)}.${idNode.toNodeString})"
  }
}

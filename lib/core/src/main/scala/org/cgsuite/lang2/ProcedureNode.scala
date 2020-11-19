package org.cgsuite.lang2

import org.antlr.runtime.tree.Tree
import org.apache.commons.text.StringEscapeUtils
import org.cgsuite.exception.EvalException
import org.cgsuite.lang2.Node.treeToRichTree


object ProcedureNode {
  def apply(tree: Tree, pkg: Option[CgscriptPackage]): ProcedureNode = {
    val parametersNode = ParametersNode(tree.head, pkg)
    ProcedureNode(tree, parametersNode, EvalNode(tree.children(1)))
  }
}

case class ProcedureNode(tree: Tree, parametersNode: ParametersNode, body: EvalNode) extends EvalNode {

  var parameters: Vector[Parameter] = _

  def arity = parameters.length

  override val children = (parametersNode.parameterNodes flatMap { _.defaultValue }) :+ body

  override def toNodeStringPrec(enclosingPrecedence: Int) = {
    val parameterStrings = parameters map { param =>
      s"${param.name} as ${param.paramType.qualifiedName}"
    }
    val parametersString = arity match {
      case 1 => parameterStrings.head
      case _ => "(" + parameterStrings.mkString(", ") + ")"
    }
    val bodyString = body.toNodeStringPrec(OperatorPrecedence.FunctionDef)
    s"$parametersString -> $bodyString"
  }

  override def elaborateImpl(domain: ElaborationDomain) = {

    parameters = parametersNode.toParameters(domain)

    domain.pushScope()
    parameters foreach { param =>
      if (domain.isDefinedInLocalScope(param.id)) {
        throw EvalException(s"Duplicate symbol: `${param.id.name}`", token = Some(param.idNode.token))
      }
      domain.insertId(param.id, param.paramType)
      param.defaultValue foreach { _.ensureElaborated(domain) }
    }
    val resultType = body.ensureElaborated(domain)
    domain.popScope()

    ConcreteType(CgscriptClass.Procedure, (parameters map { _.paramType }) :+ resultType)

  }

  override def toScalaCode(context: CompileContext) = {

    val paramNames = parameters map { _.id.name } mkString ", "

    val paramTypeNames = parameters map { _.paramType.scalaTypeName } mkString ", "

    val paramNamesCode = parameters.length match {
      case 0 => "_"
      case 1 => paramNames
      case _ => s"case($paramNames)"
    }

    val typeCode = parameters.length match {
      case 0 => "AnyRef"
      case 1 => paramTypeNames
      case _ => s"($paramTypeNames)"
    }

    val bodyCode = body.toScalaCode(context)

    val escapedNodeString = StringEscapeUtils.ESCAPE_JAVA.translate(toNodeString)

    s"""org.cgsuite.lang2.Procedure[$typeCode, ${body.elaboratedType.scalaTypeName}]($arity, "$escapedNodeString"){ $paramNamesCode => { $bodyCode } }"""

  }

}

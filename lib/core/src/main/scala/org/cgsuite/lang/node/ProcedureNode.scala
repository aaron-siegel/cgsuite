package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.apache.commons.text.StringEscapeUtils
import org.cgsuite.exception.EvalException
import org.cgsuite.lang._
import org.cgsuite.lang.parser.RichTree.treeToRichTree


object ProcedureNode {
  def apply(tree: Tree, pkg: Option[CgscriptPackage]): ProcedureNode = {
    val parametersNode = ParametersNode(tree.head, pkg)
    ProcedureNode(tree, parametersNode, EvalNode(tree.children(1)))
  }
}

case class ProcedureNode(tree: Tree, parametersNode: ParametersNode, body: EvalNode) extends EvalNode {

  var parameters: Vector[Parameter] = _

  def arity = parameters.length

  val hasUnspecifiedTypes = parametersNode.parameterNodes exists { _.typeSpecifier.isEmpty }

  if (hasUnspecifiedTypes && (parametersNode.parameterNodes exists { _.typeSpecifier.nonEmpty })) {
    sys.error("Mixed parameter types (specified & unspecified) not currently supported")
  }

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

  override def elaborateImpl(domain: ElaborationDomain): ConcreteType = {

    parameters = parametersNode.toParameters(domain)

    domain.pushScope()
    parameters foreach { param =>
      if (domain.isDefinedInLocalScope(param.id)) {
        throw EvalException(s"Duplicate symbol: `${param.id.name}`", token = Some(param.idNode.token))
      }
      domain.insertId(param.id, param.paramType)
      assert(param.defaultValue.isEmpty)
    }
    val resultType = body.ensureElaborated(domain)
    domain.popScope()

    ConcreteType(CgscriptClass.Procedure, (parameters map { _.paramType }) :+ resultType)

  }

  override def elaborateImplWithInferredType(domain: ElaborationDomain, inferredType: CgscriptType): ConcreteType = {

    inferredType match {
      case ConcreteType(CgscriptClass.Procedure, typeArguments, _) =>

        assert(typeArguments.nonEmpty)      // Must have a result type

        parameters = parametersNode.toParameters(domain, Some(typeArguments.dropRight(1)))

        domain.pushScope()
        parameters zip typeArguments foreach { case (param, inferredParamType) =>
          if (domain.isDefinedInLocalScope(param.id)) {
            throw EvalException(s"Duplicate symbol: `${param.id.name}`", token = Some(param.idNode.token))
          }
          domain.insertId(param.id, inferredParamType)
          assert(param.defaultValue.isEmpty)
        }
        val resultType = body.ensureElaborated(domain)
        domain.popScope()

        val inferredResultType = typeArguments.last
        if (resultType matches inferredResultType) {
          ConcreteType(CgscriptClass.Procedure, typeArguments.dropRight(1) :+ resultType)
        } else {
          throw EvalException("need a good error msg")
        }

      case _ =>
        sys.error("This should never happen (type match should have failed earlier)")
    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    assert(parameters != null, tree.toStringTree)

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

    val escapedNodeString = StringEscapeUtils.ESCAPE_JAVA.translate(toNodeString)

    emitter println s"""org.cgsuite.lang.Procedure[$typeCode, ${body.elaboratedType.scalaTypeName}]($arity, "$escapedNodeString") { $paramNamesCode => {"""
    emitter.indent()
    body.emitScalaCode(context, emitter)
    emitter println ""
    emitter.indent(-1)
    emitter println "}}"

  }

}

package org.cgsuite.lang

import java.util

import org.cgsuite.lang.node.FunctionDefNode
import org.cgsuite.output.StyledTextOutput.Style
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

case class Function(node: FunctionDefNode, domain: EvaluationDomain) extends CallSite with OutputTarget {

  override def parameters = node.parameters

  override def ordinal = node.ordinal

  override def allowMutableArguments = true

  override def call(args: Array[Any]) = {
    val newDomain = new EvaluationDomain(new Array[Any](node.localVariableCount), domain.contextObject, domain.dynamicVarMap, Some(domain))
    var i = 0
    while (i < node.parameters.length) {
      newDomain.localScope(node.parameters(i).methodScopeIndex) = args(i)
      i += 1
    }
    node.body.evaluate(newDomain)
  }

  override def referenceToken = Some(node.token)

  override def locationMessage = "in function call"

  override def toOutput: StyledTextOutput = new StyledTextOutput(util.EnumSet.of(Style.FACE_MONOSPACED), node.toNodeString)

}

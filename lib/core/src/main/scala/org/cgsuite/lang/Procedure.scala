package org.cgsuite.lang

import java.util

import org.cgsuite.output.StyledTextOutput.Style
import org.cgsuite.output.{OutputTarget, StyledTextOutput}

case class Procedure(node: ProcedureNode, domain: EvaluationDomain) extends CallSite with OutputTarget {

  def parameters = node.parameters
  def ordinal = node.ordinal
  def call(args: Array[Any]) = {
    val newDomain = new EvaluationDomain(new Array[Any](node.localVariableCount), domain.contextObject, domain.dynamicVarMap, Some(domain))
    CallSite.validateArguments(parameters, args, node.knownValidArgs, locationMessage)
    var i = 0
    while (i < node.parameters.length) {
      newDomain.localScope(node.parameters(i).methodScopeIndex) = args(i)
      i += 1
    }
    node.body.evaluate(newDomain)
  }

  def referenceToken = Some(node.token)
  def locationMessage = "in procedure call"

  def toOutput: StyledTextOutput = new StyledTextOutput(util.EnumSet.of(Style.FACE_MONOSPACED), node.toNodeString)

}

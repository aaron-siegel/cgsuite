package org.cgsuite.lang

import org.cgsuite.output.{OutputTarget, StyledTextOutput}

case class Procedure(node: ProcedureNode, domain: Domain) extends CallSite with OutputTarget {

  def parameters = node.parameters
  def ordinal = node.ordinal
  def call(args: Array[Any]) = {
    val newDomain = new Domain(new Array[Any](node.localVariableCount), domain.contextObject, domain.dynamicVarMap, Some(domain))
    CallSite.validateArguments(parameters, args, node.knownValidArgs, locationMessage)
    var i = 0
    while (i < node.parameters.length) {
      newDomain.localScope(node.parameters(i).methodScopeIndex) = args(i)
      i += 1
    }
    node.body.evaluate(newDomain)
  }
  def locationMessage = "in procedure call"

  def toOutput: StyledTextOutput = new StyledTextOutput(node.toNodeString)

}

package org.cgsuite.lang

import org.cgsuite.output.{OutputTarget, StyledTextOutput}

case class Procedure(node: ProcedureNode, domain: Domain) extends CallSite with OutputTarget {

  // TODO This fails to form a closure over Domain (we need a Stack of Domains to fix that)

  def parameters = node.parameters
  def ordinal = node.ordinal
  def call(args: Array[Any]) = {
    val newDomain = new Domain(new Array[Any](node.localVariableCount), domain.contextObject, domain.dynamicVarMap, Some(domain))
    var i = 0
    while (i < node.parameters.length) {
      newDomain.localScope(node.parameters(i).methodScopeIndex) = args(i)
      i += 1
    }
    node.body.evaluate(newDomain)
  }

  def toOutput: StyledTextOutput = new StyledTextOutput(node.toNodeString)

}

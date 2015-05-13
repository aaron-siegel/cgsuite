package org.cgsuite.lang

case class Procedure(node: ProcedureNode, domain: Domain) extends CallSite {

  // TODO This fails to form a closure over Domain (we need a Stack of Domains to fix that)

  def parameters = node.parameters
  def ordinal = node.ordinal
  def call(args: Array[Any]) = {
    val newDomain = new Domain(new Array[Any](node.localVariableCount), domain.contextObject, domain.dynamicVarMap)
    var i = 0
    while (i < node.parameters.length) {
      newDomain.localScope(node.parameters(i).methodScopeIndex) = args(i)
      i += 1
    }
    node.body.evaluate(newDomain)
  }

}

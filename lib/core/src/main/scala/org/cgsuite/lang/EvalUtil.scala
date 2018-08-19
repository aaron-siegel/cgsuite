package org.cgsuite.lang

import org.antlr.runtime.tree.Tree
import org.cgsuite.output.Output

import scala.collection.mutable

object EvalUtil {

  def evaluate(varMap: mutable.AnyRefMap[Symbol, Any], tree: Tree): Output = {
    println(tree.toStringTree)
    val node = EvalNode(tree.getChild(0))
    val scope = ElaborationDomain(None, Seq.empty, None)
    node.elaborate(scope)
    println(node)
    val domain = new Domain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
    val result = node.evaluate(domain)
    val output = CgscriptClass.of(result).classInfo.toOutputMethod.call(result, Array.empty)
    assert(output.isInstanceOf[Output], output.getClass)
    println(output)
    output.asInstanceOf[Output]
  }

}

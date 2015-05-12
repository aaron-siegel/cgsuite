package org.cgsuite.lang.parser

import org.cgsuite.lang._

import scala.collection.mutable


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object.ensureLoaded()
    val replVarMap = mutable.AnyRefMap[Symbol, Any]()

    while (true) {
      print("> ")
      val str = Console.in.readLine()
      if (str.trim.nonEmpty) {
        val start = System.nanoTime()
        try {
          val tree = ParserUtil.parseStatement(str)
          val node = EvalNode(tree)
          val scope = Scope(None, Set.empty)
          node.elaborate(scope)
          println(tree.toStringTree)
          println(node)
          val domain = new Domain(new Array[Any](scope.varMap.size), dynamicVarMap = Some(replVarMap))
          val result = node.evaluate(domain)
          println(result)
        } catch {
          case exc: Throwable => exc.printStackTrace()
        }
        val totalDuration = System.nanoTime() - start
        println(s"${totalDuration / 1000000} ms")
      }
    }

  }

}

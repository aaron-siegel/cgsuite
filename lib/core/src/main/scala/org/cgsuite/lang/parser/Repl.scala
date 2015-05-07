package org.cgsuite.lang.parser

import org.cgsuite.lang._

import scala.collection.mutable


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object.ensureLoaded()

    val domain = new Domain(null, None)

    while (true) {
      try {
        print("> ")
        val str = Console.in.readLine()
        val tree = ParserUtil.parseStatement(str)
        val node = EvalNode(tree)
        node.elaborate(Scope(Set.empty))
        println(tree.toStringTree)
        println(node)
        val start = System.nanoTime()
        val result = node.evaluate(domain)
        val totalDuration = System.nanoTime() - start
        println(s"${totalDuration/1000000} ms")
        println(result)
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

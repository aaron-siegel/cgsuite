package org.cgsuite.lang.parser

import org.cgsuite.lang._

import scala.collection.mutable


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object.ensureLoaded()

    val domain = new Domain(null, None)

    while (true) {
      print("> ")
      val str = Console.in.readLine()
      if (str.trim.nonEmpty) {
        val start = System.nanoTime()
        try {
          val tree = ParserUtil.parseStatement(str)
          val node = EvalNode(tree)
          node.elaborate(Scope(None, Set.empty))
          println(tree.toStringTree)
          println(node)
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

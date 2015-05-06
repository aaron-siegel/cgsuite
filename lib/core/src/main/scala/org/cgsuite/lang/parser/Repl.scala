package org.cgsuite.lang.parser

import org.cgsuite.lang.{Namespace, Node, Domain, CgsuiteClass}
import org.cgsuite.util.Profiler


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object.ensureLoaded()

    val domain = new Domain(Namespace.checkout(None, Map.empty))

    while (true) {
      try {
        val str = Console.in.readLine()
        val tree = ParserUtil.parseStatement(str)
        val node = Node(tree)
        println(tree.toStringTree)
        println(node)
        Profiler.clear()
        Profiler.setEnabled(enabled = true)
        val start = System.nanoTime()
        Profiler.start('Main)
        val result = node.evaluate(domain)
        Profiler.stop('Main)
        val totalDuration = System.nanoTime() - start
        Profiler.print(0L)
        Profiler.setEnabled(enabled = false)
        println(s"${totalDuration/1000000} ms")
        println(result)
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

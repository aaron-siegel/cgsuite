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
        val start = System.nanoTime()
        Profiler.clear()
        Profiler.setEnabled(enabled = true)
        Profiler.setWhitelist('PrepareLoop,'Loop)
        Profiler.start('Total)
        val result = domain.expression(node)
        Profiler.stop('Total)
        Profiler.print()
        Profiler.setEnabled(enabled = false)
        println(s"${(System.nanoTime()-start)/1000000} ms")
        println(result)
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

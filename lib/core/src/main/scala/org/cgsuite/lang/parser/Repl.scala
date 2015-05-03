package org.cgsuite.lang.parser

import org.cgsuite.lang.{Namespace, Node, Domain, CgsuiteClass}


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
        val startTime = System.nanoTime()
        val result = domain.expression(node)
        val duration = System.nanoTime() - startTime
        println(s"Completed in ${duration/1000000}.${(duration/100000)%10} ms")
        println(result)
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

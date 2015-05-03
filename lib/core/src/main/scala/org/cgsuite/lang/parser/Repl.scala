package org.cgsuite.lang.parser

import org.cgsuite.lang.{Node, Domain, CgsuiteClass}


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object.ensureLoaded()

    val domain = new Domain()

    while (true) {
      try {
        val str = Console.in.readLine()
        val tree = ParserUtil.parseStatement(str)
        val node = Node(tree)
        println(tree.toStringTree)
        println(node)
        println(domain.expression(node))
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

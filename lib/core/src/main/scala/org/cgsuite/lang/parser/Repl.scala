package org.cgsuite.lang.parser

import org.cgsuite.lang.{Domain, CgsuiteClass}


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object.ensureLoaded()

    val domain = new Domain()

    while (true) {
      try {
        val str = Console.in.readLine()
        val tree = ParserUtil.parseStatement(str)
        println(tree.toStringTree)
        println(domain.expression(tree))
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

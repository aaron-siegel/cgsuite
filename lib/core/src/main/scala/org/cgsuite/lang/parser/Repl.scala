package org.cgsuite.lang.parser

import org.cgsuite.lang.CgsuiteClass


object Repl {

  def main(args: Array[String]) {

    CgsuiteClass.Object

    val domain = new Domain()

    while (true) {
      try {
        val str = Console.in.readLine()
        val tree = ParserUtil.parseExpression(str)
        println(domain.expression(tree))
      } catch {
        case exc: Throwable => exc.printStackTrace()
      }
    }

  }

}

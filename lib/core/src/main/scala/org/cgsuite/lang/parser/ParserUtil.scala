package org.cgsuite.lang.parser

import org.antlr.runtime.{CommonTokenStream, ANTLRStringStream}


object ParserUtil {

  def parseExpression(str: String) = stringToParser(str).expression.getTree.asInstanceOf[CgsuiteTree]
  def parseStatement(str: String) = stringToParser(str).statement.getTree.asInstanceOf[CgsuiteTree]

  def stringToParser(str: String) = {

    val input = new ANTLRStringStream(str)
    val lexer = new CgsuiteLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor())
    parser

  }

}

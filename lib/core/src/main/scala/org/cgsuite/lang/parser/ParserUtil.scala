package org.cgsuite.lang.parser

import java.io.{ByteArrayInputStream, InputStream}

import org.antlr.runtime.tree.Tree
import org.antlr.runtime.{CharStream, CommonTokenStream}


object ParserUtil {

  def parseExpression(str: String) = parse(new ByteArrayInputStream(str.getBytes), "Worksheet") { _.expression }
  def parseStatement(str: String) = parse(new ByteArrayInputStream(str.getBytes), "Worksheet") { _.statement }
  def parseCU(in: InputStream, source: String) = parse(in, source) { _.compilationUnit }

  def charStreamToParser(input: CharStream) = {

    val lexer = new CgsuiteLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor())
    parser

  }

  private def parse(in: InputStream, source: String)(fn: CgsuiteParser => { def getTree(): Object }) = {

    val stream = new SourcedAntlrInputStream(in, source)
    val lexer = new CgsuiteLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor())

    val tree = fn(parser).getTree().asInstanceOf[Tree]

    if (!lexer.getErrors.isEmpty) {
      sys.error(lexer.getErrors.toString)
    } else if (!parser.getErrors.isEmpty) {
      sys.error(parser.getErrors.toString)
    } else {
      tree
    }

  }

}

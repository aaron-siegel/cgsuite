package org.cgsuite.lang.parser

import java.io.{ByteArrayInputStream, InputStream}

import org.antlr.runtime.tree.Tree
import org.antlr.runtime.{CharStream, CommonTokenStream}
import org.cgsuite.exception.InputException
import scala.collection.JavaConversions._

import scala.language.reflectiveCalls

object ParserUtil {

  def parseExpression(str: String) = parse(new ByteArrayInputStream(str.getBytes), "Worksheet") { _.expression }
  def parseStatement(str: String) = parse(new ByteArrayInputStream(str.getBytes), "Worksheet") { _.statement }
  def parseScript(str: String) = parse(new ByteArrayInputStream(str.getBytes), "Worksheet") { _.script }
  def parseCU(in: InputStream, source: String) = parse(in, source) { _.compilationUnit }

  def charStreamToParser(input: CharStream) = {

    val lexer = new CgsuiteLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor())
    parser

  }

  private def parse(in: InputStream, source: String)(fn: CgsuiteParser => { def getTree(): Object }): Tree = {

    val stream = new SourcedAntlrInputStream(in, source)
    val lexer = new CgsuiteLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor())

    val tree = fn(parser).getTree().asInstanceOf[Tree]

    if (!lexer.getErrors.isEmpty) {
      throw InputException("Syntax error: " + lexer.getErrors.get(0).getMessage)
    } else if (!parser.getErrors.isEmpty) {
      val err = parser.getErrors.head
      throw InputException {
        s"Syntax error at ${err.getException.token.getInputStream.getSourceName} line ${err.getException.line}:${err.getException.charPositionInLine}: ${err.getMessage}"
      }
    } else {
      tree
    }

  }

}

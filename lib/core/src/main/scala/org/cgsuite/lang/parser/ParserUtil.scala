package org.cgsuite.lang.parser

import java.io.{ByteArrayInputStream, InputStream}

import org.antlr.runtime.tree.Tree
import org.antlr.runtime.{CharStream, CommonTokenStream}
import org.cgsuite.exception.SyntaxException

import scala.collection.JavaConverters
import scala.language.reflectiveCalls

object ParserUtil {

  def parseExpression(str: String) = parse(new ByteArrayInputStream(str.getBytes), s":Input:$str") { _.expression }
  def parseStatement(str: String) = parse(new ByteArrayInputStream(str.getBytes), s":Input:$str") { _.statement }
  def parseScript(str: String) = parse(new ByteArrayInputStream(str.getBytes), s":Input:$str") { _.script }
  def parseCU(in: InputStream, source: String) = parse(in, source) { _.compilationUnit }

  def charStreamToParser(input: CharStream) = {

    val lexer = new CgsuiteLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor(tokens))
    parser

  }

  private def parse(in: InputStream, source: String)(fn: CgsuiteParser => { def getTree(): Object }): Tree = {

    val stream = new SourcedAntlrInputStream(in, source)
    val lexer = new CgsuiteLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new CgsuiteParser(tokens)
    parser.setTreeAdaptor(new CgsuiteTreeAdaptor(tokens))

    val tree = fn(parser).getTree().asInstanceOf[CgsuiteTree]

    if (!lexer.getErrors.isEmpty) {
      throw SyntaxException(lexer.getErrors.iterator().next().getException, source)
      //throw InputException("Syntax error: " + lexer.getErrors.get(0).getMessage)
    } else if (!parser.getErrors.isEmpty) {
      throw SyntaxException(parser.getErrors.iterator().next().getException, source)
      //s"Syntax error at ${err.getException.token.getInputStream.getSourceName} line ${err.getException.line}:${err.getException.charPositionInLine}: ${err.getMessage}"
    } else {
      tree
    }

  }

}

package org.cgsuite.lang2.parser

import java.io.{ByteArrayInputStream, InputStream}

import org.antlr.runtime.{CharStream, CommonTokenStream}
import org.cgsuite.lang.parser.{CgsuiteLexer, CgsuiteParser}
import org.cgsuite.lang.parser.ParserUtil.parse

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

}

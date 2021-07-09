package org.cgsuite.lang

import org.antlr.runtime.Token
import org.apache.commons.text.StringEscapeUtils

class CompileContext(val generateStackTraceInfo: Boolean) {

  private var nextTempId = 0

  def newTempId(): String = {
    val id = s"__tmp_$nextTempId"
    nextTempId += 1
    id
  }

}

class Emitter {

  private val sb = new StringBuilder()
  private var indentation = 0
  private var atNewline = true

  def indent(by: Int = 1): Unit = {
    indentation += by
    assert(indentation >= 0)
  }

  def print(any: Any): Unit = {
    val str = any.toString
    val lines = str split '\n'
    if (lines.nonEmpty) {
      lines dropRight 1 foreach { append(_, addNewline = true) }
      append(lines.last, str.nonEmpty && str.last == '\n')
    }
  }

  def println(any: Any): Unit = {
    print(any)
    append("", addNewline = true)
  }

  def printTry(): Unit = {
    print("try { ")
  }

  def printCatch(token: Token): Unit = {
    println(" } catch { case exc: org.cgsuite.exception.CgsuiteException =>")
    indent()
    val sourceName = StringEscapeUtils.escapeJava(token.getInputStream.getSourceName)
    val line = token.getLine
    val col = token.getCharPositionInLine
    println(s"""exc.addStackElement("$sourceName", $line, $col); throw exc""")
    indent(-1)
    println("}")
  }

  override def toString = sb.toString

  def toNumberedLines = {
    val str = toString
    val lines = str split '\n'
    lines.zipWithIndex map { case (line, index) =>
      f"$index%4d  $line"
    }
  }

  private def append(str: String, addNewline: Boolean): Unit = {

    if (atNewline)
      appendIndent()

    sb append str

    if (addNewline)
      sb append '\n'

    atNewline = addNewline

  }

  private def appendIndent(): Unit = {

    assert(atNewline)
    assert(indentation >= 0)
    sb append ("  " * indentation)

  }

}

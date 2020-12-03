package org.cgsuite.lang

class CompileContext {

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

  override def toString = sb.toString

  def toStringWithLineNumbers = {
    val str = toString
    val lines = str split '\n'
    val numberedLines = lines.zipWithIndex map { case (line, index) =>
      f"$index%4d  $line"
    }
    numberedLines mkString "\n"
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

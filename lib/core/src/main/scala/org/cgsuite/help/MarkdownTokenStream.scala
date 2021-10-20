package org.cgsuite.help

class MarkdownTokenStream(input: String, stripAsterisks: Boolean = false) {

  val markdownStream = new MarkdownStream(input, stripAsterisks)

  private var inCodeBlock: Boolean = false

  private var nextToken: MarkdownToken = advance()

  def isDone: Boolean = nextToken == Done

  def next: MarkdownToken = {
    val token = nextToken
    nextToken = advance()
    token
  }

  def peek: MarkdownToken = nextToken

  def exhaust(): Unit = {
    markdownStream.exhaust()
    nextToken = Done
  }

  private def advance(): MarkdownToken = {
    if (markdownStream.isDone)
      Done
    else {
      val ch = markdownStream.consume
      ch match {

        case '\\' => nextSpecial

        case '`' => inCodeBlock = !inCodeBlock; ControlSequence("`")

        case '\n' if !inCodeBlock && markdownStream.next == '\n' =>
          markdownStream consumeWhile { _ == '\n' }
          ControlSequence("\n\n")

        case '+' if !inCodeBlock && markdownStream.next == '+' =>
          val token = ControlSequence("+" + markdownStream.consumeWhile { _ == '+' })
          markdownStream consumeWhile { _ == '\n' }     // Swallow paragraph breaks after a section heading
          token

        case '~' if !inCodeBlock && markdownStream.next == '~' =>
          ControlSequence("~" + markdownStream.consume)

        case '~' | '_' | '^' | '$' if !inCodeBlock => ControlSequence(ch.toString)

        case '[' if !inCodeBlock && markdownStream.next == '[' => consumeLink()

        case _ => OrdinaryChar(ch)

      }
    }
  }

  private def nextSpecial: Special = {
    if (markdownStream.next.isLetter) {
      val command = markdownStream consumeWhile { _.isLetter }
      val arg = markdownStream.next match {
        case '{' => Some(consumeSpecialArg)
        case _ => None
      }
      Special(command, arg)
    } else {
      Special(markdownStream.consume.toString, None)
    }
  }

  private def consumeSpecialArg: String = {
    assert(markdownStream.next == '{')
    markdownStream.consume
    var bracesCount = 1
    val buf = new StringBuilder
    while (bracesCount > 0) {
      if (markdownStream.isDone)
        sys.error("runaway special arg")
      val ch = markdownStream.consume
      ch match {
        case '{' => bracesCount += 1
        case '}' => bracesCount -= 1
        case _ =>
      }
      if (bracesCount > 0)
        buf += ch
    }
    buf.toString
  }

  def consumeLink(): Link = {

    val target = consumeLinkElement()
    val text = {
      if (markdownStream.next == '[')
        Some(consumeLinkElement())
      else
        None
    }
    markdownStream consumeWhile { _ != ']' }
    markdownStream.consume

    Link(target, text)

  }

  def consumeLinkElement(): String = {

    assert(markdownStream.consume == '[')
    val str = markdownStream consumeWhile { _ != ']' }
    markdownStream.consume
    str

  }

}

sealed trait MarkdownToken
case object Done extends MarkdownToken
case class OrdinaryChar(ch: Char) extends MarkdownToken
case class ControlSequence(str: String) extends MarkdownToken
case class Special(command: String, arg: Option[String]) extends MarkdownToken
case class Link(target: String, text: Option[String]) extends MarkdownToken

class MarkdownStream(rawInput: String, stripAsterisks: Boolean = false) {

  val input = {
    val lines = rawInput split "\n"
    val trimmed = lines map { line =>
      if (stripAsterisks)
        line.trim stripPrefix "*"
      else
        line
    }
    trimmed mkString "\n"
  }

  private var pointer = 0

  def isDone: Boolean = pointer >= input.length

  def next: Char = {
    if (pointer >= input.length)
      (-1).toChar
    else
      input(pointer)
  }

  def peek(n: Int): String = {
    val endIndex = (pointer + n) min input.length
    input substring (pointer, endIndex)
  }

  def consume: Char = {
    val ch = next
    pointer += 1
    ch
  }

  def consumeWhile(fn: Char => Boolean): String = {
    val start = pointer
    while (pointer < input.length && fn(input(pointer))) {
      pointer += 1
    }
    input substring (start, pointer)
  }

  def exhaust(): Unit = {
    pointer = input.length
  }

}

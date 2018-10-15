package org.cgsuite.util

import org.cgsuite.util.Markdown.{Location, State}

import scala.collection.mutable

object Markdown {

  def apply(rawInput: String, stripAsterisks: Boolean = false): Markdown = {
    val builder = new MarkdownBuilder(rawInput, stripAsterisks)
    builder.toMarkdown
  }

  object State extends Enumeration {
    val Normal, Code, Emph, Math, Section1, Section2, Section3 = Value
  }

  object Location extends Enumeration {
    val Normal, Super, Sub = Value
  }

  val specials: Map[Char, String] = Map(
    '^' -> "&uarr;",
    'v' -> "&darr;",
    '\\' -> "<br>",
    '<' -> "<",
    '>' -> ">",
    '"' -> "\""
  )

  val specialSeqs: Map[String, String] = Map(
    "ol" -> "<ol>",
    "ul" -> "<ul>",
    "endol" -> "</ol>",
    "endul" -> "</ul>",
    "li" -> "<li>",
    "leq" -> "&lt;=",
    "geq" -> "&gt;=",
    "sim" -> "~",
    "sp" -> "&nbsp;&nbsp;",
    "cdot" -> "\u00b7",
    "Sigma" -> "\u03a3"
  )

}

case class Markdown(text: String, links: List[(String, Option[String])])

class MarkdownBuilder(rawInput: String, stripAsterisks: Boolean = false) {

  private val stream = new MarkdownStream(rawInput, stripAsterisks)
  private var state = State.Normal
  private var location = Location.Normal
  private val linksRef = mutable.MutableList[(String, Option[String])]()
  private val result = new StringBuffer()

  emit("  ")

  while (!stream.isDone) {

    emit(consumeNextChar())

  }

  def consumeNextChar(): String = {

    (state, location, stream.consume) match {

      case (State.Normal, _, '`') => state = State.Code; "<code>"
      case (State.Normal, _, '~') => state = State.Emph; "<em>"
      case (State.Normal, _, '$') => state = State.Math; "<code>"
      case (State.Normal, _, '+') if stream.peek(3) == "+++" => state = State.Section3; stream consumeWhile { _ == '+' }; "<h3>"
      case (State.Normal, _, '+') if stream.peek(2) == "++" => state = State.Section2; stream consumeWhile { _ == '+' }; "<h2>"
      case (State.Normal, _, '+') if stream.next == '+' => state = State.Section1; stream.consume; "<h1>"

      case (State.Code, _, '`') => state = State.Normal; "</code>"
      case (State.Emph, _, '~') => state = State.Normal; "</em>"
      case (State.Math, _, '$') => state = State.Normal; "</code>"
      case (State.Section3, _, '+') if stream.next == '+' => state = State.Normal; stream consumeWhile { _ == '+' }; "</h3>"
      case (State.Section2, _, '+') if stream.next == '+' => state = State.Normal; stream consumeWhile { _ == '+' }; "</h2>"
      case (State.Section1, _, '+') if stream.next == '+' => state = State.Normal; stream consumeWhile { _ == '+' }; "</h1>"

      case (_, Location.Normal, '^') if state != State.Code => location = Location.Super; "<sup>"
      case (_, Location.Normal, '_') if state != State.Code => location = Location.Sub; "<sub>"

      case (_, Location.Super, '^') => location = Location.Normal; "</sup>"
      case (_, Location.Sub, '_') => location = Location.Normal; "</sub>"

      case (_, _, '<') => "&lt;"
      case (_, _, '>') => "&gt;"
      case (_, _, '"') => "&quot;"
      case (_, _, '&') => "&amp;"

      case (_, _, '[') if stream.next == '[' => consumeLink()

      case (_, _, '\\') if stream.next.isLetter => consumeSpecialSeq()
      case (_, _, '\\') => special(stream.consume)

      case (State.Code, _, '\n') => "\n  <br>"
      case (_, _, '\n') if stream.next == '\n' => stream.consume; "\n  <p>"
      case (_, _, '\n') => " "

      case (_, _, ch) => ch.toString

    }

  }

  def consumeLink(): String = {

    val target = consumeLinkElement()
    val text = {
      if (stream.next == '[')
        Some(consumeLinkElement())
      else
        None
    }
    stream consumeWhile { _ != ']' }
    stream.consume

    linksRef += ((target, text))
    f"@@${linksRef.size-1}%04d"

  }

  def consumeLinkElement(): String = {

    val str = new mutable.StringBuilder()
    assert(stream.consume == '[')
    while (!stream.isDone && stream.next != ']') {
      str append consumeNextChar()
    }
    stream.consume
    str.toString

  }

  def consumeSpecialSeq(): String = {
    val id = stream consumeWhile { _.isLetter }
    val resolution = specialSeq(id)
    resolution
  }

  def emit(ch: Char): Unit = result append ch

  def emit(str: String): Unit = result append str

  def special(ch: Char): String = {
    Markdown.specials getOrElse (ch, ch.toString)
  }

  def specialSeq(str: String): String = {
    Markdown.specialSeqs getOrElse (str, "??????")
  }

  def toMarkdown = Markdown(result.toString, linksRef.toList)

}

class MarkdownStream(rawInput: String, stripAsterisks: Boolean = false) {

  val input = {
    val lines = rawInput split "\n"
    val trimmed = lines map { line =>
      if (stripAsterisks)
        line.trim stripPrefix "*"
      else
        line.trim
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

}

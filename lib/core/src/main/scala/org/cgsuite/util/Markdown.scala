package org.cgsuite.util

import com.typesafe.scalalogging.Logger
import org.cgsuite.util.Markdown.{Location, State, logger}
import org.slf4j.LoggerFactory

import scala.collection.mutable

object Markdown {

  private[util] val logger = Logger(LoggerFactory.getLogger(classOf[Markdown]))

  def apply(rawInput: String, linkBuilder: LinkBuilder, stripAsterisks: Boolean = false, firstSentenceOnly: Boolean = false): Markdown = {
    val builder = new MarkdownBuilder(rawInput, linkBuilder, stripAsterisks, firstSentenceOnly)
    builder.toMarkdown
  }

  object State extends Enumeration {
    val Normal, Code, Emph, Bold, Math, Section1, Section2, Section3 = Value
  }

  object Location extends Enumeration {
    val Normal, Super, Sub = Value
  }

  val specials: Map[Char, String] = Map(
    '^' -> "&uarr;",
    '\\' -> "<br>",
    '<' -> "<",
    '>' -> ">",
    '"' -> "\"",
    ',' -> "&thinsp;"
  )

  val specialSeqs: Map[String, String] = Map(
    "v" -> "&darr;",
    "ol" -> "<ol>",
    "ul" -> "<ul>",
    "endol" -> "</ol>",
    "endul" -> "</ul>",
    "li" -> "<li>",
    "to" -> "&rarr;",
    "infty" -> "&infin;",
    "leq" -> "&le;",
    "geq" -> "&ge;",
    "sim" -> "~",
    "sp" -> "&nbsp;&nbsp;",
    "cdot" -> "&middot;",
    "doubleup" -> "&uArr;"
  )

  val specialLinks: Map[String, (String, String)] = Map(
    "cgt" -> ("/materials", "<em>Combinatorial Game Theory</em>"),
    "ww" -> ("/materials", "<em>Winning Ways</em>"),
    "onag" -> ("/materials", "<em>On Numbers and Games</em>")
  )

}

case class Markdown(text: String, hasFooter: Boolean)

trait LinkBuilder {

  def hyperlink(ref: String, textOpt: Option[String]): String

}

class MarkdownBuilder(rawInput: String, linkBuilder: LinkBuilder, stripAsterisks: Boolean = false, firstSentenceOnly: Boolean = false) {

  private val stream = new MarkdownStream(rawInput, stripAsterisks)
  private var state = State.Normal
  private var location = Location.Normal
  private val result = new StringBuffer()
  private var hasFooter = false

  emit("  ")

  while (!stream.isDone) {

    emit(consumeNextChar())

  }

  def consumeNextChar(): String = {

    (state, location, stream.consume) match {

      case (State.Normal, _, '`') => state = State.Code; "<code>"
      case (State.Normal, _, '~') if stream.next == '~' => stream.consume; state = State.Bold; "<b>"
      case (State.Normal, _, '~') => state = State.Emph; "<em>"
      case (State.Normal, _, '$') => state = State.Math; "<code>"
      case (State.Normal, _, '+') if stream.peek(3) == "+++" => state = State.Section3; stream consumeWhile { _ == '+' }; "<h3>"
      case (State.Normal, _, '+') if stream.peek(2) == "++" => state = State.Section2; stream consumeWhile { _ == '+' }; "</div><h2>"
      case (State.Normal, _, '+') if stream.next == '+' => state = State.Section1; stream.consume; "</div><h1>"

      case (State.Code, _, '`') => state = State.Normal; "</code>"
      case (State.Bold, _, '~') if stream.next == '~' => stream.consume; state = State.Normal; "</b>"
      case (State.Emph, _, '~') => state = State.Normal; "</em>"
      case (State.Math, _, '$') => state = State.Normal; "</code>"
      case (State.Section3, _, '+') if stream.next == '+' => state = State.Normal; stream consumeWhile { _ == '+' }; "</h3>"
      case (State.Section2, _, '+') if stream.next == '+' => state = State.Normal; stream consumeWhile { _ == '+' }; """</h2><div class="section">"""
      case (State.Section1, _, '+') if stream.next == '+' => state = State.Normal; stream consumeWhile { _ == '+' }; """</h1><div class="section">"""

      case (_, Location.Normal, '^') if state != State.Code => location = Location.Super; "<sup>"
      case (_, Location.Normal, '_') if state != State.Code => location = Location.Sub; "<sub>"

      case (_, Location.Super, '^') => location = Location.Normal; "</sup>"
      case (_, Location.Sub, '_') => location = Location.Normal; "</sub>"

      case (_, _, '<') => "&lt;"
      case (_, _, '>') => "&gt;"
      case (_, _, '"') => "&quot;"
      case (_, _, '&') => "&amp;"

      case (_, _, '[') if stream.next == '[' => consumeLink()

      case (_, _, '\\') if stream.peek(2) == "##" => consumeControl()
      case (_, _, '\\') if stream.next.isLetter => consumeSpecialSeq()
      case (_, _, '\\') => special(stream.consume)

      case (State.Code, _, '\n') => "\n  <br>"
      case (_, _, '\n') if stream.next == '\n' => stream.consume; "\n  <p>"
      case (_, _, '\n') => " "

      case (_, _, '.') if firstSentenceOnly => stream.exhaust(); "."

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

    linkBuilder.hyperlink(target, text)

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
    if (stream.peek(2) == "{}") {
      stream.consume
      stream.consume
    }
    resolution
  }

  def consumeControl(): String = {

    stream consumeWhile { _ == '#' }

    val command = stream consumeWhile { _ != '#' }

    stream consumeWhile { _ == '#' }

    command.trim.toLowerCase match {

      case "footer" =>
        hasFooter = true
        "\n</div>\n"

      case str =>
        logger warn s"Unknown command: $str"
        ""

    }

  }

  def emit(ch: Char): Unit = result append ch

  def emit(str: String): Unit = result append str

  def special(ch: Char): String = {
    Markdown.specials getOrElse (ch, ch.toString)
  }

  def specialSeq(str: String): String = {
    Markdown.specialSeqs get str match {
      case Some(text) => text
      case None =>
        Markdown.specialLinks get str match {
          case Some((target, text)) => linkBuilder.hyperlink(target, Some(text))
          case None => s"&$str;"
        }
    }
  }

  def toMarkdown: Markdown = Markdown(result.toString, hasFooter)

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

  def exhaust(): Unit = {
    pointer = input.length
  }

}

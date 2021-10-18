package org.cgsuite.help

import com.typesafe.scalalogging.Logger
import org.cgsuite.help.Markdown.{Location, State, logger}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Markdown {

  private[help] val logger = Logger(LoggerFactory.getLogger(classOf[Markdown]))

  def apply(
    imageTargetPrefix: String,
    rawInput: String,
    linkBuilder: LinkBuilder,
    stripAsterisks: Boolean = false,
    firstSentenceOnly: Boolean = false
  ): Markdown = {
    val builder = new MarkdownBuilder(imageTargetPrefix, rawInput, linkBuilder, stripAsterisks, firstSentenceOnly)
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
    "doubleup" -> "&uArr;",
    "times" -> "&times;",
    "otimes" -> "&otimes;",
    "oplus" -> "&oplus;",
    "lip" -> "&lip;",
    "Sigma" -> "&Sigma;",
    "lt" -> "&lt;",
    "epsilon" -> "&epsilon;"
  )

  val specialLinks: Map[String, (String, String)] = Map(
    "cgt" -> ("/materials", "<em>Combinatorial Game Theory</em>"),
    "ww" -> ("/materials", "<em>Winning Ways</em>"),
    "onag" -> ("/materials", "<em>On Numbers and Games</em>")
  )

}

case class Markdown(text: String, hasFooter: Boolean, execStatements: Vector[(String, Double)])

trait LinkBuilder {

  def hyperlink(ref: String, textOpt: Option[String]): String

}

class MarkdownBuilder(
  imageTargetPrefix: String,
  rawInput: String,
  linkBuilder: LinkBuilder,
  stripAsterisks: Boolean = false,
  firstSentenceOnly: Boolean = false
) {

  private val stream = new MarkdownTokenStream(rawInput, stripAsterisks)
  private var state = State.Normal
  private var location = Location.Normal
  private val result = new StringBuffer()
  private val execStatements = ArrayBuffer[(String, Double)]()
  private var hasFooter = false

  emit("  ")

  while (!stream.isDone) {

    emit(consumeNextToken())

  }

  def consumeNextToken(): String = {

    (state, location, stream.next) match {

      case (_, _, Special(str, arg)) => resolveSpecial(str, arg)

      case (State.Normal, _, ControlSequence("`")) => state = State.Code; "<code>"
      case (State.Normal, _, ControlSequence("~~")) => state = State.Bold; "<b>"
      case (State.Normal, _, ControlSequence("~")) => state = State.Emph; "<em>"
      case (State.Normal, _, ControlSequence("$")) => state = State.Math; "<code>"
      case (State.Normal, _, ControlSequence("++")) => state = State.Section1; "</div><h1>"
      case (State.Normal, _, ControlSequence("+++")) => state = State.Section2; "</div><h2>"
      case (State.Normal, _, ControlSequence("++++")) => state = State.Section3; "<h3>"

      case (State.Code, _, ControlSequence("`")) => state = State.Normal; "</code>"
      case (State.Bold, _, ControlSequence("~~")) => state = State.Normal; "</b>"
      case (State.Emph, _, ControlSequence("~")) => state = State.Normal; "</em>"
      case (State.Math, _, ControlSequence("$")) => state = State.Normal; "</code>"
      case (State.Section3, _, ControlSequence("++++")) => state = State.Normal; "</h3>"
      case (State.Section2, _, ControlSequence("+++")) => state = State.Normal; """</h2><div class="section">"""
      case (State.Section1, _, ControlSequence("++")) => state = State.Normal; """</h1><div class="section">"""

      case (_, Location.Normal, ControlSequence("^")) => location = Location.Super; "<sup>"
      case (_, Location.Normal, ControlSequence("_")) => location = Location.Sub; "<sub>"

      case (_, Location.Super, ControlSequence("^")) => location = Location.Normal; "</sup>"
      case (_, Location.Sub, ControlSequence("_")) => location = Location.Normal; "</sub>"

      case (_, _, OrdinaryChar('<')) => "&lt;"
      case (_, _, OrdinaryChar('>')) => "&gt;"
      case (_, _, OrdinaryChar('"')) => "&quot;"
      case (_, _, OrdinaryChar('&')) => "&amp;"
      case (State.Code | State.Math, _, OrdinaryChar('-')) => "&#8209;"   // Non-breaking hyphen

      case (_, _, Link(target, text)) => linkBuilder.hyperlink(target, text)

      case (State.Code, _, OrdinaryChar('\n')) => "\n  <br>"
      case (_, _, ControlSequence("\n\n")) => "\n  <p>"
      case (_, _, OrdinaryChar('\n')) => " "

      case (_, _, OrdinaryChar('.')) if firstSentenceOnly => stream.exhaust(); "."

      case (_, _, OrdinaryChar(ch)) => ch.toString

      // TODO This shouldn't be allowed, but it's temporarily swallowed to ease the transition
      case (State.Math, _, ControlSequence("~")) => ""

      case x => sys.error(s"Invalid Markdown: $x")

    }

  }

  def resolveSpecial(str: String, arg: Option[String]): String = {

    str match {

      case "exec" => resolveExec(arg, showInput = false, showOutput = true)
      case "execHalf" => resolveExec(arg, showInput = false, showOutput = true, scale = 0.5)
      case "display" => resolveExec(arg, showInput = true, showOutput = true)
      case "displayAndHide" => resolveExec(arg, showInput = true, showOutput = false)
      case "classDiagram" => classDiagram(arg getOrElse { sys.error("Missing hierarchy arg") })

      case "footer" => hasFooter = true; "\n</div>\n"

      case _ => special(str)

    }

  }

  def resolveExec(inputOpt: Option[String], showInput: Boolean, showOutput: Boolean, scale: Double = 1.0): String = {
    inputOpt match {
      case None => sys.error("exec special missing input")
      case Some(input) =>

        execStatements += ((input, scale))

        val inputString = {
          if (showInput && showOutput)
            s"<pre>&gt; $input</pre>"
          else if (showInput)
            s"<pre>$input</pre>"
          else
            ""
        }

        val imageFilePrefix = s"$imageTargetPrefix-${execStatements.length - 1}"
        val outputString = {
          if (showOutput) {
            val style = if (showInput) "" else " style=\"vertical-align: middle; margin: 2pt 0pt 2pt 0pt;\""
            val imgString = s"""<img src="$imageFilePrefix-1.0x.png" srcset="$imageFilePrefix-2.0x.png 2x"$style />"""
            if (showInput) {
              s"""<p style="margin-top:-10pt;">$imgString</p>"""
            } else {
              imgString
            }
          } else ""
        }
        s"$inputString$outputString"

    }
  }

  def emit(ch: Char): Unit = result append ch

  def emit(str: String): Unit = result append str

  def special(str: String): String = {
    if (str.length == 1) {
      val ch = str.head
      Markdown.specials getOrElse (ch, ch.toString)
    } else {
      Markdown.specialSeqs get str match {
        case Some(text) => text
        case None =>
          Markdown.specialLinks get str match {
            case Some((target, text)) => linkBuilder.hyperlink(target, Some(text))
            case None => sys.error(s"Unknown special: $str")
          }
      }
    }
  }

  def classDiagram(str: String): String = {
    val stripped = str stripPrefix "\n" stripSuffix "\n"
    val buf = new StringBuilder
    var i = 0
    while (i < stripped.length) {
      if (stripped.charAt(i).isLetter) {
        var j = i + 1
        while (j < stripped.length && stripped.charAt(j).isLetter)
          j += 1
        buf append linkBuilder.hyperlink(stripped.substring(i, j), None)
        i = j
      } else {
        buf += stripped.charAt(i)
        i += 1
      }
    }
    s"<pre class=\"bold\">$buf</pre>"
  }

  def toMarkdown: Markdown = Markdown(result.toString, hasFooter, execStatements.toVector)

}

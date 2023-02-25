package org.cgsuite.help

import com.typesafe.scalalogging.Logger
import org.cgsuite.help.Markdown.{Location, State, Style}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Markdown {

  private[help] val logger = Logger(LoggerFactory.getLogger(classOf[Markdown]))

  def apply(
    imageTargetPrefix: String,
    rawInput: String,
    linkBuilder: LinkBuilder,
    nextImageOrdinal: Int,
    stripAsterisks: Boolean = false,
    firstSentenceOnly: Boolean = false
  ): Markdown = {
    val builder = new MarkdownBuilder(imageTargetPrefix, rawInput, linkBuilder, nextImageOrdinal, stripAsterisks, firstSentenceOnly)
    builder.toMarkdown
  }

  object State extends Enumeration {
    val Normal, Code, Math, Section1, Section2, Section3 = Value
  }

  object Style extends Enumeration {
    val Normal, Emph, Bold = Value
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
    "neq" -> "&ne;",
    "sim" -> "~",
    "sp" -> "&nbsp;&nbsp;",
    "cdot" -> "&middot;",
    "cdots" -> "&ctdot;",
    "doubleup" -> "&uArr;",
    "times" -> "&times;",
    "otimes" -> "&otimes;",
    "oplus" -> "&oplus;",
    "oast" -> "&circledast;",
    "lip" -> "&lip;",
    "Sigma" -> "&Sigma;",
    "lt" -> "&lt;",
    "alpha" -> "&alpha;",
    "beta" -> "&beta;",
    "gamma" -> "&gamma;",
    "delta" -> "&delta;",
    "epsilon" -> "&epsilon;",
    "omega" -> "&omega;",
    "in" -> "&isin;",
    "comment" -> "<!--",
    "endcomment" -> "-->"
  )

  val specialLinks: Map[String, (String, String)] = Map(
    "cgt" -> ("/materials", "<em>Combinatorial Game Theory</em>"),
    "ww" -> ("/materials", "<em>Winning Ways</em>"),
    "onag" -> ("/materials", "<em>On Numbers and Games</em>")
  )

}

case class Markdown(text: String, hasFooter: Boolean, evalStatements: Vector[(String, Double)])

trait LinkBuilder {

  def hyperlink(ref: String, textOpt: Option[String]): String

}

class MarkdownBuilder(
  imageTargetPrefix: String,
  rawInput: String,
  linkBuilder: LinkBuilder,
  nextImageOrdinal: Int,
  stripAsterisks: Boolean = false,
  firstSentenceOnly: Boolean = false
) {

  private val stream = new MarkdownTokenStream(rawInput, stripAsterisks)
  private var state = State.Normal
  private var style = Style.Normal
  private var location = Location.Normal
  private val result = new StringBuffer()
  private val evalStatements = ArrayBuffer[(String, Double)]()
  private var previousParagraphEnded = false
  private var hasFooter = false

  emit("  ")

  while (!stream.isDone) {

    emit(consumeNextToken())

  }

  def consumeNextToken(): String = {

    (state, style, location, stream.next) match {

      // Special commands
      case (_, _, _, Special(str, arg)) => resolveSpecial(str, arg)

      // If in a summary, just echo a link
      case (_, _, _, Link(target, text)) if firstSentenceOnly => prepareParagraph(); text getOrElse { s"<code>$target</code>" }

      // Link to build
      case (_, _, _, Link(target, text)) => prepareParagraph(); linkBuilder.hyperlink(target, text)

      // Paragraph break
      case (_, _, _, ControlSequence("\n\n")) => previousParagraphEnded = true; "\n"

      // Begin state
      case (State.Normal, _, _, ControlSequence("`")) => state = State.Code; "<code>"
      case (State.Normal, _, _, ControlSequence("$")) => state = State.Math; "<code>"
      case (State.Normal, _, _, ControlSequence("++")) => state = State.Section1; previousParagraphEnded = false; "</div>\n<h1>"
      case (State.Normal, _, _, ControlSequence("+++")) => state = State.Section2; previousParagraphEnded = false; "</div>\n<h2>"
      case (State.Normal, _, _, ControlSequence("++++")) => state = State.Section3; previousParagraphEnded = false; "\n<h3>"

      // End state
      case (State.Code, _, _, ControlSequence("`")) => state = State.Normal; "</code>"
      case (State.Math, _, _, ControlSequence("$")) => state = State.Normal; "</code>"
      case (State.Section1, _, _, ControlSequence("++")) => state = State.Normal; previousParagraphEnded = false; "</h1>\n<div class=\"section\">"
      case (State.Section2, _, _, ControlSequence("+++")) => state = State.Normal; previousParagraphEnded = false; "</h2>\n<div class=\"section\">"
      case (State.Section3, _, _, ControlSequence("++++")) => state = State.Normal; previousParagraphEnded = false; "</h3>\n"

      // Begin style
      case (_, Style.Normal, _, ControlSequence("~")) => style = Style.Emph; "<em>"
      case (_, Style.Normal, _, ControlSequence("~~")) => style = Style.Bold; "<b>"

      // End style
      case (_, Style.Emph, _, ControlSequence("~")) => style = Style.Normal; "</em>"
      case (_, Style.Bold, _, ControlSequence("~~")) => style = Style.Normal; "</b>"

      // Begin location
      case (_, _, Location.Normal, ControlSequence("^")) => location = Location.Super; "<sup>"
      case (_, _, Location.Normal, ControlSequence("_")) => location = Location.Sub; "<sub>"

      // End location
      case (_, _, Location.Super, ControlSequence("^")) => location = Location.Normal; "</sup>"
      case (_, _, Location.Sub, ControlSequence("_")) => location = Location.Normal; "</sub>"

      // Character convenience mappings
      case (_, _, _, OrdinaryChar('<')) => prepareParagraph(); "&lt;"
      case (_, _, _, OrdinaryChar('>')) => prepareParagraph(); "&gt;"
      case (_, _, _, OrdinaryChar('"')) => prepareParagraph(); "&quot;"
      case (_, _, _, OrdinaryChar('&')) => prepareParagraph(); "&amp;"

      case (State.Code | State.Math, _, _, OrdinaryChar('-')) => prepareParagraph(); "&#8209;"   // Non-breaking hyphen
      case (State.Code, _, _, OrdinaryChar('\n')) => prepareParagraph(); "\n  <br>"

      case (_, _, _, OrdinaryChar('\n')) => " "

      // If firstSentenceOnly, then stop when we hit a period
      case (_, _, _, OrdinaryChar('.')) if firstSentenceOnly => stream.exhaust(); "."

      // Other ordinary characters are simply echoed
      case (_, _, _, OrdinaryChar(ch)) => prepareParagraph(); ch.toString

      case x => sys.error(s"Invalid Markdown: $x")

    }

  }

  def prepareParagraph(): Unit = {
    if (previousParagraphEnded) {
      previousParagraphEnded = false
      emit("<p>")
    }
  }

  def resolveSpecial(str: String, arg: Option[String]): String = {

    str match {

      case "eval" => resolveExec(arg, showInput = false, showOutput = true)
      case "evalHalf" => resolveExec(arg, showInput = false, showOutput = true, scale = 0.5)
      case "evalText" => resolveExecText(arg)
      case "display" => resolveExec(arg, showInput = true, showOutput = true)
      case "displayAndHide" => resolveExec(arg, showInput = true, showOutput = false)
      case "classDiagram" => classDiagram(arg getOrElse { sys.error("Missing hierarchy arg") })

      case "footer" => hasFooter = true; "\n</div>\n"

      case _ => special(str)

    }

  }

  def resolveExec(inputOpt: Option[String], showInput: Boolean, showOutput: Boolean, scale: Double = 1.0): String = {
    prepareParagraph()
    inputOpt match {
      case None => sys.error("eval special missing input")
      case Some(input) =>

        evalStatements += ((input, scale))

        val inputString = {
          if (showInput && showOutput)
            s"<pre>&gt; $input</pre>"
          else if (showInput)
            s"<pre>$input</pre>"
          else
            ""
        }

        val imageFilePrefix = s"$imageTargetPrefix-${nextImageOrdinal + evalStatements.length - 1}"
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

  def resolveExecText(arg: Option[String]): String = {
    prepareParagraph()
    arg match {
      case None => sys.error("eval special missing input")
      case Some(input) => org.cgsuite.lang.System.evaluateOrException(input, mutable.AnyRefMap()).head.toString
    }
  }

  def emit(ch: Char): Unit = result append ch

  def emit(str: String): Unit = result append str

  def special(str: String): String = {
    prepareParagraph()
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

  def toMarkdown: Markdown = Markdown(result.toString, hasFooter, evalStatements.toVector)

}

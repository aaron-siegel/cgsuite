package org.cgsuite.lang

import java.io.{PrintWriter, StringWriter}
import java.util

import ch.qos.logback.classic.{Level, Logger}
import com.typesafe.scalalogging.LazyLogging
import org.antlr.runtime.Token
import org.cgsuite.exception.{CgsuiteException, EvalException, SyntaxException}
import org.cgsuite.lang.node.StatementSequenceNode
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.{EmptyOutput, Output, StyledTextOutput}
import org.cgsuite.util.UiHarness
import org.slf4j.LoggerFactory

import scala.collection.mutable

object System extends LazyLogging {

  val copyrightYear = "2003-2021"

  val version = "2.0-beta2"

  def clearAll(): Unit = {
    UiHarness.uiHarness.clearUiVars()
    CgscriptClass.clearAll()
  }

  def error(str: String): Unit = throw EvalException(str)

  def print(obj: AnyRef): Unit = UiHarness.uiHarness.print(obj)

  def isDevBuild: Boolean = CgscriptClasspath.devBuildHome.isDefined

  def evaluate(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Vector[Output] = {
    try {
      evaluateOrException(input, varMap)
    } catch {
      case exc: SyntaxException => syntaxExceptionToOutput(input, exc, includeLine = false)
      case exc: CgsuiteException => cgsuiteExceptionToOutput(input, exc)
      case exc: Throwable => throwableToOutput(input, exc)
    }
  }

  def evaluateOrException(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Vector[Output] = {
    val tree = ParserUtil.parseScript(input)
    logger debug s"Parse Tree: ${tree.toStringTree}"
    val node = StatementSequenceNode(tree.getChild(0))
    val scope = ElaborationDomain.empty()
    node.elaborate(scope)
    logger debug s"EvalNode: $node"
    val domain = new EvaluationDomain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
    val result = node.evaluate(domain)
    if (node.suppressOutput)
      Vector.empty
    else
      objectToOutput(result)
  }

  def objectToOutput(obj: Any): Vector[Output] = {
    CgscriptClass.instanceToOutput(obj) match {
      case EmptyOutput => Vector.empty
      case output => Vector(output)
    }
  }

  private def syntaxExceptionToOutput(input: String, exc: SyntaxException, includeLine: Boolean): Vector[Output] = {

    val recog = exc.exc
    val line = recog.line
    val col = recog.charPositionInLine
    val message = (if (includeLine && line > 0) s"Error (Line $line:$col): " else "") + exceptionToMessage(recog)
    if (line <= 0)
      Vector(new StyledTextOutput(message))
    else
      errorOutput(exc.getMessage) +: toLineColOutput(exc.source, input, line, col)

  }

  private def cgsuiteExceptionToOutput(input: String, exc: CgsuiteException): Vector[Output] = {

    val exceptionLimit: Int = {
      if (LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].getLevel == Level.DEBUG)
        Int.MaxValue
      else
        6
    }

    val message: Output = errorOutput(exc.getMessage)

    val stack: Vector[Output] = {
      if (exc.tokenStack.length <= 30)
        exc.tokenStack flatMap { toLineColOutput(_, input) }
      else {
        (exc.tokenStack take 15 flatMap { toLineColOutput(_, input) }) ++
          Vector(errorOutput("  ......")) ++
          (exc.tokenStack takeRight 15 flatMap { toLineColOutput(_, input) })
      }
    }.toVector

    val cause: Vector[Output] = {
      if (exc.getCause != null && isDevBuild) {
        // If the exception is chained, *and* this is a dev build of CGSuite, then
        // we output the stack of the chained exception.
        val causeClass = errorOutput(s"  caused by ${exc.getCause.getClass.getName}")
        val causeStack = {
          exc.getCause.getStackTrace.toVector.take(exceptionLimit).map { element =>
            errorOutput(s"    at ${element.getClassName} line ${element.getLineNumber}")
          }
        }
        val causeDotDotDot = {
          if (exc.getCause.getStackTrace.length > exceptionLimit)
            Vector(errorOutput("    ......"))
          else
            Vector.empty
        }
        causeClass +: (causeStack ++ causeDotDotDot)
      } else {
        Vector.empty
      }
    }

    val basicMessage = message +: (stack ++ cause)
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].getLevel match {
      case Level.DEBUG => basicMessage ++ throwableToOutput(input, exc)
      case _ => basicMessage
    }

  }

  private def throwableToOutput(input: String, exc: Throwable): Vector[Output] = {

    val sw = new StringWriter
    exc.printStackTrace(new PrintWriter(sw))
    val strings = sw.toString split "\n"
    strings.toVector map errorOutput

  }

  private def toLineColOutput(token: Token, input: String): Vector[Output] = {
    assert(token.getInputStream != null, s"Input stream is null: $token")
    toLineColOutput(token.getInputStream.getSourceName, input, token.getLine, token.getCharPositionInLine)
  }

  private def toLineColOutput(source: String, input: String, line: Int, col: Int): Vector[Output] = {

    if (source startsWith ":Input:") {
      val sourceInput = source stripPrefix ":Input:"
      var lineStartIndex = 0
      // Determine the point in the input string where the exceptional line begins.
      (1 until line) foreach { _ => lineStartIndex = sourceInput.indexOf('\n', lineStartIndex) + 1 }
      val lineEndIndex = sourceInput.indexOf('\n', lineStartIndex) match {
        case -1 => sourceInput.length
        case n => n
      }
      // Next get the part of the input string that has the error.
      val snippet = sourceInput.substring(
        Math.max(lineStartIndex, lineStartIndex + col - 24),
        Math.min(lineEndIndex, lineStartIndex + col + 25)
      )
      val pointer = (" " * Math.min(col - 1, 23)) + (if (col == 0) "^^" else "^^^")
      Vector(
        errorOutput("  at worksheet input:"),
        errorOutput(s"  $snippet"),
        errorOutput(s"  $pointer")
      )
    } else {
      Vector(errorOutput(s"  at $source line $line:$col"))
    }
  }

  def errorOutput(msg: String): Output = {
    new StyledTextOutput(util.EnumSet.of(StyledTextOutput.Style.FACE_MONOSPACED, StyledTextOutput.Style.COLOR_RED), msg)
  }

  private def exceptionToMessage(exc: Throwable) = {
    if (exc.isInstanceOf[EvalException])
      exc.getMessage
    else
      "Syntax error."
  }

}

private class System

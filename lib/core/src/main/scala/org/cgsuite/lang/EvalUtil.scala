package org.cgsuite.lang

import java.io.{PrintWriter, StringWriter}
import java.util

import ch.qos.logback.classic.{Level, Logger}
import com.typesafe.scalalogging.LazyLogging
import org.antlr.runtime.Token
import org.cgsuite.exception.{CgsuiteException, EvalException, StackElement, SyntaxException}
import org.cgsuite.output.{EmptyOutput, Output, StyledTextOutput}
import org.slf4j.LoggerFactory

object EvalUtil extends LazyLogging {

  def objectToOutput(obj: Any): Vector[Output] = {
    CgscriptClass.instanceToOutput(obj) match {
      case EmptyOutput => Vector.empty
      case output => Vector(output)
    }
  }

  def syntaxExceptionToOutput(input: String, exc: SyntaxException, includeLine: Boolean): Vector[Output] = {

    val recog = exc.exc
    val line = recog.line
    val col = recog.charPositionInLine
    val message = (if (includeLine && line > 0) s"Error (Line $line:$col): " else "") + exceptionToMessage(recog)
    if (line <= 0)
      Vector(new StyledTextOutput(message))
    else
      errorOutput(exc.getMessage) +: toLineColOutput(exc.source, input, line, col)

  }

  def cgsuiteExceptionToOutput(input: String, exc: CgsuiteException): Vector[Output] = {

    val exceptionLimit: Int = {
      if (LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].getLevel == Level.DEBUG)
        Int.MaxValue
      else
        6
    }

    val message: Output = errorOutput(exc.getMessage)

    val stack: Vector[Output] = {
      if (exc.stackTrace.length <= 30)
        exc.stackTrace flatMap { toLineColOutput(_, input) }
      else {
        (exc.stackTrace take 15 flatMap { toLineColOutput(_, input) }) ++
          Vector(errorOutput("  ......")) ++
          (exc.stackTrace takeRight 15 flatMap { toLineColOutput(_, input) })
      }
    }.toVector

    val cause: Vector[Output] = {
      if (exc.getCause == null)
        Vector.empty
      else {
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
      }
    }

    message +: (stack ++ cause)

  }

  def defaultThrowableToOutput(exc: Throwable): Vector[Output] = {

    val sw = new StringWriter
    exc.printStackTrace(new PrintWriter(sw))
    val strings = sw.toString split "\n"
    strings.toVector map errorOutput

  }

  private def toLineColOutput(stackElement: StackElement, input: String): Vector[Output] = {
    toLineColOutput(stackElement.source, input, stackElement.line, stackElement.col)
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
        errorOutput(s"    $snippet"),
        errorOutput(s"    $pointer")
      )
    } else {
      Vector(errorOutput(s"  at $source line $line:${col + 1}"))
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

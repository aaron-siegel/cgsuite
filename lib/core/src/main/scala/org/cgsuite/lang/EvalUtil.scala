package org.cgsuite.lang

import java.io.{PrintWriter, StringWriter}
import java.util

import com.typesafe.scalalogging.LazyLogging
import org.cgsuite.exception.{CgsuiteException, EvalException, SyntaxException}
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.{EmptyOutput, Output, StyledTextOutput}

import scala.collection.mutable

object EvalUtil extends LazyLogging {

  def evaluate(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Vector[Output] = {
    try {
      // TODO We need to ignore local-moding of "var" declarations
      val tree = ParserUtil.parseScript(input)
      logger debug s"Parse Tree: ${tree.toStringTree}"
      val node = EvalNode(tree.getChild(0))
      val scope = ElaborationDomain(None, Seq.empty, None)
      node.elaborate(scope)
      logger debug s"EvalNode: $node"
      val domain = new Domain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
      val result = node.evaluate(domain)
      objectToOutput(result)
    } catch {
      case exc: SyntaxException => syntaxExceptionToOutput(input, exc, false)
      case exc: CgsuiteException => cgsuiteExceptionToOutput(input, exc)
      case exc: Throwable => throwableToOutput(input, exc)
    }
  }

  def objectToOutput(obj: Any): Vector[Output] = {
    val output = CgscriptClass.of(obj).classInfo.toOutputMethod.call(obj, Array.empty)
    output match {
      case EmptyOutput => Vector.empty
      case str: String => Vector(new StyledTextOutput(str))
      case o: Output => Vector(o)
      case _ => sys error output.getClass.toString      // TODO Better exception here (ToOutput didn't return output...)
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
      errorOutput(exc.getMessage) +: getLineColOutput(exc.source, input, line, col)

  }

  private def cgsuiteExceptionToOutput(input: String, exc: CgsuiteException): Vector[Output] = {

    val message = errorOutput(exc.getMessage)
    val stack = exc.tokenStack.toVector flatMap { token =>
      assert(token.getInputStream != null, s"Input stream is null: $token (${exc.getMessage})")
      getLineColOutput(token.getInputStream.getSourceName, input, token.getLine, token.getCharPositionInLine)
    }
    val cause: Vector[Output] = {
      if (exc.getCause == null)
        Vector.empty
      else {
        val causeClass = errorOutput(s"  caused by ${exc.getCause.getClass.getName}")
        val causeStack = {
          exc.getCause.getStackTrace.toVector.take(6).map { element =>
            errorOutput(s"    at ${element.getClassName} line ${element.getLineNumber}")
          }
        }
        val causeDotDotDot = {
          if (exc.getCause.getStackTrace.length > 6)
            Vector(errorOutput("    ......"))
          else
            Vector.empty
        }
        causeClass +: (causeStack ++ causeDotDotDot)
      }
    }

    message +: (stack ++ cause)

  }

  private def throwableToOutput(input: String, exc: Throwable): Vector[Output] = {

    val sw = new StringWriter
    exc.printStackTrace(new PrintWriter(sw))
    val strings = sw.toString split "\n"
    strings.toVector map errorOutput

  }

  private def getLineColOutput(source: String, input: String, line: Int, col: Int): Vector[Output] = {

    if (source == "Worksheet") {
      var lineStartIndex = 0
      // Determine the point in the input string where the exceptional line begins.
      (1 until line) foreach { _ => lineStartIndex = input.indexOf('\n', lineStartIndex) + 1 }
      val lineEndIndex = input.indexOf('\n', lineStartIndex)
      // Next get the part of the input string that has the error.
      val snippet = input.substring(
        Math.max(lineStartIndex, lineStartIndex + col - 24),
        Math.min(if (lineEndIndex == -1) input.length else lineEndIndex, lineStartIndex + col + 25)
      )
      val pointer = (" " * Math.min(col - 1, 22)) + (if (col == 0) "^^" else "^^^")
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

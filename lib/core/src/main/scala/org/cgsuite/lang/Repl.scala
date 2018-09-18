package org.cgsuite.lang

import java.lang.{System => JSystem}

import ch.qos.logback.classic.{Level, Logger}
import org.cgsuite.core.Game
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.CgscriptClass.logger
import org.cgsuite.util.{Explorer, UiHarness}
import org.jline.reader.{Expander, History, LineReaderBuilder}
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory

import scala.collection.mutable


object Repl {

  def main(args: Array[String]) {

    println("Welcome to the CGSuite REPL, Version 2.0.")

    val terminal = TerminalBuilder.builder().jansi(true).dumb(true).build()
    val lineReader = LineReaderBuilder.builder().expander(NullExpander).terminal(terminal).build()

    UiHarness.setUiHarness(ReplUiHarness)
    CgscriptClass.Object.ensureLoaded()
    val replVarMap = mutable.AnyRefMap[Symbol, Any]()
    var done = false

    while (!done) {
      val str = lineReader.readLine("> ")
      str.trim match {
        case "" =>
        case ":clear" =>
          CgscriptClass.clearAll()
          replVarMap.clear()
        case ":debug" =>
          LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.DEBUG)
        case ":info" =>
          LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.INFO)
        case ":quit" =>
          done = true
        case _ =>
          val start = JSystem.nanoTime
          try {
            CgscriptClasspath.reloadModifiedFiles()
            val output = EvalUtil.evaluate(str, replVarMap)
            output foreach println
          } catch {
            case exc: Throwable => exc.printStackTrace()
          }
          val totalDuration = JSystem.nanoTime - start
          logger debug s"Completed in ${totalDuration / 1000000} ms"
      }

    }

  }

  object NullExpander extends Expander {

    override def expandHistory(history: History, line: String) = line

    override def expandVar(word: String) = word

  }

}

object ReplUiHarness extends UiHarness {

  override def createExplorer(g: Game): Explorer = {
    throw EvalException("The Explorer is not available in the CGSuite REPL.")
  }

  override def print(obj: AnyRef): Unit = {
    val output = EvalUtil.objectToOutput(obj)
    output foreach { out => println(out.toString) }
  }

}

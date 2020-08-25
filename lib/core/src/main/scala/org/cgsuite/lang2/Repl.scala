package org.cgsuite.lang2

import java.lang.{System => JSystem}

import ch.qos.logback.classic.{Level, Logger}
import org.cgsuite.core.{CanonicalShortGame, Game}
import org.cgsuite.exception.EvalException
import org.cgsuite.lang2.CgscriptClass.logger
import org.cgsuite.util.{Explorer, UiHarness}
import org.jline.reader.{Expander, History, LineReaderBuilder}
import org.jline.terminal.TerminalBuilder
import org.slf4j.LoggerFactory

import scala.collection.mutable


object Repl {

  val version = "2.0"

  val welcome = s"Welcome to the CGSuite REPL, Version $version."

  val replVarMap = mutable.AnyRefMap[Symbol, Any]()

  def main(args: Array[String]) {

    print(
      s"""$welcome
         |Type CGScript expressions for evaluation, or :help for a list of REPL commands.
         |""".stripMargin)

    val terminal = TerminalBuilder.builder().jansi(true).dumb(true).build()
    val lineReader = LineReaderBuilder.builder().expander(NullExpander).terminal(terminal).build()

    UiHarness.setUiHarness(ReplUiHarness)
    var done = false

    while (!done) {
      val str = lineReader.readLine("> ").trim
      if (str startsWith ":")
        done = processSpecial(str stripPrefix ":")
      else
        evaluateAndPrint(str)
    }

  }

  def processSpecial(str: String): Boolean = {
    val tokens = str split " "
    tokens.head match {
      case "clear" =>
        CgscriptClass.clearAll()
        replVarMap.clear()
        false
      case "debug" =>
        tokens.toList.tail match {
          case Nil | "on" :: Nil =>
            LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.DEBUG)
            println("Debug logging enabled.")
          case "off" :: Nil =>
            LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.INFO)
            println("Debug logging disabled.")
          case _ =>
            println(s"Debug setting must be `on` or `off`.")
        }
        false
      case "help" =>
        print (
          s"""$welcome
             |:clear            Clear all caches and variables
             |:debug [on|off]   Turn debug logging on or off
             |:help             Print this message
             |:quit             Quit the CGSuite REPL
             |:version          Print CGSuite and system version info
             |""".stripMargin)
        false
      case "quit" =>
        true
      case "version" =>
        print(
          s"""CGSuite $version
             |Java ${java.lang.System.getProperty("java.version")}
             |${java.lang.System.getProperty("os.name")} ${java.lang.System.getProperty("os.version")}
             |Heap memory: ${java.lang.Runtime.getRuntime.maxMemory >> 20} MB
             |CanonicalShortGames recognized: ${CanonicalShortGame.gameCount}
             |""".stripMargin)
        false
      case _ =>
        println(s"Unknown REPL command: `${tokens.head}`")
        false
    }
  }

  def evaluateAndPrint(str: String): Unit = {

    if (str == "")
      return

    CgscriptClass.Object.ensureLoaded()

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

  object NullExpander extends Expander {

    override def expandHistory(history: History, line: String) = line

    override def expandVar(word: String) = word

  }

}

object ReplUiHarness extends UiHarness {

  override def clearUiVars(): Unit = {
    Repl.replVarMap.clear()
  }

  override def createExplorer(g: Game): Explorer = {
    throw EvalException("The Explorer is not available in the CGSuite REPL.")
  }

  override def print(obj: AnyRef): Unit = {
    val output = EvalUtil.objectToOutput(obj)
    output foreach { out => println(out.toString) }
  }

}

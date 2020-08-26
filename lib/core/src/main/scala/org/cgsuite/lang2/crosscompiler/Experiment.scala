package org.cgsuite.lang2.crosscompiler

import ch.qos.logback.classic.{Level, Logger}
import org.cgsuite.lang2.parser.ParserUtil
import org.cgsuite.lang2.{CgscriptClass, CompileContext, ElaborationDomain2, StatementSequenceNode}
import org.slf4j.LoggerFactory

import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.runtime.universe
import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.Settings

object Experiment {

  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true

  def eval = new IMain(settings)

  def evaluate(str: String) = {
    val eval = this.eval
    eval.interpret(str)
    eval.valueOfTerm("res0").get
  }

  def testCC(str: String) = {
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.DEBUG)
    CgscriptClass.Object.ensureLoaded()
    val tree = ParserUtil.parseScript(str)
    val node = StatementSequenceNode(tree.getChild(0))
    node.elaborate2(new ElaborationDomain2(None))
    println(node.elaboratedType)
    val code = node.toScalaCode(new CompileContext())
    println(code)
    evaluate(code)
  }

}

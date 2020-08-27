package org.cgsuite.lang2.crosscompiler

import ch.qos.logback.classic.{Level, Logger}
import org.cgsuite.lang2.parser.ParserUtil
import org.cgsuite.lang2.{CgscriptClass, CompileContext, ElaborationDomain2, StatementSequenceNode}
import org.cgsuite.lang2.Node.treeToRichTree
import org.slf4j.LoggerFactory

import scala.tools.nsc.interpreter.IMain
import scala.tools.nsc.Settings

object Experiment {

  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true

  val eval = new IMain(settings)
  val domain = new ElaborationDomain2(None)

  def testCC(str: String): Unit = {
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.DEBUG)
    CgscriptClass.Object.ensureLoaded()
    val tree = ParserUtil.parseScript(str)
    val node = StatementSequenceNode(tree.children.head, topLevel = true)
    node.ensureElaborated(domain)
    println(node.elaboratedType)
    val code = node.toScalaCodeWithVarDecls(new CompileContext())
    code foreach println
    code foreach eval.interpret
  }

}

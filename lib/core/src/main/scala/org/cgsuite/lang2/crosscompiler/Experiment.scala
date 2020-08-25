package org.cgsuite.lang2.crosscompiler

import org.cgsuite.lang2.parser.ParserUtil
import org.cgsuite.lang2.{CompileContext, StatementSequenceNode}

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
    val tree = ParserUtil.parseScript(str)
    val node = StatementSequenceNode(tree.getChild(0))
    val code = node.toScalaCode(new CompileContext())
    println(code)
    evaluate(code)
  }

}

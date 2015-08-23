package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core.CanonicalShortGameOps
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.Output

import scala.collection.mutable


object Repl {

  def main(args: Array[String]) {

    println("Welcome to the CGSuite REPL, Version 2.0.")

    CgscriptClass.Object.ensureLoaded()
    val replVarMap = mutable.AnyRefMap[Symbol, Any]()

    while (true) {
      print("> ")
      val str = Console.in.readLine
      str.trim match {
        case "" =>
        case ":clear" =>
          CgscriptClass.clearAll()
        case _ =>
          val start = JSystem.nanoTime
          try {
            val tree = ParserUtil.parseScript(str)
            println(tree.toStringTree)
            val node = EvalNode(tree.getChild(0))
            val scope = ElaborationDomain(None, Seq.empty, None)
            node.elaborate(scope)
            println(node)
            val domain = new Domain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(replVarMap))
            val result = node.evaluate(domain)
            val output = CgscriptClass.of(result).classInfo.toOutputMethod.call(result, Array.empty)
            assert(output.isInstanceOf[Output], output.getClass)
            println(output)
          } catch {
            case exc: Throwable => exc.printStackTrace()
          }
          val totalDuration = JSystem.nanoTime - start
          println(s"${totalDuration / 1000000} ms")
      }

    }

  }

}

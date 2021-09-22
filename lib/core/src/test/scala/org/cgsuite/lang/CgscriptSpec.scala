package org.cgsuite.lang

import org.cgsuite.exception.{CgsuiteException, SyntaxException}
import org.cgsuite.lang.node.EvalNode
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.Output
import org.scalatest.prop.{PropertyChecks, TableFor3}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

// CGScript Functional Tests.

trait CgscriptSpec extends FlatSpec with Matchers with PropertyChecks {

  val header = ("Test Name", "input", "expected output")

  val testPackage = CgscriptPackage.root declareSubpackage "test"

  def decl(name: String, explicitDefinition: String): Unit = {
    CgscriptClass declareSystemClass (name, explicitDefinition = Some(explicitDefinition))
  }

  def executeTests(tests: TableFor3[String, String, String], preamble: String = ""): Unit = {

    CgscriptClass.clearAll()
    CgscriptClass.Object.ensureInitialized()

    val varMap = mutable.AnyRefMap[Symbol, Any]()

    if (preamble != "") parseResult(preamble, varMap)

    forAll(tests) { (_, input: String, expectedOutput: String) =>
      if (expectedOutput != null && (expectedOutput startsWith "!!")) {
        val thrown = the [CgsuiteException] thrownBy EvalUtil.evaluateScript(input, varMap)
        thrown.getMessage shouldBe (expectedOutput stripPrefix "!!")
        if (!thrown.isInstanceOf[SyntaxException])
          thrown.tokenStack should not be empty
      } else {
        val output = EvalUtil.evaluateScript(input, varMap)
        if (expectedOutput == null) {
          output shouldBe empty
        } else {
          output.length shouldBe 1
          output.head.toString shouldBe expectedOutput
        }
      }
    }

  }

  def parseResult(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Any = {
    val tree = ParserUtil.parseScript(input)
    val node = EvalNode(tree.getChild(0))
    val scope = ElaborationDomain(None, Seq.empty, None)
    node.elaborate(scope)
    val domain = new EvaluationDomain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
    node.evaluate(domain)
  }

}

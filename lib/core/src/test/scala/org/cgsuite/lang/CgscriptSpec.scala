package org.cgsuite.lang

import org.cgsuite.exception.{CgsuiteException, SyntaxException}
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
        val thrown = the [CgsuiteException] thrownBy parseResult(input, varMap)
        thrown.getMessage shouldBe (expectedOutput stripPrefix "!!")
        if (!thrown.isInstanceOf[SyntaxException])
          thrown.tokenStack should not be empty
      } else {
        val result = parseResult(input, varMap)
        val output = CgscriptClass.of(result).classInfo.toOutputMethod.call(result, Array.empty)
        if (expectedOutput == null) {
          output.asInstanceOf[AnyRef].shouldEqual(null)
        } else {
          output shouldBe an[Output]
          output.toString shouldBe expectedOutput
        }
      }
    }

  }

  def parseResult(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Any = {
    val tree = ParserUtil.parseScript(input)
    val node = EvalNode(tree.getChild(0))
    val scope = ElaborationDomain(None, Seq.empty, None)
    node.elaborate(scope)
    val domain = new Domain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
    node.evaluate(domain)
  }

}

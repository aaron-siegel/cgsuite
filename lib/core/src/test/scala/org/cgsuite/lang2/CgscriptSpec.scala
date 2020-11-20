package org.cgsuite.lang2

import org.cgsuite.output.EmptyOutput
import org.scalatest.prop.{PropertyChecks, TableFor3}
import org.scalatest.{FlatSpec, Matchers}

// CGScript Functional Tests.

trait CgscriptSpec extends FlatSpec with Matchers with PropertyChecks {

  val header = ("Test Name", "input", "expected output")

  val testPackage = CgscriptPackage.root declareSubpackage "test"

  def decl(name: String, explicitDefinition: String): Unit = {
    println(s"Declaring test class: $name")
    CgscriptClass declareSystemClass (name, explicitDefinition = Some(explicitDefinition))
  }

  def executeTests(tests: TableFor3[String, String, String], preamble: String = ""): Unit = {

    assert(preamble == "")

    //if (preamble != "") parseResult(preamble, varMap)

    forAll(tests) { (_, input: String, expectedOutput: String) =>
      val result = CgscriptSystem.evaluate(input)
      if (expectedOutput != null && (expectedOutput startsWith "!!")) {
        assert(result.isRight)
        result.right.get.getMessage shouldBe (expectedOutput stripPrefix "!!")
        /*
        if (!thrown.isInstanceOf[SyntaxException])
          thrown.tokenStack should not be empty
         */
      } else {
        // Not expecting an exception, so throw one if found (for debugging)
        result.right foreach { throw _ }
        if (expectedOutput == null) {
          assert(result.left.get == EmptyOutput)
        } else {
          assert(result.left.get != null)
          result.left.get.toString shouldBe expectedOutput
        }
      }
    }

  }
/*
  def parseResult(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Any = {
    val tree = ParserUtil.parseScript(input)
    val node = EvalNode(tree.getChild(0))
    val scope = ElaborationDomain(None, Seq.empty, None)
    node.elaborate(scope)
    val domain = new EvaluationDomain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
    node.evaluate(domain)
  }
*/
}

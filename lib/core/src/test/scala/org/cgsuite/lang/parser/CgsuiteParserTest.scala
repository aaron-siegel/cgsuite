package org.cgsuite.lang.parser

import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.lang._
import org.cgsuite.lang.node.EvalNode
import org.scalatest.{FlatSpec, Matchers}


class CgsuiteParserTest extends FlatSpec with Matchers {

  "CgsuiteParser" should "parse simple expressions" in {

    testExpr("5 + 4", Integer(9))

  }

  it should "parse ups and stars" in {

    testExpr("*", star)
    testExpr("*2", starTwo)
    testExpr("*(1+1)", starTwo)
    testExpr("^", up)
    testExpr("^*", upStar)
    testExpr("^*2", up + starTwo)
    testExpr("^^", up + up)
    testExpr("^^^", up + up + up)
    testExpr("^3", up + up + up)
    testExpr("^^*", up + up + star)
    testExpr("v", down)
    testExpr("vv", down + down)
    testExpr("v3", down + down + down)
    testExpr("v3*2", down + down + down + starTwo)

  }

  def testExpr(str: String, expected: Any) = {

    val tree = ParserUtil.parseExpression(str)
    val node = EvalNode(tree)
    val scope = ElaborationDomain(None, Seq.empty, None)
    node.elaborate(scope)
    val domain = new EvaluationDomain(new Array[Any](scope.localVariableCount))
    val result = node.evaluate(domain)
    result shouldBe expected

  }

}

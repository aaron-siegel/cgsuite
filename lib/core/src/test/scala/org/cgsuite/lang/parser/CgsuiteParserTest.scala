package org.cgsuite.lang.parser

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._
import org.cgsuite.core._
import org.cgsuite.core.Values._

@RunWith(classOf[JUnitRunner])
class CgsuiteParserTest extends Specification {

  "CgsuiteParser" should {

    "parse" in {

      testExpr("5 + 4", Integer(9))

    }

    "parse ups and stars" in {

      testExpr("*", star)
      testExpr("*2", starTwo)
      //testExpr("*(1+1)", starTwo)
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

  }

  def testExpr(str: String, expected: Any) = {

    val tree = ParserUtil.parseExpression(str)
    new Domain().expression(tree) must_== expected

  }

}

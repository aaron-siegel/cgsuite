package org.cgsuite.lang

import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.Output
import org.scalatest.prop.{TableFor3, PropertyChecks}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

// CGScript Functional Tests.

class CgscriptTest extends FlatSpec with Matchers with PropertyChecks {

  "CGScript" should "process basic expressions" in {

    execute(Table(
      ("Test Name", "input", "expected output"),
      ("Simple echo", "0", "0"),
      ("Semicolon suppress output", "0;", "nil"),
      ("Variable assignment", "g := 7", "7"),
      ("Variable retrieval", "g", "7"),
      ("Multivee/identifier parse check", "vvvvx := vvvvv", "v5"),
      ("Assign to name of class", "Integer := 5", "!!Cannot assign to class name as variable: `Integer`")
    ))

  }

  it should "process arithmetic expressions" in {

    execute(Table(
      ("Test Name", "input", "expected output"),
      ("Integer addition", "3+5", "8"),
      ("Integer multiplication", "3*5", "15"),
      ("Integer exponentiation", "3^5", "243"),
      ("Rational number", "4/6", "2/3"),
      ("Rational exponentiation", "(1/2)^4", "1/16"),
      ("Negative power", "2^(-4)", "1/16"),
      ("Negative power of rational", "(2/3)^(-3)", "27/8"),
      ("Integer modulus", "17 % 5", "2"),
      ("Rational modulus", "(17/6) % (1/3)", "1/6"),
      ("Nimber addition", "*3+*5", "*6"),
      ("Nimber operator", "*(3+5)", "*8"),
      ("Ups", "^^^^^^+vvv*+^19*3+v14", "^8*2"),
      ("Integer plus rational", "2 + 3/4", "11/4"),
      ("Integer plus canonical game", "2 + (1+*)", "3*"),
      ("Integer minus rational", "2 - 3/4", "5/4"),
      ("Integer minus canonical game", "2 - (1+*)", "1*"),
      ("Dyadic plus non-dyadic", "1/2 + 1/3", "5/6"),
      ("Dyadic times non-dyadic", "(1/2) * (1/3)", "1/6")
    ))

  }

  it should "correctly interpret canonical forms" in {
    execute(Table(
      ("Test Name", "input", "expected output"),
      ("Composition", "{0|^*5}", "^^*4"),
      ("Slashes", "{3|||2||1|0,*||||-1/2}", "{3|||2||1|0,*||||-1/2}"),
      ("Ambiguous slashes", "{3|2|1}", "!!Syntax error: null"),
      ("Floating slash", "1|0", "!!Syntax error: missing EOF at '|'"),
      ("Switch", "+-1", "+-1"),
      ("Fractional switch", "+-1/2", "+-1/2"),
      ("Incorrect multiple switch syntax", "+-(1,1+*)", "!!No operation `(,)` for arguments of types `game.Integer`, `game.NumberUpStar`"),
      ("Multiple switch", "+-{1,1+*}", "+-{1,1*}"),
      ("Number + switch", "3+-1", "{4|2}"),
      ("Compound switch", "+-1+-2+-3+-4", "+-{10|8||6|4|||4|2||0|-2}"),
      ("Tiny", "{0||0|-1}", "1.Tiny"),
      ("Tiny fraction", "{0||0|-1/4}", "(1/4).Tiny"),
      ("Tiny G", "{0|||0||-1|-2}", "{2|1}.Tiny"),
      ("Miny", "{1|0||0}", "1.Miny"),
      ("Pow", "{0|v*}", "^.Pow(2)"),
      ("Pow*", "{0,*|v}", "^.Pow(2)+*"),
      ("PowTo", "{^|*}", "^.PowTo(2)"),
      ("PowTo*", "{0,^*|0}", "^.PowTo(2)+*"),
      ("Explicit game", "'{*|*}'", "'{*|*}'"),
      ("Explicit game ordinal sum", "'{*|*}':1", "^")
    ))
  }

  it should "correctly interpret loopy game specs" in {
    execute(Table(
      ("Test Name", "input", "expected output"),
      ("on", "on := {pass|}", "on"),
      ("off", "off := {|pass}", "off"),
      //("dud", "{pass|pass}", "dud"),
      ("over", "{0|pass}", "over"),
      ("under", "{pass|0}", "under"),
      ("upon", "[{pass|*},{pass,0|0},{*|pass},{0|0,pass}]", "[^[on],^[on]*,v[on],v[on]*]"),
      ("uponth", "[{0||0|0,pass},{0,*||*|pass},{0,pass|0||0},{pass|*||0,*}]", "[^on,^on*,von,von*]"),
      ("Hanging pass", "pass", "!!Unexpected `pass`."),
      ("Hanging pass 2", "{1|0+pass}", "!!Unexpected `pass`."),
      ("loopy plus number", "{0|pass}+5", "5over"),
      ("loopy plus number (under)", "{pass|0}+5", "5under"),
      ("loopy plus number (upon)", "listof(5+x for x in [{pass|*},{pass,0|0},{*|pass},{0|0,pass}])", "[5^[on],5^[on]*,5v[on],5v[on]*]"),
      ("loopy plus number (uponth)", "listof(5+x for x in [{0||0|0,pass},{0,*||*|pass},{0,pass|0||0},{pass|*||0,*}])", "[5^on,5^on*,5von,5von*]"),
      ("loopy plus canonical", "{0|pass}+^", "over"),
      ("number plus loopy", "5+{0|pass}", "5over"),
      ("loopy minus number", "{0|pass}-5", "-5over"),
      //("explicit stopper sided", "{0|pass} & v", "over & v"),
      ("over by node label", "x{0|x}", "over"),
      ("under by node label", "x{x|0}", "under"),
      ("canonical 4-cycle", "x{0||||0|||x|*||*}", "a{0||||0|||a|*||*}"),
      ("+- loopy game", "uponth := {0||0|0,pass}; +-{0|uponth}", "{0|^on||von|0}"),
      ("multiple +- loopy game", "+-{{0|uponth},{0|uponth+*}}", "{{0|^on},{0|^on*}|{von*|0},{von|0}}")
      //("not stopper-sided", "a{0,{1|1,{*,{1+*|1+*,a}|*}}|0}", "!!That game is not stopper-sided.")
    ))
  }

  it should "simplify properly" in {
    execute(Table(
      ("Test Name", "input", "expected output"),
      ("Multiplier simplification", "(*+*)*3", "0"),
      ("Integer exponent simplification", "2^(*+*)", "1"),
      ("+- loopy game simplification", "uponth := {0||0|0,pass}; +-(uponth+*)", "0")
    ))
  }

  it should "handle comparison operators" in {
    execute(Table(
      ("Test Name", "input", "expected output"),
      ("Heterogeneous compare", "3 == nil", "false")
    ))
  }

  it should "construct collections properly" in {
    execute(Table(
      ("Test Name", "input", "expected output"),
      ("Empty List", "[]", "nil"),
      ("List", "[3,5,3,1]", "[3,5,3,1]"),
      ("Empty Set", "{}", "{}"),
      ("Set", "{3,5,3,1}", "{1,3,5}"),
      ("Heterogeneous set", """{3,"foo",nil,true,[3,5,3,1],+-6,*2,"bar"}""", """{3,*2,+-6,true,"bar","foo",nil,[3,5,3,1]}"""),
      ("Empty Map", "{=>}", "{=>}"),
      ("Map", """{"foo" => 1, "bar" => *2, 16 => 22}""", """{16 => 22, "bar" => *2, "foo" => 1}"""),
      //("Range", "3..7", "[3,4,5,6,7]"),
      ("Listof", "listof(x^2 for x from 1 to 5)", "[1,4,9,16,25]"),
      ("Setof", "setof(x^2 for x in [1,3,5,3])", "{1,9,25}"),
      ("Sumof", "sumof(n for n from 1 to 10)", "55"),
      ("Sumof 2", """sumof("foo" for x from 1 to 4)""", """"foofoofoofoo""""),
      ("Tableof", "tableof([n,n^2] for n from 1 to 3)",
        """|1 | 1
           |--+--
           |2 | 4
           |--+--
           |3 | 9""".stripMargin)
    ))
  }

  def execute(tests: TableFor3[String, String, String]): Unit = {

    CgscriptClass.clearAll()
    CgscriptClass.Object.ensureLoaded()

    val varMap = mutable.AnyRefMap[Symbol, Any]()

    forAll(tests) { (_, input: String, expectedOutput: String) =>
      if (expectedOutput startsWith "!!") {
        val thrown = the [InputException] thrownBy parseResult(input, varMap)
        thrown.getMessage shouldBe (expectedOutput stripPrefix "!!")
      } else {
        val result = parseResult(input, varMap)
        val output = CgscriptClass.of(result).classInfo.toOutputMethod.call(result, Array.empty)
        output shouldBe an[Output]
        output.toString shouldBe expectedOutput
      }
    }

  }

  def parseResult(input: String, varMap: mutable.AnyRefMap[Symbol, Any]): Any = {
    val tree = ParserUtil.parseScript(input)
    val node = EvalNode(tree.getChild(0))
    val scope = Scope(None, Set.empty)
    node.elaborate(scope)
    val domain = new Domain(new Array[Any](scope.varMap.size), dynamicVarMap = Some(varMap))
    node.evaluate(domain)
  }

}
package org.cgsuite.lang

import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.Output
import org.scalatest.prop.{TableFor3, PropertyChecks}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

// CGScript Functional Tests.

class CgscriptTest extends FlatSpec with Matchers with PropertyChecks {

  val header = ("Test Name", "input", "expected output")

  "CGScript" should "process basic expressions" in {

    execute(Table(
      header,
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
      header,
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
      header,
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
      header,
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
      header,
      ("Multiplier simplification", "(*+*)*3", "0"),
      ("Integer exponent simplification", "2^(*+*)", "1"),
      ("+- loopy game simplification", "uponth := {0||0|0,pass}; +-(uponth+*)", "0")
    ))
  }

  it should "handle comparison operators" in {
    execute(Table(
      header,
      ("Heterogeneous compare", "3 == nil", "false")
    ))
  }

  it should "construct collections properly" in {
    execute(Table(
      header,
      ("Empty List", "[]", "nil"),
      ("List", "[3,5,3,1]", "[3,5,3,1]"),
      ("Empty Set", "{}", "{}"),
      ("Set", "{3,5,3,1}", "{1,3,5}"),
      ("Heterogeneous set", """{3,"foo",nil,true,[3,5,3,1],+-6,*2,"bar"}""", """{3,*2,+-6,true,"bar","foo",nil,[3,5,3,1]}"""),
      ("Empty Map", "{=>}", "{=>}"),
      ("Map", """{"foo" => 1, "bar" => *2, 16 => 22}""", """{16 => 22, "bar" => *2, "foo" => 1}"""),
      ("Range", "3..12", "3..12"),
      ("Range w/ Step", "3..12..3", "3..12..3"),
      ("Empty Range", "1..0", "nil"),
      ("Range -> Explicit", "(3..12).ToSet", "{3,4,5,6,7,8,9,10,11,12}"),
      ("Range -> Explicit 2", "(3..12..3).ToSet", "{3,6,9,12}"),
      ("Range Equals List", "1..4 === [1,2,3,4]", "true"),
      ("Empty Range Equals List", "1..0 === nil", "true"),
      ("Listof", "listof(x^2 for x from 1 to 5)", "[1,4,9,16,25]"),
      ("Multi-listof", "listof(x^2+y^2 for x from 1 to 5 for y from 1 to 5)", "[2,5,10,17,26,5,8,13,20,29,10,13,18,25,34,17,20,25,32,41,26,29,34,41,50]"),
      ("Setof", "setof(x^2 for x in [1,3,5,3])", "{1,9,25}"),
      ("Multi-setof", "setof(x^2+y^2 for x from 1 to 5 for y from 1 to 5)", "{2,5,8,10,13,17,18,20,25,26,29,32,34,41,50}"),
      ("Sumof", "sumof(n for n from 1 to 10)", "55"),
      ("Sumof 2", """sumof("foo" for x from 1 to 4)""", """"foofoofoofoo""""),
      ("Multi-sumof", "sumof(m+n for m from 1 to 10 for n from 1 to 10)", "1100"),
      ("Tableof", "tableof([n,n^2] for n from 1 to 3)",
        """|1 | 1
           |--+--
           |2 | 4
           |--+--
           |3 | 9""".stripMargin)
    ))
  }

  it should "handle various types of loops correctly" in {

    // (name, initializer, fn, for-snippet, result, optional-sorted-result, sum)
    val loopScenarios = Seq(
      ("for-from-to", "", "x*x", "for x from 1 to 5", "1,4,9,16,25", None, "55"),
      ("for-from-to-by", "", "x*x", "for x from 1 to 10 by 3", "1,16,49,100", None, "166"),
      ("for-from-to-by (neg)", "", "x*x", "for x from 6 to 1 by -2", "36,16,4", Some("4,16,36"), "56"),
      ("for-from-to-by (game)", "", "x", "for x from 0 to ^5 by ^*", "0,^*,^^,^3*,^4", Some("0,^*,^^,^3*,^4"), "^10"),
      ("for-from-while", "", "x", "for x from 1 while x < 5", "1,2,3,4", None, "10"),
      ("for-from-to-where", "", "x", "for x from 1 to 10 where x % 3 == 1", "1,4,7,10", None, "22"),
      ("for-from-while-where", "", "x", "for x from 1 while x < 10 where x % 3 == 1", "1,4,7", None, "12"),
      ("for-in", "", "x*x", "for x in [1,2,3,2]", "1,4,9,4", Some("1,4,9"), "18"),
      ("for-in-where", "", "x", "for x in [1,2,3,2] where x % 2 == 1", "1,3", None, "4"),
      ("for-in-while", "", "x", "for x in [1,2,3,2] while x % 2 == 1", "1", None, "1"),
      ("for-in-while-where", "", "x", "for x in [1,2,3,2] while x % 2 == 1 where x != 1", null, Some(""), "nil")
    )

    val listofLoops = loopScenarios map { case (name, init, fn, snippet, result, _, _) =>
      val resultStr = Option(result) map { x => s"[$x]" } getOrElse "nil"
      (s"listof: $name", s"${init}listof($fn $snippet)", s"$resultStr")
    }

    val setofLoops = loopScenarios map { case (name, init, fn, snippet, result, sortedResult, _) =>
      val setResult = sortedResult getOrElse result
      (s"setof: $name", s"${init}setof($fn $snippet)", s"{$setResult}")
    }

    val yieldLoops = loopScenarios map { case (name, init, fn, snippet, result, _, _) =>
      val resultStr = Option(result) map { x => s"[$x]" } getOrElse "nil"
      (s"yield: $name", s"$init$snippet yield $fn end", s"$resultStr")
    }

    val sumofLoops = loopScenarios map { case (name, init, fn, snippet, _, _, sum) =>
      (s"sumof: $name", s"${init}sumof($fn $snippet)", s"$sum")
    }

    execute(Table(header, listofLoops : _*))
    execute(Table(header, setofLoops : _*))
    execute(Table(header, yieldLoops : _*))
    execute(Table(header, sumofLoops : _*))

  }

  it should "properly construct and evaluate procedures" in {

    execute(Table(
      header,
      ("Procedure definition", "f := x -> x+1", "x -> (x + 1)"),
      ("Procedure evaluation", "f(8)", "9"),
      ("Procedure scope 1", "y := 3; f := x -> x+y; f(5)", "8"),
      ("Procedure scope 2", "y := 6; f(5)", "11"),
      ("Procedure scope 3", "x := 9; f(5); x", "9"),
      ("Procedure scope 4", "f := temp -> temp+1; f(5); temp", "!!That variable is not defined: `temp`"),
      ("No-parameter procedure", "f := () -> 3", "() -> 3"),
      ("No-parameter procedure evaluation", "f()", "3"),
      ("Multiparameter procedure", "f := (x,y) -> (x+y)/2", "(x, y) -> ((x + y) / 2)"),
      ("Multiparameter procedure evaluation", "f(3,4)", "7/2"),
      ("Procedure - too few args", "f(3)", "!!Expecting 2 argument(s); found 1"),
      ("Procedure - too many args", "f(3,4,5)", "!!Expecting 2 argument(s); found 3"),
      ("Procedure - optional arg", "f(3,4,foo => 5)", "!!Invalid optional parameter: foo")
    ))

  }

  "cgsuite.lang.Collection" should "return correct answers" in {

    // (call, seq, expected result)
    val collectionScenarios = Seq(
      ("Contains(4)", "0,1,7,5,3,2,9", "false"),
      ("Contains(9)", "0,1,7,5,3,2,9", "true"),
      ("IsEmpty", "", "true"),
      ("IsEmpty", "0,1,3,4", "false"),
      ("ToList.Sorted", "0,1,7,5,3,2,9", "[0,1,2,3,5,7,9]"),
      ("ToSet", "0,1,7,5,3,2,9", "{0,1,2,3,5,7,9}")
    )

    val lists = collectionScenarios map { case (call, seq, result) =>
      (s"List: $call", s"[$seq].$call", result)
    }

    val sets = collectionScenarios map { case (call, seq, result) =>
      (s"Set: $call", s"{$seq}.$call", result)
    }

    execute(Table(header, lists : _*))
    execute(Table(header, sets : _*))

  }

  "cgsuite.lang.Range" should "implement Collection faithfully" in {

    execute(Table(
      header,
      ("Range: Contains(4)", "(1..10..2).Contains(4)", "false"),
      ("Range: Contains(9)", "(1..10..2).Contains(9)", "true"),
      ("Range: IsEmpty", "(1..0).IsEmpty", "true"),
      ("Range: IsEmpty", "(1..10).IsEmpty", "false"),
      ("Range: ToList", "(1..10).ToList", "[1,2,3,4,5,6,7,8,9,10]"),
      ("Range: ToSet", "(1..10).ToSet", "{1,2,3,4,5,6,7,8,9,10}")
    ))

  }

  "game.Integer" should "implement methods correctly" in {

    execute(Table(
      header,
      ("MinValue", "-2147483648", "-2147483648"),
      ("MinValue-1", "-2147483649", "-2147483649"),
      ("Max", "3.Max(-2147483648)", "3"),
      ("NimSum", "3.NimSum(5)", "6"),
      ("NimProduct", "8.NimProduct(8)", "13"),
      ("Div", "17.Div(4)", "4"),
      ("IsTwoPower", "[32.IsTwoPower,48.IsTwoPower]", "[true,false]"),
      ("IsSmallInteger", "[2^31,-2^31,2^31-1,-2^31-1].Apply(x -> x.IsSmallInteger)", "[false,true,true,false]"),
      ("Lb", "[15,16,17,31,2^31-1,2^31].Apply(x -> x.Lb)", "[3,4,4,4,30,31]"),
      ("Lb(0)", "0.Lb", "!!Error in call to `game.Integer.Lb`: Logarithm of 0"),
      //("Random", "Integer.SetSeed(0); listof(Integer.Random(100) from 1 to 5)", "[61,49,30,48,16]"),
      ("Isqrt",
        """
          |for n from 0 to 3000 do
          |  var isqrt := n.Isqrt;
          |  if n < isqrt^2 or n >= (isqrt+1)^2 then
          |    error("Isqrt failed at " + n.ToString);
          |  end
          |end""".stripMargin, "nil")
    ))

  }

  "game.Rational" should "implement methods correctly" in {

    // x, class, numerator, denominator, isDyadic, abs, floor, ceiling, reciprocal
    val unaryInstances = Seq(
      ("0/7", "0", "Zero", "0", "1", "true", "0", "0", "0", "inf"),
      ("19/1", "19", "Integer", "19", "1", "true", "19", "19", "19", "1/19"),
      ("-19/1", "-19", "Integer", "-19", "1", "true", "19", "-19", "-19", "-1/19"),
      ("3/16", "3/16", "DyadicRational", "3", "16", "true", "3/16", "0", "1", "16/3"),
      ("-3/16", "-3/16", "DyadicRational", "-3", "16", "true", "3/16", "-1", "0", "-16/3"),
      ("3^22/2^72", "31381059609/4722366482869645213696", "DyadicRational", "31381059609", "4722366482869645213696", "true", "31381059609/4722366482869645213696", "0", "1", "31381059609/4722366482869645213696"),
      ("-3^22/2^72", "-31381059609/4722366482869645213696", "DyadicRational", "-31381059609", "4722366482869645213696", "true", "31381059609/4722366482869645213696", "-1", "0", "-31381059609/4722366482869645213696"),
      ("6/23", "6/23", "Rational", "6", "23", "false", "6/23", "0", "1", "23/6"),
      ("-6/23", "-6/23", "Rational", "-6", "23", "false", "6/23", "-1", "0", "-23/6"),
      ("17/0", "inf", "Rational", "1", "0", "false", "inf", "!!Error in call to `game.Rational.Floor`: / by zero", "!!Error in call to `game.Rational.Ceiling`: / by zero", "0"),
      ("-17/0", "-inf", "Rational", "-1", "0", "false", "inf", "!!Error in call to `game.Rational.Floor`: / by zero", "!!Error in call to `game.Rational.Ceiling`: / by zero", "0")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, num, den, isDyadic, abs, floor, ceiling, reciprocal) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"<<game.$cls>>"),
        (s"($x).Numerator", num),
        (s"($x).Denominator", den),
        (s"($x).IsDyadic", isDyadic),
        (s"($x).Abs", abs),
        (s"($x).Floor", floor),
        (s"($x).Ceiling", ceiling)
      )
    } map { case (expr, result) => (expr, expr, result) }

    // x, y, max, min
    val binaryInstances = Seq(
      ("0", "1/7", "1/7", "0"),
      ("-3", "14/5", "14/5", "-3"),
      ("inf", "6", "inf", "6"),
      ("inf", "-inf", "inf", "-inf")
    )

    val binaryTests = binaryInstances flatMap { case (x, y, max, min) =>
      Seq(
        (s"($x).Max($y)", max),
        (s"($y).Max($x)", max),
        (s"($x).Min($y)", min),
        (s"($y).Min($x)", min)
      )
    } map { case (expr, result) => (expr, expr, result) }

    execute(Table(header, unaryTests ++ binaryTests : _*))

  }

  "game.CanonicalShortGame" should "implement 0-ary methods correctly" in {

    val tests = CanonicalShortGameTestCase.instances flatMap { _.toTests }

    execute(Table(header, tests : _*))

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

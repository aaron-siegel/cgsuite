package org.cgsuite.lang

import org.cgsuite.exception.{CgsuiteException, InputException}
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
      ("Fractional switch", "+-(1/2)", "+-1/2"),
      ("Incorrect multiple switch syntax", "+-(1,1+*)", "!!No operation `(,)` for arguments of types `game.Integer`, `game.Uptimal`"),
      ("Multiple switch", "+-{1,1+*}", "+-{1,1*}"),
      ("Number + switch", "3+-1", "{4|2}"),
      ("Compound switch", "+-1+-2+-3+-4", "+-{10|8||6|4|||4|2||0|-2}"),
      ("Tiny", "{0||0|-1}", "Tiny(1)"),
      ("Tiny fraction", "{0||0|-1/4}", "Tiny(1/4)"),
      ("Tiny G", "{0|||0||-1|-2}", "Tiny({2|1})"),
      ("Miny", "{1|0||0}", "Miny(1)"),
      ("Pow", "{0|v*}", "^<2>"),
      ("Pow*", "{0,*|v}", "^<2>*"),
      ("PowTo", "{^|*}", "^[2]"),
      ("PowTo*", "{0,^*|0}", "^[2]*"),
      ("Explicit game", "'{*|*}'", "'{*|*}'")
      //("Explicit game ordinal sum", "'{*|*}':1", "^")
    ))
  }

  it should "respect order of operations" in {
    execute(Table(
      header,
      ("Sums and differences", "5 - 3 + 4 - 8", "-2"),
      ("Products and divs", "5 - 3 / 4 * 6 - 2", "-3/2"),
      ("Exp", "5 * 3 ^ 2 / 4", "45/4"),
      ("PlusMinus", "3 - 2 +- 6 - 7 +- 8 + 4", "{12|0||-4|-16}")
    ))
  }

  it should "correctly interpret loopy game specs" in {
    execute(Table(
      header,
      ("on", "on", "on"),
      ("off", "off", "off"),
      ("dud", "{pass|pass}", "dud"),
      ("over", "over", "over"),
      ("under", "under", "under"),
      ("upon", "[{pass|*},{pass,0|0},{*|pass},{0|0,pass}]", "[^[on],^[on]*,v[on],v[on]*]"),
      ("uponth", "[{0||0|0,pass},{0,*||*|pass},{0,pass|0||0},{pass|*||0,*}]", "[^<on>,^<on>*,v<on>,v<on>*]"),
      ("Hanging pass", "pass", "!!Unexpected `pass`."),
      ("Hanging pass 2", "{1|0+pass}", "!!Unexpected `pass`."),
      ("loopy plus number", "over+5", "5over"),
      ("loopy plus number (under)", "under+5", "5under"),
      ("loopy plus number (upon)", "listof(5+x for x in [{pass|*},{pass,0|0},{*|pass},{0|0,pass}])", "[5^[on],5^[on]*,5v[on],5v[on]*]"),
      ("loopy plus number (uponth)", "listof(5+x for x in [{0||0|0,pass},{0,*||*|pass},{0,pass|0||0},{pass|*||0,*}])", "[5^<on>,5^<on>*,5v<on>,5v<on>*]"),
      ("loopy plus canonical", "over+^", "over"),
      ("number plus loopy", "5+over", "5over"),
      ("loopy minus number", "over-5", "-5over"),
      ("explicit stopper sided", "over & v", "over & v"),
      ("over by node label", "x{0|x}", "over"),
      ("under by node label", "x{x|0}", "under"),
      ("canonical 4-cycle", "x{0||||0|||x|*||*}", "a{0||||0|||a|*||*}"),
      ("+- loopy game", "uponth := {0||0|0,pass}; +-{0|uponth}", "+-{0|^<on>}"),
      ("multiple +- loopy game", "+-{{0|uponth},{0|uponth+*}}", "+-{{0|^<on>},{0|^<on>*}}"),
      ("stopper-sided", "a{1||a|0}", "2 & +-1"),
      ("not stopper-sided", "a{0,{1|1,{*,{1+*|1+*,a}|*}}|0}", "a{1|1,{1*|a||*}||0} & a{0,{1|||1*|a||*}|0}")
    ))
  }

  it should "process game operations" in {

    execute(Table(
      header,
      ("sided + sided", "a{0,{1|1,{*,{1+*|1+*,a}|*}}|0}+1", "a{2|2,{2*|a||1*}||1} & a{1,{2|||2*|a||1*}|1}"),
      ("even integer * uptimal", "10*(1+^.Pow(2)+*4)", "{10||10,10v*|{10vv,10v*||||10v3*,10vv|||10v4,10v3*||10v5*,10v4|{10v6,10v5*||||10v7*,10v6|||10v8,10v7*||10v9*,10v8|10v10}}}"),
      ("odd integer * uptimal", "9*(1+^.Pow(2)+*4)", "{9,9*,9*2,9*3,9*4|{9*4,9v*5||||9vv*4,9v*5|||9v3*5,9vv*4||9v4*4,9v3*5|{9v5*5,9v4*4||||9v6*4,9v5*5|||9v7*5,9v6*4||9v8*4,9v7*5|9v9*5}}}"),
      ("integer * canonical game", "5*{3||2|1}", "{12|||11||10,{11|10}|9Tiny({2|1})}"),
      ("integer * stopper", "10*upon", "{0|{0||||0|||0||0|{0||||0|||0||0|^[on]*}}}"),
      ("integer * stopper sided", "9*(upon & v)", "{0||||0|||0||0|{0||||0|||0||0|^[on]}} & v9"),
      ("integer * sided", "3*a{0,{1|1,{*,{1+*|1+*,a}|*}}|0}", "{a{2*|||2|2,{a|1*}||1}|2||d{2|b{1|1,{1*||b|0|||*}},{2*|c{1*|||1|1,{c|*}||0},d||0}||1}} & {0||0|a{1*||0,{1|a}|0|||*}}")
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
      ("Confused", "3 <> 3+*", "true"),
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
      ("Procedure definition - duplicate var", "(x, x) -> x", "!!Duplicate var: `x`"),
      ("Procedure evaluation", "f(8)", "9"),
      ("Procedure scope 1", "y := 3; f := x -> x+y; f(5)", "8"),
      ("Procedure scope 2", "y := 6; f(5)", "11"),
      ("Procedure scope 3", "x := 9; f(5); x", "9"),
      ("Procedure scope 4", "f := temp -> temp+1; f(5); temp", "!!That variable is not defined: `temp`"),
      ("No-parameter procedure", "f := () -> 3", "() -> 3"),
      ("No-parameter procedure evaluation", "f()", "3"),
      ("Multiparameter procedure", "f := (x,y) -> (x-y)/2", "(x, y) -> ((x - y) / 2)"),
      ("Multiparameter procedure evaluation", "f(3,4)", "-1/2"),
      ("Procedure eval - too few args", "f(3)", "!!Missing required parameter: `y`"),
      ("Procedure eval - too many args", "f(3,4,5)", "!!Too many arguments: 3 (expecting at most 2)"),
      ("Procedure eval - named args", "f(y => 3, x => 4)", "1/2"),
      ("Procedure eval - duplicate parameter (ordinary + named)", "f(3, x => 4)", "!!Duplicate parameter: `x`"),
      ("Procedure eval - duplicate parameter (named + named)", "f(y => 4, y => 5)", "!!Duplicate parameter: `y`"),
      ("Procedure eval - invalid named arg", "f(3, foo => 4)", "!!Invalid parameter name: `foo`"),
      ("Curried procedure definition", "f := x -> y -> x + y", "x -> y -> (x + y)"),
      ("Curried procedure evaluation - 1", "g := f(3)", "y -> (x + y)"),
      ("Curried procedure evaluation - 2", "h := f(5)", "y -> (x + y)"),
      ("Curried procedure evaluation - 3", "[g(7),h(7)]", "[10,12]"),
      ("Curried procedure definition - duplicate var", "x -> x -> (x + 3)", "!!Duplicate var: `x`"),
      ("Recursive procedure", "fact := n -> if n == 0 then 1 else n * fact(n-1) end; fact(6)", "720"),
      ("Closure",
        """f := () -> begin var x := nil; [y -> (x := y), () -> x] end;
          |pair1 := f(); set1 := pair1[1]; get1 := pair1[2]; pair2 := f(); set2 := pair2[1]; get2 := pair2[2];
          |set1("foo"); set2("bar"); [get1(), get2()]
        """.stripMargin, """["foo","bar"]"""),
      ("Procedure involving assignment - syntax error", "x -> y := x", "!!Syntax error.")
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

  "game.CanonicalShortGame" should "implement unary methods correctly" in {

    val tests = CanonicalShortGameTestCase.instances flatMap { _.toTests }

    execute(Table(header, tests : _*))

  }

  it should "implement >=2-ary methods correctly" in {

    val instances = Seq(
      ("*4.ConwayMultiply(*4)", "*6"),
      ("*19.ConwayMultiply(*23)", "*86"),
      ("15.ConwayMultiply(^^*2)", "^30*2"),
      ("{3|1}.ConwayMultiply(^^)", "{^6|^^}"),
      ("^^.ConwayMultiply(^^)", "{0||0,*|0,{0,*|0,*2}}"),
      ("{3||2+*|1+*}.Cool(0)", "{3||2*|1*}"),
      ("{3||2+*|1+*}.Cool(1/4)", "{11/4||2|3/2}"),
      ("{3||2+*|1+*}.Cool(1/2)", "{5/2|2*}"),
      ("{3||2+*|1+*}.Cool(5/8)", "{19/8|17/8}"),
      ("{3||2+*|1+*}.Cool(3/4)", "9/4*"),
      ("{3||2+*|1+*}.Cool(70)", "9/4"),
      ("{3||2+*|1+*}.Cool(-1/2)", "{7/2|||5/2|3/2||+-1/2}"),
      ("{3||2+*|1+*}.Cool(-1)", "!!Invalid cooling temperature (must be > -1): -1"),
      ("(1/4).Cool(-1/8)", "1/4"),
      ("(1/4).Cool(-1/4)", "1/4*"),
      ("(1/4).Cool(-3/8)", "{3/8|1/8}"),
      ("{3||2+*|1+*}.Heat(0)", "{3||2*|1*}"),
      ("{3||2+*|1+*}.Heat(1/2)", "{7/2|||5/2|3/2||+-1/2}"),
      ("{3||2+*|1+*}.Heat(1)", "{4|||3|1||0|-2}"),
      ("{3||2+*|1+*}.Heat(-3/4)", "9/4*"),
      ("{3||2+*|1+*}.Heat(-1)", "9/4"),
      ("{3||2+*|1+*}.Heat(-2)", "3/2"),
      ("{3||2+*|1+*}.Heat(-3)", "1"),
      ("{3||2+*|1+*}.Heat(-4)", "0"),
      ("{3||2+*|1+*}.Heat(*)", "{3*||2|1}"),
      ("0.NortonMultiply(^)", "0"),
      ("6.NortonMultiply(^)", "^6"),
      ("(-4).NortonMultiply(^)", "v4"),
      ("(1/2).NortonMultiply(^)", "{^^*|v*}"),
      ("^.NortonMultiply(^)", "{^^*||0|v4}"),
      ("Tiny(2).NortonMultiply(^)", "{^^*||0|v6}"),
      ("{3||2+*|1+*}.Overheat(*,1+*)", "{1||+-(1*)|-1,{-1|-3}}"),
      ("(7/16).Overheat(0,0)", "^[3]"),
      ("^.Pow({pass|1})", "!!Exponent must be nonnegative."),
      ("^.Pow({1|pass})", "{0||0,pass|0,*}"),
      ("^.Pow(7/4)", "{0||0,v*|0,{0,v*|0,*}}"),
      ("^.Pow(3)", "^<3>"),
      ("^.Pow({5|pass})", "{0||0,pass|0,v[4]*}"),
      ("^.Pow({pass|5})", "{0||0,v[4]*|0,pass}"),
      ("^.Pow(on)", "^<on>"),
      ("{0||0|-2}.Pow(3)", "{0||0|-2,{0|-2,{0|-2}}}"),
      ("{0||0|-2}.Pow(7/4)", "{0||0,{0|-2,{0|-2}}|-2,{0,{0|-2,{0|-2}}|-2,{0|-2}}}"),
      ("{0||0|-2}.Pow({5|pass})", "{0||0,pass|-2,{0|-2,{0|-2,{0|-2,{0|-2,{0|-2}}}}}}"),
      ("{0||0|-2}.Pow(on)", "{0||0|-2,pass}"),
      ("^.PowTo({pass|1})", "{pass|*,^}"),
      ("^.PowTo({1|pass})", "{^|*,pass}"),
      ("^.PowTo(7/4)", "{^|*,^[2]||*,^[2]}"),
      ("^.PowTo(3)", "^[3]"),
      ("^.PowTo({5|pass})", "{^[5]|*,pass}"),
      ("^.PowTo({pass|5})", "{pass|*,^[5]}"),
      ("upon", "^[on]"),
      ("{0||0|-2}.PowTo(3)", "{Tiny(2)||0|-2|||0|-2}"),
      ("{0||0|-2}.PowTo(7/4)", "{Tiny(2)|{0|-2},{Tiny(2)||0|-2}||{0|-2},{Tiny(2)||0|-2}}"),
      ("{0||0|-2}.PowTo({5|pass})", "{{Tiny(2)||0|-2|||0|-2||||0|-2}||0|-2|||{0|-2},pass}"),
      ("{0||0|-2}.PowTo(on)", "{pass||0|-2}"),
      ("(+-1).PowTo(3)", "{1,{1,+-1|-1}|-1}"),
      ("(+-1).Pow(3)", "!!Base must be of the form {0|H}.")
    )

    execute(Table(
      header,
      instances map { case (expr, result) => (expr, expr, result) } : _*
    ))

  }

  "game.CanonicalStopperGame" should "implement unary methods correctly" in {

    val tests = CanonicalStopperTestCase.instances flatMap { _.toTests }

    execute(Table(header, tests : _*))

  }

  it should "implement >=2-ary methods correctly" in {

    val instances = Seq(
      ("upon.Downsum(^.Pow(on))", "^[on]"),
      ("upon.DownsumVariety(^.Pow(on))", "v<on>"),
      ("upon.Upsum(^.Pow(on))", "{0|^<on>*}"),
      ("upon.UpsumVariety(^.Pow(on))", "v<on>")
    )

    execute(Table(
      header,
      instances map { case (expr, result) => (expr, expr, result) } : _*
    ))

  }

  "game.StopperSidedValue" should "implement methods correctly" in {

    val instances = Seq(
      ("g := ^.Pow(on) + {0|upon}", "{0||0|^<on>*} & {0|^[on]}"),
      ("g.Onside", "{0||0|^<on>*}"),
      ("g.Offside", "{0|^[on]}")
    )

    execute(Table(
      header,
      instances map { case (expr, result) => (expr, expr, result) } : _*
    ))

  }

  "game.Game" should "behave correctly" in {
    execute(Table(
      header,
      ("CanonicalForm on loopy game", "game.grid.FoxAndGeese({(3,1),(3,3),(3,5),(3,7)}, (1,1)).CanonicalForm",
        "!!That is not a short game. If that is intentional, try `GameValue` in place of `CanonicalForm`.")
    ))
  }

  "game.Player" should "behave correctly" in {
    execute(Table(
      header,
      ("Left", "Left", "Left"),
      ("Player.Left", "Player.Left", "Left"),
      ("Right", "Right", "Right"),
      ("Player.Right", "Player.Right", "Right"),
      ("Player properties", "for p in [Left,Right] yield [p, p.Opponent, p.Sign, p.Ordinal] end",
        "[Left,Right,1,1,Right,Left,-1,2]")
    ))
  }

  "game.grid" should "define Amazons properly" in {
    execute(Table(
      header,
      ("Amazons", """game.grid.Amazons("x...|o...").CanonicalForm""", "+-{3,{4|0,{1|0,*}},{4|{0,{1/2|0}|v},+-1,+-{1,{2|0}}}}")
    ))
  }

  it should "define Clobber properly" in {
    execute(Table(
      header,
      ("Clobber", """game.grid.Clobber("xox|ox.").CanonicalForm""", "{^^*|*,v}"),
      ("Clobber (Diagonal)", """game.grid.GenClobber(directions => Coordinates.Diagonal)("xoxo|oox.|xxx.").CanonicalForm""", "{0,v*|vv}"),
      ("Clobber (Anti)", """game.grid.AntiClobber("xoxo|oo..").CanonicalForm""", "{*|-1}")
    ))
  }

  it should "define Domineering properly" in {
    execute(Table(
      header,
      ("Domineering", """game.grid.Domineering("....|....|....|....").CanonicalForm""", "+-{0,{{2|0},2Tiny(2)|{2|0},Miny(2)}}")
    ))
  }

  it should "define FoxAndGeese properly" in {
    execute(Table(
      header,
      ("FoxAndGeese", "game.grid.FoxAndGeese({(3,1),(3,3),(3,5),(3,7),(3,9)}, (1,9), boardWidth => 10).GameValue", "{6over|5*}"),
      ("FoxAndGeese.Table", "game.grid.FoxAndGeese.Table({(3,1),(3,3),(3,5),(3,7)})",
        """|      "X" |       |       "X" |   |      "X" |    |        "X" |      @
           |----------+-------+-----------+---+----------+----+------------+------@
           |          | 2over |           | 2 |          | 3* |            | 4over@
           |----------+-------+-----------+---+----------+----+------------+------@
           |{6|2over} |       | {2over|2} |   | {4|3||2} |    | {4over|3*} |      @""".filterNot{ _ == '@' }.stripMargin)
    ))
  }

  "game.strip" should "define Toads and Frogs properly" in {
    execute(Table(
      header,
      ("ToadsAndFrogs", """game.strip.ToadsAndFrogs("ttttt..fffff").CanonicalForm""", "+-{{2|*},{5/2||2|{0||||{0||v<2>|-1},{0||||0||Miny(1/32)|-2|||-1/2*}|||v<2>|-1/2||-1*}|||0}}"),
      ("BackslidingToadsAndFrogs", """game.strip.BackslidingToadsAndFrogs("ttt..fff").GameValue""", "{on||0|-1/2} & {1/2|0||off}"),
      ("GenToadsAndFrogs", """game.strip.GenToadsAndFrogs(2)("tttt..fff").CanonicalForm""", "{1/2*|v}")
    ))
  }

  it should "avoid a weird class load order bug" in {
    execute(Table(
      header,
      ("GenToadsAndFrogs loaded first", """game.strip.GenToadsAndFrogs(2).Class""", "<<game.strip.GenToadsAndFrogs>>")
    ))
  }

  def execute(tests: TableFor3[String, String, String]): Unit = {

    CgscriptClass.clearAll()
    CgscriptClass.Object.ensureLoaded()

    val varMap = mutable.AnyRefMap[Symbol, Any]()

    forAll(tests) { (_, input: String, expectedOutput: String) =>
      if (expectedOutput startsWith "!!") {
        val thrown = the [CgsuiteException] thrownBy parseResult(input, varMap)
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
    val scope = ElaborationDomain(None, Seq.empty, None)
    node.elaborate(scope)
    val domain = new Domain(new Array[Any](scope.localVariableCount), dynamicVarMap = Some(varMap))
    node.evaluate(domain)
  }

}

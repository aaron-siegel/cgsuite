package org.cgsuite.lang

import org.cgsuite.exception.CgsuiteException
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output.Output
import org.scalatest.prop.{PropertyChecks, TableFor3}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

// CGScript Functional Tests.

class CgscriptTest extends FlatSpec with Matchers with PropertyChecks {

  val header = ("Test Name", "input", "expected output")

  val testPackage = CgscriptPackage.root declareSubpackage "test"

  def decl(name: String, explicitDefinition: String) = {
    CgscriptClass declareSystemClass (name, explicitDefinition = Some(explicitDefinition))
  }

  "CGScript" should "process basic expressions" in {

    executeTests(Table(
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

    executeTests(Table(
      header,
      ("Integer addition", "3+5", "8"),
      ("Integer multiplication", "3*5", "15"),
      ("Integer exponentiation", "3^5", "243"),
      ("Rational number", "4/6", "2/3"),
      ("Division by zero", "0/0", "!!/ by zero"),
      ("Rational exponentiation", "(1/2)^4", "1/16"),
      ("Negative power", "2^(-4)", "1/16"),
      ("Negative power of rational", "(2/3)^(-3)", "27/8"),
      ("Chained exponentiation", "3^2^2^2", "43046721"),
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
      ("Dyadic times non-dyadic", "(1/2) * (1/3)", "1/6"),
      ("Ordinal plus integer", "omega + 5", "\u03C9+5"),
      ("Integer minus ordinal", "5 - omega", "-\u03C9+5"),
      ("Multiple ordinal expression", "omega*3 + omega^(omega^2+omega) - omega^19*9*omega", "\u03C9^(\u03C9^2+\u03C9)-9\u03C9^20+3\u03C9"),
      ("Ordinal cancellation", "(omega + 5)-omega", "5"),
      ("Ordinal exponentiation", "(omega^omega^2+1)^6", "\u03C9^(6\u03C9^2)+6\u03C9^(5\u03C9^2)+15\u03C9^(4\u03C9^2)+20\u03C9^(3\u03C9^2)+15\u03C9^(2\u03C9^2)+6\u03C9^\u03C9^2+1"),
      ("Surreal number", "1/omega", "1/\u03C9"),
      ("Surreal number plus surreal number", "omega^omega + 1/(omega+1)", "(\u03C9^(\u03C9+1)+\u03C9^\u03C9+1)/(\u03C9+1)"),
      ("Surreal number simplification", "(omega^(omega+1)-omega^omega-omega+1)/(omega-1)", "\u03C9^\u03C9-1")
    ))

  }

  it should "give helpful error messages for certain arithmetic expressions" in {

    executeTests(Table(
      header,
      ("Nil plus number", "[] + 5", "!!No operation `+` for arguments of types `cgsuite.lang.Nil`, `game.Integer`")
    ))

  }

  it should "correctly interpret game specifiers" in {
    executeTests(Table(
      header,
      ("Composition", "{0|^*5}", "^^*4"),
      ("Slashes", "{3|||2||1|0,*||||-1/2}", "{3|||2||1|0,*||||-1/2}"),
      ("Ambiguous slashes", "{3|2|1}", "!!Syntax error."),
      ("Floating slash", "1|0", "!!Syntax error."),
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
      ("Explicit game", "'{*|*}'", "'{*|*}'"),
      ("Game specifier containing non-value game", "{game.grid.Domineering(\"..|..\")|-1}", "'{Domineering.Position(\"..|..\")|-1}'"),
      ("Illegal object in game specifier", "{0|false}", "!!Invalid game specifier: objects must be of type `Game` or `SidedValue`")
      //("Explicit game ordinal sum", "'{*|*}':1", "^")
    ))
  }

  it should "process infix ops" in {
    executeTests(Table(
      header,
      ("Simple infix", "3 Max 5", "5"),
      ("Star infix", "* Heat 2", "+-2"),
      ("Star2 infix", "*2 Heat 2", "+-{2,{4|0}}"),
      ("Up infix", "^ Heat 2", "{2||0|-4}"),
      ("UpStar infix", "^* Heat 2", "{2,{4|0}|-2}"),
      ("Double-up infix", "^^ Heat 2", "{2||0,+-2|-4}"),
      ("Up infix star", "^ Heat *", "!!That variable is not defined: `Heat`"),
      ("Double-up infix star", "^^ Heat *", "0")
    ))
  }

  it should "respect order of operations" in {
    executeTests(Table(
      header,
      ("Sums and differences", "5 - 3 + 4 - 8", "-2"),
      ("Products and divs", "5 - 3 / 4 * 6 - 2", "-3/2"),
      ("Exp", "5 * 3 ^ 2 / 4", "45/4"),
      ("PlusMinus", "3 - 2 +- 6 - 7 +- 8 + 4", "{12|0||-4|-16}"),
      ("Infix", "4 Max 3 + 5", "8")
    ))
  }

  it should "correctly interpret loopy game specifiers" in {
    executeTests(Table(
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
      ("not stopper-sided", "a{0,{1|1,{*,{1+*|1+*,a}|*}}|0}", "a{1|1,{1*|a||*}||0} & a{0,{1|||1*|a||*}|0}"),
      ("explicit specifier of sided values", "{2&-2|1&-2}", "{2|1} & -2*")
    ))
  }

  it should "process game operations" in {

    executeTests(Table(
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
    executeTests(Table(
      header,
      ("Multiplier simplification", "(*+*)*3", "0"),
      ("Integer exponent simplification", "2^(*+*)", "1"),
      ("+- loopy game simplification", "uponth := {0||0|0,pass}; +-(uponth+*)", "0")
    ))
  }

  it should "handle comparison operators" in {
    executeTests(Table(
      header,
      ("Ordinal comparison 1", "-omega < 0", "true"),
      ("Ordinal comparison 2", "-omega < 1", "true"),
      ("Confused", "3 <> 3+*", "true"),
      ("Heterogeneous compare", "3 == nil", "false")
    ))
  }

  it should "construct collections properly" in {
    executeTests(Table(
      header,
      ("Empty List", "[]", "nil"),
      ("List", "[3,5,3,1]", "[3,5,3,1]"),
      ("Empty Set", "{}", "{}"),
      ("Set", "{3,5,3,1}", "{1,3,5}"),
      ("Heterogeneous set", """{3,"foo",nil,true,[3,5,3,1],+-6,*2,"bar"}""", """{nil,3,*2,+-6,true,"bar","foo",[3,5,3,1]}"""),
      ("Empty Map", "{=>}", "{=>}"),
      ("Map", """{"foo" => 1, "bar" => *2, 16 => 22}""", """{16 => 22, "bar" => *2, "foo" => 1}"""),
      ("Range", "3..12", "3..12"),
      ("Range w/ Step", "3..12..3", "3..12..3"),
      ("Empty Range", "1..0", "1..0"),
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

    executeTests(Table(header, listofLoops : _*))
    executeTests(Table(header, setofLoops : _*))
    executeTests(Table(header, yieldLoops : _*))
    executeTests(Table(header, sumofLoops : _*))

  }

  it should "properly construct and evaluate procedures" in {

    executeTests(Table(
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
      ("Procedure eval - too few args", "f(3)", "!!Missing required parameter (in procedure call): `y`"),
      ("Procedure eval - too many args", "f(3,4,5)", "!!Too many arguments (in procedure call): 3 (expecting at most 2)"),
      ("Procedure eval - named args", "f(y => 3, x => 4)", "1/2"),
      ("Procedure eval - named before ordinary", "f(y => 4, 5)", "!!Named parameter `y` (in procedure call) appears in earlier position than an ordinary argument"),
      ("Procedure eval - duplicate parameter (ordinary + named)", "f(3, x => 4)", "!!Duplicate named parameter (in procedure call): `x`"),
      ("Procedure eval - duplicate parameter (named + named)", "f(y => 4, y => 5)", "!!Duplicate named parameter (in procedure call): `y`"),
      ("Procedure eval - invalid named arg", "f(3, foo => 4)", "!!Invalid parameter name (in procedure call): `foo`"),
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

  it should "validate function calls correctly" in {

    testPackage declareSubpackage "validation"

    decl("test.validation.Outer",
    """class Outer(a as Integer, b as Integer, c as String ? "bell")
      |end
      |""".stripMargin)
    decl("test.validation.Inner",
    """singleton class Inner
      |
      |  def Method3(a as Integer, b as Integer, c as String ? "bell") := a + b;
      |
      |  def Method5(a as Integer, b as Integer, c as Nimber ? *, d ? nil, e as Game ? ^*) := a + b;
      |
      |  class Nested(a as Integer, b as Integer, c as String ? "bell")
      |  end
      |
      |end
      |""".stripMargin)

    val instances = Seq(
      ("3-param method", "test.validation.Inner.Method3", "3", 3, "in call to `test.validation.Inner.Method3`"),
      ("5-param method", "test.validation.Inner.Method5", "3", 5, "in call to `test.validation.Inner.Method5`"),
      ("Outer constructor", "test.validation.Outer", "<Object of Class Outer>", 3, "in call to `test.validation.Outer` constructor"),
      ("Nested constructor", "test.validation.Inner.Nested", "<Object of Class Inner.Nested>", 3, "in call to `test.validation.Inner.Nested` constructor"),
      ("Procedure", "f", "3", 3, "in procedure call")
    )

    val tests = instances flatMap { case (name, fn, successOutput, paramCount, locationMessage) =>
      Seq(
        (s"Successful args ($name)", s"$fn(1,2)", successOutput),
        (s"Too many arguments ($name)", s"$fn(1,2,3,4,5,6,7)", s"!!Too many arguments ($locationMessage): 7 (expecting at most $paramCount)"),
        (s"Missing required parameter ($name)", s"$fn(1, c => true)", s"!!Missing required parameter ($locationMessage): `b`"),
        (s"Invalid parameter name ($name)", s"$fn(1,2,foo => true)", s"!!Invalid parameter name ($locationMessage): `foo`"),
        (s"Duplicate named parameter ($name)", s"$fn(1, b => 7, b => 6)", s"!!Duplicate named parameter ($locationMessage): `b`"),
        (s"Duplicate named parameter after ordinary ($name)", s"$fn(1, 2, a => 3)", s"!!Duplicate named parameter ($locationMessage): `a`"),
        (s"Named parameter in early position ($name)", s"$fn(a => 1, 2)", s"!!Named parameter `a` ($locationMessage) appears in earlier position than an ordinary argument"),
        (s"Invalid argument type ($name)", s"$fn(1, 1/2)", s"!!Argument `b` ($locationMessage) has type `game.DyadicRational`, which does not match expected type `game.Integer`")
      )
    }

    executeTests(Table(header, tests : _*), """f := (a as Integer, b as Integer, c as String ? "foo") -> a + b""")

    executeTests(Table(
      header,
      ("Invalid argument type (System method)", "3.NimSum(1/2)",
        "!!Argument `that` (in call to `game.Integer.NimSum`) has type `game.DyadicRational`, which does not match expected type `game.Integer`"),
      ("Invalid argument type (Special method)", "[1,2,3].Grouped(*)",
        "!!Argument `n` (in call to `cgsuite.lang.List.Grouped`) has type `game.Nimber`, which does not match expected type `game.Integer`")
    ))

  }

  it should "validate class definitions correctly" in {

    val classdefPackage = testPackage declareSubpackage "classdef"
    classdefPackage declareSubpackage "invalidconstants"
    decl("test.classdef.BaseClass", "class BaseClass def Method := 3; end")
    decl("test.classdef.SingletonClass", "singleton class SingletonClass end")
    decl("test.classdef.MutableClass", "mutable class MutableClass end")
    decl("test.classdef.MissingOverride", "class MissingOverride extends BaseClass def Method := 4; end")
    decl("test.classdef.ExtraneousOverride", "class ExtraneousOverride extends BaseClass override def NewMethod := 4; end")
    decl("test.classdef.ExternalMethodWithBody", "system class ExternalMethodWithBody external def ExternalMethod := 3; end")
    decl("test.classdef.NonsystemClassWithExternalMethod", "class NonsystemClassWithExternalMethod external def ExternalMethod := 3; end")
    decl("test.classdef.DuplicateMethodMethod", "class DuplicateMethodMethod def Method := 3; def Method := 4; end")
    decl("test.classdef.DuplicateMethodNested", "class DuplicateMethodNested def Method := 3; class Method() end end")
    decl("test.classdef.DuplicateMethodVar", "class DuplicateMethodVar def x := 3; var x := 4; end")
    decl("test.classdef.DuplicateNestedVar", "class DuplicateNestedVar class x end var x := 3; end")
    decl("test.classdef.DuplicateVarVar", "class DuplicateVarVar var x := 3; var x := 4; end")
    decl("test.classdef.SingletonWithConstructor", "singleton class SingletonWithConstructor(nope as Integer) end")
    decl("test.classdef.SubclassOfSingleton", "class SubclassOfSingleton extends SingletonClass end")
    decl("test.classdef.invalidconstants.constants", "class constants end")
    decl("test.classdef.ImmutableSubclassOfMutable", "class ImmutableSubclassOfMutable extends MutableClass end")
    decl("test.classdef.ImmutableNestedClassOfMutable", "mutable class ImmutableNestedClassOfMutable class Nested end end")
    decl("test.classdef.MutableVarOfImmutable", "class MutableVarOfImmutable mutable var x := 4; end")
    decl("test.classdef.ImmutableVarWithNoInitializer", "class ImmutableVarWithNoInitializer var x; end")

    executeTests(Table(
      header,
      ("Missing override", "test.classdef.MissingOverride.X",
        "!!Method `test.classdef.MissingOverride.Method` must be declared with `override`, since it overrides `test.classdef.BaseClass.Method`"),
      ("Extraneous override", "test.classdef.ExtraneousOverride.X",
        "!!Method `test.classdef.ExtraneousOverride.NewMethod` overrides nothing"),
      ("External method with body", "test.classdef.ExternalMethodWithBody.X",
        "!!Method is declared `external` but has a method body"),
      ("External method of nonsystem class", "test.classdef.NonsystemClassWithExternalMethod.X",
        "!!Method is declared `external`, but class `test.classdef.NonsystemClassWithExternalMethod` is not declared `system`"),
      ("Duplicate method + method", "test.classdef.DuplicateMethodMethod.X",
        "!!Member `Method` is declared twice in class `test.classdef.DuplicateMethodMethod`"),
      ("Duplicate method + nested", "test.classdef.DuplicateMethodNested.X",
        "!!Member `Method` is declared twice in class `test.classdef.DuplicateMethodNested`"),
      ("Duplicate method + var", "test.classdef.DuplicateMethodVar.X",
        "!!Member `x` conflicts with a var declaration in class `test.classdef.DuplicateMethodVar`"),
      ("Duplicate nested + var", "test.classdef.DuplicateNestedVar.X",
        "!!Member `x` conflicts with a var declaration in class `test.classdef.DuplicateNestedVar`"),
      ("Duplicate var + var", "test.classdef.DuplicateVarVar.X",
        "!!Variable `x` is declared twice in class `test.classdef.DuplicateVarVar`"),
      ("Singleton with constructor", "test.classdef.SingletonWithConstructor.X",
        "!!Class `test.classdef.SingletonWithConstructor` must not have a constructor if declared `singleton`"),
      ("Subclass of singleton", "test.classdef.SubclassOfSingleton.X",
        "!!Class `test.classdef.SubclassOfSingleton` may not extend singleton class `test.classdef.SingletonClass`"),
      ("constants is not singleton", "test.classdef.invalidconstants.constants.X",
        "!!Constants class `test.classdef.invalidconstants.constants` must be declared `singleton`"),
      ("Immutable subclass of mutable class", "test.classdef.ImmutableSubclassOfMutable.X",
        "!!Subclass `test.classdef.ImmutableSubclassOfMutable` of mutable class `test.classdef.MutableClass` is not declared `mutable`"),
      ("Immutable nested class of mutable class", "test.classdef.ImmutableNestedClassOfMutable.X",
        "!!Nested class `Nested` of mutable class `test.classdef.ImmutableNestedClassOfMutable` is not declared `mutable`"),
      ("Mutable var of immutable class", "test.classdef.MutableVarOfImmutable.X",
        "!!Class `test.classdef.MutableVarOfImmutable` is immutable, but variable `x` is declared `mutable`"),
      ("Immutable var with no initializer", "test.classdef.ImmutableVarWithNoInitializer.X",
        "!!Immutable variable `x` must be assigned a value (or else declared `mutable`)")
    ))

  }

  it should "handle mutables correctly" in {

    testPackage declareSubpackage "mutables"
    decl("test.mutables.MutableClass",
      """mutable class MutableClass()
        |  var immutableVar := 5;
        |  mutable var mutableVar;
        |  def SetMutable(newValue) begin mutableVar := newValue end
        |  def SetImmutable(newValue) begin immutableVar := newValue end
        |end""".stripMargin)
    decl("test.mutables.ImmutableClass1", "class ImmutableClass1() var immutableVar := MutableClass(); end")
    decl("test.mutables.SingletonImmutableClass", "singleton class SingletonImmutableClass var immutableVar := MutableClass(); end")
    decl("test.mutables.ImmutableClass2", "class ImmutableClass(cparam) end")

    executeTests(Table(
      header,
      ("Reassign to mutable", "x := test.mutables.MutableClass(); x.SetMutable(168); x.mutableVar", "168"),
      ("Cannot reassign to immutable", "y := test.mutables.MutableClass(); y.SetImmutable(168)",
        "!!Cannot reassign to immutable var: `immutableVar`"),
      ("Cannot assign mutable object to var of immutable class", "test.mutables.ImmutableClass1()",
        "!!Cannot assign mutable object to var `immutableVar` of immutable class `test.mutables.ImmutableClass1`"),
      ("Cannot assign mutable object to var of singleton immutable class", "test.mutables.SingletonImmutableClass.X",
        "!!Cannot assign mutable object to var `immutableVar` of immutable class `test.mutables.SingletonImmutableClass`"),
      ("Cannot pass mutable object to constructor of immutable class",
        "x := test.mutables.MutableClass(); test.mutables.ImmutableClass2(x)",
        "!!Cannot assign mutable object to var `cparam` of immutable class")
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

    executeTests(Table(header, lists : _*))
    executeTests(Table(header, sets : _*))

  }

  it should "implement methods correctly" in {

    val tests = CollectionTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests : _*))

  }

  "cgsuite.lang.Range" should "implement Collection faithfully" in {

    executeTests(Table(
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

    executeTests(Table(
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

  "game.Nimber" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("NimValue (0)", "0.NimValue", "0"),
      ("NimValue (non-0)", "*3.NimValue", "3")
    ))

  }

  "game.GeneralizedOrdinal" should "implement methods correctly" in {

    val unaryInstances = Seq(
      ("omega", "\u03C9", "GeneralizedOrdinal", "\u03C9", "\u03C9", "1", "true", "L"),
      ("-omega", "-\u03C9", "GeneralizedOrdinal", "\u03C9", "\u03C9", "-1", "false", "R"),
      ("omega+5", "\u03C9+5", "GeneralizedOrdinal", "\u03C9+5", "\u03C9+5", "1", "true", "L"),
      ("omega-5", "\u03C9-5", "GeneralizedOrdinal", "\u03C9-5", "\u03C9+5", "1", "false", "L"),
      ("-omega+5", "-\u03C9+5", "GeneralizedOrdinal", "\u03C9-5", "\u03C9+5", "-1", "false", "R"),
      ("omega*5", "5\u03C9", "GeneralizedOrdinal", "5\u03C9", "5\u03C9", "1", "true", "L"),
      ("omega^omega-5*omega^19+omega^2+1", "\u03C9^\u03C9-5\u03C9^19+\u03C9^2+1", "GeneralizedOrdinal", "\u03C9^\u03C9-5\u03C9^19+\u03C9^2+1", "\u03C9^\u03C9+5\u03C9^19+\u03C9^2+1", "1", "false", "L")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, abs, birthday, sign, isOrdinal, outcomeClass) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"<<game.$cls>>"),
        (s"($x).Abs", abs),
        (s"($x).Birthday", birthday),
        (s"($x).Sign", sign),
        (s"($x).IsOrdinal", isOrdinal),
        (s"($x).OutcomeClass", outcomeClass)
      )
    } map { case (expr, result) => (expr, expr, result) }

    executeTests(Table(header, unaryTests : _*))

  }

  "game.Rational" should "implement methods correctly" in {

    // x, class, numerator, denominator, isDyadic, abs, floor, ceiling, reciprocal
    val unaryInstances = Seq(
      ("0/7", "0", "Zero", "0", "1", "true", "0", "0", "0", "inf", "0", "P"),
      ("19/1", "19", "Integer", "19", "1", "true", "19", "19", "19", "1/19", "19", "L"),
      ("-19/1", "-19", "Integer", "-19", "1", "true", "19", "-19", "-19", "-1/19", "19", "R"),
      ("3/16", "3/16", "DyadicRational", "3", "16", "true", "3/16", "0", "1", "16/3", "5", "L"),
      ("-3/16", "-3/16", "DyadicRational", "-3", "16", "true", "3/16", "-1", "0", "-16/3", "5", "R"),
      ("3^22/2^72", "31381059609/4722366482869645213696", "DyadicRational", "31381059609", "4722366482869645213696", "true", "31381059609/4722366482869645213696", "0", "1", "4722366482869645213696/31381059609", "73", "L"),
      ("-3^22/2^72", "-31381059609/4722366482869645213696", "DyadicRational", "-31381059609", "4722366482869645213696", "true", "31381059609/4722366482869645213696", "-1", "0", "-4722366482869645213696/31381059609", "73", "R"),
      ("6/23", "6/23", "Rational", "6", "23", "false", "6/23", "0", "1", "23/6", "\u03C9", "L"),
      ("-6/23", "-6/23", "Rational", "-6", "23", "false", "6/23", "-1", "0", "-23/6", "\u03C9", "R"),
      ("17/0", "inf", "Rational", "1", "0", "false", "inf", "!!Error in call to `game.Rational.Floor`: / by zero", "!!Error in call to `game.Rational.Ceiling`: / by zero", "0", "!!Error in call to `game.Rational.Birthday`: inf has no birthday", "L"),
      ("-17/0", "-inf", "Rational", "-1", "0", "false", "inf", "!!Error in call to `game.Rational.Floor`: / by zero", "!!Error in call to `game.Rational.Ceiling`: / by zero", "0", "!!Error in call to `game.Rational.Birthday`: inf has no birthday", "R")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, num, den, isDyadic, abs, floor, ceiling, reciprocal, birthday, outcomeClass) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"<<game.$cls>>"),
        (s"($x).Numerator", num),
        (s"($x).Denominator", den),
        (s"($x).IsDyadic", isDyadic),
        (s"($x).Abs", abs),
        (s"($x).Floor", floor),
        (s"($x).Ceiling", ceiling),
        (s"($x).Reciprocal", reciprocal),
        (s"($x).Birthday", birthday),
        (s"($x).OutcomeClass", outcomeClass)
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

    executeTests(Table(header, unaryTests ++ binaryTests : _*))

  }

  "game.CanonicalShortGame" should "implement unary methods correctly" in {

    val tests = CanonicalShortGameTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests : _*))

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

    executeTests(Table(
      header,
      instances map { case (expr, result) => (expr, expr, result) } : _*
    ))

  }

  "game.CanonicalStopperGame" should "implement unary methods correctly" in {

    val tests = CanonicalStopperTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests : _*))

  }

  it should "implement >=2-ary methods correctly" in {

    val instances = Seq(
      ("upon.Downsum(^.Pow(on))", "^[on]"),
      ("upon.DownsumVariety(^.Pow(on))", "v<on>"),
      ("upon.Upsum(^.Pow(on))", "{0|^<on>*}"),
      ("upon.UpsumVariety(^.Pow(on))", "v<on>")
    )

    executeTests(Table(
      header,
      instances map { case (expr, result) => (expr, expr, result) } : _*
    ))

  }

  "game.StopperSidedValue" should "implement methods correctly" in {

    val tests = SidedValueTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests : _*))

    /*
    val unaryInstances = Seq(
      ("^.Pow(on) + {0|upon}", "{0||0|^<on>*} & {0|^[on]}", "StopperSidedValue", "{0||0|^<on>*}", "{0|^[on]}", "L")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, onside, offside, outcomeClass) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"<<game.$cls>>"),
        (s"($x).Onside", onside),
        (s"($x).Offside", offside),
        (s"($x).OutcomeClass", outcomeClass)
      )
    } map { case (expr, result) => (expr, expr, result) }

    executeTests(Table(header, unaryTests : _*))
    */

  }

  "game.Game" should "behave correctly" in {
    executeTests(Table(
      header,
      ("CanonicalForm on loopy game", "game.grid.FoxAndGeese({(3,1),(3,3),(3,5),(3,7)}, (1,1)).CanonicalForm",
        "!!That is not a short game. If that is intentional, try `GameValue` in place of `CanonicalForm`.")
    ))
  }

  "game.Player" should "behave correctly" in {
    executeTests(Table(
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
    executeTests(Table(
      header,
      ("Amazons", """game.grid.Amazons("x...|o...").CanonicalForm""", "+-{3,{4|0,{1|0,*}},{4|{0,{1/2|0}|v},+-1,+-{1,{2|0}}}}")
    ))
  }

  it should "define Clobber properly" in {
    executeTests(Table(
      header,
      ("Clobber", """game.grid.Clobber("xox|ox.").CanonicalForm""", "{^^*|*,v}"),
      ("Clobber (Diagonal)", """game.grid.GenClobber(directions => Coordinates.Diagonal)("xoxo|oox.|xxx.").CanonicalForm""", "{0,v*|vv}"),
      ("Clobber (Anti)", """game.grid.AntiClobber("xoxo|oo..").CanonicalForm""", "{*|-1}")
    ))
  }

  it should "define Domineering properly" in {
    executeTests(Table(
      header,
      ("Domineering", """game.grid.Domineering("....|....|....|....").CanonicalForm""", "+-{0,{{2|0},2Tiny(2)|{2|0},Miny(2)}}")
    ))
  }

  it should "define FoxAndGeese properly" in {
    executeTests(Table(
      header,
      ("FoxAndGeese", "game.grid.FoxAndGeese({(4,2),(4,4),(4,6),(4,8)}, (1,1)).GameValue", "{4*|7/2}"),
      ("CeyloneseFoxAndGeese", "game.grid.CeyloneseFoxAndGeese({(4,2),(4,4),(4,6),(4,8)}, (1,1)).GameValue", "{9||4v[on]*|5/2*|||3|5/2*,5/2v||5/2}"),
      ("GenFoxAndGeese", "game.grid.GenFoxAndGeese(boardWidth => 10)({(3,1),(3,3),(3,5),(3,7),(3,9)}, (1,9)).GameValue", "{6over|5*}"),
      ("FoxAndGeese.Table", "game.grid.FoxAndGeese.Table({(3,1),(3,3),(3,5),(3,7)})",
        """|      "X" |       |       "X" |   |      "X" |    |        "X" |      @
           |----------+-------+-----------+---+----------+----+------------+------@
           |          | 2over |           | 2 |          | 3* |            | 4over@
           |----------+-------+-----------+---+----------+----+------------+------@
           |{6|2over} |       | {2over|2} |   | {4|3||2} |    | {4over|3*} |      @""".filterNot{ _ == '@' }.stripMargin),
      ("FoxAndGeese Validation", "game.grid.FoxAndGeese({(3,1),(3,3),(3,5),(3,7)}, (1,6))", "!!`fox` must be a valid `Coordinates` (1 <= col <= boardWidth; row >= 1; row+col even).")
    ))
  }

  "game.strip" should "define Toads and Frogs properly" in {
    executeTests(Table(
      header,
      ("ToadsAndFrogs", """game.strip.ToadsAndFrogs("ttttt..fffff").CanonicalForm""", "+-{{2|*},{5/2||2|{0||||{0||v<2>|-1},{0||||0||Miny(1/32)|-2|||-1/2*}|||v<2>|-1/2||-1*}|||0}}"),
      ("BackslidingToadsAndFrogs", """game.strip.BackslidingToadsAndFrogs("ttt..fff").GameValue""", "{on||0|-1/2} & {1/2|0||off}"),
      ("GenToadsAndFrogs", """game.strip.GenToadsAndFrogs(2)("tttt..fff").CanonicalForm""", "{1/2*|v}")
    ))
  }

  it should "avoid a weird class load order bug" in {
    executeTests(Table(
      header,
      ("GenToadsAndFrogs loaded first", """game.strip.GenToadsAndFrogs(2).Class""", "<<game.strip.GenToadsAndFrogs>>")
    ))
  }

  "game.heap" should "define TakeAndBreak properly" in {

    val instances = Seq(
      ("game.heap.Nim", "20", "[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]", "nil"),
      ("game.heap.GrundysGame", "9", "[0,0,0,1,0,2,1,0,2,1,0,2,1,3,2,1,3,2,4,3,0]", "nil"),
      ("game.heap.Kayles", "20", "[0,1,2,3,1,4,3,2,1,4,2,6,4,1,2,7,1,4,3,2,1]", "Periodicity(Period => 12, Preperiod => 71, Saltus => 0)"),
      ("game.heap.DawsonsKayles", "10", "[0,0,1,1,2,0,3,1,1,0,3,3,2,2,4,0,5,2,2,3,3]", "Periodicity(Period => 34, Preperiod => 53, Saltus => 0)"),
      ("game.heap.TakeAndBreak(\"0.3f\")", "38", "[0,1,2,0,1,2,3,4,5,3,4,5,6,7,8,6,7,8,9,10,11]", "nil")
    )

    val tests = instances flatMap { case (rs, optionCount, nimSequence, periodicity) =>
      Seq(
        (s"$rs.NimValue", s"listof($rs(n).NimValue for n from 0 to 20)", nimSequence),
        (s"$rs(20).Options.Size", s"$rs(20).Options.Size", optionCount),
        (s"$rs.NimValueSequence", s"$rs.NimValueSequence(20)", nimSequence),
        (s"$rs.CheckPeriodicity(2000)", s"$rs.CheckPeriodicity(2000)", periodicity)
      )
    }

    executeTests(Table(header, tests : _*))
  }

  def executeTests(tests: TableFor3[String, String, String], preamble: String = ""): Unit = {

    CgscriptClass.clearAll()
    CgscriptClass.Object.ensureLoaded()

    val varMap = mutable.AnyRefMap[Symbol, Any]()

    if (preamble != "") parseResult(preamble, varMap)

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

package org.cgsuite.lang

class EvalTest extends CgscriptSpec{

  "CGScript" should "process basic expressions" in {

    executeTests(Table(
      header,
      ("Simple echo", "0", "0"),
      ("Semicolon suppress output", "0;", null),
      ("Semicolon doesn't suppress inside complex block", "begin 0; end", "0"),
      ("More complex semicolon suppression", "begin 0; end;", null),
      ("Variable assignment", "g := 7", "7"),
      ("Variable retrieval", "g", "7"),
      ("Variable assignment with var", "var h := 5", "5"),
      ("Variable retrieval with var", "h", "5"),
      ("Variable assignment with scoped var", "begin var k := 6 end", "6"),
      ("Variable retrieval from scoped var", "k", "!!That variable is not defined: `k`"),
      ("Variable retrieval within scope", "begin var l := 8; l end", "8"),
      ("Multivee/identifier parse check", "vvvvx := vvvvv", "v5"),
      ("Assign to name of class", "Integer := 5", "!!Cannot assign to class name as variable: `Integer`"),
      ("Empty script", "// This is an empty script.", null),
      ("Blank (but nonempty) expression", "begin end", "Nothing")
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
      ("Nim operator", "*(3+5)", "*8"),
      ("Nim operator (misere spec)", "*[3]", "*[3]"),
      ("Nim operator (misere spec - recursive", "*[[2],0]", "*[2#0]"),
      ("Nim operator (misere spec - invalid", "*[\"foo\"]", "!!Invalid misere game specifier: must be a `List` of `Integer`s or `MisereCanonicalGame`s"),
      ("Nim operator (negative value)", "*(-8)", "!!Nim value is negative: -8"),
      ("Nim operator (invalid type)", "*\"foo\"", "!!No operation `nim` for argument of type `cgsuite.lang.String`"),
      ("Ups", "^^^^^^+vvv*+^19*3+v14", "^8*2"),
      ("Up operator (invalid type)", "^\"foo\"", "!!No operation `up` for arguments of types `cgsuite.lang.String`, `game.Zero`"),
      ("Down operator (invalid type)", "v\"foo\"*9", "!!No operation `down` for arguments of types `cgsuite.lang.String`, `game.Integer`"),
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
      ("Nil plus number", "[] + 5", "!!No operation `+` for arguments of types `cgsuite.lang.List`, `game.Integer`"),
      ("Number plus Nothing", "5 + Nothing", "!!No operation `+` for arguments of types `game.Integer`, `cgsuite.lang.Nothing`")
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
      ("Game specifier containing non-value game", "{game.grid.Domineering(\"..|..\")|-1}", "'{Domineering.Position(\"..|..\")|-1}'"),
      ("Illegal object in game specifier", "{0|false}", "!!Invalid game specifier: objects must be of type `Game` or `SidedValue`"),
      ("Integer out of bounds", "{2^100|0}", "!!Integer out of bounds in game specifier (must satisfy -2147483648 <= n <= 2147483647)"),
      ("Max options exceeded", "{1|*16384}", "!!Too many options for `CanonicalShortGame` (must have at most 16383 Left options and 16383 Right options)")
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
      ("Heterogeneous compare", "3 == []", "false")
    ))
  }

  it should "handle explicit and compound games" in {
    executeTests(Table(
      header,
      ("Explicit game", "'{*|*}'", "'{*|*}'"),
      ("Explicit sum", "'{0|}' + 1", "'{0|}' + 1"),
      ("Explicit ordinal sum", "'{-1|1}':1", "'{-1|1}' : 1"),
      ("Explicit ordinal sum - eval", "('{-1|1}':1).CanonicalForm", "1/2"),
      ("Explicit ordinal sum - eval with loopy", "('{-1|1}':on).GameValue", "1under"),
      ("Explicit multiple", "-2 * '{|-1}'", "-2 * '{|-1}'"),
      ("Explicit multiple - eval", "(-2 * '{|-1}').CanonicalForm", "4"),
      ("Explicit Conway product", "*6 ConwayProduct '{0,*|0,*}'", "*6 ConwayProduct '{0,*|0,*}'"),
      ("Explicit Conway product - eval", "(*6 ConwayProduct '{0,*|0,*}').CanonicalForm", "*11"),
      ("Explicit game eval - 1", "g := '{*|*}':1", "'{*|*}' : 1"),
      ("Explicit game eval - 2", "-g", "'{*|*}' : -1"),
      ("Explicit game eval - 3", "g-g", "'{*|*}' : 1 + '{*|*}' : -1"),
      ("Explicit game eval - 4", "{g|-1}", "'{'{*|*}' : 1|-1}'"),
      ("Explicit game eval - 5", "2*g", "2 * ('{*|*}' : 1)"),
      ("Explicit game eval - 6", "(2*g).CanonicalForm", "^^"),
      ("Explicit game eval - 7", "(g*2).CanonicalForm", "^^")
    ))
  }

  it should "construct collections properly" in {
    executeTests(Table(
      header,
      ("Coordinates", "(1,3)", "(1,3)"),
      ("Invalid coordinates", """("foo","bar")""", "!!No operation `(,)` for arguments of types `cgsuite.lang.String`, `cgsuite.lang.String`"),
      ("Empty List", "[]", "[]"),
      ("List", "[3,5,3,1]", "[3,5,3,1]"),
      ("Empty Set", "{}", "{}"),
      ("Set", "{3,5,3,1}", "{1,3,5}"),
      ("Heterogeneous set", """{3,"foo",[],true,Nothing,[3,5,3,1],+-6,*2,"bar"}""", """{Nothing,3,*2,+-6,true,"bar","foo",[],[3,5,3,1]}"""),
      ("Empty Map", "{=>}", "{=>}"),
      ("Map", """{"foo" => 1, "bar" => *2, 16 => 22}""", """{16 => 22, "bar" => *2, "foo" => 1}"""),
      ("Range", "3..12", "3..12"),
      ("Range w/ Step", "3..12..3", "3..12..3"),
      ("Empty Range", "1..0", "1..0"),
      ("Range -> Explicit", "(3..12).ToSet", "{3,4,5,6,7,8,9,10,11,12}"),
      ("Range -> Explicit 2", "(3..12..3).ToSet", "{3,6,9,12}"),
      ("Range Equals List", "1..4 === [1,2,3,4]", "true"),
      ("Empty Range Equals List", "1..0 === []", "true"),
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
      ("for-in-while-where", "", "x", "for x in [1,2,3,2] while x % 2 == 1 where x != 1", "", Some(""), "0")
    )

    val listofLoops = loopScenarios map { case (name, init, fn, snippet, result, _, _) =>
      (s"listof: $name", s"${init}listof($fn $snippet)", s"[$result]")
    }

    val setofLoops = loopScenarios map { case (name, init, fn, snippet, result, sortedResult, _) =>
      val setResult = sortedResult getOrElse result
      (s"setof: $name", s"${init}setof($fn $snippet)", s"{$setResult}")
    }

    val yieldLoops = loopScenarios map { case (name, init, fn, snippet, result, _, _) =>
      (s"yield: $name", s"$init$snippet yield $fn end", s"[$result]")
    }

    val sumofLoops = loopScenarios map { case (name, init, fn, snippet, _, _, sum) =>
      (s"sumof: $name", s"${init}sumof($fn $snippet)", sum)
    }

    executeTests(Table(header, listofLoops : _*))
    executeTests(Table(header, setofLoops : _*))
    executeTests(Table(header, yieldLoops : _*))
    executeTests(Table(header, sumofLoops : _*))

  }

  it should "properly construct and evaluate procedures" in {

    executeTests(Table(
      header,
      ("Procedure definition", "f := x -> x+1", "x -> x + 1"),
      ("Procedure definition - duplicate var", "(x, x) -> x", "!!Duplicate var: `x`"),
      ("Procedure evaluation", "f(8)", "9"),
      ("Procedure scope 1", "y := 3; f := x -> x+y; f(5)", "8"),
      ("Procedure scope 2", "y := 6; f(5)", "11"),
      ("Procedure scope 3", "x := 9; f(5); x", "9"),
      ("Procedure scope 4", "f := temp -> temp+1; f(5); temp", "!!That variable is not defined: `temp`"),
      ("No-parameter procedure", "f := () -> 3", "() -> 3"),
      ("No-parameter procedure evaluation", "f()", "3"),
      ("Multiparameter procedure", "f := (x,y) -> (x-y)/2", "(x, y) -> (x - y) / 2"),
      ("Multiparameter procedure evaluation", "f(3,4)", "-1/2"),
      ("Procedure eval - too few args", "f(3)", "!!Missing required parameter (in procedure call): `y`"),
      ("Procedure eval - too many args", "f(3,4,5)", "!!Too many arguments (in procedure call): 3 (expecting at most 2)"),
      ("Procedure eval - named args", "f(y => 3, x => 4)", "1/2"),
      ("Procedure eval - named before ordinary", "f(y => 4, 5)", "!!Named parameter `y` appears in earlier position than an ordinary argument"),
      ("Procedure eval - duplicate parameter (ordinary + named)", "f(3, x => 4)", "!!Named parameter shadows an earlier ordinary argument (in procedure call): `x`"),
      ("Procedure eval - duplicate parameter (named + named)", "f(y => 4, y => 5)", "!!Duplicate named parameter: `y`"),
      ("Procedure eval - invalid named arg", "f(3, foo => 4)", "!!Invalid parameter name (in procedure call): `foo`"),
      ("Curried procedure definition", "f := x -> y -> x + y", "x -> y -> x + y"),
      ("Curried procedure evaluation - 1", "g := f(3)", "y -> x + y"),
      ("Curried procedure evaluation - 2", "h := f(5)", "y -> x + y"),
      ("Curried procedure evaluation - 3", "[g(7),h(7)]", "[10,12]"),
      ("Curried procedure definition - duplicate var", "x -> x -> (x + 3)", "!!Duplicate var: `x`"),
      ("Recursive procedure", "fact := n -> if n == 0 then 1 else n * fact(n-1) end; fact(6)", "720"),
      ("Closure",
        """f := () -> begin var x := []; [y -> (x := y), () -> x] end;
          |pair1 := f(); set1 := pair1[1]; get1 := pair1[2]; pair2 := f(); set2 := pair2[1]; get2 := pair2[2];
          |set1("foo"); set2("bar"); [get1(), get2()]
        """.stripMargin, """["foo","bar"]"""),
      ("Procedure involving assignment - syntax error", "x -> y := x", "!!Syntax error."),
      ("False eval", "5(3)", "!!No method `Eval` for class: `game.Integer`"),
      ("Procedure eval - infinite recursion", "j := n -> j(n); j(5)", "!!Possible infinite recursion.")
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
        |  def Method5(a as Integer, b as Integer, c as Nimber ? *, d ? [], e as Game ? ^*) := a + b;
        |
        |  class Nested(a as Integer, b as Integer, c as String ? "bell")
        |  end
        |
        |end
        |""".stripMargin)

    val instances = Seq(
      ("3-param method", "test.validation.Inner.Method3", "3", 3, "in call to `test.validation.Inner.Method3`"),
      ("5-param method", "test.validation.Inner.Method5", "3", 5, "in call to `test.validation.Inner.Method5`"),
      ("Outer constructor", "test.validation.Outer", "Outer(1, 2, c => \"bell\")", 3, "in call to `test.validation.Outer` constructor"),
      ("Nested constructor", "test.validation.Inner.Nested", "Inner.Nested(1, 2, c => \"bell\")", 3, "in call to `test.validation.Inner.Nested` constructor"),
      ("Procedure", "f", "3", 3, "in procedure call")
    )

    val tests = instances flatMap { case (name, fn, successOutput, paramCount, locationMessage) =>
      Seq(
        (s"Successful args ($name)", s"$fn(1,2)", successOutput),
        (s"Too many arguments ($name)", s"$fn(1,2,3,4,5,6,7)", s"!!Too many arguments ($locationMessage): 7 (expecting at most $paramCount)"),
        (s"Missing required parameter ($name)", s"$fn(1, c => true)", s"!!Missing required parameter ($locationMessage): `b`"),
        (s"Invalid parameter name ($name)", s"$fn(1,2,foo => true)", s"!!Invalid parameter name ($locationMessage): `foo`"),
        (s"Duplicate named parameter ($name)", s"$fn(1, b => 7, b => 6)", s"!!Duplicate named parameter: `b`"),
        (s"Duplicate named parameter after ordinary ($name)", s"$fn(1, 2, a => 3)", s"!!Named parameter shadows an earlier ordinary argument ($locationMessage): `a`"),
        (s"Named parameter in early position ($name)", s"$fn(a => 1, 2)", s"!!Named parameter `a` appears in earlier position than an ordinary argument"),
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

}

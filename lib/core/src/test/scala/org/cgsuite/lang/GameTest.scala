package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class GameTest extends CgscriptSpec {

  "game.Integer" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("MinValue", "-2147483648", "-2147483648"),
      ("MinValue-1", "-2147483649", "-2147483649"),
      ("Max", "3.Max(-2147483648)", "3"),
      ("NimSum", "[11 NimSum n for n from 1 to 16]", "[10,9,8,15,14,13,12,3,2,1,0,7,6,5,4,27]"),
      ("NimSum (invalid)", "11 NimSum -1", "!!NimSum applies only to nonnegative integers."),
      ("NimProduct", "[8 NimProduct n for n from 1 to 16]", "[8,12,4,11,3,7,15,13,5,1,9,6,14,10,2,128]"),
      ("NimProduct (invalid)", "11 NimProduct -1", "!!NimProduct applies only to nonnegative integers."),
      ("NimInverse", "[n.NimInverse for n from 1 to 16]", "[1,3,2,15,12,9,11,10,6,8,7,5,14,13,4,170]"),
      ("NimInverse (invalid)", "0.NimInverse", "!!NimInverse applies only to positive integers."),
      ("NimDiv", "[11 NimDiv n for n from 1 to 16]", "[11,6,13,8,14,2,15,4,10,9,1,12,3,5,7,68]"),
      ("UglyProduct", "[14 UglyProduct n for n from 1 to 16]", "[14,13,3,13,3,0,14,7,9,10,4,10,4,7,9,23]"),
      ("Div", "17.Div(4)", "4"),
      ("Div by 0", "17.Div(0)", "!!/ by zero"),
      ("IsEvil", "[11.IsEvil, 23.IsEvil]", "[false,true]"),
      ("IsOdious","[11.IsOdious, 23.IsOdious]", "[true,false]"),
      ("IsTwoPower", "[32.IsTwoPower,48.IsTwoPower]", "[true,false]"),
      ("IsSmallInteger", "[2^31,-2^31,2^31-1,-2^31-1].Apply(x -> x.IsSmallInteger)", "[false,true,true,false]"),
      ("Lb", "[15,16,17,31,2^30-1,2^30,2^30+1,2^31-1,2^31,2^31+1,2^32-1,2^32,2^32+1].Apply(x -> x.Lb)", "[3,4,4,4,29,30,30,30,31,31,31,32,32]"),
      ("Lb(0)", "0.Lb", "!!Argument to Lb is not strictly positive: 0"),
      ("Lb(-32)", "(-32).Lb", "!!Argument to Lb is not strictly positive: -32"),
      ("Lb(-2^40)", "(-2^40).Lb", "!!Argument to Lb is not strictly positive: -1099511627776"),
      //("Random", "Integer.SetSeed(0); [Integer.Random(100) from 1 to 5]", "[61,49,30,48,16]"),
      ("NimInverse check",
        """for n from 1 to 3000 do
          |  if n NimProduct n.NimInverse != 1 then
          |    error("NimInverse failed at " + n.ToString);
          |  end
          |end""".stripMargin, "Nothing"),
      ("Isqrt",
        """
          |for n from 0 to 3000 do
          |  var isqrt := n.Isqrt;
          |  if n < isqrt^2 or n >= (isqrt+1)^2 then
          |    error("Isqrt failed at " + n.ToString);
          |  end
          |end""".stripMargin, "Nothing"),
      ("Isqrt(-1)", "(-1).Isqrt", "!!Argument to Isqrt is negative: -1")
    ))

  }

  "game.Nimber" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("NimValue (0)", "0.NimValue", "0"),
      ("NimValue (non-0)", "*3.NimValue", "3")
    ))

  }

  "game.GeneralizedOrdinal" should "implement unary methods correctly" in {

    val unaryInstances = Seq(
      ("omega", "\u03C9", "GeneralizedOrdinal", "\u03C9", "\u03C9", "1", "true", "L"),
      ("-omega", "-\u03C9", "GeneralizedOrdinal", "\u03C9", "\u03C9", "-1", "false", "R"),
      ("omega+5", "\u03C9+5", "GeneralizedOrdinal", "\u03C9+5", "\u03C9+5", "1", "true", "L"),
      ("omega-5", "\u03C9-5", "GeneralizedOrdinal", "\u03C9-5", "\u03C9+5", "1", "false", "L"),
      ("-omega+5", "-\u03C9+5", "GeneralizedOrdinal", "\u03C9-5", "\u03C9+5", "-1", "false", "R"),
      ("omega*5", "\u03C9\u00d75", "GeneralizedOrdinal", "\u03C9\u00d75", "\u03C9\u00d75", "1", "true", "L"),
      ("omega^omega-5*omega^19+omega^2+1", "\u03C9^\u03C9-\u03C9^19\u00d75+\u03C9^2+1", "GeneralizedOrdinal", "\u03C9^\u03C9-\u03C9^19\u00d75+\u03C9^2+1", "\u03C9^\u03C9+\u03C9^19\u00d75+\u03C9^2+1", "1", "false", "L")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, abs, birthday, sign, isOrdinal, outcomeClass) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"\u27eagame.$cls\u27eb"),
        (s"($x).Abs", abs),
        (s"($x).Birthday", birthday),
        (s"($x).Sign", sign),
        (s"($x).IsOrdinal", isOrdinal),
        (s"($x).OutcomeClass", outcomeClass)
      )
    } map { case (expr, result) => (expr, expr, result) }

    executeTests(Table(header, unaryTests : _*))

  }

  it should "implement >=2-ary methods correctly" in {

    val instances = Seq(
      ("(omega^omega*4 + omega^3*9 + 11) NimSum (omega^omega*6 + omega^3*5 + omega)", "ω^ω×2+ω^3×12+ω+11"),
      ("omega NimProduct 7", "ω×7"),
      ("omega NimProduct omega", "ω^2"),
      ("omega NimProduct omega^2", "2"),
      ("omega^3 NimProduct omega^3", "ω^6"),
      ("omega^3 NimProduct omega^6", "ω"),
      ("omega^3 NimProduct omega^8", "2"),
      ("omega^8 NimProduct omega^8", "ω^5×2"),
      ("omega^omega NimProduct omega^(omega*4)", "4"),
      ("omega^(omega*49) NimProduct omega^(omega*100)", "4"),
      ("omega^(omega^2*4) NimProduct omega^(omega^2*3)", "ω+1"),
      ("omega^(omega^2*31) NimProduct omega^(omega^2*24)", "ω+1"),
      ("omega^(omega^2*32) NimProduct omega^(omega^2*24)", "ω^(ω^2+1)+ω^ω^2"),
      ("omega NimExp 3", "2"),
      ("omega^3 NimExp 9", "2"),
      ("omega^27 NimExp 81", "2"),
      ("omega^(omega^41) NimExp 191", "ω^ω^6+ω^ω"),
      ("omega^(omega^42) NimExp 193", "ω+65536"),
      ("omega^omega^3 NimExp 66", "ω^(ω×4)+ω^(ω×2)+ω^ω×4+1"),   // The example on p. 449-450 of CGT
      ("omega^omega^13 NimExp 47", "ω^ω^7+1"),                  // Lenstra's example
      ("omega^omega^omega NimProduct 2", "!!NimProduct out of range."),
      ("omega^omega^1000 NimProduct 2", "!!NimProduct out of range."),
      ("(omega-1) NimSum 2", "!!NimSum applies only to ordinals."),
      ("(omega-1) NimProduct 2", "!!NimProduct applies only to ordinals.")
    )

    executeTests(Table(
      header,
      instances map { case (expr, result) => (expr, expr, result) }: _*
    ))

  }

  "game.TransfiniteNimber" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("ConwayProduct", "(*omega) ConwayProduct *(omega^2)", "*2")
    ))

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
      ("17/0", "inf", "Rational", "1", "0", "false", "inf", "!!/ by zero", "!!/ by zero", "0", "!!inf has no birthday", "L"),
      ("-17/0", "-inf", "Rational", "-1", "0", "false", "inf", "!!/ by zero", "!!/ by zero", "0", "!!inf has no birthday", "R")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, num, den, isDyadic, abs, floor, ceiling, reciprocal, birthday, outcomeClass) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"\u27eagame.$cls\u27eb"),
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
      ("*4.ConwayProduct(*4)", "*6"),
      ("*19.ConwayProduct(*23)", "*86"),
      ("15.ConwayProduct(^^*2)", "^30*2"),
      ("{3|1}.ConwayProduct(^^)", "{^6|^^}"),
      ("^^.ConwayProduct(^^)", "{0||0,*|0,{0,*|0,*2}}"),
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
      ("0.NortonProduct(^)", "0"),
      ("6.NortonProduct(^)", "^6"),
      ("(-4).NortonProduct(^)", "v4"),
      ("(1/2).NortonProduct(^)", "{^^*|v*}"),
      ("^.NortonProduct(^)", "{^^*||0|v4}"),
      ("Tiny(2).NortonProduct(^)", "{^^*||0|v6}"),
      ("{3||2+*|1+*}.Overheat(*,1+*)", "{1||+-(1*)|-1,{-1|-3}}"),
      ("(7/16).Overheat(0,0)", "^[3]"),
      ("^.Pow({pass|1})", "!!Invalid exponent."),
      ("^.Pow(off)", "!!Invalid exponent."),
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
      ("(+-1).Pow(3)", "!!Invalid base for `Pow` operation (base must be of the form {0|H})."),
      ("(1/8).Subordinate(1)", "-3"),
      ("(^.PowTo(5)+*).Subordinate(*)", "5"),
      ("*.Subordinate(1)", "!!That game cannot be subordinated to the specified base."),
      ("(+-1).Subordinate(0)", "+-1"),
      ("(+-1).Subordinate(+-1)", "0"),
      ("(-13/16).Subordinate(-1)", "3/8"),
      ("^.PowTo(5).Subordinate('{*|*}')", "5")    // Subordinating to a non-canonical base
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
      ("over.Subordinate(1)", "off"),
      ("(upon+*).Subordinate(*)", "on"),
      ("upon.Subordinate('{*|*}')", "on"),
      ("{0,*|0,pass}.Subordinate(*)", "over"),
      ("on.Subordinate(1)", "on"),
      ("over.Subordinate(1)", "off"),
      ("on.Subordinate(*)", "!!That game cannot be subordinated to the specified base."),
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

    val unaryInstances = Seq(
      ("^.Pow(on) + {0|upon}", "{0||0|^<on>*} & {0|^[on]}", "StopperSidedValue", "{0||0|^<on>*}", "{0|^[on]}", "L")
    )

    val unaryTests = unaryInstances flatMap { case (x, xOut, cls, onside, offside, outcomeClass) =>
      Seq(
        (x, xOut),
        (s"($x).Class", s"\u27eagame.$cls\u27eb"),
        (s"($x).Onside", onside),
        (s"($x).Offside", offside),
        (s"($x).OutcomeClass", outcomeClass)
      )
    } map { case (expr, result) => (expr, expr, result) }

    executeTests(Table(header, unaryTests : _*))

  }

  "game.misere.MisereCanonicalGame" should "implement unary methods correctly" in {

    val tests = MisereCanonicalGameTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests : _*))

  }

  it should "implement binary methods correctly" in {

    val instances = Seq(
      ("*[[2]] Distinguisher *[2]+*[2]", "*[(2#1)21]"),
      ("*[[[2]]] IsLinkedTo *[2]+*[2]", "true"),
      ("*[[[2]]] IsLinkedTo *[[2]]", "false"),
      ("*[[[2]]] Link *[2]+*[2]", "*[((2#1)21)32]"),
      ("*[[[2]]] Link *[[2]]", "!!Those misere games are not linked: *[2##], *[2#]"),
      ("*[[[2]]] MisereMinus *[[2]]", "*[2[##-2]0]"),
      ("*[[[2]]] MisereMinus *[[4]]", "!!Those misere games are not subtractable: *[2##], *[4#]")
    )

    val binaryTests = instances map { case (in, out) => (in, in, out) }

    executeTests(Table(header, binaryTests : _*))

  }

  "game.Game" should "behave correctly" in {

    testPackage declareSubpackage "game"
    decl("test.game.NoDepthHint", "singleton class NoDepthHint extends Game override def Options(player as Player) := [this]; end")

    executeTests(Table(
      header,
      ("CanonicalForm on loopy game", "game.grid.FoxAndGeese({(3,1),(3,3),(3,5),(3,7)}, (1,1)).CanonicalForm",
        "!!That is not a short game. If that is intentional, try `GameValue` in place of `CanonicalForm`.")
      //("DepthHint not implemented", "test.game.NoDepthHint.GameValue",
      //  "!!That game is loopy (not a short game). If that is intentional, it must implement the `DepthHint` method. See the CGSuite documentation for more details.")
    ))

  }

  it should "implement methods correctly" in {
    executeTests(Table(
      header,
      ("Followers", """game.grid.Amazons("x...|o...").Followers.Size""", "2784"),
      ("MisereOutcomeClass", """game.grid.Domineering(Grid.Empty(2, 4)).MisereOutcomeClass""", "N")
    ))
  }

  "game.CompoundGame" should "implement compounds correctly" in {

    val tests = CompoundGameTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests : _*))

  }

  it should "implement negatives correctly" in {
    executeTests(Table(
      header,
      ("-ConjunctiveSum", "-(1 ConjunctiveSum '{1|0}')", "-1 ConjunctiveSum '{0|-1}'"),
      ("-ConwayProduct", "-(1 ConwayProduct '{1|0}')", "-(1 * '{1|0}')"),
      ("-ConwayProduct", "-(1/2 ConwayProduct '{1|0}')", "-(1/2 ConwayProduct '{1|0}')"),
      ("-DisjunctiveSum", "-(1 + '{1|0}')", "-1 + '{0|-1}'"),
      ("-OrdinalProduct", "-(1 OrdinalProduct '{1|0}')", "-(1 OrdinalProduct '{1|0}')"),
      ("-OrdinalSum", "-(1 : '{1|0}')", "-1 : '{0|-1}'"),
      ("-ConjunctiveSum", "-(1 SelectiveSum '{1|0}')", "-1 SelectiveSum '{0|-1}'"),
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
        "[[Left,Right,1,1],[Right,Left,-1,2]]")
    ))
  }

}

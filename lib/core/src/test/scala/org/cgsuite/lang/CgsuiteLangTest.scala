package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class CgsuiteLangTest extends CgscriptSpec {

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

  "cgsuite.lang.List" should "implement methods correctly" in {
    executeTests(Table(
      header,
      ("List: Adjoin", "[5,12,13] Adjoin 99", "[5,12,13,99]"),
      ("List: Concat", "[5,12,13] Concat [99,101]", "[5,12,13,99,101]"),
      ("List: Distinct", "[5,12,5,13,12].Distinct", "[5,12,13]"),
      ("List: Grouped", "[0,1,2,3,4,5,6,7,8,9] Grouped 3", "[[0,1,2],[3,4,5],[6,7,8],[9]]"),
      ("List: IndexOf", "[5,12,13].IndexOf(12)", "2"),
      ("List: Length", "[5,12,13].Length", "3"),
      ("List: Lookup", "[5,12,13][2]", "12"),
      ("List: Lookup out of bounds", "[5,12,13][4]", "!!List index out of bounds: 4"),
      ("List: Lookup way out of bounds", "[5,12,13][2^100]", "!!Overflow."),
      ("List: PeriodicTable", "[0,1,2,3,4,5,6,7,8,9] PeriodicTable 3",
        """0 | 1 | 2
          |--+---+--
          |3 | 4 | 5
          |--+---+--
          |6 | 7 | 8
          |--+---+--
          |9""".stripMargin),
      ("List: Sorted", "[[5,3,7],[1,6,3],[9,2,8]].Sorted", "[[1,6,3],[5,3,7],[9,2,8]]"),
      ("List: Sorted with non-dyadic rationals", "[92/47, 41/23, 9/4, 3].Sorted", "[41/23,92/47,9/4,3]"),
      ("List: SortedWith", "[[5,3,7],[1,6,3],[9,2,8]].SortedWith((a, b) -> a[2] - b[2])", "[[9,2,8],[5,3,7],[1,6,3]]"),
      ("List: SortedWith invalid comparator", "[[5,3,7],[1,6,3],[9,2,8]].SortedWith((a, b) -> \"I am a banana.\")",
        "!!Expected `game.Integer`; found `cgsuite.lang.String`."),
      ("List: SortedWith invalid comparator 2", "[[5,3,7],[1,6,3],[9,2,8]].SortedWith(a -> 0)",
        "!!Function has invalid number of parameters (expecting 2, has 1)."),
      ("List: Updated", "[5,12,13].Updated(2, 99)", "[5,99,13]"),
      ("List: Updated out of bounds", "[5,12,13].Updated(0, 99)", "!!List index out of bounds: 0")
    ))
  }

  "cgsuite.lang.Map" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("Map: Lookup", "{7 => true, 1/2 => ^^*}[1/2]", "^^*"),
      ("Map: Lookup not found", "{7 => true, 1/2 => ^^*}[1/4]", "!!Key not found: 1/4"),
      ("Map: ContainsKey", "{7 => true, 1/2 => ^^*} ContainsKey 7", "true"),
      ("Map: Entries", "{7 => true, 1/2 => ^^*}.Entries", "{1/2 => ^^*,7 => true}"),
      ("Map: Keys", "{7 => true, 1/2 => ^^*}.Keys", "{1/2,7}"),
      ("Map: Values", "{7 => true, 1/2 => ^^*}.Values", "{^^*,true}")
    ))

  }

  "cgsuite.lang.Object" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("Object.Class", "3.Class", "\u27eagame.Integer\u27eb"),
      ("Object.EnclosingObject 1", "3.EnclosingObject", "Nothing"),
      ("Object.EnclosingObject 2", """game.grid.Amazons("x...|o...").EnclosingObject""", "Amazons"),
      ("Object.JavaClass", "3.JavaClass", "\"org.cgsuite.core.SmallIntegerImpl\""),
      ("Object.ToOutput", "3.ToOutput", "3"),
      ("Object.ToString", "3.ToString", "\"3\"")
    ))

  }

  "cgsuite.lang.Range" should "implement Collection faithfully" in {

    executeTests(Table(
      header,
      ("Range: Contains(4)", "(1..10..2).Contains(4)", "false"),
      ("Range: Contains(9)", "(1..10..2).Contains(9)", "true"),
      ("Range: IsEmpty", "(1..0).IsEmpty", "true"),
      ("Range: IsEmpty", "(1..10).IsEmpty", "false"),
      ("Range: ToList", "(1..10).ToList", "1..10"),
      ("Range: ToSet", "(1..10).ToSet", "{1,2,3,4,5,6,7,8,9,10}")
    ))

  }

  "cgsuite.lang.String" should "implement methods properly" in {

    executeTests(Table(
      header,
      ("op[]", "\"Hackenbush\"[7]", "\"b\""),
      ("op[] out of bounds", "\"Hackenbush\"[0]", "!!String index out of bounds: 0"),
      ("op[] out of bounds", "\"Hackenbush\"[11]", "!!String index out of bounds: 11"),
      ("Concat", """"Winning " Concat "Ways"""", "\"Winning Ways\""),
      ("Length", "\"Berlekamp\".Length", "9"),
      ("Matches", """"On Numbers and Games" Matches ".*umber.*G+.*"""", "true"),
      ("Replace", """"Jangly".Replace("angl", "ohn H. Conwa")""", "\"John H. Conway\""),
      ("ReplaceRegex", """"Mathematical Plays".ReplaceRegex("(t|P)..", "")""", "\"Mamaal ys\""),
      ("ReplaceRegex 2", """"Winning Ways".ReplaceRegex("W(.)[a-z]*", "M$1sh")""", "\"Mish Mash\""),
      ("Substring", "\"Clobber\".Substring(4, 6)", "\"bbe\""),
      ("ToLowerCase", "\"Toads and Frogs\".ToLowerCase", "\"toads and frogs\""),
      ("ToUpperCase", "\"Toads and Frogs\".ToUpperCase", "\"TOADS AND FROGS\""),
      ("ToUnquotedOutput", "\"Toads and Frogs\".ToUnquotedOutput", "Toads and Frogs"),
      ("Updated", """"Short".Updated(2, "n")""", "\"Snort\"")
    ))

  }

  "cgsuite.util.Table" should "implement methods properly" in {
    executeTests(Table(
      header,
      ("Table construction", "table := Table([n,n^2] for n from 1 to 3)",
        """|1 | 1
           |--+--
           |2 | 4
           |--+--
           |3 | 9""".stripMargin),
      ("Intensity plot", "table.IntensityPlot()", "<3 x 2 IntensityPlot>")
    ))
  }

  "cgsuite.util.Thermograph" should "implement methods properly" in {
    executeTests(Table(
      header,
      ("Thermograph.Wall(Left)", "{3||2|1}.Thermograph.Wall(Left)", "Trajectory(9/4,[3/4],[0,-1])"),
      ("Thermograph.Wall(Right)", "{3||2|1}.Thermograph.Wall(Right)", "Trajectory(9/4,[3/4,1/2],[0,1,0])")
    ))
  }

}

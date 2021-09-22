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
      ("List: Lookup", "[5,12,13][2]", "12"),
      ("List: Lookup out of bounds", "[5,12,13][4]", "!!List index out of bounds: 4"),
      ("List: Lookup way out of bounds", "[5,12,13][2^100]", "!!Overflow."),
      ("List: Updated", "[5,12,13].Updated(2, 99)", "[5,99,13]"),
      ("List: Updated out of bounds", "[5,12,13].Updated(0, 99)", "!!List index out of bounds: 0"),
      ("List: Append", "[5,12,13] Append 99", "[5,12,13,99]"),
      ("List: AppendAll", "[5,12,13] AppendAll [99,101]", "[5,12,13,99,101]"),
      ("List: Grouped", "[0,1,2,3,4,5,6,7,8,9] Grouped 3", "[[0,1,2],[3,4,5],[6,7,8],[9]]"),
      ("List: PeriodicTable", "[0,1,2,3,4,5,6,7,8,9] PeriodicTable 3",
        """0 | 1 | 2
          |--+---+--
          |3 | 4 | 5
          |--+---+--
          |6 | 7 | 8
          |--+---+--
          |9""".stripMargin)
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

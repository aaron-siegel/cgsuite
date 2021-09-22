package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class GameGridTest extends CgscriptSpec {

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
      ("Domineering", """game.grid.Domineering(Grid.Empty(4,4)).CanonicalForm""", "+-{0,{{2|0},2Tiny(2)|{2|0},Miny(2)}}")
    ))
  }

  it should "define FoxAndGeese properly" in {
    executeTests(Table(
      header,
      ("FoxAndGeese", "game.grid.FoxAndGeese({(4,2),(4,4),(4,6),(4,8)}, (1,1)).GameValue", "{4*|7/2}"),
      ("CeyloneseFoxAndGeese", "game.grid.CeyloneseFoxAndGeese({(4,2),(4,4),(4,6),(4,8)}, (1,1)).GameValue", "{9||4v[on]*|5/2*|||3|5/2*,5/2v||5/2}"),
      ("GenFoxAndGeese", "game.grid.GenFoxAndGeese(boardWidth => 10)({(3,1),(3,3),(3,5),(3,7),(3,9)}, (1,9)).GameValue", "{6over|5*}"),
      ("FoxAndGeese.ValuesTable", "game.grid.FoxAndGeese.ValuesTable({(3,1),(3,3),(3,5),(3,7)})",
        """|      "X" |       |       "X" |   |      "X" |    |        "X" |      @
           |----------+-------+-----------+---+----------+----+------------+------@
           |          | 2over |           | 2 |          | 3* |            | 4over@
           |----------+-------+-----------+---+----------+----+------------+------@
           |{6|2over} |       | {2over|2} |   | {4|3||2} |    | {4over|3*} |      @""".stripMargin filterNot { _ == '@' }),
      ("FoxAndGeese Validation - 1", "game.grid.FoxAndGeese({(3,1),(3,3),(3,5),(3,7)}, (1,6))", "!!`fox` must be a valid `Coordinates` (1 <= col <= boardWidth; row >= 1; row + col even)"),
      ("FoxAndGeese Validation - 2", "game.grid.FoxAndGeese({(3,1),(3,3),(2,5),(3,7)}, (1,1))", "!!Every element of `geese` must be a valid `Coordinates` (1 <= col <= boardWidth; row >= 1; row + col even)"),
      ("FoxAndGeese Validation - 3", "game.grid.GenFoxAndGeese(foxRange => 0)", "!!`foxRange` must be >= 1")
    ))
  }

}

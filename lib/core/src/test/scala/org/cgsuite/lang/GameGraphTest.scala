package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class GameGraphTest extends CgscriptSpec {

  "game.graph" should "define base methods properly" in {
    executeTests(Table(
      header
    ))
  }

  it should "define Col properly" in {
    executeTests(Table(
      header,
      ("Col", """game.graph.Col("1R-2-3R-4-5L-6-7R-8-9R-10").CanonicalForm""", "7/8"),
      ("Snort", """game.graph.Snort("1L-2-3-4-5L").CanonicalForm""", "{4|0,+-1}"),
      ("Snort", """game.graph.Snort("1-2-3-4;3-5").CanonicalForm""", "+-{3|2}")
    ))
  }

}

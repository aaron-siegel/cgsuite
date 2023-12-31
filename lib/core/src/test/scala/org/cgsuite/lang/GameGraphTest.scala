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
      ("Col", """game.graph.Col("R-.-R-.-L-.-R-.-R-.").CanonicalForm""", "7/8"),
      ("Snort", """game.graph.Snort("L-.-.-.-L").CanonicalForm""", "{4|0,+-1}"),
      ("Snort", """game.graph.Snort(".-.-.(-.;-.)").CanonicalForm""", "+-{3|2}")
    ))
  }

}

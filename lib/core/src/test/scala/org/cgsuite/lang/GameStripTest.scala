package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class GameStripTest extends CgscriptSpec {

  "game.strip" should "define Toads and Frogs properly" in {
    executeTests(Table(
      header,
      ("ToadsAndFrogs", """game.strip.ToadsAndFrogs("ttttt..fffff").CanonicalForm""", "+-{{2|*},{5/2||2|{0||||{0||v<2>|-1},{0||||0||Miny(1/32)|-2|||-1/2*}|||v<2>|-1/2||-1*}|||0}}"),
      ("BackslidingToadsAndFrogs", """game.strip.BackslidingToadsAndFrogs("ttt..fff").GameValue""", "{on||0|-1/2} & {1/2|0||off}"),
      ("GenToadsAndFrogs", """game.strip.GenToadsAndFrogs(2)("tttt..fff").CanonicalForm""", "{1/2*|v}"),
      ("ElephantsAndRhinos", """game.strip.ElephantsAndRhinos("tttt..fff").CanonicalForm""", "1")
    ))
  }

  "game.strip" should "define Turning properly" in {
    executeTests(Table(
      header,
      ("Turning", """game.strip.Turning(game.heap.Spawning("1-3"))("htththtth").NimValue""", "29"),
      ("Turning", """game.strip.Turning("1-3")("htththtth").NimValue""", "29")      // Same with String constructor
    ))
  }

  it should "avoid a weird class load order bug" in {
    executeTests(Table(
      header,
      ("GenToadsAndFrogs loaded first", """game.strip.GenToadsAndFrogs(2).Class""", "\u27eagame.strip.GenToadsAndFrogs\u27eb")
    ))
  }

}

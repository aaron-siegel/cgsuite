package org.cgsuite.lang

class GameStripTest extends CgscriptSpec {

  "game.strip" should "define Toads and Frogs properly" in {
    executeTests(Table(
      header,
      ("ToadsAndFrogs", """game.strip.ToadsAndFrogs("ttttt..fffff").CanonicalForm""", "+-{{2|*},{5/2||2|{0||||{0||v<2>|-1},{0||||0||Miny(1/32)|-2|||-1/2*}|||v<2>|-1/2||-1*}|||0}}"),
      ("BackslidingToadsAndFrogs", """game.strip.BackslidingToadsAndFrogs("ttt..fff").GameValue""", "{on||0|-1/2} & {1/2|0||off}"),
      ("GenToadsAndFrogs", """game.strip.GenToadsAndFrogs(2)("tttt..fff").CanonicalForm""", "{1/2*|v}")
    ))
  }

}

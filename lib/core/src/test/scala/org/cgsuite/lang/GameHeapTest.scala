package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class GameHeapTest extends CgscriptSpec {

  "game.heap" should "define TakeAndBreak properly" in {

    val instances = Seq(

      ("game.heap.Nim", "0.[3]", "20", "[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]", "Nothing",
        "[1,0,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]", "[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]"),

      ("game.heap.GrundysGame", "4!.0", "9", "[0,0,0,1,0,2,1,0,2,1,0,2,1,3,2,1,3,2,4,3,0]", "Nothing",
        "[1,1,1,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2,0,1,2]", "[0,0,0,1,0,2,1,0,2,1,0,2,1,3^1431,2,1,3^1431,2,4^0564,3^1431,0^20]"),

      ("game.heap.Kayles", "0.77", "20", "[0,1,2,3,1,4,3,2,1,4,2,6,4,1,2,7,1,4,3,2,1]",
        "Periodicity(Period => 12, Preperiod => 71, Saltus => 0)",
        "[1,0,2,3,0,1,3,2,1,0,2,4,0,1,2,5,1,6,3,2,0]",
        "[0,1,2,3,1,4^146,3,2^2,1^1,4^046,2^2,6^46,4^046,1^1,2^2,7^57,1^13,4^64,3^31,2^20,1^031]"),

      ("game.heap.DawsonsKayles", "0.07", "10", "[0,0,1,1,2,0,3,1,1,0,3,3,2,2,4,0,5,2,2,3,3]",
        "Periodicity(Period => 34, Preperiod => 53, Saltus => 0)",
        "[1,1,0,0,2,1,3,0,0,1,1,3,0,2,1,1,0,0,2,1,3]", "[0,0,1,1,2,0,3,1,1,0,3^1431,3,2^0520,2,4^146,0,5^057,2^0520,2,3^1431,3]"),

      ("game.heap.Subtraction([2,3,7])", "0.0330003", "3", "[0,0,1,1,2,0,0,1,1,2,0,0,1,1,2,0,0,1,1,2,0]",
        "Periodicity(Period => 5, Preperiod => 0, Saltus => 0)",
        "[1,1,0,0,2,1,1,0,0,2,1,1,0,0,2,1,1,0,0,2,1]", "[0,0,1,1,2,0,0,1,1,2,0,0,1,1,2,0,0,1,1,2,0]"),

      ("game.heap.Subtraction([2,3,7], allbut => true)", "0.3003330[3]", "17", "[0,1,0,1,2,3,2,3,4,5,4,5,6,7,6,7,8,9,8,9,10]", "Nothing",
        "[1,0,1,0,2,3,2,3,4,5,4,5,6,7,6,7,8,9,8,9,10]", "[0,1,0,1,2,3,2,3,4,5,4,5,6,7,6,7,8,9,8,9,10]"),

      ("game.heap.TakeAndBreak(\"0.3F\")", "0.3F", "38", "[0,1,2,0,1,2,3,4,5,3,4,5,6,7,8,6,7,8,9,10,11]", "Nothing",
        "[1,0,2,1,0,2,1,0,4,1,0,4,1,0,6,1,0,6,1,0,6]",
        "[0,1,2,0,1,2,3^1431,4^0564,5^46,3^1431,4^0564,5^4875,6^(1,9,10,8),7^075,8^6875,6^(1,9,10,8),7^075,8^(6,10,8),9^(1,9,12,11,9),10^(0,11,13,9,11),11^(6,10,8)]")

    )

    val tests = instances flatMap { case (rs, code, optionCount, nimSequence, periodicity, misereNimValue, genus) =>
      Seq(
        (s"$rs.code", s"$rs.code", s"\"$code\""),
        (s"$rs(20).Options.Size", s"$rs(20).Options.Size", optionCount),
        (s"$rs.NimValue", s"[$rs(n).NimValue for n from 0 to 20]", nimSequence),
        (s"$rs.NimValueSequence", s"$rs.NimValueSequence(20)", nimSequence),
        (s"$rs.CheckPeriodicity(2000)", s"$rs.CheckPeriodicity(2000)", periodicity),
        (s"$rs.MisereNimValue", s"[$rs(n).MisereNimValue for n from 0 to 20]", misereNimValue),
        (s"$rs.Genus", s"[$rs(n).Genus for n from 0 to 20]", genus)
      )
    }

    val moreTests = Seq(
      ("game.heap.GrundysGame.MisereCanonicalForm", "game.heap.GrundysGame(22).MisereCanonicalForm", "*[(((2[2]21)[2](2[2]21)20)(2[2]21)3)((2[2]21)[2](2[2]21)20)(2[2]21)[1]2[2]1]")
    )

    executeTests(Table(header, tests ++ moreTests : _*))

  }

  it should "define other heap games properly" in {

    val instances = Seq(

      // Mock Turtles
      ("game.heap.Spawning(\"1-3\")", "191", "[0,1,2,4,7,8,11,13,14,16,19,21,22,25,26,28,31,32,35,37,38]"),
      // Moidores
      ("game.heap.Spawning(\"1-9\")", "169766", "[0,1,2,4,8,16,32,64,128,256,511,512,1024,2048,4096,7711,8192,16384,26215,32768,43691]"),
      // Triplets
      ("game.heap.Spawning(\"3\")", "171", "[0,0,0,1,2,4,7,8,11,13,14,16,19,21,22,25,26,28,31,32,35]"),
      // Ruler
      ("game.heap.Spawning(\"1+C\")", "20", "[0,1,2,1,4,1,2,1,8,1,2,1,4,1,2,1,16,1,2,1,4]"),
      // Mock Turtle Fives
      ("game.heap.Spawning(\"1-3C(5)\")", "11", "[0,1,2,4,7,8,1,2,4,7,8,1,2,4,7,8,1,2,4,7,8]"),
      // Triplet Fives
      ("game.heap.Spawning(\"3C(5)\")", "6", "[0,0,0,1,2,4,0,0,1,2,4,0,0,1,2,4,0,0,1,2,4]"),
      // Ruler Fives
      ("game.heap.Spawning(\"1-5C\")", "5", "[0,1,2,1,4,1,2,1,4,1,2,1,4,1,2,1,4,1,2,1,4]"),
      // Turnips
      ("game.heap.Spawning(\"3E\")", "9", "[0,0,0,1,0,0,1,2,2,1,0,0,1,0,0,1,2,2,1,4,4]"),
      // Grunt
      ("game.heap.Spawning(\"4FS\")", "9", "[0,0,0,0,1,0,2,1,0,2,1,0,2,1,3,2,1,3,2,4,3]"),
      // Sym
      ("game.heap.Spawning(\"1+S\")", "2046", "[0,1,2,4,3,6,7,8,16,18,25,32,11,64,31,128,10,256,5,512,28]"),

      ("game.heap.FunctionalHeapRuleset(k -> [[a,b] for a from 0 to k - 1 for b from 0 to a - 1])",
        "190", "[0,0,1,2,4,7,8,11,13,14,16,19,21,22,25,26,28,31,32,35,37]")

    )

    val tests = instances flatMap { case (rs, optionCount, nimSequence) =>
      Seq(
        (s"$rs.NimValue", s"[$rs(n).NimValue for n from 0 to 20]", nimSequence),
        (s"$rs(20).Options.Size", s"$rs(20).Options.Size", optionCount),
        (s"$rs.NimValueSequence", s"$rs.NimValueSequence(20)", nimSequence)
      )
    }

    executeTests(Table(header, tests : _*))

  }

  it should "define partizan heap games properly" in {

    executeTests(Table(
      header,
      ("PartizanSubtraction(1,3|2,3)", "game.heap.PartizanSubtraction([1,3],[2,3])(6).CanonicalForm",
        "{1|1,{1|0}||0,{1|0}|0|||0,{1|0}|0}")
    ))

  }

  it should "define coordinate games properly" in {

    val instances = Seq(
      ("game.heap.Wythoff", "30", "[10,11,9,8,13,12,0,15,16,17,14,18,7,6,2,3,1,4,5,23,28]",
        "[10,11,9,8^8,13^(13),12^(12),0^0,15^(15),16^(16),17^(17),14^(14),18^(18),7^7,6^6,2^2,3^3,1^1,4^4,5^5,23^(23),28^(28)]"),
      ("game.heap.GenWythoff(x -> x + 2)", "48", "[10,11,12,13,0,14,9,17,18,19,20,21,22,23,24,25,26,15,28,29,30]",
        "[10,11,12,13,0,14,9,17,18,19,20,21,22,23,24,25,26,15,28,29,30]"),
      ("game.heap.FibonacciNim", "10", "[0,2,2,2,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]", "[0,2,2,2,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5]"),
      ("game.heap.TakeAway(3)", "10", "[0,2,2,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6]", "[0,2,2,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6]"),
      ("game.heap.Mem", "1", "[3,3,3,3,2,2,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0]", "[3,3,3,3,2,2,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0]"),
      ("game.heap.GenMem((memory, j) -> j != memory)", "9", "[6,6,6,4,6,6,3,6,6,6,6,6,6,6,6,6,6,6,6,6,6]", "[6,6,6,4,6,6,3,6,6,6,6,6,6,6,6,6,6,6,6,6,6]")

    )

    val tests = instances flatMap { case (rs, optionCount, nimRow10, genusRow10) =>
      Seq(
        (s"$rs((10,10)).Options.Size", s"$rs((10,10)).Options.Size", optionCount),
        (s"$rs.NimValueSequence", s"[$rs((10,n)).NimValue for n from 0 to 20]", nimRow10),
        (s"$rs.Genus", s"[$rs((10,n)).Genus for n from 0 to 20]", genusRow10)
      )
    }

    val moreTests = Seq(
      ("game.heap.GenMem.NimValueTable", "game.heap.GenMem((memory, j) -> j != memory).NimValueTable(10, 10)",
        """0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0
          |--+---+---+---+---+---+---+---+---+---+--
          |1 | 0 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1
          |--+---+---+---+---+---+---+---+---+---+--
          |1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1
          |--+---+---+---+---+---+---+---+---+---+--
          |2 | 2 | 2 | 0 | 2 | 2 | 2 | 2 | 2 | 2 | 2
          |--+---+---+---+---+---+---+---+---+---+--
          |3 | 2 | 3 | 3 | 0 | 3 | 3 | 3 | 3 | 3 | 3
          |--+---+---+---+---+---+---+---+---+---+--
          |3 | 3 | 3 | 3 | 3 | 0 | 3 | 3 | 3 | 3 | 3
          |--+---+---+---+---+---+---+---+---+---+--
          |2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2
          |--+---+---+---+---+---+---+---+---+---+--
          |4 | 4 | 4 | 4 | 4 | 4 | 4 | 0 | 4 | 4 | 4
          |--+---+---+---+---+---+---+---+---+---+--
          |5 | 4 | 5 | 3 | 5 | 5 | 5 | 5 | 5 | 5 | 5
          |--+---+---+---+---+---+---+---+---+---+--
          |5 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 5 | 0 | 5
          |--+---+---+---+---+---+---+---+---+---+--
          |6 | 6 | 6 | 4 | 6 | 6 | 3 | 6 | 6 | 6 | 6""".stripMargin)
    )

    // First n P-positions of r-Wythoff
    def wytP(r: Int, n: Int) = {
      val alpha = 0.5 * (2 - r + math.sqrt(r * r + 4))
      val beta = alpha + r
      (0 until n) map { i => ((i * alpha).toInt, (i * beta).toInt) }
    }

    // Check that r-Wythoff (1 <= r <= 5) gives the correct nim value 0 for the first 15 P-positions
    // (We compute the P-positions using Fraenkel's formula)
    val wytTests = (1 to 5) map { r =>
      val ppos = wytP(r, 15)
      println(s"r = $r: ${ppos mkString " "}")
      (s"game.heap.GenWythoff($r) P-positions",
        s"""rs := game.heap.GenWythoff(x -> x + $r);
           |[${ppos mkString ","}].Apply(coord -> rs(coord).NimValue)
           |""".stripMargin,
        "[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]")
    }

    executeTests(Table(header, tests ++ moreTests ++ wytTests : _*))

  }

  // TODO Partizan splittles, partizan takeaway, FunctionalCoordinateRuleset, PartizanCoordinateRuleset

  it should "define partizan coordinate games properly" in {

    val instances = Seq(
      ("game.heap.Cutcake", "4", "[-7,-7,-3,-3,-1,-1,-1,-1,0]"),
      ("game.heap.MaundyCake", "3", "[-7,-7,-3,-3,-1,-3,-1,-3,0]"),
      ("game.heap.Eatcake", "4", "[-8,^,{^|||^*|v||0},{^^|*},{^^|*||0,{0|vv*}},{^3|^*||*},{^3|^*||*|||0,{0,{^|0,v*}|||0|vv*||0}},{0||^^*|0|||*},*]"),
      ("game.heap.TurnAndEatcake", "4", "[0,v,0,v,0,v<3>,0,v,0]"),
      // Twisted Maundy cake! This is a fun one:
      ("game.heap.GenCutcake(game.heap.TakeAndBreak(\"{2+}=.0\"), game.heap.TakeAndBreak(\"{2+}=.0\"), twisted => true)", "3",
        "[1,1,{8|-2},{8|-3},{8|-6},{8|-5},{8|-6,{-4|-14}},{10|-7},+-12]"),
      ("game.heap.PartizanEuclid", "0", "[0,0,0,v,0,0,^,^5,0]")
    )

    val tests = instances flatMap { case (rs, optionCount, values) =>
      Seq(
        (s"$rs((8,8)).LeftOptions.Size", s"$rs((8,8)).LeftOptions.Size", optionCount),
        (s"$rs canonical forms", s"[$rs((8,n)).CanonicalForm for n from 0 to 8]", values)
      )
    }

    executeTests(Table(header, tests : _*))

  }

}

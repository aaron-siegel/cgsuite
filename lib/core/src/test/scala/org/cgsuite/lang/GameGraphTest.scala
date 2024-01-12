package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class GameGraphTest extends CgscriptSpec {

  "game.graph" should "define base methods properly" in {
    executeTests(Table(
      header
    ))
  }

  it should "define ArcKayles properly" in {
    executeTests(Table(
      header,
      ("Arc Kayles paths are Dawson's Kayles heaps",
        "[game.graph.ArcKayles(Graph.Path(n)).NimValue for n from 1 to 50]",
        "[0,1,1,2,0,3,1,1,0,3,3,2,2,4,0,5,2,2,3,3,0,1,1,3,0,2,1,1,0,4,5,2,7,4,0,1,1,2,0,3,1,1,0,3,3,2,2,4,4,5]"),
      ("Arc Kayles looped paths are Kayles heaps",
        """def loopedPath(n) begin
          |  var pathAdj := Graph.Path(n).AdjacencyList;
          |  var loopedPathAdj := [pathAdj[i] Adjoin i for i from 1 to pathAdj.Length];
          |  Graph.FromAdjacencyList(loopedPathAdj)
          |end;
          |[game.graph.ArcKayles(loopedPath(n)).NimValue for n from 1 to 50]""".stripMargin,
        "[1,2,3,1,4,3,2,1,4,2,6,4,1,2,7,1,4,3,2,1,4,6,7,4,1,2,8,5,4,7,2,1,8,6,7,4,1,2,3,1,4,7,2,1,8,2,7,4,1,2]")
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

  it should "define Hackenbush properly" in {
    executeTests(Table(
      header,
      ("Hackenbush Number", """game.graph.Hackenbush("lrlrrllr").CanonicalForm""", "77/128"),
      ("Hackenbush Tree", """game.graph.Hackenbush("l(l;rl)").CanonicalForm""", "3/2"),
      ("Hackenbush Nimber", """game.graph.Hackenbush("eeeeeee").CanonicalForm""", "*7"),
      ("Hackenbush RBG Stalk", """game.graph.Hackenbush("lrlerrr").CanonicalForm""", "3/4v[3]*"),
      ("Hackenbush Green Tree", """game.graph.Hackenbush("e(ee(e;e);e(eee;ee);e(ee;e))").CanonicalForm""", "*5"),
      ("Hackenbush Green Girl",   // Figure 3 from WW, Chapter 7
        """game.graph.Hackenbush("e(ee:{waist};ee:{waist}e(ee;e(e:{head};e:{head}e)))").CanonicalForm""", "*2"),
      /* TODO Uncomment this if Hackenbush can be successfully optimized
      ("Hackenbush Moderately Hard Bed",    // Figure 31 from WW, Chapter 7
        """game.graph.Hackenbush("(lr:A(rr:B;rr:D;rr:F);lr:B(r(r:D;r:E));lr:C(rr:F;rr:G);lr:D;lr:E(r(r:F;r:G));lr:F;lr:G)").CanonicalForm""", "1/128"),
       */
      ("Hackenbush Disconnected", """game.graph.Hackenbush("lrlr;lr").CanonicalForm""", "!!That `Graph` is not connected."),
      ("Hackenbush Empty", """game.graph.Hackenbush("").CanonicalForm""", "!!That `Graph` is empty (no ground).")
    ))
  }

  it should "define Childish Hackenbush properly" in {
    executeTests(Table(
      header,
      ("Childish Hackenbush", """game.graph.ChildishHackenbush("l(l;rl)").CanonicalForm""", "2"),
      ("Childish Hackenbush hot", """game.graph.ChildishHackenbush(":{ground}llllllrrrr:{ground}").CanonicalForm""", "{4|-2}"),
      ("Childish Hackenbush fractional", """game.graph.ChildishHackenbush("r(l:T;r(l:T;r(l:T;r(l:T;rl:T))))").CanonicalForm""", "15/16"),
      ("Childish Hackenbush lollipop", """game.graph.ChildishHackenbush("(e:B;e:Bllllllrrr:B)").CanonicalForm""", "{0|^[4]*||vv}"),
      ("Childish Hackenbush lollipop 2", """game.graph.ChildishHackenbush("(e:B;e:B;e:Blllllllllrr:B)").AtomicWeight""", "3/2")
    ))
  }

  it should "define Partizan Arc Kayles properly" in {
    executeTests(Table(
      header,
      ("Partizan Arc Kayles", """game.graph.PartizanArcKayles("llr").CanonicalForm""", "1/2"),
      ("Partizan Arc Kayles with Green", """game.graph.PartizanArcKayles("lrlr(ll;ree(rl;l))").CanonicalForm""", "{3*|2*||3/2,3/2*|1/2*}")
    ))
  }

}

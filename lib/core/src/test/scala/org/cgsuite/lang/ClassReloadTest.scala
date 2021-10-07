package org.cgsuite.lang

import better.files._
import org.scalatest.prop.TableDrivenPropertyChecks.Table

class ClassReloadTest extends CgscriptSpec {

  val testClasspathDir = "target" / "cgs-test"

  "CGSuite classes" should "properly invalidate overridable method resolvers" in {

    if (!testClasspathDir.exists) {
      testClasspathDir.createDirectory()
    }
    CgscriptClasspath.declareClasspathRoot(testClasspathDir, ensureStartupScriptExists = false)

    // For reasons I still don't fully understand, only the TopplingDominoes example (and not the simpler
    // ReloadTest) fails when MemberResolution invalidation is disabled.

    testClasspathDir/"ReloadTest.cgs" overwrite
      """class ReloadTest
        |  def OverridableMethod := 0;
        |  class Nested(arg as Integer)
        |    def NestedMethod := OverridableMethod;
        |  end
        |end
        |""".stripMargin

    testClasspathDir/"ReloadTestSub.cgs" overwrite
      """singleton class ReloadTestSub extends ReloadTest
        |  override def OverridableMethod := 22;
        |  class Derived(arg as Integer) extends Nested
        |  end
        |end
        |""".stripMargin

    synchronized {
      wait(1000)
    }
    CgscriptClasspath.reloadModifiedFiles()

    executeTests(Table(
      header,
      ("Initial Class behavior (OverridableMethod)", "ReloadTestSub.OverridableMethod", "22"),
      ("Initial Class behavior (NestedMethod)", "ReloadTestSub.Derived(88).NestedMethod", "22")
    ))

    testClasspathDir/"ReloadTestSub.cgs" overwrite
      """singleton class ReloadTestSub extends ReloadTest
        |  override def OverridableMethod := UndefinedSymbol;
        |  class Derived(arg as Integer) extends Nested
        |  end
        |end
        |""".stripMargin

    synchronized {
      wait(1000)
    }
    CgscriptClasspath.reloadModifiedFiles()

    executeTestsNoClear(Table(
      header,
      ("Modified Class behavior (OverridableMethod)", "ReloadTestSub.OverridableMethod", "!!That variable is not defined: `UndefinedSymbol`"),
      ("Modified Class behavior (NestedMethod)", "ReloadTestSub.Derived(88).NestedMethod", "!!That variable is not defined: `UndefinedSymbol`")
    ))

    testClasspathDir/"TopplingDominoes.cgs" overwrite
      """singleton class TopplingDominoes extends game.strip.StripRuleset
        |
        |  class Position(strip as Strip) extends StripGame
        |
        |    override def Options(player as Player) begin
        |
        |      var us := player.Ordinal;
        |      var them := player.Opponent.Ordinal;
        |
        |      for n from 1 to strip.Length
        |      where strip[n] == us or strip[n] == 0
        |      yield Position(strip.Substrip(1, n-1))
        |      yield Position(strip.Substrip(n+1, strip.Length))
        |      end
        |
        |    end
        |
        |  end
        |
        |  override def CharMap := "exo";
        |
        |  override def Icons := [Icon.GrayStone, Icon.BlackStone, Icon.WhiteStone];
        |
        |end
        |""".stripMargin

    synchronized {
      wait(1000)
    }
    CgscriptClasspath.reloadModifiedFiles()

    executeTestsNoClear(Table(
      header,
      ("Initial Class Behavior", """TopplingDominoes("xoxo")""", """TopplingDominoes.Position("xoxo")""")
    ))

    testClasspathDir/"TopplingDominoes.cgs" overwrite
      """singleton class TopplingDominoes extends game.strip.StripRuleset
        |
        |  class Position(strip as Strip) extends StripGame
        |
        |    override def Options(player as Player) begin
        |
        |      var us := player.Ordinal;
        |      var them := player.Opponent.Ordinal;
        |
        |      for n from 1 to strip.Length
        |      where strip[n] == us or strip[n] == 0
        |      yield Position(strip.Substrip(1, n-1))
        |      yield Position(strip.Substrip(n+1, strip.Length))
        |      end
        |
        |    end
        |
        |  end
        |
        |  override def CharMap := "exo";
        |
        |  override def Icons := [Icon.GreyStone, Icon.BlackStone, Icon.WhiteStone];
        |
        |end
        |""".stripMargin

    synchronized {
      wait(1000)
    }
    CgscriptClasspath.reloadModifiedFiles()

    executeTestsNoClear(Table(
      header,
      ("Modified Class Behavior", """TopplingDominoes("xoxo")""", "!!Not a method or member variable: `GreyStone` (in object of class `cgsuite.lang.Class`)")
    ))

  }

}

package org.cgsuite.lang

import org.cgsuite.core.Game
import org.cgsuite.exception.EvalException
import org.cgsuite.util.{Explorer, UiHarness}
import org.scalatest.prop.TableDrivenPropertyChecks.Table

class CgsuiteUiTest extends CgscriptSpec {

  "cgsuite.ui.Explorer" should "implement methods correctly" in {

    UiHarness.setUiHarness(TestUiHarness)

    executeTests(Table(
      header,
      ("Explorer.NewExplorer", "Explorer.NewExplorer(19)", "!!TestUiHarness.createExplorer(19)"),
      ("Explorer.Eval", "Explorer(*22)", "!!TestUiHarness.createExplorer(*22)")
    ))
  }

}

object TestUiHarness extends UiHarness {

  override def clearUiVars(): Unit = { }

  override def createExplorer(g: Game): Explorer = throw EvalException(s"TestUiHarness.createExplorer(${System.objectToOutput(g).head})")

  override def print(obj: AnyRef): Unit = throw EvalException(s"TestUiHarness.print(${System.objectToOutput(obj).head})")

}

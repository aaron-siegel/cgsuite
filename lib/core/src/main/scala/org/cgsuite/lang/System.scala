package org.cgsuite.lang

import org.cgsuite.exception.EvalException
import org.cgsuite.util.UiHarness


object System {

  def print(obj: AnyRef): Unit = UiHarness.uiHarness.print(obj)

  def error(str: String): Unit = throw EvalException(str)

}

private class System

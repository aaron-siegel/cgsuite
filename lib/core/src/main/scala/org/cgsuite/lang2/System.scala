package org.cgsuite.lang2

import org.cgsuite.exception.EvalException
import org.cgsuite.util.UiHarness

object System {

  def clearAll(): Unit = {
    CgscriptClass.clearAll()
  }

  def error(str: String): Unit = throw EvalException(str)

  def print(obj: AnyRef): Unit = UiHarness.uiHarness.print(obj)

}

class System

package org.cgsuite.lang

import org.cgsuite.exception.EvalException


object System {

  // TODO This needs to be rearchitected
  def print(obj: AnyRef): Unit = println(obj)
  def error(str: String): Unit = throw EvalException(str)

}

private class System

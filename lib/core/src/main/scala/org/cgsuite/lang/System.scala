package org.cgsuite.lang

import org.cgsuite.exception.InputException


object System {

  def print(obj: AnyRef): Unit = println(obj)
  def error(str: String): Unit = throw InputException(str)

}

private class System

package org.cgsuite.lang

import org.cgsuite.exception.EvalException
import org.cgsuite.core.CanonicalShortGame
import org.cgsuite.util.UiHarness

object System {

  def clearAll(): Unit = {
    CgscriptClass.clearAll()
  }

  def error(str: String): Unit = throw EvalException(str)

  def print(obj: AnyRef): Unit = UiHarness.uiHarness.print(obj)

  def printSystemInfo(): Unit = {
    Vector(
      s"""Java ${java.lang.System.getProperty("java.version")}""",
      s"""${java.lang.System.getProperty("os.name")} ${java.lang.System.getProperty("os.version")}""",
      s"""Heap memory: ${java.lang.Runtime.getRuntime.maxMemory >> 20} MB""",
      s"""CanonicalShortGames recognized: ${CanonicalShortGame.gameCount}"""
    ) foreach print
  }

}

class System

package org.cgsuite.util

import org.cgsuite.core.Game

object UiHarness {

  var uiHarnessRef: UiHarness = _

  def uiHarness = uiHarnessRef

  def setUiHarness(harness: UiHarness): Unit = {
    this.uiHarnessRef = harness
  }

}

trait UiHarness {

  def clearUiVars(): Unit

  def createExplorer(g: Game): Explorer

  def print(obj: AnyRef): Unit

}

trait Explorer {

  def selection: Game

}

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

object Explorer {

  def newExplorer(g: Game): Explorer = UiHarness.uiHarness.createExplorer(g)

}

trait Explorer {

  def selection: Game

}

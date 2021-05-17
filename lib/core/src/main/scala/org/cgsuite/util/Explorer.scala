package org.cgsuite.util

import org.cgsuite.core.{Game, Player}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Explorer {

  def newExplorer(g: Game) = UiHarness.uiHarness.createExplorer(g)

}

class Explorer {

  private var selectedNode: Option[ExplorerNode] = None
  private val nodes = ArrayBuffer[ExplorerNode]()
  private val nodeMap = mutable.Map[Game, ExplorerNode]()

  def addNode(g: Game): ExplorerNode = {
    nodeMap getOrElseUpdate (g, createNode(g))
  }

  def selectNode(ordinal: Option[Int]): Unit = {
    selectedNode = ordinal map { nodes(_) }
  }

  def expandOptions(g: Game, player: Player): Iterable[ExplorerNode] = {
    g.options(player) map addNode
  }

  private def createNode(g: Game): ExplorerNode = {
    val newNode = ExplorerNode(nodes.length, g)
    nodes += newNode
    nodeMap(g) = newNode
    newNode
  }

}

case class ExplorerNode(ordinal: Int, g: Game)

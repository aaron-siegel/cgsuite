package org.cgsuite.util

import org.cgsuite.core.{Game, Player}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Explorer {

  def newExplorer(g: Game) = UiHarness.uiHarness.createExplorer(g)

}

class Explorer {

  private val allNodes = ArrayBuffer[Node]()
  private val rootNodes = mutable.Map[Game, Node]()

  def addRootNode(g: Game): Node = {
    rootNodes getOrElseUpdate (g, createNode(g))
  }

  def lookupNode(ordinal: Int): Node = allNodes(ordinal)

  def expandOptions(node: Node, player: Player): Iterable[Node] = {
    ensureExpanded(node, player, node.g.options(player))
  }

  def expandSensibleOptions(node: Node, player: Player): Iterable[Node] = {
    ensureExpanded(node, player, node.g.sensibleOptions(player))
  }

  def ensureExpanded(node: Node, player: Player, options: Iterable[Game]): Iterable[Node] = {
    val children = player match {
      case Player.Left => node.leftChildren
      case Player.Right => node.rightChildren
    }
    val newNodes = options collect {
      case option if !children.contains(option) => createNode(option)
    }
    newNodes foreach { node => children(node.g) = node }
    newNodes
  }

  private def createNode(g: Game): Node = {
    val newNode = Node(allNodes.length, g)
    allNodes += newNode
    newNode
  }

  case class Node(ordinal: Int, g: Game) {

    private[Explorer] val leftChildren = mutable.Map[Game, Node]()
    private[Explorer] val rightChildren = mutable.Map[Game, Node]()

  }

}

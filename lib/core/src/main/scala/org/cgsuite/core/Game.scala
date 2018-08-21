/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgsuite.core

import org.cgsuite.lang.NotShortGameException
import org.cgsuite.output.OutputTarget
import org.cgsuite.util.TranspositionTable

import scala.collection.mutable

trait Game extends OutputTarget {

  def unary_+ : Game = this
  def unary_- : Game = NegativeGame(this)

  def +(other: Game): Game = CompoundGame(CompoundType.Disjunctive, this, other)
  def -(other: Game): Game = this + (-other)

  def options(player: Player): Iterable[Game]

  def canonicalForm: CanonicalShortGame = {
    canonicalForm(new TranspositionTable())   // TODO Cache in class obj??
  }

  def canonicalForm(tt: TranspositionTable): CanonicalShortGame = {
    canonicalForm(tt, mutable.HashSet[Game]())
  }

  private def canonicalForm(tt: TranspositionTable, visited: mutable.Set[Game]): CanonicalShortGame = {
    val decomp = decomposition
    if (decomp.size == 1 && decomp.head == this) {
      canonicalFormR(tt, visited)
    } else {
      var result: CanonicalShortGame = Values.zero
      val it = decomp.iterator
      while (it.hasNext) {
        val component = it.next() match {
          case g: Game => g.canonicalFormR(tt, visited)
        }
        result += component
      }
      result
    }
  }

  private def canonicalFormR(tt: TranspositionTable, visited: mutable.Set[Game]): CanonicalShortGame = {
    tt.get(this) match {
      case Some(x: CanonicalShortGame) => x
      case None if !visited.contains(this) =>
        visited += this
        try {
          val lo = options(Left ) map { _.canonicalForm(tt, visited) }
          val ro = options(Right) map { _.canonicalForm(tt, visited) }
          val canonicalForm = CanonicalShortGame(lo, ro)
          tt.put(this, canonicalForm)
          canonicalForm
        } finally {
          visited -= this
        }
      case _ =>
        throw NotShortGameException(s"That is not a short game. If that is intentional, try `GameValue` in place of `CanonicalForm`.")
    }
  }

  def gameValue: SidedValue = gameValue(new TranspositionTable())

  def gameValue(tt: TranspositionTable): SidedValue = {
    try {
      canonicalForm(tt)
    } catch {
      case _: NotShortGameException => loopyGameValue(tt)
    }
  }

  final def loopyGameValue(tt: TranspositionTable): SidedValue = {
    tt.get(this) match {
      case Some(x: SidedValue) => x
      case Some(x) => sys.error("this should never happen")
      case _ =>
        val nodeMap = mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)]()
        buildNodeMap(tt, nodeMap)
        nodeMap foreach { case (g, (onsideNode, offsideNode)) =>
          if (!tt.contains(g)) {
            val value = SidedValue(onsideNode, offsideNode)
            tt.put(g, value)
          }
        }
        tt(this).asInstanceOf[SidedValue]
    }
  }

  private def buildNodeMap(tt: TranspositionTable, nodeMap: mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)])
    : (LoopyGame.Node, LoopyGame.Node) = {
    nodeMap.get(this) match {
      case Some(nodes) => nodes
      case None =>
        val decomp = decomposition
        if (decomp.size == 1 && decomp.head == this) {
          buildNodeMapR(tt, nodeMap)
        } else {
          var result: SidedValue = Values.zero
          val it = decomp.iterator
          while (it.hasNext) {
            it.next() match {
              case sv: SidedValue => result += sv
              case g: Game => result += g.loopyGameValue(tt)
            }
          }
          val nodes = (new LoopyGame.Node(result.onside.loopyGame), new LoopyGame.Node(result.offside.loopyGame))
          nodeMap.put(this, nodes)
          nodes
        }
    }
  }

  private def buildNodeMapR(tt: TranspositionTable, nodeMap: mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)])
    : (LoopyGame.Node, LoopyGame.Node) = {

    val onsideNode = new LoopyGame.Node()
    val offsideNode = new LoopyGame.Node()
    nodeMap.put(this, (onsideNode, offsideNode))
    val depth = depthHint
    options(Left) foreach { g =>
      if (g.depthHint < depth) {
        val value = g.loopyGameValue(tt)
        onsideNode.addLeftEdge(value.onside.loopyGame)
        offsideNode.addLeftEdge(value.offside.loopyGame)
      } else {
        val (onsideTarget, offsideTarget) = g.buildNodeMap(tt, nodeMap)
        onsideNode.addLeftEdge(onsideTarget)
        offsideNode.addLeftEdge(offsideTarget)
      }
    }
    options(Right) foreach { g =>
      if (g.depthHint < depth) {
        val value = g.loopyGameValue(tt)
        onsideNode.addRightEdge(value.onside.loopyGame)
        offsideNode.addRightEdge(value.offside.loopyGame)
      } else {
        val (onsideTarget, offsideTarget) = g.buildNodeMap(tt, nodeMap)
        onsideNode.addRightEdge(onsideTarget)
        offsideNode.addRightEdge(offsideTarget)
      }
    }
    (onsideNode, offsideNode)

  }

  def depthHint: Int = sys.error("Loopy games must override `depthHint`.")

  def gameName: String = getClass.getName

  def leftOptions: Iterable[Game] = options(Left)
  
  def rightOptions: Iterable[Game] = options(Right)

  def sensibleOptions(player: Player): Iterable[Game] = {
    val allOptions = options(player)
    val canonicalOptions = canonicalForm options player
    canonicalOptions flatMap { k =>
      if (player == Left)
        allOptions find { _.canonicalForm >= k }
      else
        allOptions find { _.canonicalForm <= k }
    }
  }

  def sensibleLines(player: Player): Iterable[Seq[Game]] = ???

  def decomposition: Iterable[_] = Seq(this)

}

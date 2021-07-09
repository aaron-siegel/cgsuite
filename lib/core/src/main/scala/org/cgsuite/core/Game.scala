package org.cgsuite.core

import org.cgsuite.exception.{NotImplementedException, NotShortGameException}
import org.cgsuite.output.OutputTarget
import org.cgsuite.util.{TranspositionCache, TranspositionTable}

import scala.collection.mutable

trait Game extends OutputTarget {

  def unary_+ : Game = this

  def unary_- : Game = NegativeGame(this)

  def +(that: Game): Game = CompoundGame(DisjunctiveSum, this, that)

  def -(that: Game): Game = this + (-that)

  def options(player: Player): Iterable[Game]

  def canonicalForm: CanonicalShortGame = {
    canonicalForm(new TranspositionCache())
  }

  def canonicalForm(tc: TranspositionCache): CanonicalShortGame = {
    CanonicalShortGameReducer.reduce(this, tc.tableFor[CanonicalShortGame]('CanonicalShortGame))
  }

  def conwayProduct(that: Game): Game = CompoundGame(ConwayProduct, this, that)

  def ordinalProduct(that: Game): Game = CompoundGame(OrdinalProduct, this, that)

  def ordinalSum(that: Game): Game = CompoundGame(OrdinalSum, this, that)

  def gameValue: SidedValue = gameValue(new TranspositionCache())

  def gameValue(tc: TranspositionCache): SidedValue = {
    try {
      canonicalForm(tc)
    } catch {
      case _: NotShortGameException => loopyGameValue(tc.tableFor[SidedValue]('SidedValue))
    }
  }

  final def loopyGameValue(tt: TranspositionTable[SidedValue]): SidedValue = {
    tt.get(this) match {
      case Some(x) => x
      case _ =>
        val nodeMap = mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)]()
        buildNodeMap(tt, nodeMap)
        nodeMap foreach { case (g, (onsideNode, offsideNode)) =>
          if (!tt.contains(g)) {
            val value = SidedValue(onsideNode, offsideNode)
            tt.put(g, value)
          }
        }
        tt(this)
    }
  }

  private def buildNodeMap(tt: TranspositionTable[SidedValue], nodeMap: mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)])
    : (LoopyGame.Node, LoopyGame.Node) = {
    nodeMap.get(this) match {
      case Some(nodes) => nodes
      case None =>
        val subst = substitution
        val decomp = subst.decomposition
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
          val nodes = (new LoopyGame.Node(result.onsideSimplified.loopyGame), new LoopyGame.Node(result.offsideSimplified.loopyGame))
          nodeMap.put(this, nodes)
          nodes
        }
    }
  }

  private def buildNodeMapR(tt: TranspositionTable[SidedValue], nodeMap: mutable.AnyRefMap[Game, (LoopyGame.Node, LoopyGame.Node)])
    : (LoopyGame.Node, LoopyGame.Node) = {

    val onsideNode = new LoopyGame.Node()
    val offsideNode = new LoopyGame.Node()
    nodeMap.put(this, (onsideNode, offsideNode))
    val depth = depthHint
    options(Left) foreach { g =>
      if (g.depthHint < depth) {
        val value = g.loopyGameValue(tt)
        onsideNode.addLeftEdge(value.onsideSimplified.loopyGame)
        offsideNode.addLeftEdge(value.offsideSimplified.loopyGame)
      } else {
        val (onsideTarget, offsideTarget) = g.buildNodeMap(tt, nodeMap)
        onsideNode.addLeftEdge(onsideTarget)
        offsideNode.addLeftEdge(offsideTarget)
      }
    }
    options(Right) foreach { g =>
      if (g.depthHint < depth) {
        val value = g.loopyGameValue(tt)
        onsideNode.addRightEdge(value.onsideSimplified.loopyGame)
        offsideNode.addRightEdge(value.offsideSimplified.loopyGame)
      } else {
        val (onsideTarget, offsideTarget) = g.buildNodeMap(tt, nodeMap)
        onsideNode.addRightEdge(onsideTarget)
        offsideNode.addRightEdge(offsideTarget)
      }
    }
    (onsideNode, offsideNode)

  }

  def depthHint: Integer = {
    throw NotImplementedException(
      "That game is loopy (not a short game). If that is intentional, it must implement the `DepthHint` method. See the CGSuite documentation for more details."
    )
  }

  def outcomeClass: LoopyOutcomeClass = gameValue.outcomeClass

  def gameName: String = getClass.getName

  def leftOptions: Iterable[Game] = options(Left)
  
  def rightOptions: Iterable[Game] = options(Right)

  def sensibleOptions(player: Player): Iterable[Game] = {
    val allOptions = this options player
    val canonicalOptions = canonicalForm options player
    canonicalOptions flatMap { k =>
      if (player == Left)
        allOptions find { _.canonicalForm >= k }
      else
        allOptions find { _.canonicalForm <= k }
    }
  }

  def sensibleLines(player: Player): Iterable[IndexedSeq[Game]] = {
    val canonicalOptions = canonicalForm options player
    canonicalOptions map { k =>
      val thisCanonicalForm = canonicalForm
      val line = mutable.ArrayBuffer[Game]()
      var done = false
      var current = this
      while (!done) {
        val options = current options player
        val sensibleOption = {
          if (player == Left)
            options find { _.canonicalForm >= k }
          else
            options find { _.canonicalForm <= k }
        }
        line += (sensibleOption getOrElse { sys error "unable to find sensible continuation" })
        done = line.last.canonicalForm == k
        if (!done) {
          // Find a reverting option
          val options = line.last options player.opponent
          val revertingOption = {
            if (player == Left)
              options find { _.canonicalForm <= thisCanonicalForm }
            else
              options find { _.canonicalForm >= thisCanonicalForm }
          }
          line += (revertingOption getOrElse { sys error "unable to find reverting continuation" })
          current = line.last
        }
      }
      line.toIndexedSeq
    }
  }

  def decomposition: Iterable[_] = Seq(this)

  def substitution: Game = this

}

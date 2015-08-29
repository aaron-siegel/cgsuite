package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.TranspositionTable

case class CompoundGame(compoundType: CompoundType.Value, g: Game, h: Game) extends Game {

  import CompoundType._

  override def unary_- : CompoundGame = CompoundGame(compoundType, -g, -h)

  def options(player: Player) = compoundType match {

    case Disjunctive =>
      g.options(player).map { CompoundGame(Disjunctive, _, h) } ++
      h.options(player).map { CompoundGame(Disjunctive, g, _) }

    case Ordinal =>
      g.options(player) ++ h.options(player).map { CompoundGame(Ordinal, g, _) }

  }

  override def canonicalForm(tt: TranspositionTable) = {
    compoundType match {
      case Disjunctive => g.canonicalForm(tt) + h.canonicalForm(tt)
      case _ => super.canonicalForm(tt)
    }
  }

  override def gameValue(tt: TranspositionTable) = {
    compoundType match {
      case Disjunctive => g.gameValue(tt) + h.gameValue(tt)
      case _ => super.gameValue(tt)
    }
  }

  override def decomposition: Iterable[_] = {
    compoundType match {
      case Disjunctive => Seq(g, h)
      case _ => super.decomposition
    }
  }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendOutput(g.toOutput)
    sto.appendMath(" + ")     // TODO Other compound types
    sto.appendOutput(h.toOutput)
    sto
  }

}

object CompoundImpartialGame {

  def apply(compoundType: CompoundType.Value, g: ImpartialGame, h: ImpartialGame) = {
    new CompoundImpartialGame(compoundType, g, h)
  }

}

class CompoundImpartialGame(compoundType: CompoundType.Value, g: ImpartialGame, h: ImpartialGame)
  extends CompoundGame(compoundType, g, h) with ImpartialGame {

  import CompoundType._

  override def unary_- : CompoundImpartialGame = this

  override def options(player: Player): Iterable[ImpartialGame] = {

    compoundType match {

      case Disjunctive =>
        g.options(player).map { CompoundImpartialGame(Disjunctive, _, h) } ++
        h.options(player).map { CompoundImpartialGame(Disjunctive, g, _) }

      case Ordinal =>
        g.options(player) ++ h.options(player).map { CompoundImpartialGame(Ordinal, g, _) }

    }

  }

  override def nimValue(tt: TranspositionTable): Integer = g.nimValue(tt) ^ h.nimValue(tt)

}

object CompoundType extends Enumeration {
  type CompoundType = Value
  val Disjunctive, Ordinal = Value
}

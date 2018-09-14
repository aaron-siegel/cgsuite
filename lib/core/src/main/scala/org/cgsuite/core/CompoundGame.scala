package org.cgsuite.core

import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.TranspositionCache

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

  override def canonicalForm(tc: TranspositionCache) = {
    compoundType match {
      case Disjunctive => g.canonicalForm(tc) + h.canonicalForm(tc)
      case _ => super.canonicalForm(tc)
    }
  }

  override def gameValue(tc: TranspositionCache) = {
    compoundType match {
      case Disjunctive => g.gameValue(tc) + h.gameValue(tc)
      case _ => super.gameValue(tc)
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

  override def options: Iterable[ImpartialGame] = {

    compoundType match {

      case Disjunctive =>
        g.options.map { CompoundImpartialGame(Disjunctive, _, h) } ++
        h.options.map { CompoundImpartialGame(Disjunctive, g, _) }

      case Ordinal =>
        g.options ++ h.options.map { CompoundImpartialGame(Ordinal, g, _) }

    }

  }

  override def nimValue(tc: TranspositionCache): Integer = {
    compoundType match {
      case Disjunctive => g.nimValue(tc) ^ h.nimValue(tc)
      case _ => super.nimValue(tc)
    }
  }

  override def misereCanonicalForm(tc: TranspositionCache): MisereCanonicalGame = {
    compoundType match {
      case Disjunctive => g.misereCanonicalForm(tc) + h.misereCanonicalForm(tc)
      case _ => super.misereCanonicalForm(tc)
    }
  }

}

object CompoundType extends Enumeration {
  type CompoundType = Value
  val Disjunctive, Ordinal = Value
}

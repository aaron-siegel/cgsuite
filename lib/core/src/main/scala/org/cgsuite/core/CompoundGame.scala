package org.cgsuite.core

import org.cgsuite.output.StyledTextOutput


case class CompoundGame(compoundType: CompoundType.Value, g: Game, h: Game) extends Game {

  import CompoundType._

  override def unary_- = CompoundGame(compoundType, -g, -h)

  def options(player: Player) = compoundType match {

    case Disjunctive =>
      g.options(player).map { CompoundGame(Disjunctive, _, h) } ++
      h.options(player).map { CompoundGame(Disjunctive, g, _) }

    case Ordinal =>
      g.options(player) ++ h.options(player).map { CompoundGame(Ordinal, g, _) }

  }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendOutput(g.toOutput)
    sto.appendMath(" + ")     // TODO Other compound types
    sto.appendOutput(h.toOutput)
    sto
  }

}

object CompoundType extends Enumeration {
  type CompoundType = Value
  val Disjunctive, Ordinal = Value
}

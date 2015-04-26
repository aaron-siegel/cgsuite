package org.cgsuite.core


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

}

object CompoundType extends Enumeration {
  type CompoundType = Value
  val Disjunctive, Ordinal = Value
}

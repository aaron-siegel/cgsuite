package org.cgsuite.core


object ExplicitGame {

  def apply(lo: Game*)(ro: Game*): ExplicitGame = apply(Iterable(lo : _*), Iterable(ro : _*))

}

case class ExplicitGame(lo: Iterable[Game], ro: Iterable[Game]) extends Game {

  override def unary_- = ExplicitGame(ro map { -_ }, lo map { -_ })
  override def options(player: Player) = player match {
    case Left => lo
    case Right => ro
  }

  override def toString = "'{" + (lo mkString ",") + "|" + (ro mkString ",") + "}'"

}

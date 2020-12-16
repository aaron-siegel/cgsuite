package org.cgsuite.core

import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.output.StyledTextOutput
import org.cgsuite.util.TranspositionCache

case class CompoundGame(compoundType: CompoundType, g: Game, h: Game) extends Game {

  override def unary_- : Game = {
    compoundType match {
      case DisjunctiveSum | OrdinalSum => CompoundGame(compoundType, -g, -h)
      case ConwayProduct | OrdinalProduct => super.unary_-
    }
  }

  def options(player: Player) = compoundType match {

    case DisjunctiveSum =>
      g.options(player).map { CompoundGame(DisjunctiveSum, _, h) } ++
      h.options(player).map { CompoundGame(DisjunctiveSum, g, _) }

    case OrdinalSum =>
      g.options(player) ++ h.options(player).map { CompoundGame(OrdinalSum, g, _) }

    case OrdinalProduct =>
      for {
        p <- Seq(Left, Right)
        hOpt <- h.options(p)
        gOpt <- (if (p == Left) g else -g).options(player)
      } yield {
        CompoundGame(OrdinalProduct, g, hOpt) ordinalSum gOpt
      }

    case ConwayProduct =>
      for {
        p <- Seq(Left, Right)
        gOpt <- g.options(p)
        hOpt <- h.options(if (player == Left) p else p.opponent)
      } yield {
        CompoundGame(ConwayProduct, gOpt, h) + CompoundGame(ConwayProduct, g, hOpt) - CompoundGame(ConwayProduct, gOpt, hOpt)
      }

  }

  override def depthHint: Integer = g.depthHint + h.depthHint

  override def decomposition: Iterable[_] = {
    compoundType match {
      case DisjunctiveSum => g.decomposition ++ h.decomposition
      case _ => super.decomposition
    }
  }

  override def toOutput = {
    val sto = new StyledTextOutput
    appendSubOutput(sto, g)
    sto.appendMath(s" ${compoundType.symbol(g, h)} ")
    appendSubOutput(sto, h)
    sto
  }

  private def appendSubOutput(sto: StyledTextOutput, x: Game): Unit = {
    x match {
      case CompoundGame(thatType, _, _) =>
        if (thatType.precedence > compoundType.precedence)
          sto.appendMath("(")
        sto.append(x.toOutput)
        if (thatType.precedence > compoundType.precedence)
          sto.appendMath(")")
      case _ =>
        sto.append(x.toOutput)
    }
  }

}

object CompoundImpartialGame {

  def apply(compoundType: CompoundType, g: ImpartialGame, h: ImpartialGame) = {
    new CompoundImpartialGame(compoundType, g, h)
  }

}

class CompoundImpartialGame(compoundType: CompoundType, g: ImpartialGame, h: ImpartialGame)
  extends CompoundGame(compoundType, g, h) with ImpartialGame {

  override def unary_- : CompoundImpartialGame = this

  override def options: Iterable[ImpartialGame] = {

    compoundType match {

      case DisjunctiveSum =>
        g.options.map { CompoundImpartialGame(DisjunctiveSum, _, h) } ++
        h.options.map { CompoundImpartialGame(DisjunctiveSum, g, _) }

      case OrdinalSum =>
        g.options ++ h.options.map { CompoundImpartialGame(OrdinalSum, g, _) }

      case OrdinalProduct =>
        for {
          gOpt <- g.options
          hOpt <- h.options
        } yield {
          CompoundImpartialGame(OrdinalProduct, g, hOpt) ordinalSum gOpt
        }

      case ConwayProduct =>
        for {
          gOpt <- g.options
          hOpt <- h.options
        } yield {
          CompoundImpartialGame(ConwayProduct, gOpt, h) + CompoundImpartialGame(ConwayProduct, g, hOpt) + CompoundImpartialGame(ConwayProduct, gOpt, hOpt)
        }

    }

  }

  override def nimValue(tc: TranspositionCache): Integer = {
    compoundType match {
      case DisjunctiveSum => g.nimValue(tc) ^ h.nimValue(tc)
      //case ConwayProduct => g.nimValue(tc) nimProduct h.nimValue(tc)
      case _ => super.nimValue(tc)
    }
  }

  override def misereCanonicalForm(tc: TranspositionCache): MisereCanonicalGame = {
    compoundType match {
      case DisjunctiveSum => g.misereCanonicalForm(tc) + h.misereCanonicalForm(tc)
      case _ => super.misereCanonicalForm(tc)
    }
  }

}

sealed trait CompoundType {
  def precedence: Int
  def symbol(g: Game, h: Game): String
}

case object DisjunctiveSum extends CompoundType {
  val precedence = 3
  def symbol(g: Game, h: Game) = "+"
}

case object OrdinalSum extends CompoundType {
  val precedence = 2
  def symbol(g: Game, h: Game) = ":"
}

case object OrdinalProduct extends CompoundType {
  val precedence = 4
  def symbol(g: Game, h: Game) = "OrdinalProduct"
}

case object ConwayProduct extends CompoundType {
  val precedence = 1
  def symbol(g: Game, h: Game) = {
    g match {
      case _: Integer => "*"
      case _ => "ConwayProduct"
    }
  }
}

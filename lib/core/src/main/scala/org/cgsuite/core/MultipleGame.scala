package org.cgsuite.core

import org.cgsuite.dsl._
import org.cgsuite.output.StyledTextOutput

object MultipleGame {

  def binarySum[T](n: Int, t: T, zero: T)(op: (T, T) => T): T = {

    assert(n >= 0)
    var ctr = n
    var result = zero
    var square = t

    while (ctr != 0) {
      if ((ctr & 1) != 0)
        result = op(result, square)
      ctr >>= 1
      if (ctr != 0)
        square = op(square, square)
    }

    result

  }

}

case class MultipleGame(n: Integer, g: Game) extends Game {

  assert(n > zero)

  override def unary_- = MultipleGame(n, -g)

  def options(player: Player) = {
    g.options(player) map { go => MultipleGame(n - one, g) + go }
  }

  override def toOutput = {
    val sto = new StyledTextOutput
    sto.appendMath(n.toString)
    sto.appendMath(" * (")
    sto.appendOutput(g.toOutput)
    sto.appendMath(")")
    sto
  }

}

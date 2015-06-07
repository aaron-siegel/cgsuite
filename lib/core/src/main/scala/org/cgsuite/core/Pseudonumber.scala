package org.cgsuite.core

import org.cgsuite.core.Values._
import org.cgsuite.exception.InputException

object Pseudonumber {

  def apply(x: DyadicRationalNumber, overSign: Int) = {
    if (overSign == 0)
      x
    else
      OverNumberImpl(x, overSign.signum)
  }

}

trait Pseudonumber extends CanonicalStopper {

  override def options(player: Player): Iterable[Pseudonumber] = sys.error("should get overriden by implementations")

  override def unary_- : Pseudonumber = sys.error("should get overriden by implementations")

  override def isPseudonumber = true

  def min(that: Pseudonumber): Pseudonumber = if (this <= that) this else that

  def max(that: Pseudonumber): Pseudonumber = if (this <= that) that else this

  def abs: Pseudonumber = if (this < zero) -this else this

  def blowup: Pseudonumber

}

case object On extends Pseudonumber {

  def loopyGame = LoopyGame.ON

  override def unary_- : Pseudonumber = Off

  override def options(player: Player): Iterable[Pseudonumber] = {
    player match {
      case Left => Set(On)
      case Right => Set.empty
    }
  }

  def blowup = On

}

case object Off extends Pseudonumber {

  def loopyGame = LoopyGame.OFF

  override def unary_- : Pseudonumber = On

  override def options(player: Player): Iterable[Pseudonumber] = {
    player match {
      case Left => Set.empty
      case Right => Set(Off)
    }
  }

  def blowup = throw InputException("Exponent must be nonnegative.")

}

case class OverNumberImpl private[core] (x: DyadicRationalNumber, overSign: Int) extends Pseudonumber {

  assert(overSign == 1 || overSign == -1)

  def loopyGame = {
    val thisNode = new LoopyGame.Node()
    if (overSign > 0) {
      thisNode.addLeftEdge(x)
      thisNode.addRightEdge(thisNode)
    } else {
      thisNode.addLeftEdge(thisNode)
      thisNode.addRightEdge(x)
    }
    new LoopyGame(thisNode)
  }

  override def options(player: Player): Iterable[Pseudonumber] = {
    player match {
      case Left => if (overSign > 0) Set(x) else Set(this)
      case Right => if (overSign < 0) Set(x) else Set(this)
    }
  }

  override def unary_- : Pseudonumber = OverNumberImpl(-x, -overSign)

  def blowup = {
    if (x.isZero && overSign == 1) {
      off
    } else if (x > zero) {
      OverNumberImpl(x.blowup, overSign)
    } else {
      throw InputException("Exponent must be nonnegative.")
    }
  }

}

package org.cgsuite.core.misere

import org.cgsuite.core.{ImpartialGame, Integer, OutcomeClass, SmallInteger}
import org.cgsuite.core.misere.{MisereCanonicalGameOps => ops}
import org.cgsuite.exception.InputException
import org.cgsuite.output.StyledTextOutput

object MisereCanonicalGame {

  private[cgsuite] def apply(misereGameId: Int): MisereCanonicalGame = {
    if (misereGameId < 0)
      throw new IllegalArgumentException("misereGameId < 0")
    MisereCanonicalGameImpl(misereGameId)
  }

  private[cgsuite] def apply(collection: Iterable[_]): MisereCanonicalGame = {
    collection.size match {
      case 1 => recursiveApply(collection.head)
      case _ => MisereCanonicalGame(collection.toSeq map recursiveApply: _*)
    }
  }

  private[cgsuite] def recursiveApply(value: Any): MisereCanonicalGame = {
    value match {
      case g: MisereCanonicalGame => g
      case n: SmallInteger => nimHeap(n.intValue)
      case collection: Iterable[_] => MisereCanonicalGame(collection.toSeq map recursiveApply: _*)
      case _ => sys.error("not a valid specifier")     // TODO better error message
    }
  }

  def apply(options: MisereCanonicalGame*): MisereCanonicalGame = {
    val optionIds = options map { _.misereGameId }
    MisereCanonicalGame(ops.constructFromOptions(optionIds.toArray, optionIds.length))
  }

  def nimHeap(size: Int): MisereCanonicalGame = {
    MisereCanonicalGame(ops.findNimber(size))
  }

}

trait MisereCanonicalGame extends ImpartialGame {

  def misereGameId: Int

  override def unary_- : MisereCanonicalGame = this

  def +(that: MisereCanonicalGame) = MisereCanonicalGame(ops.add(misereGameId, that.misereGameId))

  def misereMinus(that: MisereCanonicalGame) = {
    ops.subtract(misereGameId, that.misereGameId) match {
      case -1 => throw InputException(s"Misere canonical forms are not subtractable: $this MisereMinus $that")
      case id => MisereCanonicalGame(id)
    }
  }

  def misereOutcomeClass: OutcomeClass = {
    if (ops.isPPosition(misereGameId)) OutcomeClass.P else OutcomeClass.N
  }

  def birthday = Integer(ops.birthday(misereGameId))

  def mate = MisereCanonicalGame(ops.mate(misereGameId))

  def genus = ops.genus(misereGameId)

  def isExtraverted = ops.isExtraverted(misereGameId)

  def isHalfTame = (this + this).isTame

  def isIntroverted = misereGameId < 2

  def isPrime = ops.isPrime(misereGameId)

  def isTame = ops.isTame(misereGameId)

  def isTameable = ops.isTameable(misereGameId)

  def isRestless = ops.isRestless(misereGameId)

  def isRestive = ops.isRestive(misereGameId)

  def distinguisher(that: MisereCanonicalGame) = MisereCanonicalGame(ops.discriminatorPN(misereGameId, that.misereGameId))

  def isLinkedTo(that: MisereCanonicalGame) = ops.isLinked(misereGameId, that.misereGameId)

  def link(that: MisereCanonicalGame) = {
    ops.findLink(misereGameId, that.misereGameId) match {
      case -1 => throw InputException("not linked")     // TODO Improve
      case id => MisereCanonicalGame(id)
    }
  }

  override def options: Iterable[MisereCanonicalGame] = {
    ops.getOptions(misereGameId) map { MisereCanonicalGame(_) }
  }

  override def toOutput = {
    val output = new StyledTextOutput
    output.appendMath("*[")
    output.appendMath(ops.appendMidToStringBuilder(misereGameId, new java.lang.StringBuilder).toString)
    output.appendMath("]")
    output
  }

}

case class MisereCanonicalGameImpl(misereGameId: Int) extends MisereCanonicalGame

package org.cgsuite.core.misere

import org.cgsuite.core.{ImpartialGame, Integer, OutcomeClass, SmallInteger}
import org.cgsuite.core.misere.{MisereCanonicalGameOps => ops}
import org.cgsuite.exception.{InvalidArgumentException, InvalidOperationException}
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
      case _ => throw InvalidArgumentException(
        "Invalid misere game specifier: must be a `List` of `Integer`s or `MisereCanonicalGame`s"
      )
    }
  }

  def apply(options: MisereCanonicalGame*): MisereCanonicalGame = {
    val optionIds = options map { _.misereGameId }
    MisereCanonicalGame(ops.constructFromOptions(optionIds.toArray, optionIds.length))
  }

  def nimHeap(size: Int): MisereCanonicalGame = {
    MisereCanonicalGame(ops.constructFromNimber(size))
  }

  def dayN(birthday: Integer, maxGames: Integer, maxOptions: Integer): Iterable[MisereCanonicalGame] = {
    ops.getEarlyVals(maxGames.intValue, birthday.intValue, maxOptions.intValue, maxOptions.intValue, true)
      .map { MisereCanonicalGame(_) }
  }

  object DeterministicOrdering extends Ordering[MisereCanonicalGame] {

    def compare(g: MisereCanonicalGame, h: MisereCanonicalGame): Int = {
      ops.midComparator.compare(g.misereGameId, h.misereGameId)
    }

  }

}

trait MisereCanonicalGame extends ImpartialGame {

  def misereGameId: Int

  override def unary_- : MisereCanonicalGame = this

  def +(that: MisereCanonicalGame) = MisereCanonicalGame(ops.add(misereGameId, that.misereGameId))

  def -(that: MisereCanonicalGame) = this + that

  def misereMinus(that: MisereCanonicalGame) = {
    ops.subtract(misereGameId, that.misereGameId) match {
      case -1 => throw InvalidOperationException(s"Those misere games are not subtractable: $this, $that")
      case id => MisereCanonicalGame(id)
    }
  }

  def misereOutcomeClass: OutcomeClass = {
    if (ops.isPPosition(misereGameId)) OutcomeClass.P else OutcomeClass.N
  }

  def birthday = Integer(ops.birthday(misereGameId))

  def mate = MisereCanonicalGame(ops.mate(misereGameId))

  override def genus: Genus = ops.genus(misereGameId)

  def isEmpty = ops.numOptions(misereGameId) == 0

  def isEven = ops.isEven(misereGameId)

  def isExtraverted = ops.isExtraverted(misereGameId)

  def isGenerallyRestive = ops.isGenerallyRestive(misereGameId)

  def isGenerallyTame = ops.isGenerallyTame(misereGameId)

  def isHalfTame = ops.isHalfTame(misereGameId)

  def isIntroverted = ops.isIntroverted(misereGameId)

  def isNimHeap = ops.isNimHeap(misereGameId)

  def isNPosition = !isPPosition

  def isPPosition = ops.isPPosition(misereGameId)

  def isPrime = ops.isPrime(misereGameId)

  def isTame = ops.isTame(misereGameId)

  def isTameable = ops.isTameable(misereGameId)

  def isRestless = ops.isRestless(misereGameId)

  def isRestive = ops.isRestive(misereGameId)

  override def misereCanonicalForm = this

  override def misereNimValue = Integer(ops.gMinusValue(misereGameId))

  override def nimValue = Integer(ops.gPlusValue(misereGameId))

  def distinguisher(that: MisereCanonicalGame) = MisereCanonicalGame(ops.discriminatorPN(misereGameId, that.misereGameId))

  def parts = ops.properParts(misereGameId).toIndexedSeq map { MisereCanonicalGame(_) }

  def partitions = {
    ops.partitions(misereGameId, true).toIndexedSeq map {
      _.toIndexedSeq map { MisereCanonicalGame(_) }
    }
  }

  def isLinkedTo(that: MisereCanonicalGame) = ops.isLinked(misereGameId, that.misereGameId)

  def link(that: MisereCanonicalGame) = {
    ops.findLink(misereGameId, that.misereGameId) match {
      case -1 => throw InvalidOperationException(s"Those misere games are not linked: $this, $that")
      case id => MisereCanonicalGame(id)
    }
  }

  override def options: IndexedSeq[MisereCanonicalGame] = {
    ops.getOptions(misereGameId) map { MisereCanonicalGame(_) }
  }

  override def toOutput = {
    val output = new StyledTextOutput
    output.appendSymbol(StyledTextOutput.Symbol.STAR)
    output.appendMath("[")
    ops.appendMidToOutput(misereGameId, output)
    output.appendMath("]")
    output
  }

}

case class MisereCanonicalGameImpl(misereGameId: Int) extends MisereCanonicalGame

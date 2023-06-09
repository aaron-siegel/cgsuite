package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core._
import org.cgsuite.core.impartial.HeapRuleset
import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.exception.EvalException
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

class StandardObject(val cls: CgscriptClass, val objArgs: Array[Any], val enclosingObj: Any = null)
  extends OutputTarget {

  private[lang] var vars: Array[Any] = _
  init()

  def init(): Unit = {
    vars = new Array[Any](cls.classInfo.instanceVarLookup.size)
    JSystem.arraycopy(objArgs, 0, vars, 0, objArgs.length)
    cls.ancestors foreach { ancestor =>
      if (ancestor.initializers.nonEmpty) {
        val domain = new EvaluationDomain(new Array(ancestor.initializerLocalVariableCount), Some(this))
        ancestor.initializers foreach { node => node.body evaluate domain }
      }
    }
  }

  def instantiatedClassName: String = {
    enclosingObj match {
      case null => cls.name
      case stdObj: StandardObject => s"${stdObj.instantiatedClassName}.${cls.nameAsFullyScopedMember}"
      case x => s"${CgscriptClass.of(x).name}.${cls.nameAsFullyScopedMember}"
    }
  }

  val cachedHashCode: Option[Int] = {
    if (cls.isMutable) {
      None
    } else {
      // TODO We can probably improve the hashcode resolution here
      val clsHashCode = cls.hashCode()
      val enclHashCode = if (enclosingObj == null) 0 else enclosingObj.hashCode()
      val varsHashCode = java.util.Arrays.hashCode(vars.asInstanceOf[Array[AnyRef]])
      Some(clsHashCode ^ enclHashCode ^ varsHashCode)
    }
  }

  override def equals(other: Any) = other match {
    case obj: StandardObject =>
      (this eq obj) ||
        cls == obj.cls && enclosingObj == obj.enclosingObj && java.util.Arrays.equals(vars.asInstanceOf[Array[AnyRef]], obj.vars.asInstanceOf[Array[AnyRef]])
    case _ => false
  }

  override def hashCode(): Int = {
    cachedHashCode match {
      case Some(hc) => hc
      case None => cls.hashCode() ^ java.util.Arrays.hashCode(vars.asInstanceOf[Array[AnyRef]])
    }
  }

  def toOutput: Output = {
    cls.classInfo.toOutputMethod.call(this, Array.empty).asInstanceOf[Output]
  }

  def toDefaultOutput: StyledTextOutput = {
    if (cls.isMutable) {
      new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, s"${cls.name}.instance")
    } else {
      val sto = new StyledTextOutput(instantiatedClassName)
      cls.constructor foreach { ctor =>
        sto appendMath "("
        val params = ctor.parameters
        params.indices foreach { idx =>
          params(idx).defaultValue match {
            case None =>
            case Some(_) =>
              sto appendText params(idx).id.name
              sto appendMath " "
              sto appendSymbol StyledTextOutput.Symbol.BIG_RIGHT_ARROW
              sto appendMath " "
          }
          val argOutput = CgscriptClass instanceToOutput objArgs(idx)
          sto append argOutput
          if (idx < params.size - 1)
            sto appendMath ", "
        }
        sto appendMath ")"
      }
      sto
    }
  }

  private[lang] def validateResult[T: Manifest](methodName: String, className: String, obj: Any): T = {
    obj match {
      case t: T => t
      case x =>
        throw EvalException(
          s"The `$methodName` method in class `${cls.qualifiedName}` returned an invalid value of type `${CgscriptClass.of(x).qualifiedName}` (expected a `$className`)."
        )
    }
  }

  private[lang] def validateResultCollection[T: Manifest](methodName: String, className: String, obj: Any): Iterable[T] = {
    val result = validateResult[Iterable[_]](methodName, "cgsuite.lang.Collection", obj)
    result foreach {
      case _: T =>
      case x =>
        throw EvalException(
          s"The `$methodName` returned by class `${cls.qualifiedName}` includes an element of type `${CgscriptClass.of(x).qualifiedName}`, which is not a `$className`."
        )
    }
    result.asInstanceOf[Iterable[T]]
  }

}

class EnumObject(cls: CgscriptClass, val literal: String) extends StandardObject(cls, Array.empty)

class GameObject(cls: CgscriptClass, objArgs: Array[Any], enclosingObj: Any = null)
  extends StandardObject(cls, objArgs, enclosingObj) with Game {

  def options(player: Player): Iterable[Game] = {
    val result = cls.classInfo.optionsMethodWithParameter.call(this, Array(player))
    validateResultCollection[Game]("Options", "game.Game", result)
  }

  override def canonicalForm: CanonicalShortGame = canonicalForm(cls.transpositionCache)

  override def gameValue: SidedValue = gameValue(cls.transpositionCache)

  override def decomposition: Iterable[Game] = {
    val result = cls.classInfo.decompositionMethod.call(this, Array.empty)
    validateResultCollection[Game]("Decomposition", "game.Game", result)
  }

  override def depthHint: Int = {
    val result = cls.classInfo.depthHintMethod.call(this, Array.empty)
    validateResult[Integer]("DepthHint", "game.Integer", result).intValue
  }

  override def gameName: String = cls.qualifiedName

}

class ImpartialGameObject(cls: CgscriptClass, objArgs: Array[Any], enclosingObj: Any = null)
  extends GameObject(cls, objArgs, enclosingObj) with ImpartialGame {

  override def options: Iterable[ImpartialGame] = {
    val result = cls.classInfo.optionsMethod.call(this, Array.empty)
    validateResultCollection[ImpartialGame]("Options", "game.ImpartialGame", result)
  }

  override def decomposition: Iterable[ImpartialGame] = {
    val result = cls.classInfo.decompositionMethod.call(this, Array.empty)
    validateResultCollection[ImpartialGame]("Decomposition", "game.ImpartialGame", result)
  }

  override def misereCanonicalForm: MisereCanonicalGame = misereCanonicalForm(cls.transpositionCache)

  override def nimValue: Integer = nimValue(cls.transpositionCache)

}

class HeapRulesetObject(cls: CgscriptClass, objArgs: Array[Any], enclosingObj: Any = null)
  extends StandardObject(cls, objArgs, enclosingObj) with HeapRuleset {

  override def heapOptions(heapSize: Integer) = {
    val result = cls.classInfo.heapOptionsMethod.call(this, Array(heapSize))
    val collection = validateResultCollection[IndexedSeq[_]]("HeapOptions", "cgsuite.lang.List", result)
    collection foreach {
      validateResultCollection[Integer]("HeapOptions", "game.Integer", _)
    }
    collection.asInstanceOf[Iterable[IndexedSeq[Integer]]]
  }

}

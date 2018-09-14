package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.output.{Output, OutputTarget, StyledTextOutput}

class StandardObject(val cls: CgscriptClass, val objArgs: Array[Any], val enclosingObj: Any = null)
  extends OutputTarget {

  private[lang] var vars: Array[Any] = _
  init()

  def init() {
    vars = new Array[Any](cls.classInfo.classVarLookup.size)
    JSystem.arraycopy(objArgs, 0, vars, 0, objArgs.length)
    cls.ancestors foreach { ancestor =>
      val domain = new Domain(new Array(ancestor.initializerLocalVariableCount), Some(this))
      ancestor.initializers foreach { node => node.body evaluate domain }
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
      new StyledTextOutput(s"${cls.name}.instance")
    } else {
      val sto = new StyledTextOutput
      enclosingObj match {
        case null =>
        case stdObj: StandardObject =>
          sto append stdObj.toDefaultOutput
          sto appendText "."
        case _ =>
          sto appendText CgscriptClass.of(enclosingObj).name
          // TODO Append .instance for nonsingletons?
          sto appendText "."
      }
      sto appendText cls.fullyScopedName
      cls.constructor foreach { ctor =>
        sto appendText "("
        val params = ctor.parameters
        params.indices foreach { idx =>
          params(idx).defaultValue match {
            case None =>
            case Some(_) =>
              sto appendText params(idx).id.name
              sto appendText " => "
          }
          val argOutput = CgscriptClass instanceToOutput objArgs(idx)
          sto append argOutput
          if (idx < params.size - 1)
            sto appendText ", "
        }
        sto appendText ")"
      }
      sto
    }
  }

  def lookupInstanceMethod(id: Symbol): Option[Any] = {
    cls.lookupMethod(id).map { method =>
      if (method.isStatic) sys.error("foo")
      if (method.autoinvoke)
        method.call(this, Array.empty)
      else
        InstanceMethod(this, method)
    }
  }

}

class EnumObject(cls: CgscriptClass, val literal: String) extends StandardObject(cls, Array.empty)

class GameObject(cls: CgscriptClass, objArgs: Array[Any], enclosingObj: Any = null)
  extends StandardObject(cls, objArgs, enclosingObj) with Game {

  def options(player: Player) = {
    cls.classInfo.optionsMethod.call(this, Array(player)).asInstanceOf[Iterable[Game]]
  }

  override def canonicalForm: CanonicalShortGame = canonicalForm(cls.transpositionCache)

  override def gameValue: SidedValue = gameValue(cls.transpositionCache)

  override def decomposition: Iterable[_] = {
    cls.classInfo.decompositionMethod.call(this, Array.empty).asInstanceOf[Iterable[_]]
  }

  override def depthHint: Int = {
    cls.classInfo.depthHintMethod.call(this, Array.empty).asInstanceOf[Integer].intValue
  }

  override def gameName: String = cls.qualifiedName

}

class ImpartialGameObject(cls: CgscriptClass, objArgs: Array[Any], enclosingObj: Any = null)
  extends GameObject(cls, objArgs, enclosingObj) with ImpartialGame {

  override def options = {
    cls.classInfo.optionsMethod.call(this, Array.empty).asInstanceOf[Iterable[ImpartialGame]]
  }

  override def misereCanonicalForm: MisereCanonicalGame = misereCanonicalForm(cls.transpositionCache)

  override def nimValue: Integer = nimValue(cls.transpositionCache)

}

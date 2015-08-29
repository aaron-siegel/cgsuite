package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core._
import org.cgsuite.exception.CgsuiteException
import org.cgsuite.output.{Output, OutputTarget}
import org.cgsuite.util.TranspositionTable

import scala.collection.mutable

class StandardObject(val cls: CgscriptClass, val objArgs: Array[Any], val enclosingObj: Any = null)
  extends OutputTarget {

  private[lang] var vars: Array[Any] = _
  init()

  def init() {
    vars = new Array[Any](cls.classInfo.allClassVars.size)
    JSystem.arraycopy(objArgs, 0, vars, 0, objArgs.length)
    val domain = new Domain(null, Some(this))
    cls.ancestors foreach { ancestor =>
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

  override def canonicalForm: CanonicalShortGame = canonicalForm(cls.transpositionTable)

  override def gameValue: SidedValue = gameValue(cls.transpositionTable)

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

  override def options(player: Player) = {
    cls.classInfo.optionsMethod.call(this, Array(player)).asInstanceOf[Iterable[ImpartialGame]]
  }

  override def nimValue: Integer = nimValue(cls.transpositionTable)

}

case class NotShortGameException(msg: String, cause: Throwable = null) extends CgsuiteException(msg, cause)

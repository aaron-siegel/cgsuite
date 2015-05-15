package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core._
import org.cgsuite.util.TranspositionTable

class StandardObject(val cls: CgsuiteClass, objArgs: Array[Any]) {

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
      Some(cls.hashCode() ^ java.util.Arrays.hashCode(vars.asInstanceOf[Array[AnyRef]]))
    }
  }

  override def equals(other: Any) = other match {
    case obj: StandardObject =>
      (this eq obj) ||
        cls == obj.cls && java.util.Arrays.equals(vars.asInstanceOf[Array[AnyRef]], obj.vars.asInstanceOf[Array[AnyRef]])
    case _ => false
  }

  override def hashCode(): Int = {
    cachedHashCode match {
      case Some(hc) => hc
      case None => cls.hashCode() ^ java.util.Arrays.hashCode(vars.asInstanceOf[Array[AnyRef]])
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

object GameObject {

  private def callCanonicalForm(g: Game, tt: TranspositionTable) = {
    g match {
      case go: GameObject => go.cls.classInfo.canonicalFormMethod.call(go, Array.empty).asInstanceOf[CanonicalShortGame]
      case _ => g.shortCanonicalForm(tt)
    }
  }

}

class GameObject(cls: CgsuiteClass, objArgs: Array[Any]) extends StandardObject(cls, objArgs) with Game {

  def options(player: Player) = {
    cls.classInfo.optionsMethod.call(this, Array(player)).asInstanceOf[Seq[Game]]
  }

  override def canonicalForm: CanonicalShortGame = {
    val decomp = decomposition
    if (decomp.size == 1 && decomp.head == this) {
      canonicalFormR
    } else {
      var result: CanonicalShortGame = Values.zero
      val it = decomp.iterator
      while (it.hasNext) {
        val component = it.next() match {
          case go: GameObject => go.canonicalFormR
          case g => g.shortCanonicalForm(cls.transpositionTable)
        }
        result += component
      }
      result
    }
  }

  private def canonicalFormR: CanonicalShortGame = {
    cls.transpositionTable.get(this) match {
      case Some(x) => x.asInstanceOf[CanonicalShortGame]
      case _ =>
        val lo = options(Left ) map { GameObject.callCanonicalForm(_, cls.transpositionTable) }
        val ro = options(Right) map { GameObject.callCanonicalForm(_, cls.transpositionTable) }
        val canonicalForm = CanonicalShortGame(lo, ro)
        cls.transpositionTable.put(this, canonicalForm)
        canonicalForm
    }
  }

  override def decomposition: Seq[Game] = {
    cls.classInfo.decompositionMethod.call(this, Array.empty).asInstanceOf[Seq[Game]]
  }

}

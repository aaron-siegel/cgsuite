package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.core.{Game, Player}

class StandardObject(val cls: CgsuiteClass, objArgs: Array[Any]) {

  private[lang] var vars: Array[Any] = _
  init()

  def init() {
    vars = new Array[Any](cls.classInfo.allClassVars.size)
    JSystem.arraycopy(objArgs, 0, vars, 0, objArgs.length)
    val domain = new Domain(null, Some(this))
    cls.ancestors.foreach { ancestor =>
      ancestor.initializers.foreach { node => node.body.evaluate(domain) }
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

class GameObject(cls: CgsuiteClass, objArgs: Array[Any]) extends StandardObject(cls, objArgs) with Game {

  def options(player: Player) = {
    cls.classInfo.optionsMethod.get.call(this, Array(player)).asInstanceOf[Seq[Game]]
  }

  override def canonicalForm: Game = canonicalForm(cls.transpositionTable)

  override def decomposition: Seq[Game] = {
    cls.classInfo.decompositionMethod.get.call(this, Array.empty).asInstanceOf[Seq[Game]]
  }

}

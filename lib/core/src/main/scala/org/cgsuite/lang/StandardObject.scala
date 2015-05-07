package org.cgsuite.lang

import org.cgsuite.core.{Player, Game}

class StandardObject(val cls: CgsuiteClass, val objArgs: Map[Symbol, Any]) {

  val namespace: Namespace = new Namespace()
  namespace.initialize(None, objArgs)
  init()

  val cachedHashCode: Option[Int] = {
    if (cls.isMutable) {
      None
    } else {
      Some(cls.hashCode() ^ namespace.fullMap.hashCode())
    }
  }

  override def equals(other: Any) = other match {
    case obj: StandardObject => cls == obj.cls && namespace.fullMap == obj.namespace.fullMap
    case _ => false
  }

  override def hashCode(): Int = {
    cachedHashCode match {
      case Some(hc) => hc
      case None => cls.hashCode() ^ (objArgs ++ namespace.additions).hashCode()
    }
  }

  def init() {
    val domain = new Domain(null, Some(this))
    cls.ancestors.foreach { ancestor =>
      ancestor.initializers.foreach { node => node.body.evaluate(domain) }
    }
  }

  def lookup(id: Symbol): Option[Any] = {
    lookupInstanceMethod(id) orElse namespace.lookup(id)
  }

  def lookupInstanceMethod(id: Symbol): Option[Any] = {
    cls.lookupMethod(id).map { method =>
      if (method.isStatic) sys.error("foo")
      if (method.autoinvoke)
        method.call(this, Seq.empty, Map.empty)
      else
        InstanceMethod(this, method)
    }
  }

}

class GameObject(cls: CgsuiteClass, objArgs: Map[Symbol, Any]) extends StandardObject(cls, objArgs) with Game {

  def options(player: Player) = {
    val method = lookupInstanceMethod(Symbol("Options")).get.asInstanceOf[InstanceMethod]
    method.call(Seq(player), Map.empty).asInstanceOf[Seq[Game]]   // TODO Validation?
  }

  override def canonicalForm: Game = canonicalForm(cls.transpositionTable)

}

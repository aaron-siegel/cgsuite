package org.cgsuite.lang

import scala.collection.mutable


class StandardObject(val cls: CgsuiteClass, val objArgs: Map[Symbol, Any]) {

  private var namespace: mutable.Map[Symbol, Any] = _

  def lookup(id: Symbol): Option[Any] = {
    objArgs.get(id).orElse(lookupInstanceMethod(id)).orElse(lookupInNamespace(id))
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

  def putIntoNamespace(id: Symbol, obj: Any) {
    if (namespace == null) {
      namespace = mutable.Map[Symbol, Any]()
    }
    namespace.put(id, obj)
  }

  def lookupInNamespace(id: Symbol): Option[Any] = {
    if (namespace == null) None else namespace.get(id)
  }

}

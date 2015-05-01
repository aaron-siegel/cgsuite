package org.cgsuite.lang

import scala.collection.mutable


class StandardObject(val cls: CgsuiteClass, val objArgs: Map[String, Any]) {

  private var namespace: mutable.Map[String, Any] = _

  def lookup(id: String): Option[Any] = {
    objArgs.get(id).orElse(lookupInstanceMethod(id)).orElse(lookupInNamespace(id))
  }

  def lookupInstanceMethod(id: String): Option[Any] = {
    cls.lookupMethod(id).map { method =>
      if (method.isStatic) sys.error("foo")
      if (method.autoinvoke)
        method.call(this, Seq.empty, Map.empty)
      else
        InstanceMethod(this, method)
    }
  }

  def putIntoNamespace(id: String, obj: Any) {
    if (namespace == null) {
      namespace = mutable.Map[String, Any]()
    }
    namespace.put(id, obj)
  }

  def lookupInNamespace(id: String): Option[Any] = {
    if (namespace == null) None else namespace.get(id)
  }

}

package org.cgsuite.lang

import org.cgsuite.exception.InputException


class ClassObject(val forClass: CgsuiteClass, objArgs: Map[String, Any])
  extends StandardObject(CgsuiteClass.Class, objArgs) with CallSite {

  override def lookupInstanceMethod(id: String): Option[Any] = {
    forClass.lookupMethod(id).map { method =>
      if (method.autoinvoke)
        method.call(this, Seq.empty, Map.empty)
      else
        InstanceMethod(this, method)
    }.orElse(super.lookupInstanceMethod(id))
  }

  def call(args: Seq[Any], namedArgs: Map[String,Any]) = {
    forClass.constructor match {
      case Some(ctor) => ctor.call(this, args, namedArgs)
      case None => throw InputException(s"The class ${forClass.name} cannot be directly instantiated.")
    }
  }

}

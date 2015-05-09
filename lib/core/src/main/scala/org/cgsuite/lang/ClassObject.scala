package org.cgsuite.lang

class ClassObject(val forClass: CgsuiteClass)
  extends StandardObject(CgsuiteClass.Class, Array.empty) {

  def name() = forClass.id.name

  override def init(): Unit = {
    vars = new Array[Any](forClass.classInfo.staticVars.size)
  }

  override def lookupInstanceMethod(id: Symbol): Option[Any] = {
    forClass.lookupMethod(id).map { method =>
      if (method.autoinvoke)
        method.call(this, Array.empty)
      else
        InstanceMethod(this, method)
    }.orElse(super.lookupInstanceMethod(id))
  }

}

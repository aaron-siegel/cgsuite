package org.cgsuite.lang

case class InstanceMethod(obj: Any, method: CgscriptClass#Method) extends CallSite {

  def parameters = method.parameters
  def ordinal = method.ordinal
  def call(args: Array[Any]): Any = {
    method.call(obj, args)
  }
  def locationMessage = s"in call to `${method.qualifiedName}`"

}

case class InstanceClass(enclosingObject: Any, cls: CgscriptClass) extends CallSite {

  val ctor = cls.constructor.get.asInstanceOf[cls.UserConstructor]
  def parameters = ctor.parameters
  def ordinal = ctor.ordinal
  def call(args: Array[Any]): Any = ctor.call(args, enclosingObject)
  def locationMessage = s"in call to `${cls.qualifiedName}` constructor"
  def nestedClass = cls.classObject

}

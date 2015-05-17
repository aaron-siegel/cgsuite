package org.cgsuite.lang

case class InstanceMethod(obj: Any, method: CgscriptClass#Method) extends CallSite {

  def parameters = method.parameters
  def ordinal = method.ordinal
  def call(args: Array[Any]): Any = {
    method.call(obj, args)
  }

}

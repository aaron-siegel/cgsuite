package org.cgsuite.lang

case class InstanceMethod(obj: Any, clsMethod: CgsuiteClass#Method) extends CallSite {

  def call(args: Seq[Any], namedArgs: Map[String, Any]): Any = {
    clsMethod.call(obj, args, namedArgs)
  }

}

package org.cgsuite.lang

import org.cgsuite.exception.EvalException

case class InstanceMethodGroup(enclosingObject: Any, methodGroup: CgscriptClass#MethodGroup)

case class InstanceClass(enclosingObject: Any, cls: CgscriptClass) extends CallSite {

  lazy val ctor = cls.constructor map { _.asInstanceOf[CgscriptClass#UserConstructor] } getOrElse {
    throw EvalException(
      s"The class `${cls.qualifiedName}` has no constructor and cannot be directly instantiated.",
      token = cls.declNode map { _.token }
    )
  }
  override def ordinal = ctor.ordinal
  override def parameters = ctor.parameters
  override def allowMutableArguments = ctor.allowMutableArguments
  override def call(args: Array[Any]): Any = ctor.call(args, enclosingObject)
  override def referenceToken = Some(cls.classInfo.idNode.token)
  override def locationMessage = s"in call to `${cls.qualifiedName}` constructor"

}

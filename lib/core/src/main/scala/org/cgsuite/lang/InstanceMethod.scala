package org.cgsuite.lang

import org.cgsuite.exception.EvalException
import org.cgsuite.output.{OutputTarget, StyledTextOutput}
/*
case class InstanceMethod(enclosingObject: Any, method: CgscriptClass#Method) extends CallSite {

  def parameters = method.parameters
  def call(args: Array[Any]): Any = {
    method.call(enclosingObject, args)
  }
  def referenceToken = Some(method.idNode.token)
  def locationMessage = s"in call to `${method.qualifiedName}`"

  override def toOutput: StyledTextOutput = {
    val sto = new StyledTextOutput
    sto appendMath s"\u27ea${CgscriptClass.instanceToDefaultOutput(enclosingObject)}.${method.methodName}\u27eb"
    sto
  }

}
*/
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

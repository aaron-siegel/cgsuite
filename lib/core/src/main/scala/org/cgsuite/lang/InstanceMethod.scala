package org.cgsuite.lang

import org.cgsuite.output.{OutputTarget, StyledTextOutput}

case class InstanceMethod(enclosingObject: Any, method: CgscriptClass#Method) extends OutputTarget with CallSite {

  def parameters = method.parameters
  def ordinal = method.ordinal
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

case class InstanceClass(enclosingObject: Any, cls: CgscriptClass) extends CallSite {

  val ctor = cls.constructor.get.asInstanceOf[cls.UserConstructor]
  def parameters = ctor.parameters
  def ordinal = ctor.ordinal
  def call(args: Array[Any]): Any = ctor.call(args, enclosingObject)
  def referenceToken = Some(cls.classInfo.idNode.token)
  def locationMessage = s"in call to `${cls.qualifiedName}` constructor"
  def nestedClass = cls.classObject

}

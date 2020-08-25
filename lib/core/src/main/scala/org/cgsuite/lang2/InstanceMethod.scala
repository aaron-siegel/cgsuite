package org.cgsuite.lang2

import org.cgsuite.exception.EvalException
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

  lazy val ctor = cls.constructor map { _.asInstanceOf[CgscriptClass#UserConstructor] } getOrElse {
    throw EvalException(s"The class `${cls.qualifiedName}` has no constructor and cannot be directly instantiated.")
  }
  def parameters = ctor.parameters
  def ordinal = ctor.ordinal
  def call(args: Array[Any]): Any = ctor.call(args, enclosingObject)
  def referenceToken = Some(cls.classInfo.idNode.token)
  def locationMessage = s"in call to `${cls.qualifiedName}` constructor"
  def nestedClass = cls.classObject

}

package org.cgsuite.lang

case class InstanceMethod(obj: Any, method: CgscriptClass#Method) extends CallSite {

  def parameters = method.parameters
  def ordinal = method.ordinal
  def call(args: Array[Any]): Any = {
    method.call(obj, args)
  }

}

// TODO This shouldn't have to be a StandardObject - it breaks e.g. TakeAndBreak.Eval
case class InstanceClass(obj: Any, cls: CgscriptClass) extends CallSite {

  def parameters = cls.constructor.get.parameters
  def ordinal = cls.constructor.get.ordinal
  def call(args: Array[Any]): Any = {
    // TODO There's some duplicated logic here w/ UserConstructor
    if (cls.ancestors.contains(CgscriptClass.ImpartialGame))
      new ImpartialGameObject(cls, args, obj)
    else if (cls.ancestors.contains(CgscriptClass.Game))
      new GameObject(cls, args, obj)
    else
      new StandardObject(cls, args, obj)
  }

}

package org.cgsuite.lang

import scala.collection.mutable

object Resolver {

  private val resolvers = mutable.AnyRefMap[Symbol, Resolver]()

  def forId(id: Symbol): Resolver = resolvers.getOrElseUpdate(id, Resolver(id))

  def clear(): Unit = resolvers.clear()

}

case class Resolver(id: Symbol) {

  var resolutions: Array[Resolution] = new Array(0)
  var staticResolutions: Array[Resolution] = new Array(0)

  def findResolution(x: Any): Resolution = {

    // No way to avoid this lookup
    val cls = CgscriptClass.of(x)
    var res: Resolution = null
    if (cls == CgscriptClass.Class) {
      // Special handling for class objects
      val co = x.asInstanceOf[ClassObject]
      if (staticResolutions.length <= co.forClass.classOrdinal) {
        staticResolutions = staticResolutions ++ new Array[Resolution](co.forClass.classOrdinal + 1 - staticResolutions.length)
      }
      var res = staticResolutions(co.forClass.classOrdinal)
      if (res == null) {
        res = Resolution(co.forClass, id, static = true)
        staticResolutions(co.forClass.classOrdinal) = res
      }
      res
    } else {
      if (resolutions.length <= cls.classOrdinal) {
        // Grow the array. This can happen at most O(#classes) times.
        resolutions = resolutions ++ new Array[Resolution](cls.classOrdinal + 1 - resolutions.length)
      }
      var res = resolutions(cls.classOrdinal)
      if (res == null) {
        res = Resolution(cls, id)
        resolutions(cls.classOrdinal) = res
      }
      res
    }

  }

  def resolve(x: Any): Any = {

    val res = findResolution(x)
    if (res.classScopeIndex >= 0) {
      val y = x.asInstanceOf[StandardObject].vars(res.classScopeIndex)
      if (y == null) Nil else y
    } else if (res.method.isDefined) {
      if (res.method.get.autoinvoke)
        res.method.get.call(x, Array.empty)
      else
        InstanceMethod(x, res.method.get)
    } else {
      null
    }

  }

}

case class Resolution(cls: CgscriptClass, id: Symbol, static: Boolean = false) {

  val classScopeIndex = {
    if (static) {
      cls.classInfo.staticVarOrdinals.getOrElse(id, -1)
    } else {
      cls.classInfo.classVarOrdinals.getOrElse(id, -1)
    }
  }
  val method = {
    if (static) {
      // This is a little more complicated since we also need to look up common
      // static methods, i.e., methods of class Class.
      CgscriptClass.Class.lookupMethod(id) match {
        case Some(x) => Some(x)
        case None => cls.lookupMethod(id)
      }
    } else {
      // In the instance case we can just do a straight lookup.
      cls.lookupMethod(id)
    }
  }
  assert(classScopeIndex == -1 || method.isEmpty)

}

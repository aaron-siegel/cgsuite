package org.cgsuite.lang

import scala.collection.mutable

class Domain(
  val localScope: Array[Any],
  val contextObject: Option[Any] = None,
  val dynamicVarMap: Option[mutable.AnyRefMap[Symbol, Any]] = None,
  val enclosingDomain: Option[Domain] = None
  ) {

  def isOuterDomain = contextObject.isEmpty

  def putDynamicVar(id: Symbol, value: Any): Unit = {
    assert(dynamicVarMap.isDefined)
    dynamicVarMap.get.put(id, value)
  }

  def getDynamicVar(id: Symbol): Option[Any] = {
    assert(dynamicVarMap.isDefined)
    dynamicVarMap.get.get(id)
  }

  def backref(n: Int): Domain = {
    n match {
      case 0 => this
      case 1 => enclosingDomain.get
      case 2 => enclosingDomain.get.enclosingDomain.get
      case _ => enclosingDomain.get.enclosingDomain.get.enclosingDomain.get.backref(n - 3)
    }
  }

}

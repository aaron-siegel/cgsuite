package org.cgsuite.lang

import org.cgsuite.exception.InputException

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

object ElaborationDomain {

  def apply(pkg: Option[CgscriptPackage], classVars: Seq[Set[Symbol]], enclosingDomain: Option[ElaborationDomain]) = {
    new ElaborationDomain(pkg, classVars, enclosingDomain, mutable.AnyRefMap(), mutable.Stack(mutable.HashSet()))
  }

}

class ElaborationDomain private (
  val pkg: Option[CgscriptPackage],    // None = "external" (Worksheet/REPL) scope
  val classVars: Seq[Set[Symbol]],
  val enclosingDomain: Option[ElaborationDomain],
  varMap: mutable.AnyRefMap[Symbol, Int],
  scopeStack: mutable.Stack[mutable.HashSet[Symbol]]
  )
{

  def pushScope(): Unit = {
    scopeStack.push(mutable.HashSet())
  }

  def popScope(): Unit = {
    scopeStack.pop()
  }

  def contains(id: Symbol): Boolean = {
    (scopeStack exists { _ contains id }) ||
      (classVars exists { _ contains id }) ||
      (enclosingDomain exists { _ contains id })
  }

  def localVariableCount = varMap.size

  def insertId(id: Symbol): Int = {
    if (contains(id)) {
      throw InputException(s"Duplicate var: `${id.name}`")
    } else {
      scopeStack.top += id
      varMap getOrElseUpdate (id, varMap.size)
    }
  }

  def lookup(id: Symbol, depth: Int = 0): Option[VariableReference] = {
    if (varMap contains id) {
      Some(LocalVariableReference(depth, varMap(id)))
    } else if (classVars exists { _ contains id }) {
      Some(ClassVariableReference(classVars indexWhere { _ contains id }, Resolver forId id))
    } else {
      enclosingDomain flatMap { _ lookup (id, depth + 1) }
    }
  }

}

sealed trait VariableReference
case class LocalVariableReference(domainHops: Int, index: Int) extends VariableReference
case class ClassVariableReference(nestingHops: Int, resolver: Resolver) extends VariableReference

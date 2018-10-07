package org.cgsuite.lang

import org.cgsuite.exception.EvalException

import scala.collection.mutable

class EvaluationDomain(
  val localScope: Array[Any],
  val contextObject: Option[Any] = None,
  val dynamicVarMap: Option[mutable.AnyRefMap[Symbol, Any]] = None,
  val enclosingDomain: Option[EvaluationDomain] = None
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

  def backref(n: Int): EvaluationDomain = {
    n match {
      case 0 => this
      case 1 => enclosingDomain.get
      case 2 => enclosingDomain.get.enclosingDomain.get
      case _ => enclosingDomain.get.enclosingDomain.get.enclosingDomain.get.backref(n - 3)
    }
  }

  def nestingBackrefContextObject(n: Int): Any = {
    n match {
      case 0 => contextObject.get
      case 1 => contextObject.get.asInstanceOf[StandardObject].enclosingObj
      case 2 => contextObject.get.asInstanceOf[StandardObject].enclosingObj.asInstanceOf[StandardObject].enclosingObj
      case _ => sys.error("not supported yet")
    }
  }

}

object ElaborationDomain {

  def apply(pkg: Option[CgscriptPackage], classVars: Seq[Set[Symbol]], enclosingDomain: Option[ElaborationDomain]) = {
    new ElaborationDomain(pkg, classVars, enclosingDomain, mutable.AnyRefMap(), mutable.Stack(mutable.HashSet()))
  }

  def empty(pkg: Option[CgscriptPackage] = None) = ElaborationDomain(pkg, Seq(), None)

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

  def scopeDepth: Int = scopeStack.size

  def isToplevelWorksheet: Boolean = {
    pkg.isEmpty && enclosingDomain.isEmpty && scopeDepth <= 2   // It's 2 instead of 1 because of the enclosing StatementSequence
  }

  def contains(id: Symbol): Boolean = {
    (scopeStack exists { _ contains id }) ||
      (classVars exists { _ contains id }) ||
      (enclosingDomain exists { _ contains id })
  }

  def localVariableCount = varMap.size

  def insertId(idNode: IdentifierNode): Int = {
    val id = idNode.id
    if (contains(id)) {
      throw EvalException(s"Duplicate var: `${id.name}`", token = Some(idNode.token))
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

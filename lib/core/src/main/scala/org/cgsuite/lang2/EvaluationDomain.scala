package org.cgsuite.lang2

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

object ElaborationDomain2 {

  def apply(cls: Option[CgscriptClass]) = new ElaborationDomain2(cls)

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

class ElaborationDomain2 (
  val cls: Option[CgscriptClass]      // None = "external" (Worksheet/REPL) scope
)
{

  private var scopeStack = List.empty[ElaborationScope]
  private var elaborationCallStack = List.empty[Member]

  pushScope()

  def pushScope(): Unit = {
    scopeStack = new ElaborationScope +: scopeStack
  }

  def popScope(): Unit = {
    scopeStack = scopeStack.tail
  }

  def isToplevelWorksheet: Boolean = {
    cls.isEmpty && scopeStack.size == 1
  }

  def contains(id: Symbol): Boolean = {
    (scopeStack exists { _ contains id }) ||
      (cls exists { _.classInfo.classVarLookup contains id })
  }

  def typeOf(id: Symbol): Option[Option[CgscriptType]] = {
    (scopeStack flatMap { _ lookup id }).headOption orElse
      (cls flatMap { _.classInfo.classVarLookup get id } map { member => Some(member.resultType) })
  }

  def insertId(id: Symbol, typ: CgscriptType): Unit = {
    assert(scopeStack.nonEmpty)
    scopeStack.head.insertId(id, typ)
  }

  def pushMember(member: Member): Unit = {
    if (elaborationCallStack contains member)
      sys.error("circular reference (needs error msg)")
    elaborationCallStack = member +: elaborationCallStack
  }

  def popMember(): Unit = {
    elaborationCallStack = elaborationCallStack.tail
  }

}

private[lang2] class ElaborationScope {

  private val declaredVars = mutable.AnyRefMap[Symbol, Option[CgscriptType]]()

  def contains(id: Symbol) = declaredVars contains id

  def declare(id: Symbol, `type`: Option[CgscriptType] = None): Unit = {
    declaredVars(id) = `type`
  }

  def lookup(id: Symbol): Option[Option[CgscriptType]] = declaredVars get id

  def insertId(id: Symbol, typ: CgscriptType): Unit = {
    declaredVars(id) = Some(typ)
  }

}

sealed trait VariableReference
case class LocalVariableReference(domainHops: Int, index: Int) extends VariableReference
case class ClassVariableReference(nestingHops: Int, resolver: Resolver) extends VariableReference

package org.cgsuite.lang2

import scala.collection.mutable

object ElaborationDomain {

  def apply(cls: CgscriptClass): ElaborationDomain = new ElaborationDomain(Some(cls))

  def apply(cls: Option[CgscriptClass] = None): ElaborationDomain = new ElaborationDomain(cls)

}

class ElaborationDomain(
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
      (cls flatMap { _.classInfo.classVarLookup get id } map { member => Some(member.resultType) }) orElse
      (cls flatMap { _.classInfo.allNestedClassesInScope get id } map { member => Some(CgscriptType(CgscriptClass.Class, Vector(CgscriptType(member)))) })
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

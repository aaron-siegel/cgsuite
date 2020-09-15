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

  pushScope()

  def pushScope(): Unit = {
    scopeStack = new ElaborationScope +: scopeStack
  }

  def popScope(): Unit = {
    scopeStack = scopeStack.tail
  }

  def popScopeToTopLevel(): Unit = {
    scopeStack = scopeStack takeRight 1
  }

  def isToplevelWorksheet: Boolean = {
    cls.isEmpty && scopeStack.size == 1
  }

  def isDefinedInLocalScope(id: Symbol): Boolean = {
    scopeStack.head contains id
  }

  def typeOf(id: Symbol): Option[Option[CgscriptType]] = {
    (scopeStack flatMap { _ lookup id }).headOption
    //orElse
    //  (cls flatMap { _.lookupVar(id) } map { member => Some(member.ensureElaborated()) }) orElse
    //  (cls flatMap { _.lookupStaticVar(id) } map { member => Some(member.ensureElaborated()) }) orElse
    //  (cls flatMap { _.classInfo.allNestedClassesInScope get id } map { member => Some(CgscriptType(CgscriptClass.Class, Vector(CgscriptType(member)))) })
  }

  def insertId(id: Symbol, typ: CgscriptType): Unit = {
    assert(scopeStack.nonEmpty)
    scopeStack.head.insertId(id, typ)
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

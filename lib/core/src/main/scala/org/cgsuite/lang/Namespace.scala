package org.cgsuite.lang

import scala.collection.mutable

object Namespace {

  private val pool = mutable.Queue[Namespace]()

  def checkout(parent: Option[Namespace], args: Map[Symbol, Any]): Namespace = {
    val namespace = {
      if (pool.isEmpty) {
        new Namespace()
      } else {
        pool.dequeue()
      }
    }
    namespace.initialize(parent, args)
    namespace
  }

  def checkin(namespace: Namespace) {
    namespace.clear()
    pool.enqueue(namespace)
  }

}

class Namespace {

  var parent: Option[Namespace] = _
  var args: Map[Symbol, Any] = _
  var additions: mutable.Map[Symbol, Any] = _

  def initialize(parent: Option[Namespace], args: Map[Symbol, Any]) {
    this.parent = parent
    this.args = args
  }

  def clear() {
    this.parent = null
    this.args = null
    if (additions != null)
      additions.clear()
  }

  def contains(symbol: Symbol): Boolean = {
    containsInScope(symbol) || parent.exists { _.containsInScope(symbol) }
  }

  def lookup(symbol: Symbol): Option[Any] = {
    lookupInScope(symbol).orElse { parent flatMap { _.lookup(symbol) } }
  }

  def put(symbol: Symbol, x: Any, declare: Boolean) {
    if (declare || true || contains(symbol)) {
      putInScope(symbol, x)
    } else {
      parent match {
        case None => sys.error(s"undeclared variable: ${symbol.name}")
        case Some(namespace) => namespace.put(symbol, x, declare = false)
      }
    }
  }

  def containsInScope(symbol: Symbol): Boolean = {
    args.contains(symbol) || (additions != null && additions.contains(symbol))
  }

  def lookupInScope(symbol: Symbol): Option[Any] = {
    if (additions == null)
      args get symbol
    else
      additions get symbol orElse (args get symbol)
  }

  def putInScope(symbol: Symbol, x: Any) {
    if (additions == null)
      additions = mutable.Map()
    additions.put(symbol, x)
  }

  def fullMap: Map[Symbol, Any] = {
    if (additions == null)
      args
    else
      args ++ additions
  }

}

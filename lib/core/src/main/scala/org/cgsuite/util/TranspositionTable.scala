package org.cgsuite.util

import scala.collection.mutable

class TranspositionTable[T] {

  private val map = mutable.AnyRefMap[AnyRef, T]()

  def put(k: AnyRef, v: T): Unit = {
    map.put(k, v)
  }

  def get(k: AnyRef) = map.get(k)

  def contains(k: AnyRef) = map.contains(k)

  def apply(k: AnyRef) = map(k)

  def clear(): Unit = map.clear()

}

class TranspositionCache {

  private val tables = mutable.AnyRefMap[Symbol, TranspositionTable[_]]()

  def tableFor[T](symbol: Symbol): TranspositionTable[T] = {
    val table = tables.getOrElseUpdate(symbol, new TranspositionTable[T]())
    table.asInstanceOf[TranspositionTable[T]]
  }

  def clear(): Unit = tables.clear()

}

package org.cgsuite.util

import scala.collection.mutable

class TranspositionTable {

  val map = mutable.AnyRefMap[AnyRef, Any]()

  def put(k: AnyRef, v: Any) {
    map.put(k, v)
  }

  def get(k: AnyRef) = map.get(k)

  def contains(k: AnyRef) = map.contains(k)

  def apply(k: AnyRef) = map(k)

  def clear() = map.clear()

}

package org.cgsuite.util

import scala.collection.mutable

class TranspositionTable {

  val map = mutable.Map[Any, Any]()

  def put(k: Any, v: Any) {
    map.put(k, v)
  }

  def get(k: Any) = map.get(k)

  def contains(k: Any) = map.contains(k)

  def clear() = map.clear()

}

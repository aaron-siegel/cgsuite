package org.cgsuite.core

trait GameOrdered[T <: GameOrdered[T]] {

  def <=(t: GameOrdered[T]): Boolean

  def >=(t: GameOrdered[T]): Boolean = t <= this
  def |>(t: GameOrdered[T]): Boolean = !(this <= t)
  def <|(t: GameOrdered[T]): Boolean = !(t <= this)
  def > (t: GameOrdered[T]): Boolean = t <= this && !(this <= t)
  def < (t: GameOrdered[T]): Boolean = this <= t && !(t <= this)
  def <>(t: GameOrdered[T]): Boolean = !(this <= t) && !(t <= this)

}

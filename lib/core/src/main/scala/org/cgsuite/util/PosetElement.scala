package org.cgsuite.util

trait PosetElement[T <: PosetElement[T]] {

  def >=(that: T): Boolean

  def <=(that: T): Boolean = that >= this.asInstanceOf[T]

  def > (that: T): Boolean = this != that && this >= that

  def < (that: T): Boolean = this != that && this <= that

  def |>(that: T): Boolean = !(this <= that)

  def <|(that: T): Boolean = !(this >= that)

  def <>(that: T): Boolean = (this |> that) && (this <| that)

}

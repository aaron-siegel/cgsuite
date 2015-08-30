package org.cgsuite.lang

object CallSite {
  private var nextCallSiteOrdinal = 0
  def newCallSiteOrdinal = {
    val next = nextCallSiteOrdinal
    nextCallSiteOrdinal += 1
    next
  }
}

trait CallSite {

  def parameters: Seq[Parameter]
  def call(args: Array[Any]): Any
  def ordinal: Int
  def locationMessage: String

}

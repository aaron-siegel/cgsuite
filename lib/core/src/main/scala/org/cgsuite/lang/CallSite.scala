package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.exception.EvalException

import scala.collection.mutable

object CallSite {

  private var nextCallSiteOrdinal = 0

  def newCallSiteOrdinal = {
    val next = nextCallSiteOrdinal
    nextCallSiteOrdinal += 1
    next
  }

}

trait CallSite {

  def parameters: Vector[Parameter]

  def call(args: Array[Any]): Any

  def ordinal: Int

  def referenceToken: Option[Token]

  def locationMessage: String

  def allowMutableArguments: Boolean

}

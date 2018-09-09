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

  def validateArguments(
    parameters: Seq[Parameter],
    args: Array[Any],
    knownValidArgs: mutable.LongMap[Unit],
    locationMessage: String,
    ensureImmutable: Boolean = false
    ): Unit = {

    var classcode: Long = 0
    if (parameters.size <= 4 && {
      var i = 0
      while (i < args.length) {
        classcode |= (CgscriptClass of args(i)).classOrdinal
        classcode <<= 16
        i += 1
      }
      knownValidArgs contains classcode
    }) {
      // We're known to be ok
    } else {
      var i = 0
      while (i < args.length) {
        val cls = CgscriptClass of args(i)
        val expectedType = {
          if (i == args.length - 1 && parameters.last.isExpandable) {
            CgscriptClass.List   // TODO Validate elements of the list as well? Ideally we should do this only if explicitly specified
          } else {
            parameters(i).paramType
          }
        }
        if (!(cls.ancestors contains expectedType)) {
          throw EvalException(s"Argument `${parameters(i).id.name}` ($locationMessage) " +
            s"has type `${cls.qualifiedName}`, which does not match expected type `${parameters(i).paramType.qualifiedName}`")
        }
        if (ensureImmutable && cls.isMutable) {
          throw EvalException(s"Cannot assign mutable object to var `${parameters(i).id.name}` of immutable class")
        }
        i += 1
      }
      if (parameters.size <= 4) {
        knownValidArgs put (classcode, ())
      }
    }

  }

}

trait CallSite {

  def parameters: Seq[Parameter]
  def call(args: Array[Any]): Any
  def ordinal: Int
  def referenceToken: Option[Token]
  def locationMessage: String

}

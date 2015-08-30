package org.cgsuite.lang

import org.cgsuite.exception.InputException

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
    locationMessage: String
    ): Unit = {

    assert(args.length == parameters.length)
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
        if (!(cls.ancestors contains parameters(i).paramType)) {
          throw InputException(s"Argument `${parameters(i).id.name}` ($locationMessage) " +
            s"has type `${cls.qualifiedName}`, which does not match expected type `${parameters(i).paramType.qualifiedName}`")
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
  def locationMessage: String

}

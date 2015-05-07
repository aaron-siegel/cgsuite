package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import scala.collection.mutable

class Domain(
  val localScope: Array[Any],
  val contextObject: Option[Any] = None
  ) {

  def lookup(id: Symbol, refToken: Token): Any = {

    val opt = try {
      lookup(id)
    } catch {
      case exc: InputException =>
        exc.addToken(refToken)
        throw exc
    }
    opt match {
      case Some(x) => x
      case None => throw InputException("That variable is not defined: " + id.name, token = Option(refToken))
    }

  }

  def lookup(id: Symbol): Option[Any] = contextObject map { resolve(_, id) }

  def resolve(x: Any, id: Symbol) = {
    val optResult = x match {
      case so: StandardObject => so.lookup(id)
      case _ =>
        CgsuiteClass.of(x).lookupMethod(id).map { method =>
          if (method.autoinvoke)
            method.call(x, Seq.empty, Map.empty)
          else
            InstanceMethod(x, method)
        }
    }
    optResult getOrElse {
      throw InputException(s"Member not found: ${id.name} (in object of type ${CgsuiteClass.of(x)})")
    }
  }

}

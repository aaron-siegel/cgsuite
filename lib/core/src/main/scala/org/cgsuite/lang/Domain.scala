package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import scala.collection.mutable
import org.cgsuite.util.Profiler

class Domain(
  val namespace: Namespace,
  val contextObject: Option[Any] = None,
  val contextMethod: Option[CgsuiteClass#Method] = None
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

  def lookup(id: Symbol): Option[Any] = {
    CgsuitePackage.lookupClass(id).map { _.classObject }
      .orElse(namespace.lookup(id))
      .orElse(contextObject flatMap { resolve(_, id) })   // TODO Use contextMethod
  }

  def resolve(x: Any, id: Symbol): Option[Any] = {

    x match {
      case so: StandardObject => so.lookup(id)
      case _ =>
        CgsuiteClass.of(x).lookupMethod(id).map { method =>
          if (method.autoinvoke)
            method.call(x, Seq.empty, Map.empty)
          else
            InstanceMethod(x, method)
        }
    }

  }

  def assignTo(id: Symbol, x: Any, isVarDeclaration: Boolean, refToken: Token): Any = {

    try {
      // TODO Check for duplicate declaration
      namespace.put(id, x, declare = isVarDeclaration)
      x
    } catch {
      case exc: InputException =>
        exc.addToken(refToken)
        throw exc
    }

  }

  def toStringOutput(x: Any) = x.toString

}

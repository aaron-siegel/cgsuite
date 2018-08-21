package org.cgsuite.exception

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.lang.Node._

import scala.collection.mutable

object InputException {

  def apply(msg: String, e: Throwable = null, token: Option[Token] = None): InputException = {
    val exc = InputException(msg, e)
    exc.tokenStack ++= token
    exc
  }

  def apply(msg: String, tree: Tree): InputException = {
    InputException(msg, token = Some(tree.token))
  }

}

case class InputException private (msg: String, e: Throwable) extends CgsuiteException(msg, e) {

  var invocationTarget: Option[String] = None
  val tokenStack = mutable.MutableList[Token]()

  def addToken(token: Token): Unit = {
    tokenStack += token
  }

  def msgWithLocation: String = {
    tokenStack.headOption match {
      case None => s"[unknown location] $msg"
      case Some(t) => s"[${t.getInputStream.getSourceName} line ${t.getLine}:${t.getCharPositionInLine}] $msg"
    }
  }

}

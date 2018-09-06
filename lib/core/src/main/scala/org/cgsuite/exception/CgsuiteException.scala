package org.cgsuite.exception

import org.antlr.runtime.Token

import scala.collection.mutable

class CgsuiteException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends RuntimeException(msg, cause) {

  var invocationTarget: Option[String] = None
  val tokenStack = mutable.MutableList[Token]()

  tokenStack ++= token

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

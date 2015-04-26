package org.cgsuite.exception

import collection.mutable
import org.antlr.runtime.Token


case class InputException(msg: String, e: Throwable = null, token: Option[Token]) extends Exception(msg, e) {

  var invocationTarget: Option[String] = None
  val tokenStack = mutable.MutableList[Token]()

  tokenStack ++= token

  def addToken(token: Token) {
    tokenStack += token
  }

}

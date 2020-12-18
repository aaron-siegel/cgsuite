package org.cgsuite.exception

import org.antlr.runtime.Token

import scala.collection.mutable

class CgsuiteException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends RuntimeException(msg, cause) {

  var invocationTarget: Option[String] = None
  val stackTrace = mutable.MutableList[StackElement]()

  token foreach addToken

  def addToken(token: Token): Unit = {
    stackTrace += StackElement(token.getInputStream.getSourceName, token.getLine, token.getCharPositionInLine)
  }

  def addStackElement(source: String, line: Int, col: Int): Unit = {
    stackTrace += StackElement(source, line, col)
  }

  def msgWithLocation: String = {
    stackTrace.headOption match {
      case None => s"[unknown location] $msg"
      case Some(element) => s"[${element.source} line ${element.line}:${element.col}] $msg"
    }
  }

}

case class StackElement(source: String, line: Int, col: Int)

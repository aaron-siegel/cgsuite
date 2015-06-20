package org.cgsuite.exception

import org.antlr.runtime.Token
import org.cgsuite.lang.Node.RichTree

import scala.collection.mutable

object InputException {
  def apply(msg: String, tree: RichTree): InputException = {
    InputException(msg, token = Some(tree.token))
  }
}

case class InputException(msg: String, e: Throwable = null, token: Option[Token] = None) extends CgsuiteException(msg, e) {

  var invocationTarget: Option[String] = None
  val tokenStack = mutable.MutableList[Token]()

  tokenStack ++= token

  def addToken(token: Token) {
    tokenStack += token
  }

}

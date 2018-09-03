package org.cgsuite.exception

import org.antlr.runtime.Token

case class ArithmeticException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

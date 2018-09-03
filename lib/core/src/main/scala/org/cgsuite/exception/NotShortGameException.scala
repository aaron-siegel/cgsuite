package org.cgsuite.exception

import org.antlr.runtime.Token

case class NotShortGameException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

package org.cgsuite.exception

import org.antlr.runtime.Token

case class ArithmeticException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class InvalidArgumentException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class NotAtomicException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotShortGameException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

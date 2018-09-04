package org.cgsuite.exception

import org.antlr.runtime.Token

case class ArithmeticException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class CalculationCanceledException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class CounterexampleException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class GridParseException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class InvalidArgumentException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

case class InvalidOperationException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotAtomicException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotShortGameException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotStopperException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class SystemException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

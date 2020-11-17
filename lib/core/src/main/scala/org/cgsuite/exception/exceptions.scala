package org.cgsuite.exception

import org.antlr.runtime.Token

case class ArithmeticException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class CalculationCanceledException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class CounterexampleException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class GridParseException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class InvalidArgumentException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class InvalidOperationException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class MalformedCodeException(codeString: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(s"Malformed code: $codeString", cause, token)

case class NotAtomicException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotImplementedException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotNumberException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotShortGameException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotStopperException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class NotUptimalException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class OutOfBoundsException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

case class SystemException(msg: String, cause: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, cause, token)

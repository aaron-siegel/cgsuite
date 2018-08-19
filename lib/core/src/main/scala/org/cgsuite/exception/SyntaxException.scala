package org.cgsuite.exception

import org.antlr.runtime.RecognitionException

case class SyntaxException(exc: RecognitionException) extends CgsuiteException("Syntax error.", exc)

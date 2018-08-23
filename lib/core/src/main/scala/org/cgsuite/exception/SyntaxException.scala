package org.cgsuite.exception

import org.antlr.runtime.RecognitionException

case class SyntaxException(exc: RecognitionException, source: String)
  extends CgsuiteException("Syntax error.", exc)

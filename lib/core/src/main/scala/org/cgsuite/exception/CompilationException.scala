package org.cgsuite.exception

case class CompilationException(msg: String, input: String, cause: Throwable = null)
  extends CgsuiteException(msg, cause, None)

package org.cgsuite.lang2

class CompileContext {

  private var nextTempId = 0

  def newTempId(): String = {
    val id = s"__tmp_$nextTempId"
    nextTempId += 1
    id
  }

}

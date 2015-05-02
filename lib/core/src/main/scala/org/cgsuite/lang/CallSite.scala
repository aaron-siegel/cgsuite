package org.cgsuite.lang

trait CallSite {

  def call(args: Seq[Any], namedArgs: Map[Symbol, Any]): Any

}

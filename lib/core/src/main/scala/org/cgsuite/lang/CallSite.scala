package org.cgsuite.lang

trait CallSite {

  def call(args: Seq[Any], namedArgs: Map[String, Any]): Any

}

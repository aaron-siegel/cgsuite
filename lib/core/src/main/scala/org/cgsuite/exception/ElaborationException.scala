package org.cgsuite.exception

import org.antlr.runtime.Token
import org.antlr.runtime.tree.Tree
import org.cgsuite.lang.parser.RichTree.treeToRichTree

object ElaborationException {

  def apply(msg: String, tree: Tree): EvalException = {
    EvalException(msg, token = Some(tree.token))
  }

}

case class ElaborationException(msg: String, e: Throwable = null, token: Option[Token] = None)
  extends CgsuiteException(msg, e, token)

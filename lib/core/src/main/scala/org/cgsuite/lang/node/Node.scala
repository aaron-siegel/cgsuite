package org.cgsuite.lang.node

import org.antlr.runtime.tree.{CommonTree, Tree}
import org.cgsuite.lang.parser.CgsuiteParser
import org.cgsuite.lang.parser.RichTree.treeToRichTree

import scala.language.implicitConversions

trait Node {

  def tree: Tree
  def token = tree.asInstanceOf[CommonTree].token
  def ttype = tree.getType

  def children: Iterable[Node]

  lazy val docComment: Option[String] = {
    tree.precedingToken match {
      case Some(tok) if tok.getType == CgsuiteParser.DOC_COMMENT =>
        Some(tok.getText stripPrefix "/**" stripSuffix "*/")
      case _ => None
    }
  }

}

package org.cgsuite.lang

import org.antlr.runtime.tree.{CommonTree, Tree}

import scala.collection.JavaConversions._

object Node {

  implicit def treeToRichTree(tree: Tree): RichTree = new RichTree(tree)

  class RichTree(tree: Tree) {
    lazy val token = tree.asInstanceOf[CommonTree].token
    lazy val children: Seq[Tree] = {
      val jChildren = tree.asInstanceOf[CommonTree].getChildren
      if (jChildren == null)  // Really dumb ANTLR semantics
        Seq.empty
      else
        jChildren.toSeq map { _.asInstanceOf[Tree] }
    }
    def location = {
      if (token.getInputStream == null)
        throw new RuntimeException(tree.toStringTree)
      token.getInputStream.getSourceName + ":" + token.getLine + ":" + token.getCharPositionInLine
    }
  }

}

trait Node {

  def tree: Tree
  def token = tree.asInstanceOf[CommonTree].token
  def ttype = tree.getType

  def children: Iterable[Node]

}
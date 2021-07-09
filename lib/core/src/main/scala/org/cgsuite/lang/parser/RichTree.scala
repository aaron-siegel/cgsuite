package org.cgsuite.lang.parser

import org.antlr.runtime.CommonToken
import org.antlr.runtime.tree.{CommonTree, Tree}

import scala.collection.JavaConverters

object RichTree {

  implicit def treeToRichTree(tree: Tree): RichTree = new RichTree(tree)

}

class RichTree(tree: Tree) {

  lazy val token = tree.asInstanceOf[CommonTree].token

  lazy val children: Vector[Tree] = {
    val jChildren = tree.asInstanceOf[CommonTree].getChildren
    if (jChildren == null) // Really dumb ANTLR semantics
      Vector.empty
    else
      JavaConverters.iterableAsScalaIterable(jChildren).toVector map {
        _.asInstanceOf[Tree]
      }
  }

  lazy val head = children.head

  def location = {
    if (token.getInputStream == null)
      throw new RuntimeException(tree.toStringTree)
    token.getInputStream.getSourceName + " line " + token.getLine + ":" + token.getCharPositionInLine
  }

  lazy val tokenStream = tree.asInstanceOf[CgsuiteTree].getTokenStream

  lazy val precedingToken: Option[CommonToken] = Option(tree.asInstanceOf[CgsuiteTree].getPrecedingNonHiddenToken)

}

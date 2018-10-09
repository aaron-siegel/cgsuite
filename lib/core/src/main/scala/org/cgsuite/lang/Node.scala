package org.cgsuite.lang

import org.antlr.runtime.CommonToken
import org.antlr.runtime.tree.{CommonTree, Tree}
import org.cgsuite.lang.Node._
import org.cgsuite.lang.parser.{CgsuiteParser, CgsuiteTree}

import scala.collection.JavaConversions._
import scala.language.implicitConversions

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

    lazy val head = children.head

    def location = {
      if (token.getInputStream == null)
        throw new RuntimeException(tree.toStringTree)
      token.getInputStream.getSourceName + " line " + token.getLine + ":" + token.getCharPositionInLine
    }

    lazy val tokenStream = tree.asInstanceOf[CgsuiteTree].getTokenStream

    lazy val precedingToken: Option[CommonToken] = Option(tree.asInstanceOf[CgsuiteTree].getPrecedingNonHiddenToken)

  }

}

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

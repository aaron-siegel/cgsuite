package org.cgsuite.util

import org.cgsuite.core.Bigraph

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object ColoredGraph {

  def apply[T](
    leftEdges: IndexedSeq[IndexedSeq[Integer]],
    rightEdges: IndexedSeq[IndexedSeq[Integer]],
    vertexTags: Option[IndexedSeq[T]]): ColoredGraph[T] = {

    val mappedLeftEdges = {
      leftEdges.map { _.map { _.intValue - 1 }.toArray }.toArray
    }
    val mappedRightEdges = {
      rightEdges.map { _.map { _.intValue - 1 }.toArray }.toArray
    }
    ColoredGraph(mappedLeftEdges, mappedRightEdges, vertexTags)

  }

  private def apply[T](
    leftEdges: Array[Array[Int]],
    rightEdges: Array[Array[Int]],
    vertexTags: Option[IndexedSeq[T]]): ColoredGraph[T] = {
    new ColoredGraph(new Bigraph(leftEdges, rightEdges), vertexTags)
  }

  def parse[T >: Null](str: String, tagMap: scala.collection.Map[Char, T] = Map.empty): ColoredGraph[T] = {
    val leftEdges = ArrayBuffer[ArrayBuffer[Int]]()
    val rightEdges = ArrayBuffer[ArrayBuffer[Int]]()
    val vertexNames = mutable.Map[String, Int]()
    val tags = ArrayBuffer[T]()
    var next = -1
    do {
      next = parse(str, next + 1, tagMap, leftEdges, rightEdges, vertexNames, tags, None)
    } while (next < str.length && str.charAt(next) == ';')
    if (next < str.length) {
      sys.error("parse error")
    }
    ColoredGraph(
      leftEdges.toArray map { _.distinct.sorted.toArray },
      rightEdges.toArray map { _.distinct.sorted.toArray },
      Option(tags.toVector)
    )
  }

  def parse[T >: Null](
    str: String,
    index: Int,
    tagMap: scala.collection.Map[Char, T],
    leftEdges: ArrayBuffer[ArrayBuffer[Int]],
    rightEdges: ArrayBuffer[ArrayBuffer[Int]],
    vertexNames: mutable.Map[String, Int],
    tags: ArrayBuffer[T],
    precOpt: Option[Int]
    ): Int = {

    if (index == str.length) {
      return index
    }

    var next: Int = index
    var leftIn: Boolean = false
    var rightIn: Boolean = false
    var leftOut: Boolean = false
    var rightOut: Boolean = false

    if (precOpt.isDefined) {

      if (str.charAt(index) == '(') {
        do {
          next = parse(str, next + 1, tagMap, leftEdges, rightEdges, vertexNames, tags, precOpt)
        } while (next < str.length && str.charAt(next) == ';')
        if (next == str.length || str.charAt(next) != ')') {
          sys.error("parse error")
        }
        return next + 1
      }

      str.charAt(index) match {
        case '-' | 'e' => leftIn = true; rightIn = true; leftOut = true; rightOut = true
        case '>' => leftOut = true; rightOut = true
        case '<' => leftIn = true; rightIn = true
        case 'l' => leftIn = true; leftOut = true
        case 'r' => rightIn = true; rightOut = true
        case ';' | ')' => return next
        case _ => sys.error("parse error")
      }

      next += 1

    }

    var tag: T = null
    var vertexName: String = null

    if (next < str.length) {

      str.charAt(next) match {
        case '.' =>
          next += 1
        case x if tagMap contains x =>
          tag = tagMap(x)
          next += 1
        case _ =>
      }

      if (str.charAt(next) == '[') {
        val last = str.indexWhere({ !_.isLetterOrDigit }, next + 1)
        vertexName = str.substring(next + 1, last)
        if (vertexName.isEmpty || str.charAt(last) != ']') {
          sys.error("parse error")
        }
        next = last + 1
      }

    }

    var vertex = -1

    if (vertexName != null && vertexNames.contains(vertexName)) {
      vertex = vertexNames(vertexName)
      if (tags(vertex) != tag) {
        sys.error("parse error (conflicting tags)")
      }
    } else {
      vertex = leftEdges.length
      leftEdges += ArrayBuffer[Int]()
      rightEdges += ArrayBuffer[Int]()
      tags += tag
      if (vertexName != null) {
        vertexNames(vertexName) = vertex
      }
    }

    precOpt match {

      case Some(prec) =>
        if (leftOut) leftEdges(prec) += vertex
        if (leftIn) leftEdges(vertex) += prec
        if (rightOut) rightEdges(prec) += vertex
        if (rightIn) rightEdges(vertex) += prec
      case None =>

    }

    parse(str, next, tagMap, leftEdges, rightEdges, vertexNames, tags, Some(vertex))

  }

}

class ColoredGraph[T] private (val underlying: Bigraph, val vertexTagsOpt: Option[IndexedSeq[T]]) {



}

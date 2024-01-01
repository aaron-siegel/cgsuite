package org.cgsuite.util

import org.cgsuite.core.{Bigraph, Integer, Left, Player, Right, Values}
import org.cgsuite.exception.EvalException
import org.cgsuite.output.StyledTextOutput

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object EdgeColoredGraph {

  def apply[T >: Null](
    leftEdges: IndexedSeq[IndexedSeq[Integer]],
    rightEdges: IndexedSeq[IndexedSeq[Integer]],
    vertexTagsOpt: Option[IndexedSeq[T]]): EdgeColoredGraph[T] = {

    val mappedLeftEdges = {
      leftEdges.map { _.map { _.intValue - 1 }.toArray }.toArray
    }
    val mappedRightEdges = {
      rightEdges.map { _.map { _.intValue - 1 }.toArray }.toArray
    }
    EdgeColoredGraph(mappedLeftEdges, mappedRightEdges, vertexTagsOpt)

  }

  private[cgsuite] def apply[T >: Null](
    leftEdges: Array[Array[Int]],
    rightEdges: Array[Array[Int]],
    vertexTagsOpt: Option[IndexedSeq[T]]): EdgeColoredGraph[T] = {
    EdgeColoredGraph(new Bigraph(leftEdges, rightEdges), vertexTagsOpt)
  }

  private[cgsuite] def apply[T >: Null](underlying: Bigraph, vertexTagsOpt: Option[IndexedSeq[T]]): EdgeColoredGraph[T] = {
    if (isImpartial(underlying)) {
      new Graph[T](underlying, vertexTagsOpt)
    } else {
      new EdgeColoredGraph[T](underlying, vertexTagsOpt)
    }
  }

  private def isImpartial(bigraph: Bigraph): Boolean = {
    (0 until bigraph.getNumVertices) forall { n =>
      bigraph.getNumLeftEdges(n) == bigraph.getNumRightEdges(n) &&
        (0 until bigraph.getNumLeftEdges(n)).forall { i =>
          bigraph.getLeftEdgeTarget(n, i) == bigraph.getRightEdgeTarget(n, i)
        }
    }
  }

  def parse[T >: Null](str: String, tagMap: scala.collection.Map[String, T] = scala.collection.Map.empty[String, T]): EdgeColoredGraph[T] = {
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
    EdgeColoredGraph(
      leftEdges.toArray map { _.distinct.sorted.toArray },
      rightEdges.toArray map { _.distinct.sorted.toArray },
      Option(tags.toVector)
    )
  }

  def parse[T >: Null](
    str: String,
    index: Int,
    tagMap: scala.collection.Map[String, T],
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

    if (next < str.length) {
      str.charAt(next) match {
        case '.' =>
          next += 1
        case x if tagMap contains x.toString =>
          tag = tagMap(x.toString)
          next += 1
        case _ =>
      }
    }

    var vertexName: String = null

    if (next < str.length && str.charAt(next) == '[') {
      val last = str.indexWhere({ !_.isLetterOrDigit }, next + 1)
      vertexName = str.substring(next + 1, last)
      if (vertexName.isEmpty || str.charAt(last) != ']') {
        sys.error("parse error")
      }
      next = last + 1
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

case class EdgeColoredGraph[T >: Null] private[cgsuite] (
  underlying: Bigraph,
  vertexTagsOpt: Option[IndexedSeq[T]]
) {

  def vertexCount: Int = underlying.getNumVertices

  def totalEdgeCount(player: Player): Int = (0 until vertexCount map {
    i => outedgeCount(Integer(i + 1), player)
  }).sum

  def outedgeCount(vertex: Integer, player: Player) = {
    player match {
      case Left => underlying.getNumLeftEdges(vertex.intValue - 1)
      case Right => underlying.getNumRightEdges(vertex.intValue - 1)
    }
  }

  def outedge(vertex: Integer, player: Player, index: Integer): Int = {
    player match {
      case Left => underlying.getLeftEdgeTarget(vertex.intValue - 1, index.intValue - 1) + 1
      case Right => underlying.getRightEdgeTarget(vertex.intValue - 1, index.intValue - 1) + 1
    }
  }

  def outedges(vertex: Integer, player: Player): IndexedSeq[Integer] = {
    0 until outedgeCount(vertex, player) map { i =>
      Integer(outedge(vertex, player, Integer(i + 1)))
    }
  }

  def vertexTag(vertex: Integer): T = {
    vertexTagsOpt.map { _(vertex.intValue - 1) }.orNull
  }

  def vertexTags: IndexedSeq[T] = {
    vertexTagsOpt getOrElse {
      throw EvalException("That graph has no vertex tags.")
    }
  }

  def deleteVertex(vertex: Integer): EdgeColoredGraph[T] = {
    EdgeColoredGraph(
      underlying.deleteVertex(vertex.intValue - 1),
      vertexTagsOpt map { tags => tags.take(vertex.intValue - 1) ++ tags.drop(vertex.intValue) }
    )
  }

  def deleteVertices(vertices: IndexedSeq[Integer]): EdgeColoredGraph[T] = {
    // This is a bit inefficient, but will do for now
    vertices.sortBy {
      -_
    }.foldLeft(this) { case (graph, vertex) => graph.deleteVertex(vertex) }
  }

  def updateTag(vertex: Integer, tag: T): EdgeColoredGraph[T] = {
    val newTags = vertexTagsOpt getOrElse {
      throw EvalException("That graph has no vertex tags.")
    } updated(vertex.intValue - 1, tag)
    EdgeColoredGraph(underlying, Some(newTags))
  }

  def updateTags(updatesMap: scala.collection.Map[Integer, T]): EdgeColoredGraph[T] = {
    val newTags = ArrayBuffer[T]()
    newTags ++= vertexTagsOpt.getOrElse {
      throw EvalException("That graph has no vertex tags.")
    }
    updatesMap foreach { case (vertex, t) =>
      newTags(vertex.intValue - 1) = t
    }
    EdgeColoredGraph(underlying, Some(newTags.toIndexedSeq))
  }

  // TODO This is not efficient
  def isConnected: Boolean = {
    vertexCount == 0 || connectedComponent(Values.one).vertexCount == vertexCount
  }

  // TODO Preserve vertex tags
  def connectedComponent(vertex: Integer): EdgeColoredGraph[T] = {
    EdgeColoredGraph(underlying.packGraph(vertex.intValue - 1), None)
  }

  override def toString = {
    val leftEdgeCount = totalEdgeCount(Left)
    val rightEdgeCount = totalEdgeCount(Right)
    s"<EdgeColoredGraph with $vertexCount vertices and $leftEdgeCount+$rightEdgeCount edges>"
  }

  def toOutput = new StyledTextOutput(toString)

}

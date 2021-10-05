package org.cgsuite.util

import org.cgsuite.core.Bigraph
import org.cgsuite.core.Integer
import org.cgsuite.exception.{EvalException, InvalidArgumentException}
import org.cgsuite.output.StyledTextOutput

import scala.collection.mutable.ArrayBuffer

object Graph {

  def apply[T](edges: IndexedSeq[IndexedSeq[Integer]], vertexTags: Option[IndexedSeq[T]]): Graph[T] = {
    val mappedEdges = {
      edges.map { _.map { _.intValue - 1 }.toArray }.toArray
    }
    Graph(mappedEdges, vertexTags)
  }

  private def apply[T](edges: Array[Array[Int]], vertexTags: Option[IndexedSeq[T]]): Graph[T] = {
    Graph(new Bigraph(edges, Array.fill(edges.length, 0)(0)), vertexTags)
  }

  def complete[T](vertexCount: Int, vertexTags: Option[IndexedSeq[T]]): Graph[T] = {
    val edges = Array.fill(vertexCount, vertexCount - 1)(0)
    for (i <- 0 until vertexCount; j <- 0 until vertexCount - 1) {
      if (j < i) edges(i)(j) = j
      else edges(i)(j) = j + 1
    }
    Graph(edges, vertexTags)
  }

  def linear[T](vertexCount: Int, directed: Boolean, vertexTags: Option[IndexedSeq[T]]): Graph[T] = {
    val edges = new Array[Array[Int]](vertexCount)
    if (vertexCount > 1) {
      edges(0) = Array[Int](1)
      if (!directed) {
        edges(vertexCount - 1) = Array[Int](vertexCount - 2)
      }
    }
    for (i <- 1 until vertexCount - 1) {
      if (directed) {
        edges(i) = Array[Int](i + 1)
      } else {
        edges(i) = Array[Int](i - 1, i + 1)
      }
    }
    Graph(edges, vertexTags)
  }

  def cycle[T](vertexCount: Int, directed: Boolean, vertexTags: Option[IndexedSeq[T]]): Graph[T] = {
    val edges = new Array[Array[Int]](vertexCount)
    if (vertexCount > 1) {
      for (i <- 0 until vertexCount - 1) {
        if (directed) {
          edges(i) = Array[Int]((i + 1) % vertexCount)
        } else {
          edges(i) = Array[Int]((i + vertexCount - 1) % vertexCount, (i + 1) % vertexCount).sorted
        }
      }
    }
    Graph(edges, vertexTags)
  }

  def parse[T](vertexCount: Integer, str: String, vertexTags: IndexedSeq[T]): Graph[T] = {
    val paths = str split ';'
    val edges = Array.fill(vertexCount.intValue)(new ArrayBuffer[Int]())
    paths foreach { parsePath(edges, _) }
    Graph(edges map { _.distinct.sorted.toArray }, Option(vertexTags))
  }

  private def parsePath(edges: Array[ArrayBuffer[Int]], pathStr: String): Unit = {
    val firstSep = pathStr indexWhere { !_.isDigit }
    var currentVertex = pathStr.substring(0, firstSep).toInt
    var index = firstSep
    while (index < pathStr.length) {
      val direction = pathStr(index) match {
        case '-' => 0
        case '>' => 1
        case '<' => -1
        case _ => throw InvalidArgumentException(s"Invalid graph path specification: $pathStr")
      }
      index += 1
      var nextSep = index + 1
      while (nextSep < pathStr.length && pathStr(nextSep).isDigit) {
        nextSep += 1
      }
      val nextVertex = pathStr.substring(index, nextSep).toInt
      if (direction != -1) {
        edges(currentVertex - 1) += nextVertex - 1
      }
      if (direction != 1) {
        edges(nextVertex - 1) += currentVertex - 1
      }
      currentVertex = nextVertex
      index = nextSep
    }
  }

}

case class Graph[T] private (underlying: Bigraph, vertexTagsOpt: Option[IndexedSeq[T]]) {

  def vertexCount: Int = underlying.getNumVertices

  def totalEdgeCount: Int = (0 until vertexCount map underlying.getNumLeftEdges).sum

  def outedgeCount(vertex: Integer) = underlying.getNumLeftEdges(vertex.intValue - 1)

  def outedge(vertex: Integer, index: Integer): Int = underlying.getLeftEdgeTarget(vertex.intValue - 1, index.intValue - 1) + 1

  def outedges(vertex: Integer): IndexedSeq[Integer] = 0 until outedgeCount(vertex) map { i =>
    Integer(underlying.getLeftEdgeTarget(vertex.intValue - 1, i) + 1)
  }

  def vertexTag(vertex: Integer): T = {
    vertexTagsOpt getOrElse { throw EvalException("That graph has no vertex tags.") } apply (vertex.intValue - 1)
  }

  def vertexTags: IndexedSeq[T] = {
    vertexTagsOpt getOrElse { throw EvalException("That graph has no vertex tags.") }
  }

  def deleteVertex(vertex: Integer): Graph[T] = {
    Graph(underlying.deleteVertex(vertex.intValue - 1), vertexTagsOpt map { tags => tags.take(vertex.intValue - 1) ++ tags.drop(vertex.intValue) })
  }

  def deleteVertices(vertices: IndexedSeq[Integer]): Graph[T] = {
    // This is a bit inefficient, but will do for now
    vertices.sortBy { -_ }.foldLeft(this) { case (graph, vertex) => graph.deleteVertex(vertex) }
  }

  def updateTag(vertex: Integer, tag: T): Graph[T] = {
    val newTags = vertexTagsOpt getOrElse { throw EvalException("That graph has no vertex tags.") } updated (vertex.intValue - 1, tag)
    Graph(underlying, Some(newTags))
  }

  def updateTags(updatesMap: Map[Integer, T]): Graph[T] = {
    val newTags = ArrayBuffer[T]()
    newTags ++= vertexTagsOpt.getOrElse { throw EvalException("That graph has no vertex tags.") }
    updatesMap foreach { case (vertex, t) =>
      newTags(vertex.intValue - 1) = t
    }
    Graph(underlying, Some(newTags.toIndexedSeq))
  }

  override def toString = s"<Graph with $vertexCount vertices and $totalEdgeCount edges>"

  def toOutput = new StyledTextOutput(toString)

}

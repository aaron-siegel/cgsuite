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

  def parse(str: String, tagMap: scala.collection.Map[String, AnyRef] = Map.empty): Graph[AnyRef] = {
    val paths = str split ';'
    val edges = ArrayBuffer[ArrayBuffer[Int]]()
    val tags = ArrayBuffer[AnyRef]()
    paths foreach { parsePath(tagMap, edges, tags, _) }
    Graph(edges.toArray map { _.distinct.sorted.toArray }, Option(tags.toVector))
  }

  private def parsePath(tagMap: scala.collection.Map[String, AnyRef], edges: ArrayBuffer[ArrayBuffer[Int]], tags: ArrayBuffer[AnyRef], pathStr: String): Unit = {
    var currentVertex = -1
    var direction = 0
    var index = 0
    while (index < pathStr.length) {
      var tagIndex = index
      while (tagIndex < pathStr.length && pathStr(tagIndex).isDigit) {
        tagIndex += 1
      }
      val nextVertex = pathStr.substring(index, tagIndex).toInt
      var sepIndex = tagIndex
      while (sepIndex < pathStr.length && pathStr(sepIndex).isLetter) {
        sepIndex += 1
      }
      val tag = pathStr.substring(tagIndex, sepIndex)
      while (edges.length < nextVertex) {
        edges += ArrayBuffer[Int]()
        if (tagMap.nonEmpty) {
          tags += null
        }
      }
      if (tag.nonEmpty) {
        if (tagMap.isEmpty) {
          throw InvalidArgumentException(s"Tag is specified without tagMap: $tag")
        }
        val tagValue = tagMap getOrElse
          (tag, throw InvalidArgumentException(s"Unknown tag in graph specification: $tag"))
        if (tags(nextVertex - 1) != null && tags(nextVertex - 1) != tagValue) {
          throw InvalidArgumentException(s"Conflicting tag for vertex $nextVertex: $tag")
        }
        tags(nextVertex - 1) = tagValue
      }
      if (currentVertex >= 0) {
        if (direction != -1) {
          edges(currentVertex - 1) += nextVertex - 1
        }
        if (direction != 1) {
          edges(nextVertex - 1) += currentVertex - 1
        }
      }
      index = sepIndex
      if (sepIndex < pathStr.length) {
        direction = pathStr(sepIndex) match {
          case '-' => 0
          case '>' => 1
          case '<' => -1
          case _ => throw InvalidArgumentException(s"Invalid graph path specification: $pathStr")
        }
        index += 1
      }
      currentVertex = nextVertex
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

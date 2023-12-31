package org.cgsuite.util

import org.cgsuite.core.{Bigraph, Integer, Left}

object Graph {

  def apply[T >: Null](
    edges: IndexedSeq[IndexedSeq[Integer]],
    vertexTagsOpt: Option[IndexedSeq[T]]): Graph[T] = {
    EdgeColoredGraph(edges, edges, vertexTagsOpt).asInstanceOf[Graph[T]]
  }

  def parse[T >: Null](str: String, tagMap: scala.collection.Map[String, T] = Map.empty): Graph[T] = {
    EdgeColoredGraph.parse(str, tagMap).asInstanceOf[Graph[T]]
  }

}

class Graph[T >: Null] private[cgsuite] (underlying: Bigraph, vertexTagsOpt: Option[IndexedSeq[T]])
  extends EdgeColoredGraph[T](underlying, vertexTagsOpt) {

  def totalEdgeCount: Int = super.totalEdgeCount(Left)

  def outedgeCount(vertex: Integer) = super.outedgeCount(vertex, Left)

  def outedge(vertex: Integer, index: Integer): Int = super.outedge(vertex, Left, index)

  def outedges(vertex: Integer): IndexedSeq[Integer] = super.outedges(vertex, Left)

  override def deleteVertex(vertex: Integer): Graph[T] = {
    super.deleteVertex(vertex).asInstanceOf[Graph[T]]
  }

  override def deleteVertices(vertices: IndexedSeq[Integer]): Graph[T] = {
    super.deleteVertices(vertices).asInstanceOf[Graph[T]]
  }

  override def updateTag(vertex: Integer, tag: T): Graph[T] = {
    super.updateTag(vertex, tag).asInstanceOf[Graph[T]]
  }

  override def updateTags(updatesMap: scala.collection.Map[Integer, T]): Graph[T] = {
    super.updateTags(updatesMap).asInstanceOf[Graph[T]]
  }

  override def toString = s"<Graph with $vertexCount vertices and $totalEdgeCount edges>"

}

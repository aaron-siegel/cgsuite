package org.cgsuite.util

import org.cgsuite.core.Values._
import org.cgsuite.core.{Integer, Values}
import org.cgsuite.output.{OutputTarget, StyledTextOutput}
import org.cgsuite.util.Graph.{Edge, Vertex}

object DirectedGraph {

  def parse[V, E](
    str: String,
    vertexTypes: PartialFunction[String, V],
    edgeTypes: PartialFunction[String, E]
  ): DirectedGraph[V, E] = {
    GraphParser.parse(str, vertexTypes, edgeTypes, allowDirected = true)(DirectedGraph.apply)
  }

  def fromAdjacencyList[V, E](adjacencyList: IndexedSeq[IndexedSeq[Integer]], vTag: V, eTag: E): DirectedGraph[V, E] = DirectedGraph {
    adjacencyList.indices map { vIndex =>
      val v = Integer(vIndex + 1)
      Vertex(vTag, adjacencyList(vIndex) map { toVertex =>
        Edge(v, toVertex, eTag)
      })
    }
  }

  val empty: DirectedGraph[Nothing, Nothing] = DirectedGraph(IndexedSeq.empty)

  def directedPath[V, E](size: Integer, vTag: V, eTag: E): DirectedGraph[V, E] = {
    DirectedGraph {
      one to size map {
        case n if n == size => Vertex(vTag, IndexedSeq.empty)
        case n => Vertex(vTag, IndexedSeq(Edge(n, n + one, eTag)))
      }
    }
  }

  def singleton[V](vTag: V): DirectedGraph[V, Nothing] = {
    DirectedGraph(IndexedSeq(Vertex(vTag, IndexedSeq.empty)))
  }

}

case class DirectedGraph[+V, +E](vertices: IndexedSeq[Vertex[V, E]])
  extends OutputTarget with GraphOps[V, E, DirectedGraph, DirectedGraph[V, E]] {

  override def fromVertices[W >: V, F >: E](vertices: IndexedSeq[Vertex[W, F]]): DirectedGraph[W, F] = {
    DirectedGraph(vertices)
  }

  def edgeCount: Integer = Integer {
    vertices.map { _.edges.size }.sum
  }

  def edges: IndexedSeq[Edge[E]] = {
    vertices flatMap { _.edges }
  }

  def deleteEdge[W >: V, F >: E](edge: Edge[F]): DirectedGraph[W, F] = {
    super.deleteEdge(edge, undirected = false)
  }

  def deleteEdgeByEndpoints[W >: V, F >: E](vFrom: Integer, vTo: Integer, tag: F): DirectedGraph[W, F] = {
    super.deleteEdgeByEndpoints(vFrom, vTo, tag, undirected = false)
  }

  def deleteEdgeByIndex[W >: V, F >: E](vFrom: Integer, eIndex: Integer): DirectedGraph[W, F] = {
    super.deleteEdgeByIndex(vFrom, eIndex, undirected = false)
  }

  override def toOutput: StyledTextOutput = {
    new StyledTextOutput(toString)
  }

  override def toString = {
    s"<DirectedGraph with $vertexCount vertices and $edgeCount directed edges>"
  }

}

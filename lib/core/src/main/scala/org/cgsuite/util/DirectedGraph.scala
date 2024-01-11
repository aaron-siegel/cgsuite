package org.cgsuite.util

import org.cgsuite.core.Integer
import org.cgsuite.core.Values._
import org.cgsuite.output.{OutputTarget, StyledTextOutput}
import org.cgsuite.util.Graph.{Edge, Vertex}

object DirectedGraph {

  def parse[V, E](
    str: String,
    vertexLabels: PartialFunction[String, V],
    edgeLabels: PartialFunction[String, E]
  ): DirectedGraph[V, E] = {
    GraphParser.parse(str, vertexLabels, edgeLabels, allowDirected = true)(DirectedGraph.apply)
  }

  def fromAdjacencyList[V, E](adjacencyList: IndexedSeq[IndexedSeq[Integer]], vLabel: V, eLabel: E): DirectedGraph[V, E] = DirectedGraph {
    adjacencyList.indices map { vIndex =>
      val v = Integer(vIndex + 1)
      Vertex(vLabel, adjacencyList(vIndex) map { toVertex =>
        Edge(v, toVertex, eLabel)
      })
    }
  }

  val empty: DirectedGraph[Nothing, Nothing] = DirectedGraph(IndexedSeq.empty)

  def directedPath[V, E](size: Integer, vLabel: V, eLabel: E): DirectedGraph[V, E] = {
    DirectedGraph {
      one to size map {
        case n if n == size => Vertex(vLabel, IndexedSeq.empty)
        case n => Vertex(vLabel, IndexedSeq(Edge(n, n + one, eLabel)))
      }
    }
  }

  def singleton[V](vLabel: V): DirectedGraph[V, Nothing] = {
    DirectedGraph(IndexedSeq(Vertex(vLabel, IndexedSeq.empty)))
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

  def deleteEdgeByEndpoints[W >: V, F >: E](vFrom: Integer, vTo: Integer, label: F): DirectedGraph[W, F] = {
    super.deleteEdgeByEndpoints(vFrom, vTo, label, undirected = false)
  }

  def deleteEdgeByIndex[W >: V, F >: E](vFrom: Integer, eIndex: Integer): DirectedGraph[W, F] = {
    super.deleteEdgeByIndex(vFrom, eIndex, undirected = false)
  }

  override def toOutput: StyledTextOutput = {
    new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, s"""DirectedGraph("$toString")""")
  }

}

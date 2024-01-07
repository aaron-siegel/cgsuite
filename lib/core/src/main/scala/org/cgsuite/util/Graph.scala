package org.cgsuite.util

import org.cgsuite.core.Integer
import org.cgsuite.core.Values.{one, zero}
import org.cgsuite.output.{OutputTarget, StyledTextOutput}
import org.cgsuite.util.Graph._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Graph {

  def parse[T](
    str: String,
    vertexTypes: PartialFunction[String, T],
    edgeTypes: PartialFunction[String, T]
  ): Graph[T] = {
    GraphParser.parse(str, vertexTypes, edgeTypes, allowDirected = false)(Graph.apply)
  }

  case class Vertex[T](tag: T, edges: IndexedSeq[Edge[T]]) {

    def edgeCount: Integer = Integer(edges.length)

    def edge(n: Integer): Edge[T] = edges(n.intValue - 1)

    def deleteEdgeByIndex(eIndex: Integer): Vertex[T] = Vertex(
      tag,
      one to edgeCount collect { case n if n != eIndex =>
        edge(n)
      }
    )

    def outVertices: IndexedSeq[Integer] = edges map { _.toVertex }

  }

  case class Edge[T](fromVertex: Integer, toVertex: Integer, tag: T)

}

case class Graph[T](vertices: IndexedSeq[Vertex[T]]) extends OutputTarget with GraphOps[T, Graph[T]] {

  override def fromVertices(vertices: IndexedSeq[Vertex[T]]): Graph[T] = {
    Graph(vertices)
  }

  override def edgeCount: Integer = Integer {
    (one to vertexCount).map { v => vertex(v).edges.count { v <= _.toVertex } }.sum
  }

  override def edges: IndexedSeq[Edge[T]] = {
    vertices flatMap { _.edges filter { e => e.fromVertex <= e.toVertex } }
  }

  override def toOutput: StyledTextOutput = {
    new StyledTextOutput(toString)
  }

  override def toString = {
    s"<Graph with $vertexCount vertices and $edgeCount edges>"
  }

}

object GraphParser {

  private[cgsuite] def parse[T, C](
    str: String,
    vertexTypes: PartialFunction[String, T],
    edgeTypes: PartialFunction[String, T],
    allowDirected: Boolean
  )(
    fromVertices: IndexedSeq[Vertex[T]] => C
  ): C = {

    val tokens = tokenize(str, 0)
    val vertexTags = ArrayBuffer[T]()
    val edges = ArrayBuffer[ArrayBuffer[Edge[T]]]()
    val vertexNames = mutable.Map[String, Integer]()
    parse(tokens, vertexTypes, edgeTypes, allowDirected, vertexTags, edges, vertexNames, None)
    fromVertices {
      vertexTags.zip(edges).toIndexedSeq map { case (tag, edges) =>
        Vertex(tag, edges.toIndexedSeq)
      }
    }

  }

  def tokenize(str: String, index: Int): List[String] = {
    if (index >= str.length) {
      Nil
    } else str.charAt(index) match {
      case ' ' => tokenize(str, index + 1)
      case '{' =>
        val end = str.indexOf('}', index + 1)
        if (end == -1)
          sys.error("parse error")
        str.substring(index + 1, end) :: tokenize(str, end + 1)
      case '[' =>
        val end = str.indexOf(']', index + 1)
        if (end == -1)
          sys.error("parse error")
        "[" :: str.substring(index + 1, end) :: tokenize(str, end + 1)
      case ch => ch.toString :: tokenize(str, index + 1)
    }
  }

  private def parse[T](
    tokens: List[String],
    vertexTypes: PartialFunction[String, T],
    edgeTypes: PartialFunction[String, T],
    allowDirected: Boolean,
    vertexTags: ArrayBuffer[T],
    edges: ArrayBuffer[ArrayBuffer[Edge[T]]],
    vertexNames: mutable.Map[String, Integer],
    precOpt: Option[Integer]
    ): List[String] = {

    if (tokens.isEmpty) {
      return Nil
    }

    var inedge: Boolean = true
    var outedge: Boolean = true
    var edgeTag: Option[T] = None

    var stream = tokens

    if (precOpt.isDefined) {

      if (stream.head == "(") {
        do {
          stream = parse(stream.tail, vertexTypes, edgeTypes, allowDirected, vertexTags, edges, vertexNames, precOpt)
        } while (stream.nonEmpty && stream.head == ";")
        if (stream.isEmpty || stream.head != ")") {
          sys.error("parse error")
        }
        return stream.tail
      }

      stream.head match {
        case "-" => edgeTag = Some(edgeTypes(""))
        case ">" => inedge = false; edgeTag = Some(edgeTypes(""))
        case "<" => outedge = false; edgeTag = Some(edgeTypes(""))
        case ";" | ")" => return stream
        case x =>
          edgeTag = Some(edgeTypes(x))
          stream.headOption match {
            case Some(">") => inedge = false
            case Some("<") => outedge = false
            case _ =>
          }
      }

      if (!allowDirected && inedge != outedge) {
        sys.error("Directed edge not allowed here")
      }

      stream = stream.tail

    }

    val vertexTag: T = {
      if (stream.nonEmpty && vertexTypes.lift(stream.head).isDefined) {
        val vt = stream.head
        stream = stream.tail
        vertexTypes(vt)
      } else {
        if (stream.headOption.contains(".")) {
          stream = stream.tail
        }
        vertexTypes("")
      }
    }

    var vertexName: Option[String] = None

    if (stream.nonEmpty && stream.head == "[") {
      vertexName = Some(stream.tail.head)
      stream = stream.tail.tail
    }

    var vertex: Integer = null

    if (vertexName.exists(vertexNames.contains)) {
      vertex = vertexNames(vertexName.get)
      if (vertexTags(vertex.intValue - 1) != vertexTag) {
        sys.error("parse error (conflicting tags)")
      }
    } else {
      vertex = Integer(vertexTags.length + 1)
      vertexTags += vertexTag
      edges += ArrayBuffer[Edge[T]]()
      vertexName foreach { vertexNames(_) = vertex }
    }

    precOpt match {

      case Some(prec) =>
        if (outedge)
          edges(prec.intValue - 1) += Edge(prec, vertex, edgeTag.get)
        if (inedge)
          edges(vertex.intValue - 1) += Edge(vertex, prec, edgeTag.get)
      case None =>

    }

    parse(stream, vertexTypes, edgeTypes, allowDirected, vertexTags, edges, vertexNames, Some(vertex))

  }

}

trait GraphOps[T, +C] {

  def vertices: IndexedSeq[Vertex[T]]

  def edgeCount: Integer //= Integer(vertices.map { _.edges.size }.sum)

  def vertexCount: Integer = Integer(vertices.length)

  def vertex(n: Integer): Vertex[T] = vertices(n.intValue - 1)

  def edges: IndexedSeq[Edge[T]] //= vertices flatMap { _.edges }

  def fromVertices(vertices: IndexedSeq[Vertex[T]]): C

  def deleteEdge(edge: Edge[T], symmetric: java.lang.Boolean = false): C = {
    deleteEdgeByEndpoints(edge.fromVertex, edge.toVertex, edge.tag, symmetric)
  }

  def deleteEdgeByIndex(vFrom: Integer, eIndex: Integer, symmetric: java.lang.Boolean = false): C = fromVertices {
    val edge = vertex(vFrom).edge(eIndex)
    one to vertexCount map {
      case n if n == vFrom => vertex(n).deleteEdgeByIndex(eIndex)
      case n if symmetric && vFrom != edge.toVertex && n == edge.toVertex =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).toVertex == vFrom && (vertex(n).edge(k).tag == edge.tag)
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n => vertex(n)
    }
  }

  def deleteEdgeByEndpoints(vFrom: Integer, vTo: Integer, tag: T, symmetric: java.lang.Boolean = false): C = fromVertices {
    one to vertexCount map {
      case n if n == vFrom =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).toVertex == vTo && vertex(n).edge(k).tag == tag
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n if symmetric && vFrom != vTo && n == vTo =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).toVertex == vFrom && vertex(n).edge(k).tag == tag
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n => vertex(n)
    }
  }

  def deleteVertex(v: Integer): C = fromVertices {
    one to vertexCount collect { case n if n != v =>
      Vertex(vertex(n).tag, vertex(n).edges collect { case e if e.toVertex != v =>
        Edge(
          if (e.fromVertex < v) e.fromVertex else e.fromVertex - one,
          if (e.toVertex < v) e.toVertex else e.toVertex - one,
          e.tag
        )
      })
    }
  }

  def deleteVertices(vs: IndexedSeq[Integer]): C = {
    // TODO This is inefficient for large graphs
    retainVertices(one to vertexCount filterNot vs.contains)
  }

  def retainVertices(vs: IndexedSeq[Integer]): C = fromVertices {
    var next: Integer = zero
    val sorted = vs.sorted
    val vertexMap = sorted.map { n =>
      next += one
      n -> next
    }.toMap
    sorted.map { n =>
      val edges = vertex(n).edges collect {
        case edge if sorted contains edge.toVertex =>
          Edge[T](vertexMap(edge.fromVertex), vertexMap(edge.toVertex), edge.tag)
      }
      Vertex(vertex(n).tag, edges)
    }
  }

  def updatedVertexTag(v: Integer, tag: T): C = fromVertices {
    one to vertexCount map {
      case n if n == v => Vertex(tag, vertex(n).edges)
      case n => vertex(n)
    }
  }

  def updatedVertexTags(tagMap: scala.collection.Map[Integer, T]): C = fromVertices {
    one to vertexCount map {
      case n if tagMap contains n => Vertex(tagMap(n), vertex(n).edges)
      case n => vertex(n)
    }
  }

  def connectedEdgeCount(v: Integer): Integer = {
    Integer(connectedCount(v, new mutable.BitSet(vertices.length), countEdges = true))
  }

  def connectedVertexCount(v: Integer): Integer = {
    Integer(connectedCount(v, new mutable.BitSet(vertices.length)))
  }

  private def connectedCount(v: Integer, visited: mutable.BitSet, countEdges: Boolean = false): Int = {
    if (visited.contains(v.intValue - 1)) {
      0
    } else {
      visited += v.intValue - 1
      var cnt = if (countEdges) vertex(v).edges.length else 1
      for (edge <- vertex(v).edges) {
        cnt += connectedCount(edge.toVertex, visited, countEdges)
      }
      cnt
    }
  }

  def connectedComponent(v: Integer): C = {
    val visited = new mutable.BitSet(vertices.length)
    connectedCount(v, visited)
    val vs = visited.toIndexedSeq map { n => Integer(n + 1) }
    retainVertices(vs)
  }

}

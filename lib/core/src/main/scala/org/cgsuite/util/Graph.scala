package org.cgsuite.util

import org.cgsuite.core.Integer
import org.cgsuite.core.Values.{one, zero}
import org.cgsuite.util.Graph._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Graph {

  def parse[T](
    str: String,
    tagMap: scala.collection.Map[String, T] = scala.collection.Map.empty[String, T]
  ): Graph[T] = {

    val tokens = tokenize(str, 0)
    val vertexTags = ArrayBuffer[Option[T]]()
    val edges = ArrayBuffer[ArrayBuffer[Edge[T]]]()
    val vertexNames = mutable.Map[String, Integer]()
    parse(tokens, tagMap, vertexTags, edges, vertexNames, None)
    Graph {
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
    tagMap: scala.collection.Map[String, T],
    vertexTags: ArrayBuffer[Option[T]],
    edges: ArrayBuffer[ArrayBuffer[Edge[T]]],
    vertexNames: mutable.Map[String, Integer],
    precOpt: Option[Integer]
    ): List[String] = {

    if (tokens.isEmpty) {
      return Nil
    }

    var inedge: Boolean = true
    var outedge: Boolean = true
    var edgeTag: Option[String] = None

    var stream = tokens

    if (precOpt.isDefined) {

      if (stream.head == "(") {
        do {
          stream = parse(stream.tail, tagMap, vertexTags, edges, vertexNames, precOpt)
        } while (stream.nonEmpty && stream.head == ";")
        if (stream.isEmpty || stream.head != ")") {
          sys.error("parse error")
        }
        return stream.tail
      }

      stream.head match {
        case "-" =>
        case ">" => inedge = false
        case "<" => outedge = false
        case "l" | "r" | "e" => edgeTag = Some(stream.head)
        case ";" | ")" => return stream
        case _ => sys.error("parse error")
      }

      stream = stream.tail

    }

    var tag: Option[T] = None

    if (stream.nonEmpty) {
      stream.head match {
        case "." =>
          stream = stream.tail
        case x if tagMap contains x =>
          tag = Some(tagMap(x))
          stream = stream.tail
        case _ =>
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
      if (vertexTags(vertex.intValue - 1) != tag) {
        sys.error("parse error (conflicting tags)")
      }
    } else {
      vertex = Integer(vertexTags.length + 1)
      vertexTags += tag
      edges += ArrayBuffer[Edge[T]]()
      vertexName foreach { vertexNames(_) = vertex }
    }

    precOpt match {

      case Some(prec) =>
        if (outedge)
          edges(prec.intValue - 1) += Edge(edgeTag, vertex)
        if (inedge)
          edges(vertex.intValue - 1) += Edge(edgeTag, prec)
      case None =>

    }

    parse(stream, tagMap, vertexTags, edges, vertexNames, Some(vertex))

  }

  case class Vertex[T](tag: Option[T], edges: IndexedSeq[Edge[T]]) {

    def edgeCount: Integer = Integer(edges.length)

    def edge(n: Integer) = edges(n.intValue - 1)

    def deleteEdgeByIndex(eIndex: Integer): Vertex[T] = Vertex(
      tag,
      one to edgeCount collect { case n if n != eIndex =>
        edge(n)
      }
    )

  }

  case class Edge[T](tag: Option[String], outVertex: Integer)

}

case class Graph[T](vertices: IndexedSeq[Vertex[T]]) {

  def edgeCount: Integer = Integer(vertices.map { _.edges.size }.sum)

  def vertexCount: Integer = Integer(vertices.length)

  def vertex(n: Integer): Vertex[T] = vertices(n.intValue - 1)

  def deleteEdgeByIndex(v: Integer, eIndex: Integer): Graph[T] = Graph {
    one to vertexCount map {
      case n if n == v => vertex(n).deleteEdgeByIndex(eIndex)
      case n => vertex(n)
    }
  }

  def deleteEdgeByEndpoints(vFrom: Integer, vTo: Integer, tag: Option[T], symmetric: Boolean): Graph[T] = Graph {
    one to vertexCount map {
      case n if n == vFrom =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).outVertex == vTo &&
            (tag.isEmpty || vertex(n).edge(k).tag == tag)
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n if symmetric && vFrom != vTo && n == vTo =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).outVertex == vFrom &&
            (tag.isEmpty || vertex(n).edge(k).tag == tag)
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n => vertex(n)
    }
  }

  def deleteVertex(v: Integer): Graph[T] = Graph {
    one to vertexCount collect { case n if n != v =>
      Vertex(vertex(n).tag, vertex(n).edges collect { case e if e.outVertex != v =>
        if (e.outVertex < v) e else Edge(e.tag, e.outVertex - one)
      })
    }
  }

  def deleteVertices(vs: IndexedSeq[Integer]): Graph[T] = Graph {
    var next: Integer = zero
    val vertexMap = {
      one to vertexCount collect {
        case n if !vs.contains(n) =>
          next += one
          n -> next
      }
    }.toMap
    one to vertexCount collect {
      case n if !vs.contains(n) =>
        val edges = vertex(n).edges collect {
          case edge if !vs.contains(edge.outVertex) =>
            Edge[T](edge.tag, vertexMap(edge.outVertex))
        }
        Vertex(vertex(n).tag, edges)
    }
  }

  def updateVertexTag(v: Integer, tag: Option[T]): Graph[T] = Graph {
    one to vertexCount map {
      case n if n == v => Vertex(tag, vertex(n).edges)
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
        cnt += connectedCount(edge.outVertex, visited, countEdges)
      }
      cnt
    }
  }

  def connectedComponent(v: Integer): Graph[T] = {
    val visited = new mutable.BitSet(vertices.length)
    connectedCount(v, visited)
    val vs = visited.toIndexedSeq map { n => Integer(n + 1) }
    deleteVertices(vs)
  }

  override def toString = {
    s"<EdgeColoredGraph with $vertexCount vertices and $edgeCount edges>"
  }

}

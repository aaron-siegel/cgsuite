package org.cgsuite.util

import org.cgsuite.core.Values._
import org.cgsuite.core.{Integer, Values}
import org.cgsuite.exception.EvalException
import org.cgsuite.output.{OutputTarget, StyledTextOutput}
import org.cgsuite.util.Graph._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Graph {

  def parse[V, E](
    str: String,
    vertexLabels: PartialFunction[String, V],
    edgeLabels: PartialFunction[String, E]
  ): Graph[V, E] = {
    GraphParser.parse(str, vertexLabels, edgeLabels, allowDirected = false)(Graph.apply)
  }

  def fromAdjacencyList[V, E](adjacencyList: IndexedSeq[IndexedSeq[Integer]], vLabel: V, eLabel: E): Graph[V, E] = Graph {
    adjacencyList.indices map { vIndex =>
      val v = Integer(vIndex + 1)
      Vertex(vLabel, adjacencyList(vIndex) map { toVertex =>
        Edge(v, toVertex, eLabel)
      })
    }
  }

  val empty: Graph[Nothing, Nothing] = Graph(IndexedSeq.empty)

  def singleton[V](vLabel: V): Graph[V, Nothing] = {
    Graph(IndexedSeq(Vertex(vLabel, IndexedSeq.empty)))
  }

  def loop[V, E](vLabel: V, eLabel: E): Graph[V, E] = {
    Graph(IndexedSeq(Vertex(vLabel, IndexedSeq(Edge(one, one, eLabel)))))
  }

  def clique[V, E](size: Integer, vLabel: V, eLabel: E): Graph[V, E] = Graph {
    one to size map { n =>
      Vertex(vLabel, one to size collect {
        case k if k != n => Edge(n, k, eLabel)
      })
    }
  }

  def path[V, E](size: Integer, vLabel: V, eLabel: E): Graph[V, E] = size match {
    case Values.zero => empty
    case Values.one => singleton(vLabel)
    case _ => Graph {
      one to size map {
        case Values.one => Vertex(vLabel, IndexedSeq(Edge(one, two, eLabel)))
        case n if n == size => Vertex(vLabel, IndexedSeq(Edge(size, size - one, eLabel)))
        case n => Vertex(vLabel, IndexedSeq(Edge(n, n - one, eLabel), Edge(n, n + one, eLabel)))
      }
    }
  }

  def cycle[V, E](size: Integer, vLabel: V, eLabel: E): Graph[V, E] = size match {
    case Values.zero => empty
    case Values.one => loop(vLabel, eLabel)
    case _ => Graph {
      one to size map { n =>
        Vertex(vLabel, IndexedSeq(
          Edge(n, if (n == one) size else n - one, eLabel),
          Edge(n, if (n == size) one else n + one, eLabel)
        ))
      }
    }
  }

  def star[V, E](size: Integer, vLabel: V, eLabel: E): Graph[V, E] = size match {
    case Values.zero => empty
    case _ => Graph {
      Vertex(vLabel, two to size map { Edge(one, _, eLabel) }) +:
        (two to size map { n => Vertex(vLabel, IndexedSeq(Edge(n, one, eLabel))) })
    }
  }

  case class Vertex[+V, +E](label: V, edges: IndexedSeq[Edge[E]]) {

    val edgeCount: Integer = Integer(edges.length)

    def edge(n: Integer): Edge[E] = {
      val nInt = n.intValue - 1
      if (nInt >= 0 && nInt < edges.length) {
        edges(nInt)
      } else {
        throw EvalException(s"Edge is out of bounds: $n")
      }
    }

    def deleteEdgeByIndex(eIndex: Integer): Vertex[V, E] = Vertex(
      label,
      one to edgeCount collect { case n if n != eIndex =>
        edge(n)
      }
    )

    def outVertices: IndexedSeq[Integer] = edges map { _.toVertex }

  }

  case class Edge[+E](fromVertex: Integer, toVertex: Integer, label: E) {
    def toOutput = new StyledTextOutput(toString)
  }

}

case class Graph[+V, +E](vertices: IndexedSeq[Vertex[V, E]]) extends OutputTarget with GraphOps[V, E, Graph, Graph[V, E]] {

  override def fromVertices[W >: V, F >: E](vertices: IndexedSeq[Vertex[W, F]]): Graph[W, F] = {
    Graph(vertices)
  }

  def edgeCount: Integer = Integer {
    (one to vertexCount).map { v => vertex(v).edges.count { v <= _.toVertex } }.sum
  }

  def edges: IndexedSeq[Edge[E]] = {
    vertices flatMap { _.edges filter { e => e.fromVertex <= e.toVertex } }
  }

  def deleteEdge[W >: V, F >: E](edge: Edge[F]): Graph[W, F] = {
    super.deleteEdge(edge, undirected = true)
  }

  def deleteEdgeByEndpoints[W >: V, F >: E](vFrom: Integer, vTo: Integer, label: F): Graph[W, F] = {
    super.deleteEdgeByEndpoints(vFrom, vTo, label, undirected = true)
  }

  def deleteEdgeByIndex[W >: V, F >: E](vFrom: Integer, eIndex: Integer): Graph[W, F] = {
    super.deleteEdgeByIndex(vFrom, eIndex, undirected = true)
  }

  def connectedEdgeCount(v: Integer): Integer = {
    Integer(connectedCount(v, new mutable.BitSet(vertices.length), countEdges = true, undirected = true))
  }

  def isConnected: Boolean = isEmpty || connectedVertexCount(one) == vertexCount

  def isSimple: Boolean = {
    val edges = this.edges
    edges.forall { edge => edge.toVertex != edge.fromVertex } &&
      edges.distinct.size == edges.size
  }

  def toDirectedGraph: DirectedGraph[V, E] = DirectedGraph(vertices)

  override def toOutput: StyledTextOutput = {
    new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, s"""Graph("$toString")""")
  }

}

object GraphParser {

  private[cgsuite] def parse[E, V, C](
    str: String,
    vertexLabels: PartialFunction[String, V],
    edgeLabels: PartialFunction[String, E],
    allowDirected: Boolean
  )(
    fromVertices: IndexedSeq[Vertex[V, E]] => C
  ): C = {

    var stream = tokenize(str, 0)
    val assignedVertexLabels = ArrayBuffer[V]()
    val edges = ArrayBuffer[ArrayBuffer[Edge[E]]]()
    val vertexNames = mutable.Map[String, Integer]()

    stream = parse(stream, vertexLabels, edgeLabels, allowDirected, assignedVertexLabels, edges, vertexNames, None)
    while (stream.nonEmpty) {
      if (stream.head != ";") {
        throw EvalException("Invalid graph specification.")
      }
      stream = stream.tail
      stream = parse(stream, vertexLabels, edgeLabels, allowDirected, assignedVertexLabels, edges, vertexNames, None)
    }

    fromVertices {
      assignedVertexLabels.zip(edges).toIndexedSeq map { case (label, edges) =>
        Vertex(label, edges.toIndexedSeq)
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
          throw EvalException("Invalid graph specification.")
        str.substring(index + 1, end) :: tokenize(str, end + 1)
      case ch => ch.toString :: tokenize(str, index + 1)
    }
  }

  private def parse[V, E](
    tokens: List[String],
    vertexLabels: PartialFunction[String, V],
    edgeLabels: PartialFunction[String, E],
    allowDirected: Boolean,
    assignedVertexLabels: ArrayBuffer[V],
    edges: ArrayBuffer[ArrayBuffer[Edge[E]]],
    vertexNames: mutable.Map[String, Integer],
    precOpt: Option[Integer]
    ): List[String] = {

    if (tokens.isEmpty) {
      return Nil
    }

    var inedge: Boolean = true
    var outedge: Boolean = true
    var edgeLabel: Option[E] = None

    var stream = tokens

    if (precOpt.isDefined) {

      if (stream.head == "(") {
        do {
          stream = parse(stream.tail, vertexLabels, edgeLabels, allowDirected, assignedVertexLabels, edges, vertexNames, precOpt)
        } while (stream.nonEmpty && stream.head == ";")
        if (stream.isEmpty || stream.head != ")") {
          throw EvalException("Invalid graph specification: Unmatched `(`")
        }
        return stream.tail
      }

      stream.head match {
        case "-" => edgeLabel = Some(edgeLabels(""))
        case ">" => inedge = false; edgeLabel = Some(edgeLabels(""))
        case "<" => outedge = false; edgeLabel = Some(edgeLabels(""))
        case ";" | ")" => return stream
        case x =>
          edgeLabel = Some(edgeLabels(x))
          stream.tail.headOption match {
            case Some(">") => inedge = false; stream = stream.tail
            case Some("<") => outedge = false; stream = stream.tail
            case _ =>
          }
      }

      stream = stream.tail

      if (!allowDirected && inedge != outedge) {
        throw EvalException("Invalid graph specification: Directed graph edges are not allowed here")
      }

    }

    val vertexLabel: V = {
      if (stream.nonEmpty && vertexLabels.lift(stream.head).isDefined) {
        val vt = stream.head
        stream = stream.tail
        vertexLabels(vt)
      } else {
        if (stream.headOption.contains(".")) {
          stream = stream.tail
        }
        vertexLabels("")
      }
    }

    var vertexName: Option[String] = None

    if (stream.nonEmpty && stream.head == ":") {
      vertexName = Some(stream.tail.head)
      stream = stream.tail.tail
    }

    var vertex: Integer = null

    if (vertexName.exists(vertexNames.contains)) {
      vertex = vertexNames(vertexName.get)
      if (assignedVertexLabels(vertex.intValue - 1) != vertexLabel) {
        throw EvalException("Invalid graph specification: The same named vertex has multiple distinct labels")
      }
    } else {
      vertex = Integer(assignedVertexLabels.length + 1)
      assignedVertexLabels += vertexLabel
      edges += ArrayBuffer[Edge[E]]()
      vertexName foreach { vertexNames(_) = vertex }
    }

    precOpt match {

      case Some(prec) =>
        if (outedge)
          edges(prec.intValue - 1) += Edge(prec, vertex, edgeLabel.get)
        // We need to be slightly careful not to "double-count" the edge in the case where prec == vertex
        if (inedge && (!outedge || prec != vertex))
          edges(vertex.intValue - 1) += Edge(vertex, prec, edgeLabel.get)
      case None =>

    }

    parse(stream, vertexLabels, edgeLabels, allowDirected, assignedVertexLabels, edges, vertexNames, Some(vertex))

  }

}

trait GraphOps[+V, +E, +CC[_, _], +C] {

  def vertices: IndexedSeq[Vertex[V, E]]

  val vertexCount: Integer = Integer(vertices.length)

  def vertex(n: Integer): Vertex[V, E] = {
    val nInt = n.intValue - 1
    if (nInt >= 0 && nInt < vertices.length) {
      vertices(n.intValue - 1)
    } else {
      throw EvalException(s"Vertex is out of bounds: $n")
    }
  }

  def fromVertices[W >: V, F >: E](vertices: IndexedSeq[Vertex[W, F]]): CC[W, F]

  private[cgsuite] def deleteEdge[W >: V, F >: E](edge: Edge[F], undirected: Boolean): CC[W, F] = {
    deleteEdgeByEndpoints(edge.fromVertex, edge.toVertex, edge.label, undirected)
  }

  private[cgsuite] def deleteEdgeByIndex[W >: V, F >: E](vFrom: Integer, eIndex: Integer, undirected: Boolean): CC[W, F] = fromVertices {
    val edge = vertex(vFrom).edge(eIndex)
    one to vertexCount map {
      case n if n == vFrom => vertex(n).deleteEdgeByIndex(eIndex)
      case n if undirected && vFrom != edge.toVertex && n == edge.toVertex =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).toVertex == vFrom && (vertex(n).edge(k).label == edge.label)
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n => vertex(n)
    }
  }

  def deleteEdgeByEndpoints[W >: V, F >: E](vFrom: Integer, vTo: Integer, label: F, undirected: Boolean): CC[W, F] = fromVertices {
    one to vertexCount map {
      case n if n == vFrom =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).toVertex == vTo && vertex(n).edge(k).label == label
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n if undirected && vFrom != vTo && n == vTo =>
        one to vertex(n).edgeCount find { k =>
          vertex(n).edge(k).toVertex == vFrom && vertex(n).edge(k).label == label
        } match {
          case Some(k) => vertex(n).deleteEdgeByIndex(k)
          case None => vertex(n)
        }
      case n => vertex(n)
    }
  }

  def deleteVertex[W >: V, F >: E](v: Integer): CC[W, F] = fromVertices {
    if (v < one || v > vertexCount) {
      throw EvalException(s"Vertex is out of bounds: $v")
    }
    one to vertexCount collect { case n if n != v =>
      Vertex(vertex(n).label, vertex(n).edges collect { case e if e.toVertex != v =>
        Edge(
          if (e.fromVertex < v) e.fromVertex else e.fromVertex - one,
          if (e.toVertex < v) e.toVertex else e.toVertex - one,
          e.label
        )
      })
    }
  }

  def deleteVertices[W >: V, F >: E](vs: IndexedSeq[Integer]): CC[W, F] = {
    for (v <- vs if v < one || v > vertexCount) {
      throw EvalException(s"Vertex is out of bounds: $v")
    }
    // TODO This is inefficient for large graphs
    retainVertices(one to vertexCount filterNot vs.contains)
  }

  def retainVertices[W >: V, F >: E](vs: IndexedSeq[Integer]): CC[W, F] = fromVertices {
    for (v <- vs if v < one || v > vertexCount) {
      throw EvalException(s"Vertex is out of bounds: $v")
    }
    var next: Integer = zero
    val sorted = vs.sorted
    val vertexMap = sorted.map { n =>
      next += one
      n -> next
    }.toMap
    sorted.map { n =>
      val edges = vertex(n).edges collect {
        case edge if sorted contains edge.toVertex =>
          Edge[E](vertexMap(edge.fromVertex), vertexMap(edge.toVertex), edge.label)
      }
      Vertex(vertex(n).label, edges)
    }
  }

  def updatedVertexLabel[W >: V, F >: E](v: Integer, label: W): CC[W, F] = fromVertices {
    if (v < one || v > vertexCount) {
      throw EvalException(s"Vertex is out of bounds: $v")
    }
    one to vertexCount map {
      case n if n == v => Vertex(label, vertex(n).edges)
      case n => vertex(n)
    }
  }

  def updatedVertexLabels[W >: V, F >: E](labelMap: scala.collection.Map[Integer, W]): CC[W, F] = fromVertices {
    for (v <- labelMap.keys if v < one || v > vertexCount) {
      throw EvalException(s"Vertex is out of bounds: $v")
    }
    one to vertexCount map {
      case n if labelMap contains n => Vertex(labelMap(n), vertex(n).edges)
      case n => vertex(n)
    }
  }

  def connectedVertexCount(v: Integer): Integer = {
    Integer(connectedCount(v, new mutable.BitSet(vertices.length), countEdges = false, undirected = false))
  }

  private[cgsuite] def connectedCount[W >: V](
    v: Integer,
    visited: mutable.BitSet,
    countEdges: Boolean,
    undirected: Boolean,
    avoidLabel: Option[W] = None
  ): Int = {
    if (avoidLabel.contains(vertex(v).label) || visited.contains(v.intValue - 1)) {
      0
    } else {
      visited += v.intValue - 1
      var cnt = {
        if (countEdges) {
          if (undirected) {
            vertex(v).edges.count { v <= _.toVertex }
          } else {
            vertex(v).edges.length
          }
        } else {
          1
        }
      }
      for (edge <- vertex(v).edges) {
        cnt += connectedCount(edge.toVertex, visited, countEdges, undirected, avoidLabel)
      }
      cnt
    }
  }

  def connectedComponent[W >: V, F >: E](v: Integer): CC[W, F] = {
    val visited = new mutable.BitSet(vertices.length)
    connectedCount(v, visited, countEdges = false, undirected = false)
    val vs = visited.toIndexedSeq map { n => Integer(n + 1) }
    retainVertices(vs)
  }

  def connectedComponents[W >: V, F >: E]: IndexedSeq[CC[W, F]] = {
    connectedComponentsOptionalDecomp(None)
  }

  def decomposition[W >: V, F >: E](label: W): IndexedSeq[CC[W, F]] = {
    connectedComponentsOptionalDecomp(Some(label))
  }

  private def connectedComponentsOptionalDecomp[W >: V, F >: E](avoidLabel: Option[W]): IndexedSeq[CC[W, F]] = {
    val allVisited = new mutable.BitSet(vertices.length)
    val visited = new mutable.BitSet(vertices.length)
    one to vertexCount collect {
      case v if !allVisited.contains(v.intValue - 1) =>
        visited.clear()
        connectedCount(v, visited, countEdges = false, undirected = false, avoidLabel)
        allVisited ++= visited
        visited.toIndexedSeq map { n => Integer(n + 1) }
    } filter {
      _.nonEmpty
    } map retainVertices
  }

  def isEmpty: Boolean = vertexCount.isZero

  override def toString: String = toString(PartialFunction.empty, PartialFunction.empty)

  def toString(
    vertexLabelStrings: PartialFunction[V, String],
    edgeLabelStrings: PartialFunction[E, String]
  ): String = {
    val edgesRemaining = vertices map { vertex => ArrayBuffer(vertex.edges : _*) }
    val refCount = ArrayBuffer.fill[Int](vertices.length)(0)
    val trees = vertices.indices collect {
      case n if refCount(n) == 0 =>
        treeify(n, edgesRemaining, refCount)
    }
    val vertexNames = mutable.Map[Int, String]()
    trees map { stringify(_, refCount, vertexLabelStrings, edgeLabelStrings, vertexNames) } mkString ";"
  }

  private def treeify[F](vertex: Int, edgesRemaining: IndexedSeq[ArrayBuffer[Edge[F]]], refCount: ArrayBuffer[Int]): Tree[F] = {
    refCount(vertex) += 1
    if (refCount(vertex) == 1) {
      val children = ArrayBuffer[(Tree[F], F, Boolean)]()
      while (edgesRemaining(vertex).nonEmpty) {
        val nextEdge = edgesRemaining(vertex).head
        edgesRemaining(vertex).remove(0)
        val target = nextEdge.toVertex.intValue - 1
        var isDirected: Boolean = false
        if (nextEdge.toVertex != nextEdge.fromVertex) {
          val backIndex = edgesRemaining(target) indexOf Edge(nextEdge.toVertex, nextEdge.fromVertex, nextEdge.label)
          if (backIndex >= 0) {
            edgesRemaining(target).remove(backIndex)
          } else {
            isDirected = true
          }
        }
        children += ((treeify(target, edgesRemaining, refCount), nextEdge.label, isDirected))
      }
      Tree(vertex, children.toIndexedSeq)
    } else {
      Tree(vertex, IndexedSeq.empty)
    }
  }

  private def stringify[F](
    tree: Tree[F],
    refCount: ArrayBuffer[Int],
    vertexLabelStrings: PartialFunction[V, String],
    edgeLabelStrings: PartialFunction[F, String],
    namedVertices: mutable.Map[Int, String]
  ): String = {
    val builder = new StringBuilder()
    stringifyR(tree, refCount, vertexLabelStrings, edgeLabelStrings, namedVertices, builder)
    if (builder.isEmpty) {
      // Special case: a single vertex with no name, whose label maps to ""
      "."
    } else {
      builder.toString
    }
  }

  private def stringifyR[F](
    tree: Tree[F],
    refCount: ArrayBuffer[Int],
    vertexLabelStrings: PartialFunction[V, String],
    edgeLabelStrings: PartialFunction[F, String],
    namedVertices: mutable.Map[Int, String],
    builder: StringBuilder
  ): Unit = {
    val vLabel = vertices(tree.vertex).label
    val vLabelStr = vertexLabelStrings lift vLabel match {
      case Some(str) => str
      case None => if (vLabel == null) "" else vLabel.toString
    }
    stringifyAppend(builder, vLabelStr)
    if (refCount(tree.vertex) > 1) {
      builder += ':'
      stringifyAppend(builder, stringifyVertexName(namedVertices, tree.vertex))
    }
    if (tree.children.size > 1) {
      builder += '('
    }
    for (i <- tree.children.indices) {
      val (subtree, eLabel, isDirected) = tree.children(i)
      val eLabelStr = edgeLabelStrings lift eLabel match {
        case Some(str) => str
        case None => if (eLabel == null) "" else eLabel.toString
      }
      if (!isDirected && eLabelStr.isEmpty) {
        builder += '-'
      }
      stringifyAppend(builder, eLabelStr)
      if (isDirected) {
        builder += '>'
      }
      stringifyR(subtree, refCount, vertexLabelStrings, edgeLabelStrings, namedVertices, builder)
      if (i < tree.children.size - 1) {
        builder += ';'
      }
    }
    if (tree.children.size > 1) {
      builder += ')'
    }
  }

  private def stringifyAppend(builder: StringBuilder, labelStr: String): Unit = {
    if (labelStr.length > 1) {
      builder append s"{$labelStr}"
    } else {
      builder append labelStr
    }
  }

  private def stringifyVertexName(namedVertices: mutable.Map[Int, String], vertex: Int): String = {
    namedVertices.getOrElseUpdate(vertex,
      if (namedVertices.size < 26) {
        ('A' + namedVertices.size).toChar.toString
      } else {
        s"v${namedVertices.size}"
      }
    )
  }

  private case class Tree[+F](vertex: Int, children: IndexedSeq[(Tree[F], F, Boolean)])

}

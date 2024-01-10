package org.cgsuite.lang

object GraphTestCase {

  val instances = Seq(

    // Empty graph
    GraphTestCase(
      """Graph("")""", """Graph("")""",
      adjacencyList = "[]",
      connectedComponent = "!!Vertex is out of bounds: 1",
      connectedComponents = "[]",
      decomposition = "[]",
      deleteEdge = "!!Vertex is out of bounds: 1",
      deleteVertex = "!!Vertex is out of bounds: 1",
      deleteVertices = "!!Vertex is out of bounds: 1",
      edgeCount = "0",
      edge1 = "!!List index out of bounds: 1",
      isConnected = "true",
      isEmpty = "true",
      isSimple = "true",
      retainVertices = "!!Vertex is out of bounds: 1",
      updatedVertexTags = "!!Vertex is out of bounds: 1",
      vertexCount = "0",
      vertex1 = "!!List index out of bounds: 1"
    ),

    // One vertex, no edges
    GraphTestCase(
      """Graph(".")""",
      """Graph(".")""",
      adjacencyList = "[[]]",
      connectedComponent = """Graph(".")""",
      connectedComponents = """[Graph(".")]""",
      decomposition = """[Graph(".")]""",
      deleteEdge = "!!Edge is out of bounds: 1",
      deleteVertex = """Graph("")""",
      deleteVertices = "!!Vertex is out of bounds: 2",
      edgeCount = "0",
      edge1 = "!!List index out of bounds: 1",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """!!Vertex is out of bounds: 2""",
      updatedVertexTags = """Graph("{Left}")""",
      vertexCount = "1",
      vertex1 = "Vertex.instance"
    ),

    // Two vertices, single undirected edge
    GraphTestCase(
      """Graph("-")""",
      """Graph("-")""",
      adjacencyList = "[[2],[1]]",
      connectedComponent = """Graph("-")""",
      connectedComponents = """[Graph("-")]""",
      decomposition = """[Graph("-")]""",
      deleteEdge = """Graph(".;.")""",
      deleteVertex = """Graph(".")""",
      deleteVertices = """Graph("")""",
      edgeCount = "1",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexTags = """Graph("{Left}-")""",
      vertexCount = "2",
      vertex1 = "Vertex.instance"
    ),

    // Undirected path of length 5
    GraphTestCase(
      """Graph.Path(5)""",
      """Graph("----")""",
      adjacencyList = "[[2],[1,3],[2,4],[3,5],[4]]",
      connectedComponent = """Graph("----")""",
      connectedComponents = """[Graph("----")]""",
      decomposition = """[Graph("----")]""",
      deleteEdge = """Graph(".;---")""",
      deleteVertex = """Graph("---")""",
      deleteVertices = """Graph("--")""",
      edgeCount = "4",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexTags = """Graph("{Left}----")""",
      vertexCount = "5",
      vertex1 = "Vertex.instance"
    ),

    // Simple loop (cycle of length 1)
    GraphTestCase(
      """Graph.Cycle(1)""",
      """Graph(":A-:A")""",
      adjacencyList = "[[1]]",
      connectedComponent = """Graph(":A-:A")""",
      connectedComponents = """[Graph(":A-:A")]""",
      decomposition = """[Graph(":A-:A")]""",
      deleteEdge = """Graph(".")""",
      deleteVertex = """Graph("")""",
      deleteVertices = """!!Vertex is out of bounds: 2""",
      edgeCount = "1",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "false",
      retainVertices = """!!Vertex is out of bounds: 2""",
      updatedVertexTags = """Graph("{Left}:A-{Left}:A")""",
      vertexCount = "1",
      vertex1 = "Vertex.instance"
    ),

    // Undirected cycle of length 5
    GraphTestCase(
      """Graph.Cycle(5)""",
      """Graph(":A-----:A")""",
      adjacencyList = "[[2,5],[1,3],[2,4],[3,5],[1,4]]",
      connectedComponent = """Graph(":A-----:A")""",
      connectedComponents = """[Graph(":A-----:A")]""",
      decomposition = """[Graph(":A-----:A")]""",
      deleteEdge = """Graph("----")""",
      deleteVertex = """Graph("---")""",
      deleteVertices = """Graph("--")""",
      edgeCount = "5",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexTags = """Graph("{Left}:A-----{Left}:A")""",
      vertexCount = "5",
      vertex1 = "Vertex.instance"
    )

  )

}

case class GraphTestCase(
  x: String,
  xOut: String,
  adjacencyList: String,
  connectedComponent: String,
  connectedComponents: String,
  decomposition: String,
  deleteEdge: String,
  deleteVertex: String,
  deleteVertices: String,
  edgeCount: String,
  edge1: String,
  isConnected: String,
  isEmpty: String,
  isSimple: String,
  retainVertices: String,
  updatedVertexTags: String,
  vertexCount: String,
  vertex1: String
) {

  def toTests = Seq(
    (x, xOut),
    (s"Graph.FromAdjacencyList($adjacencyList)", xOut),
    (s"($x).AdjacencyList", adjacencyList),
    (s"($x).ConnectedComponent(1)", connectedComponent),
    (s"($x).ConnectedComponents", connectedComponents),
    (s"($x).Decomposition(Left)", decomposition),
    (s"($x).DeleteEdge(($x)[1][1])", deleteEdge),
    (s"($x).DeleteEdgeByIndex(1, 1)", deleteEdge),
    (s"($x).DeleteVertex(1)", deleteVertex),
    (s"($x).DeleteVertices([1, 2])", deleteVertices),
    (s"($x).EdgeCount", edgeCount),
    (s"($x).Edges[1]", edge1),
    (s"($x).IsConnected", isConnected),
    (s"($x).IsEmpty", isEmpty),
    (s"($x).IsSimple", isSimple),
    (s"($x).RetainVertices([1, 2])", retainVertices),
    (s"($x).UpdatedVertexTags({ 1 => Left })", updatedVertexTags),
    (s"($x).VertexCount", vertexCount),
    (s"($x).Vertices[1]", vertex1)
  ) map { case (expr, result) => (expr, expr, result) }

}

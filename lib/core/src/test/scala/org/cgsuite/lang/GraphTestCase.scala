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
      updatedVertexLabels = "!!Vertex is out of bounds: 1",
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
      updatedVertexLabels = """Graph("{Left}")""",
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
      updatedVertexLabels = """Graph("{Left}-")""",
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
      updatedVertexLabels = """Graph("{Left}----")""",
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
      updatedVertexLabels = """Graph("{Left}:A-{Left}:A")""",
      vertexCount = "1",
      vertex1 = "Vertex.instance"
    ),

    // Multigraph (two identical edges)
    GraphTestCase(
      """Graph(":A--:A")""",
      """Graph(":A--:A")""",
      adjacencyList = "[[2,2],[1,1]]",
      connectedComponent = """Graph(":A--:A")""",
      connectedComponents = """[Graph(":A--:A")]""",
      decomposition = """[Graph(":A--:A")]""",
      deleteEdge = """Graph("-")""",
      deleteVertex = """Graph(".")""",
      deleteVertices = """Graph("")""",
      edgeCount = "2",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "false",
      retainVertices = """Graph(":A--:A")""",
      updatedVertexLabels = """Graph("{Left}:A--{Left}:A")""",
      vertexCount = "2",
      vertex1 = "Vertex.instance"
    ),

    // Undirected cycle of size 5
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
      updatedVertexLabels = """Graph("{Left}:A-----{Left}:A")""",
      vertexCount = "5",
      vertex1 = "Vertex.instance"
    ),

    // Star graph of size 5
    GraphTestCase(
      """Graph.Star(5)""",
      """Graph("(-;-;-;-)")""",
      adjacencyList = "[[2,3,4,5],[1],[1],[1],[1]]",
      connectedComponent = """Graph("(-;-;-;-)")""",
      connectedComponents = """[Graph("(-;-;-;-)")]""",
      decomposition = """[Graph("(-;-;-;-)")]""",
      deleteEdge = """Graph("(-;-;-);.")""",
      deleteVertex = """Graph(".;.;.;.")""",
      deleteVertices = """Graph(".;.;.")""",
      edgeCount = "4",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexLabels = """Graph("{Left}(-;-;-;-)")""",
      vertexCount = "5",
      vertex1 = "Vertex.instance"
    ),

    // Clique of size 5
    GraphTestCase(
      """Graph.Clique(5)""",
      """Graph(":A-:B-:C(-:A;-(-:A;-:B;-(-:A;-:B;-:C)))")""",
      adjacencyList = "[[2,3,4,5],[1,3,4,5],[1,2,4,5],[1,2,3,5],[1,2,3,4]]",
      connectedComponent = """Graph(":A-:B-:C(-:A;-(-:A;-:B;-(-:A;-:B;-:C)))")""",
      connectedComponents = """[Graph(":A-:B-:C(-:A;-(-:A;-:B;-(-:A;-:B;-:C)))")]""",
      decomposition = """[Graph(":A-:B-:C(-:A;-(-:A;-:B;-(-:A;-:B;-:C)))")]""",
      deleteEdge = """Graph(":A-:C-:B-(-:A;-:C;-(-:A;-:B;-:C))")""",
      deleteVertex = """Graph(":A-:B-(-:A;-(-:A;-:B))")""",
      deleteVertices = """Graph(":A---:A")""",
      edgeCount = "10",
      edge1 = "Edge.instance",
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexLabels = """Graph("{Left}:A-:B-:C(-{Left}:A;-(-{Left}:A;-:B;-(-{Left}:A;-:B;-:C)))")""",
      vertexCount = "5",
      vertex1 = "Vertex.instance"
    ),

    // Several disconnected subgraphs
    GraphTestCase(
      """Graph("-;--;---")""",
      """Graph("-;--;---")""",
      adjacencyList = "[[2],[1],[4],[3,5],[4],[7],[6,8],[7,9],[8]]",
      connectedComponent = """Graph("-")""",
      connectedComponents = """[Graph("-"),Graph("--"),Graph("---")]""",
      decomposition = """[Graph("-"),Graph("--"),Graph("---")]""",
      deleteEdge = """Graph(".;.;--;---")""",
      deleteVertex = """Graph(".;--;---")""",
      deleteVertices = """Graph("--;---")""",
      edgeCount = "6",
      edge1 = "Edge.instance",
      isConnected = "false",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexLabels = """Graph("{Left}-;--;---")""",
      vertexCount = "9",
      vertex1 = "Vertex.instance"
    ),

    // Decomposition
    GraphTestCase(
      """Graph.Parse(".-.-L-L-.-.-L", vertexLabels => { "" => Nothing, "L" => Left })""",
      """Graph("--{Left}-{Left}---{Left}")""",
      adjacencyList = "[[2],[1,3],[2,4],[3,5],[4,6],[5,7],[6]]",
      connectedComponent = """Graph("--{Left}-{Left}---{Left}")""",
      connectedComponents = """[Graph("--{Left}-{Left}---{Left}")]""",
      decomposition = """[Graph("-"),Graph("-")]""",
      deleteEdge = """Graph(".;-{Left}-{Left}---{Left}")""",
      deleteVertex = """Graph("-{Left}-{Left}---{Left}")""",
      deleteVertices = """Graph("{Left}-{Left}---{Left}")""",
      edgeCount = "6",
      edge1 = "Edge.instance",
      fromAdjacencyList = Some("""Graph("------")"""),
      isConnected = "true",
      isEmpty = "false",
      isSimple = "true",
      retainVertices = """Graph("-")""",
      updatedVertexLabels = """Graph("{Left}--{Left}-{Left}---{Left}")""",
      vertexCount = "7",
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
  fromAdjacencyList: Option[String] = None, // If different from xOut
  isConnected: String,
  isEmpty: String,
  isSimple: String,
  retainVertices: String,
  updatedVertexLabels: String,
  vertexCount: String,
  vertex1: String
) {

  def toTests = Seq(
    (x, xOut),
    (s"Graph.FromAdjacencyList($adjacencyList)", fromAdjacencyList getOrElse xOut),
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
    (s"($x).UpdatedVertexLabels({ 1 => Left })", updatedVertexLabels),
    (s"($x).VertexCount", vertexCount),
    (s"($x).Vertices[1]", vertex1)
  ) map { case (expr, result) => (expr, expr, result) }

}

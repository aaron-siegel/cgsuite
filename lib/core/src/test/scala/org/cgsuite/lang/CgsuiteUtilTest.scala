package org.cgsuite.lang

import org.scalatest.prop.TableDrivenPropertyChecks.Table

class CgsuiteUtilTest extends CgscriptSpec {

  "cgsuite.util.MutableList" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("MutableList: Construction (empty)", "MutableList()", "MutableList()"),
      ("MutableList: Construction (seeded)", "x := MutableList([3,4,5])", "MutableList(3, 4, 5)"),
      ("MutableList: Lookup", "x[2]", "4"),
      ("MutableList: Lookup (not in list)", "x[0]", "!!List index out of bounds: 0"),
      ("MutableList: Add", "x.Add(*); x", "MutableList(3, 4, 5, *)"),
      ("MutableList: AddAll", "x.AddAll([1/2, 1/4, *2, *3, 1/2, 1/4, *3]); x", "MutableList(3, 4, 5, *, 1/2, 1/4, *2, *3, 1/2, 1/4, *3)"),
      ("MutableList: Contains", "x.Contains(*3)", "true"),
      ("MutableList: Remove", "x.Remove(1/2); x", "MutableList(3, 4, 5, *, 1/4, *2, *3, 1/2, 1/4, *3)"),
      ("MutableList: RemoveAll", "x.RemoveAll([4, *2]); x", "MutableList(3, 5, *, 1/4, *3, 1/2, 1/4, *3)"),
      ("MutableList: RemoveAll (multi)", "x.RemoveAll([1/4, 1/4]); x", "MutableList(3, 5, *, *3, 1/2, *3)"),
      ("MutableList: RemoveAt", "x.RemoveAt(2); x", "MutableList(3, *, *3, 1/2, *3)"),
      ("MutableList: Sort", "x.Sort(); x", "MutableList(1/2, 3, *, *3, *3)"),
      ("MutableList: SortWith", "x.SortWith((a, b) -> if a < b then -1 elseif a > b then 1 else 0 end)", "MutableList(*, *3, *3, 1/2, 3)"),
      ("MutableList: Clear", "x.Clear(); x", "MutableList()"),

    ))

  }

  "cgsuite.util.MutableSet" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("MutableSet: Construction (empty)", "MutableSet()", "MutableSet()"),
      ("MutableSet: Construction (seeded)", "x := MutableSet([3,4,5])", "MutableSet(3, 4, 5)"),
      ("MutableSet: Add", "x.Add(*); x", "MutableSet(3, 4, 5, *)"),
      ("MutableSet: AddAll", "x.AddAll([1/2, 1/4, *2, *3, 1/2, 1/4, *3]); x", "MutableSet(1/4, 1/2, 3, 4, 5, *, *2, *3)"),
      ("MutableSet: Contains", "x.Contains(*3)", "true"),
      ("MutableSet: Remove", "x.Remove(1/4); x", "MutableSet(1/2, 3, 4, 5, *, *2, *3)"),
      ("MutableSet: RemoveAll", "x.RemoveAll([3, 5, *2, 5, 19]); x", "MutableSet(1/2, 4, *, *3)"),
      ("MutableSet: Clear", "x.Clear(); x", "MutableSet()")
    ))

  }
  "cgsuite.util.MutableMap" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("MutableMap: Construction (empty)", "MutableMap()", "MutableMap()"),
      ("MutableMap: Construction (seeded)", "x := MutableMap({3 => true, 5 => Left})", "MutableMap(3 => true, 5 => Left)"),
      ("MutableMap: Lookup", "x[5]", "Left"),
      ("MutableMap: Lookup (not in map)", "x[*]", "!!Key not found: *"),
      ("MutableMap: ContainsKey", "x.ContainsKey(5)", "true"),
      ("MutableMap: Entries", "x.Entries", "{3 => true,5 => Left}"),
      ("MutableMap: Put 1", "x.Put(3, false); x", "MutableMap(3 => false, 5 => Left)"),
      ("MutableMap: Put 2", "x.Put(*, 101); x", "MutableMap(3 => false, 5 => Left, * => 101)"),
      ("MutableMap: PutAll", "x.PutAll({Right => 1474, 3 => 1/7}); x", "MutableMap(3 => 1/7, 5 => Left, * => 101, Right => 1474)"),
      ("MutableMap: Clear", "x.Clear(); x", "MutableMap()")
    ))

  }

  "cgsuite.util.Strip" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("Empty", "Strip.Empty(8)", "Strip([0,0,0,0,0,0,0,0])"),
      ("Parse", """strip := Strip.Parse(".abbd.", ".abcd")""", "Strip([0,1,2,2,4,0])"),
      ("op []", "strip[3]", "2"),
      ("Decomposition", """strip.Decomposition(2)""", "[Strip([0,1]),Strip([4,0])]"),
      ("Decomposition edges", """strip.Decomposition(0)""", "[Strip([1,2,2,4])]"),
      ("Decomposition empty", """Strip.Empty(0).Decomposition(0)""", "[]"),
      ("FindAll", "strip.FindAll(2)", "[3,4]"),
      ("Length", "strip.Length", "6"),
      ("ToString", """strip.ToString(".abcd")""", "\".abbd.\""),
      ("Updated", "strip.Updated(3, 4)", "Strip([0,1,4,2,4,0])"),
      ("Updated OOB", "strip.Updated(7, 0)", "!!Index is out of bounds: 7"),
      ("Updated Value OOB", "strip.Updated(3, 1001)", "!!Value is out of range (must satisfy -128 <= x <= 127): 1001"),
      ("UpdatedRange", "strip.UpdatedRange(2, 4, 5)", "Strip([0,5,5,5,4,0])"),
      ("UpdatedRange OOB", "strip.UpdatedRange(-1, 7, 5)", "Strip([5,5,5,5,5,5])"),
      ("Updated Multi", "strip.Updated({ 1 => 4, 4 => 127, 6 => -128 })", "Strip([4,1,2,127,4,-128])"),
      ("Updated Multi OOB", "strip.Updated({ 3 => 4, 4 => 1, -1 => 2 })", "!!Index is out of bounds: -1"),
      ("Updated Multi Value OOB", "strip.Updated({ 3 => -129 })", "!!Value is out of range (must satisfy -128 <= x <= 127): -129"),
      ("Substrip", "strip.Substrip(2, 5)", "Strip([1,2,2,4])"),
      ("Singleton Substrip", "strip.Substrip(2, 2)", "Strip([1])"),
      ("Empty Substrip 1", "strip.Substrip(3, 2)", "Strip([])"),
      ("Empty Substrip 2", "strip.Substrip(19, 2)", "Strip([])")
    ))
  }

  "cgsuite.util.Grid" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("Empty", "Grid.Empty(3,4)", "Grid([[0,0,0,0],[0,0,0,0],[0,0,0,0]])"),
      ("Empty with zero", "Grid.Empty(4,0)", "Grid([])"),
      ("Empty with neg", "Grid.Empty(-1,2)", "!!Invalid RowCount: -1"),
      ("Empty with neg 2", "Grid.Empty(2,-3)", "!!Invalid ColCount: -3"),
      ("Parse", """grid := Grid.Parse(".abb|.ba.|.a..", ".ab")""", "Grid([[0,1,2,2],[0,2,1,0],[0,1,0,0]])"),
      ("op []", "grid[(2,2)]", "2"),
      ("ColCount", "grid.ColCount", "4"),
      ("FindAll", "grid.FindAll(1)", "[(1,2),(2,3),(3,2)]"),
      ("IsInBounds", "grid.IsInBounds((1,3))", "true"),
      ("IsInBounds false", "grid.IsInBounds((5,3))", "false"),
      ("IsInBounds negative", "grid.IsInBounds((2,-1))", "false"),
      ("Permuted", "grid.Permuted(Symmetry.Inversion)", "Grid([[0,0,1,0],[0,1,2,0],[2,2,1,0]])"),
      ("Permuted 2", "grid.Permuted(Symmetry.ClockwiseRotation)", "Grid([[2,0,0],[2,1,0],[1,2,1],[0,0,0]])"),
      ("RowCount", "grid.RowCount", "3"),
      ("Subgrid", "grid.Subgrid((1,2),(3,3))", "Grid([[1,2],[2,1],[1,0]])"),
      ("Singleton Subgrid", "grid.Subgrid((3,3),(3,3))", "Grid([[0]])"),
      ("Empty Subgrid 1", "grid.Subgrid((1,2),(2,1))", "Grid([])"),
      ("Empty Subgrid 2", "grid.Subgrid((1,3),(2,1))", "Grid([])"),
      ("ToString", """grid.ToString(".ab")""", "\".abb|.ba.|.a..\""),
      ("Updated", "grid.Updated((1,3), 4)", "Grid([[0,1,4,2],[0,2,1,0],[0,1,0,0]])"),
      ("Updated OOB", "grid.Updated((1,7), 4)", "!!Coordinate is out of bounds: (1,7)"),
      ("Updated Value OOB", "grid.Updated((1,3), 128)", "!!Value is out of range (must satisfy -128 <= x <= 127): 128"),
      ("Updated Multi", "grid.Updated({ (1,3) => 4, (2,4) => 127, (3,1) => -128 })", "Grid([[0,1,4,2],[0,2,1,127],[-128,1,0,0]])"),
      ("Updated Multi OOB", "grid.Updated({ (1,3) => 4, (0,1) => 5 })", "!!Coordinate is out of bounds: (0,1)"),
      ("Updated Multi Value OOB", "grid.Updated({ (1,3) => 4, (2,4) => -1776 })", "!!Value is out of range (must satisfy -128 <= x <= 127): -1776"),
    ))

  }

  "cgsuite.util.Graph" should "implement methods correctly" in {

    val tests = GraphTestCase.instances flatMap { _.toTests }

    executeTests(Table(header, tests: _*))

  }

  "cgsuite.util.Random" should "implement methods correctly" in {

    executeTests(Table(
      header,
      ("Construct a Random", "random := Random(1474)", "Random(1474)"),
      ("Random.NextInteger", "[random.NextInteger(100) for n from 1 to 20]",
        "[64,92,17,25,63,58,88,49,0,30,85,46,58,24,72,31,98,61,61,69]"),
      ("Random overflow", "Random(2^63)", "!!Overflow.")
    ))

  }

}

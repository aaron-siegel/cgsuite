package org.cgsuite.lang

import org.cgsuite.core.CanonicalShortGame
import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.util.Coordinates


object UniversalOrdering extends Ordering[Any] {

  def compare(x: Any, y: Any): Int = {
    (x, y) match {
      case (null, null) => 0
      case (null, _) => -1
      case (_, null) => 1
      case (g: CanonicalShortGame, h: CanonicalShortGame) => CanonicalShortGame.DeterministicOrdering.compare(g, h)
      case (_: CanonicalShortGame, _) => -1
      case (_, _: CanonicalShortGame) => 1
      case (g: MisereCanonicalGame, h: MisereCanonicalGame) => MisereCanonicalGame.DeterministicOrdering.compare(g, h)
      case (a: String, b: String) => a.compare(b)
      case (a: ClassObject, b: ClassObject) => a.forClass.classOrdinal - b.forClass.classOrdinal
      case (a: StandardObject, b: StandardObject) =>
        var cmp = a.cls.classOrdinal - b.cls.classOrdinal
        var i = 0
        while (cmp == 0 && i < a.vars.length) {
          cmp = compare(a.vars(i), b.vars(i))
          i += 1
        }
        cmp
      case (a: Coordinates, b: Coordinates) =>
        val cmp = a.row - b.row
        if (cmp == 0) a.col - b.col else cmp
      case (a: (_,_), b: (_,_)) =>
        val cmp = compare(a._1, b._1)
        if (cmp == 0) compare(a._2, b._2) else cmp
      case (a: Seq[_], b: Seq[_]) =>
        var cmp = 0
        var i = 0
        while (cmp == 0 && i < a.length && i < b.length) {
          cmp = compare(a(i), b(i))
          i += 1
        }
        if (cmp == 0) a.length - b.length else cmp
      case (a: Set[_], b: Set[_]) => compare(a.toSeq.sorted(this), b.toSeq.sorted(this))
      case (_, _) =>
        val cmp = CgscriptClass.of(x).classOrdinal - CgscriptClass.of(y).classOrdinal
        if (cmp == 0)
          x.hashCode - y.hashCode  // TODO this can be improved
        else
          cmp
    }
  }

}

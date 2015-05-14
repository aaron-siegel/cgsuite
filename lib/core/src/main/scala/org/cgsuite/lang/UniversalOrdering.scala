package org.cgsuite.lang

import org.cgsuite.core.CanonicalShortGame


object UniversalOrdering extends Ordering[Any] {

  def compare(x: Any, y: Any): Int = {
    (x, y) match {
      case (g: CanonicalShortGame, h: CanonicalShortGame) => CanonicalShortGame.DeterministicOrdering.compare(g, h)
      case (_: CanonicalShortGame, _) => -1
      case (_, _: CanonicalShortGame) => 1
      case (a: ClassObject, b: ClassObject) => a.forClass.classOrdinal - b.forClass.classOrdinal
      case (a: StandardObject, b: StandardObject) =>
        var cmp = a.cls.classOrdinal - b.cls.classOrdinal
        var i = 0
        while (cmp == 0 && i < a.vars.length) {
          cmp = compare(a.vars(i), b.vars(i))
          i += 1
        }
        cmp
      case (_, _) => x.hashCode - y.hashCode  // TODO this can be improved
    }
  }

}

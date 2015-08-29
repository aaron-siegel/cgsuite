package org.cgsuite.core.impartial

import org.cgsuite.core.Integer

trait HeapRuleset {

  def heapOptions(heapSize: Integer): Iterable[Iterable[Integer]]

}

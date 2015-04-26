package org.cgsuite

import org.cgsuite.core._

package object dsl extends ValuesTrait {

  implicit def intToInteger(n: Int) = SmallInteger(n)
  implicit def longToInteger(n: Long) = Integer(n)

}

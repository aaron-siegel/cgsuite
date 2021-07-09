package org.cgsuite

import org.cgsuite.core._

import scala.language.implicitConversions

package object dsl extends ValuesTrait {

  implicit def longToInteger(x: Long): Integer = Integer(x)

  implicit object IntegerIsIntegral extends Integral[Integer] {
    def plus(x: Integer, y: Integer): Integer = x + y
    def minus(x: Integer, y: Integer): Integer = x - y
    def times(x: Integer, y: Integer): Integer = x * y
    def quot(x: Integer, y: Integer): Integer = x div y
    def rem(x: Integer, y: Integer): Integer = x % y
    def negate(x: Integer): Integer = -x
    def fromInt(x: Integer): Integer = x
    def toInt(x: Integer): Int = x.intValue
    def toLong(x: Integer): Long = x.longValue
    def toFloat(x: Integer): Float = x.floatValue
    def toDouble(x: Integer): Double = x.doubleValue
    def compare(x: Integer, y: Integer) = x compare y
    def fromInt(x: Int) = SmallInteger(x)
  }

}

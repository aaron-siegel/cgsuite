package org.cgsuite

import org.cgsuite.core._

package object dsl extends ValuesTrait {

  implicit object IntegerIsIntegral extends Integral[Integer] {
    def plus(x: Integer, y: Integer): Integer = x + y
    def minus(x: Integer, y: Integer): Integer = x - y
    def times(x: Integer, y: Integer): Integer = x * y
    def quot(x: Integer, y: Integer): Integer = x div y
    def rem(x: Integer, y: Integer): Integer = x % y
    def negate(x: Integer): Integer = -x
    def toInt(x: Integer): Int = x.intValue
    def toLong(x: Integer): Long = x.longValue
    def toFloat(x: Integer): Float = x.floatValue
    def toDouble(x: Integer): Double = x.doubleValue
    def compare(x: Integer, y: Integer) = x compare y
    def fromInt(x: Int) = SmallInteger(x)
    def parseString(str: String): Option[Integer] = Some(Integer.parseInteger(str))
  }

  implicit object GeneralizedOrdinalIsNumeric extends Numeric[GeneralizedOrdinal] {
    override def plus(x: GeneralizedOrdinal, y: GeneralizedOrdinal): GeneralizedOrdinal = x + y
    override def minus(x: GeneralizedOrdinal, y: GeneralizedOrdinal): GeneralizedOrdinal = x - y
    override def times(x: GeneralizedOrdinal, y: GeneralizedOrdinal): GeneralizedOrdinal = x * y
    override def negate(x: GeneralizedOrdinal): GeneralizedOrdinal = -x
    override def fromInt(x: Int): GeneralizedOrdinal = Integer(x)
    override def parseString(str: String): Option[GeneralizedOrdinal] = ???
    override def toInt(x: GeneralizedOrdinal): Int = x.asInstanceOf[Integer].intValue
    override def toLong(x: GeneralizedOrdinal): Long = x.asInstanceOf[Integer].longValue
    override def toFloat(x: GeneralizedOrdinal): Float = x.asInstanceOf[Integer].floatValue
    override def toDouble(x: GeneralizedOrdinal): Double = x.asInstanceOf[Integer].doubleValue
    override def compare(x: GeneralizedOrdinal, y: GeneralizedOrdinal): Int = x compare y
  }

  implicit class RichGOIterable(iterable: Iterable[GeneralizedOrdinal]) {
    def nimSum: GeneralizedOrdinal = iterable.foldLeft[GeneralizedOrdinal](zero) { _ nimSum _ }
  }

}

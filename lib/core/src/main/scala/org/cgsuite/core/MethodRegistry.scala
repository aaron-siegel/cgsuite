package org.cgsuite.core


object MethodRegistry {

  val registry: Map[String, MethodRegistry] = Map(
    z("Mean") { case g: CanonicalShortGame => g.mean },
    z("Temperature") { case g: CanonicalShortGame => g.temperature },
    u("Cool") { case g: CanonicalShortGame => { case t: DyadicRationalNumber => g.cool(t) } }
  )

  def z(name: String)(lookup: Any => Any) = name -> ZeroaryMethod(lookup)
  def u(name: String)(lookup: Any => Any => Any) = name -> UnaryMethod(lookup)

  def lookup(name: String, x: Any): Option[Any] = {
    registry.get(name) map {
      case ZeroaryMethod(lookup) => lookup(x)
      case UnaryMethod(lookup) => lookup(x)
    }
  }

}

sealed trait MethodRegistry
case class ZeroaryMethod(lookup: Any => Any) extends MethodRegistry
case class UnaryMethod(lookup: Any => Any => Any) extends MethodRegistry

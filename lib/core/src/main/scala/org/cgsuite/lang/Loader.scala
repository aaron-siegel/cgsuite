package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.util.{Coordinates, Grid}

object Loader {

  val systemClasses: Map[String, Option[Class[_]]] = Map(

    "cgsuite.lang.Class" -> None,
    "cgsuite.lang.Coordinates" -> Some(classOf[Coordinates]),
    "cgsuite.lang.Object" -> None,
    "cgsuite.lang.String" -> Some(classOf[String]),

    "cgsuite.util.Grid" -> Some(classOf[Grid]),

    "game.CanonicalShortGame" -> Some(classOf[CanonicalShortGame]),
    "game.DyadicRational" -> Some(classOf[DyadicRationalNumber]),
    "game.Game" -> Some(classOf[Game]),
    "game.Integer" -> Some(classOf[Integer]),
    "game.Nimber" -> Some(classOf[Nimber]),
    "game.NumberUpStar" -> Some(classOf[NumberUpStar]),
    "game.Player" -> Some(classOf[Player]),
    "game.Rational" -> Some(classOf[RationalNumber]),
    "game.Zero" -> Some(Zero.getClass),

    "game.Clobber" -> None
  )

  def declareSystemResources() {

    systemClasses foreach { case (name, scalaClass) =>
      declareSystemClass(name, scalaClass)
    }

  }

  private def declareSystemClass(name: String, scalaClass: Option[Class[_]]) {

    val path = name.replace('.', '/')
    val url = getClass.getResource(s"resources/$path.cgs")
    val components = name.split("\\.").toSeq
    val pkg = Package.root.lookup(components.dropRight(1)).getOrElse {
      sys.error("Cannot find package: " + components.dropRight(1))
    }
    pkg.declareClass(components.last, url, scalaClass)

  }

}

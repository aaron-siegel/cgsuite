package org.cgsuite.lang

import org.cgsuite.core._
import org.cgsuite.util.{Strip, Coordinates, Grid, Symmetry}

object Loader {

  val systemClasses: Map[String, Option[Class[_]]] = Map(

    "cgsuite.lang.Boolean" -> Some(classOf[Boolean]),
    "cgsuite.lang.Class" -> Some(classOf[ClassObject]),
    "cgsuite.lang.Coordinates" -> Some(classOf[Coordinates]),
    "cgsuite.lang.List" -> Some(classOf[Seq[_]]),
    "cgsuite.lang.Map" -> Some(classOf[Map[_, _]]),
    "cgsuite.lang.Object" -> Some(classOf[AnyRef]),
    "cgsuite.lang.Procedure" -> Some(classOf[Procedure]),
    "cgsuite.lang.Set" -> Some(classOf[Set[_]]),
    "cgsuite.lang.String" -> Some(classOf[String]),
    "cgsuite.lang.System" -> Some(classOf[System]),

    "cgsuite.util.Grid" -> Some(classOf[Grid]),
    "cgsuite.util.Strip" -> Some(classOf[Strip]),
    "cgsuite.util.Symmetry" -> Some(classOf[Symmetry]),

    "game.CanonicalShortGame" -> Some(classOf[CanonicalShortGame]),
    "game.DyadicRational" -> Some(classOf[DyadicRationalNumber]),
    "game.Game" -> Some(classOf[Game]),
    "game.GridGame" -> None,
    "game.Integer" -> Some(classOf[Integer]),
    "game.Nimber" -> Some(classOf[Nimber]),
    "game.NumberUpStar" -> Some(classOf[NumberUpStar]),
    "game.Player" -> Some(classOf[Player]),
    "game.Rational" -> Some(classOf[RationalNumber]),
    "game.StripGame" -> None,
    "game.Zero" -> Some(Zero.getClass),

    "game.grid.Amazons" -> None,
    "game.grid.Clobber" -> None,
    "game.grid.Domineering" -> None,
    "game.grid.Fission" -> None,

    "game.strip.ToadsAndFrogs" -> None

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
    val pkg = CgsuitePackage.root.lookupSubpackage(components.dropRight(1)).getOrElse {
      sys.error("Cannot find package: " + components.dropRight(1))
    }
    pkg.declareClass(Symbol(components.last), url, scalaClass)

  }

}

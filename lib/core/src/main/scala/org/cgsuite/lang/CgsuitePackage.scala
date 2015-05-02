package org.cgsuite.lang

import scala.collection.mutable
import java.net.URL

object CgsuitePackage {

  val root = new CgsuitePackage(None, "$root")

  val cgsuite = root.declareSubpackage("cgsuite")
  val lang = cgsuite.declareSubpackage("lang")
  val util = cgsuite.declareSubpackage("util")

  val game = root.declareSubpackage("game")
  val grid = game.declareSubpackage("grid")
  Loader.declareSystemResources()

  def lookupClass(name: String): Option[CgsuiteClass] = {
    lang.lookupClass(name).orElse(game.lookupClass(name)).orElse(util.lookupClass(name))
  }

  def lookupClass(path: Seq[String]): Option[CgsuiteClass] = {
    if (path.length == 1)
      lookupClass(path.head)
    else
      root.lookup(path.dropRight(1)).flatMap { _.lookupClass(path.last) }
  }

}

class CgsuitePackage(parent: Option[CgsuitePackage], name: String) {

  private val subpackages = mutable.Map[String, CgsuitePackage]()
  private val classes = mutable.Map[String, CgsuiteClass]()

  val path: Seq[String] = parent match {
    case None => Seq.empty
    case Some(p) => p.path :+ name
  }

  val qualifiedName = path mkString "."

  def declareSubpackage(name: String): CgsuitePackage = {
    subpackages.getOrElseUpdate(name, new CgsuitePackage(Some(this), name))
  }

  def lookup(path: Seq[String]): Option[CgsuitePackage] = {
    if (path.isEmpty) {
      Some(this)
    } else {
      subpackages.get(path.head).flatMap { _.lookup(path.tail) }
    }
  }

  def lookupClass(name: String): Option[CgsuiteClass] = classes.get(name)

  def declareClass(name: String, url: URL, scalaClass: Option[Class[_]]): CgsuiteClass = {
    val cls = classes.getOrElseUpdate(name, new CgsuiteClass(this, name, scalaClass))
    cls.setURL(url)
    cls
  }

}

package org.cgsuite.lang

import scala.collection.mutable
import java.net.URL

object Package {

  val root = new Package(None, "$root")
  val cgsuite = root.declareSubpackage("cgsuite")
  val lang = cgsuite.declareSubpackage("lang")
  val game = root.declareSubpackage("game")
  Loader.declareSystemResources()

  def lookupClass(name: String): Option[CgsuiteClass] = {
    lang.lookupClass(name).orElse(game.lookupClass(name))
  }

  def lookupClass(path: Seq[String]): Option[CgsuiteClass] = {
    if (path.length == 1)
      lookupClass(path.head)
    else
      root.lookup(path.dropRight(1)).flatMap { _.lookupClass(path.last) }
  }

}

class Package(parent: Option[Package], name: String) {

  private val subpackages = mutable.Map[String, Package]()
  private val classes = mutable.Map[String, CgsuiteClass]()

  val path: Seq[String] = parent match {
    case None => Seq.empty
    case Some(p) => p.path :+ name
  }

  val qualifiedName = path mkString "."

  def declareSubpackage(name: String): Package = {
    subpackages.getOrElseUpdate(name, new Package(Some(this), name))
  }

  def lookup(path: Seq[String]): Option[Package] = {
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

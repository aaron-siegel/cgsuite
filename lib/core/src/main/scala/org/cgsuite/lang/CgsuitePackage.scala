package org.cgsuite.lang

import scala.collection.mutable
import java.net.URL

object CgsuitePackage {

  private[lang] val classDictionary = mutable.Map[Symbol, CgsuiteClass]()

  val root = new CgsuitePackage(None, "$root")

  val cgsuite = root.declareSubpackage("cgsuite")
  val lang = cgsuite.declareSubpackage("lang")
  val util = cgsuite.declareSubpackage("util")

  val game = root.declareSubpackage("game")
  val grid = game.declareSubpackage("grid")

  Loader.declareSystemResources()

  def lookupClass(id: Symbol): Option[CgsuiteClass] = classDictionary.get(id)

  // Less efficient!
  def lookupClassByName(name: String): Option[CgsuiteClass] = classDictionary.get(Symbol(name))

}

class CgsuitePackage(parent: Option[CgsuitePackage], name: String) {

  private val subpackages = mutable.Map[String, CgsuitePackage]()
  private val classes = mutable.Map[Symbol, CgsuiteClass]()

  val path: Seq[String] = parent match {
    case None => Seq.empty
    case Some(p) => p.path :+ name
  }

  val qualifiedName = path mkString "."

  def declareSubpackage(name: String): CgsuitePackage = {
    subpackages.getOrElseUpdate(name, new CgsuitePackage(Some(this), name))
  }

  def lookupSubpackage(path: Seq[String]): Option[CgsuitePackage] = {
    if (path.isEmpty) {
      Some(this)
    } else {
      subpackages.get(path.head) flatMap { _.lookupSubpackage(path.tail) }
    }
  }

  def lookupClass(id: Symbol): Option[CgsuiteClass] = classes.get(id)

  def declareClass(id: Symbol, url: URL, scalaClass: Option[Class[_]]): CgsuiteClass = {
    val cls = classes.getOrElseUpdate(id, new CgsuiteClass(this, id, scalaClass))
    cls.setURL(url)
    CgsuitePackage.classDictionary.put(cls.qualifiedName, cls)
    if (this == CgsuitePackage.lang || this == CgsuitePackage.util || this == CgsuitePackage.game) {
      // TODO We should have a way to manage conflicts here.
      CgsuitePackage.classDictionary.put(id, cls)
    }
    cls
  }

}

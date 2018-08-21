package org.cgsuite.lang

import scala.collection.mutable
import java.net.URL

object CgscriptPackage {

  private[lang] val classDictionary = mutable.Map[Symbol, CgscriptClass]()

  val root = new CgscriptPackage(None, "$root")

  val cgsuite = root.declareSubpackage("cgsuite")
  val lang = cgsuite.declareSubpackage("lang")
  val util = cgsuite.declareSubpackage("util")
  val ui = cgsuite.declareSubpackage("ui")
  val output = util.declareSubpackage("output")

  val game = root.declareSubpackage("game")
  val grid = game.declareSubpackage("grid")
  val strip = game.declareSubpackage("strip")
  val heap = game.declareSubpackage("heap")

  def lookupClass(id: Symbol): Option[CgscriptClass] = classDictionary.get(id)

  def lookupConstant(id: Symbol): Option[Resolution] = {
    lang.lookupConstant(id) orElse util.lookupConstant(id) orElse game.lookupConstant(id)
  }

  // Less efficient!
  def lookupClassByName(name: String): Option[CgscriptClass] = classDictionary.get(Symbol(name))

}

class CgscriptPackage(parent: Option[CgscriptPackage], name: String) {

  private val subpackages = mutable.Map[String, CgscriptPackage]()
  private val classes = mutable.Map[Symbol, CgscriptClass]()

  val path: Seq[String] = parent match {
    case None => Seq.empty
    case Some(p) => p.path :+ name
  }

  val qualifiedName = path mkString "."

  def declareSubpackage(name: String): CgscriptPackage = {
    subpackages.getOrElseUpdate(name, new CgscriptPackage(Some(this), name))
  }

  def lookupSubpackage(path: Seq[String]): Option[CgscriptPackage] = {
    if (path.isEmpty) {
      Some(this)
    } else {
      subpackages.get(path.head) flatMap { _.lookupSubpackage(path.tail) }
    }
  }

  def lookupClass(id: Symbol): Option[CgscriptClass] = classes.get(id)

  def declareClass(id: Symbol, url: Option[URL], explicitDefinition: Option[String], scalaClass: Option[Class[_]]): CgscriptClass = {
    val cls = classes.getOrElseUpdate(id, new CgscriptClass(this, None, id, scalaClass))
    (url, explicitDefinition) match {
      case (Some(u), None) => cls setURL u
      case (None, Some(d)) => cls setExplicitDefinition d
      case _ => sys.error("Invalid arguments")
    }
    CgscriptPackage.classDictionary.put(cls.qualifiedId, cls)
    if (this == CgscriptPackage.lang || this == CgscriptPackage.util || this == CgscriptPackage.game) {
      // TODO We should have a way to manage conflicts here.
      CgscriptPackage.classDictionary.put(id, cls)
    }
    cls
  }

  def lookupConstant(id: Symbol): Option[Resolution] = {
    lookupClass('constants) flatMap { constantsCls =>
      Option(Resolver forId id findResolutionForClass constantsCls) match {
        case Some(res) if res.isResolvable => Some(res)
        case _ => None
      }
    }
  }

}

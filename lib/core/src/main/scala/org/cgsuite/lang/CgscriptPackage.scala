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
  val misere = game.declareSubpackage("misere")

  def lookupClass(id: Symbol): Option[CgscriptClass] = classDictionary.get(id)

  // TODO Separate dictionary for URLs?
  def lookupClass(url: URL): Option[CgscriptClass] = classDictionary.values find { _.url == url }

  def lookupConstant(id: Symbol): Option[Resolution] = {
    lang.lookupConstant(id) orElse util.lookupConstant(id) orElse game.lookupConstant(id)
  }

  // Less efficient!
  def lookupClassByName(name: String): Option[CgscriptClass] = classDictionary.get(Symbol(name))

}

case class CgscriptPackage(parent: Option[CgscriptPackage], name: String) {

  private val subpackages = mutable.Map[String, CgscriptPackage]()
  private val classes = mutable.Map[Symbol, CgscriptClass]()

  val path: Seq[String] = parent match {
    case None => Seq.empty
    case Some(p) => p.path :+ name
  }

  val qualifiedName = path mkString "."

  def isRoot: Boolean = parent.isEmpty

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

  def declareClass(id: Symbol, classdef: CgscriptClassDef, scalaClass: Option[Class[_]]): CgscriptClass = {

    classes.get(id) map { _.classdef } match {

      case Some(UrlClassDef(url)) =>
        val cls = classes(id)
        if (classdef != cls.classdef && !SystemClassRegistry.allSystemClasses.exists { _._1 == cls.qualifiedName }) {
          sys error s"Class conflict in package $name: ${id.name}"    // TODO Better error message
        }
        cls

      case _ =>
        val cls = new CgscriptClass(this, classdef, id, scalaClass)
        classes.put(id, cls)
        CgscriptPackage.classDictionary.put(cls.qualifiedId, cls)
        if (this == CgscriptPackage.lang || this == CgscriptPackage.util || this == CgscriptPackage.game || this == CgscriptPackage.ui) {
          CgscriptPackage.classDictionary.put(id, cls)
        }
        cls

    }

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

package org.cgsuite.lang

import scala.collection.mutable
import java.net.URL

object CgscriptPackage {

  private[lang] val classDictionary = mutable.Map[String, CgscriptClass]()

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

  def lookupClass(url: URL): Option[CgscriptClass] = classDictionary.values find { _.url contains url }

  def lookupClassByName(name: String): Option[CgscriptClass] = classDictionary.get(name)

  def lookupConstantMember(id: Symbol): Option[MemberResolution] = {
    lang.lookupConstantMember(id) orElse util.lookupConstantMember(id) orElse game.lookupConstantMember(id)
  }

  def allClasses = classDictionary.values.toVector.distinct sortBy { _.qualifiedName }

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

  def allKnownClasses = classes.values.toVector

  def declareSubpackage(name: String): CgscriptPackage = {
    subpackages.getOrElseUpdate(name, new CgscriptPackage(Some(this), name))
  }

  def lookupSubpackage(id: Symbol): Option[CgscriptPackage] = lookupSubpackage(Seq(id.name))

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

      case Some(UrlClassDef(_, url)) =>
        val cls = classes(id)
        if (classdef != cls.classdef && !CgscriptSystem.allSystemClasses.exists { _._1 == cls.qualifiedName }) {
          sys error s"Class conflict in package $name: ${id.name}"    // TODO Better error message
        }
        cls

      case _ =>
        assert(!classdef.isInstanceOf[NestedClassDef])
        val cls = new CgscriptClass(this, classdef, id, scalaClass)
        classes.put(id, cls)
        CgscriptPackage.classDictionary.put(cls.qualifiedName, cls)
        if (this == CgscriptPackage.lang || this == CgscriptPackage.util || this == CgscriptPackage.game || this == CgscriptPackage.ui) {
          CgscriptPackage.classDictionary.put(cls.nameInPackage, cls)
        }
        cls

    }

  }

  def lookupMember(id: Symbol): Option[MemberResolution] = {
    lookupConstantMember(id) orElse lookupClass(id)
  }

  def lookupConstantMember(id: Symbol): Option[MemberResolution] = {
    lookupClass(id = 'constants) flatMap { _.resolveInstanceMember(id) }
  }

}

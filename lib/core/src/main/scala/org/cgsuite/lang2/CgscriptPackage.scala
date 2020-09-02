package org.cgsuite.lang2

import scala.collection.mutable
import java.net.URL

object CgscriptPackage {

  private[lang2] val classDictionary = mutable.Map[Symbol, CgscriptClass]()

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

  def lookupConstantVar(id: Symbol): Option[Member] = {
    lang.lookupConstantVar(id) orElse util.lookupConstantVar(id) orElse game.lookupConstantVar(id)
  }

  def lookupConstantMethod(id: Symbol, argumentTypes: Vector[CgscriptType]): Option[CgscriptClass#Method] = {
    lang.lookupConstantMethod(id, argumentTypes) orElse util.lookupConstantMethod(id, argumentTypes) orElse game.lookupConstantMethod(id, argumentTypes)
  }

  // Less efficient!
  def lookupClassByName(name: String): Option[CgscriptClass] = classDictionary.get(Symbol(name))

  def allClasses = classDictionary.values.toVector.distinct sortBy { _.qualifiedName }

}

case class CgscriptPackage(parent: Option[CgscriptPackage], name: String) {

  private val subpackages = mutable.Map[String, CgscriptPackage]()
  private val classesLookup = mutable.Map[Symbol, CgscriptClass]()

  val allClasses = classesLookup.values

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

  def lookupClass(id: Symbol): Option[CgscriptClass] = classesLookup.get(id)

  def declareClass(id: Symbol, classdef: CgscriptClassDef, scalaClass: Option[Class[_]]): CgscriptClass = {

    classesLookup.get(id) map { _.classdef } match {

      case Some(UrlClassDef(_, url)) =>
        val cls = classesLookup(id)
        if (classdef != cls.classdef && !CgscriptSystem.allSystemClasses.exists { _._1 == cls.qualifiedName }) {
          sys error s"Class conflict in package $name: ${id.name}"    // TODO Better error message
        }
        cls

      case _ =>
        assert(!classdef.isInstanceOf[NestedClassDef])
        val cls = new CgscriptClass(this, classdef, id, scalaClass)
        classesLookup.put(id, cls)
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

  def lookupConstantMethod(id: Symbol, argumentTypes: Vector[CgscriptType]): Option[CgscriptClass#Method] = {
    lookupClass('constants) flatMap { constantsCls =>
      constantsCls.lookupMethod(id, argumentTypes)
    }
  }

  def lookupConstantVar(id: Symbol): Option[Member] = {
    lookupClass(id = 'constants) flatMap { constantsCls =>
      // TODO Nested classes
      constantsCls.lookupVar(id)
    }
  }

}

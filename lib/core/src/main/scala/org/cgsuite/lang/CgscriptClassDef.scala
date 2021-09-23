package org.cgsuite.lang

import java.net.URL

import org.cgsuite.lang.node.ClassDeclarationNode

sealed trait CgscriptClassDef

case class UrlClassDef(classpathRoot: better.files.File, url: URL) extends CgscriptClassDef

case class ExplicitClassDef(text: String) extends CgscriptClassDef

case class NestedClassDef(enclosingClass: CgscriptClass, declNode: ClassDeclarationNode) extends CgscriptClassDef

package org.cgsuite.lang

import java.net.URL

sealed trait CgscriptClassDef

case class UrlClassDef(classpathRoot: better.files.File, url: URL) extends CgscriptClassDef

case class ExplicitClassDef(text: String) extends CgscriptClassDef

case class NestedClassDef(enclosingClass: CgscriptClass) extends CgscriptClassDef

object LifecycleStage extends Enumeration {
  val New, DeclaringPhase1, DeclaredPhase1, DeclaringPhase2, Declared, Elaborated, Loaded, Unloaded = Value
}

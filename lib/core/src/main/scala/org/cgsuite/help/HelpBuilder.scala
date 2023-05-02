package org.cgsuite.help

import java.awt.Graphics2D
import java.awt.image.BufferedImage

import better.files._
import com.typesafe.scalalogging.Logger
import javax.imageio.ImageIO
import org.cgsuite.exception.CgsuiteException
import org.cgsuite.help.HelpBuilder._
import org.cgsuite.lang._
import org.cgsuite.output.Output
import org.slf4j.LoggerFactory

import scala.collection.mutable

object HelpBuilder {

  private[help] val logger = Logger(LoggerFactory.getLogger(classOf[HelpBuilder]))

  private[help] val packagePath = "org/cgsuite/help/docs"

  val preferredImageWidth = 500

  def main(args: Array[String]): Unit = {
    val resourcesDir = if (args.isEmpty) "src/main/resources" else ""
    val buildDir = if (args.isEmpty) "target/classes" else args.head
    val builder = HelpBuilder(resourcesDir.toFile, buildDir.toFile, externalBuild = false)
    builder.run()
  }

  def standardHeaderBar(backref: String) =
    s"""<div class="titlebar"><p><div class="section">
       |  <a href="${backref}contents.html#top">Contents</a>
       |  &nbsp;&nbsp;
       |  <a href="${backref}overview.html#top">Packages</a>
       |  &nbsp;&nbsp;
       |  <a href="${backref}cgscript-index.html#top">Index</a>
       |</div></div>
     """.stripMargin

  def anchorName(member: Member): String = {
    if (member.isStatic)
      s"static_${member.name}"
    else
      member.name
  }

}

case class HelpBuilder(resourcesDir: File, buildDir: File, externalBuild: Boolean) { thisHelpBuilder =>

  private[help] val srcDir = resourcesDir/packagePath

  private[help] val targetRootDir = if (externalBuild) buildDir else buildDir/packagePath

  private[help] val referenceDir = targetRootDir/"reference"

  private[help] val searchIndex = targetRootDir/"search-index.csv"

  private[help] val cgshFiles = allMatchingFiles(srcDir) { _.extension contains ".cgsh" }

  private var nextImageOrdinal = 0

  private var markdownErrors = false

  CgscriptClass.Object.ensureDeclared()

  private[help] val allClasses = CgscriptPackage.allClasses filter { cls =>
    cls.classdef match {
      case UrlClassDef(classpathRoot, _) => classpathRoot == CgscriptClasspath.systemDir
      case _ => false
    }
  }

  private[help] val allPackages = allClasses.map { _.pkg }.distinct

  // First pass to build targets mapping

  private[help] val fixedTargets = {
    Map("reference/cgscript-index" -> "Index", "reference/overview" -> "Packages", "license" -> "License") ++
    cgshFiles.map { cgshFile =>
      val title = cgshFile.lines.head
      val path = srcDir relativize cgshFile
      (path.toString stripSuffix ".cgsh", title)
    }.toMap
  }

  def run(): Unit = {

    val allHelpSources = resourcesDir.glob("**/*.{cgs,cgsh}")
    val lastModified = allHelpSources.toVector.map { _.lastModifiedTime.toEpochMilli }.max

    if (searchIndex.exists && searchIndex.lastModifiedTime.toEpochMilli >= lastModified) {
      println("Documentation is up to date.")
    } else {
      renderAll()
    }

  }

  def renderAll(): Unit = {

    searchIndex overwrite ""
    println("Rendering .cgsh files ...")
    renderCgshFiles()
    println("Rendering overview ...")
    renderOverview()
    println("Rendering index ...")
    renderIndex()
    println("Rendering classes ...")
    renderClasses()
    println("Rendering package members ...")
    renderPackageMembers()

    if (markdownErrors) {
      sys.error("There were markdown errors.")
    }

    println("Documentation successfully generated.")

  }

  def renderCgshFiles(): Unit = {

    cgshFiles foreach { cgshFile =>

      val relPath = srcDir relativize cgshFile.parent
      val targetDir = targetRootDir/relPath.toString
      val backPath = targetDir relativize targetRootDir
      val targetFile = targetDir/s"${cgshFile.nameWithoutExtension}.html"

      logger debug s"Generating file: $cgshFile -> $targetFile"

      val lines = cgshFile.lines
      val title = lines.head

      val backref = {
        if (backPath.toString == "")
          "."
        else
          backPath.toString
      }

      val linkBuilder = HelpLinkBuilder(targetRootDir, targetDir, externalBuild, backref + "/", fixedTargets, CgscriptPackage.root)

      generateMarkdown(targetFile, lines.tail mkString "\n", linkBuilder) match {

        case Some(markdown) =>
          val header =
            s"""<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
               |<html>
               |<head>
               |  <link rel="stylesheet" href="$backref/cgsuite.css" type="text/css">
               |  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
               |  <title>$title</title>
               |</head>
               |
               |<body><div class="spacer">
               |<!--<div class="blanksection">&nbsp;</div>
               |<p>
               |${standardHeaderBar(backref)}-->
               |<h1>$title</h1>
               |<div class="section">
               |
               |""".stripMargin

          val footer = s"\n\n${if (markdown.hasFooter) "" else "</div>"}<p></div></body></html>"

          targetFile overwrite header
          targetFile append markdown.text
          targetFile append footer

        case None =>
          logger.error(s"Failed to generate Markdown: $cgshFile")

      }

    }

  }

  def allMatchingFiles(path: File)(matcher: File => Boolean): Vector[File] = {

    if (path.isDirectory)
      path.children.toVector flatMap { allMatchingFiles(_)(matcher) }
    else if (matcher(path))
      Vector(path)
    else
      Vector.empty

  }

  def renderClasses(): Unit = {

    allClasses foreach { cls =>

      try {

        if (cls.classInfo != null) {
          ClassRenderer(cls).renderClass()
        }

      } catch {

        case exc: CgsuiteException =>
          logger warn s"Error rendering class `${cls.qualifiedName}`: `${exc.getMessage}`"

      }

    }

  }

  def renderPackageMembers(): Unit = {

    for {
      cls <- allClasses
      if cls.isPackageObject
      member <- cls.classInfo.allMembers
      if member.declaringClass == cls
    } {

      val packageDir = cls.pkg.path.foldLeft(referenceDir) { (file, pathComponent) => file/pathComponent }
      val file = packageDir/s"${cls.name}.${member.name}.html"
      val backref = "../" * (cls.pkg.path.length + 1)
      val linkBuilder = HelpLinkBuilder(targetRootDir, packageDir, externalBuild, backref, fixedTargets, cls.pkg, Some(cls))

      val packageStr = s"""<p><code class="big">package <a href="constants.html#top">${cls.pkg.qualifiedName}</a></code>"""
      val memberTypeStr = member match {
        case _: CgscriptClass#Method => "def"
        case _: CgscriptClass#Var => "var"
        case _ => sys.error(s"Unexpected member in package object: $member")
      }
      val parametersStr = member match {
        case method: CgscriptClass#Method => makeParameters(linkBuilder, method)
        case _ => ""
      }

      val header = s"""<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
         |<html>
         |<head>
         |  <link rel="stylesheet" href="${"../" * cls.pkg.path.length}../cgsuite.css" type="text/css">
         |  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         |  <title>${cls.pkg.qualifiedName}.${member.name}</title>
         |</head>
         |
         |<body><div class="spacer">
         |<!--${standardHeaderBar(backref)}
         |
         |<div class="blanksection">&nbsp;</div><br>-->
         |<p>
         |
         |$packageStr
         |<h1>${member.name}</h1>
         |
         |<p><div class="section">
         |  <code class="big">$memberTypeStr <b>${cls.pkg.qualifiedName}.${member.name}</b>$parametersStr</code>
         |</div></p>
         |
         |""".stripMargin

      file overwrite header
      file append makeMemberDetail(file, cls, linkBuilder, member, includeDecl = false)
      file append htmlFooter

    }

  }

  private case class ClassRenderer(cls: CgscriptClass) {

    val packageDir = cls.pkg.path.foldLeft(referenceDir) { (file, pathComponent) => file/pathComponent }

    val linkBuilder = HelpLinkBuilder(targetRootDir, packageDir, externalBuild, "../" * (cls.pkg.path.length + 1), fixedTargets, cls.pkg, Some(cls))

    val file = packageDir/s"${cls.name}.html"

    def renderClass(): Unit = {

      val relpath = targetRootDir relativize file

      logger debug s"Generating class `${cls.qualifiedName}` to $file"

      val header = makeHeader()

      val classComment = {
        for {
          node <- cls.declNode
          comment <- node.docComment
        } yield {
          s"""<div class="section">
             |${processDocComment(comment)}
             |</div>""".stripMargin
        }
      }

      val (classParameters, regularMembers) = cls.classInfo.allUnshadowedMembers filterNot { member =>
        // Don't include private members
        member.modifiers.hasPrivate ||
        // Don't include enum elements (they'll be handled separately)
        member.isInstanceOf[CgscriptClass#Var] && member.asInstanceOf[CgscriptClass#Var].isEnumElement
      } partition {
        case v: CgscriptClass#Var if v.isConstructorParam => true
        case _ => false
      }

      val members = {
        if (cls.isPackageObject)
          regularMembers.filter { _.declaringClass == cls } ++ cls.pkg.allClasses.filter { !_.isPackageObject }
        else
          regularMembers
      } sortBy { _.id.name }

      val (staticMembers, instanceMembers) = members partition { _.isStatic }

      val staticMembersWithoutConstructor = staticMembers filterNot { _.isInstanceOf[CgscriptClass#Constructor] }

      val enumElementSummary = {
        if (cls.classInfo.localEnumElements.isEmpty)
          ""
        else
          makeMemberSummary(cls, cls.classInfo.localEnumElements sortBy { _.id.name }, "<h2>Enum Elements</h2>")
      }

      val staticMemberSummary = {
        if (staticMembersWithoutConstructor.isEmpty)
          ""
        else
          makeMemberSummary(cls, staticMembersWithoutConstructor, "<h2>Static Members</h2>")
      }

      val parameterSummary = {
        if (classParameters.isEmpty)
          ""
        else
          makeMemberSummary(
            cls,
            classParameters sortBy { cls.classInfo.constructorParamVars.indexOf(_) },
            "<h2>Class Parameters</h2>"
          )
      }

      val memberSummary = makeMemberSummary(cls, instanceMembers filter {
        member => member.declaringClass == cls || member.declaringClass == null
      }, "<h2>Members</h2>")

      val prevMemberSummary = cls.properAncestors.reverse map { declaringClass =>
        val declaredMembers = instanceMembers filter { _.declaringClass == declaringClass }
        if (declaredMembers.isEmpty) {
          ""
        } else {
          makeMemberSummary(
            declaringClass,
            declaredMembers,
            s"<h3>Members inherited from ${linkBuilder hyperlinkToClass declaringClass}</h3>"
          )
        }
      }

      val enumElementDetails = cls.classInfo.localEnumElements sortBy { _.id.name } map makeMemberDetail mkString "\n<p>\n"

      val staticMemberDetails = staticMembersWithoutConstructor sortBy { _.id.name } map makeMemberDetail mkString "\n<p>\n"

      val memberDetails = (instanceMembers ++ classParameters) filter { _.declaringClass == cls } map makeMemberDetail mkString "\n<p>\n"

      packageDir.createDirectories()
      file overwrite header
      classComment foreach file.append
      file append parameterSummary
      file append enumElementSummary
      file append staticMemberSummary
      file append memberSummary
      prevMemberSummary foreach file.append
      if (!cls.isPackageObject) {
        if (enumElementDetails.nonEmpty) {
          file append "\n<h2>Enum Element Details</h2>\n\n"
          file append enumElementDetails
        }
        if (staticMemberDetails.nonEmpty) {
          file append "\n<h2>Static Member Details</h2>\n\n"
          file append staticMemberDetails
        }
        file append "\n<h2>Member Details</h2>\n\n"
        file append memberDetails
      }
      file append htmlFooter

      searchIndex appendLine s"${cls.name},${cls.qualifiedName},$relpath,0"
      searchIndex appendLine s"${cls.qualifiedName},${cls.qualifiedName},$relpath,1"
      members foreach { member =>
        if (member.declaringClass == cls) {
          searchIndex appendLine s"${cls.name}.${member.name},${cls.qualifiedName}.${member.name},$relpath#${anchorName(member)},0"
        }
      }

    }

    def makeHeader(): String = {

      val backref = "../" * (cls.pkg.path.length + 1)

      val modifiersStr = {
        if (cls.isPackageObject)
          ""
        else
          cls.classInfo.modifiers.allModifiers map { _.getText } mkString " "
      }

      val packageStr = {
        if (cls.isPackageObject)
          ""
        else
          s"""<p><code class="big">package <a class="valid" href="constants.html#top">${cls.pkg.qualifiedName}</a></code>"""
      }

      val classtypeStr = {
        if (cls.isPackageObject)
          "package"
        else
          "class"   // TODO enums
      }

      val supers = cls.classInfo.supers filterNot { _ == CgscriptClass.Object } map { sup =>
        s"${linkBuilder hyperlinkToClass sup}"
      }

      assert(supers.isEmpty || !cls.isPackageObject)

      val extendsStr = {
        if (supers.isEmpty)
          ""
        else
          " extends " + (supers mkString ", ")
      }

      s"""<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
         |<html>
         |<head>
         |  <link rel="stylesheet" href="${"../" * cls.pkg.path.length}../cgsuite.css" type="text/css">
         |  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         |  <title>${if (cls.isPackageObject) cls.pkg.qualifiedName else cls.qualifiedName}</title>
         |</head>
         |
         |<body><div class="spacer">
         |<!--${standardHeaderBar(backref)}
         |
         |<div class="blanksection">&nbsp;</div><br>-->
         |<p>
         |
         |$packageStr
         |<h1>${if (cls.isPackageObject) cls.pkg.qualifiedName else cls.name}</h1>
         |
         |<p><div class="section">
         |  <code class="big">$modifiersStr $classtypeStr <b>${if (cls.isPackageObject) cls.pkg.qualifiedName else cls.name}</b>${makeParameters(linkBuilder, cls)}$extendsStr</code>
         |</div></p>
         |
         |""".stripMargin

    }

    def makeMemberSummary(
      declaringClass: CgscriptClass,
      members: Seq[Member],
      header: String
    ): String = {

      val tableHeader =
        """<p><div class="section"><table class="members">
          |""".stripMargin

      val footer = "</table></div>\n"

      val rows = members filterNot { member =>

        // For package object, filter out constants (it'd just be a link to itself)
        declaringClass.isPackageObject && member.name == "constants"

      } map { member =>

        val etype = entityType(member)
        val name = member.idNode.id.name

        val memberLink = member match {

          case _ if member.declaringClass == null =>
            linkBuilder.hyperlinkToClass(member.asInstanceOf[CgscriptClass], textOpt = Some(name))

          case _ =>
            linkBuilder.hyperlinkToClass(declaringClass, Some(member), Some(name))
        }

        val memberSuffix = member match {
          case v: CgscriptClass#Var if v.isConstructorParam =>
            // Class param suffix such as " as Integer?"
            makeParameterSuffix(linkBuilder, v.asConstructorParam.get, showDefault = true)
          case _ =>
            // Method parameters suffix such as "(x as Integer, y as Integer)"
            makeParameters(linkBuilder, member)
        }

        val description = docCommentForMember(member) match {
          case Some((commentStr, _)) => processDocComment(commentStr, firstSentenceOnly = true)
          case None => "&nbsp;"
        }

        s"""  <tr>
           |    <td class="entitytype">
           |      <code class="big">$etype${"&nbsp;" * (5 - etype.length)}</code>
           |    </td>
           |    <td class="member">
           |      <code class="big">$memberLink$memberSuffix</code>
           |      <br>$description
           |    </td>
           |  </tr>
           |""".stripMargin

      }

      if (rows.isEmpty) {
        s"\n$header"
      } else {
        s"\n$header\n\n$tableHeader${rows mkString ""}$footer"
      }

    }

    def makeMemberDetail(member: Member) = {

      thisHelpBuilder.makeMemberDetail(file, cls, linkBuilder, member)

    }

    def processDocComment(comment: String, firstSentenceOnly: Boolean = false): String = {

      thisHelpBuilder.processDocComment(file, comment, linkBuilder, firstSentenceOnly)

    }

  }

  val htmlFooter =
    """<p>
      |</div></body>
      |</html>
      |""".stripMargin

  def renderOverview(): Unit = {

    val file = referenceDir/"overview.html"

    val header =
      s"""<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
         |<html>
         |<head>
         |  <link rel="stylesheet" href="../cgsuite.css" type="text/css">
         |  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         |  <title>Overview of CGScript Packages</title>
         |</head>
         |
         |<body><div class="spacer">
         |<!--${standardHeaderBar("")}
         |<div class="blanksection">&nbsp;</div><p>-->
         |<h1>Overview of CGScript Packages</h1>
         |<div class="section">
         |
         |<table class="members">
         |""".stripMargin

    val packages = allPackages map { pkg =>

      val memberLink = s"""<a class="valid" href="${pkg.path mkString "/"}/constants.html#top">${pkg.qualifiedName}</a>"""

      val description = {
        try {
          pkg.lookupClassInScope(Symbol("constants")) match {
            case Some(constants) =>
              constants.declNode flatMap { _.docComment } match {
                case Some(comment) => ClassRenderer(constants).processDocComment(comment, firstSentenceOnly = true)
                case None => "&nbsp;"
              }
            case None => "&nbsp;"
          }
        } catch {
          case exc: CgsuiteException =>
            logger warn s"Error rendering package `${pkg.qualifiedName}`: `${exc.getMessage}`"
        }
      }

      s"""  <tr>
         |    <td class="entitytype">
         |      $memberLink
         |    </td>
         |    <td class="member">
         |      $description
         |    </td>
         |  </tr>
       """.stripMargin

    }

    val footer = "</table></div><p></body></html>"

    referenceDir.createDirectories()
    file overwrite header
    packages foreach file.append
    file append footer

  }

  def makeMemberDetail(file: File, cls: CgscriptClass, linkBuilder: HelpLinkBuilder, member: Member, includeDecl: Boolean = true): String = {

    val name = member.idNode.id.name

    val memberSuffix = member match {
      case v: CgscriptClass#Var if v.isConstructorParam => makeParameterSuffix(linkBuilder, v.asConstructorParam.get, showDefault = true)
      case _ => makeParameters(linkBuilder, member)
    }

    val commentOpt = docCommentForMember(member)

    val comment = commentOpt match {
      case None => ""
      case Some((commentStr, ancestorClass)) =>
        val processedComment = processDocComment(file, commentStr, linkBuilder)
        val disclaimer = {
          if (cls == ancestorClass) ""
          else {
            val link = linkBuilder.hyperlinkToClass(ancestorClass)
            s"<em>(description copied from </em>$link<em>)</em>\n<p>"
          }
        }
        s"$disclaimer$processedComment"
    }

    val declStr = if (includeDecl) s"""<code class="big">${entityType(member)} <b>$name</b>$memberSuffix</code>\n<p>""" else ""

    s"""<a name="${anchorName(member)}"></a>
       |<p><div class="section">
       |  $declStr$comment
       |</div>""".stripMargin

  }

  def entityType(any: AnyRef): String = {
    any match {
      case _: CgscriptPackage => "package"
      case cls: CgscriptClass if cls.properAncestors.contains(CgscriptClass.Enum) => "enum"
      case _: CgscriptClass => "class"
      case _: CgscriptClass#Method => "def"
      case v: CgscriptClass#Var if v.isConstructorParam => "param"
      case _: CgscriptClass#Var => "var"
      case _ => sys.error("can't happen")
    }
  }

  // If the specified member has an associated doc comment, then return
  // it along with the class that the doc comment is actually copied from
  def docCommentForMember(member: Member): Option[(String, CgscriptClass)] = {

    member.declNode flatMap { _.docComment } match {

      case Some(comment) => Some((comment, member.declaringClass))

      case None if member.declaringClass != null =>
        // Traverse the ancestor tree of the member's declaring class in standard order
        // and pick the first occurrence of a doc comment.
        val docOccurrence = member.declaringClass.ancestors.findLast { ancestorClass =>
          val ancestorMemberOpt = ancestorClass.lookupMember(member.id)
          ancestorMemberOpt exists { ancestorMember =>
            ancestorMember.declaringClass == ancestorClass &&
              ancestorMember.declNode.flatMap { _.docComment }.nonEmpty
          }
        }
        docOccurrence map { ancestorClass =>
          (ancestorClass.lookupMember(member.id).get.declNode.get.docComment.get, ancestorClass)
        }

      case _ => None

    }

  }

  def makeParameters(linkBuilder: HelpLinkBuilder, member: Member): String = {
    member match {
      case _: CgscriptClass#Var => ""
      case method: CgscriptClass#Method =>
        if (method.autoinvoke) "" else makeParameters(linkBuilder, method.parameters)
      case nestedClass: CgscriptClass =>
        nestedClass.classInfo.constructor map { ctor => makeParameters(linkBuilder, ctor.parameters) } getOrElse ""
    }
  }

  def makeParameters(linkBuilder: HelpLinkBuilder, parameters: Seq[Parameter]): String = {

    val withoutParens = parameters map { param =>
      param.id.name + makeParameterSuffix(linkBuilder, param)
    } mkString ", "

    s"($withoutParens)"

  }

  def makeParameterSuffix(linkBuilder: HelpLinkBuilder, parameter: Parameter, showDefault: Boolean = false): String = {

    val asString = {
      if (parameter.paramType == CgscriptClass.Object)
        ""
      else
        " as " + linkBuilder.hyperlinkToClass(parameter.paramType)
    }

    val expandString = if (parameter.isExpandable) " ..." else ""

    val defaultString = parameter.defaultValue match {
      case None => ""
      case Some(default) => if (showDefault) " ? " + default.toNodeString else "?"
    }

    s"$asString$expandString$defaultString"

  }

  def processDocComment(file: File, comment: String, linkBuilder: HelpLinkBuilder, firstSentenceOnly: Boolean = false): String = {

    // TODO Links don't work quite right for copied doc comments
    // (Link GENERATION works fine, but link RESOLUTION does not)

    generateMarkdown(file, comment, linkBuilder, stripAsterisks = true, firstSentenceOnly = firstSentenceOnly) match {

      case Some(markdown) => markdown.text
      case None => logger.error(s"Failed to generate markdown: $file"); "&nbsp;"

    }

  }

  def renderIndex(): Unit = {

    val file = referenceDir/"cgscript-index.html"

    val header =
      s"""<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
         |<html>
         |<head>
         |  <link rel="stylesheet" href="../cgsuite.css" type="text/css">
         |  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         |  <title>Index of CGScript Classes</title>
         |</head>
         |
         |<body><div class="spacer">
         |<!--${standardHeaderBar("")}
         |<div class="blanksection">&nbsp;</div><p>-->
         |<h1>Index of CGScript Classes</h1>
         |<div class="section">
         |  This index lists all the packages, classes, methods, and constants that have top-level scope in CGScript.
         |</div>
         |<p>
         |<div class="section">
         |
         |<table class="members">
         |""".stripMargin

    val (constantsClasses, ordinaryClasses) = allClasses partition { _.name == "constants" }
    val constantsMembers = constantsClasses flatMap { _.classInfo.localMembers }
    val membersToSummarize = (ordinaryClasses ++ constantsMembers ++ allPackages) sortBy {
      case cls: CgscriptClass => (cls.pkg.qualifiedName, cls.name)
      case member: Member => (member.declaringClass.pkg.qualifiedName, member.name)
      case pkg: CgscriptPackage => (pkg.qualifiedName, "")
    }

    var packageMemberCount = 0

    val summaries = membersToSummarize.zipWithIndex map { case (member, index) =>

      // TODO There is surely a more elegant way to combine these cases
      val (memberLink, description) = member match {

        case cls: CgscriptClass =>
          (s"""<a class="valid" href="${cls.pkg.path mkString "/"}/${cls.name}.html#top">${cls.qualifiedName}</a>""",
            try {
              cls.declNode flatMap { _.docComment } match {
                case Some(comment) => ClassRenderer(cls).processDocComment(comment, firstSentenceOnly = true)
                case None => "&nbsp;"
              }
            } catch {
              case exc: CgsuiteException =>
                logger warn s"Error rendering class `${cls.qualifiedName}`: `${exc.getMessage}`"
            }
          )

        case member: Member =>
          (s"""<a class="valid" href="${member.declaringClass.pkg.path mkString "/"}/constants.${member.name}.html#top">${member.displayName}</a>""",
            try {
              docCommentForMember(member) match {
                case Some((commentStr, _)) =>
                  ClassRenderer(member.declaringClass).processDocComment(commentStr, firstSentenceOnly = true)
                case None => "&nbsp;"
              }
            } catch {
              case exc: CgsuiteException =>
                logger warn s"Error rendering member `${member.qualifiedName}`: `${exc.getMessage}`"
            }
          )

        case pkg: CgscriptPackage =>
          (s"""<a class="valid" href="${pkg.path mkString "/"}/constants.html#top">${pkg.qualifiedName}</a>""",
            try {
              val constantsClass = pkg.lookupClassInScope(Symbol("constants"))
              constantsClass flatMap { _.declNode } flatMap { _.docComment } match {
                case Some(comment) => ClassRenderer(constantsClass.get).processDocComment(comment, firstSentenceOnly = true)
                case None => "&nbsp;"
              }
            } catch {
              case exc: CgsuiteException =>
                logger warn s"Error rendering package `${pkg.qualifiedName}`: `${exc.getMessage}`"
            }
          )

      }

      val etype = entityType(member)

      val rowBreak = {
        // Put a row break before each package except the first.
        if (index > 0 && member.isInstanceOf[CgscriptPackage]) {
          if (packageMemberCount % 2 == 0) {
            // If index is even, put a double row-break (each half height) to ensure we "rectify the count".
            s"""  <tr class="rowsep1"><td colspan="3" class="rowsep"></td></tr><tr class="rowsep2"><td colspan="3" class="rowsep"></td></tr>"""
          } else {
            // Just a single row-break.
            s"""  <tr class="rowsep"><td colspan="3" class="rowsep"></td></tr>"""
          }
        } else {
          ""
        }
      }

      if (member.isInstanceOf[CgscriptPackage]) {
        packageMemberCount = 0
      }

      packageMemberCount += 1

      s"""  $rowBreak
         |  <tr>
         |    <td class="entitytype">
         |      <b>$etype</b>
         |    </td>
         |    <td class="entitytype">
         |      $memberLink
         |    </td>
         |    <td class="member">
         |      $description
         |    </td>
         |  </tr>
         |""".stripMargin

    }

    val footer = "</table></div><p></body></html>"

    referenceDir.createDirectories()
    file overwrite header
    summaries foreach file.append
    file append footer

  }

  var reportedImageException: Boolean = false

  def generateMarkdown(targetFile: File, rawInput: String, linkBuilder: LinkBuilder, stripAsterisks: Boolean = false, firstSentenceOnly: Boolean = false): Option[Markdown] = {
    try {
      if (!targetFile.parent.exists()) {
        targetFile.parent.createDirectories()
      }
      val markdown = Markdown(targetFile.nameWithoutExtension, rawInput, linkBuilder, nextImageOrdinal, stripAsterisks, firstSentenceOnly)
      val varMap = mutable.AnyRefMap[Symbol, Any]()
      markdown.evalStatements.zipWithIndex.foreach { case ((statement, scale, preferredImageWidth), ordinal) =>
        try {
          generateImages(targetFile, statement, scale, preferredImageWidth, nextImageOrdinal + ordinal, varMap)
        } catch {
          case exc: Throwable =>
            if (!reportedImageException) {
              logger.error(s"Could not generate image for statement: $statement", exc)
              reportedImageException = true
            } else {
              logger.error(s"Could not generate image for statement: $statement")
            }
            markdownErrors = true
        }
      }
      nextImageOrdinal += markdown.evalStatements.length;
      Some(markdown)
    } catch {
      case exc: Exception =>
        logger.error("Exception occurred processing markdown.", exc)
        markdownErrors = true
        None
    }
  }

  def generateImages(targetFile: File, statement: String, rescale: Double, preferredImageWidth: Int, ordinal: Int, varMap: mutable.AnyRefMap[Symbol, Any]): Unit = {
    val output = org.cgsuite.lang.System.evaluateOrException(statement, varMap)
    val outputFilePrefix = s"${targetFile.parent}/${targetFile.nameWithoutExtension}-$ordinal"
    if (output.nonEmpty) {
      for (scale <- Vector(1.0, 2.0)) {
        generateImage(output.last, outputFilePrefix, scale, rescale, preferredImageWidth)
      }
    }
  }

  def generateImage(output: Output, outputFilePrefix: String, scale: Double, rescale: Double, preferredImageWidth: Int): Unit = {
    val scaledImageWidth = (preferredImageWidth / rescale).toInt
    val size = output.getSize(scaledImageWidth)
    val image = new BufferedImage((size.width * scale * rescale).toInt, (size.height * scale * rescale).toInt, BufferedImage.TYPE_INT_ARGB)
    val g2d = image.getGraphics.asInstanceOf[Graphics2D]
    g2d.scale(scale * rescale, scale * rescale)
    output.paint(g2d, scaledImageWidth)
    val outputFile = new java.io.File(f"$outputFilePrefix-$scale%1.1fx.png")
    //ImageUtil.writeHiResImage(image, outputFile, "png", 144)
    ImageIO.write(image, "png", outputFile)
  }

}

case class HelpLinkBuilder(
  targetRootDir: File,
  targetDir: File,
  externalBuild: Boolean,
  backPath: String,
  fixedTargets: Map[String, String],
  referringPackage: CgscriptPackage,
  referringClass: Option[CgscriptClass] = None
  ) extends LinkBuilder {

  def hyperlink(ref: String, textOpt: Option[String]): String = {

    val resolution = resolveAsCgscriptRef(ref)

    resolution match {

      case (None, _) => hyperlinkForPath(ref, textOpt)
      case (Some(targetClass), targetMemberOpt) => hyperlinkToClass(targetClass, targetMemberOpt, textOpt)

    }

  }

  def hyperlinkForPath(ref: String, textOpt: Option[String]): String = {

    if (ref contains "://") {
      hyperlinkForExternalPath(ref, textOpt)
    } else {
      hyperlinkForInternalPath(ref, textOpt)
    }

  }

  def hyperlinkForExternalPath(ref: String, textOpt: Option[String]): String = {

    if (externalBuild) {
      s"""<a class="external" href="$ref">${textOpt getOrElse "??????"}</a>"""
    } else {
      s"""<a class="external" href="javascript:cgsuite.openExternal('$ref')">${textOpt getOrElse "??????"}</a> (external link)"""
    }

  }

  def hyperlinkForInternalPath(ref: String, textOpt: Option[String]): String = {

    val refFile = {
      if (ref startsWith "/")
        targetRootDir / (ref stripPrefix "/")
      else
        targetDir / ref
    }

    val canonicalPath = (targetRootDir relativize refFile).toString

    val (defaultText, htmlClass) = {
      if (fixedTargets contains canonicalPath) {
        (fixedTargets(canonicalPath), "valid")
      } else {
        logger warn s"Unknown reference: $ref"
        (ref, "unknown")
      }
    }

    val relativePath = targetDir relativize refFile

    s"""<a class="$htmlClass" href="$relativePath.html#top">${textOpt getOrElse defaultText}</a>"""

  }

  def hyperlinkToClass(targetClass: CgscriptClass, targetMemberOpt: Option[Member] = None, textOpt: Option[String] = None): String = {

    val classRef = relativeRef(targetClass)
    val classRefSuffix = targetMemberOpt match {
      case Some(member) if targetClass.isPackageObject => s".${member.name}.html#top"
      case Some(member) => s".html#${anchorName(member)}"
      case None => ".html#top"
    }
    val classDisplayName = if (targetClass.isPackageObject) targetClass.pkg.qualifiedName else targetClass.name
    val refText = targetMemberOpt match {
      case Some(member) if referringClass contains targetClass =>
        // Print just the member name
        member.idNode.id.name
      case Some(member) =>
        // Print the member name qualified with the class name
        s"$classDisplayName.${member.idNode.id.name}"
      case None => classDisplayName
    }
    val codePrefix = if (textOpt.isDefined) "" else """<code>"""
    val linkText = textOpt getOrElse s"$refText"
    val codeSuffix = if (textOpt.isDefined) "" else "</code>"
    s"""$codePrefix<a class="valid" href="$classRef$classRefSuffix">$linkText</a>$codeSuffix"""

  }

  def qualifiedRefName(targetClass: CgscriptClass): String = {
    if (referringPackage == targetClass.pkg)
      targetClass.name
    else
      targetClass.qualifiedName
  }

  def resolveAsCgscriptRef(ref: String): (Option[CgscriptClass], Option[Member]) = {
    if (ref contains "#")
      resolveAsMemberRef(ref)
    else
      resolveAsTopRef(ref)
  }

  def resolveAsTopRef(ref: String): (Option[CgscriptClass], Option[Member]) = {
    resolveAsClassRef(ref) match {
      case Some(cls) => (Some(cls), None)
      case None =>
        val id = Symbol(ref)
        val resolution = referringPackage lookupConstantInScope id orElse (CgscriptPackage lookupConstant id)
        resolution match {
          case Some(Resolution(cls, memberId, _)) =>
            cls lookupMember memberId match {
              case Some(member) => (Some(cls), Some(member))
              case None => (None, None)
            }

          case None => (None, None)
        }
    }
  }

  def resolveAsClassRef(ref: String): Option[CgscriptClass] = {
    val path = ref.split('.').toVector
    val classRef = path.length match {
      case 0 => referringClass
      case 1 => referringPackage lookupClassInScope Symbol(path.head) orElse (CgscriptPackage lookupClassByName path.head)
      case _ => CgscriptPackage.root.lookupSubpackage(path dropRight 1) flatMap { _ lookupClassInScope Symbol(path.last) }
    }
    classRef orElse {
      // Try it as a package (= constants class) instead
      CgscriptPackage.root lookupSubpackage path flatMap { _.lookupClassInScope(Symbol("constants")) }
    }
  }

  def resolveAsMemberRef(ref: String): (Option[CgscriptClass], Option[Member]) = {

    val parts = ref split "#"
    if (parts.length != 2)
      return (None, None)

    val clsName = parts(0)
    val memberId = Symbol(parts(1))

    val refcls = clsName match {
      case "" => referringClass
      case _ => resolveAsClassRef(clsName)
    }

    refcls match {
      case Some(clsref) =>
        clsref lookupMember memberId match {
          case Some(member) => (Some(clsref), Some(member))
          case None =>
            // Check for static. TODO This should be refactored.
            clsref.classInfo.staticVarLookup.get(memberId) match {
              case Some(staticMember) => (Some(clsref), Some(staticMember))
              case None =>
                clsref.lookupMethodGroup(memberId, asStatic = true) match {
                  case Some(staticMethodGroup) => (Some(clsref), Some(staticMethodGroup.methods.head))
                  case None => (None, None)
                }
            }
        }
      case None => (None, None)
    }

  }

  def relativeRef(targetClass: CgscriptClass): String = {

    if (referringPackage == targetClass.pkg)
      targetClass.name
    else
      backPath + "reference/" + pathTo(targetClass)

  }

  def pathTo(targetClass: CgscriptClass): String = {
    (targetClass.pkg.path mkString "/") + "/" + targetClass.name
  }

}

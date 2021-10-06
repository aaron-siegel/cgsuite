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
import org.cgsuite.util.{ImageUtil, LinkBuilder, Markdown}
import org.slf4j.LoggerFactory

import scala.collection.mutable

object HelpBuilder {

  private[help] val logger = Logger(LoggerFactory.getLogger(classOf[HelpBuilder]))

  private[help] val packagePath = "org/cgsuite/help/docs"

  val preferredImageWidth = 500

  def main(args: Array[String]): Unit = {
    val resourcesDir = if (args.isEmpty) "src/main/resources" else ""
    val buildDir = if (args.isEmpty) "target/classes" else args.head
    val builder = HelpBuilder(resourcesDir, buildDir)
    builder.run()
  }

  def standardHeaderBar(backref: String) =
    s"""<div class="titlebar"><p><div class="section">
       |  <a href="${backref}contents.html">Contents</a>
       |  &nbsp;&nbsp;
       |  <a href="${backref}reference/overview.html">Packages</a>
       |  &nbsp;&nbsp;
       |  <a href="${backref}reference/cgscript-index.html">Index</a>
       |</div></div>
     """.stripMargin

}

case class HelpBuilder(resourcesDir: String, buildDir: String) {

  private[help] val srcDir = resourcesDir/packagePath

  private[help] val targetRootDir = buildDir/packagePath

  private[help] val referenceDir = targetRootDir/"reference"

  private[help] val searchIndex = targetRootDir/"search-index.csv"

  private[help] val cgshFiles = allMatchingFiles(srcDir) { _.extension contains ".cgsh" }

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

    searchIndex overwrite ""
    renderCgshFiles()
    renderOverview()
    renderIndex()
    renderClasses()

  }

  def renderCgshFiles(): Unit = {

    cgshFiles foreach { cgshFile =>

      val relPath = srcDir relativize cgshFile.parent
      val targetDir = targetRootDir/relPath.toString
      val backPath = targetDir relativize targetRootDir
      val targetFile = targetDir/s"${cgshFile.nameWithoutExtension}.html"

      logger info s"Generating file: $cgshFile -> $targetFile"

      val lines = cgshFile.lines
      val title = lines.head

      val backref = {
        if (backPath.toString == "")
          "."
        else
          backPath.toString
      }

      val linkBuilder = HelpLinkBuilder(targetRootDir, targetDir, backref, fixedTargets, CgscriptPackage.root)
      val markdown = generateMarkdown(targetFile, lines.tail mkString "\n", linkBuilder)

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

  private case class ClassRenderer(cls: CgscriptClass) {

    val packageDir = cls.pkg.path.foldLeft(referenceDir) { (file, pathComponent) => file/pathComponent }

    val linkBuilder = HelpLinkBuilder(targetRootDir, packageDir, "../" * cls.pkg.path.length, fixedTargets, cls.pkg, Some(cls))

    val file = packageDir/s"${cls.name}.html"

    def renderClass(): Unit = {

      val relpath = targetRootDir relativize file

      logger info s"Generating class `${cls.qualifiedName}` to $file"

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

      val regularMembers = {
        cls.classInfo.localMembers
      }

      val members = {
        if (cls.isPackageObject)
          regularMembers.filter { _.declaringClass == cls } ++ cls.pkg.allClasses.filter { !_.isPackageObject }
        else
          regularMembers
      } sortBy { _.id.name }

      val enumElementSummary = {
        if (cls.classInfo.localEnumElements.isEmpty)
          ""
        else
          makeMemberSummary(cls, cls.classInfo.localEnumElements sortBy { _.id.name }, "<h2>Enum Elements</h2>")
      }

      val staticMemberSummary = {
        if (cls.classInfo.localStaticVars.isEmpty)
          ""
        else
          makeMemberSummary(cls, cls.classInfo.localStaticVars sortBy { _.id.name }, "<h2>Static Members</h2>")
      }

      val memberSummary = makeMemberSummary(cls, members filter {
        member => member.declaringClass == cls || member.declaringClass == null
      }, "<h2>All Members</h2>")

      // TODO Breadth-first order for ancestor tree?

      val allDeclaringClasses = members.filterNot { member =>
        member.declaringClass == null || member.declaringClass == cls
      }.map { _.declaringClass }.distinct sortBy { _.qualifiedName }

      val prevMemberSummary = allDeclaringClasses map { declaringClass =>
        val declaredMembers = members filter { _.declaringClass == declaringClass }
        makeMemberSummary(
          declaringClass,
          declaredMembers,
          s"<h3>Members inherited from ${linkBuilder hyperlinkToClass declaringClass}</h3>"
        )
      }

      val enumElementDetails = cls.classInfo.localEnumElements sortBy { _.id.name } map makeMemberDetail mkString "\n<p>\n"

      val staticMemberDetails = cls.classInfo.localStaticVars sortBy { _.id.name } map makeMemberDetail mkString "\n<p>\n"

      val memberDetails = members filterNot { _.declaringClass == null } map makeMemberDetail mkString "\n<p>\n"

      packageDir.createDirectories()
      file overwrite header
      classComment foreach file.append
      file append enumElementSummary
      file append staticMemberSummary
      file append memberSummary
      prevMemberSummary foreach file.append
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
      file append htmlFooter

      searchIndex appendLine s"${cls.name},${cls.qualifiedName},$relpath,0"
      searchIndex appendLine s"${cls.qualifiedName},${cls.qualifiedName},$relpath,1"
      members foreach { member =>
        if (member.declaringClass == cls) {
//          searchIndex appendLine s"${member.name},${cls.name}.${member.name},$relpath#${member.name}"
          searchIndex appendLine s"${cls.name}.${member.name},${cls.qualifiedName}.${member.name},$relpath#${member.name},0"
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
          s"""<p><code>package <a href="constants.html">${cls.pkg.qualifiedName}</a></code>"""
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
         |  <code>$modifiersStr $classtypeStr <b>${if (cls.isPackageObject) cls.pkg.qualifiedName else cls.name}</b>${makeParameters(cls)}$extendsStr</code>
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
        val memberLink = {
          if (member.declaringClass == null)
            linkBuilder.hyperlinkToClass(member.asInstanceOf[CgscriptClass], textOpt = Some(name))
          else
            linkBuilder.hyperlinkToClass(cls, Some(member), Some(name))
        }

        val description = docCommentForMember(member) match {
          case Some((commentStr, _)) => processDocComment(commentStr, firstSentenceOnly = true)
          case None => "&nbsp;"
        }

        s"""  <tr>
           |    <td class="entitytype">
           |      <code>$etype${"&nbsp;" * (5 - etype.length)}</code>
           |    </td>
           |    <td class="member">
           |      <code>$memberLink${makeParameters(member)}</code>
           |      <br>$description
           |    </td>
           |  </tr>
           |""".stripMargin

      }

      s"\n$header\n\n$tableHeader${rows mkString ""}$footer"

    }

    def makeMemberDetail(member: Member): String = {

      val name = member.idNode.id.name

      val parametersStr = makeParameters(member)

      val commentOpt = docCommentForMember(member)

      val comment = commentOpt match {
        case None => ""
        case Some((commentStr, ancestorClass)) =>
          val processedComment = processDocComment(commentStr)
          val disclaimer = {
            if (cls == ancestorClass) ""
            else {
              val link = linkBuilder.hyperlinkToClass(ancestorClass)
              s"<p><em>(description copied from </em>$link<em>)</em>\n"
            }
          }
          s"$disclaimer<p>$processedComment"
      }

      s"""<a name="$name"></a>
         |<p><div class="section">
         |  <code>${entityType(member)} <b>$name</b>$parametersStr</code>
         |  $comment
         |</div>""".stripMargin

    }

    def entityType(member: Member): String = {
      member match {
        case _: CgscriptClass => "class"
        case _: CgscriptClass#Method => "def"
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

    def makeParameters(member: Member): String = {
      member match {
        case _: CgscriptClass#Var => ""
        case method: CgscriptClass#Method =>
          if (method.autoinvoke) "" else makeParameters(method.parameters)
        case nestedClass: CgscriptClass =>
          nestedClass.classInfo.constructor map { ctor => makeParameters(ctor.parameters) } getOrElse ""
      }
    }

    def makeParameters(parameters: Seq[Parameter]): String = {

      val strings = parameters map { parameter =>

        val asString = {
          if (parameter.paramType == CgscriptClass.Object)
            ""
          else
            " as " + linkBuilder.hyperlinkToClass(parameter.paramType)
        }

        val expandString = if (parameter.isExpandable) " ..." else ""

        val defaultString = parameter.defaultValue match {
          case None => ""
          case Some(default) => " ? " + default.toNodeString
        }

        s"${parameter.id.name}$asString$expandString$defaultString"

      }

      s"(${strings mkString ", "})"

    }

    def processDocComment(comment: String, firstSentenceOnly: Boolean = false): String = {

      // TODO Links don't work quite right for copied doc comments
      // (Link GENERATION works fine, but link RESOLUTION does not)

      generateMarkdown(file, comment, linkBuilder, stripAsterisks = true, firstSentenceOnly = firstSentenceOnly).text

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

      val memberLink = s"""<a class="valid" href="${pkg.path mkString "/"}/constants.html">${pkg.qualifiedName}</a>"""

      val description = {
        try {
          pkg.lookupClass(Symbol("constants")) match {
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
         |
         |<table class="members">
         |""".stripMargin

    // TODO Package constants too

    val classes = allClasses map { cls =>

      val memberLink = s"""<a class="valid" href="${cls.pkg.path mkString "/"}/${cls.name}.html">${cls.qualifiedName}</a>"""

      val description = {
        try {
          cls.declNode flatMap { _.docComment } match {
            case Some(comment) => ClassRenderer(cls).processDocComment(comment, firstSentenceOnly = true)
            case None => "&nbsp;"
          }
        } catch {
          case exc: CgsuiteException =>
            logger warn s"Error rendering class `${cls.qualifiedName}`: `${exc.getMessage}`"
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
    classes foreach file.append
    file append footer

  }

  var reportedImageException: Boolean = false

  def generateMarkdown(targetFile: File, rawInput: String, linkBuilder: LinkBuilder, stripAsterisks: Boolean = false, firstSentenceOnly: Boolean = false): Markdown = {
    val markdown = Markdown(targetFile.nameWithoutExtension, rawInput, linkBuilder, stripAsterisks, firstSentenceOnly)
    val varMap = mutable.AnyRefMap[Symbol, Any]()
    markdown.execStatements.zipWithIndex.foreach { case (statement, ordinal) =>
      try {
        generateImages(targetFile, statement, ordinal, varMap)
      } catch {
        case exc: Throwable =>
          if (!reportedImageException) {
            logger.error(s"Could not generate image for statement: $statement", exc)
            reportedImageException = true
          } else {
            logger.error(s"Could not generate image for statement: $statement")
          }
      }
    }
    markdown
  }

  def generateImages(targetFile: File, statement: String, ordinal: Int, varMap: mutable.AnyRefMap[Symbol, Any]): Unit = {
    val output = org.cgsuite.lang.System.evaluateOrException(statement, varMap)
    val outputFilePrefix = s"${targetFile.parent}/${targetFile.nameWithoutExtension}-$ordinal"
    if (output.nonEmpty) {
      for (scale <- Vector(1.0, 2.0)) {
        generateImage(output.last, outputFilePrefix, scale)
      }
    }
  }

  def generateImage(output: Output, outputFilePrefix: String, scale: Double): Unit = {
    val size = output.getSize(HelpBuilder.preferredImageWidth)
    val image = new BufferedImage((size.width * scale).toInt, (size.height * scale).toInt, BufferedImage.TYPE_INT_ARGB)
    val g2d = image.getGraphics.asInstanceOf[Graphics2D]
    g2d.scale(scale, scale)
    output.paint(g2d, HelpBuilder.preferredImageWidth)
    val outputFile = new java.io.File(f"$outputFilePrefix-$scale%1.1fx.png")
    //ImageUtil.writeHiResImage(image, outputFile, "png", 144)
    ImageIO.write(image, "png", outputFile)
  }

}

case class HelpLinkBuilder(
  targetRootDir: File,
  targetDir: File,
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

    s"""<a class="valid" href="javascript:cgsuite.openExternal('$ref')">${textOpt getOrElse "??????"}</a>"""

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

    s"""<a class="$htmlClass" href="$relativePath.html">${textOpt getOrElse defaultText}</a>"""

  }

  def hyperlinkToClass(targetClass: CgscriptClass, targetMemberOpt: Option[Member] = None, textOpt: Option[String] = None): String = {

    val classRef = relativeRef(targetClass)
    val memberRef = targetMemberOpt match {
      case Some(member) => s"#${member.idNode.id.name}"
      case None => ""
    }
    val refText = {
      if (referringClass contains targetClass) {
        targetMemberOpt match {
          case Some(member) => member.idNode.id.name
          case None => targetClass.name
        }
      } else {
        targetMemberOpt match {
          case Some(member) => s"${qualifiedRefName(targetClass)}.${member.idNode.id.name}"
          case None => qualifiedRefName(targetClass)
        }
      }
    }
    val codePrefix = if (textOpt.isDefined) "" else "<code>"
    val linkText = textOpt getOrElse s"$refText"
    val codeSuffix = if (textOpt.isDefined) "" else "</code>"
    s"""$codePrefix<a class="valid" href="$classRef$memberRef">$linkText</a>$codeSuffix"""

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
      (resolveAsClassRef(ref), None)
  }

  def resolveAsClassRef(ref: String): Option[CgscriptClass] = {
    if (ref == "")
      referringClass
    else
      referringPackage lookupClass Symbol(ref) orElse (CgscriptPackage lookupClassByName ref)
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
          case None => (None, None)
        }
      case None => (None, None)
    }

  }

  def relativeRef(targetClass: CgscriptClass): String = {

    if (referringClass contains targetClass)
      ""
    else if (referringPackage == targetClass.pkg)
      targetClass.name + ".html"
    else
      backPath + pathTo(targetClass)

  }

  def pathTo(targetClass: CgscriptClass): String = {
    (targetClass.pkg.path mkString "/") + "/" + targetClass.name + ".html"
  }

}

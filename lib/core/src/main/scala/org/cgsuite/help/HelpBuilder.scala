package org.cgsuite.help

import better.files._
import com.typesafe.scalalogging.Logger
import org.cgsuite.exception.CgsuiteException
import org.cgsuite.help.HelpBuilder._
import org.cgsuite.lang._
import org.cgsuite.util.{LinkBuilder, Markdown}
import org.slf4j.LoggerFactory

object HelpBuilder {

  private[help] val logger = Logger(LoggerFactory.getLogger(classOf[HelpBuilder]))

  private[help] val packagePath = "org/cgsuite/help/docs"

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

  private[help] val cgshFiles = allMatchingFiles(srcDir) { _.extension contains ".cgsh" }

  CgscriptSystem.evaluate("0")

  private[help] val allClasses = CgscriptPackage.allClasses filter { cls =>
    cls.classdef match {
      case UrlClassDef(classpathRoot, _) => classpathRoot == CgscriptClasspath.systemDir
      case _ => false
    }
  }

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

    renderCgshFiles()
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
      val markdown = Markdown(lines.tail mkString "\n", linkBuilder)

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

        if (cls.classInfo != null)
          ClassRenderer(cls).renderClass()

      } catch {

        case exc: CgsuiteException =>
          logger warn s"Error rendering class `${cls.qualifiedName}`: `${exc.getMessage}`"

      }

    }

  }

  private case class ClassRenderer(cls: CgscriptClass) {

    val packageDir = cls.pkg.path.foldLeft(referenceDir) { (file, pathComponent) => file/pathComponent }

    val linkBuilder = HelpLinkBuilder(targetRootDir, packageDir, "../" * cls.pkg.path.length, fixedTargets, cls.pkg, Some(cls))

    def renderClass(): Unit = {

      val file = packageDir/s"${cls.name}.html"

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

      val regularMembers = cls.classInfo.localMembers

      val members = {
        if (cls.isConstantsClass)
          regularMembers.filter { _.declaringClass == cls } ++ cls.pkg.allKnownClasses
        else
          regularMembers
      } sortBy { _.id.name }

      val enumElementSummary = {
        if (cls.classInfo.enumElements.isEmpty)
          ""
        else
          makeMemberSummary(cls, cls.classInfo.enumElements sortBy { _.id.name }, "<h2>Enum Elements</h2>")
      }

      val staticMemberSummary = {
        if (cls.classInfo.staticVars.isEmpty)
          ""
        else
          makeMemberSummary(cls, cls.classInfo.staticVars.values.toVector sortBy { _.id.name }, "<h2>Static Members</h2>")
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

      val enumElementDetails = cls.classInfo.enumElements sortBy { _.id.name } map makeMemberDetail mkString "\n<p>\n"

      val staticMemberDetails = cls.classInfo.staticVars.values.toVector sortBy { _.id.name } map makeMemberDetail mkString "\n<p>\n"

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

    }

    def makeHeader(): String = {

      val backref = "../" * (cls.pkg.path.length + 1)

      val modifiersStr = {
        if (cls.isConstantsClass)
          ""
        else
          cls.classInfo.modifiers.allModifiers map { _.getText } mkString " "
      }

      val packageStr = {
        if (cls.isConstantsClass)
          ""
        else
          s"""<p><code>package <a href="constants.html">${cls.pkg.qualifiedName}</a></code>"""
      }

      val classtypeStr = {
        if (cls.isConstantsClass)
          "package"
        else
          "class"   // TODO enums
      }

      val typedClassname = {
        if (cls.isConstantsClass) {
          cls.pkg.qualifiedName
        } else {
          cls.typeParameters.length match {
            case 0 => s"<b>${cls.name}</b>"
            case 1 => s"<b>${cls.name}</b> of " + makeType(cls.typeParameters.head)
            case 2 =>
              val typeParametersString = cls.typeParameters map makeType mkString ", "
              s"<b>${cls.name}</b> of ($typeParametersString)"
          }
        }
      }

      val supers = cls.classInfo.supers filterNot { _ == CgscriptClass.Object } map { sup =>
        s"${linkBuilder hyperlinkToClass sup}"
      }

      assert(supers.isEmpty || !cls.isConstantsClass)

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
         |  <title>${if (cls.isConstantsClass) cls.pkg.qualifiedName else cls.qualifiedName}</title>
         |</head>
         |
         |<body><div class="spacer">
         |<!--${standardHeaderBar(backref)}
         |
         |<div class="blanksection">&nbsp;</div><br>-->
         |<p>
         |
         |$packageStr
         |<h1>${if (cls.isConstantsClass) cls.pkg.qualifiedName else cls.name}</h1>
         |
         |<p><div class="section">
         |  <code>$modifiersStr $classtypeStr $typedClassname${makeParameters(cls)}$extendsStr</code>
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

      val rows = members map { member =>

        val etype = entityType(member)
        val name = member.id.name
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

      val name = member.id.name

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
          val docOccurrences = member.declaringClass.ancestors.reverse flatMap { ancestorClass =>
            val ancestorMemberResolution = ancestorClass.resolveMember(member.id)
            val ancestorMember = (ancestorMemberResolution, member) match {
              case (Some(methodGroup: CgscriptClass#MethodGroup), method: CgscriptClass#Method) =>
                methodGroup.methods find { _.method.parameters == method.parameters } map { _.method }
              case (Some(m: Member), _) => Some(m)
              case _ => None
            }
            ancestorMember flatMap { _.declNode flatMap { _.docComment } } map { (_, ancestorClass) }
          }
          docOccurrences.headOption

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
            " as " + makeType(parameter.paramType)
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

    def makeType(typ: CgscriptType): String = {

      typ match {

        case TypeVariable(symbol, isExpandable) =>
          val expander = if (isExpandable) "*" else ""
          s"${symbol.name}$expander"

        case ConcreteType(baseClass, typeArguments) =>
          val classLink = linkBuilder.hyperlinkToClass(baseClass)
          typeArguments.length match {
            case 0 => classLink
            case 1 => s"$classLink of ${makeType(typeArguments.head)}"
            case _ =>
              val typeArgumentsString = typeArguments map makeType mkString ", "
              s"$classLink of ($typeArgumentsString)"
          }

      }

    }

    def processDocComment(comment: String, firstSentenceOnly: Boolean = false): String = {

      // TODO Links don't work quite right for copied doc comments
      // (Link GENERATION works fine, but link RESOLUTION does not)

      Markdown(comment, linkBuilder, stripAsterisks = true, firstSentenceOnly = firstSentenceOnly).text

    }

  }

  val htmlFooter =
    """<p>
      |</div></body>
      |</html>
      |""".stripMargin

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

      case (None, _) => hyperlinkToPath(ref, textOpt)
      case (Some(targetClass), targetMemberOpt) => hyperlinkToClass(targetClass, targetMemberOpt, textOpt)

    }

  }

  def hyperlinkToPath(ref: String, textOpt: Option[String]): String = {

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
      case Some(member) => s"#${member.id.name}"
      case None => ""
    }
    val refText = {
      if (referringClass contains targetClass) {
        targetMemberOpt match {
          case Some(member) => member.id.name
          case None => targetClass.name
        }
      } else {
        targetMemberOpt match {
          case Some(member) => s"${qualifiedRefName(targetClass)}.${member.id.name}"
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
        clsref resolveMember memberId match {
          case Some(member: Member) => (Some(clsref), Some(member))
          case Some(methodGroup: CgscriptClass#MethodGroup) => (None, None)       // TODO Handle methods
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

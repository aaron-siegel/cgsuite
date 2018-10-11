package org.cgsuite.help

import better.files._
import com.typesafe.scalalogging.Logger
import org.cgsuite.exception.SyntaxException
import org.cgsuite.lang.{CgscriptClass, CgscriptPackage, Member, Parameter}
import org.cgsuite.util.Markdown
import org.slf4j.LoggerFactory

object HelpBuilder {

  private[help] val logger = Logger(LoggerFactory.getLogger(getClass))

  private[help] val rootDir = "src"/"main"/"gen"/"org"/"cgsuite"/"help"/"docs"

  def run(): Unit = {

    CgscriptClass.Object.ensureDeclared()

    CgscriptPackage.allClasses foreach { cls =>

      try {

        if (cls.classInfo != null)
          renderClass(cls)

      } catch {

        case exc: SyntaxException =>
          logger.warn(s"Syntax error parsing class: `${cls.qualifiedName}`")

      }

    }

  }

  def renderClass(cls: CgscriptClass): Unit = {

    val packageDir = cls.pkg.path.foldLeft(rootDir) { (file, pathComponent) => file/pathComponent }

    val file = packageDir/s"${cls.name}.html"

    logger info s"Generating class `${cls.qualifiedName}` to $file"

    val header = makeHeader(cls)

    val classComment = {
      for {
        node <- cls.declNode
        comment <- node.docComment
      } yield {
        processDocComment(cls, comment)
      }
    }

    val members = cls.classInfo.allNonSuperMembersInScope.values.toVector sortBy { _.idNode.id.name }

    val memberInfo = members map { member =>
      val entityType = member match {
        case _: CgscriptClass => "class"
        case _: CgscriptClass#Method => "def"
        case _: CgscriptClass#Var => "var"
        case _ => sys.error("can't happen")
      }
      val declaringLink = hyperlink(cls, member.declaringClass.qualifiedName, None)
      MemberInfo(member, entityType, declaringLink)
    }

    val memberSummary = makeMemberSummary(cls, memberInfo)

    val memberDetails = memberInfo map { makeMemberDetail(cls, _) } mkString "\n<p>&nbsp;<p>\n"

    packageDir.createDirectories()
    file overwrite htmlHeader
    file append header
    classComment foreach file.append
    file append memberSummary
    file append "<h2>Member Details</h2>\n\n"
    file append memberDetails
    file append htmlFooter

  }

  def makeHeader(cls: CgscriptClass): String = {

    val modifiersStr = cls.classInfo.modifiers.allModifiers map { _.getText } mkString " "

    val classtypeStr = {
      "class"   // TODO enums
    }

    val supers = cls.classInfo.supers filterNot { _ == CgscriptClass.Object } map { sup =>
      hyperlink(cls, sup.qualifiedName, None)
    }

    val extendsStr = {
      if (supers.isEmpty)
        ""
      else
        " extends " + (supers mkString ", ")
    }

    s"""<title>${cls.qualifiedName}</title>
       |</head><body>
       |<code>package ${cls.pkg.qualifiedName}</code>
       |<h1>${cls.name}</h1>
       |<p>
       |<code>$modifiersStr $classtypeStr <b>${cls.name}</b>$extendsStr</code>
       |<p>
     """.stripMargin

  }

  def makeMemberSummary(cls: CgscriptClass, members: Vector[MemberInfo]) = {

    val header =
      s"""<h2>All Members</h2>
         |<table>
         |  <tr>
         |    <th colspan="2">Member</th>
         |    <th>Description</th>
         |    <th>Defined In</th>
         |  </tr>
         |""".stripMargin

    val footer = "</table>"

    val rows = members map { info =>

      val name = info.member.idNode.id.name
      val memberLink = hyperlink(cls, s"${info.member.declaringClass.qualifiedName}.$name", Some(name))

      val description = info.member.declNode flatMap { _.docComment } match {
        case Some(comment) => processDocComment(cls, comment, firstSentenceOnly = true)
        case None => ""
      }

      s"""  <tr>
         |    <td><code>${info.entityType}&nbsp;</code></td>
         |    <td><code>$memberLink</code></td>
         |    <td>$description</td>
         |    <td><code>${info.declarationLink}</code></td>
         |  </tr>
       """.stripMargin

    }

    s"$header${rows mkString ""}$footer"

  }

  def makeMemberDetail(cls: CgscriptClass, info: MemberInfo): String = {

    val name = info.member.idNode.id.name

    val parametersStr = info.member match {
      case _: CgscriptClass#Var => ""
      case method: CgscriptClass#Method =>
        if (method.autoinvoke) "" else makeParameters(cls, method.parameters)
      case nestedClass: CgscriptClass =>
        nestedClass.classInfo.constructor map { ctor => makeParameters(cls, ctor.parameters) } getOrElse ""
    }

    val disclaimer = {
      if (cls == info.member.declaringClass) {
        ""
      } else {
        val link = hyperlink(cls, info.member.declaringClass.qualifiedName, None)
        s"<p><em>(description copied from </em>$link<em>)</em>"
      }
    }

    val comment = {
      for {
        node <- info.member.declNode
        comment <- node.docComment
      } yield {
        "<p>\n" + processDocComment(cls, comment)
      }
    } getOrElse ""

    s"""<a name="$name"/>
       |<code>${info.entityType} <b>$name</b>$parametersStr</code>
       |$disclaimer
       |$comment""".stripMargin

  }

  def makeParameters(cls: CgscriptClass, parameters: Seq[Parameter]): String = {

    val strings = parameters map { parameter =>

      val asString = {
        if (parameter.paramType == CgscriptClass.Object)
          ""
        else
          " as " + hyperlink(cls, parameter.paramType.qualifiedName, None)
      }

      val expandString = if (parameter.isExpandable) " ..." else ""

      val defaultString = parameter.defaultValue match {
        case None => ""
        case Some(default) => default.toNodeString
      }

      s"(${parameter.id.name}$asString$expandString$defaultString)"

    }

    strings mkString ", "

  }

  def processDocComment(cls: CgscriptClass, comment: String, firstSentenceOnly: Boolean = false): String = {

    val markdown = Markdown(comment, stripAsterisks = true)
    var result = {
      if (firstSentenceOnly) {
        markdown.text indexOf '.' match {
          case -1 => markdown.text
          case index => markdown.text substring (0, index + 1)
        }
      } else {
        markdown.text
      }
    }

    markdown.links.zipWithIndex foreach { case ((ref, textOpt), index) =>

      val link = hyperlink(cls, ref, textOpt)
      result = result replaceFirst (f"@@$index%04d", link)

    }

    result

  }

  def hyperlink(cls: CgscriptClass, ref: String, text: Option[String]): String = {

    val resolution = resolveRef(cls, ref)

    resolution match {

      case (Some(refcls), memberOpt) =>
        val classRef = relativeRef(cls, refcls)
        val memberRef = memberOpt match {
          case Some(member) => s"#${member.idNode.id.name}"
          case None => ""
        }
        val refText = {
          if (cls == refcls && memberOpt.isDefined)
            memberOpt.get.idNode.id.name
          else if (cls == refcls)
            cls.name
          else if (memberOpt.isDefined)
            s"${qualifiedRefName(cls, refcls)}.${memberOpt.get.idNode.id.name}"
          else
            qualifiedRefName(cls, refcls)
        }
        val linkText = text getOrElse s"<code>$refText</code>"
        s"""<a href="$classRef$memberRef">$linkText</a>"""

      case (None, _) => text getOrElse ref   // TODO Markup in red?

    }

  }

  def relativeRef(cls: CgscriptClass, refcls: CgscriptClass): String = {

    if (cls == refcls)
      ""
    else if (cls.pkg == refcls.pkg)
      refcls.name + ".html"
    else
      "../" * cls.pkg.path.length + (refcls.pkg.path mkString "/") + "/" + refcls.name + ".html"

  }

  def qualifiedRefName(cls: CgscriptClass, refcls: CgscriptClass): String = {

    if (cls.pkg == refcls.pkg)
      refcls.name
    else
      refcls.qualifiedName

  }

  def resolveRef(cls: CgscriptClass, ref: String): (Option[CgscriptClass], Option[Member]) = {
    resolveClassRef(cls, ref) match {
      case Some(clsref) => (Some(clsref), None)
      case None => resolveMemberRef(cls, ref)
    }
  }

  def resolveClassRef(cls: CgscriptClass, ref: String): Option[CgscriptClass] = {
    cls.pkg lookupClass Symbol(ref) orElse (CgscriptPackage lookupClassByName ref)
  }

  def resolveMemberRef(cls: CgscriptClass, ref: String): (Option[CgscriptClass], Option[Member]) = {

    val lastDot = ref lastIndexOf '.'
    if (lastDot == -1)
      return (None, None)

    val clsName = ref.substring(0, lastDot)
    val memberId = Symbol(ref.substring(lastDot + 1))
    resolveClassRef(cls, clsName) match {
      case Some(clsref) =>
        clsref lookupMember memberId match {
          case Some(member) => (Some(clsref), Some(member))
          case None => (None, None)
        }
      case None => (None, None)
    }

  }

  def main(args: Array[String]): Unit = run()

  val htmlHeader = {
    """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
      |<html><head>
      |<link rel="stylesheet" href="nbdocs:/org/cgsuite/help/docs/cgsuite.css" type="text/css">
      |<style type="text/css">
      |code { font-size: 13pt; }
      |ul { list-style-type: disc; list-style-image: none; list-style-position: outside; }
      |</style>
      |<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      |""".stripMargin
  }

  val htmlFooter =
    """</body>
      |""".stripMargin

}

case class MemberInfo(member: Member, entityType: String, declarationLink: String)

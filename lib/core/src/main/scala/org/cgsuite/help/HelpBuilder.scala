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
        s"""<div class="section">
           |${processDocComment(cls, comment)}
           |</div>""".stripMargin
      }
    }

    val regularMembers = {
      cls.classInfo.allNonSuperMembersInScope.values.toVector
    }

    val members = {
      if (cls.isPackageObject)
        regularMembers.filter { _.declaringClass == cls } ++ cls.pkg.allClasses
      else
        regularMembers
    } sortBy { _.idNode.id.name }

    val memberInfo = members map { member =>
      val entityType = member match {
        case _: CgscriptClass => "class"
        case _: CgscriptClass#Method => "def"
        case _: CgscriptClass#Var => "var"
        case _ => sys.error("can't happen")
      }
      MemberInfo(member, entityType)
    }

    val memberSummary = makeMemberSummary(cls, cls, memberInfo filter {
      info => info.member.declaringClass == cls || info.member.declaringClass == null
    })

    // TODO Breadth-first order for ancestor tree?

    val allDeclaringClasses = memberInfo.filterNot { info =>
      info.member.declaringClass == null || info.member.declaringClass == cls
    }.map { _.member.declaringClass }.distinct sortBy { _.qualifiedName }

    val prevMemberSummary = allDeclaringClasses map { declaringClass =>
      val declaredMembers = memberInfo filter { _.member.declaringClass == declaringClass }
      makeMemberSummary(cls, declaringClass, declaredMembers)
    }

    val memberDetails = memberInfo filterNot { _.member.declaringClass == null } map { makeMemberDetail(cls, _) } mkString "\n<p>\n"

    packageDir.createDirectories()
    file append header
    classComment foreach file.append
    file append memberSummary
    prevMemberSummary foreach file.append
    file append "\n<h2>Member Details</h2>\n\n"
    file append memberDetails
    file append htmlFooter

  }

  def makeHeader(cls: CgscriptClass): String = {

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
      s"<code>${hyperlink(cls, sup)}</code>"
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
       |  <link rel="stylesheet" href="${"../" * cls.pkg.path.length}cgsuite.css" type="text/css">
       |  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
       |  <title>${if (cls.isPackageObject) cls.pkg.qualifiedName else cls.qualifiedName}</title>
       |</head>
       |
       |<body>
       |
       |$packageStr
       |<h1>${if (cls.isPackageObject) "package " + cls.pkg.qualifiedName else cls.name}</h1>
       |
       |<p><div class="section">
       |  <code>$modifiersStr $classtypeStr <b>${if (cls.isPackageObject) cls.pkg.qualifiedName else cls.name}</b>${makeParameters(cls, cls)}$extendsStr</code>
       |</div></p>
       |
       |""".stripMargin

  }

  def makeMemberSummary(cls: CgscriptClass, declaringClass: CgscriptClass, members: Vector[MemberInfo]) = {

    val sectionHeader = {
      if (cls == declaringClass)
        "\n<h2>All Members</h2>\n\n"
      else
        s"\n<h3>Members inherited from ${hyperlink(cls, declaringClass)}</h3>\n\n"
    }

    val tableHeader =
      """<p><div class="section"><table class="members">
        |""".stripMargin

    val footer = "</table></div>\n"

    val rows = members map { info =>

      val name = info.member.idNode.id.name
      val memberLink = {
        if (info.member.declaringClass == null)
          hyperlink(cls, info.member.asInstanceOf[CgscriptClass], text = Some(name))
        else
          hyperlink(cls, info.member.declaringClass, Some(info.member), Some(name))
      }

      val description = info.member.declNode flatMap { _.docComment } match {
        case Some(comment) => processDocComment(cls, comment, firstSentenceOnly = true)
        case None => "&nbsp;"
      }

      s"""  <tr>
         |    <td class="entitytype">
         |      <code>${info.entityType}${"&nbsp;" * (5 - info.entityType.length)}</code>
         |    </td>
         |    <td class="member">
         |      <code>$memberLink${makeParameters(cls, info.member)}</code>
         |      <br>$description
         |    </td>
         |  </tr>
         |""".stripMargin

    }

    s"$sectionHeader$tableHeader${rows mkString ""}$footer"

  }

  def makeMemberDetail(cls: CgscriptClass, info: MemberInfo): String = {

    val name = info.member.idNode.id.name

    val parametersStr = makeParameters(cls, info.member)

    val comment = {
      for {
        node <- info.member.declNode
        comment <- node.docComment
      } yield {
        processDocComment(cls, comment)
      }
    } getOrElse ""

    val disclaimer = {
      if (comment == "" || cls == info.member.declaringClass) {
        ""
      } else {
        val link = {
          if (info.member.declaringClass == null)
            hyperlink(cls, info.member.asInstanceOf[CgscriptClass])
          else
            hyperlink(cls, info.member.declaringClass)
        }
        s"<p><em>(description copied from </em><code>$link</code><em>)</em>\n"
      }
    }

    s"""<a name="$name"></a>
       |<p><div class="section">
       |  <code>${info.entityType} <b>$name</b>$parametersStr</code>
       |  $disclaimer<p>$comment
       |</div>""".stripMargin

  }

  def makeParameters(cls: CgscriptClass, member: Member): String = {
    member match {
      case _: CgscriptClass#Var => ""
      case method: CgscriptClass#Method =>
        if (method.autoinvoke) "" else makeParameters(cls, method.parameters)
      case nestedClass: CgscriptClass =>
        nestedClass.classInfo.constructor map { ctor => makeParameters(cls, ctor.parameters) } getOrElse ""
    }
  }

  def makeParameters(cls: CgscriptClass, parameters: Seq[Parameter]): String = {

    val strings = parameters map { parameter =>

      val asString = {
        if (parameter.paramType == CgscriptClass.Object)
          ""
        else
          " as " + hyperlink(cls, parameter.paramType)
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
      result = result replaceFirst (f"@@$index%04d", s"<code>$link</code>")

    }

    result

  }

  def hyperlink(cls: CgscriptClass, refClass: CgscriptClass, refMember: Option[Member] = None, text: Option[String] = None): String = {

    val classRef = relativeRef(cls, refClass)
    val memberRef = refMember match {
      case Some(member) => s"#${member.idNode.id.name}"
      case None => ""
    }
    val refText = {
      if (cls == refClass && refMember.isDefined)
        refMember.get.idNode.id.name
      else if (cls == refClass)
        cls.name
      else if (refMember.isDefined)
        s"${qualifiedRefName(cls, refClass)}.${refMember.get.idNode.id.name}"
      else
        qualifiedRefName(cls, refClass)
    }
    val linkText = text getOrElse s"$refText"
    s"""<a href="$classRef$memberRef">$linkText</a>"""

  }

  def hyperlink(cls: CgscriptClass, ref: String, text: Option[String]): String = {

    val resolution = resolveRef(cls, ref)

    resolution match {

      case (Some(refcls), memberOpt) => hyperlink(cls, refcls, memberOpt, text)
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

  val htmlFooter =
    """</body>
      |</html>
      |""".stripMargin

}

case class MemberInfo(member: Member, entityType: String)

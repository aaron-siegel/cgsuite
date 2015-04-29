package org.cgsuite.lang

import java.net.URL
import org.cgsuite.core._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.{CgsuiteTree, ParserUtil}
import scala.collection.JavaConversions._
import scala.collection.mutable

object CgsuiteClass {

  val Object = Package.lang.lookupClass("Object").get
  val Class = Package.lang.lookupClass("Class").get
  val Integer = Package.game.lookupClass("Integer").get
  val DyadicRational = Package.game.lookupClass("DyadicRational").get
  val Rational = Package.game.lookupClass("Rational").get
  val CanonicalShortGame = Package.game.lookupClass("CanonicalShortGame").get
  val Player = Package.game.lookupClass("Player").get
  val Zero = Package.game.lookupClass("Zero").get
  val Nimber = Package.game.lookupClass("Nimber").get
  val NumberUpStar = Package.game.lookupClass("NumberUpStar").get
  Object.ensureLoaded()

  def of(x: Any): CgsuiteClass = {
    x match {
      case so: StandardObject => so.cls
      case _: org.cgsuite.core.Zero => Zero
      case _: Integer => Integer
      case _: DyadicRationalNumber => DyadicRational
      case _: RationalNumber => Rational
      case _: Nimber => Nimber
      case _: NumberUpStar => NumberUpStar
      case _: CanonicalShortGame => CanonicalShortGame
      case _: Player => Player
    }
  }

}

class CgsuiteClass(
  pkg: Package,
  name: String,
  systemClass: Option[Class[_]] = None
  ) {

  val javaClass = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }
  val qualifiedName = pkg.qualifiedName + "." + name

  private val methods = mutable.Map[String, CgsuiteClass#Method]()
  private var constructor: Option[UserMethod] = None
  private var loaded = false
  private var loading = false
  private var url: Option[URL] = None
  private var supers: Seq[CgsuiteClass] = Seq.empty
  private var classObjectRef: StandardObject = _

  def classObject = {
    ensureLoaded()
    classObjectRef
  }

  trait Method {

    def name: String
    def parameters: Seq[MethodParameter]
    def autoinvoke: Boolean
    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any

    def declaringClass = CgsuiteClass.this

  }

  case class UserMethod(
    name: String,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    tree: CgsuiteTree
  ) extends Method {

    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = {
      val allArgs = prepareArgs(args, namedArgs)
      new Domain(allArgs, Some(obj), Some(this)).statementSequence(tree)
    }

    def prepareArgs(args: Seq[Any], namedArgs: Map[String, Any]): Map[String, Any] = {
      if (args.length > parameters.length) {
        sys.error("too many args")
      } else {
        val argsWithNames = args.zip(parameters).map { case (x, param) =>
          // TODO Typecheck
          (param.name, x)
        }
        argsWithNames.toMap ++ namedArgs
      }
    }

  }

  case class SystemMethod(
    name: String,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = {
      // TODO Validation!
      javaMethod.invoke(obj.asInstanceOf[AnyRef], args.asInstanceOf[Seq[AnyRef]] : _*)
    }

  }

  case class ExplicitMethod0(name: String, autoinvoke: Boolean)(fn: Any => Any) extends Method {

    def parameters = Seq.empty
    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = fn(obj)

  }

  def setURL(url: URL) {
    println(s"Declaring class: $name at $url")
    this.url = Some(url)
    this.loaded = false
    this.loading = false
    methods.clear()
    constructor = None
  }

  def lookupMethod(id: String): Option[CgsuiteClass#Method] = {
    ensureLoaded()
    methods.get(id)
  }

  def ensureLoaded() {
    if (!loaded) {
      url match {
        case Some(u) => load(u)
        case None => sys.error("URL not set")
      }
    }
  }

  private def load(url: URL) {

    if (loading) {
      sys.error("circular class definition?")
    }
    loading = true

    println(s"Loading class: $name at $url")

    val in = url.openStream()
    val tree = try {
      ParserUtil.parseCU(in)
    } finally {
      in.close()
    }

    println(tree.toStringTree)

    assert(tree.getType == EOF)

    declareClass(tree.getChild(1))

  }

  private def declareClass(tree: CgsuiteTree) {

    assert(tree.getType == CLASS || tree.getType == ENUM, tree.toStringTree)

    val modifiersTree = tree.getChild(0)
    val nameTree = tree.getChild(1)
    val extendsTree = tree.getChildren.find { _.getType == EXTENDS }
    val constructorParamsTree = tree.getChildren.find { _.getType == METHOD_PARAMETER_LIST }
    val declarationsTree = tree.getChildren.find { _.getType == DECLARATIONS }.getOrElse {
      sys.error("no declarations")
    }
    val enumElementListTree = tree.getChildren.find { _.getType == ENUM_ELEMENT_LIST }

    assert(modifiersTree.getType == MODIFIERS)
    assert(nameTree.getType == IDENTIFIER)

    val isMutable = modifiersTree.getChildren.exists { _.getType == MUTABLE }
    val isSystem = modifiersTree.getChildren.exists { _.getType == SYSTEM }

    if (CgsuiteClass.Object.loaded) { // Hack to bootstrap Object
      supers = extendsTree match {
        case Some(etree) => etree.getChildren.map { parseQualifiedClass }.toSeq
        case None => Seq(CgsuiteClass.Object)
      }
      supers.foreach { _.ensureLoaded() }
      supers.foreach { scls => methods ++= scls.methods }

      val defs = declarationsTree.getChildren.filter { _.getType == DEF }
      defs foreach { declareMethod }
    } else {
      // We're loading Object right now!
      declareMethodsForObject()
    }

    classObjectRef = new StandardObject(CgsuiteClass.Class, Map("Name" -> name))
    methods.foreach { case (methodName, method) => classObjectRef.putIntoNamespace(methodName, method) }

    enumElementListTree foreach { _.getChildren foreach { declareEnumElement } }

    loading = false
    loaded = true

  }

  private def parseQualifiedClass(tree: CgsuiteTree): CgsuiteClass = {
    Package.lookupClass(flattenQualifiedClass(tree)) getOrElse {
      sys.error("not found")
    }
  }

  private def flattenQualifiedClass(tree: CgsuiteTree): Seq[String] = {
    tree.getType match {
      case IDENTIFIER => Seq(tree.getText)
      case DOT => tree.getChildren.map { flattenQualifiedClass }.toSeq.flatten
    }
  }

  private def declareMethod(tree: CgsuiteTree): Method = {

    assert(tree.getType == DEF)

    val modifiersTree = tree.getChild(0)
    val nameTree = tree.getChild(1)
    val paramsTree = tree.getChildren.find { _.getType == METHOD_PARAMETER_LIST }
    val bodyTree = tree.getChildren.last

    assert(modifiersTree.getType == MODIFIERS)
    assert(nameTree.getType == IDENTIFIER)

    val isExternal = modifiersTree.getChildren.exists { _.getType == EXTERNAL }
    val isStatic = modifiersTree.getChildren.exists { _.getType == STATIC }
    val isOverride = modifiersTree.getChildren.exists { _.getType == OVERRIDE }
    val name = nameTree.getText

    if (methods.get(name).exists { _.declaringClass == this }) {
      sys.error(s"duplicate method: $name")
    }

    val (autoinvoke, parameters) = paramsTree match {
      case Some(t) => (false, parseParameterList(t))
      case None => (true, Seq.empty)
    }

    val newMethod = {
      if (isExternal) {
        val externalName = name.updated(0, name(0).toLower)
        val externalParameterTypes = parameters map { _.paramType.javaClass }
        println(s"Declaring external method: $name => $externalName")
        val externalMethod = javaClass.getMethod(externalName, externalParameterTypes : _*)
        println(s"Here it is: $externalMethod")
        new SystemMethod(name, parameters, autoinvoke, externalMethod)
      } else {
        println(s"Declaring user method: $name")
        new UserMethod(name, parameters, autoinvoke, bodyTree)
      }
    }

    methods.put(name, newMethod)
    newMethod

  }

  private def parseParameterList(tree: CgsuiteTree): Seq[MethodParameter] = {

    assert(tree.getType == METHOD_PARAMETER_LIST)

    tree.getChildren map { paramTree =>
      paramTree.getType match {
        case IDENTIFIER =>
          val name = paramTree.getText
          val ttype = Package.lookupClass(paramTree.getChild(0).getText).getOrElse {
            sys.error("unknown symbol")
          }
          MethodParameter(name, ttype, None)
        case QUESTION =>
          val name = paramTree.getChild(0).getText
          val ttype = Package.lookupClass(paramTree.getChild(0).getChild(0).getText).getOrElse {
            sys.error("unknown symbol")
          }
          val defaultValue = paramTree.getChild(1)
          MethodParameter(name, ttype, Some(defaultValue))
      }
    }

  }

  private def declareEnumElement(tree: CgsuiteTree): Any = {

    val name = tree.getText
    val isExternal = tree.getChild(0).getChildren.exists { _.getType == EXTERNAL }

    // TODO This is a total hack
    if (isExternal) {
      val obj = {
        name match {
          case "Left" => org.cgsuite.core.Left
          case "Right" => org.cgsuite.core.Right
        }
      }
      classObjectRef.putIntoNamespace(name, obj)
    } else {
      sys.error("TODO")
    }

  }

  private def declareMethodsForObject() {
    methods.put("Class", ExplicitMethod0("Class", autoinvoke = true) { CgsuiteClass.of(_).classObject })
  }

  override def toString = s"<class $qualifiedName>"

}

case class MethodParameter(name: String, paramType: CgsuiteClass, defaultValue: Option[CgsuiteTree])

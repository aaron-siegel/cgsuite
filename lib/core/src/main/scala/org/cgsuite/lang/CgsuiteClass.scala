package org.cgsuite.lang

import java.net.URL
import org.cgsuite.core._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.{CgsuiteTree, ParserUtil}
import org.cgsuite.util.{Coordinates, Grid}
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.util.Try
import org.cgsuite.exception.InputException

object CgsuiteClass {

  val Object = Package.lang.lookupClass("Object").get
  val Class = Package.lang.lookupClass("Class").get
  val Coordinates = Package.lang.lookupClass("Coordinates").get
  val String = Package.lang.lookupClass("String").get

  val Grid = Package.util.lookupClass("Grid").get

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
      case _: Coordinates => Coordinates
      case _: String => String
      case _: Grid => Grid
    }
  }

  // Various conversions from Java types to CGScript types.
  def internalize(obj: AnyRef) = {
    obj match {
      case x: java.lang.Integer => SmallInteger(x.intValue)
      case _ => obj
    }
  }

}

class CgsuiteClass(
  val pkg: Package,
  val name: String,
  val systemClass: Option[Class[_]] = None
  ) {

  val javaClass = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }
  val companionObject = Try { Class.forName(javaClass + "$").getField("MODULE$").get(null) }.toOption
  val qualifiedName = pkg.qualifiedName + "." + name

  private val methods = mutable.Map[String, CgsuiteClass#Method]()
  private var constructorRef: Option[ConstructorMethod] = None
  private var loaded = false
  private var loading = false
  private var url: Option[URL] = None
  private var supers: Seq[CgsuiteClass] = _
  private var ancestors: Set[CgsuiteClass] = _
  private var classObjectRef: StandardObject = _

  def classObject = {
    ensureLoaded()
    classObjectRef
  }

  def constructor = {
    ensureLoaded()
    constructorRef
  }

  trait Method {

    def name: String
    def parameters: Seq[MethodParameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any

    val declaringClass = CgsuiteClass.this
    val qualifiedName = declaringClass.qualifiedName + "." + name
    val signature = s"$qualifiedName(${parameters.map { _.signature }.mkString(", ")})"

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

  case class UserMethod(
    name: String,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    tree: CgsuiteTree
  ) extends Method {

    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = {
      val allArgs = prepareArgs(args, namedArgs)
      val target = if (isStatic) classObjectRef else obj
      new Domain(allArgs, Some(target), Some(this)).statementSequence(tree)
    }

  }

  case class SystemMethod(
    name: String,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = {
      // TODO Validation!
      val target = if (isStatic) null else obj.asInstanceOf[AnyRef]
      assert(
        target == null || CgsuiteClass.of(target).ancestors.contains(declaringClass),
        (CgsuiteClass.of(target), declaringClass)
      )
      try {
        CgsuiteClass.internalize(javaMethod.invoke(target, args.asInstanceOf[Seq[AnyRef]] : _*))
      } catch {
        case exc: IllegalArgumentException =>
          throw new InputException(s"Invalid parameters for method $qualifiedName.")
      }
    }

  }

  case class ConstructorMethod(
    name: String,
    parameters: Seq[MethodParameter]
  ) extends Method {

    def autoinvoke = false
    def isStatic = false
    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = {
      // TODO Superconstructor
      // TODO Parse var initializers
      val allArgs = prepareArgs(args, namedArgs)
      val newObj = new StandardObject(CgsuiteClass.this, allArgs)
      //new Domain(allArgs, Some(newObj), Some(this)).statementSequence(tree)
      newObj
    }

  }

  case class ExplicitMethod0(name: String, autoinvoke: Boolean, isStatic: Boolean)(fn: Any => Any) extends Method {

    def parameters = Seq.empty
    def call(obj: Any, args: Seq[Any], namedArgs: Map[String, Any]): Any = {
      fn(if (isStatic) classObject else obj)
    }

  }

  def setURL(url: URL) {
    println(s"Declaring class: $name at $url")
    this.url = Some(url)
    this.loaded = false
    this.loading = false
    methods.clear()
    constructorRef = None
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
      ancestors = (supers.flatMap { _.ancestors } :+ this).toSet

      // Method declarations
      val defs = declarationsTree.getChildren.filter { _.getType == DEF }
      defs foreach { declareMethod }

      constructorRef = constructorParamsTree map { t =>
        ConstructorMethod(name, parseParameterList(t))
      }
    } else {
      // We're loading Object right now!
      supers = Seq.empty
      ancestors = Set(this)
      declareMethodsForObject()
    }

    classObjectRef = new ClassObject(this, Map("Name" -> name))
    methods.foreach { case (methodName, method) => classObjectRef.putIntoNamespace(methodName, method) }

    // Var declarations & statements
    val initializerDomain = new Domain(classObjectRef.objArgs, Some(classObjectRef), None)
    val staticDeclarations = declarationsTree.getChildren.filter { t =>
      t.getType == STATIC || (t.getType == VAR && t.getChild(0).getChildren.exists { _.getType == STATIC })
    }

    enumElementListTree foreach { _.getChildren foreach { declareEnumElement } }

    loading = false
    loaded = true

    staticDeclarations foreach { staticDeclaration(initializerDomain, _) }

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
        new SystemMethod(name, parameters, autoinvoke, isStatic, externalMethod)
      } else {
        println(s"Declaring user method: $name")
        new UserMethod(name, parameters, autoinvoke, isStatic, bodyTree)
      }
    }

    methods.put(name, newMethod)
    newMethod

  }

  private def staticDeclaration(domain: Domain, tree: CgsuiteTree) {

    tree.getType match {

      case VAR =>
        val modifiersTree = tree.getChild(0)
        assert(modifiersTree.getChildren.exists { _.getType == STATIC })
        tree.getChild(1).getType match {
          case IDENTIFIER => classObjectRef.putIntoNamespace(tree.getChild(0).getText, Nil)
          case ASSIGN =>
            val id = tree.getChild(1).getChild(0).getText
            val value = domain.expression(tree.getChild(1).getChild(1))
            classObjectRef.putIntoNamespace(id, value)
        }

      case STATIC => domain.statementSequence(tree.getChild(0))

      case _ => assert(false)

    }

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
    methods.put("Class", ExplicitMethod0("Class", autoinvoke = true, isStatic = false) { CgsuiteClass.of(_).classObject })
  }

  override def toString = s"<class $qualifiedName>"

}

case class MethodParameter(name: String, paramType: CgsuiteClass, defaultValue: Option[CgsuiteTree]) {
  val signature = paramType.qualifiedName + " " + name + (if (defaultValue.isDefined) "?" else "")
}

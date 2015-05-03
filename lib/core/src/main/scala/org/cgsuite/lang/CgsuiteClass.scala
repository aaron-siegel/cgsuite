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

  val Object = CgsuitePackage.lookupClassByName("Object").get
  val Class = CgsuitePackage.lookupClassByName("Class").get
  val Coordinates = CgsuitePackage.lookupClassByName("Coordinates").get
  val String = CgsuitePackage.lookupClassByName("String").get

  val Grid = CgsuitePackage.lookupClassByName("Grid").get

  val Integer = CgsuitePackage.lookupClassByName("Integer").get
  val DyadicRational = CgsuitePackage.lookupClassByName("DyadicRational").get
  val Rational = CgsuitePackage.lookupClassByName("Rational").get
  val CanonicalShortGame = CgsuitePackage.lookupClassByName("CanonicalShortGame").get
  val Player = CgsuitePackage.lookupClassByName("Player").get
  val Zero = CgsuitePackage.lookupClassByName("Zero").get
  val Nimber = CgsuitePackage.lookupClassByName("Nimber").get
  val NumberUpStar = CgsuitePackage.lookupClassByName("NumberUpStar").get

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
  val pkg: CgsuitePackage,
  val id: Symbol,
  val systemClass: Option[Class[_]] = None
  ) {

  val javaClass = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }
  val companionObject = Try { Class.forName(javaClass + "$").getField("MODULE$").get(null) }.toOption
  val qualifiedName = Symbol(pkg.qualifiedName + "." + id.name)

  private val methods = mutable.Map[Symbol, CgsuiteClass#Method]()
  private var constructorRef: Option[ConstructorMethod] = None
  private var loaded = false
  private var loading = false
  private var url: Option[URL] = None
  private var supers: Seq[CgsuiteClass] = _
  private var ancestors: Set[CgsuiteClass] = _
  private var classObjectRef: StandardObject = _
  private var initializers: Seq[Node] = _

  def classObject = {
    ensureLoaded()
    classObjectRef
  }

  def constructor = {
    ensureLoaded()
    constructorRef
  }

  trait Method {

    def id: Symbol
    def parameters: Seq[MethodParameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any

    val declaringClass = CgsuiteClass.this
    val qualifiedId = Symbol(declaringClass.qualifiedName + "." + id.name)
    val signature = s"${qualifiedId.name}(${parameters.map { _.signature }.mkString(", ")})"

    def prepareArgs(args: Seq[Any], namedArgs: Map[Symbol, Any]): Map[Symbol, Any] = {
      if (args.length > parameters.length) {
        sys.error("too many args")
      } else {
        val argsWithNames = args.zip(parameters).map { case (x, param) =>
          // TODO Typecheck
          (param.id, x)
        }
        argsWithNames.toMap ++ namedArgs
      }
    }

  }

  case class UserMethod(
    id: Symbol,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    body: StatementSequenceNode
  ) extends Method {

    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      val allArgs = prepareArgs(args, namedArgs)
      val target = if (isStatic) classObjectRef else obj
      new Domain(allArgs, Some(target), Some(this)).statementSequence(body)
    }

  }

  case class SystemMethod(
    id: Symbol,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      // TODO Validation!
      // TODO Named args should work here too
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
    id: Symbol,
    parameters: Seq[MethodParameter]
  ) extends Method {

    def autoinvoke = false
    def isStatic = false
    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      // TODO Superconstructor
      // TODO Parse var initializers
      val allArgs = prepareArgs(args, namedArgs)
      val newObj = new StandardObject(CgsuiteClass.this, allArgs)
      //new Domain(allArgs, Some(newObj), Some(this)).statementSequence(tree)
      newObj
    }

  }

  case class ExplicitMethod0(id: Symbol, autoinvoke: Boolean, isStatic: Boolean)(fn: Any => Any) extends Method {

    def parameters = Seq.empty
    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      fn(if (isStatic) classObject else obj)
    }

  }

  def setURL(url: URL) {
    println(s"Declaring class: ${id.name} at $url")
    this.url = Some(url)
    this.loaded = false
    this.loading = false
    methods.clear()
    constructorRef = None
  }

  def lookupMethod(id: Symbol): Option[CgsuiteClass#Method] = {
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

    println(s"Loading class: ${id.name} at $url")

    val in = url.openStream()
    val tree = try {
      ParserUtil.parseCU(in)
    } finally {
      in.close()
    }

    println(tree.toStringTree)

    assert(tree.getType == EOF)

    val node = ClassDeclarationNode(tree.getChild(1))
    println(node)
    declareClass(node)

  }

  private def declareClass(node: ClassDeclarationNode) {

    if (CgsuiteClass.Object.loaded) { // Hack to bootstrap Object
      supers = {
        if (node.extendsClause.isEmpty)
          Seq(CgsuiteClass.Object)
        else
          node.extendsClause.map {
            case IdentifierNode(tree, superId) => CgsuitePackage.lookupClass(superId) getOrElse { sys.error("not found") }
            case node: DotNode => CgsuitePackage.lookupClass(node.asQualifiedClassName.get) getOrElse { sys.error("not found") }
          }
      }
      supers.foreach { _.ensureLoaded() }
      supers.foreach { scls => methods ++= scls.methods }
      ancestors = (supers.flatMap { _.ancestors } :+ this).toSet

      // Method declarations
      node.methodDeclarations.foreach(declareMethod)

      constructorRef = node.constructorParams.map { t =>
        ConstructorMethod(id, parseParameterList(t))
      }
    } else {
      // We're loading Object right now!
      supers = Seq.empty
      ancestors = Set(this)
      declareMethodsForObject()
    }

    classObjectRef = new ClassObject(this, Map(Symbol("Name") -> id.name))
    methods.foreach { case (methodId, method) => classObjectRef.putIntoNamespace(methodId, method) }

    node.enumElements foreach { _.foreach(declareEnumElement)}

    loading = false
    loaded = true

    // Static declarations

    val initializerDomain = new Domain(classObjectRef.objArgs, Some(classObjectRef), None)
    node.staticInitializers.foreach { node => initializerDomain.expression(node.body) }

    // Instance initializers

    this.initializers = node.ordinaryInitializers

  }

  private def declareMethod(node: MethodDeclarationNode): Method = {

    val name = node.idNode.id.name

    if (methods.get(node.idNode.id).exists { _.declaringClass == this }) {
      sys.error(s"duplicate method: $name")
    }

    val (autoinvoke, parameters) = node.parameters match {
      case Some(n) => (false, parseParameterList(n))
      case None => (true, Seq.empty)
    }

    val newMethod = {
      if (node.isExternal) {
        val externalName = name.updated(0, name(0).toLower)
        val externalParameterTypes = parameters map { _.paramType.javaClass }
        println(s"Declaring external method: $name => $externalName")
        val externalMethod = javaClass.getMethod(externalName, externalParameterTypes : _*)
        println(s"Here it is: $externalMethod")
        new SystemMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, externalMethod)
      } else {
        println(s"Declaring user method: $name")
        val body = node.body getOrElse { sys.error("no body") }
        new UserMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, body)
      }
    }

    methods.put(node.idNode.id, newMethod)
    newMethod

  }

  private def parseParameterList(node: ParametersNode): Seq[MethodParameter] = {

    node.parameters.map { n =>
      val ttype = CgsuitePackage.lookupClass(n.classId.id) getOrElse {
        sys.error("unknown symbol")
      }
      MethodParameter(n.id.id, ttype, n.defaultValue)
    }

  }

  private def declareEnumElement(node: EnumElementNode): Any = {

    val isExternal = node.modifiers.exists { _.modifier == Modifier.External }

    // TODO This is a total hack
    if (isExternal) {
      val obj = {
        node.id.id.name match {
          case "Left" => org.cgsuite.core.Left
          case "Right" => org.cgsuite.core.Right
        }
      }
      classObjectRef.putIntoNamespace(node.id.id, obj)
    } else {
      sys.error("TODO")
    }

  }

  private def declareMethodsForObject() {
    methods.put(Symbol("Class"), ExplicitMethod0(Symbol("Class"), autoinvoke = true, isStatic = false) {
      CgsuiteClass.of(_).classObject }
    )
  }

  override def toString = s"<class ${qualifiedName.name}>"

}

case class MethodParameter(id: Symbol, paramType: CgsuiteClass, defaultValue: Option[Node]) {
  val signature = paramType.qualifiedName.name + " " + id.name + (if (defaultValue.isDefined) "?" else "")
}

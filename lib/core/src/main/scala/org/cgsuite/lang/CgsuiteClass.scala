package org.cgsuite.lang

import java.net.URL

import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.util.{Coordinates, Grid, TranspositionTable}

import scala.collection.mutable
import scala.util.Try

object CgsuiteClass {

  val Object = CgsuitePackage.lookupClassByName("Object").get
  val Class = CgsuitePackage.lookupClassByName("Class").get
  val Coordinates = CgsuitePackage.lookupClassByName("Coordinates").get
  val String = CgsuitePackage.lookupClassByName("String").get

  val Grid = CgsuitePackage.lookupClassByName("Grid").get

  val Game = CgsuitePackage.lookupClassByName("Game").get
  val Integer = CgsuitePackage.lookupClassByName("Integer").get
  val DyadicRational = CgsuitePackage.lookupClassByName("DyadicRational").get
  val Rational = CgsuitePackage.lookupClassByName("Rational").get
  val CanonicalShortGame = CgsuitePackage.lookupClassByName("CanonicalShortGame").get
  val Player = CgsuitePackage.lookupClassByName("Player").get
  val Zero = CgsuitePackage.lookupClassByName("Zero").get
  val Nimber = CgsuitePackage.lookupClassByName("Nimber").get
  val NumberUpStar = CgsuitePackage.lookupClassByName("NumberUpStar").get

  Object.ensureLoaded()

  private val classLookupCache = mutable.AnyRefMap[Class[_], CgsuiteClass]()

  def of(x: Any): CgsuiteClass = {
    val result = x match {
      case so: StandardObject => so.cls
      case _ => classLookupCache.getOrElseUpdate(x.getClass, ofNew(x))
    }
    result
  }

  private def ofNew(x: Any): CgsuiteClass = {
    x match {
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

  def is(x: Any, cls: CgsuiteClass) = of(x).ancestors.contains(cls)

  // Various conversions from Java types to CGScript types.
  def internalize(obj: AnyRef) = {
    obj match {
      case x: java.lang.Integer => SmallInteger(x.intValue)
      case _ => obj
    }
  }

  def clearAll() {
    CgsuitePackage.classDictionary.values foreach { _.unload() }
    Object.ensureLoaded()
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

  class ClassInfo(
    val modifiers: Set[Modifier.Value],
    val supers: Seq[CgsuiteClass],
    val methods: Map[Symbol, CgsuiteClass#Method],
    val constructor: Option[CgsuiteClass#ConstructorMethod],
    val initializers: Seq[InitializerNode]
    ) {
    val properAncestors: Seq[CgsuiteClass] = supers.flatMap { _.classInfo.ancestors }.distinct
    val ancestors = properAncestors :+ CgsuiteClass.this
    val isMutable = modifiers.contains(Modifier.Mutable)
    val inheritedClassVars = supers.flatMap { _.classInfo.allClassVars }.toSet
    val constructorParamVars = constructor.toSeq.flatMap { _.parameters.map { _.id } }.toSet
    val localClassVars = initializers.collect {
      case InitializerNode(_, AssignToNode(_, assignId, _, true), false) => assignId.id
    }.toSet
    val allClassVars: Set[Symbol] = inheritedClassVars ++ constructorParamVars ++ localClassVars
  }

  private var url: URL = _
  private var classInfoRef: ClassInfo = _
  private var loading = false
  private var classObjectRef: ClassObject = _

  val transpositionTable = new TranspositionTable()

  def isLoaded = classInfoRef != null

  def classInfo = {
    ensureLoaded()
    classInfoRef
  }

  def classObject = {
    ensureLoaded()
    classObjectRef
  }

  def isMutable = classInfo.isMutable

  def constructor = classInfo.constructor

  def ancestors = classInfo.ancestors

  def initializers = classInfo.initializers

  trait Method {

    def id: Symbol
    def parameters: Seq[MethodParameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any

    val declaringClass = CgsuiteClass.this
    val qualifiedId = Symbol(declaringClass.qualifiedName.name + "." + id.name)
    val signature = s"${qualifiedId.name}(${parameters.map { _.signature }.mkString(", ")})"

    def elaborate() {}

    def prepareArgs(args: Seq[Any], namedArgs: Map[Symbol, Any]): Map[Symbol, Any] = {
      if (args.length > parameters.length) {
        sys.error("too many args")
      } else {
        Profiler.start('PrepareArgs)
        val argsWithNames = args.zip(parameters).map { case (x, param) =>
          // TODO Typecheck
          (param.id, x)
        }
        val providedArgs = argsWithNames.toMap ++ namedArgs
        val result = parameters.map { param =>
          param.id -> providedArgs.getOrElse(param.id, param.defaultValue.get)
        }.toMap
        Profiler.stop('PrepareArgs)
        result
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

    private val invokeUserMethod = Symbol(s"InvokeUserMethod [${qualifiedId.name}]")
    private var localVariableCount: Int = 0

    override def elaborate() {
      val scope = new Scope(classInfo.allClassVars, mutable.AnyRefMap(), mutable.Stack(mutable.HashSet()))
      parameters foreach { param => scope.insertId(param.id) }
      parameters foreach { param => param.methodScopeIndex = scope.varMap(param.id) }
      body.elaborate(scope)
      localVariableCount = scope.varMap.size
    }

    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      //val allArgs = prepareArgs(args, namedArgs)
      Profiler.start(invokeUserMethod)
      val target = if (isStatic) classObject else obj
      try {
        // Construct a new domain with local scope for this method.
        val array = if (localVariableCount == 0) null else new Array[Any](localVariableCount)
        val domain = new Domain(array, Some(target))
        // TODO more intelligent populating of arguments, validation etc
        var i = 0
        while (i < parameters.length) {
          domain.localScope(parameters(i).methodScopeIndex) = args(i)
          i += 1
        }
        body.evaluate(domain)
      } finally {
        Profiler.stop(invokeUserMethod)
      }
    }

  }

  case class SystemMethod(
    id: Symbol,
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    private val reflect = Symbol(s"Reflect [${javaMethod.toString}]")

    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      // TODO Validation!
      // TODO Named args should work here too
      val target = if (isStatic) null else obj.asInstanceOf[AnyRef]
      assert(
        target == null || CgsuiteClass.of(target).ancestors.contains(declaringClass),
        (CgsuiteClass.of(target), declaringClass)
      )
      try {
        Profiler.start(reflect)
        CgsuiteClass.internalize(javaMethod.invoke(target, args.asInstanceOf[Seq[AnyRef]] : _*))
      } catch {
        case exc: IllegalArgumentException =>
          throw new InputException(s"Invalid parameters for method $qualifiedName.")
      } finally {
        Profiler.stop(reflect)
      }
    }

  }

  case class ConstructorMethod(
    id: Symbol,
    parameters: Seq[MethodParameter]
  ) extends Method {

    private val invokeConstructor = Symbol(s"InvokeConstructor [${qualifiedId.name}]")

    def autoinvoke = false
    def isStatic = false
    def call(obj: Any, args: Seq[Any], namedArgs: Map[Symbol, Any]): Any = {
      // TODO Superconstructor
      // TODO Parse var initializers
      val allArgs = prepareArgs(args, namedArgs)
      Profiler.start(invokeConstructor)
      try {
        if (ancestors.contains(CgsuiteClass.Game))
          new GameObject(CgsuiteClass.this, allArgs)
        else
          new StandardObject(CgsuiteClass.this, allArgs)
      } finally {
        Profiler.stop(invokeConstructor)
      }
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
    this.url = url
    unload()
  }

  def unload() {
    this.classInfoRef = null
    this.loading = false
    this.transpositionTable.clear()
  }

  def lookupMethod(id: Symbol): Option[CgsuiteClass#Method] = {
    ensureLoaded()
    classInfo.methods.get(id)
  }

  def ensureLoaded() {
    if (classInfoRef == null) {
      if (url == null) {
        sys.error("URL not set")
      }
      load(url)
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
      ParserUtil.parseCU(in, url.toString)
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

    val modifiers = node.modifiers.map { _.modifier }.toSet

    val (supers, methods, constructor) = {

      if (CgsuiteClass.Object.isLoaded) { // Hack to bootstrap Object

        val supers = {
          if (node.extendsClause.isEmpty)
            Seq(CgsuiteClass.Object)
          else
            node.extendsClause.map {
              case IdentifierNode(tree, superId) => CgsuitePackage.lookupClass(superId) getOrElse { sys.error("not found") }
              case node: DotNode => CgsuitePackage.lookupClass(node.asQualifiedClassName.get) getOrElse { sys.error("not found") }
            }
        }
        supers.foreach { _.ensureLoaded() }
        // TODO Check for unresolved superclass method conflicts
        // TODO Check for duplicate local method names
        val superMethods = supers.flatMap { _.classInfo.methods }
        val localMethods = node.methodDeclarations.map { parseMethod }
        val constructor = node.constructorParams.map { t => ConstructorMethod(id, parseParameterList(t)) }

        (supers, (superMethods ++ localMethods).toMap, constructor)

      } else {

        // We're loading Object right now!
        (Seq.empty, methodsForObject(), None)

      }

    }

    classInfoRef = new ClassInfo(modifiers, supers, methods, constructor, node.ordinaryInitializers)
    loading = false

    // Create the class object
    classObjectRef = new ClassObject(CgsuiteClass.this, Map(Symbol("Name") -> id.name))

    // Populate namespace
    methods.foreach { case (methodId, method) => classObject.namespace.put(methodId, method, declare = true) }
    node.enumElements foreach { _.foreach(declareEnumElement) }

    // Elaborate methods
    methods.foreach { case (_, method) => method.elaborate() }

    // Static declarations
    val initializerDomain = new Domain(null, Some(classObject))
    node.staticInitializers.foreach { node => node.body.evaluate(initializerDomain) }

    node.ordinaryInitializers.foreach { _.body.elaborate(Scope(classInfo.allClassVars)) }

  }

  private def parseMethod(node: MethodDeclarationNode): (Symbol, Method) = {

    val name = node.idNode.id.name

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

    node.idNode.id -> newMethod

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
      classObject.namespace.put(node.id.id, obj, declare = true)
    } else {
      sys.error("TODO")
    }

  }

  private def methodsForObject(): Map[Symbol, CgsuiteClass#Method] = {
    Map(Symbol("Class") -> ExplicitMethod0(Symbol("Class"), autoinvoke = true, isStatic = false) {
      CgsuiteClass.of(_).classObject
    })
  }

  override def toString = s"<class ${qualifiedName.name}>"

}

case class MethodParameter(id: Symbol, paramType: CgsuiteClass, defaultValue: Option[Node]) {
  val signature = paramType.qualifiedName.name + " " + id.name + (if (defaultValue.isDefined) "?" else "")
  var methodScopeIndex = -1
}

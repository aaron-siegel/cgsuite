package org.cgsuite.lang

import java.lang.reflect.InvocationTargetException
import java.net.URL

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output._
import org.cgsuite.util._
import org.slf4j.LoggerFactory

import scala.collection.immutable.NumericRange
import scala.collection.mutable

object CgscriptClass {

  private val logger = Logger(LoggerFactory.getLogger(classOf[CgscriptClass]))

  private var nextClassOrdinal = 0
  private[lang] def newClassOrdinal = {
    val ord = nextClassOrdinal
    nextClassOrdinal += 1
    ord
  }

  private val baseSystemClasses: Seq[(String, Class[_])] = Seq(

    "cgsuite.lang.Object" -> classOf[AnyRef],
    "cgsuite.lang.Enum" -> classOf[EnumObject]

  )

  private val typedSystemClasses: Seq[(String, Class[_])] = Seq(

    "cgsuite.lang.Class" -> classOf[ClassObject],
    "cgsuite.lang.Boolean" -> classOf[java.lang.Boolean],
    "cgsuite.lang.String" -> classOf[String],
    "cgsuite.lang.Coordinates" -> classOf[Coordinates],
    "cgsuite.lang.Range" -> classOf[NumericRange[_]],
    "cgsuite.lang.List" -> classOf[Seq[_]],
    "cgsuite.lang.Set" -> classOf[Set[_]],
    "cgsuite.lang.Map" -> classOf[Map[_, _]],
    "cgsuite.lang.MapEntry" -> classOf[(_,_)],
    "cgsuite.lang.Procedure" -> classOf[Procedure],
    "cgsuite.lang.System" -> classOf[System],
    "cgsuite.lang.Table" -> classOf[Table],
    "cgsuite.lang.Collection" -> classOf[Iterable[_]],

    "cgsuite.util.Grid" -> classOf[Grid],
    "cgsuite.util.Strip" -> classOf[Strip],
    "cgsuite.util.Symmetry" -> classOf[Symmetry],
    "cgsuite.util.UptimalExpansion" -> classOf[UptimalExpansion],

    // The order is extremely important in the following hierarchies (most specific first)

    "cgsuite.util.output.GridOutput" -> classOf[GridOutput],
    "cgsuite.util.output.TextOutput" -> classOf[StyledTextOutput],
    "cgsuite.lang.Output" -> classOf[Output],

    "game.Zero" -> classOf[Zero],
    "game.Integer" -> classOf[Integer],
    "game.DyadicRational" -> classOf[DyadicRationalNumber],
    "game.Rational" -> classOf[RationalNumber],
    "game.Nimber" -> classOf[Nimber],
    "game.NumberUpStar" -> classOf[NumberUpStar],
    "game.CanonicalShortGame" -> classOf[CanonicalShortGame],
    "game.CanonicalStopperGame" -> classOf[CanonicalStopperGame],
    "game.Game" -> classOf[Game],

    "game.Player" -> classOf[Player]

  )

  private val otherSystemClasses: Seq[String] = Seq(

    "cgsuite.util.Icon",

    "game.GridGame",
    "game.StripGame",

    "game.grid.Amazons",
    "game.grid.Clobber",
    "game.grid.Domineering",
    "game.grid.Fission",

    "game.strip.ToadsAndFrogs"

  )

  val systemClasses = ((baseSystemClasses ++ typedSystemClasses) map { case (name, cls) => (name, Some(cls)) }) ++
    (otherSystemClasses map { (_, None) })

  systemClasses foreach { case (name, scalaClass) => declareSystemClass(name, scalaClass) }

  private def declareSystemClass(name: String, scalaClass: Option[Class[_]]) {

    val path = name.replace('.', '/')
    val url = getClass.getResource(s"resources/$path.cgs")
    val components = name.split("\\.").toSeq
    val pkg = CgscriptPackage.root.lookupSubpackage(components.dropRight(1)).getOrElse {
      sys.error("Cannot find package: " + components.dropRight(1))
    }
    pkg.declareClass(Symbol(components.last), url, scalaClass)

  }

  val Object = CgscriptPackage.lookupClassByName("Object").get
  val Class = CgscriptPackage.lookupClassByName("Class").get
  val Enum = CgscriptPackage.lookupClassByName("Enum").get
  val Game = CgscriptPackage.lookupClassByName("Game").get

  Object.ensureLoaded()

  private val classLookupCache = mutable.AnyRefMap[Class[_], CgscriptClass]()

  def of(x: Any): CgscriptClass = {
    val result = x match {
      case so: StandardObject => so.cls
      case _ => classLookupCache.getOrElseUpdate(x.getClass, toCgscriptClass(x))
    }
    result
  }

  private def toCgscriptClass(x: Any): CgscriptClass = {
    // This is slow, but we cache the results so that it only happens once
    // per distinct (Java) type witnessed.
    val systemClass = typedSystemClasses find { case (_, cls) => cls.isAssignableFrom(x.getClass) }
    systemClass flatMap { case (name, _) => CgscriptPackage.lookupClassByName(name) } getOrElse {
      sys.error(s"Could not determine CGScript class for object of type `${x.getClass}`: $x")
    }
  }

  def is(x: Any, cls: CgscriptClass) = of(x).ancestors.contains(cls)

  // Various conversions from Java types to CGScript types.
  def internalize(obj: AnyRef) = {
    obj match {
      case x: java.lang.Integer => SmallInteger(x.intValue)
      case null => Nil
      case _ => obj
    }
  }

  def clearAll() {
    CgscriptPackage.classDictionary.values foreach { _.unload() }
    Object.ensureLoaded()
  }

}

class CgscriptClass(
  val pkg: CgscriptPackage,
  val id: Symbol,
  val systemClass: Option[Class[_]] = None
  ) extends LazyLogging {

  import CgscriptClass._

  val classOrdinal = newClassOrdinal
  val javaClass = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }
  val qualifiedName = pkg.qualifiedName + "." + id.name
  val qualifiedId = Symbol(qualifiedName)

  class ClassInfo(
    val modifiers: Set[Modifier.Value],
    val supers: Seq[CgscriptClass],
    val methods: Map[Symbol, CgscriptClass#Method],
    val constructor: Option[CgscriptClass#Constructor],
    val initializers: Seq[InitializerNode],
    val staticInitializers: Seq[InitializerNode]
    ) {

    val properAncestors: Seq[CgscriptClass] = supers.flatMap { _.classInfo.ancestors }.distinct
    val ancestors = properAncestors :+ CgscriptClass.this
    val isMutable = modifiers.contains(Modifier.Mutable)
    val inheritedClassVars = supers.flatMap { _.classInfo.allClassVars }.distinct
    val constructorParamVars = constructor.toSeq.flatMap { _.parameters.map { _.id } }
    val localClassVars = initializers.collect {
      case InitializerNode(_, AssignToNode(_, assignId, _, true), false, _) => assignId.id
    }
    val allClassVars: Seq[Symbol] = (constructorParamVars ++ inheritedClassVars ++ localClassVars).distinct
    val classVarOrdinals: Map[Symbol, Int] = allClassVars.zipWithIndex.toMap
    val staticVars = staticInitializers.collect {
      case InitializerNode(_, AssignToNode(_, assignId, _, true), true, _) => assignId.id
    }
    val staticVarOrdinals: Map[Symbol, Int] = staticVars.zipWithIndex.toMap
    val allSymbolsInScope: Set[Symbol] = classVarOrdinals.keySet ++ staticVarOrdinals.keySet ++ methods.keySet

    // For efficiency, we cache lookups for some methods that get called in hardcoded locations
    lazy val optionsMethod = lookupMethod('Options) getOrElse { throw InputException("Method not found: Options") }
    lazy val decompositionMethod = lookupMethod('Decomposition) getOrElse { throw InputException("Method not found: Decomposition") }
    lazy val canonicalFormMethod = lookupMethod('CanonicalForm) getOrElse { throw InputException("Method not found: CanonicalForm") }
    lazy val toOutputMethod = lookupMethod('ToOutput) getOrElse { throw InputException("Method not found: ToOutput") }

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
    def parameters: Seq[Parameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def call(obj: Any, args: Array[Any]): Any

    val declaringClass = CgscriptClass.this
    val qualifiedName = declaringClass.qualifiedName + "." + id.name
    val qualifiedId = Symbol(qualifiedName)
    val signature = s"$qualifiedName(${parameters.map { _.signature }.mkString(", ")})"
    val ordinal = CallSite.newCallSiteOrdinal

    def elaborate(): Unit = {
      logger.debug(s"Elaborating $qualifiedName")
      val scope = new Scope(Some(pkg), classInfo.allSymbolsInScope, mutable.AnyRefMap(), mutable.Stack(mutable.HashSet()))
      parameters foreach { param =>
        param.defaultValue foreach { _.elaborate(scope) }
      }
      logger.debug(s"Done elaborating $qualifiedName")
    }

  }

  case class UserMethod(
    id: Symbol,
    parameters: Seq[Parameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    body: StatementSequenceNode
  ) extends Method {

    private val invokeUserMethod = Symbol(s"InvokeUserMethod [$qualifiedName]")
    private var localVariableCount: Int = 0

    override def elaborate(): Unit = {
      val scope = new Scope(Some(pkg), classInfo.allSymbolsInScope, mutable.AnyRefMap(), mutable.Stack(mutable.HashSet()))
      parameters foreach { param =>
        scope.insertId(param.id)
        param.methodScopeIndex = scope.varMap(param.id)
        param.defaultValue foreach { _.elaborate(scope) }
      }
      body.elaborate(scope)
      localVariableCount = scope.varMap.size
    }

    def call(obj: Any, args: Array[Any]): Any = {
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
    parameters: Seq[Parameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    private val reflect = Symbol(s"Reflect [$javaMethod]")

    def call(obj: Any, args: Array[Any]): Any = {
      // TODO Validation!
      // TODO Named args should work here too
      val target = if (isStatic) null else obj.asInstanceOf[AnyRef]
      assert(
        target == null || of(target).ancestors.contains(declaringClass),
        (of(target), declaringClass)
      )
      try {
        Profiler.start(reflect)
        internalize(javaMethod.invoke(target, args.asInstanceOf[Array[AnyRef]] : _*))
      } catch {
        case exc: IllegalArgumentException => throw InputException(
          s"Invalid parameters for method `$qualifiedName` of types (${args.map { _.getClass.getName }.mkString(", ")})"
        )
        case exc: InvocationTargetException => throw InputException(
          exc.getTargetException match {
            case nestedExc: InputException =>
              // TODO nestedExc.setInvocationTarget(qualifiedName)
              throw nestedExc
            case nestedExc => throw InputException(s"Error in call to `$qualifiedName`: ${nestedExc.getMessage}")
          }
        )
      } finally {
        Profiler.stop(reflect)
      }
    }

  }

  case class ExplicitMethod(
    id: Symbol,
    parameters: Seq[Parameter],
    autoinvoke: Boolean,
    isStatic: Boolean)
    (fn: (Any, Any) => Any) extends Method {

    // TODO This only works for 0-ary methods currently
    def call(obj: Any, args: Array[Any]): Any = {
      val argsTuple = parameters.size match {
        case 0 => ()
        case 1 => args(0)
        case 2 => (args(0), args(1))
      }
      fn(if (isStatic) classObject else obj, argsTuple)
    }

  }

  trait Constructor extends Method with CallSite {

    val autoinvoke = false
    val isStatic = false

    def call(obj: Any, args: Array[Any]): Any = call(args)

  }

  case class UserConstructor(
    id: Symbol,
    parameters: Seq[Parameter]
  ) extends Constructor {

    private val invokeConstructor = Symbol(s"InvokeConstructor [$qualifiedName]")

    def call(args: Array[Any]): Any = {
      // TODO Superconstructor
      // TODO Parse var initializers
      Profiler.start(invokeConstructor)
      try {
        if (ancestors.contains(Game))
          new GameObject(CgscriptClass.this, args)
        else
          new StandardObject(CgscriptClass.this, args)
      } finally {
        Profiler.stop(invokeConstructor)
      }
    }

  }

  case class SystemConstructor(
    id: Symbol,
    parameters: Seq[Parameter],
    javaConstructor: java.lang.reflect.Constructor[_]
  ) extends Constructor {

    private val reflect = Symbol(s"Reflect [$javaConstructor]")

    def call(args: Array[Any]): Any = {
      try {
        Profiler.start(reflect)
        javaConstructor.newInstance(args.asInstanceOf[Array[AnyRef]] : _*)
      } catch {
        case exc: IllegalArgumentException =>
          throw new InputException(s"Invalid parameters for constructor `$qualifiedName`.")
      } finally {
        Profiler.stop(reflect)
      }
    }

  }

  def setURL(url: URL) {
    logger.debug(s"Declaring class $classOrdinal: ${id.name} at $url")
    this.url = url
    unload()
  }

  def unload() {
    this.classInfoRef = null
    this.loading = false
    this.transpositionTable.clear()
  }

  def lookupMethod(id: Symbol): Option[CgscriptClass#Method] = {
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
      sys.error("circular class definition?: " + url)
    }
    loading = true

    logger.debug(s"Loading class: ${id.name} at $url")

    val in = url.openStream()
    val tree = try {
      ParserUtil.parseCU(in, url.toString)
    } finally {
      in.close()
    }

    logger.debug(tree.toStringTree)

    assert(tree.getType == EOF)

    val node = ClassDeclarationNode(tree.getChild(1))
    logger.debug(node.toString)
    declareClass(node)

    logger.debug(s"Done loading class: ${id.name}")

  }

  private def declareClass(node: ClassDeclarationNode) {

    val modifiers = node.modifiers.map { _.modifier }.toSet

    val (supers, methods, constructor) = {

      if (Object.isLoaded) { // Hack to bootstrap Object

        val supers = {
          if (node.extendsClause.isEmpty) {
            if (node.isEnum)
              Seq(Enum)
            else
              Seq(Object)
          } else {
            node.extendsClause.map {
              case IdentifierNode(tree, superId) => CgscriptPackage.lookupClass(superId) getOrElse {
                throw InputException(s"Unknown superclass: `${superId.name}`", tree)
              }
              case node: DotNode => CgscriptPackage.lookupClass(node.asQualifiedClassName.get) getOrElse {
                sys.error("not found")
              }
            }
          }
        }
        supers.foreach { _.ensureLoaded() }
        // TODO Check for unresolved superclass method conflicts
        // TODO Check for duplicate local method names
        val superMethods = supers.flatMap { _.classInfo.methods }
        val localMethods = node.methodDeclarations map parseMethod
        val constructor = node.constructorParams.map { t =>
          val parameters = parseParameterList(t)
          systemClass match {
            case None => UserConstructor(id, parameters)
            case Some(cls) =>
              val externalParameterTypes = parameters map { _.paramType.javaClass }
              SystemConstructor(id, parameters, cls.getConstructor(externalParameterTypes : _*))
          }
        }
        val renamedSuperMethods = {
          for {
            (id, method) <- superMethods
          } yield {
            (Symbol("super$" + id.name), method)
          }
        }

        (supers, (superMethods ++ renamedSuperMethods ++ localMethods).toMap, constructor)

      } else {

        // We're loading Object right now!
        val localMethods = node.methodDeclarations map parseMethod
        (Seq.empty, localMethods.toMap, None)

      }

    }

    classInfoRef = new ClassInfo(
      modifiers,
      supers,
      methods,
      constructor,
      node.ordinaryInitializers,
      node.staticInitializers
    )
    loading = false

    // Create the class object
    classObjectRef = new ClassObject(CgscriptClass.this)

    // Elaborate methods
    constructor foreach { _.elaborate() }
    methods foreach { case (_, method) => method.elaborate() }

    // Static declarations - create a domain whose context is the class object
    val initializerDomain = new Domain(null, Some(classObject))
    node.staticInitializers.foreach { node =>
      if (!node.isExternal)
        node.body.evaluate(initializerDomain)
    }

    // Enum construction
    if (node.isEnum) {
      for ((id, index) <- classInfoRef.staticVarOrdinals) {
        classObjectRef.vars(index) = new EnumObject(this, id.name)
      }
    }

    // Big temporary hack to populate Left and Right
    if (qualifiedName == "game.Player") {
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Left)) = Left
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Right)) = Right
    }
    if (qualifiedName == "cgsuite.util.Symmetry") {
      import Symmetry._
      Map('Identity -> Identity, 'Inversion -> Inversion, 'HorizontalFlip -> HorizontalFlip, 'VerticalFlip -> VerticalFlip,
        'Transpose -> Transpose, 'AntiTranspose -> AntiTranspose, 'ClockwiseRotation -> ClockwiseRotation,
        'AnticlockwiseRotation -> AnticlockwiseRotation) foreach { case (symId, value) =>
        classObjectRef.vars(classInfoRef.staticVarOrdinals(symId)) = value
      }
    }

    node.ordinaryInitializers.foreach { _.body.elaborate(Scope(Some(pkg), classInfo.allSymbolsInScope)) }

  }

  private def parseMethod(node: MethodDeclarationNode): (Symbol, Method) = {

    val name = node.idNode.id.name

    val (autoinvoke, parameters) = node.parameters match {
      case Some(n) => (false, parseParameterList(n))
      case None => (true, Seq.empty)
    }

    val newMethod = {
      if (node.isExternal) {
        logger.debug(s"Declaring external method: $name")
        SpecialMethods.specialMethods.get(qualifiedName + "." + name) match {
          case Some(fn) =>
            logger.debug("It's a special method.")
            new ExplicitMethod(node.idNode.id, parameters, autoinvoke, node.isStatic)(fn)
          case None =>
            val externalName = name.updated(0, name(0).toLower)
            val externalParameterTypes = parameters map { _.paramType.javaClass }
            val externalMethod = javaClass.getMethod(externalName, externalParameterTypes: _*)
            logger.debug(s"It's a Java method via reflection: $externalMethod")
            new SystemMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, externalMethod)
        }
      } else {
        logger.debug(s"[$qualifiedName] Declaring user method: $name")
        val body = node.body getOrElse { sys.error("no body") }
        new UserMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, body)
      }
    }

    node.idNode.id -> newMethod

  }

  private def parseParameterList(node: ParametersNode): Seq[Parameter] = {

    node.parameters.map { n =>
      val ttype = CgscriptPackage.lookupClass(n.classId.id) getOrElse {
        sys.error("unknown symbol")
      }
      Parameter(n.id.id, ttype, n.defaultValue)
    }

  }

  override def toString = s"<<$qualifiedName>>"

}

case class Parameter(id: Symbol, paramType: CgscriptClass, defaultValue: Option[EvalNode]) {
  val signature = paramType.qualifiedName + " " + id.name + (if (defaultValue.isDefined) "?" else "")
  var methodScopeIndex = -1
}

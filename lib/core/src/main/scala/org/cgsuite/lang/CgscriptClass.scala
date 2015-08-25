package org.cgsuite.lang

import java.lang.reflect.InvocationTargetException
import java.net.URL

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.cgsuite.core._
import org.cgsuite.exception.{CgsuiteException, InputException}
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output._
import org.cgsuite.util._
import org.slf4j.LoggerFactory

import scala.collection.immutable.NumericRange
import scala.collection.mutable

import scala.language.existentials
import scala.language.postfixOps

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
    "cgsuite.lang.Nil" -> Nil.getClass,
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
    "game.Uptimal" -> classOf[Uptimal],
    "game.CanonicalShortGame" -> classOf[CanonicalShortGame],
    "game.Pseudonumber" -> classOf[Pseudonumber],
    "game.CanonicalStopper" -> classOf[CanonicalStopper],
    "game.StopperSidedValue" -> classOf[StopperSidedValue],
    "game.SidedValue" -> classOf[SidedValue],
    "game.NormalValue" -> classOf[NormalValue],

    "game.ExplicitGame" -> classOf[ExplicitGame],

    "game.Game" -> classOf[Game],

    "game.Player" -> classOf[Player],
    "game.Side" -> classOf[Side]

  )

  private val otherSystemClasses: Seq[String] = Seq(

    "cgsuite.util.Icon",

    "game.constants",
    "game.Ruleset",

    "game.grid.Amazons",
    "game.grid.constants",
    "game.grid.Domineering",
    "game.grid.Fission",
    "game.grid.GenClobber",
    "game.grid.GenFoxAndGeese",
    "game.grid.GridRuleset",

    "game.strip.constants",
    "game.strip.GenToadsAndFrogs",
    "game.strip.StripRuleset"

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
  val List = CgscriptPackage.lookupClassByName("List").get
  val NilClass = CgscriptPackage.lookupClassByName("Nil").get

  Object.ensureLoaded()

  private val classLookupCache = mutable.AnyRefMap[Class[_], CgscriptClass]()

  def of(x: Any): CgscriptClass = {
    assert(x != null)
    x match {
      case so: StandardObject => so.cls
      case _ => classLookupCache.getOrElseUpdate(x.getClass, toCgscriptClass(x))
    }
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
    CanonicalShortGameOps.reinit()
    Resolver.clearAll()
    CgscriptPackage.classDictionary.values foreach { _.unload() }
    Object.ensureLoaded()
  }

}

trait Member {
  def declaringClass: CgscriptClass
}

class CgscriptClass(
  val pkg: CgscriptPackage,
  val enclosingClass: Option[CgscriptClass],
  val id: Symbol,
  val systemClass: Option[Class[_]] = None
  ) extends Member with LazyLogging {

  import CgscriptClass._

  val classOrdinal = newClassOrdinal        // TODO How to handle for nested classes??
  val declaringClass = enclosingClass orNull
  val javaClass = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }
  val name: String = enclosingClass match {
    case Some(encl) => encl.name + "." + id.name
    case None => id.name
  }
  val qualifiedName: String = pkg.qualifiedName + "." + name
  val qualifiedId: Symbol = Symbol(qualifiedName)
  val logPrefix = f"[$classOrdinal%3d: $qualifiedName%s]"

  class ClassInfo(
    val modifiers: Set[Modifier.Value],
    val supers: Seq[CgscriptClass],
    val nestedClasses: Map[Symbol, CgscriptClass],
    val methods: Map[Symbol, CgscriptClass#Method],
    val constructor: Option[CgscriptClass#Constructor],
    val initializers: Seq[InitializerNode],
    val staticInitializers: Seq[InitializerNode],
    val enumElements: Seq[EnumElementNode]
    ) {

    val properAncestors: Seq[CgscriptClass] = supers.flatMap { _.classInfo.ancestors }.distinct
    val ancestors = properAncestors :+ CgscriptClass.this
    val isMutable = modifiers.contains(Modifier.Mutable)
    val isSingleton = modifiers.contains(Modifier.Singleton)
    val inheritedClassVars = supers.flatMap { _.classInfo.allClassVars }.distinct
    val constructorParamVars = constructor.toSeq.flatMap { _.parameters.map { _.id } }
    val localClassVars = initializers.collect {
      case InitializerNode(_, AssignToNode(_, assignId, _, _), true, false, _) => assignId.id
    }
    val allClassVars: Seq[Symbol] = (constructorParamVars ++ inheritedClassVars ++ localClassVars).distinct
    val classVarOrdinals: Map[Symbol, Int] = allClassVars.zipWithIndex.toMap
    val staticVars = enumElements.map { _.id.id } ++ staticInitializers.collect {
      case InitializerNode(_, AssignToNode(_, assignId, _, _), true, true, _) => assignId.id
    }
    val staticVarOrdinals: Map[Symbol, Int] = staticVars.zipWithIndex.toMap
    val allSymbolsInThisClass: Set[Symbol] = {
      classVarOrdinals.keySet ++ staticVarOrdinals.keySet ++ methods.keySet ++ nestedClasses.keySet
    }
    lazy val allSymbolsInClassScope: Seq[Set[Symbol]] = {
      allSymbolsInThisClass +: enclosingClass.map { _.classInfo.allSymbolsInClassScope }.getOrElse(Seq.empty)
    }
    val allMethodsInScope: Map[Symbol, CgscriptClass#Method] = {
      (enclosingClass map { _.classInfo.allMethodsInScope } getOrElse Map.empty) ++ methods
    }
    val allNestedClassesInScope: Map[Symbol, CgscriptClass] = {
      (enclosingClass map { _.classInfo.allNestedClassesInScope } getOrElse Map.empty) ++ nestedClasses
    }

    // For efficiency, we cache lookups for some methods that get called in hardcoded locations
    lazy val evalMethod = lookupMethod('Eval) getOrElse { throw InputException("Method not found: `Eval`") }
    lazy val optionsMethod = lookupMethod('Options) getOrElse { throw InputException("Method not found: `Options`") }
    lazy val decompositionMethod = lookupMethod('Decomposition) getOrElse { throw InputException("Method not found: `Decomposition`") }
    lazy val canonicalFormMethod = lookupMethod('CanonicalForm) getOrElse { throw InputException("Method not found: `CanonicalForm`") }
    lazy val gameValueMethod = lookupMethod('GameValue) getOrElse { throw InputException("Method not found: `GameValue`") }
    lazy val depthHintMethod = lookupMethod('DepthHint) getOrElse { throw InputException("Method not found: `DepthHint`") }
    lazy val toOutputMethod = lookupMethod('ToOutput) getOrElse { throw InputException("Method not found: `ToOutput`") }

  }

  private var url: URL = _
  private var classInfoRef: ClassInfo = _
  private var loading = false
  private var classObjectRef: ClassObject = _
  private var singletonInstanceRef: Any = _

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

  def singletonInstance = {
    ensureLoaded()
    singletonInstanceRef
  }

  def isMutable = classInfo.isMutable

  def isSingleton = classInfo.isSingleton

  def constructor = classInfo.constructor

  def ancestors = classInfo.ancestors

  def initializers = classInfo.initializers

  trait Method extends Member {

    def id: Symbol
    def parameters: Seq[Parameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def isOverride: Boolean
    def call(obj: Any, args: Array[Any]): Any

    val declaringClass = CgscriptClass.this
    val qualifiedName = declaringClass.qualifiedName + "." + id.name
    val qualifiedId = Symbol(qualifiedName)
    val signature = s"$qualifiedName(${parameters.map { _.signature }.mkString(", ")})"
    val ordinal = CallSite.newCallSiteOrdinal

    def elaborate(): Unit = {
      logger.debug(s"$logPrefix Elaborating.")
      val scope = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
      parameters foreach { param =>
        param.defaultValue foreach { _.elaborate(scope) }
      }
      logger.debug(s"$logPrefix Done elaborating.")
    }

  }

  case class UserMethod(
    id: Symbol,
    parameters: Seq[Parameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    isOverride: Boolean,
    body: StatementSequenceNode
  ) extends Method {

    private val invokeUserMethod = Symbol(s"InvokeUserMethod [$qualifiedName]")
    private var localVariableCount: Int = 0

    override def elaborate(): Unit = {
      val scope = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
      parameters foreach { param =>
        param.methodScopeIndex = scope.insertId(param.id)
        param.defaultValue foreach { _.elaborate(scope) }
      }
      body.elaborate(scope)
      localVariableCount = scope.localVariableCount
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
    isOverride: Boolean,
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
            case nestedExc: CgsuiteException =>
              // TODO nestedExc.setInvocationTarget(qualifiedName)
              throw nestedExc
            case nestedExc => throw InputException(s"Error in call to `$qualifiedName`: ${nestedExc.getMessage}", nestedExc)
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
    isStatic: Boolean,
    isOverride: Boolean
    )
    (fn: (Any, Any) => Any) extends Method {

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
    val isOverride = false

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
    logger.debug(s"$logPrefix Setting URL: $url")
    this.url = url
    unload()
  }

  def unload() {
    logger.debug(s"$logPrefix Unloading.")
    if (classInfoRef != null) {
      classInfoRef.nestedClasses.values foreach { _.unload() }
      classInfoRef = null
    }
    this.loading = false
    this.transpositionTable.clear()
  }

  def lookupMethod(id: Symbol): Option[CgscriptClass#Method] = {
    ensureLoaded()
    classInfo.allMethodsInScope.get(id)
  }

  def lookupNestedClass(id: Symbol): Option[CgscriptClass] = {
    ensureLoaded()
    classInfo.allNestedClassesInScope.get(id)
  }

  def ensureLoaded() {
    if (classInfoRef == null) {
      if (url == null) {
        sys.error("URL not set: " + logPrefix)
      }
      load(url)
    }
  }

  private def load(url: URL) {

    if (loading) {
      sys.error("circular class definition?: " + url)
    }
    loading = true

    logger.debug(s"$logPrefix Loading class from URL: $url")

    val in = url.openStream()
    val tree = try {
      ParserUtil.parseCU(in, url.toString)
    } finally {
      in.close()
    }

    logger.debug(s"$logPrefix Parse tree: ${tree.toStringTree}")

    assert(tree.getType == EOF)

    val node = ClassDeclarationNode(tree.getChild(1))
    logger.debug(s"$logPrefix Node: $node")
    declareClass(node)

    logger.debug(s"$logPrefix Done loading class from URL: $url")

  }

  private def declareClass(node: ClassDeclarationNode) {

    val modifiers = node.modifiers.map { _.modifier }.toSet

    val (supers, nestedClasses, methods, constructor) = {

      if (Object.isLoaded) { // Hack to bootstrap Object

        val supers = {
          if (node.extendsClause.isEmpty) {
            Seq(if (node.isEnum) Enum else Object)
          } else {
            node.extendsClause map {
              case IdentifierNode(tree, superId) =>
                // Try looking this id up two ways:
                // First, if this is a nested class, then look it up as some other nested class
                // of this class's enclosing class;
                // Then try looking it up as a global class.
                enclosingClass flatMap { _ lookupNestedClass superId } getOrElse {
                  pkg lookupClass superId getOrElse {
                    CgscriptPackage lookupClass superId getOrElse {
                      throw InputException(s"Unknown superclass: `${superId.name}`", tree)
                    }
                  }
                }
              case node: DotNode =>
                node.elaborate(ElaborationDomain.empty(Some(pkg)))
                Option(node.classResolution) getOrElse {
                  sys.error("not found")
                }
            }
          }
        }
        supers foreach { _.ensureLoaded() }

        val localMethods = node.methodDeclarations map parseMethod
        val localNestedClasses = node.nestedClassDeclarations map { decl =>
          val newClass = new CgscriptClass(pkg, Some(this), decl.id.id)
          (decl.id.id, newClass)
        } toMap
        val constructor = node.constructorParams map { t =>
          val parameters = parseParameterList(t)
          systemClass match {
            case None => UserConstructor(id, parameters)
            case Some(cls) =>
              val externalParameterTypes = parameters map { _.paramType.javaClass }
              SystemConstructor(id, parameters, cls.getConstructor(externalParameterTypes : _*))
          }
        }
        val localMembers = localMethods ++ localNestedClasses

        // Check for duplicate methods.

        localMembers groupBy { _._1 } find { _._2.size > 1 } foreach { case (memberId, _) =>
          throw InputException(s"Member `${memberId.name}` is declared twice in class `$qualifiedName`.")
        }

        // TODO Check for unresolved superclass method conflicts
        // TODO Check for duplicate local method names
        val superMethods = supers flatMap { _.classInfo.methods } filterNot { _._1.name startsWith "super$" }
        val superNestedClasses = supers flatMap { _.classInfo.nestedClasses } filterNot { _._1.name startsWith "super$" }
        val superMembers = superMethods ++ superNestedClasses

        // Check for conflicting superclass methods.

        val mostSpecificSuperMembers = superMembers groupBy { _._1 } map { case (superId, instances) =>
          val mostSpecificInstances = instances.distinct filterNot { case (_, member) =>
            // Filter out this declaration if there exists a strict subclass that also declares this method
            // (that one will override)
            instances.exists { case (_, other) =>
              other != member && other.declaringClass.ancestors.contains(member.declaringClass)
            }
          }
          // If there are multiple most specific instances (so that neither one overrides the other),
          // and this method isn't redeclared by the loading class, that's an error
          if (mostSpecificInstances.size > 1 && !localMembers.exists { case (localId, _) => localId == superId }) {
            val superclassNames = mostSpecificInstances map { case (_, superMember) => s"`${superMember.declaringClass.qualifiedName}`" }
            throw InputException(
              s"Member `${superId.name}` needs to be declared explicitly in class `$qualifiedName`, " +
                s"because it is defined in multiple superclasses (${superclassNames mkString ", "})"
            )
          }
          (superId, mostSpecificInstances)
        }

        val resolvedSuperMembers = mostSpecificSuperMembers collect {
          case (_, instances) if instances.size == 1 => instances.head
        }

        // TODO What if there are multiple resolved supermethods? How do we define `super.` syntax in that case?

        val renamedSuperMembers = resolvedSuperMembers map { case (superId, superMember) =>
          (Symbol("super$" + superId.name), superMember)
        }

        // override modifier validation.
        // TODO for Nested classes too!

        localMethods foreach { case (methodId, method) =>
          if (method.isOverride) {
            if (!superMethods.exists { case (superId, _) => superId == methodId }) {
              throw InputException(s"Method `${method.qualifiedName}` is declared with `override` but overrides nothing.")
            }
          } else {
            superMethods find { case (superId, _) => superId == methodId } match {
              case None =>
              case Some((_, superMethod)) =>
                throw InputException(s"Method `${method.qualifiedName}` must be declared with `override`, since it overrides `${superMethod.qualifiedName}`.")
            }
          }
        }

        val allMembers = resolvedSuperMembers ++ renamedSuperMembers ++ localMembers
        val (allMethods, allNestedClasses) = allMembers partition { _._2.isInstanceOf[CgscriptClass#Method] }

        ( supers,
          allNestedClasses mapValues { _.asInstanceOf[CgscriptClass] },
          allMethods mapValues { _.asInstanceOf[CgscriptClass#Method] },
          constructor
        )

      } else {

        // We're loading Object right now!
        val localMethods = node.methodDeclarations map parseMethod
        (Seq.empty[CgscriptClass], Map.empty[Symbol, CgscriptClass], localMethods.toMap, None)

      }

    }

    classInfoRef = new ClassInfo(
      modifiers,
      supers,
      nestedClasses,
      methods,
      constructor,
      node.ordinaryInitializers,
      node.staticInitializers,
      node.enumElements
    )
    loading = false

    // Create the class object
    classObjectRef = new ClassObject(CgscriptClass.this)

    // Declare nested classes
    node.nestedClassDeclarations foreach { decl =>
      val id = decl.id.id
      classInfo.nestedClasses(id).declareClass(decl)
    }

    // Elaborate methods
    constructor foreach { _.elaborate() }
    methods foreach { case (_, method) => method.elaborate() }

    // TODO Validate that singletons have no constructor
    // TODO Validate that singletons cannot be subclassed

    // Singleton construction
    if (isSingleton) {
      if (enclosingClass.isDefined) {
        sys.error("Nested singleton classes are not yet supported.")
      }
      if (qualifiedName == "game.Zero") {
        singletonInstanceRef = ZeroImpl
      } else {
        singletonInstanceRef = new StandardObject(this, Array.empty)
      }
    } else {
      singletonInstanceRef = null
    }

    // Enum construction
    node.enumElements foreach { element =>
      classObjectRef.vars(classInfoRef.staticVarOrdinals(element.id.id)) = new EnumObject(this, element.id.id.name)
    }

    // Big temporary hack to populate Left and Right
    if (qualifiedName == "game.Player") {
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Left)) = Left
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Right)) = Right
    }
    if (qualifiedName == "game.Side") {
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Onside)) = Onside
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Offside)) = Offside
    }
    if (qualifiedName == "cgsuite.util.Symmetry") {
      import Symmetry._
      Map('Identity -> Identity, 'Inversion -> Inversion, 'HorizontalFlip -> HorizontalFlip, 'VerticalFlip -> VerticalFlip,
        'Transpose -> Transpose, 'AntiTranspose -> AntiTranspose, 'ClockwiseRotation -> ClockwiseRotation,
        'AnticlockwiseRotation -> AnticlockwiseRotation) foreach { case (symId, value) =>
        classObjectRef.vars(classInfoRef.staticVarOrdinals(symId)) = value
      }
    }

    // Static declarations - create a domain whose context is the class object
    val initializerDomain = new Domain(null, Some(classObject))
    node.staticInitializers.foreach { node =>
      if (!node.isExternal) {
        val scope = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
        // We intentionally don't elaborate var declarations, since those are already
        // accounted for in the class vars. But we still need to elaborate the RHS of
        // the assignment.
        node.body match {
          case AssignToNode(_, _, expr, true) => expr.elaborate(scope)
          case evalNode => evalNode.elaborate(scope)
        }
        node.body.evaluate(initializerDomain)
      }
    }

    node.ordinaryInitializers.foreach { _.body elaborate ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None) }

  }

  private def parseMethod(node: MethodDeclarationNode): (Symbol, Method) = {

    val name = node.idNode.id.name

    val (autoinvoke, parameters) = node.parameters match {
      case Some(n) => (false, parseParameterList(n))
      case None => (true, Seq.empty)
    }

    val newMethod = {
      if (node.isExternal) {
        logger.debug(s"$logPrefix Declaring external method: $name")
        SpecialMethods.specialMethods.get(qualifiedName + "." + name) match {
          case Some(fn) =>
            logger.debug(s"$logPrefix   It's a special method.")
            new ExplicitMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, node.isOverride)(fn)
          case None =>
            val externalName = name.updated(0, name(0).toLower)
            val externalParameterTypes = parameters map { _.paramType.javaClass }
            val externalMethod = javaClass.getMethod(externalName, externalParameterTypes: _*)
            logger.debug(s"$logPrefix   It's a Java method via reflection: $externalMethod")
            new SystemMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, node.isOverride, externalMethod)
        }
      } else {
        logger.debug(s"$logPrefix Declaring user method: $name")
        val body = node.body getOrElse { sys.error("no body") }
        new UserMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, node.isOverride, body)
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

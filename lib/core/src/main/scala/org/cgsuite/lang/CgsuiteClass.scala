package org.cgsuite.lang

import java.net.URL

import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.util._

import scala.collection.mutable

object CgsuiteClass {

  private var nextClassOrdinal = 0
  private[lang] def newClassOrdinal = {
    val ord = nextClassOrdinal
    nextClassOrdinal += 1
    ord
  }

  val Object = CgsuitePackage.lookupClassByName("Object").get
  val Class = CgsuitePackage.lookupClassByName("Class").get
  val Boolean = CgsuitePackage.lookupClassByName("Boolean").get
  val Coordinates = CgsuitePackage.lookupClassByName("Coordinates").get
  val List = CgsuitePackage.lookupClassByName("List").get
  val Map = CgsuitePackage.lookupClassByName("Map").get
  val Set = CgsuitePackage.lookupClassByName("Set").get
  val String = CgsuitePackage.lookupClassByName("String").get

  val Grid = CgsuitePackage.lookupClassByName("Grid").get
  val Strip = CgsuitePackage.lookupClassByName("Strip").get
  val Symmetry = CgsuitePackage.lookupClassByName("Symmetry").get

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
      case _: Zero => Zero
      case _: Integer => Integer
      case _: DyadicRationalNumber => DyadicRational
      case _: RationalNumber => Rational
      case _: Nimber => Nimber
      case _: NumberUpStar => NumberUpStar
      case _: CanonicalShortGame => CanonicalShortGame
      case _: Player => Player
      case _: Boolean => Boolean
      case _: Coordinates => Coordinates
      case _: String => String
      case _: Grid => Grid
      case _: Strip => Strip
      case _: Map[_, _] => Map
      case _: Seq[_] => List
      case _: Set[_] => Set
      case _: Symmetry => Symmetry
    }
  }

  def is(x: Any, cls: CgsuiteClass) = of(x).ancestors.contains(cls)

  // Various conversions from Java types to CGScript types.
  def internalize(obj: AnyRef) = {
    obj match {
      case x: java.lang.Integer => SmallInteger(x.intValue)
      case null => Nil
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

  val classOrdinal = CgsuiteClass.newClassOrdinal
  val javaClass = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }
  val qualifiedName = Symbol(pkg.qualifiedName + "." + id.name)

  class ClassInfo(
    val modifiers: Set[Modifier.Value],
    val supers: Seq[CgsuiteClass],
    val methods: Map[Symbol, CgsuiteClass#Method],
    val constructor: Option[CgsuiteClass#Constructor],
    val initializers: Seq[InitializerNode],
    val staticInitializers: Seq[InitializerNode]
    ) {

    val properAncestors: Seq[CgsuiteClass] = supers.flatMap { _.classInfo.ancestors }.distinct
    val ancestors = properAncestors :+ CgsuiteClass.this
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
    def call(obj: Any, args: Array[Any]): Any

    val declaringClass = CgsuiteClass.this
    val qualifiedId = Symbol(declaringClass.qualifiedName.name + "." + id.name)
    val signature = s"${qualifiedId.name}(${parameters.map { _.signature }.mkString(", ")})"
    val ordinal = CallSite.newCallSiteOrdinal

    def elaborate(): Unit = {
      println(s"Elaborating ${qualifiedId.name}")
      val scope = new Scope(Some(pkg), classInfo.allSymbolsInScope, mutable.AnyRefMap(), mutable.Stack(mutable.HashSet()))
      parameters foreach { param =>
        param.defaultValue foreach { _.elaborate(scope) }
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
    parameters: Seq[MethodParameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    private val reflect = Symbol(s"Reflect [${javaMethod.toString}]")

    def call(obj: Any, args: Array[Any]): Any = {
      // TODO Validation!
      // TODO Named args should work here too
      val target = if (isStatic) null else obj.asInstanceOf[AnyRef]
      assert(
        target == null || CgsuiteClass.of(target).ancestors.contains(declaringClass),
        (CgsuiteClass.of(target), declaringClass)
      )
      try {
        Profiler.start(reflect)
        CgsuiteClass.internalize(javaMethod.invoke(target, args.asInstanceOf[Array[AnyRef]] : _*))
      } catch {
        case exc: IllegalArgumentException =>
          throw new InputException(s"Invalid parameters for method $qualifiedName.")
      } finally {
        Profiler.stop(reflect)
      }
    }

  }

  case class Constructor(
    id: Symbol,
    parameters: Seq[MethodParameter]
  ) extends Method with CallSite {

    private val invokeConstructor = Symbol(s"InvokeConstructor [${qualifiedId.name}]")

    def autoinvoke = false
    def isStatic = false

    def call(args: Array[Any]): Any = {
      // TODO Superconstructor
      // TODO Parse var initializers
      Profiler.start(invokeConstructor)
      try {
        if (ancestors.contains(CgsuiteClass.Game))
          new GameObject(CgsuiteClass.this, args)
        else
          new StandardObject(CgsuiteClass.this, args)
      } finally {
        Profiler.stop(invokeConstructor)
      }
    }

    def call(obj: Any, args: Array[Any]): Any = call(args)

  }

  case class ExplicitMethod0(id: Symbol, autoinvoke: Boolean, isStatic: Boolean)(fn: Any => Any) extends Method {

    def parameters = Seq.empty
    def call(obj: Any, args: Array[Any]): Any = {
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

    println(s"Done loading class: ${id.name}")

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
              case IdentifierNode(tree, superId) => CgsuitePackage.lookupClass(superId) getOrElse {
                throw InputException(s"Unknown superclass: `${superId.name}`", tree)
              }
              case node: DotNode => CgsuitePackage.lookupClass(node.asQualifiedClassName.get) getOrElse { sys.error("not found") }
            }
        }
        supers.foreach { _.ensureLoaded() }
        // TODO Check for unresolved superclass method conflicts
        // TODO Check for duplicate local method names
        val superMethods = supers.flatMap { _.classInfo.methods }
        val localMethods = node.methodDeclarations.map { parseMethod }
        val constructor = node.constructorParams.map { t => Constructor(id, parseParameterList(t)) }
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
        (Seq.empty, methodsForObject(), None)

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
    classObjectRef = new ClassObject(CgsuiteClass.this)

    // Elaborate methods
    constructor foreach { _.elaborate() }
    methods foreach { case (_, method) => method.elaborate() }

    // Big temporary hack to populate Left and Right
    if (qualifiedName.name == "game.Player") {
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Left)) = Left
      classObjectRef.vars(classInfoRef.staticVarOrdinals('Right)) = Right
    }
    if (qualifiedName.name == "cgsuite.util.Symmetry") {
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
      if (!node.isExternal)
        node.body.evaluate(initializerDomain)
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
        val externalName = name.updated(0, name(0).toLower)
        val externalParameterTypes = parameters map { _.paramType.javaClass }
        println(s"Declaring external method: $name => $externalName")
        val externalMethod = javaClass.getMethod(externalName, externalParameterTypes : _*)
        println(s"Here it is: $externalMethod")
        new SystemMethod(node.idNode.id, parameters, autoinvoke, node.isStatic, externalMethod)
      } else {
        println(s"[${qualifiedName.name}] Declaring user method: $name")
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

  private def methodsForObject(): Map[Symbol, CgsuiteClass#Method] = {
    Map(Symbol("Class") -> ExplicitMethod0(Symbol("Class"), autoinvoke = true, isStatic = false) {
      CgsuiteClass.of(_).classObject
    })
  }

  override def toString = s"<class ${qualifiedName.name}>"

}

case class MethodParameter(id: Symbol, paramType: CgsuiteClass, defaultValue: Option[EvalNode]) {
  val signature = paramType.qualifiedName.name + " " + id.name + (if (defaultValue.isDefined) "?" else "")
  var methodScopeIndex = -1
}

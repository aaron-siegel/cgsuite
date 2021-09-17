package org.cgsuite.lang

import java.io.{ByteArrayInputStream, File}
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.nio.charset.StandardCharsets

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.antlr.runtime.tree.Tree
import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGameOps
import org.cgsuite.exception.{CgsuiteException, EvalException}
import org.cgsuite.lang.Node.treeToRichTree
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output._
import org.cgsuite.util._
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.language.{existentials, postfixOps}

object CgscriptClass {

  private[lang] val logger = Logger(LoggerFactory.getLogger(classOf[CgscriptClass]))

  private var nextClassOrdinal = 0
  private[lang] def newClassOrdinal = {
    val ord = nextClassOrdinal
    nextClassOrdinal += 1
    ord
  }

  logger debug "Declaring system classes."

  SystemClassRegistry.allSystemClasses foreach { case (name, scalaClass) =>
    declareSystemClass(name, Some(scalaClass))
  }

  logger debug "Declaring folders."

  CgscriptClasspath.declareFolders()

  val Object = CgscriptPackage.lookupClassByName("Object").get
  val Class = CgscriptPackage.lookupClassByName("Class").get
  val Enum = CgscriptPackage.lookupClassByName("Enum").get
  val Game = CgscriptPackage.lookupClassByName("Game").get
  val ImpartialGame = CgscriptPackage.lookupClassByName("ImpartialGame").get
  val List = CgscriptPackage.lookupClassByName("List").get
  lazy val NothingClass = CgscriptPackage.lookupClassByName("Nothing").get
  lazy val HeapRuleset = CgscriptPackage.lookupClassByName("game.heap.HeapRuleset").get

  Object.ensureInitialized()

  def of(x: Any): CgscriptClass = {
    x match {
      case null => NothingClass
      case so: StandardObject => so.cls
      case _ => classLookupCache.getOrElseUpdate(x.getClass, resolveToSystemClass(x))
    }
  }

  def is(x: Any, cls: CgscriptClass) = of(x).ancestors.contains(cls)

  // Various conversions from Java types to CGScript types.
  def internalize(obj: AnyRef) = {
    obj match {
      case x: java.lang.Integer => SmallInteger(x.intValue)
      case _ => obj
    }
  }

  def clearAll() {
    CanonicalShortGameOps.reinit()
    MisereCanonicalGameOps.reinit()
    Resolver.clearAll()
    CgscriptPackage.classDictionary.values foreach { _.unload() }
    Object.ensureInitialized()
  }

  def instanceToOutput(x: Any): Output = {
    CgscriptClass.of(x).classInfo.toOutputMethod.call(x, Array.empty) match {
      case str: String => new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, str)
      case output: Output => output
      case y =>
        throw EvalException(
          s"`ToOutput` method of class `${CgscriptClass.of(x).qualifiedName}` returned an object of type `${CgscriptClass.of(y).qualifiedName}` (expecting type `Output` or `String`)"
        )
    }
  }

  def instanceToDefaultOutput(x: Any): StyledTextOutput = {
    val sto = new StyledTextOutput
    x match {
      case stdObj: StandardObject =>
        sto append stdObj.toDefaultOutput
      case _ =>
        val cls = CgscriptClass of x
        sto appendText s"${cls.name}.instance"
    }
    sto
  }

  private[lang] def declareSystemClass(name: String, scalaClass: Option[Class[_]] = None, explicitDefinition: Option[String] = None) {

    val path = name.replace('.', '/')
    val classdef: CgscriptClassDef = {
      explicitDefinition match {
        case Some(text) => ExplicitClassDef(text)
        case None => UrlClassDef(CgscriptClasspath.systemDir, getClass.getResource(s"resources/$path.cgs"))
      }
    }
    val components = name.split("\\.").toSeq
    val pkg = CgscriptPackage.root lookupSubpackage (components dropRight 1) getOrElse {
      sys.error("Cannot find package: " + (components dropRight 1))
    }
    pkg.declareClass(Symbol(components.last), classdef, scalaClass)

  }

  private[lang] def resolveToSystemClass(x: Any): CgscriptClass = {
    // This is slow, but we cache the results so that it only happens once
    // per distinct (Java) type witnessed.
    val systemClass = SystemClassRegistry.typedSystemClasses find { case (_, cls) => cls isAssignableFrom x.getClass }
    systemClass flatMap { case (name, _) => CgscriptPackage.lookupClassByName(name) } getOrElse {
      sys.error(s"Could not determine CGScript class for object of type `${x.getClass}`: $x")
    }
  }

  private val classLookupCache = mutable.AnyRefMap[Class[_], CgscriptClass]()

}

class CgscriptClass(
  val pkg: CgscriptPackage,
  val classdef: CgscriptClassDef,
  override val id: Symbol,
  val systemClass: Option[Class[_]] = None
  ) extends Member with LazyLogging { thisClass =>

  import CgscriptClass._

  private val locallyDefinedNestedClasses: mutable.Map[Symbol, CgscriptClass] = mutable.Map()

  ///////////////////////////////////////////////////////////////
  // Basic properties derivable without parsing

  val classOrdinal: Int = newClassOrdinal        // TODO How to handle for nested classes??

  val enclosingClass: Option[CgscriptClass] = classdef match {
    case NestedClassDef(cls, _) => Some(cls)
    case _ => None
  }

  val topClass: CgscriptClass = enclosingClass match {
    case Some(cls) => cls.topClass
    case None => this
  }

  val isTopClass: Boolean = this == topClass

  val url: Option[URL] = classdef match {
    case UrlClassDef(_, x) => Some(x)
    case _ => None
  }

  val javaClass: Class[_] = systemClass match {
    case Some(cls) => cls
    case None => classOf[StandardObject]
  }

  override val declaringClass = enclosingClass.orNull

  val nameAsFullyScopedMember: String = id.name

  val name: String = enclosingClass match {
    case Some(encl) => encl.name + "." + nameAsFullyScopedMember
    case None => nameAsFullyScopedMember
  }

  val qualifiedName: String = {
    if (pkg.isRoot)
      name
    else
      s"${pkg.qualifiedName}.$name"
  }

  val qualifiedId: Symbol = Symbol(qualifiedName)

  val isPackageObject = id == 'constants

  override def toString = s"\u27ea$qualifiedName\u27eb"

  private val logPrefix = f"[$classOrdinal%3d: $qualifiedName%s]"

  private[cgsuite] def logDebug(message: => String): Unit = logger debug s"$logPrefix $message"

  logDebug(s"Formed new class with classdef: $classdef")

  private var stage: LifecycleStage.Value = LifecycleStage.New

  ///////////////////////////////////////////////////////////////
  // ClassInfo properties and lookups (post-declaration)

  private var classInfoRef: ClassInfo = _
  private var scriptObjectRef: Script = _
  private var classObjectRef: ClassObject = _
  private var singletonInstanceRef: Any = _
  var initializerLocalVariableCount: Int = 0

  private[cgsuite] val transpositionCache = new TranspositionCache()

  def isLoaded = classInfoRef != null

  def classInfo: ClassInfo = {
    ensureDeclared()
    classInfoRef
  }

  def classObject: ClassObject = {
    ensureInitialized()
    classObjectRef
  }

  def scriptObject: Script = {
    ensureInitialized()
    scriptObjectRef
  }

  // Singleton instance is instantiated lazily
  def singletonInstance: Any = {
    ensureSingletonInstance()
    singletonInstanceRef
  }

  def isScript = scriptObject != null

  def isMutable = classInfo.modifiers.hasMutable

  def isSingleton = classInfo.modifiers.hasSingleton

  def isSystem = classInfo.modifiers.hasSystem

  def constructor = classInfo.constructor

  def evalMethodOpt = lookupMethod('Eval)

  def ancestors = classInfo.ancestors

  def properAncestors = classInfo.properAncestors

  def initializers = classInfo.ordinaryInitializers

  def declNode: Option[ClassDeclarationNode] = classdef match {
    case NestedClassDef(_, node) => Some(node)
    case _ => Some(classInfo.declNode)
  }

  def idNode = classInfo.idNode

  ///////////////////////////////////////////////////////////////
  // Lifecycle management

  def unload() {
    if (this.stage != LifecycleStage.Unloaded) {
      logDebug(s"Building unload list.")
      val unloadList = mutable.HashSet[CgscriptClass]()
      topClass.buildUnloadList(unloadList)
      logDebug(s"Unloading ${unloadList.size} classes.")
      unloadList foreach { _.doUnload() }
      Resolver.clearAll()
    }
  }

  private def doUnload() {
    logDebug(s"Unloading.")
    classInfoRef = null
    scriptObjectRef = null
    singletonInstanceRef = null
    this.transpositionCache.clear()
    this.stage = LifecycleStage.Unloaded
  }

  private def buildUnloadList(list: mutable.HashSet[CgscriptClass]): Unit = {

    if (list contains this)
      return

    list += this

    // Add the topClass of any derived classes
    CgscriptPackage.classDictionary.values foreach { cls =>
      addDerivedClassesToUnloadList(list, cls)
    }

    // Recursively add nested classes
    locallyDefinedNestedClasses.values foreach { _.buildUnloadList(list )}

  }

  private def addDerivedClassesToUnloadList(list: mutable.HashSet[CgscriptClass], cls: CgscriptClass): Unit = {
    if (cls.classInfoRef != null && (cls.classInfoRef.supers contains this)) {
      cls.topClass.buildUnloadList(list)
    }
    // Recurse through nested classes
    cls.locallyDefinedNestedClasses.values foreach { addDerivedClassesToUnloadList(list, _) }
  }

  // TODO We still need to handle method groups with multiple methods.
  def lookupMethod(id: Symbol): Option[CgscriptClass#Method] = {
    ensureInitialized()
    classInfo.allMethodGroups.get(id) map { _.methods.head }
  }

  def lookupNestedClass(id: Symbol): Option[CgscriptClass] = {
    ensureInitialized()
    classInfo.allNestedClasses.get(id)
  }

  def lookupVar(id: Symbol): Option[CgscriptClass#Var] = {
    ensureInitialized()
    classInfo.instanceVarLookup.get(id)
  }

  def lookupMember(id: Symbol): Option[Member] = {
    ensureInitialized()
    lookupMethod(id) orElse lookupNestedClass(id) orElse lookupVar(id)
  }

  def ensureDeclared(): Unit = {
    if (stage != LifecycleStage.Declared && stage != LifecycleStage.Initialized && stage != LifecycleStage.Initializing) {
      enclosingClass match {
        case Some(cls) =>
          cls.ensureInitialized()
          if (stage != LifecycleStage.Initialized)
            throw EvalException(s"Class no longer exists: `$qualifiedName`")
        case _ => declare()
      }
    }
  }

  def ensureInitialized(): Unit = {
    ensureDeclared()
    if (stage != LifecycleStage.Initialized) {
      enclosingClass match {
        case Some(cls) =>
          cls.ensureInitialized()
          if (stage != LifecycleStage.Initialized)
            throw EvalException(s"Class no longer exists: `$qualifiedName`")
        case _ => initialize()
      }
    }
  }

  def ensureSingletonInstance(): Unit = {
    ensureInitialized()
    if (singletonInstanceRef == null)   // TODO This doesn't work for Nothing.
      constructSingletonInstance()
  }

  private[this] def constructSingletonInstance(): Unit = {
    if (isSingleton) {
      logDebug(s"Constructing singleton instance.")
      if (enclosingClass.isDefined) {
        sys.error("Nested singleton classes are not yet supported.")
      }
      singletonInstanceRef = {
        qualifiedName match {
          case "game.Zero" => ZeroImpl
          case "cgsuite.lang.Nothing" => null
          case "cgsuite.util.output.EmptyOutput" => EmptyOutput
          // TODO There is some code duplication here with general object instantiation (search for "GameObject")
          case _ if ancestors contains ImpartialGame => new ImpartialGameObject(this, Array.empty)
          case _ if ancestors contains Game => new GameObject(this, Array.empty)
          case _ if ancestors contains HeapRuleset => new HeapRulesetObject(this, Array.empty)
          case _ => new StandardObject(this, Array.empty)
        }
      }
    } else {
      sys.error("Not a singleton")
    }
  }

  ///////////////////////////////////////////////////////////////
  // Parsing and declaration

  private def parseTree(): Tree = {

    val (in, source) = classdef match {

      case UrlClassDef(_, url) =>
        logDebug(s"Parsing from URL: $url")
        (url.openStream(), new File(url.getFile).getName)

      case ExplicitClassDef(text) =>
        logDebug(s"Parsing from explicit definition.")
        (new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), qualifiedName)

      case NestedClassDef(_, _) =>
        throw EvalException(s"Class no longer exists: `$qualifiedName`")

    }

    try {
      ParserUtil.parseCU(in, source)
    } finally {
      in.close()
    }

  }

  private def declare() {

    logDebug(s"Declaring.")

    if (stage == LifecycleStage.Declaring) {
      // TODO Better error message/handling here?
      sys.error("circular class definition?: " + qualifiedName)
    }

    // Force constants to declare first
    pkg lookupClass 'constants foreach { constantsCls =>
      if (constantsCls != this) constantsCls.ensureDeclared()
    }

    val tree = parseTree()

    logDebug(s"Parsed class: ${tree.toStringTree}")

    try {
      tree.getType match {

        case SCRIPT =>
          val node = StatementSequenceNode(tree.children.head)
          logDebug(s"Script Node: $node")
          declareScript(node)

        case EOF =>
          val node = ClassDeclarationNode(tree.children(1), pkg)
          logDebug(s"Class Node: $node")
          declareClass(node)

      }
    } finally {
      stage = LifecycleStage.Unloaded
    }

    stage = LifecycleStage.Declared

    logDebug(s"Done declaring.")

  }

  private def declareScript(node: StatementSequenceNode): Unit = {

    val domain = ElaborationDomain.empty()
    node.elaborate(domain)
    classInfoRef = null
    classObjectRef = null
    scriptObjectRef = Script(this, node, domain)
    singletonInstanceRef = null

  }

  private def declareClass(declNode: ClassDeclarationNode): Unit = {

    stage = LifecycleStage.Declaring

    logDebug(s"Declaring class.")

    classInfoRef = new ClassInfo(declNode)

    logDebug(s"Done declaring class.")

    stage = LifecycleStage.Declared

  }

  class ClassInfo(val declNode: ClassDeclarationNode) {

    if (declNode.idNode.id.name != nameAsFullyScopedMember)
      throw EvalException(s"Class name does not match filename: `${declNode.idNode.id.name}` (was expecting `$nameAsFullyScopedMember`)", declNode.idNode.tree)

    val idNode: IdentifierNode = declNode.idNode

    val modifiers: Modifiers = declNode.modifiers

    val ordinaryInitializers: Vector[InitializerNode] = declNode.ordinaryInitializers

    val staticInitializers: Vector[InitializerNode] = declNode.staticInitializers

    val enumElementNodes: Vector[EnumElementNode] = declNode.enumElements

    val supers: Vector[CgscriptClass] = {

      if (thisClass == Object) {

        Vector.empty    // Object has no supers

      } else if (declNode.extendsClause.isEmpty) {

        Vector(if (declNode.isEnum) Enum else Object)

      } else {

        declNode.extendsClause map {
          case IdentifierNode(tree, superId) =>
            // Try looking this id up two ways:
            // First, if this is a nested class, then look it up as some other nested class
            // of this class's enclosing class;
            // Then try looking it up as a global class.
            enclosingClass flatMap { _ lookupNestedClass superId } getOrElse {
              pkg lookupClass superId getOrElse {
                CgscriptPackage lookupClass superId getOrElse {
                  throw EvalException(s"Unknown superclass: `${superId.name}`", tree)
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

    supers foreach { _.ensureDeclared() }

    val properAncestors: Vector[CgscriptClass] = supers.reverse.flatMap { _.classInfo.ancestors }.distinct

    val ancestors = properAncestors :+ CgscriptClass.this

    val localInstanceVars: Vector[Var] = ordinaryInitializers collect {
      case declNode: VarDeclarationNode if !declNode.modifiers.hasStatic =>
        Var(declNode.idNode, Some(declNode), declNode.modifiers)
    }

    val localMethods: Vector[Method] = declNode.methodDeclarations map { parseMethod(_, declNode.modifiers) }

    val localNestedClasses: Vector[CgscriptClass] = declNode.nestedClassDeclarations map { decl =>
      val id = decl.idNode.id
      locallyDefinedNestedClasses getOrElseUpdate (id, new CgscriptClass(pkg, NestedClassDef(thisClass, decl), id))
    }

    val constructor: Option[Constructor] = declNode.classParameterNodes map { node =>
      val parameters = node.toParameters
      systemClass match {
        case None => UserConstructor(declNode.idNode, parameters)
        case Some(cls) =>
          val externalParameterTypes = parameters map { _.paramType.javaClass }
          SpecialMethods.specialMethods get qualifiedName match {
            case Some(fn) =>
              ExplicitConstructor(declNode.idNode, parameters)(fn)
            case None =>
              SystemConstructor(declNode.idNode, parameters, cls.getConstructor(externalParameterTypes : _*))
          }
      }
    }

    val localMembers: Vector[Member] = localMethods ++ localNestedClasses

    // Check for duplicate members.
    /*
    localMembers groupBy { _.id } find { _._2.size > 1 } foreach { case (memberId, _) =>
      throw EvalException(s"Member `${memberId.name}` is declared twice in class `$qualifiedName`", declNode.tree)
    }
    */

    // Check for conflicting superclass methods.
    /*
    val mostSpecificSuperMembers: Map[Symbol, Vector[(Symbol, Member)]] = superMembers groupBy { _._1 } map { case (superId, instances) =>
      val mostSpecificInstances = instances.distinct filterNot { case (_, member) =>
        // Filter out this declaration if there exists a strict subclass that also declares this method
        // (that one will override)
        instances.exists { case (_, other) =>
          other != member && other.declaringClass.ancestors.contains(member.declaringClass)
        }
      }
      // If there are multiple most specific instances (so that neither one overrides the other),
      // and this method isn't redeclared by the loading class, that's an error
      if (mostSpecificInstances.size > 1 && !localMembers.exists { _.id == superId }) {
        val superclassNames = mostSpecificInstances map { case (_, superMember) => s"`${superMember.declaringClass.qualifiedName}`" }
        throw EvalException(
          s"Member `${superId.name}` needs to be declared explicitly in class `$qualifiedName`, " +
            s"because it is defined in multiple superclasses (${superclassNames mkString ", "})"
        )
      }
      (superId, mostSpecificInstances)
    }

    val resolvedSuperMembers: Map[Symbol, Member] = mostSpecificSuperMembers collect {
      case (_, instances) if instances.size == 1 => instances.head
    }

    // TODO What if there are multiple resolved supermethods? How do we define `super.` syntax in that case?

    val renamedSuperMembers: Map[Symbol, Member] = resolvedSuperMembers map { case (superId, superMember) =>
      (Symbol("super$" + superId.name), superMember)
    }

    // override modifier validation.
    // TODO for Nested classes too!

    localMethods foreach { method =>
      if (method.isOverride) {
        if (!superMethods.exists { case (superId, _) => superId == method.id }) {
          throw EvalException(
            s"Method `${method.qualifiedName}` overrides nothing",
            token = Some(method.idNode.token)
          )
        }
      } else {
        superMethods find { case (superId, _) => superId == method.id } match {
          case None =>
          case Some((_, superMethod)) =>
            throw EvalException(
              s"Method `${method.qualifiedName}` must be declared with `override`, since it overrides `${superMethod.qualifiedName}`",
              token = Some(method.idNode.token)
            )
        }
      }
    }
    */

    val inheritedMembers: Vector[Member] = (supers flatMap { _.classInfo.allMembers }).distinct

    val allMembers: Vector[Member] = localMembers ++ inheritedMembers

    // TODO It'd be nice if nested class constructors could join a method group

    lazy val allMethodGroups: Map[Symbol, MethodGroup] = allMembers groupBy { _.id } collect {

      case (id, members) if members.head.isInstanceOf[CgscriptClass#Method] =>

        /*
        if (members exists { !_.isInstanceOf[CgscriptClass#Method] }) {
          throwExceptionForDuplicateSymbol(id, members)
        }
         */
        val methods = members map { _.asInstanceOf[CgscriptClass#Method] }
        val locallyDefinedMethods = methods filter { _.declaringClass == thisClass }

        if (locallyDefinedMethods.nonEmpty) {
          // Check for "static consistency": if any method is static, all must be
          val isStatic = locallyDefinedMethods.head.isStatic
          if (methods exists { _.isStatic != isStatic })
            throw EvalException(
              s"Inconsistent use of `static` for method `${locallyDefinedMethods.head.qualifiedName}`",
              token = locallyDefinedMethods.head.declNode map { _.idNode.token }
            )
        }

        // TODO Duplicate method signatures
        val checkedMethods = validateMethods(methods)
        (id, MethodGroup(id, checkedMethods))

    }

    lazy val allNestedClasses: Map[Symbol, CgscriptClass] = allMembers groupBy { _.id } collect {

      case (id, classes) if classes.head.isInstanceOf[CgscriptClass] =>

        //if (classes.size > 1)
        //  throwExceptionForDuplicateSymbol(id, classes)
        (id, classes.head.asInstanceOf[CgscriptClass])

    }

    lazy val staticVars: Vector[Var] = declNode.staticInitializers collect {
      case declNode: VarDeclarationNode if declNode.modifiers.hasStatic =>
        Var(declNode.idNode, Some(declNode), declNode.modifiers)
    }

    lazy val staticVarLookup: Map[Symbol, Var] = staticVars map { v => (v.id, v) } toMap

    lazy val enumElements: Vector[Var] = declNode.enumElements map { node =>
      Var(node.idNode, Some(node), node.modifiers)
    }

    lazy val inheritedInstanceVars: Vector[CgscriptClass#Var] = supers.flatMap { _.classInfo.allInstanceVars }.distinct
    lazy val constructorParamVars: Vector[Var] = constructor match {
      case Some(ctor) => ctor.parameters map { param => Var(param.idNode, Some(declNode), Modifiers.none, isConstructorParam = true) }
      case None => Vector.empty
    }
    lazy val allInstanceVars: Vector[CgscriptClass#Var] = constructorParamVars ++ inheritedInstanceVars ++ localInstanceVars
    lazy val allInstanceVarSymbols: Vector[Symbol] = allInstanceVars map { _.id } distinct
    lazy val instanceVarLookup: Map[Symbol, CgscriptClass#Var] = allInstanceVars map { v => (v.id, v) } toMap
    lazy val instanceVarOrdinals: Map[Symbol, Int] = allInstanceVarSymbols.zipWithIndex.toMap

    lazy val staticVarSymbols: Vector[Symbol] = enumElementNodes.map { _.idNode.id } ++ staticVars.map { _.idNode.id }
    lazy val staticVarOrdinals: Map[Symbol, Int] = staticVarSymbols.zipWithIndex.toMap

    lazy val allSymbolsInThisClass: Set[Symbol] = {
      instanceVarOrdinals.keySet ++ staticVarOrdinals.keySet ++ allMethodGroups.keySet ++ allNestedClasses.keySet
    }
    lazy val allSymbolsInClassScope: Vector[Set[Symbol]] = {
      allSymbolsInThisClass +: enclosingClass.map { _.classInfo.allSymbolsInClassScope }.getOrElse(Vector.empty)
    }

    // For efficiency, we cache lookups for some methods that get called in hardcoded locations
    lazy val evalMethod = lookupMethodOrEvalException('Eval)
    lazy val optionsMethod = lookupMethodOrEvalException('Options)
    lazy val optionsForMethod = lookupMethodOrEvalException('OptionsFor)
    lazy val decompositionMethod = lookupMethodOrEvalException('Decomposition)
    lazy val canonicalFormMethod = lookupMethodOrEvalException('CanonicalForm)
    lazy val gameValueMethod = lookupMethodOrEvalException('GameValue)
    lazy val depthHintMethod = lookupMethodOrEvalException('DepthHint)
    lazy val toOutputMethod = lookupMethodOrEvalException('ToOutput)
    lazy val heapOptionsMethod = lookupMethodOrEvalException('HeapOptions)

    private def lookupMethodOrEvalException(id: Symbol): CgscriptClass#Method = {
      lookupMethod(id) getOrElse {
        throw EvalException(s"No method `${id.name}` for class: `$qualifiedName`")
      }
    }

    logDebug(s"Validating class.")

    // Check for duplicate vars (we make an exception for the constructor)

    (inheritedInstanceVars ++ localInstanceVars) groupBy { _.id } foreach { case (varId, vars) =>
      if (vars.size > 1 && (vars exists { !_.isConstructorParam })) {
        val class1 = vars.head.declaringClass
        val class2 = vars(1).declaringClass
        if (class1 == thisClass && class2 == thisClass)
          throw EvalException(s"Variable `${varId.name}` is declared twice in class `$qualifiedName`", declNode.tree)
        else if (class2 == thisClass)
          throw EvalException(s"Variable `${varId.name}` in class `$qualifiedName` shadows definition in class `${class1.qualifiedName}`")
        else
          throw EvalException(s"Variable `${varId.name}` is defined in multiple superclasses: `${class1.qualifiedName}`, `${class2.qualifiedName}`")
      }
    }

    // Check for member/var conflicts

    // TODO We may want to allow defs to override vars in the future - then the vars become private to the
    // superclass. For now, we prohibit it.
/*
    allMethodGroups ++ allNestedClasses foreach { case (id, _) =>
      if (instanceVarLookup contains id)
        throw EvalException(
          s"Member `${id.name}` conflicts with a var declaration in class `$qualifiedName`",
          token = Some(idNode.token)    // TODO Would rather use member.idNode.token but don't have it working yet
        )
    }
*/
    // Check that singleton => no constructor
    if (constructor.isDefined && declNode.modifiers.hasSingleton) {
      throw EvalException(
        s"Class `$qualifiedName` must not have a constructor if declared `singleton`",
        token = Some(idNode.token)
      )
    }

    // Check that no superclass is a singleton
    supers foreach { ancestor =>
      if (ancestor.isSingleton) {
        throw EvalException(
          s"Class `$qualifiedName` may not extend singleton class `${ancestor.qualifiedName}`",
          token = Some(idNode.token)
        )
      }
    }

    // Check that constants classes must be singletons
    if (name == "constants" && !declNode.modifiers.hasSingleton) {
      throw EvalException(
        s"Constants class `$qualifiedName` must be declared `singleton`",
        token = Some(idNode.token)
      )
    }

    if (declNode.modifiers.hasMutable) {
      // If we're mutable, check that no nested class is immutable
      declNode.nestedClassDeclarations foreach { nested =>
        if (!nested.modifiers.hasMutable) {
          throw EvalException(
            s"Nested class `${nested.idNode.id.name}` of mutable class `$qualifiedName` is not declared `mutable`",
            token = Some(nested.idNode.token)
          )
        }
      }
    } else {
      // If we're immutable, check that no superclass is mutable
      supers foreach { spr =>
        if (spr.isMutable) {
          throw EvalException(
            s"Subclass `$qualifiedName` of mutable class `${spr.qualifiedName}` is not declared `mutable`",
            token = Some(idNode.token)
          )
        }
      }
      // ... and that there are no mutable vars
      ordinaryInitializers foreach {
        case declNode: VarDeclarationNode if !declNode.modifiers.hasStatic && declNode.modifiers.hasMutable =>
          throw EvalException(
            s"Class `$qualifiedName` is immutable, but variable `${declNode.idNode.id.name}` is declared `mutable`",
            token = Some(declNode.idNode.token)
          )
        case _ =>
      }
    }

    // Check that immutable class vars are assigned at declaration time
    ordinaryInitializers collect {
      // This is a little bit of a hack, we look for "phantom" constant nodes since those are indicative of
      // a "default" nil value. This could be refactored to be a bit more elegant.
      case VarDeclarationNode(_, AssignToNode(_, idNode, ConstantNode(null, _), AssignmentDeclType.ClassVarDecl), modifiers)
        if !modifiers.hasMutable =>
        throw EvalException(
          s"Immutable variable `${idNode.id.name}` must be assigned a value (or else declared `mutable`)",
          token = Some(idNode.token)
        )
    }

    private def validateMethods(methods: Vector[CgscriptClass#Method]): Vector[CgscriptClass#Method] = {

      // A "signature" is just a list of types.
      val groupedBySignature: Map[Vector[CgscriptClass], Vector[CgscriptClass#Method]] = methods groupBy { method =>
        method.parameters map { _.paramType }
      }

      val resolved = groupedBySignature map { case (_, matchingMethods) =>

        val localDeclarations = matchingMethods filter { _.declaringClass == thisClass }

        if (localDeclarations.size > 1) {
          throw EvalException(
            s"Method `${localDeclarations(1).qualifiedName}` is declared twice with identical signature",
            token = Some(localDeclarations(1).idNode.token)
          )
        }

        if (localDeclarations.nonEmpty) {
          if (matchingMethods.size == 1 && localDeclarations.head.isOverride) {
            throw EvalException(
              s"Method `${localDeclarations.head.qualifiedName}` overrides nothing",
              token = Some(localDeclarations.head.idNode.token)
            )
          }
          if (matchingMethods.size > 1 && !localDeclarations.head.isOverride) {
            throw EvalException(
              s"Method `${localDeclarations.head.qualifiedName}` must be declared with `override`, since it overrides `${matchingMethods(1).qualifiedName}`",
              token = Some(localDeclarations.head.idNode.token)
            )
          }
        }

        val mostSpecificMethods = matchingMethods filterNot { method =>
          // Filter out a declaration if there exists a strict subclass that declares a method
          // with exactly the same signature
          matchingMethods exists { otherMethod =>
            val ancestors = {
              if (otherMethod.declaringClass == thisClass)
                properAncestors
              else
                otherMethod.declaringClass.properAncestors
            }
            ancestors contains method.declaringClass
          }
        }

        if (mostSpecificMethods.size > 1) {
          assert(localDeclarations.isEmpty)   // Otherwise, localDeclarations.head should have filtered out everything else
          throw EvalException(
            s"Method `${mostSpecificMethods.head.methodName}` must be declared explicitly in class `$qualifiedName`, " +
              s"because it is defined in multiple superclasses (`${mostSpecificMethods.head.declaringClass.qualifiedName}`, `${mostSpecificMethods(1).declaringClass.qualifiedName}`)",
            token = Some(declNode.idNode.token)
          )
        }

        mostSpecificMethods.head      // At this point, it must be a singleton.

      }

      // TODO Check that result types match

      resolved.toVector

    }

    private def throwExceptionForDuplicateSymbol(symbol: Symbol, members: Vector[Member]): Nothing = {

      val locallyDefinedNonMethods = members filter { member =>
        member.declaringClass == thisClass && !member.isInstanceOf[CgscriptClass#Method]
      }
      val firstLocallyDefinedMethod = members find { member =>
        member.declaringClass == thisClass && member.isInstanceOf[CgscriptClass#Method]
      }
      val locallyDefinedMembers = locallyDefinedNonMethods ++ firstLocallyDefinedMethod
      val superclassesDefining = members.map { _.declaringClass }.filterNot { _ == thisClass }.distinct

      locallyDefinedMembers.size match {

        case 0 =>
          assert(superclassesDefining.size >= 2)
          throw EvalException(
            s"Symbol `${symbol.name}` is defined in multiple superclasses: `${superclassesDefining.head.qualifiedName}`, `${superclassesDefining(1).qualifiedName}`",
            token = Some(declNode.idNode.token)
          )

        case 1 =>
          assert(superclassesDefining.nonEmpty)
          throw EvalException(
            s"Symbol `${symbol.name}` in class `$qualifiedName` shadows definition in class `${superclassesDefining.head.qualifiedName}`",
            token = locallyDefinedMembers.head.declNode map { _.idNode.token }
          )

        case _ =>
          throw EvalException(
            s"Duplicate symbol `${symbol.name}` in class `$qualifiedName`",
            token = locallyDefinedMembers(1).declNode map { _.idNode.token }
          )

      }

    }

  }

  private def parseMethod(node: MethodDeclarationNode, classModifiers: Modifiers): Method = {

    val name = node.idNode.id.name

    val (autoinvoke, parameters) = node.parameters match {
      case Some(n) => (false, n.toParameters)
      case None => (true, Vector.empty)
    }

    val newMethod = {
      if (node.modifiers.hasExternal) {
        if (!classModifiers.hasSystem)
          throw EvalException(s"Method is declared `external`, but class `$qualifiedName` is not declared `system`", node.tree)
        if (node.body.isDefined)
          throw EvalException(s"Method is declared `external` but has a method body", node.tree)
        logger.debug(s"$logPrefix Declaring external method: $name")
        SpecialMethods.specialMethods.get(qualifiedName + "." + name) match {
          case Some(fn) =>
            logger.debug(s"$logPrefix   It's a special method.")
            ExplicitMethod(node.idNode, Some(node), parameters, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride)(fn)
          case None =>
            val externalName = externalMethodName(name)
            val externalParameterTypes = parameters map { _.paramType.javaClass }
            logger.debug(s"$logPrefix   It's a Java method via reflection: ${javaClass.getName}.$externalName(${externalParameterTypes mkString ","})")
            val externalMethod = try {
              javaClass.getMethod(externalName, externalParameterTypes: _*)
            } catch {
              case _: NoSuchMethodException =>
                throw EvalException(s"Method is declared `external`, but has no corresponding Java method (expecting `$javaClass`.`$externalName`): `$qualifiedName.$name`", node.tree)
            }
            logger.debug(s"$logPrefix   Found the Java method: $externalMethod")
            SystemMethod(node.idNode, Some(node), parameters, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride, externalMethod)
        }
      } else {
        logger.debug(s"$logPrefix Declaring user method: $name")
        val body = node.body getOrElse { sys.error("no body") }
        UserMethod(node.idNode, Some(node), parameters, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride, body)
      }
    }

    newMethod

  }

  private def externalMethodName(name: String): String = {
    name match {
      case "op +" => "$plus"
      case "op -" => "$minus"
      case "op *" => "$times"
      case "op /" => "$div"
      case "op %" => "$percent"
      case "op ^" => "exp"
      case "op <=" => "$less$eq"
      case "op []" => "get"     // TODO Not so sure.
      case "op unary+" => "unary_$plus"
      case "op unary-" => "unary_$minus"
      case "op unary+-" => "switch"
      case _ => name.updated(0, name(0).toLower)
    }
  }

  ///////////////////////////////////////////////////////////////
  // Initialization

  private def initialize(): Unit = {

    try {
      if (classInfoRef != null)
        initializeClass(classInfoRef.declNode)
    } finally {
      stage = LifecycleStage.Unloaded
    }

    stage = LifecycleStage.Initialized

  }

  private def initializeClass(node: ClassDeclarationNode): Unit = {

    if (stage == LifecycleStage.Initializing) {
      return
    }

    assert(classInfoRef != null)

    classInfoRef.supers foreach { _.ensureInitialized() }

    stage = LifecycleStage.Initializing

    logDebug(s"Initializing class.")

    // Create the class object
    classObjectRef = new ClassObject(CgscriptClass.this)

    // Declare nested classes
    node.nestedClassDeclarations foreach { decl =>
      val id = decl.idNode.id
      classInfo.allNestedClasses(id).declareClass(decl)
    }

    // Elaborate methods
    constructor foreach { _.elaborate() }
    classInfoRef.localMethods foreach { _.elaborate() }

    // Enum construction
    node.enumElements foreach { element =>
      classObjectRef.vars(classInfoRef.staticVarOrdinals(element.idNode.id)) = new EnumObject(this, element.idNode.id.name)
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
    if (qualifiedName == "game.OutcomeClass") {
      import org.cgsuite.core.OutcomeClass._
      classObjectRef.vars(classInfoRef.staticVarOrdinals('P)) = P
      classObjectRef.vars(classInfoRef.staticVarOrdinals('N)) = N
      classObjectRef.vars(classInfoRef.staticVarOrdinals('L)) = L
      classObjectRef.vars(classInfoRef.staticVarOrdinals('R)) = R
      classObjectRef.vars(classInfoRef.staticVarOrdinals('D)) = D
      classObjectRef.vars(classInfoRef.staticVarOrdinals('PHat)) = PHat
      classObjectRef.vars(classInfoRef.staticVarOrdinals('PCheck)) = PCheck
      classObjectRef.vars(classInfoRef.staticVarOrdinals('NHat)) = NHat
      classObjectRef.vars(classInfoRef.staticVarOrdinals('NCheck)) = NCheck
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
    val initializerDomain = new EvaluationDomain(null, Some(classObjectRef))
    node.staticInitializers foreach { initNode =>
      if (!initNode.modifiers.hasExternal) {
        val scope = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
        // We intentionally don't elaborate class var declarations, since those are already
        // accounted for in the class vars. But we still need to elaborate the RHS of
        // the assignment.
        initNode match {
          case declNode: VarDeclarationNode if declNode.body.declType == AssignmentDeclType.ClassVarDecl =>
            declNode.body.expr.elaborate(scope)
          case _ => initNode.body.elaborate(scope)
        }
        initNode.body.evaluate(initializerDomain)
      }
    }

    val initializerElaborationDomain = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
    node.ordinaryInitializers.foreach { _.body elaborate initializerElaborationDomain }
    initializerLocalVariableCount = initializerElaborationDomain.localVariableCount

    logDebug(s"Done initializing class.")

    stage = LifecycleStage.Initialized

    // Initialize nested classes
    node.nestedClassDeclarations foreach { decl =>
      val id = decl.idNode.id
      classInfo.allNestedClasses(id).initializeClass(decl)
    }

  }

  case class Var(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    modifiers: Modifiers,
    isConstructorParam: Boolean = false
  ) extends Member {

    def declaringClass = thisClass
    def isMutable = modifiers.hasMutable

  }

  case class MethodGroup(id: Symbol, methods: Vector[CgscriptClass#Method]) extends MemberResolution {

    def declaringClass = thisClass

    val isPureAutoinvoke = methods.size == 1 && methods.head.autoinvoke

    val autoinvokeMethod = methods find { _.autoinvoke }

    val methodsWithArguments = methods filter { !_.autoinvoke }

    def name = methods.head.methodName

    def qualifiedName = methods.head.qualifiedName

    def isStatic = methods.head.isStatic

    def lookupMethod(
      argumentTypes: Vector[CgscriptClass],
      namedArgumentTypes: Map[Symbol, CgscriptClass]
    ): Option[CgscriptClass#Method] = {

      // This is fairly slow; successful lookups will be cached at the call site.

      val matchingMethods = methodsWithArguments filter { method =>

        val requiredParametersAreSatisfied = method.requiredParameters.indices forall { index =>
          if (index < argumentTypes.length) {
            argumentTypes(index).ancestors contains method.requiredParameters(index).paramType
          } else {
            val optNamedArgumentType = namedArgumentTypes get method.parameters(index).id
            optNamedArgumentType match {
              case Some(argumentType) => argumentType.ancestors contains method.requiredParameters(index).paramType
              case None => method.parameters(index).defaultValue.isDefined
            }
          }
        }

        val allArgumentsAreValid = {
          argumentTypes.length <= method.parameters.length && {
            namedArgumentTypes.keys forall { id =>
              method.parameters exists { _.id == id }
            }
          }
        }

        requiredParametersAreSatisfied && allArgumentsAreValid

      }

      // This is used for error handling:
      def argTypesString = if (argumentTypes.isEmpty) "()" else argumentTypes map { "`" + _.qualifiedName + "`" } mkString ", "

      val reducedMatchingMethods = reduceMethodList(matchingMethods)
      if (reducedMatchingMethods.size >= 2) {
        throw EvalException(s"Method `$name` is ambiguous when applied to $argTypesString")
      }

      reducedMatchingMethods.headOption

    }

    // Pare down the method list by removing all methods that have a strict refinement in the list
    def reduceMethodList(methods: Vector[CgscriptClass#Method]) = {
      methods filterNot { method =>
        methods exists { other =>
          method != other && parametersLeq(other.parameters, method.parameters)
        }
      }
    }

    // True if p1 <= p2, i.e., if p1 is a refinement of p2.
    def parametersLeq(p1: Vector[Parameter], p2: Vector[Parameter]): Boolean = {
      p1.length == p2.length &&
        p1.indices.forall { i => p1(i).paramType.ancestors contains p2(i).paramType }
    }

  }

  trait Method extends Member {

    def idNode: IdentifierNode
    def parameters: Vector[Parameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def isOverride: Boolean
    def call(obj: Any, args: Array[Any]): Any

    val methodName = idNode.id.name
    val declaringClass = thisClass
    val qualifiedName = declaringClass.qualifiedName + "." + methodName
    val qualifiedId = Symbol(qualifiedName)
    val signature = s"$qualifiedName(${parameters.map { _.signature }.mkString(", ")})"
    val ordinal = CallSite.newCallSiteOrdinal
    val locationMessage = s"in call to `$qualifiedName`"

    val (requiredParameters, optionalParameters) = parameters partition { _.defaultValue.isEmpty }

    var knownValidArgs: mutable.LongMap[Unit] = mutable.LongMap()

    def elaborate(): Unit = {
      logger.debug(s"$logPrefix Elaborating method: $qualifiedName")
      val scope = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
      parameters foreach { param =>
        param.defaultValue foreach { _.elaborate(scope) }
      }
      logger.debug(s"$logPrefix Done elaborating method: $qualifiedName")
    }

    // This is optimized to be really fast for methods with <= 4 parameters.
    // TODO Optimize for more than 4 parameters?
    def validateArguments(args: Array[Any], ensureImmutable: Boolean = false): Unit = {
      assert(args.isEmpty || parameters.nonEmpty, qualifiedName)
      CallSite.validateArguments(parameters, args, knownValidArgs, locationMessage, ensureImmutable)
    }

  }

  case class UserMethod(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    parameters: Vector[Parameter],
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
        param.methodScopeIndex = scope.insertId(param.idNode)
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
        val domain = new EvaluationDomain(array, Some(target))
        validateArguments(args)
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
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    parameters: Vector[Parameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    isOverride: Boolean,
    javaMethod: java.lang.reflect.Method
  ) extends Method {

    private val reflect = Symbol(s"Reflect [$javaMethod]")

    def call(obj: Any, args: Array[Any]): Any = {
      val target = if (isStatic) null else obj.asInstanceOf[AnyRef]
      /*
      assert(
        target == null || of(target).ancestors.contains(declaringClass),
        (of(target), declaringClass)
      )
      */
      Profiler.start(reflect)
      try {
        validateArguments(args)
        internalize(javaMethod.invoke(target, args.asInstanceOf[Array[AnyRef]] : _*))
      } catch {
        case exc: IllegalArgumentException => throw EvalException(
          s"`IllegalArgumentException` in external method `$qualifiedName` (misconfigured parameters?)", exc
        )
        case exc: InvocationTargetException => throw EvalException(
          exc.getTargetException match {
            case nestedExc: CgsuiteException =>
              // TODO nestedExc.setInvocationTarget(qualifiedName)
              throw nestedExc
            case nestedExc => throw EvalException(s"Error in call to `$qualifiedName`: ${nestedExc.getMessage}", nestedExc)
          }
        )
      } finally {
        Profiler.stop(reflect)
      }
    }

  }

  case class ExplicitMethod(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    parameters: Vector[Parameter],
    autoinvoke: Boolean,
    isStatic: Boolean,
    isOverride: Boolean
    )
    (fn: (Any, Any) => Any) extends Method {

    def call(obj: Any, args: Array[Any]): Any = {
      validateArguments(args)
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

    override def declNode = None

    def call(obj: Any, args: Array[Any]): Any = call(args)

    override def referenceToken = Some(idNode.token)

    override val locationMessage = s"in call to `${thisClass.qualifiedName}` constructor"

  }

  case class UserConstructor(
    idNode: IdentifierNode,
    parameters: Vector[Parameter]
  ) extends Constructor {

    private val invokeUserConstructor = Symbol(s"InvokeUserConstructor [$qualifiedName]")

    lazy val instantiator: (Array[Any], Any) => StandardObject = {
      if (ancestors.contains(ImpartialGame)) {
        (args: Array[Any], enclosingObject: Any) => new ImpartialGameObject(thisClass, args, enclosingObject)
      } else if (ancestors.contains(Game)) {
        (args: Array[Any], enclosingObject: Any) => new GameObject(thisClass, args, enclosingObject)
      } else if (ancestors.contains(HeapRuleset)) {
        (args: Array[Any], enclosingObject: Any) => new HeapRulesetObject(thisClass, args, enclosingObject)
      } else {
        (args: Array[Any], enclosingObject: Any) => new StandardObject(thisClass, args, enclosingObject)
      }
    }

    def call(args: Array[Any]): Any = call(args, null)

    def call(args: Array[Any], enclosingObject: Any): Any = {
      // TODO Superconstructor
      Profiler.start(invokeUserConstructor)
      try {
        validateArguments(args, ensureImmutable = !isMutable)
        instantiator(args, enclosingObject)
      } finally {
        Profiler.stop(invokeUserConstructor)
      }
    }

  }

  case class SystemConstructor(
    idNode: IdentifierNode,
    parameters: Vector[Parameter],
    javaConstructor: java.lang.reflect.Constructor[_]
  ) extends Constructor {

    private val reflect = Symbol(s"Reflect [$javaConstructor]")

    def call(args: Array[Any]): Any = {
      try {
        Profiler.start(reflect)
        validateArguments(args, ensureImmutable = !isMutable)
        javaConstructor.newInstance(args.asInstanceOf[Array[AnyRef]] : _*)
      } catch {
        case exc: IllegalArgumentException =>
          throw EvalException(s"`IllegalArgumentException` in external constructor for `${thisClass.qualifiedName}` (misconfigured parameters?)")
        case exc: InvocationTargetException =>
          exc.getCause match {
            case exc2: CgsuiteException => throw exc2
            case _ => throw exc
          }
      } finally {
        Profiler.stop(reflect)
      }
    }

  }

  case class ExplicitConstructor(
    idNode: IdentifierNode,
    parameters: Vector[Parameter]
    )
    (fn: (Any, Any) => Any) extends Constructor {

    override def call(args: Array[Any]) = {
      validateArguments(args, ensureImmutable = !isMutable)
      val argsTuple = parameters.size match {
        case 0 => ()
        case 1 => args(0)
        case 2 => (args(0), args(1))
      }
      fn(classObject, argsTuple)
    }

  }

}

sealed trait CgscriptClassDef
case class UrlClassDef(classpathRoot: better.files.File, url: URL) extends CgscriptClassDef
case class ExplicitClassDef(text: String) extends CgscriptClassDef
case class NestedClassDef(enclosingClass: CgscriptClass, declNode: ClassDeclarationNode) extends CgscriptClassDef

object LifecycleStage extends Enumeration {
  val New, Declaring, Declared, Initializing, Initialized, Unloaded = Value
}

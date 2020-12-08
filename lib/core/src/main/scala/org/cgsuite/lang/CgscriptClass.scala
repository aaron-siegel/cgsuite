package org.cgsuite.lang

import java.io.{ByteArrayInputStream, File}
import java.net.URL
import java.nio.charset.StandardCharsets

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.antlr.runtime.tree.Tree
import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGameOps
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.node._
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.output._
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.language.{existentials, postfixOps}
import scala.tools.nsc.interpreter.IMain

object CgscriptClass {

  // TODO Validate that method overrides have compatible return types

  private[lang] val logger = Logger(LoggerFactory.getLogger(classOf[CgscriptClass]))

  private var nextClassOrdinal = 0
  private[lang] def newClassOrdinal = {
    val ord = nextClassOrdinal
    nextClassOrdinal += 1
    ord
  }

  logger debug "Declaring system classes."

  CgscriptSystem.allSystemClasses foreach { case (name, scalaClass) =>
    declareSystemClass(name, Some(scalaClass))
  }

  logger debug "Declaring folders."

  CgscriptClasspath.declareFolders()

  val Object = CgscriptPackage.lookupClassByName("Object").get
  val Class = CgscriptPackage.lookupClassByName("Class").get
  val Enum = CgscriptPackage.lookupClassByName("Enum").get
  val Game = CgscriptPackage.lookupClassByName("Game").get
  val SidedValue = CgscriptPackage.lookupClassByName("SidedValue").get
  val CanonicalStopper = CgscriptPackage.lookupClassByName("CanonicalStopper").get
  val Pseudonumber = CgscriptPackage.lookupClassByName("Pseudonumber").get
  val CanonicalShortGame = CgscriptPackage.lookupClassByName("CanonicalShortGame").get
  val ImpartialGame = CgscriptPackage.lookupClassByName("ImpartialGame").get
  val Rational = CgscriptPackage.lookupClassByName("Rational").get
  val Uptimal = CgscriptPackage.lookupClassByName("Uptimal").get
  val DyadicRational = CgscriptPackage.lookupClassByName("DyadicRational").get
  val Integer = CgscriptPackage.lookupClassByName("Integer").get
  val Nimber = CgscriptPackage.lookupClassByName("Nimber").get
  val Zero = CgscriptPackage.lookupClassByName("Zero").get
  val ExplicitGame = CgscriptPackage.lookupClassByName("ExplicitGame").get
  val Coordinates = CgscriptPackage.lookupClassByName("Coordinates").get
  val Collection = CgscriptPackage.lookupClassByName("Collection").get
  val List = CgscriptPackage.lookupClassByName("List").get
  val Set = CgscriptPackage.lookupClassByName("Set").get
  val Map = CgscriptPackage.lookupClassByName("Map").get
  val MapEntry = CgscriptPackage.lookupClassByName("MapEntry").get
  val Table = CgscriptPackage.lookupClassByName("Table").get
  val String = CgscriptPackage.lookupClassByName("String").get
  val Boolean = CgscriptPackage.lookupClassByName("Boolean").get
  val Procedure = CgscriptPackage.lookupClassByName("Procedure").get
  lazy val NothingClass = CgscriptPackage.lookupClassByName("Nothing").get
  lazy val HeapRuleset = CgscriptPackage.lookupClassByName("game.heap.HeapRuleset").get

  Object.ensureDeclared()

  def clearAll() {
    // TODO Unload the interpreter?
    CanonicalShortGameOps.reinit()
    MisereCanonicalGameOps.reinit()
    CgscriptPackage.classDictionary.values foreach { _.unload() }
    Object.ensureDeclared()
  }

  def instanceToOutput(x: Any): Output = {
    x match {
      case ot: OutputTarget => ot.toOutput
      case str: String => new StyledTextOutput(StyledTextOutput.Style.FACE_TEXT, "\"" + str + "\"")
      case list: IndexedSeq[_] => RichList(list).mkOutput(",", parens = "[]")
      case set: Set[_] => RichList(set.toVector.sorted(UniversalOrdering)).mkOutput(",", parens = "{}")
      case map: Map[_,_] if map.isEmpty => new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, "{=>}")
      case map: Map[_,_] => RichList(map.toVector.sorted(UniversalOrdering)).mkOutput(", ", parens = "{}")
      case (k, v) =>
        val output = new StyledTextOutput
        output.append(instanceToOutput(k))
        output.appendMath(" ")
        output.appendSymbol(StyledTextOutput.Symbol.BIG_RIGHT_ARROW)
        output.appendMath(" ")
        output.append(instanceToOutput(v))
        output
      case null => EmptyOutput
      case _ =>
        assert(CgscriptSystem.allSystemClasses exists { case (_, cls) => cls.isAssignableFrom(x.getClass) })
        new StyledTextOutput(StyledTextOutput.Style.FACE_TEXT, x.toString)
    }
  }

  private[lang] def declareSystemClass(name: String, scalaClass: Option[Class[_]] = None, explicitDefinition: Option[String] = None) {

    val path = name.replace('.', '/')
    val classdef: CgscriptClassDef = {
      explicitDefinition match {
        case Some(text) => ExplicitClassDef(text)
        case None => UrlClassDef(CgscriptClasspath.systemDir, CgscriptSystem.getClass.getResource(s"resources/$path.cgs"))
      }
    }
    val components = name.split("\\.").toSeq
    val pkg = CgscriptPackage.root lookupSubpackage (components dropRight 1) getOrElse {
      sys.error("Cannot find package: " + (components dropRight 1))
    }
    pkg.declareClass(Symbol(components.last), classdef, scalaClass)

  }

}

class CgscriptClass(
  val pkg: CgscriptPackage,
  val classdef: CgscriptClassDef,
  override val id: Symbol,
  val systemClass: Option[Class[_]] = None
  ) extends Member with LazyLogging { thisClass =>

  ///////////////////////////////////////////////////////////////
  // Basic properties derivable without parsing

  val classOrdinal: Int = CgscriptClass.newClassOrdinal

  val enclosingClass: Option[CgscriptClass] = classdef match {
    case NestedClassDef(cls) => Some(cls)
    case _ => None
  }

  val topClass: CgscriptClass = enclosingClass match {
    case Some(cls) => cls.topClass
    case None => this
  }

  val isTopClass: Boolean = this == topClass

  def url: Option[URL] = classdef match {
    case UrlClassDef(_, x) => Some(x)
    case _ => None
  }

  override val declaringClass = enclosingClass.orNull

  val name: String = id.name

  val nameInPackage: String = enclosingClass match {
    case Some(encl) => encl.nameInPackage + "." + name
    case None => name
  }

  val isConstantsClass = nameInPackage == "constants"

  val qualifiedName: String = {
    if (pkg.isRoot)
      nameInPackage
    else
      s"${pkg.qualifiedName}.$nameInPackage"
  }

  def scalaName = scalaClassdefName     // For Member trait - TODO Clean this up?

  val scalaClassdefName: String = {
    qualifiedName match {
      case "cgsuite.lang.Nothing" => "Null"
      case _ =>
        enclosingClass match {
          case Some(_) => name
          case _ => qualifiedName.replace('.', '$')
        }
    }
  }

  lazy val scalaTyperefName: String = scalaTyperefName(Iterable.empty)

  def scalaTyperefName(nestProjection: Iterable[(TypeVariable, CgscriptType)]): String = {
    qualifiedName match {
      case "cgsuite.lang.Nothing" => "Null"
      case "cgsuite.lang.Boolean" => "Boolean"
      case _ =>
        systemClass match {
          case Some(cls) => cls.getName
          case None =>
            enclosingClass match {
              case Some(cls) =>
                s"${cls.scalaClassdefName}${cls.scalaTypeParametersBlock(nestProjection)}#$name"
              case None =>
                if (isSingleton)
                  s"$scalaClassdefName.type"
                else
                  scalaClassdefName
            }
        }
    }
  }

  lazy val scalaClassrefName: String = {
    qualifiedName match {
      case "cgsuite.lang.Nothing" => "null"
      case _ =>
        systemClass match {
          case Some(cls) => cls.getName
          case None => scalaClassdefName
        }
    }
  }

  lazy val scalaTypeParametersBlock: String = scalaTypeParametersBlock(Iterable.empty)

  def scalaTypeParametersBlock(typeSubstitutions: Iterable[(TypeVariable, CgscriptType)]): String = {
    if (typeParameters.isEmpty) {
      ""
    } else {
      val typeParametersString = typeParameters map { _.substituteAll(typeSubstitutions).scalaTypeName } mkString ", "
      s"[$typeParametersString]"
    }
  }

  override def toString = s"\u27ea$qualifiedName\u27eb"

  private val logPrefix = f"[$classOrdinal%3d: $qualifiedName%s]"

  private[cgsuite] def logDebug(message: => String): Unit = logger debug s"$logPrefix $message"

  ///////////////////////////////////////////////////////////////
  // ClassInfo properties and lookups
  // (available after phase 1 declaration)

  def classInfo: ClassInfo = {
    if (stage == LifecycleStage.DeclaringPhase1)
      sys.error("classInfo called while still declaring: " + this)
    ensureDeclaredPhase1()
    if (!isTopClass)
      topClass.ensureDeclaredPhase2()     // I'm not 100% sure abt this pattern
    assert(isScript || classInfoRef != null, this)
    classInfoRef
  }

  override def declNode = {
    enclosingClass match {
      case Some(cls) => cls.declNode
      case _ =>
        ensureDeclaredPhase1()
        assert(classInfoRef != null)
        Option(classInfoRef.declNode)
    }
  }

  def isScript = {
    ensureDeclaredPhase1()
    scriptRef != null
  }

  def ancestors: Vector[CgscriptClass] = classInfo.ancestors

  def properAncestors: Vector[CgscriptClass] = classInfo.properAncestors

  def isEnum: Boolean = classInfo.declNode.isEnum

  def isMutable: Boolean = classInfo.modifiers.hasMutable

  def isSingleton: Boolean = !isScript && classInfo.modifiers.hasSingleton

  def isStatic: Boolean = classInfo.modifiers.hasStatic

  def isSystem: Boolean = classInfo.modifiers.hasSystem

  def constructor: Option[Constructor] = classInfo.constructor

  def scriptBody: ScriptBody = scriptRef

  def typeParameters: Vector[TypeVariable] = if (isScript) Vector.empty else classInfo.typeParameters

  def mostGenericType: ConcreteType = ConcreteType(this, typeParameters)

  def substituteForTypeParameters(typeArguments: CgscriptType*): ConcreteType = {
    assert(typeArguments.length == typeParameters.length)
    ConcreteType(this, typeArguments.toVector)
  }

  def <=(that: CgscriptClass) = ancestors contains that

  def <(that: CgscriptClass) = this <= that && this != that

  def resolveMember(id: Symbol): Option[MemberResolution] = {
    resolveInstanceMember(id) orElse resolveStaticMember(id)
  }

  def resolveInstanceMember(id: Symbol): Option[MemberResolution] = {
    classInfo.instanceMemberLookup get id orElse {
      enclosingClass flatMap { _.resolveInstanceMember(id) }
    }
  }

  def resolveInstanceMemberWithImplicits(id: Symbol): Option[MemberResolution] = {

    resolveInstanceMember(id) orElse {

      // Try various types of implicit conversions. This is a bit of a hack to handle
      // Rational -> DyadicRational -> Integer conversions in a few places. In later versions, this might be
      // replaced by a more elegant / general solution.

      val implicits = this match {
        case CgscriptClass.SidedValue => Vector(this, CgscriptClass.CanonicalStopper, CgscriptClass.Pseudonumber)
        case CgscriptClass.CanonicalShortGame => Vector(this, CgscriptClass.Uptimal)
        case CgscriptClass.Rational => Vector(this, CgscriptClass.DyadicRational, CgscriptClass.Integer)
        case CgscriptClass.DyadicRational => Vector(this, CgscriptClass.Integer)
        case _ => Vector(this)
      }

      val implicitResolutions = implicits flatMap { _.resolveInstanceMember(id) }
      implicitResolutions.headOption

    }

  }

  def lookupInstanceMethod(
    id: Symbol,
    argumentTypes: Vector[CgscriptType],
    namedArgumentTypes: Map[Symbol, CgscriptType],
    objectType: Option[CgscriptType] = None): Option[MethodProjection] = {

    resolveMember(id) match {
      case Some(methodGroup: MethodGroup) => methodGroup.lookupMethod(argumentTypes, namedArgumentTypes, objectType)
      case None => None
    }

  }

  def resolveInstanceMethod(
    id: Symbol,
    argumentTypes: Vector[CgscriptType],
    namedArgumentTypes: Map[Symbol, CgscriptType],
    objectType: Option[CgscriptType] = None,
    withImplicits: Boolean = false): MethodProjection = {

    resolveMember(id) match {
      case Some(methodGroup: MethodGroup) => methodGroup.resolveToMethod(argumentTypes, namedArgumentTypes, objectType, withImplicits)
      case None =>
        val objSuffixString = objectType map { typ => s" (for object of type `${typ.qualifiedName}`)" } getOrElse ""
        throw EvalException(s"No method `${id.name}`$objSuffixString")
    }

  }

  def resolveStaticMember(id: Symbol): Option[MemberResolution] = {
    classInfo.staticMemberLookup get id
  }

  ///////////////////////////////////////////////////////////////
  // Lifecycle management

  logDebug(s"Formed new class with classdef: $classdef")

  private var stage: LifecycleStage.Value = LifecycleStage.New
  private var classInfoRef: ClassInfo = _
  private var scriptRef: ScriptBody = _

  def ensureDeclared(): Unit = {
    ensureDeclaredPhase1()
    ensureDeclaredPhase2()
  }

  def isDeclaredPhase1: Boolean = classInfoRef != null

  def ensureDeclaredPhase1(): Unit = {
    enclosingClass match {
      case Some(cls) => cls.ensureDeclaredPhase1()
      case None =>
        if (stage == LifecycleStage.DeclaringPhase1) {
          // TODO Better error message/handling here?
          sys.error("circular class definition?: " + qualifiedName)
        }
        stage match {
          case LifecycleStage.New | LifecycleStage.Unloaded => declarePhase1()
          case _ =>   // Nothing to do
        }
    }
  }

  private def declarePhase1(): Unit = {

    logDebug(s"Declaring class (phase 1).")

    // Force constants to declare first
    pkg lookupClass 'constants foreach { constantsCls =>
      if (constantsCls != this) {
        constantsCls.ensureDeclaredPhase1()
      }
    }

    val tree = parse()

    logDebug(s"Parsed class.")

    stage = LifecycleStage.DeclaringPhase1

    try {

      tree.getType match {

        case SCRIPT =>
          val node = StatementSequenceNode(tree.children.head, topLevel = true)
          logDebug(s"Script Node: $node")
          declareScriptPhase1(node)

        case EOF =>
          val node = ClassDeclarationNode(tree.children(1), pkg)
          logDebug(s"Class Node: $node")
          declareClassPhase1(node)

      }

    } finally {

      // If an exception occurred, we want to treat this class as Unloaded
      stage = LifecycleStage.Unloaded

    }

    stage = LifecycleStage.DeclaredPhase1

    logger debug s"$logPrefix Done declaring (phase 1)."

  }

  def ensureDeclaredPhase2(): Unit = {
    enclosingClass match {
      case Some(cls) => cls.ensureDeclaredPhase2()
      case None =>
        stage match {
          case LifecycleStage.DeclaredPhase1 => declarePhase2()
          case _ =>   // Nothing to do
        }
    }
  }

  private def declarePhase2(): Unit = {

    if (classInfoRef != null) {

      assert(classInfoRef != null, this)

      classInfoRef.supers foreach { _.ensureDeclaredPhase2() }

      // Declare nested classes
      classInfoRef.declNode.nestedClassDeclarations foreach { decl =>
        val id = decl.idNode.id
        classInfoRef.localNestedClasses.find { _.id == id }.get.declareClassPhase1(decl)
      }

    } else {

      assert(scriptBody != null, this)

    }

    //validateClass()

    stage = LifecycleStage.Declared

  }

  override def elaborate() = sys.error("use ensureElaborated()")

  override def ensureElaborated() = {

    classInfo.constructor foreach { _.ensureElaborated() }
    classInfo.allMembers foreach { _.ensureElaborated() }
    classInfo.initializers foreach { _.ensureElaborated() }
    CgscriptType(this, typeParameters)

  }

  override def mentionedClasses = {

    ensureElaborated()

    (classInfo.supers flatMap { _.mentionedClasses }) ++
      (classInfo.allMembers flatMap { _.mentionedClasses }) ++
      (classInfo.localNestedClasses flatMap { _.mentionedClasses }) :+
      this
    // TODO Initializers?

  }

  def ensureCompiled(eval: IMain): Unit = {

    if (isScript)
      return

    stage match {

      case LifecycleStage.New | LifecycleStage.DeclaredPhase1 | LifecycleStage.Declared | LifecycleStage.Unloaded =>
        enclosingClass match {
          case Some(cls) => cls.ensureCompiled(eval)    // TODO What if no longer exists?
          case _ =>
            ensureDeclared()
            compile(eval)
        }

      case LifecycleStage.DeclaringPhase1 | LifecycleStage.DeclaringPhase2 =>
        assert(assertion = false, stage)

      case LifecycleStage.Loaded =>

    }

  }

  def unload() {
    if (this.stage != LifecycleStage.Unloaded) {
      logger debug s"$logPrefix Building unload list."
      val unloadList = mutable.HashSet[CgscriptClass]()
      topClass.buildUnloadList(unloadList)
      logger debug s"$logPrefix Unloading ${unloadList.size} classes."
      unloadList foreach { _.doUnload() }
    }
  }

  private def doUnload() {
    logger debug s"$logPrefix Unloading."
    classInfoRef = null
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
    if (classInfoRef != null) {
      classInfoRef.localNestedClasses foreach { _.buildUnloadList(list) }
    }

    // TODO Also anything that references this class?

  }

  private def addDerivedClassesToUnloadList(list: mutable.HashSet[CgscriptClass], cls: CgscriptClass): Unit = {
    if (cls.classInfoRef != null && (cls.classInfoRef.supers contains this)) {
      cls.topClass.buildUnloadList(list)
    }
    // Recurse through nested classes
    if (cls.classInfoRef != null) {
      cls.classInfoRef.localNestedClasses foreach { addDerivedClassesToUnloadList(list, _) }
    }
  }

  ///////////////////////////////////////////////////////////////
  // Parsing and declaration

  private def parse(): Tree = {

    val (in, source) = classdef match {

      case UrlClassDef(_, url) =>
        logger debug s"$logPrefix Parsing from URL: $url"
        (url.openStream(), new File(url.getFile).getName)

      case ExplicitClassDef(text) =>
        logger debug s"$logPrefix Parsing from explicit definition"
        (new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), qualifiedName)

      case NestedClassDef(_) =>
        throw EvalException(s"Class no longer exists: `$qualifiedName`")

    }

    try {
      ParserUtil.parseCU(in, source)
    } finally {
      in.close()
    }

  }

  private def declareScriptPhase1(node: StatementSequenceNode): Unit = {

    classInfoRef = null
    scriptRef = ScriptBody(node)

  }

  private def declareClassPhase1(node: ClassDeclarationNode): Unit = {

    logger debug s"$logPrefix Declaring class."

    classInfoRef = new ClassInfo(node)
    scriptRef = null

    logger debug s"$logPrefix Done declaring class (phase 1)."

  }

  private def parseMethod(node: MethodDeclarationNode, classModifiers: Modifiers): Method = {

    val name = node.idNode.id.name

    val (autoinvoke, parameters) = node.parameters match {
      case Some(node) => (false, Some(node))
      case None => (true, None)
    }

    /*
    val explicitReturnType = node.returnType map { idNode =>
      pkg lookupClass idNode.id orElse (CgscriptPackage lookupClass idNode.id) getOrElse {
        throw EvalException(s"Unknown class in parameter declaration: `${idNode.id.name}`", idNode.tree)
      }
    } map { CgscriptType(_) }
    */
    val newMethod = {
      if (node.modifiers.hasExternal) {
        if (!classModifiers.hasSystem)
          throw EvalException(s"Method is declared `external`, but class `$qualifiedName` is not declared `system`", node.tree)
        if (node.body.isDefined)
          throw EvalException(s"Method is declared `external` but has a method body", node.tree)
        logger.debug(s"$logPrefix Declaring external method: $name")
        SystemMethod(node.idNode, Some(node), parameters, node.returnType, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride)
      } else {
        logger.debug(s"$logPrefix Declaring user method: $name")
        val body = node.body getOrElse { sys.error("no body") }
        UserMethod(node.idNode, Some(node), parameters, node.returnType, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride, body)
      }
    }

    newMethod

  }

  class ClassInfo(val declNode: ClassDeclarationNode) {

    val idNode: IdentifierNode = declNode.idNode

    if (idNode.id.name != name)
      throw EvalException(s"Class name does not match filename: `${idNode.id.name}` (was expecting `$name`)", idNode.tree)

    val modifiers: Modifiers = declNode.modifiers

    val typeParameters: Vector[TypeVariable] = declNode.typeParameters map { typeParameterNode =>
      typeParameterNode.toType
    }

    val enumElementNodes = declNode.enumElements

    val superTypes: Vector[ConcreteType] = {

      if (qualifiedName == "cgsuite.lang.Object") {
        Vector.empty
      } else if (declNode.extendsClause.isEmpty) {
        Vector(if (declNode.isEnum) ConcreteType(CgscriptClass.Enum) else ConcreteType(CgscriptClass.Object))
      } else {

        declNode.extendsClause map {

          case concreteNode: ConcreteTypeSpecifierNode =>
            concreteNode.toType(ElaborationDomain(thisClass), allowInstanceNestedClasses = false)

          case _ =>
            throw EvalException(s"Illegal type variable in `extends` clause", declNode.tree)

        }

      }

    }

    val supers: Vector[CgscriptClass] = superTypes map { _.baseClass }

    def ensureFullyDeclaredPhase1(baseType: ConcreteType): Unit = {
      baseType.baseClass.ensureDeclaredPhase1()
      baseType.typeArguments foreach {
        case concreteType: ConcreteType => ensureFullyDeclaredPhase1(concreteType)
        case _ =>
      }
    }

    superTypes foreach ensureFullyDeclaredPhase1

    val properAncestorTypes: Vector[ConcreteType] = {
      val allResolvedTypes = superTypes.reverse flatMap { superType =>
        val superTypeAncestors = superType.baseClass.classInfo.ancestorTypes
        assert(superType.baseClass.typeParameters.length == superType.typeArguments.length)
        val substitutions = superType.baseClass.typeParameters zip superType.typeArguments
        superTypeAncestors map { ancestor =>
          ancestor substituteAll substitutions
        }
      }
      // Check that if a class appears multiple times, it's identical
      allResolvedTypes groupBy { _.baseClass } foreach { case (_, types) =>
        val resolvedType = types.head
        val conflictingTypeOpt = types find { _ != resolvedType }
        conflictingTypeOpt match {
          case None =>
          case Some(conflictingType) =>
            throw EvalException(
              s"Class `$qualifiedName` extends multiple conflicting types: `${resolvedType.qualifiedName}`, `${conflictingType.qualifiedName}`",
              declNode.tree
            )
        }
      }
      allResolvedTypes.distinct
    }

    val properAncestors: Vector[CgscriptClass] = properAncestorTypes map { _.baseClass }

    val ancestors = properAncestors :+ thisClass

    val ancestorTypes = properAncestorTypes :+ ConcreteType(thisClass, typeParameters)

    // Just to be safe, we check once more that each class appears at most once as a base class in ancestorTypes
    assert(ancestorTypes groupBy { _.baseClass } forall { case (_, types) => types.size == 1 })

    val initializers: Vector[Initializer] = declNode.initializers map {
      case blockNode: InitializerBlockNode =>
        OrdinaryInitializer(blockNode.modifiers.hasMutable, blockNode.modifiers.hasStatic, Some(blockNode.body))
      case varDeclNode: VarDeclarationNode =>
        Var(varDeclNode.idNode, Some(varDeclNode), varDeclNode.modifiers.hasMutable, varDeclNode.modifiers.hasStatic, None, Some(varDeclNode.body.children(1)))   // TODO: Explicit result type
    }

    val localVars: Vector[Var] = initializers collect { case variable: Var => variable }

    val localMethods: Vector[Method] = declNode.methodDeclarations map { parseMethod(_, declNode.modifiers) }

    val localNestedClasses: Vector[CgscriptClass] = declNode.nestedClassDeclarations map { nestedDeclNode =>
      new CgscriptClass(pkg, NestedClassDef(thisClass), nestedDeclNode.idNode.id)
    }

    val enumElements: Vector[CgscriptClass#Var] = enumElementNodes map { node =>
      Var(node.idNode, Some(node), isMutable = false, isStatic = true, Some(CgscriptType(thisClass)), None, isEnumElement = true)
    }

    val constructor: Option[Constructor] = declNode.constructorParams map { parametersNode =>
      systemClass match {
        case Some(_) => SystemConstructor(declNode.idNode, Some(parametersNode))
        case None => UserConstructor(declNode.idNode, Some(parametersNode))
      }
    }

    val constructorParamVars: Vector[Var] = constructor match {
      case Some(ctor) => ctor.parameters map { param =>
        Var(param.idNode, Some(declNode), isMutable = false, isStatic = false, Some(param.paramType), None, isConstructorParam = true)
      }
      case None => Vector.empty
    }

    val localMembers: Vector[Member] = localVars ++ localMethods ++ localNestedClasses ++ constructorParamVars ++ enumElements

    // TODO Handle overrides & inheritance conflicts

    val inheritedMembers: Vector[Member] = supers flatMap { _.classInfo.allMembers }

    val allMembers: Vector[Member] = localMembers ++ inheritedMembers

    lazy val groupedMembers: Map[Symbol, Vector[Member]] = allMembers groupBy { _.id }

    lazy val allVars: Map[Symbol, CgscriptClass#Var] = groupedMembers collect {
      case (id, vars) if vars.head.isInstanceOf[CgscriptClass#Var] =>
        if (vars.size > 1)
          throwExceptionForDuplicateSymbol(id, vars)
        (id, vars.head.asInstanceOf[CgscriptClass#Var])
    }

    lazy val allMethods: Map[Symbol, MethodGroup] = groupedMembers collect {

      case (id, members) if members.head.isInstanceOf[CgscriptClass#Method] =>

        if (members exists { !_.isInstanceOf[CgscriptClass#Method] })
          throwExceptionForDuplicateSymbol(id, members)
        val methods = members map { _.asInstanceOf[CgscriptClass#Method] }
        val locallyDefinedMethods = methods filter { _.declaringClass == thisClass }

        if (locallyDefinedMethods.nonEmpty) {
          // Check for "static consistency": if any method is static, all must be
          val isStatic = locallyDefinedMethods.head.isStatic
          if (methods exists { _.isStatic != isStatic })
            throw EvalException(s"Inconsistent use of `static` for method `${locallyDefinedMethods.head.qualifiedName}`", token = locallyDefinedMethods.head.declNode map { _.idNode.token })
        }

        // TODO Duplicate method signatures
        val checkedMethods = validateMethods(methods)
        (id, MethodGroup(id, checkedMethods))

    }

    lazy val allNestedClasses: Map[Symbol, CgscriptClass] = {
      groupedMembers collect {
        case (id, classes) if classes.head.isInstanceOf[CgscriptClass] =>
          if (classes.size > 1)
            throwExceptionForDuplicateSymbol(id, classes)
          (id, classes.head.asInstanceOf[CgscriptClass])
      }
    }

    lazy val (staticVars, instanceVars) = allVars partition { _._2.isStatic }
    lazy val (staticMethods, instanceMethods) = allMethods partition { _._2.isStatic }
    lazy val instanceNestedClasses = allNestedClasses   // TODO support static nested classes?
    //lazy val (staticNestedClasses, instanceNestedClasses) = allNestedClasses partition { _._2.isStatic }

    lazy val allInstanceMethodsInScope: Map[Symbol, CgscriptClass#MethodGroup] = {
      (enclosingClass map { _.classInfo.allInstanceMethodsInScope } getOrElse Map.empty) ++ instanceMethods
    }

    lazy val allInstanceNestedClassesInScope: Map[Symbol, CgscriptClass] = {
      (enclosingClass map { _.classInfo.allInstanceNestedClassesInScope } getOrElse Map.empty) ++ instanceNestedClasses
    }

    lazy val instanceMemberLookup: Map[Symbol, MemberResolution] = {
      // The locally present vars, method groups, and nested classes will all have unique names at this point,
      // and if they collide with something else in scope, they should legitimately shadow it.
      allInstanceMethodsInScope ++ allInstanceNestedClassesInScope ++ instanceVars ++ instanceMethods ++ instanceNestedClasses
    }

    lazy val staticMemberLookup: Map[Symbol, MemberResolution] = {
      staticVars ++ staticMethods // ++ staticNestedClasses
    }

    validateClass()

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
            token = Some(classInfo.declNode.idNode.token)
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

    private def validateMethods(methods: Vector[CgscriptClass#Method]): Vector[MethodProjection] = {

      // Compute the type substitutions for each method.

      val typeSubstitutions = {
        methods.distinct map { method =>
          val ancestorType = ancestorTypes find { _.baseClass == method.declaringClass } getOrElse {
            sys.error("This should never happen - it means the method's declaring class was not found among the ancestors of this class")
          }
          method -> (ancestorType.baseClass.typeParameters zip ancestorType.typeArguments)
        }
      }.toMap

      // Compute the signature projection of each method.

      val groupedBySignatureProjection = methods.distinct groupBy { method =>
        method.parameterTypeList.substituteAll(typeSubstitutions(method))
      }

      // Resolve each (projected) signature to a specific method.

      val resolved = groupedBySignatureProjection map { case (signatureProjection, matchingMethods) =>

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
            otherMethod.declaringClass.properAncestors contains method.declaringClass
          }
        }

        if (mostSpecificMethods.size > 1) {
          assert(localDeclarations.isEmpty)   // Otherwise, localDeclarations.head should have filtered out everything else
          throw EvalException(
            s"Method `${mostSpecificMethods.head.methodName}` must be declared explicitly in class `$qualifiedName`, " +
              s"because it is defined in multiple superclasses (`${mostSpecificMethods.head.declaringClass.qualifiedName}`, `${mostSpecificMethods(1).declaringClass.qualifiedName}`)",
            token = Some(classInfo.declNode.idNode.token)
          )
        }

        MethodProjection(mostSpecificMethods.head, typeSubstitutions(mostSpecificMethods.head), signatureProjection)

      }

      // TODO Check that result types match (this should happen later, during elaboration)

      resolved.toVector

    }

    private def validateClass(): Unit = {

      // Check that singleton => no constructor
      if (constructor.isDefined && declNode.modifiers.hasSingleton) {
        throw EvalException(
          s"Class `$qualifiedName` cannot have a constructor if declared `singleton`",
          token = Some(idNode.token)
        )
      }

      // Check that no superclass is a singleton
      supers foreach { ancestor =>
        if (ancestor.isSingleton) {
          throw EvalException(
            s"Class `$qualifiedName` cannot extend singleton class `${ancestor.qualifiedName}`",
            token = Some(idNode.token)
          )
        }
      }

      // Check that constants classes must be singletons
      if (nameInPackage == "constants" && !declNode.modifiers.hasSingleton) {
        throw EvalException(
          s"Constants class `$qualifiedName` must be declared `singleton`",
          token = Some(idNode.token)
        )
      }

      // If this class is mutable, check that every nested class is also mutable
      if (declNode.modifiers.hasMutable) {
        // If we're mutable, check that no (locally declared) nested class is immutable
        declNode.nestedClassDeclarations foreach { nested =>
          if (!nested.modifiers.hasMutable) {
            throw EvalException(
              s"Nested class `${nested.idNode.id.name}` of mutable class `$qualifiedName` is not declared `mutable`",
              token = Some(nested.idNode.token)
            )
          }
        }
      }

      // If this class is immutable, check that every superclass is also immutable
      if (!declNode.modifiers.hasMutable) {
        supers foreach { spr =>
          if (spr.isMutable) {
            throw EvalException(
              s"Subclass `$qualifiedName` of mutable class `${spr.qualifiedName}` is not declared `mutable`",
              token = Some(classInfoRef.idNode.token)
            )
          }
        }
        // ... and that there are no mutable vars
        declNode.initializers foreach {
          case declNode: VarDeclarationNode if !declNode.modifiers.hasStatic && declNode.modifiers.hasMutable =>
            throw EvalException(
              s"Class `$qualifiedName` is immutable, but variable `${declNode.idNode.id.name}` is declared `mutable`",
              token = Some(declNode.idNode.token)
            )
          case _ =>
        }
      }

      // Check that immutable class vars are assigned values at declaration time
      declNode.initializers collect {
        // This is a little bit of a hack: we look for "phantom" constant nodes since those are indicative of
        // a "default" nil value. This could be refactored to be a bit more elegant.
        case VarDeclarationNode(_, AssignToNode(_, idNode, NullNode(_), AssignmentDeclType.ClassVarDecl), modifiers)
          if !modifiers.hasMutable =>
          throw EvalException(
            s"Immutable variable `${idNode.id.name}` must be assigned a value",
            token = Some(idNode.token)
          )
      }

    }

  }

  case class ScriptBody(node: StatementSequenceNode) {

    def ensureElaborated(): CgscriptType = {
      node.ensureElaborated(new ElaborationDomain(pkg, None))
    }

  }

  ///////////////////////////////////////////////////////////////
  // Compilation (Scala code generation)

  private def compile(eval: IMain): Unit = {

    val context = new CompileContext
    val classesCompiling = mutable.HashSet[CgscriptClass]()
    val emitter = new Emitter

    includeInCompilationUnit(context, classesCompiling, emitter)

    emitter.toNumberedLines foreach { line =>
      logger.debug(line)
    }

    eval.interpret(emitter.toString)

    classesCompiling foreach { compiledClass =>
      compiledClass.stage = LifecycleStage.Loaded
    }

  }

  private def includeInCompilationUnit(context: CompileContext, classesCompiling: mutable.HashSet[CgscriptClass], emitter: Emitter): Unit = {

    enclosingClass match {

      case Some(cls) =>
        cls.includeInCompilationUnit(context, classesCompiling, emitter)
        return

      case _ =>

    }

    if (stage == LifecycleStage.Loaded || (classesCompiling contains this))
      return

    if (this == CgscriptClass.NothingClass)
      return

    classesCompiling += this
    ensureElaborated()
    appendScalaCode(context, classesCompiling, emitter)

    // Compile all mentioned classes that have not yet been compiled.
    mentionedClasses foreach { _.includeInCompilationUnit(context, classesCompiling, emitter) }

  }

  private def appendScalaCode(context: CompileContext, classesCompiling: mutable.HashSet[CgscriptClass], emitter: Emitter): Unit = {

    logger debug s"$logPrefix Generating compiled code."

    val nonObjectSupers = classInfo.superTypes filterNot { _.baseClass == CgscriptClass.Object } map {
      _.scalaTypeName
    }
    val extendsClause = (nonObjectSupers :+ "org.cgsuite.lang.CgscriptObject") mkString " with "
    val enclosingClause = if (this == topClass) " enclosingObject =>"

    // Generate code.
    if (isSingleton && isSystem)
      emitter println s"case object $scalaClassdefName {$enclosingClause\n"
    else if (isSingleton)
      emitter println s"case object $scalaClassdefName\n  extends $extendsClause {$enclosingClause\n"
    else
      emitter println s"object $scalaClassdefName {\n"

    emitter.indent()
    emitter println s"""val _class = $classLocatorCode\n"""

    if (constructor.isDefined) {

      // Class is instantiable.
      assert(!isSingleton, this)
      emitter print "def apply("
      Parameter.emitScalaCode(classInfo.constructor.get.parameters, context, emitter)
      val applicatorName = if (isSystem) scalaTyperefName else s"$scalaClassdefName$$Impl"
      emitter print s") = $applicatorName("
      emitter print (
        classInfo.constructorParamVars map { parameter =>
          parameter.id.name
        } mkString ", "
        )
      emitter println ")\n"

    }

    val companionObjectInitializers = {
      if (isSingleton)
      // TODO Handle suspicious forward reference
        classInfo.initializers     // TODO Don't allow static declaration in singletons
      else {
        // Enum elements are handled separately, below
        classInfo.initializers filter { _.isStatic }
      }
    }

    companionObjectInitializers foreach { initializer =>
      initializer match {
        case variable: Var =>
          emitter println s"val ${variable.id.name}: ${variable.ensureElaborated().scalaTypeName} = {"
          emitter.indent()
        case _: OrdinaryInitializer =>
      }
      initializer.initializerNode foreach { node =>
        node.emitScalaCode(context, emitter)
      }
      if (initializer.isInstanceOf[Var]) {
        emitter.indent(-1)
        emitter println "\n}\n"
      }
    }

    val companionObjectMethods = {
      if (isSingleton)
        classInfo.localMethods
      else
        classInfo.localMethods filter { _.isStatic }
    }

    val companionObjectUserMethods = companionObjectMethods collect { case method: UserMethod => method }

    companionObjectUserMethods foreach { method =>

      val overrideSpec = if (method.isOverride) "override " else ""
      emitter print s"${overrideSpec}def ${method.scalaName}"
      if (!method.autoinvoke) {
        emitter print "("
        Parameter.emitScalaCode(method.parameters, context, emitter)
        emitter print ")"
      }
      emitter print ": " + method.ensureElaborated().scalaTypeName + " = "

      method.body.emitScalaCode(context, emitter)

      emitter println "\n"

    }

    if (isEnum && !isSystem) {
      classInfo.enumElements.zipWithIndex foreach { case(enumElement, ordinal) =>
        val literal = enumElement.id.name
        emitter print
          s"""val $literal = new $scalaClassdefName($ordinal, "$literal")
             |""".stripMargin
      }
    }

    emitter.indent(-1)
    emitter println "}\n"

    if (isEnum && !isSystem) {

      emitter println
        s"""case class $scalaClassdefName(ordinal: Int, literal: String) extends org.cgsuite.lang.CgscriptObject {$enclosingClause
           |  override def toString = "$nameInPackage." + literal
           |  override def _class = $scalaClassdefName._class
           |}
           |""".stripMargin

    } else if (!isSingleton && this != CgscriptClass.Object) {

      if (isSystem) {
        // We'll need to mix in any non-system antecedents as traits.
        val nonSystemAncestors = classInfo.ancestorTypes filterNot { _.baseClass.isSystem }
        val mostSpecificNonSystemAncestors = nonSystemAncestors filterNot { ancestor => nonSystemAncestors exists {
          _.baseClass < ancestor.baseClass
        }}
        val withClause = {
          if (mostSpecificNonSystemAncestors.isEmpty)
            ""
          else
            " with " + (mostSpecificNonSystemAncestors map { _.scalaTypeName } mkString " with ")
        }
        emitter println
          s"""case class $scalaClassdefName$scalaTypeParametersBlock(_instance: $scalaTyperefName$scalaTypeParametersBlock)
             |  extends org.cgsuite.lang.SystemExtensionObject$withClause {$enclosingClause
             |
             |  override def _class = $scalaClassdefName._class
             |""".stripMargin
      } else {
        emitter println s"trait $scalaClassdefName$scalaTypeParametersBlock\n  extends $extendsClause {$enclosingClause\n"
      }

      emitter.indent()

      if (!isSystem) {
        classInfo.constructorParamVars foreach { parameter =>
          emitter println s"def ${parameter.id.name}: ${parameter.ensureElaborated().scalaTypeName}\n"
        }
      }

      classInfo.initializers filter { !_.isStatic } foreach { initializer =>
        initializer match {
          case variable: Var =>
            emitter println s"val ${variable.id.name}: ${variable.ensureElaborated().scalaTypeName} = {\n"
          case _: OrdinaryInitializer =>
        }
        initializer.initializerNode foreach { node =>
          node.emitScalaCode(context, emitter)
        }
        if (initializer.isInstanceOf[Var]) {
          emitter println "}\n"
        }
      }

      val userMethods = classInfo.localMethods collect { case method: UserMethod => method }

      userMethods foreach { method =>

        val overrideSpec = if (method.isOverride) "override " else ""
        emitter print s"${overrideSpec}def ${method.scalaName}"
        // TODO This may not work for nested classes (we'd need a recursive way to capture bound type parameters)
        val allTypeParameters = method.parameters flatMap { _.paramType.allTypeVariables }
        val unboundTypeParameters = allTypeParameters.toSet -- thisClass.typeParameters
        if (unboundTypeParameters.nonEmpty) {
          emitter print "["
          emitter print (unboundTypeParameters map { _.scalaTypeName } mkString ", ")
          emitter print "]"
        }
        if (!method.autoinvoke) {
          emitter print "("
          Parameter.emitScalaCode(method.parameters, context, emitter)
          emitter print ")"
        }
        emitter print ": " + method.ensureElaborated().scalaTypeName + " = "

        method.body.emitScalaCode(context, emitter)

        emitter println "\n"

      }

      classInfo.localNestedClasses foreach { _.appendScalaCode(context, classesCompiling, emitter) }

      emitter.indent(-1)
      emitter println "}\n"

      if (constructor.isDefined && !isSystem) {

        // Class is instantiable.

        emitter print s"case class $scalaClassdefName$$Impl("
        Parameter.emitScalaCode(classInfo.constructor.get.parameters, context, emitter)
        emitter println ")"
        emitter println
          s"""  extends $scalaClassdefName {$enclosingClause
             |
             |    override def _class = $scalaClassdefName._class
             |
             |}
             |""".stripMargin

      }

    }

    // Implicit conversion for enriched system types
    if (isSystem && this != CgscriptClass.Object) {

      if (isSingleton) {
        emitter println
          s"""implicit def enrich$$$scalaClassdefName(_instance: $scalaTyperefName): $scalaClassdefName.type = {
             |  $scalaClassdefName
             |}\n""".stripMargin
      } else {
        emitter println
          s"""implicit def enrich$$$scalaClassdefName$scalaTypeParametersBlock(_instance: $scalaTyperefName$scalaTypeParametersBlock): $scalaClassdefName$scalaTypeParametersBlock = {
             |  $scalaClassdefName(_instance)
             |}\n""".stripMargin
      }

    }

  }

  def classLocatorCode: String = {
    enclosingClass match {
      case Some(cls) => cls.classLocatorCode + s""".classInfo.allInstanceNestedClassesInScope(Symbol("$name"))"""
      case None => s"""org.cgsuite.lang.CgscriptPackage.lookupClassByName("$qualifiedName").get"""
    }
  }

  trait Initializer {
    def isMutable: Boolean
    def isStatic: Boolean
    def initializerNode: Option[EvalNode]
    def ensureElaborated(): CgscriptType
  }

  case class OrdinaryInitializer(
    isMutable: Boolean,
    isStatic: Boolean,
    initializerNode: Option[EvalNode]
  ) extends Initializer {

    override def ensureElaborated() = initializerNode match {
      case Some(node) => node.ensureElaborated(new ElaborationDomain(pkg, Some(thisClass)))
      case None => CgscriptType(CgscriptClass.NothingClass)
    }

  }

  case class Var(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    isMutable: Boolean,
    isStatic: Boolean,
    explicitResultType: Option[CgscriptType],
    initializerNode: Option[EvalNode],
    isConstructorParam: Boolean = false,
    isEnumElement: Boolean = false
  ) extends Member with Initializer {

    val id = idNode.id

    def declaringClass = thisClass

    def scalaName = id.name

    override def elaborate() = {

      // TODO: Detect discrepancy between explicit result type and initializer type

      val domain = new ElaborationDomain(pkg, Some(thisClass))
      explicitResultType match {
        case Some(explicitType) => explicitType
        case None =>
          //domain.pushMember(this)  TODO Catch circular references
          val inferredResultType = initializerNode match {
            case Some(node) => node.ensureElaborated(domain)
            case None => throw EvalException("Class member with no initializer")    // TODO Improve all this
          }
          //domain.popMember()
          inferredResultType
      }

    }

    override def mentionedClasses: Iterable[CgscriptClass] = {
      (explicitResultType.toIterable flatMap { _.mentionedClasses }) ++ (initializerNode.toIterable flatMap { _.mentionedClasses })
    }

  }

  trait Method extends Member {

    def idNode: IdentifierNode
    def parameters: Vector[Parameter]
    def autoinvoke: Boolean
    def isStatic: Boolean
    def isOverride: Boolean
    def isExternal: Boolean

    val methodName = idNode.id.name
    val scalaName = methodName match {
      case "Apply" if isExternal => "map"
      case "ToList" if isExternal => "toVector"
      case "ContainsKey" if isExternal => "contains"
      case "Class" => "_class"
      case "ForAll" => "forall"
      case _ => methodName.updated(0, methodName.charAt(0).toLower)
    }
    val declaringClass = thisClass
    val qualifiedName = declaringClass.qualifiedName + "." + methodName
    val qualifiedId = Symbol(qualifiedName)
    def signature = s"$qualifiedName(${parameters.map { _.signature }.mkString(", ")})"
    val locationMessage = s"in call to `$qualifiedName`"
    def parameterTypeList = CgscriptTypeList(parameters map { _.paramType })

    var knownValidArgs: mutable.LongMap[Unit] = mutable.LongMap()

  }

  case class UserMethod(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    parametersNode: Option[ParametersNode],
    resultTypeNode: Option[TypeSpecifierNode],
    autoinvoke: Boolean,
    isStatic: Boolean,
    isOverride: Boolean,
    body: StatementSequenceNode
  ) extends Method {

    val id = idNode.id

    override def isExternal = false

    var _parameters: Vector[Parameter] = _

    def parameters = {
      if (_parameters == null)
        _parameters = parametersNode map { _.toParameters(ElaborationDomain(thisClass)) } getOrElse Vector.empty
      _parameters
    }

    override def elaborate(): CgscriptType = {

      val domain = ElaborationDomain(thisClass)
      domain.pushScope()
      parameters foreach { parameter =>
        domain.insertId(parameter.id, parameter.paramType)
      }
      val inferredType = body.ensureElaborated(domain)
      domain.popScope()

      // TODO Check if explicit result type clashes with inferred type
      val explicitResultType = resultTypeNode map { _.toType(domain) }
      explicitResultType match {
        case Some(explicitType) => explicitType
        case None => inferredType
      }

    }

    override def mentionedClasses = body.mentionedClasses

    /*
    override def elaborate(): Unit = {
      val scope = ElaborationDomain(Some(pkg), classInfo.allSymbolsInClassScope, None)
      parameters foreach { param =>
        param.methodScopeIndex = scope.insertId(param.idNode)
        param.defaultValue foreach { _.elaborate(scope) }
      }
      body.elaborate(scope)
      localVariableCount = scope.localVariableCount
    }*/

  }

  case class SystemMethod(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    parametersNode: Option[ParametersNode],
    resultTypeNode: Option[TypeSpecifierNode],
    autoinvoke: Boolean,
    isStatic: Boolean,
    isOverride: Boolean
  ) extends Method {

    val id = idNode.id

    override def isExternal = true

    var _parameters: Vector[Parameter] = _

    def parameters = {
      if (_parameters == null)
        _parameters = parametersNode map { _.toParameters(ElaborationDomain(thisClass)) } getOrElse Vector.empty
      _parameters
    }

    override def elaborate() = {
      val domain = new ElaborationDomain(pkg, Some(thisClass))
      domain.pushScope()
      parameters foreach { parameter =>
        domain.insertId(parameter.id, parameter.paramType)
        parameter.defaultValue foreach { _.ensureElaborated(domain) }
      }
      val resultType = resultTypeNode map { _.toType(domain) }
      resultType match {
        case Some(typ) => typ
        case _ => throw EvalException(s"`external` method is missing result type: `$qualifiedName`")
      }
    }

    override def mentionedClasses = parameters flatMap { _.mentionedClasses }

  }

  trait Constructor extends Method {

    def autoinvoke = false
    def isStatic = false
    def isOverride = false
    def isExternal = false
    val id = idNode.id

    def parametersNode: Option[ParametersNode]

    var _parameters: Vector[Parameter] = _

    def parameters = {
      if (_parameters == null)
        _parameters = parametersNode map { _.toParameters(ElaborationDomain(pkg, enclosingClass)) } getOrElse Vector.empty
      _parameters
    }

    override def elaborate(): CgscriptType = {
      val domain = new ElaborationDomain(pkg, Some(thisClass))
      domain.pushScope()
      parameters foreach { parameter =>
        domain.insertId(parameter.id, parameter.paramType)
        parameter.defaultValue foreach { _.ensureElaborated(domain) }
      }
      CgscriptType(thisClass, typeParameters)
    }

    override def declNode = None

    override val locationMessage = s"in call to `${thisClass.qualifiedName}` constructor"

    override def mentionedClasses = parameters flatMap { _.mentionedClasses }

  }

  case class UserConstructor(
    idNode: IdentifierNode,
    parametersNode: Option[ParametersNode]
  ) extends Constructor

  case class SystemConstructor(
    idNode: IdentifierNode,
    parametersNode: Option[ParametersNode]
  ) extends Constructor

  case class MethodGroup(override val id: Symbol, methods: Vector[MethodProjection]) extends MemberResolution {

    override def declaringClass = thisClass

    val isPureAutoinvoke = methods.size == 1 && methods.head.method.autoinvoke

    val autoinvokeMethod = methods find { _.method.autoinvoke }

    def name = methods.head.method.methodName

    def qualifiedName = methods.head.method.qualifiedName

    def isStatic = methods.head.method.isStatic

    def lookupMethod(
      argumentTypes: Vector[CgscriptType],
      namedArgumentTypes: Map[Symbol, CgscriptType],
      objectType: Option[CgscriptType] = None
      ): Option[MethodProjection] = {

      /** This assertion is not valid for implicit resolutions
      assert(
        objectType match {
          case Some(typ) if typ.baseClass == CgscriptClass.Class =>
            thisClass == CgscriptClass.Class || thisClass == typ.typeArguments.head.baseClass
          case Some(typ) => thisClass == typ.baseClass
          case None => true
        },
        (objectType.get.qualifiedName, thisClass.qualifiedName)
      )
      */

      // The following strings are used for error reporting:
      def argTypesString = argumentTypes map { "`" + _.qualifiedName + "`" } mkString ", "
      def objTypeSuffix = objectType map { " (of object `" + _.qualifiedName + "`)" } getOrElse ""

      // Determine which methods match the specified arguments
      val matchingMethods = methods filter { methodProjection =>

        val typeParameterSubstitutions = objectType map { _.typeArguments } getOrElse Vector.empty
        val substitutedParameterTypeList = methodProjection.signatureProjection substituteAll (typeParameters zip typeParameterSubstitutions)

        val requiredParametersAreSatisfied = substitutedParameterTypeList.types.indices forall { index =>
          if (index < argumentTypes.length) {
            argumentTypes(index) matches substitutedParameterTypeList.types(index)
          } else {
            val optNamedArgumentType = namedArgumentTypes get methodProjection.method.parameters(index).id
            optNamedArgumentType match {
              case Some(argumentType) => argumentType matches substitutedParameterTypeList.types(index)
              case None => methodProjection.method.parameters(index).defaultValue.isDefined
            }
          }
        }

        val allArgumentsAreValid = {
          argumentTypes.length <= substitutedParameterTypeList.types.length && {
            namedArgumentTypes.keys forall { id =>
              methodProjection.method.parameters exists { _.id == id }
            }
          }
        }

        requiredParametersAreSatisfied && allArgumentsAreValid

      }

      val reducedMatchingMethods = reduceMethodList(matchingMethods)
      if (reducedMatchingMethods.size >= 2) {
        throw EvalException(s"Method `$name`$objTypeSuffix is ambiguous when applied to $argTypesString")
      }

      reducedMatchingMethods.headOption

    }

    def resolveToMethod(
      argumentTypes: Vector[CgscriptType],
      namedArgumentTypes: Map[Symbol, CgscriptType],
      objectType: Option[CgscriptType] = None,
      withImplicits: Boolean = false
      ): MethodProjection = {

      // The following strings are used for error reporting:
      def argTypesString = argumentTypes map { "`" + _.qualifiedName + "`" } mkString ", "
      def objTypeSuffix = objectType map { " (of object `" + _.qualifiedName + "`)" } getOrElse ""

      lookupMethod(argumentTypes, namedArgumentTypes, objectType) orElse {

        if (withImplicits) {

          // Try various types of implicit conversions. This is a bit of a hack to handle
          // Rational -> DyadicRational -> Integer conversions in a few places. In later versions, this might be
          // replaced by a more elegant / general solution.

          argumentTypes.length match {

            // TODO Handle >= 2 members
            case 1 =>
              val implicitMethods = availableImplicits(argumentTypes.head) flatMap { implArgType =>
                lookupMethod(Vector(implArgType), Map.empty, objectType)
              }
              implicitMethods.headOption

            case _ => None

          }

        } else {
          None
        }

      } getOrElse {
        throw EvalException(s"Method `$name`$objTypeSuffix cannot be applied to argument types $argTypesString")
      }

    }

    def availableImplicits(typ: CgscriptType): Vector[CgscriptType] = {

      typ match {
        case ConcreteType(CgscriptClass.SidedValue, _, _) => Vector(typ, CgscriptType(CgscriptClass.CanonicalStopper), CgscriptType(CgscriptClass.Pseudonumber))
        case ConcreteType(CgscriptClass.CanonicalShortGame, _, _) => Vector(typ, CgscriptType(CgscriptClass.Uptimal))
        case ConcreteType(CgscriptClass.Rational, _, _) => Vector(typ, CgscriptType(CgscriptClass.DyadicRational), CgscriptType(CgscriptClass.Integer))
        case ConcreteType(CgscriptClass.DyadicRational, _, _) => Vector(typ, CgscriptType(CgscriptClass.Integer))
        case _ => Vector(typ)
      }

    }

    def reduceMethodList(methods: Vector[MethodProjection]) = {
      methods filterNot { methodProjection =>
        methods exists { other =>
          methodProjection != other && other.signatureProjection <= methodProjection.signatureProjection
        }
      }
    }

  }

  case class MethodProjection(
    method: CgscriptClass#Method,
    typeSubstitutions: Iterable[(TypeVariable, CgscriptType)],
    signatureProjection: CgscriptTypeList
  ) {

    def ensureElaborated(): CgscriptType = {

      val rawType = method.ensureElaborated()
      val projectedType = {
        // Special projection: "EnclosingObject" is a hard-coded specially typed exception.
        if (method.methodName == "EnclosingObject" && method.parameters.isEmpty) {
          (enclosingClass getOrElse CgscriptClass.NothingClass).mostGenericType
        } else {
          rawType.substituteAll(typeSubstitutions)
        }
      }
      projectedType match {
        case concreteType: ConcreteType => concreteType.copy(nestProjection = typeSubstitutions)
        case _ => projectedType
      }

    }

    def declaringClass = method.declaringClass
    def isExternal = method.isExternal
    def methodName = method.methodName
    def scalaName = method.scalaName

  }

}

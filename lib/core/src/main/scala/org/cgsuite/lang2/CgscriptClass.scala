package org.cgsuite.lang2

import java.io.{ByteArrayInputStream, File}
import java.net.URL
import java.nio.charset.StandardCharsets

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.antlr.runtime.tree.Tree
import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGameOps
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.Node.treeToRichTree
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.output._
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.language.{existentials, postfixOps}
import scala.tools.nsc.interpreter.IMain

object CgscriptClass {

  private[lang2] val logger = Logger(LoggerFactory.getLogger(classOf[CgscriptClass]))

  private var nextClassOrdinal = 0
  private[lang2] def newClassOrdinal = {
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
  val CanonicalShortGame = CgscriptPackage.lookupClassByName("CanonicalShortGame").get
  val ImpartialGame = CgscriptPackage.lookupClassByName("ImpartialGame").get
  val Rational = CgscriptPackage.lookupClassByName("Rational").get
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

  private[lang2] def declareSystemClass(name: String, scalaClass: Option[Class[_]] = None, explicitDefinition: Option[String] = None) {

    val path = name.replace('.', '/')
    val classdef: CgscriptClassDef = {
      explicitDefinition match {
        case Some(text) => ExplicitClassDef(text)
        case None => UrlClassDef(CgscriptClasspath.systemDir, org.cgsuite.lang.CgscriptClasspath.getClass.getResource(s"resources/$path.cgs"))
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

  val qualifiedName: String = {
    if (pkg.isRoot)
      nameInPackage
    else
      s"${pkg.qualifiedName}.$nameInPackage"
  }

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

  val scalaTyperefName: String = {
    qualifiedName match {
      case "cgsuite.lang.Nothing" => "Null"
      case "cgsuite.lang.Boolean" => "Boolean"
      case _ =>
        systemClass match {
          case Some(cls) => cls.getName
          case None =>
            enclosingClass match {
              case Some(cls) => s"${cls.scalaTyperefName}#$name"
              case None => scalaClassdefName
            }
        }
    }
  }

  val scalaClassrefName: String = {
    qualifiedName match {
      case "cgsuite.lang.Nothing" => "null"
      case _ =>
        systemClass match {
          case Some(cls) => cls.getName
          case None => scalaClassdefName
        }
    }
  }

  override def toString = s"<<$qualifiedName>>"

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
    assert(classInfoRef != null, this)
    classInfoRef
  }

  override def declNode = {
    ensureDeclaredPhase1()
    Option(classInfoRef.declNode)
  }

  def isScript = {
    ensureDeclaredPhase1()
    false // TODO
  }

  def ancestors: Vector[CgscriptClass] = classInfo.ancestors

  def properAncestors: Vector[CgscriptClass] = classInfo.properAncestors

  def isEnum: Boolean = classInfo.declNode.isEnum

  def isMutable: Boolean = classInfo.modifiers.hasMutable

  def isSingleton: Boolean = classInfo.modifiers.hasSingleton

  def isStatic: Boolean = classInfo.modifiers.hasStatic

  def isSystem: Boolean = classInfo.modifiers.hasSystem

  def constructor: Option[Constructor] = classInfo.constructor

  def typeParameters: Vector[TypeVariable] = classInfo.typeParameters

  def mostGenericType: ConcreteType = ConcreteType(this, typeParameters)

  def <=(that: CgscriptClass) = ancestors contains that

  def resolveMember(id: Symbol): Option[MemberResolution] = {
    resolveInstanceMember(id) orElse resolveStaticMember(id)
  }

  def resolveInstanceMember(id: Symbol): Option[MemberResolution] = {
    classInfo.instanceMemberLookup get id orElse {
      enclosingClass flatMap { _.resolveInstanceMember(id) }
    }
  }

  def lookupInstanceMethod(
    id: Symbol,
    argumentTypes: Vector[CgscriptType],
    namedArgumentTypes: Map[Symbol, CgscriptType],
    objectType: Option[CgscriptType] = None): Option[CgscriptClass#Method] = {

    resolveMember(id) match {
      case Some(methodGroup: MethodGroup) => methodGroup.lookupMethod(argumentTypes, namedArgumentTypes, objectType)
      case None => None
    }

  }

  def resolveInstanceMethod(
    id: Symbol,
    argumentTypes: Vector[CgscriptType],
    namedArgumentTypes: Map[Symbol, CgscriptType],
    objectType: Option[CgscriptType] = None): CgscriptClass#Method = {

    resolveMember(id) match {
      case Some(methodGroup: MethodGroup) => methodGroup.resolveToMethod(argumentTypes, namedArgumentTypes, objectType)
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

  def ensureDeclared(): Unit = {
    ensureDeclaredPhase1()
    ensureDeclaredPhase2()
  }

  def isDeclaredPhase1: Boolean = classInfoRef != null

  def ensureDeclaredPhase1(): Unit = {
    enclosingClass match {
      case Some(cls) => cls.ensureDeclaredPhase1()
      case None =>
        stage match {
          case LifecycleStage.New | LifecycleStage.Unloaded => declarePhase1()
          case _ =>   // Nothing to do
        }
    }
  }

  private def declarePhase1(): Unit = {

    logDebug(s"Declaring class (phase 1).")

    if (stage == LifecycleStage.DeclaringPhase1) {
      // TODO Better error message/handling here?
      sys.error("circular class definition?: " + qualifiedName)
    }

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
          val node = StatementSequenceNode(tree.children.head)
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

    assert(classInfoRef != null, this)

    classInfoRef.supers foreach { _.ensureDeclaredPhase2() }

    // Declare nested classes
    classInfoRef.declNode.nestedClassDeclarations foreach { decl =>
      val id = decl.idNode.id
      classInfoRef.localNestedClasses.find { _.id == id }.get.declareClassPhase1(decl)
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

  }

  private def declareClassPhase1(node: ClassDeclarationNode): Unit = {

    logger debug s"$logPrefix Declaring class."

    classInfoRef = new ClassInfo(node)

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

    val supers: Vector[CgscriptClass] = {

      if (qualifiedName == "cgsuite.lang.Object") {
        Vector.empty
      } else if (declNode.extendsClause.isEmpty) {
        Vector(if (declNode.isEnum) CgscriptClass.Enum else CgscriptClass.Object)
      } else {

        declNode.extendsClause map {
          case IdentifierNode(tree, superId) =>
            // Try looking this id up two ways:
            // First, if this is a nested class, then look it up as some other nested class
            // of this class's enclosing class;
            // Then try looking it up as a global class.
            enclosingClass flatMap { _.classInfoRef.allInstanceNestedClassesInScope get superId } getOrElse {
              pkg lookupClass superId getOrElse {
                CgscriptPackage lookupClassByName superId.name getOrElse {
                  throw EvalException(s"Unknown superclass: `${superId.name}`", tree)
                }
              }
            }
          case node: DotNode =>
            Option(node.ensureElaborated(new ElaborationDomain(Some(thisClass))).baseClass) getOrElse {
              sys.error("not found")
            }
        }

      }
    }

    supers foreach { _.ensureDeclaredPhase1() }

    val properAncestors: Vector[CgscriptClass] = supers.reverse.flatMap { _.classInfo.ancestors }.distinct

    val ancestors = properAncestors :+ CgscriptClass.this

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

      val locallyDefinedMembers = members filter { member =>
        member.declaringClass == thisClass && !member.isInstanceOf[CgscriptClass#Method]
      }
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
            s"Duplicate symbol `${id.name}` in class `$qualifiedName`",
            token = locallyDefinedMembers(1).declNode map { _.idNode.token }
          )

      }

    }

    private def validateMethods(methods: Vector[CgscriptClass#Method]): Vector[CgscriptClass#Method] = {

      val groupedBySignature = methods.distinct groupBy { _.parameterTypeList }

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

        mostSpecificMethods.head

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

  ///////////////////////////////////////////////////////////////
  // Compilation (Scala code generation)

  private def compile(eval: IMain): Unit = {

    val context = new CompileContext
    val classesCompiling = mutable.HashSet[CgscriptClass]()
    val sb = new StringBuilder

    includeInCompilationUnit(context, classesCompiling, sb)

    if (CgscriptSystem.isDebug)
      println(sb.toString)

    eval.interpret(sb.toString)

    classesCompiling foreach { compiledClass =>
      compiledClass.stage = LifecycleStage.Loaded
    }

  }

  private def includeInCompilationUnit(context: CompileContext, classesCompiling: mutable.HashSet[CgscriptClass], sb: StringBuilder): Unit = {

    enclosingClass match {

      case Some(cls) =>
        cls.includeInCompilationUnit(context, classesCompiling, sb)
        return

      case _ =>

    }

    if (stage == LifecycleStage.Loaded || (classesCompiling contains this))
      return

    if (this == CgscriptClass.NothingClass)
      return

    classesCompiling += this
    ensureElaborated()
    appendScalaCode(context, classesCompiling, sb)

    // Compile all mentioned classes that have not yet been compiled.
    mentionedClasses foreach { _.includeInCompilationUnit(context, classesCompiling, sb) }

  }

  private def appendScalaCode(context: CompileContext, classesCompiling: mutable.HashSet[CgscriptClass], sb: StringBuilder): Unit = {

    logger debug s"$logPrefix Generating compiled code."

    val nonObjectSupers = classInfo.supers filterNot { _ == CgscriptClass.Object } map { _.scalaTyperefName }
    val extendsClause = (nonObjectSupers :+ "org.cgsuite.lang2.CgscriptObject") mkString " with "
    val enclosingClause = if (this == topClass) " enclosingObject =>"

    val genericTypeParametersBlock = {
      if (typeParameters.isEmpty) {
        ""
      } else {
        val typeParametersString = typeParameters map { _.scalaTypeName } mkString ", "
        s"[$typeParametersString]"
      }
    }

    // Generate code.
    if (isSingleton)
      sb append s"case object $scalaClassdefName\n  extends $extendsClause {$enclosingClause\n\n"
    else
      sb append s"object $scalaClassdefName {\n\n"

    sb append s"""  val _class = $classLocatorCode\n\n"""

    if (constructor.isDefined) {

      // Class is instantiable.
      assert(!isSingleton, this)
      sb append "def apply("
      sb append (
        classInfo.constructor.get.parameters map { _.toScalaCode(context) } mkString ", "
        )
      val applicatorName = if (isSystem) scalaTyperefName else s"$scalaClassdefName$$Impl"
      sb append s") = $applicatorName("
      sb append (
        classInfo.constructorParamVars map { parameter =>
          parameter.id.name
        } mkString ", "
        )
      sb append ")\n\n"

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
          sb append s"val ${variable.id.name}: ${variable.ensureElaborated().scalaTypeName} = {\n\n"
        case _: OrdinaryInitializer =>
      }
      initializer.initializerNode foreach { node =>
        sb append node.toScalaCode(context)
      }
      if (initializer.isInstanceOf[Var]) {
        sb append "\n}\n\n"
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
      sb append s"${overrideSpec}def ${method.scalaName}"
      if (!method.autoinvoke) {
        sb append "("
        sb append (
          method.parameters map { _.toScalaCode(context) } mkString ", "
          )
        sb append ")"
      }
      sb append ": " + method.ensureElaborated().scalaTypeName + " = {\n\n"

      sb append method.body.toScalaCode(context)

      sb append "\n}\n\n"

    }

    if (isEnum && !isSystem) {
      classInfo.enumElements.zipWithIndex foreach { case(enumElement, ordinal) =>
        val literal = enumElement.id.name
        sb append
          s"""val $literal = new $scalaClassdefName($ordinal, "$literal")
             |""".stripMargin
      }
    }

    sb append "}\n\n"

    if (isEnum && !isSystem) {

      sb append
        s"""case class $scalaClassdefName(ordinal: Int, literal: String) extends org.cgsuite.lang2.CgscriptObject {$enclosingClause
           |  override def toString = "$nameInPackage." + literal
           |  override def _class = $scalaClassdefName._class
           |}
           |""".stripMargin

    } else if (!isSingleton && this != CgscriptClass.Object) {

      if (isSystem) {
        sb append
          s"""case class $scalaClassdefName$genericTypeParametersBlock(_instance: $scalaTyperefName$genericTypeParametersBlock)
             |  extends org.cgsuite.lang2.CgscriptObject {$enclosingClause
             |
             |  override def _class = $scalaClassdefName._class
             |  override def toOutput: org.cgsuite.output.Output = org.cgsuite.lang2.CgscriptClass.instanceToOutput(_instance)
             |\n""".stripMargin
      } else {
        sb append s"trait $scalaClassdefName\n  extends $extendsClause {$enclosingClause\n\n"
      }

      if (!isSystem) {
        classInfo.constructorParamVars foreach { parameter =>
          sb append s"def ${parameter.id.name}: ${parameter.ensureElaborated().scalaTypeName}\n\n"
        }
      }

      classInfo.initializers filter { !_.isStatic } foreach { initializer =>
        initializer match {
          case variable: Var =>
            sb append s"val ${variable.id.name}: ${variable.ensureElaborated().scalaTypeName} = {\n\n"
          case _: OrdinaryInitializer =>
        }
        initializer.initializerNode foreach { node =>
          sb append node.toScalaCode(context)
        }
        sb append "\n"
        if (initializer.isInstanceOf[Var]) {
          sb append "}\n\n"
        }
      }

      val userMethods = classInfo.localMethods collect { case method: UserMethod => method }

      userMethods foreach { method =>

        val overrideSpec = if (method.isOverride) "override " else ""
        sb append s"${overrideSpec}def ${method.scalaName}"
        // TODO This may not work for nested classes (we'd need a recursive way to capture bound type parameters)
        val allTypeParameters = method.parameters flatMap { _.paramType.allTypeVariables }
        val unboundTypeParameters = allTypeParameters.toSet -- thisClass.typeParameters
        if (unboundTypeParameters.nonEmpty) {
          sb append "["
          sb append (unboundTypeParameters map { _.scalaTypeName } mkString ", ")
          sb append "]"
        }
        if (!method.autoinvoke) {
          sb append "("
          sb append (
            method.parameters map { _.toScalaCode(context) } mkString ", "
            )
          sb append ")"
        }
        sb append ": " + method.ensureElaborated().scalaTypeName + " = {\n\n"

        sb append method.body.toScalaCode(context)

        sb append "\n}\n\n"

      }

      classInfo.localNestedClasses foreach { _.appendScalaCode(context, classesCompiling, sb) }

      sb append "}\n\n"

      if (constructor.isDefined && !isSystem) {

        // Class is instantiable.

        sb append s"case class $scalaClassdefName$$Impl("
        sb append (
          classInfo.constructor.get.parameters map { _.toScalaCode(context) } mkString ", "
          )
        sb append ")\n"
        sb append
          s"""  extends $scalaClassdefName {$enclosingClause
             |
             |    override def _class = $scalaClassdefName._class
             |
             |}
             |\n""".stripMargin

      }

    }

    // Implicit conversion for enriched system types
    if (isSystem && this != CgscriptClass.Object) {

      sb append
        s"""implicit def enrich$$$scalaClassdefName$genericTypeParametersBlock(_instance: $scalaTyperefName$genericTypeParametersBlock): $scalaClassdefName$genericTypeParametersBlock = {
           |  $scalaClassdefName(_instance)
           |}

           """.stripMargin

    }

  }

  def classLocatorCode: String = {
    enclosingClass match {
      case Some(cls) => cls.classLocatorCode + s""".classInfo.allInstanceNestedClassesInScope(Symbol("$name"))"""
      case None => s"""org.cgsuite.lang2.CgscriptPackage.lookupClassByName("$qualifiedName").get"""
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
      case Some(node) => node.ensureElaborated(new ElaborationDomain(Some(thisClass)))
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

    override def elaborate() = {

      // TODO: Detect discrepancy between explicit result type and initializer type

      val domain = new ElaborationDomain(Some(thisClass))
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
      case "Class" => "_class"
      case "ForAll" => "forall"
      case _ => methodName.updated(0, methodName.charAt(0).toLower)
    }
    val declaringClass = thisClass
    val qualifiedName = declaringClass.qualifiedName + "." + methodName
    val qualifiedId = Symbol(qualifiedName)
    def signature = s"$qualifiedName(${parameters.map { _.signature }.mkString(", ")})"
    val ordinal = CallSite.newCallSiteOrdinal
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
      val domain = new ElaborationDomain(Some(thisClass))
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

  trait Constructor extends Method with CallSite {

    def autoinvoke = false
    def isStatic = false
    def isOverride = false
    def isExternal = false
    val id = idNode.id

    def parametersNode: Option[ParametersNode]

    var _parameters: Vector[Parameter] = _

    def parameters = {
      if (_parameters == null)
        _parameters = parametersNode map { _.toParameters(ElaborationDomain(enclosingClass)) } getOrElse Vector.empty
      _parameters
    }

    override def elaborate(): CgscriptType = {
      val domain = new ElaborationDomain(Some(thisClass))
      domain.pushScope()
      parameters foreach { parameter =>
        domain.insertId(parameter.id, parameter.paramType)
        parameter.defaultValue foreach { _.ensureElaborated(domain) }
      }
      CgscriptType(thisClass, typeParameters)
    }

    override def declNode = None

    override def referenceToken = Some(idNode.token)

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

  case class MethodGroup(override val id: Symbol, methods: Vector[CgscriptClass#Method]) extends MemberResolution {

    override def declaringClass = thisClass

    val isPureAutoinvoke = methods.size == 1 && methods.head.autoinvoke

    val autoinvokeMethod = methods find { _.autoinvoke }

    def name = methods.head.methodName

    def qualifiedName = methods.head.qualifiedName

    def isStatic = methods.head.isStatic

    def lookupMethod(
      argumentTypes: Vector[CgscriptType],
      namedArgumentTypes: Map[Symbol, CgscriptType],
      objectType: Option[CgscriptType] = None,
      failFastOnSingleMethod: Boolean = false
      ): Option[CgscriptClass#Method] = {

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

      val failFast: Boolean = failFastOnSingleMethod && methods.size == 1

      // Determine which methods match the specified arguments
      val matchingMethods = methods filter { method =>

        val typeParameterSubstitutions = objectType map { _.typeArguments } getOrElse Vector.empty
        val substitutedParameterTypeList = method.parameterTypeList substituteAll (typeParameters zip typeParameterSubstitutions)

        val requiredParametersAreSatisfied = substitutedParameterTypeList.types.indices forall { index =>
          if (index < argumentTypes.length) {
            argumentTypes(index) matches substitutedParameterTypeList.types(index)
          } else {
            val optNamedArgumentType = namedArgumentTypes get method.parameters(index).id
            optNamedArgumentType match {
              case Some(argumentType) => argumentType matches substitutedParameterTypeList.types(index)
              case None => method.parameters(index).defaultValue.isDefined
            }
          }
        }

        val allArgumentsAreValid = {
          argumentTypes.length <= substitutedParameterTypeList.types.length && {
            namedArgumentTypes.keys forall { id =>
              method.parameters exists { _.id == id }
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
      objectType: Option[CgscriptType] = None
      ): CgscriptClass#Method = {

      // The following strings are used for error reporting:
      def argTypesString = argumentTypes map { "`" + _.qualifiedName + "`" } mkString ", "
      def objTypeSuffix = objectType map { " (of object `" + _.qualifiedName + "`)" } getOrElse ""

      lookupMethod(argumentTypes, namedArgumentTypes, objectType, failFastOnSingleMethod = true) getOrElse {
        throw EvalException(s"Method `$name`$objTypeSuffix cannot be applied to argument types $argTypesString")
      }

    }

    def reduceMethodList(methods: Vector[CgscriptClass#Method]) = {
      methods filterNot { method =>
        methods exists { other =>
          method != other && other.parameterTypeList <= method.parameterTypeList
        }
      }
    }

  }

}

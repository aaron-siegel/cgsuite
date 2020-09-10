package org.cgsuite.lang2

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

import scala.annotation.tailrec
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

  private[cgsuite] def logDebug(message: => String): Unit = logger debug s"$logPrefix $message"

  val classOrdinal: Int = CgscriptClass.newClassOrdinal

  val url: URL = classdef match {
    case UrlClassDef(_, x) => x
    case _ => null
  }

  val enclosingClass: Option[CgscriptClass] = classdef match {
    case NestedClassDef(cls) => Some(cls)
    case _ => None
  }

  val topClass: CgscriptClass = enclosingClass match {
    case Some(cls) => cls.topClass
    case None => this
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

  val scalaClassdefName: String = {
    if (qualifiedName == "cgsuite.lang.Nothing")
      "Nothing"
    else {
      enclosingClass match {
        case Some(_) => nameAsFullyScopedMember
        case _ => qualifiedName.replace('.', '$')
      }
    }
  }

  val scalaTyperefName: String = {
    qualifiedName match {
      case "cgsuite.lang.Nothing" => "Nothing"
      case "cgsuite.lang.Boolean" => "Boolean"
      case _ =>
        systemClass match {
          case Some(cls) => cls.getName
          case None =>
            enclosingClass match {
              case Some(cls) => s"${cls.scalaTyperefName}#$nameAsFullyScopedMember"
              case None => scalaClassdefName
            }
        }
    }
  }

  override def elaborate() = sys.error("use ensureElaborated()")

  override def ensureElaborated() = {

    classInfo.constructorParamVars foreach { _.ensureElaborated() }
    classInfo.allMembers foreach { _.ensureElaborated() }
    classInfo.localNestedClasses.values foreach { _.ensureElaborated() }
    CgscriptType(this, typeParameters)

  }

  private val locallyDefinedNestedClasses: mutable.Map[Symbol, CgscriptClass] = mutable.Map()

  private val logPrefix = f"[$classOrdinal%3d: $qualifiedName%s]"

  logger debug s"$logPrefix Formed new class with classdef: $classdef"

  private[lang2] var stage: LifecycleStage.Value = LifecycleStage.New

  private var classDeclarationNode: ClassDeclarationNode = _
  private var classInfoRef: ClassInfo = _

  def classInfo: ClassInfo = {
    if (stage == LifecycleStage.DeclaringPhase1)
      sys.error("classInfo called while still declaring: " + this)
    ensureDeclaredPhase1()
    if (this != topClass)
      topClass.ensureDeclaredPhase2()     // I'm not 100% sure abt this pattern
    assert(classInfoRef != null, this)
    classInfoRef
  }

  override def idNode = classInfo.idNode

  override def declNode = {
    ensureDeclaredPhase1()
    Option(classDeclarationNode)
  }

  def isScript = {
    ensureDeclaredPhase1()
    false // TODO
  }

  def isEnum = classDeclarationNode.isEnum

  def isMutable = classInfo.modifiers.hasMutable

  def isSingleton = classInfo.modifiers.hasSingleton

  def isSystem = classInfo.modifiers.hasSystem

  def constructor = classInfo.constructor

  def evalMethodOpt = lookupMethods('Eval).headOption   // TODO What if there's more than one Eval method

  def ancestors = classInfo.ancestors

  def initializers = classInfo.initializers

  def typeParameters = classInfo.typeParameters

  lazy val mostGenericType = CgscriptType(this, typeParameters)

  def <=(that: CgscriptClass) = ancestors contains that

  override def mentionedClasses = {

    ensureElaborated()

    (classInfo.supers flatMap { _.mentionedClasses }) ++
      (classInfo.allMembers flatMap { _.mentionedClasses }) ++
      (classInfo.localNestedClasses.values flatMap { _.mentionedClasses }) :+
      this
    // TODO Initializers?

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
    locallyDefinedNestedClasses.values foreach { _.buildUnloadList(list )}

  }

  private def addDerivedClassesToUnloadList(list: mutable.HashSet[CgscriptClass], cls: CgscriptClass): Unit = {
    if (cls.classInfoRef != null && (cls.classInfoRef.supers contains this)) {
      cls.topClass.buildUnloadList(list)
    }
    // Recurse through nested classes
    cls.locallyDefinedNestedClasses.values foreach { addDerivedClassesToUnloadList(list, _) }
  }

  def lookupMethods(id: Symbol): Vector[CgscriptClass#Method] = {
    classInfo.allMethodsInScope.getOrElse(id, Vector.empty)
  }

  def lookupMethod(id: Symbol, argumentTypes: Vector[CgscriptType], typeParameterSubstitutions: Vector[CgscriptType] = Vector.empty): Option[CgscriptClass#Method] = {
    val allMethods = lookupMethods(id)
    val argumentTypeList = CgscriptTypeList(argumentTypes)
    val matchingMethods = allMethods filter { method =>
      val substitutedParameterTypeList = method.parameterTypeList substituteAll (typeParameters zip typeParameterSubstitutions)
      argumentTypeList <= substitutedParameterTypeList
    }
    val reducedMatchingMethods = reduceMethodList(matchingMethods)
    if (reducedMatchingMethods.size >= 2) {
      val argTypeNames = argumentTypes map { "`" + _.qualifiedName + "`" } mkString ", "
      reducedMatchingMethods foreach { m => println(m.qualifiedName) }
      throw EvalException(s"Method `${id.name}` of class `$qualifiedName` is ambiguous when applied to $argTypeNames")
    }
    reducedMatchingMethods.headOption
  }

  def reduceMethodList(methods: Vector[CgscriptClass#Method]) = {
    methods filterNot { method =>
      methods exists { other =>
        method != other && other.parameterTypeList <= method.parameterTypeList
      }
    }
  }

  def lookupNestedClass(id: Symbol): Option[CgscriptClass] = {
    classInfo.allNestedClassesInScope.get(id)
  }

  def lookupVar(id: Symbol): Option[CgscriptClass#Var] = {
    classInfo.classVarLookup.get(id) orElse (enclosingClass flatMap { _.lookupVar(id) })
  }

  def lookupStaticVar(id: Symbol): Option[CgscriptClass#Var] = {
    classInfo.staticVarLookup.get(id) orElse (enclosingClass flatMap { _.lookupVar(id) })
  }

  /*
  def lookupMember(id: Symbol): Option[Member] = {
    ensureDeclared()
    lookupMethod(id) orElse lookupNestedClass(id) orElse lookupVar(id)
  }
  */
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

    val companionObjectVars = {
      if (isSingleton)
        classInfo.localClassVars
      else
        classInfo.staticVars
    }

    companionObjectVars foreach { variable =>
      sb append s"val ${variable.id.name}: ${variable.resultType.scalaTypeName} = {\n\n"
      variable.initializerNode foreach { node =>
        sb append node.toScalaCode(context)
      }
      sb append "\n}\n\n"
    }

    val companionObjectMethods = {
      if (isSingleton)
        classInfo.localMethods
      else
        Vector.empty //classInfo.staticMethods
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
      sb append ": " + method.resultType.scalaTypeName + " = {\n\n"

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
           |  override def toString = "$name." + literal
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
          sb append s"def ${parameter.id.name}: ${parameter.resultType.scalaTypeName}\n\n"
        }
      }

      classInfo.localClassVars foreach { variable =>
        sb append s"val ${variable.id.name}: ${variable.resultType.scalaTypeName} = {\n\n"
        variable.initializerNode foreach { node =>
          sb append node.toScalaCode(context)
        }
        sb append "\n}\n\n"
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
        sb append ": " + method.resultType.scalaTypeName + " = {\n\n"

        sb append method.body.toScalaCode(context)

        sb append "\n}\n\n"

      }

      classInfo.localNestedClasses.values foreach { _.appendScalaCode(context, classesCompiling, sb) }

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
      case Some(cls) => cls.classLocatorCode + s""".lookupNestedClass(Symbol("$nameAsFullyScopedMember")).get"""
      case None => s"""org.cgsuite.lang2.CgscriptPackage.lookupClassByName("$qualifiedName").get"""
    }
  }

  private def declarePhase1(): Unit = {

    logger debug s"$logPrefix Declaring (phase 1)."

    if (stage == LifecycleStage.DeclaringPhase1) {
      // TODO Better error message/handling here?
      sys.error("circular class definition?: " + qualifiedName)
    }

    // Force constants to declare first
    pkg lookupClass 'constants foreach { constantsCls =>
      if (constantsCls != this) constantsCls.ensureDeclaredPhase1()
    }

    val tree = parseTree()

    logger debug s"$logPrefix Parsed class: ${tree.toStringTree}"

    stage = LifecycleStage.DeclaringPhase1

    try {

      tree.getType match {

        case SCRIPT =>
          val node = StatementSequenceNode(tree.children.head)
          logger debug s"$logPrefix Script Node: $node"
          declareScript(node)

        case EOF =>
          val node = ClassDeclarationNode(tree.children(1), pkg)
          logger debug s"$logPrefix Class Node: $node"
          declareClass(node)

      }

    } finally {

      // If an exception occurred, we want to treat this class as Unloaded
      stage = LifecycleStage.Unloaded

    }

    stage = LifecycleStage.DeclaredPhase1

    logger debug s"$logPrefix Done declaring (phase 1)."

  }

  private def declarePhase2(): Unit = {

    assert(classInfoRef != null, this)

    classInfoRef.supers foreach { _.ensureDeclaredPhase2() }

    // Declare nested classes
    classDeclarationNode.nestedClassDeclarations foreach { decl =>
      val id = decl.idNode.id
      classInfoRef.localNestedClasses(id).declareClass(decl)
    }

    //validateClass()

    stage = LifecycleStage.Declared

  }

  private def parseTree(): Tree = {

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

  private def declareScript(node: StatementSequenceNode): Unit = {

    classDeclarationNode = null
    classInfoRef = null

  }

  private def declareClass(node: ClassDeclarationNode): Unit = {

    logger debug s"$logPrefix Declaring class."

    classDeclarationNode = node

    if (node.idNode.id.name != nameAsFullyScopedMember)
      throw EvalException(s"Class name does not match filename: `${node.idNode.id.name}` (was expecting `$nameAsFullyScopedMember`)", node.idNode.tree)

    val typeParameters = node.typeParameters map { typeParameterNode =>
      TypeVariable(typeParameterNode.id)
    }

    val supers = {

      if (qualifiedName == "cgsuite.lang.Object") {
        Vector.empty
      } else if (node.extendsClause.isEmpty) {
        Vector(if (node.isEnum) CgscriptClass.Enum else CgscriptClass.Object)
      } else {

        node.extendsClause map {
          case IdentifierNode(tree, superId) =>
            // Try looking this id up two ways:
            // First, if this is a nested class, then look it up as some other nested class
            // of this class's enclosing class;
            // Then try looking it up as a global class.
            enclosingClass flatMap { _.classInfoRef.allNestedClassesInScope get superId } getOrElse {
              pkg lookupClass superId getOrElse {
                CgscriptPackage lookupClass superId getOrElse {
                  throw EvalException(s"Unknown superclass: `${superId.name}`", tree)
                }
              }
            }
          case node: DotNode =>
            Option(node.ensureElaborated(new ElaborationDomain(Some(this))).baseClass) getOrElse {
              sys.error("not found")
            }
        }

      }
    }

    supers foreach { _.ensureDeclaredPhase1() }

    val localMethods = node.methodDeclarations map { parseMethod(_, node.modifiers) }
    val localNestedClasses = node.nestedClassDeclarations map { decl =>
      val id = decl.idNode.id
      val newClass = locallyDefinedNestedClasses getOrElseUpdate (id, new CgscriptClass(pkg, NestedClassDef(this), id))
      (id, newClass)
    } toMap
    val constructor = node.constructorParams map { parametersNode =>
      systemClass match {
        case Some(_) => SystemConstructor(node.idNode, Some(parametersNode))
        case None => UserConstructor(node.idNode, Some(parametersNode))
      }
    }
    val localMembers = localMethods ++ localNestedClasses

    // Check for duplicate methods.

    /*
    localMembers groupBy { _._1 } find { _._2.size > 1 } foreach { case (memberId, _) =>
      throw EvalException(s"Member `${memberId.name}` is declared twice in class `$qualifiedName`", node.tree)
    }
    */
    /*

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
        throw EvalException(
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
    */
    // override modifier validation.
    // TODO for Nested classes too!

    /*
    localMethods foreach { case (methodId, method) =>
      if (method.isOverride) {
        if (!superMethods.exists { case (superId, _) => superId == methodId }) {
          throw EvalException(
            s"Method `${method.qualifiedName}` overrides nothing",
            token = Some(method.idNode.token)
          )
        }
      } else {
        superMethods find { case (superId, _) => superId == methodId } match {
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
    /*
    if (systemClass.isDefined) {
      resolvedSuperMembers foreach {
        case (id, method: CgscriptClass#SystemMethod) =>
          val javaParameterTypes = method.javaMethod.getParameterTypes
          try {
            val overrider = javaClass.getMethod(method.javaMethod.getName, javaParameterTypes : _*)
            if (overrider != method.javaMethod && !(localMembers exists { _._1 == id })) {
              logger.warn(
                s"Method `${method.qualifiedName}` with Java method `${method.javaMethod.getName}` is overridden in Java class `${javaClass.getName}`, but is not redeclared in class `$qualifiedName`."
              )
            }
          } catch {
            case exc: NoSuchMethodException =>
          }
        case _ =>
      }
    }
    */

    //val allMembers = resolvedSuperMembers ++ renamedSuperMembers ++ localMembers
    //val (allMethods, allNestedClasses) = localMembers partition { _._2.isInstanceOf[CgscriptClass#Method] }

    // TODO Handle overrides & inheritance conflicts

    val inheritedMethods = supers flatMap { _.classInfo.methods.values.flatten }

    // TODO Resolve conflicts
    val inheritedNestedClasses = (supers flatMap { _.classInfo.allNestedClasses }).toMap

    val allNestedClasses = inheritedNestedClasses ++ localNestedClasses

    classInfoRef = new ClassInfo(
      node.idNode,
      node,
      node.modifiers,
      typeParameters,
      supers,
      localNestedClasses,
      allNestedClasses,
      localMethods,
      inheritedMethods,
      constructor,
      node.ordinaryInitializers,
      node.staticInitializers,
      node.enumElements
    )

    logger debug s"$logPrefix Done declaring class (phase 1)."

  }

  private def checkOverrides(methods: Vector[CgscriptClass#Method]): Vector[CgscriptClass#Method] = {

    val groupedBySignature = methods groupBy { _.parameterTypeList }

    val resolved = groupedBySignature map { case (signature, matchingMethods) =>

      if (matchingMethods.size == 1) {
        // TODO Check no "override" keyword
      } else {
        // TODO Check "override" keyword
        // TODO Check that method is *locally* defined (if not, it's a conflict)
        // TODO Check that result types match
      }

      matchingMethods.head

    }

    resolved.toVector

  }

  private def validateClass(): Unit = {

    // Check for duplicate vars (we make an exception for the constructor)

    (classInfoRef.inheritedClassVars ++ classInfoRef.localClassVars) groupBy { _.id } foreach { case (varId, vars) =>
      if (vars.size > 1 && (vars exists { !_.isConstructorParam })) {
        val class1 = vars.head.declaringClass
        val class2 = vars(1).declaringClass
        if (class1 == thisClass && class2 == thisClass)
          throw EvalException(s"Variable `${varId.name}` is declared twice in class `$qualifiedName`", classDeclarationNode.tree)
        else if (class2 == thisClass)
          throw EvalException(s"Variable `${varId.name}` in class `$qualifiedName` shadows definition in class `${class1.qualifiedName}`")
        else
          throw EvalException(s"Variable `${varId.name}` is defined in multiple superclasses: `${class1.qualifiedName}`, `${class2.qualifiedName}`")
      }
    }

    // Check for member/var conflicts

    // TODO We may want to allow defs to override vars in the future - then the vars become private to the
    // superclass. For now, we prohibit it.

    classInfoRef.methods ++ classInfoRef.allNestedClasses foreach { case (memberId, member) =>
      if (classInfoRef.classVarLookup contains memberId)
        throw EvalException(
          s"Member `${memberId.name}` conflicts with a var declaration in class `$qualifiedName`",
          token = Some(classInfoRef.idNode.token)    // TODO Would rather use member.idNode.token but don't have it working yet
        )
    }

    // Check that singleton => no constructor
    if (classInfoRef.constructor.isDefined && classDeclarationNode.modifiers.hasSingleton) {
      throw EvalException(
        s"Class `$qualifiedName` must not have a constructor if declared `singleton`",
        token = Some(classInfoRef.idNode.token)
      )
    }

    // Check that no superclass is a singleton
    classInfoRef.supers foreach { ancestor =>
      if (ancestor.isSingleton) {
        throw EvalException(
          s"Class `$qualifiedName` may not extend singleton class `${ancestor.qualifiedName}`",
          token = Some(classInfoRef.idNode.token)
        )
      }
    }

    // Check that constants classes must be singletons
    if (name == "constants" && !classDeclarationNode.modifiers.hasSingleton) {
      throw EvalException(
        s"Constants class `$qualifiedName` must be declared `singleton`",
        token = Some(classInfoRef.idNode.token)
      )
    }

    if (classDeclarationNode.modifiers.hasMutable) {
      // If we're mutable, check that no nested class is immutable
      classDeclarationNode.nestedClassDeclarations foreach { nested =>
        if (!nested.modifiers.hasMutable) {
          throw EvalException(
            s"Nested class `${nested.idNode.id.name}` of mutable class `$qualifiedName` is not declared `mutable`",
            token = Some(nested.idNode.token)
          )
        }
      }
    } else {
      // If we're immutable, check that no superclass is mutable
      classInfoRef.supers foreach { spr =>
        if (spr.isMutable) {
          throw EvalException(
            s"Subclass `$qualifiedName` of mutable class `${spr.qualifiedName}` is not declared `mutable`",
            token = Some(classInfoRef.idNode.token)
          )
        }
      }
      // ... and that there are no mutable vars
      classInfoRef.initializers foreach {
        case declNode: VarDeclarationNode if !declNode.modifiers.hasStatic && declNode.modifiers.hasMutable =>
          throw EvalException(
            s"Class `$qualifiedName` is immutable, but variable `${declNode.idNode.id.name}` is declared `mutable`",
            token = Some(declNode.idNode.token)
          )
        case _ =>
      }
    }

    // Check that immutable class vars are assigned at declaration time
    /*
    classInfoRef.initializers collect {
      // This is a little bit of a hack, we look for "phantom" constant nodes since those are indicative of
      // a "default" nil value. This could be refactored to be a bit more elegant.
      case VarDeclarationNode(_, AssignToNode(_, idNode, ConstantNode(null, _), AssignmentDeclType.ClassVarDecl), modifiers)
        if !modifiers.hasMutable =>
        throw EvalException(
          s"Immutable variable `${idNode.id.name}` must be assigned a value (or else declared `mutable`)",
          token = Some(idNode.token)
        )
    }
  */
  }
/*
  private def initialize(): Unit = {

    try {
      if (classDeclarationNode != null)
        initializeClass(classDeclarationNode)
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

    logger debug s"$logPrefix Initializing class."

    // Create the class object
    classObjectRef = new ClassObject(CgscriptClass.this)

    // Declare nested classes
    node.nestedClassDeclarations foreach { decl =>
      val id = decl.idNode.id
      classInfo.nestedClasses(id).declareClass(decl)
    }

    // Elaborate methods
    constructor foreach { _.elaborate() }
    classInfoRef.methods foreach { case (_, method) =>
      if (method.declaringClass == this)
        method.elaborate()
    }

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

    logger debug s"$logPrefix Done initializing class."

    stage = LifecycleStage.Initialized

    // Initialize nested classes
    node.nestedClassDeclarations foreach { decl =>
      val id = decl.idNode.id
      classInfo.nestedClasses(id).initializeClass(decl)
    }

  }
*/
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
        /*
        SpecialMethods.specialMethods.get(qualifiedName + "." + name) match {
          case Some(fn) =>
            logger.debug(s"$logPrefix   It's a special method.")
            ExplicitMethod(node.idNode, Some(node), parameters, explicitReturnType, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride)(fn)
          case None =>
            val externalName = name.updated(0, name(0).toLower)
            val externalMethod = try {
              javaClass.getMethod(externalName, externalParameterTypes: _*)
            } catch {
              case exc: NoSuchMethodException =>
                throw EvalException(s"Method is declared `external`, but has no corresponding Java method (in Java class `$javaClass`): `$qualifiedName.$name`", node.tree)
            }
            logger.debug(s"$logPrefix   Found the Java method: $externalMethod")
            SystemMethod(node.idNode, Some(node), parameters, explicitReturnType, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride, externalMethod)
        }
         */
      } else {
        logger.debug(s"$logPrefix Declaring user method: $name")
        val body = node.body getOrElse { sys.error("no body") }
        UserMethod(node.idNode, Some(node), parameters, node.returnType, autoinvoke, node.modifiers.hasStatic, node.modifiers.hasOverride, body)
      }
    }

    newMethod

  }

  override def toString = s"<<$qualifiedName>>"

  class ClassInfo(
    val idNode: IdentifierNode,
    val declNode: ClassDeclarationNode,
    val modifiers: Modifiers,
    val typeParameters: Vector[TypeVariable],
    val supers: Vector[CgscriptClass],
    val localNestedClasses: Map[Symbol, CgscriptClass],
    val allNestedClasses: Map[Symbol, CgscriptClass],
    val localMethods: Seq[Method],
    val inheritedMethods: Seq[CgscriptClass#Method],
    val constructor: Option[CgscriptClass#Constructor],
    val initializers: Seq[InitializerNode],
    val staticInitializers: Seq[InitializerNode],
    val enumElementNodes: Seq[EnumElementNode]
    ) {

    val properAncestors: Vector[CgscriptClass] = supers.reverse.flatMap { _.classInfo.ancestors }.distinct
    val ancestors = properAncestors :+ CgscriptClass.this

    val staticVars: Seq[CgscriptClass#Var] = staticInitializers collect {
      case declNode: VarDeclarationNode if declNode.modifiers.hasStatic =>
        Var(declNode.idNode, Some(declNode), declNode.modifiers, None, Some(declNode.body.children(1)))   // TODO: Explicit result type
    }
    val staticVarLookup: Map[Symbol, CgscriptClass#Var] = staticVars map { v => (v.id, v) } toMap

    val enumElements: Seq[CgscriptClass#Var] = enumElementNodes map { node =>
      Var(node.idNode, Some(node), node.modifiers, Some(CgscriptType(thisClass)), None)
    }

    val inheritedClassVars = supers.flatMap { _.classInfo.allClassVars }.distinct
    lazy val constructorParamVars = constructor match {
      case Some(ctor) => ctor.parameters map { param =>
        Var(param.idNode, Some(declNode), Modifiers.none, Some(param.paramType), None, isConstructorParam = true)
      }
      case None => Seq.empty
    }
    val localClassVars = initializers collect {
      case declNode: VarDeclarationNode if !declNode.modifiers.hasStatic =>
        Var(declNode.idNode, Some(declNode), declNode.modifiers, None, Some(declNode.body.children(1)))   // TODO: Explicit result type
    }
    lazy val allClassVars: Seq[CgscriptClass#Var] = constructorParamVars ++ inheritedClassVars ++ localClassVars
    lazy val allClassVarSymbols: Seq[Symbol] = allClassVars map { _.id } distinct
    lazy val classVarLookup: Map[Symbol, CgscriptClass#Var] = allClassVars map { v => (v.id, v) } toMap
    lazy val classVarOrdinals: Map[Symbol, Int] = allClassVarSymbols.zipWithIndex.toMap

    lazy val staticVarSymbols: Seq[Symbol] = enumElementNodes.map { _.idNode.id } ++ staticVars.map { _.idNode.id }
    lazy val staticVarOrdinals: Map[Symbol, Int] = staticVarSymbols.zipWithIndex.toMap

    lazy val methods: Map[Symbol, Vector[CgscriptClass#Method]] =
      (localMethods ++ inheritedMethods) groupBy { _.id } mapValues { methods =>
        checkOverrides(methods.toVector)
      }

    lazy val allSymbolsInThisClass: Set[Symbol] = {
      classVarOrdinals.keySet ++ staticVarOrdinals.keySet ++ methods.keySet ++ allNestedClasses.keySet
    }

    lazy val allSymbolsInClassScope: Seq[Set[Symbol]] = {
      allSymbolsInThisClass +: enclosingClass.map { _.classInfo.allSymbolsInClassScope }.getOrElse(Seq.empty)
    }

    lazy val allMethodsInScope: Map[Symbol, Vector[CgscriptClass#Method]] = {
      (enclosingClass map { _.classInfo.allMethodsInScope } getOrElse Map.empty) ++ methods
    }

    lazy val allNestedClassesInScope: Map[Symbol, CgscriptClass] = {
      (enclosingClass map { _.classInfo.allNestedClassesInScope } getOrElse Map.empty) ++ allNestedClasses
    }

    lazy val allMembers = {
      (methods flatMap { _._2 }) ++ allClassVars ++ staticVars ++ enumElements ++ constructor
    }.toVector
    /*
    lazy val allMembersInScope: Map[Symbol, Member] = {
      classVarLookup ++ allMethodsInScope ++ allNestedClassesInScope
    }
    lazy val allNonSuperMembersInScope: Map[Symbol, Member] = {
      allMembersInScope filterNot { case (symbol, _) => symbol.name startsWith "super$" }
    }
    */
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
      // TODO What if there's more than one?
      lookupMethods(id).headOption getOrElse {
        throw EvalException(s"No method `${id.name}` for class: `$qualifiedName`")
      }
    }

  }

  case class Var(
    idNode: IdentifierNode,
    declNode: Option[MemberDeclarationNode],
    modifiers: Modifiers,
    explicitResultType: Option[CgscriptType],
    initializerNode: Option[EvalNode],
    isConstructorParam: Boolean = false
  ) extends Member {

    def declaringClass = thisClass

    def isMutable = modifiers.hasMutable

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

    /*
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
    */
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

    def parametersNode: Option[ParametersNode]

    var _parameters: Vector[Parameter] = _

    def parameters = {
      if (_parameters == null)
        _parameters = parametersNode map { _.toParameters(ElaborationDomain(thisClass)) } getOrElse Vector.empty
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

}

package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.{CalculationCanceledException, CgsuiteException, EvalException}
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.lang._
import org.cgsuite.output.EmptyOutput

import scala.collection.mutable

object FunctionCallNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSite = EvalNode(tree.head)
    val argsWithNames = tree.children(1).children.map { t =>
      t.getType match {
        case BIGRARROW => (EvalNode(t.children(1)), Some(IdentifierNode(t.head)))
        case _ => (EvalNode(t), None)
      }
    }
    val (args, argNames) = argsWithNames.unzip
    FunctionCallNode(tree, callSite, args, argNames)
  }
}

case class FunctionCallNode(
  tree: Tree,
  callSiteNode: EvalNode,
  argNodes: Vector[EvalNode],
  argNames: Vector[Option[IdentifierNode]]
) extends EvalNode {

  val methodGroupResolutions = mutable.LongMap[MethodGroupResolution]()
  val callSiteResolutions = mutable.LongMap[CallSiteResolution]()

  // Some profiler keys
  val prepareCallSite = Symbol(s"PrepareCallSite [${tree.location}]")
  val prepareCallArgs = Symbol(s"PrepareCallArgs [${tree.location}]")
  val functionCall = Symbol(s"FunctionCall [${tree.location}]")

  override val children = (callSiteNode +: argNodes) ++ argNames.flatten

  val ordinaryArgCount = 1 + argNames.lastIndexWhere { _.isEmpty }

  override def elaborate(scope: ElaborationDomain): Unit = {

    callSiteNode elaborate scope
    argNodes foreach { _ elaborate scope }

    // Check for named args in earlier position than ordinary args
    argNames take ordinaryArgCount foreach {
      case None =>
      case Some(idNode) => throw EvalException(
        s"Named parameter `${idNode.id.name}` appears in earlier position than an ordinary argument",
        token = Some(idNode.token)
      )
    }

    // Check for duplicate named parameter
    val definedArgNames = argNames collect { case Some(idNode) => idNode }
    for (i <- definedArgNames.indices; j <- 0 until i) {
      if (definedArgNames(i).id == definedArgNames(j).id) {
        throw EvalException(
          s"Duplicate named parameter: `${definedArgNames(i).id.name}`",
          token = Some(definedArgNames(i).token)
        )
      }
    }

  }

  override def evaluate(domain: EvaluationDomain): Any = {

    if (Thread.interrupted())
      throw CalculationCanceledException("Calculation canceled by user.", token = Some(token))

    val obj = callSiteNode match {
      case idNode: IdentifierNode => idNode.evaluate(domain, asFunctionCallAntecedent = true)
      case dotNode: DotNode => dotNode.evaluate(domain, asFunctionCallAntecedent = true)
      case node => node.evaluate(domain)
    }

    obj match {
      case cs: CallSite => resolveAndEvaluateCallSite(domain, cs)
      case img: InstanceMethodGroup => resolveAndEvaluateMethod(domain, img.enclosingObject, img.methodGroup)
      case script: Script => resolveAndEvaluateScript(domain, script)
      case co: ClassObject =>
        try {
          val evalMethod = co.forClass.classInfo.staticEvalMethod
          resolveAndEvaluateMethod(domain, null, evalMethod)
        } catch {
          case exc: CgsuiteException =>
            exc addToken token
            throw exc
        }
      case any =>
        try {
          val evalMethod = CgscriptClass.of(any).classInfo.evalMethod
          resolveAndEvaluateMethod(domain, any, evalMethod)
        } catch {
          case exc: CgsuiteException =>
            exc addToken token
            throw exc
        }
    }

  }

  def resolveAndEvaluateCallSite(domain: EvaluationDomain, callSite: CallSite): Any = {

    val args = evaluateArgsForCallSite(
      domain,
      callSite.ordinal,
      callSite.parameters,
      callSite.locationMessage,
      callSite.allowMutableArguments
    )

    try {
      callSite call args
    } catch {
      case exc: CgsuiteException =>
        exc addToken token
        throw exc
      case _: StackOverflowError =>
        throw EvalException("Possible infinite recursion.", token = Some(token))
    }

  }

  def evaluateArgsForCallSite(
    domain: EvaluationDomain,
    ordinal: Int,
    parameters: Vector[Parameter],
    locationMessage: String,
    allowMutableArguments: Boolean = true
  ): Array[Any] = {

    val res = callSiteResolutions.getOrElseUpdate(ordinal, CallSiteResolution(parameters, locationMessage))

    val args = new Array[Any](parameters.length)
    var i = 0
    while (i < args.length) {
      if (res.expandedLastParameter && i == args.length - 1) {
        args(i) = argNodes.slice(i, argNodes.length) map { _.evaluate(domain) }
      } else {
        if (res.parameterToArgsMapping(i) >= 0)
          args(i) = argNodes(res.parameterToArgsMapping(i)).evaluate(domain)
        else
          args(i) = parameters(i).defaultValue.get.evaluate(domain)
      }
      i += 1
    }

    res.validateArguments(args, allowMutableArguments)
    args

  }

  def resolveAndEvaluateScript(domain: EvaluationDomain, script: Script): Any = {

    val scriptDomain = new EvaluationDomain(new Array[Any](script.scope.localVariableCount), dynamicVarMap = domain.dynamicVarMap)
    val result = script.node evaluate scriptDomain
    if (script.node.suppressOutput)
      EmptyOutput
    else
      result

  }

  def resolveAndEvaluateMethod(domain: EvaluationDomain, baseObject: Any, methodGroup: CgscriptClass#MethodGroup): Any = {

    if (methodGroup.methodsWithArguments.size == 1) {
      resolveAndEvaluateSingletonMethod(domain, baseObject, methodGroup)
    } else {
      resolveAndEvaluateOverloadedMethod(domain, baseObject, methodGroup)
    }

  }

  def resolveAndEvaluateSingletonMethod(domain: EvaluationDomain, baseObject: Any, methodGroup: CgscriptClass#MethodGroup): Any = {

    val method = methodGroup.methodsWithArguments.head
    val args = evaluateArgsForCallSite(
      domain,
      methodGroup.ordinal,
      method.parameters,
      method.locationMessage,
      allowMutableArguments = method.allowMutableArguments
    )

    try {
      method.call(baseObject, args)
    } catch {
      case exc: CgsuiteException =>
        exc addToken token
        throw exc
      case _: StackOverflowError =>
        throw EvalException("Possible infinite recursion.", token = Some(token))
    }

  }

  def resolveAndEvaluateOverloadedMethod(domain: EvaluationDomain, baseObject: Any, methodGroup: CgscriptClass#MethodGroup): Any = {

    val methodGroupResolution = methodGroupResolutions.getOrElseUpdate(methodGroup.ordinal, MethodGroupResolution(methodGroup))

    val args = new Array[Any](argNodes.length)
    var i = 0
    while (i < args.length) {
      args(i) = argNodes(i).evaluate(domain)
      i += 1
    }

    val methodResolution = methodGroupResolution.resolveToMethod(args)

    val mappedArgs = new Array[Any](methodResolution.method.parameters.length)

    i = 0
    while (i < mappedArgs.length) {
      if (methodResolution.expandedLastParameter && i == mappedArgs.length - 1) {
        mappedArgs(i) = args.slice(i, args.length).toVector
      } else {
        if (methodResolution.parameterToArgsMapping(i) >= 0)
          mappedArgs(i) = args(methodResolution.parameterToArgsMapping(i))
        else
        // TODO Validate default
          mappedArgs(i) = methodResolution.method.parameters(i).defaultValue.get.evaluate(domain)
      }
      i += 1
    }

    try {
      methodResolution.method.call(baseObject, mappedArgs)
    } catch {
      case exc: CgsuiteException =>
        exc addToken token
        throw exc
      case _: StackOverflowError =>
        throw EvalException("Possible infinite recursion.", token = Some(token))
    }

  }

  case class MethodGroupResolution(methodGroup: CgscriptClass#MethodGroup) {

    val shortMethodResolutions = mutable.LongMap[MethodResolution]()
    val longMethodResolutions = mutable.Map[Vector[Short], MethodResolution]()

    def resolveToMethod(args: Array[Any]): MethodResolution = {

      var classcode: Long = 0
      var classVec: Vector[Short] = Vector.empty

      if (args.length <= 4) {
        var i = 0
        while (i < args.length) {
          classcode |= (CgscriptClass of args(i)).classOrdinal
          classcode <<= 16
          i += 1
        }
        if (shortMethodResolutions.contains(classcode))
          return shortMethodResolutions(classcode)
      } else {
        classVec = args.toVector map { arg => (CgscriptClass of arg).classOrdinal.toShort }
        if (longMethodResolutions.contains(classVec))
          return longMethodResolutions(classVec)
      }

      val argNameIds = argNames.flatten map { _.id }
      val argTypes = args map CgscriptClass.of

      // Check that each named parameter matches at least one method
      // (if not, fail-fast and generate a helpful error message)
      // TODO: Unit test for this
      val allParameterNames = methodGroup.methodsWithArguments flatMap { _.parameters } map { _.id }
      argNames foreach {
        case Some(node) =>
          if (!allParameterNames.contains(node.id)) {
            throw EvalException(
              s"Invalid parameter name (${methodGroup.methods.head.locationMessage}): `${node.id.name}`",
              token = Some(token)
            )
          }
        case None =>
      }

      val matchingMethods = methodGroup.methodsWithArguments filter { method =>

        val parameters = method.parameters

        // Last parameter is expandable only if there are no named args
        val expandedLastParameter = parameters.nonEmpty && parameters.last.isExpandable && argNames.forall { _.isEmpty }

        // Validate that no named parameter shadows an ordinary argument
        // (if this fails for any method, it will cause the entire method group to fail)
        parameters take ordinaryArgCount foreach { param =>
          argNames find { _ exists { _.id == param.id } } match {
            case Some(Some(node)) =>
              throw EvalException(
                s"Named parameter shadows an earlier ordinary argument (${method.locationMessage}): `${param.id.name}`",
                token = Some(node.token)
              )
            case _ =>
          }
        }

        // There are not too many arguments
        args.length <= parameters.length && {

          // Every required parameter is either specified by an unnamed argument
          // or is explicitly named
          parameters drop ordinaryArgCount forall { param =>
            param.defaultValue.nonEmpty || argNameIds.contains(param.id)
          }

        } && {

          // Argument types are valid
          parameters.indices forall { i =>
            val expectedType = {
              if (expandedLastParameter && i == parameters.length - 1) {
                CgscriptClass.List
              } else {
                parameters(i).paramType
              }
            }
            val mappedIndex = {
              if (i < ordinaryArgCount) {
                i
              } else {
                argNames indexWhere { _ exists { _.id == parameters(i).id } }
              }
            }
            assert(parameters(i).defaultValue.nonEmpty || mappedIndex >= 0)    // This was checked above
            mappedIndex == -1 || argTypes(mappedIndex).ancestors.contains(expectedType)
          }

        }

      }

      // Filter out a method f in matchingMethods if it is "dominated" by another matching method g,
      // in the sense that:
      // (i) f and g have exactly the same number of parameters
      // (ii) every parameter type of g is a subclass of the corresponding parameter type of f.
      val reducedMatchingMethods = {
        matchingMethods filterNot { method =>
          matchingMethods exists { otherMethod =>
            otherMethod != method &&
              otherMethod.parameters.length == method.parameters.length &&
              method.parameters.indices.forall { i => otherMethod.parameters(i).paramType.ancestors contains method.parameters(i).paramType }
          }
        }
      }

      def argTypesString = if (argTypes.isEmpty) "()" else argTypes map { "`" + _.qualifiedName + "`" } mkString ", "

      if (reducedMatchingMethods.isEmpty) {
        throw EvalException(
          s"Method `${methodGroup.qualifiedName}` cannot be applied to argument types: $argTypesString",
          token = Some(token)
        )
      }

      if (reducedMatchingMethods.size > 1) {
        throw EvalException(
          s"Method `${methodGroup.qualifiedName}` is ambiguous when applied to argument types: $argTypesString",
          token = Some(token)
        )
      }

      val resolution = MethodResolution(reducedMatchingMethods.head)

      if (args.length <= 4) {
        shortMethodResolutions.put(classcode, resolution)
      } else {
        longMethodResolutions.put(classVec, resolution)
      }

      resolution

    }

  }

  case class MethodResolution(method: CgscriptClass#Method) {

    val parameters = method.parameters

    val expandedLastParameter = parameters.nonEmpty && parameters.last.isExpandable && argNames.forall { _.isEmpty }

    val parameterToArgsMapping = new Array[Int](if (expandedLastParameter) parameters.length - 1 else parameters.length)
    java.util.Arrays.fill(parameterToArgsMapping, -1)
    argNames.zipWithIndex foreach {
      case (None, index) =>
        if (index < parameterToArgsMapping.length)
          parameterToArgsMapping(index) = index
      case (Some(idNode), index) =>
        val namedIndex = parameters indexWhere { _.id == idNode.id }
        assert(namedIndex >= 0)    // This was checked above
        parameterToArgsMapping(namedIndex) = index
    }

  }

  case class CallSiteResolution(parameters: Vector[Parameter], locationMessage: String) {

    val shortValidations = mutable.LongMap[Unit]()
    val longValidations = mutable.Map[Vector[Short], Unit]()

    // Last parameter is expandable only if there are no named args
    val expandedLastParameter = parameters.nonEmpty && parameters.last.isExpandable && argNames.forall { _.isEmpty }

    // Check for too many arguments
    if (!expandedLastParameter && argNames.length > parameters.length) {
      throw EvalException(
        s"Too many arguments ($locationMessage): ${argNames.length} (expecting at most ${parameters.length})",
        token = Some(token)
      )
    }

    val parameterToArgsMapping = new Array[Int](if (expandedLastParameter) parameters.length - 1 else parameters.length)
    java.util.Arrays.fill(parameterToArgsMapping, -1)
    argNames.zipWithIndex foreach {
      case (None, index) =>
        if (index < parameterToArgsMapping.length)
          parameterToArgsMapping(index) = index
      case (Some(idNode), index) =>
        val namedIndex = parameters indexWhere { _.id == idNode.id }
        if (namedIndex == -1)
          throw EvalException(
            s"Invalid parameter name ($locationMessage): `${idNode.id.name}`",
            token = Some(token)
          )
        if (parameterToArgsMapping(namedIndex) != -1)
          throw EvalException(
            s"Named parameter shadows an earlier ordinary argument ($locationMessage): `${idNode.id.name}`",
            token = Some(token)
          )
        parameterToArgsMapping(namedIndex) = index
    }

    // Check that all required args are present
    parameters zip parameterToArgsMapping foreach { case (param, index) =>
      if (param.defaultValue.isEmpty && index == -1)
        throw EvalException(
          s"Missing required parameter ($locationMessage): `${param.id.name}`",
          token = Some(token)
        )
    }

    // TODO We need unit tests for allowMutableArguments
    def validateArguments(args: Array[Any], allowMutableArguments: Boolean = true): Unit = {

      var classcode: Long = 0
      var classVec: Vector[Short] = Vector.empty
      if (parameters.size <= 4) {
        var i = 0
        while (i < args.length) {
          classcode |= (CgscriptClass of args(i)).classOrdinal
          classcode <<= 16
          i += 1
        }
        if (shortValidations.contains(classcode))
          return
      } else {
        classVec = args.toVector map { arg => (CgscriptClass of arg).classOrdinal.toShort }
        if (longValidations.contains(classVec))
          return
      }

      var i = 0
      assert(args.length == 0 || parameters.nonEmpty)
      while (i < args.length) {
        val cls = CgscriptClass of args(i)
        val expectedType = {
          if (i == args.length - 1 && parameters.last.isExpandable) {
            CgscriptClass.List   // TODO Validate elements of the list as well? Ideally we should do this only if explicitly specified
          } else {
            parameters(i).paramType
          }
        }
        if (!(cls.ancestors contains expectedType)) {
          throw EvalException(
            s"Argument `${parameters(i).id.name}` ($locationMessage) " +
              s"has type `${cls.qualifiedName}`, which does not match expected type `${parameters(i).paramType.qualifiedName}`",
            token = Some(token)
          )
        }
        if (!allowMutableArguments && cls.isMutable) {
          throw EvalException(
            s"Cannot assign mutable object to var `${parameters(i).id.name}` of immutable class",
            token = Some(token)
          )
        }
        i += 1
      }
      if (parameters.size <= 4) {
        shortValidations put (classcode, ())
      } else {
        longValidations put (classVec, ())
      }
    }

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val argStr = argNodes map { _.toNodeString } mkString ", "
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr)"
    else
      s"(${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr))"
  }

}

object VarNode {
  def apply(tree: Tree): AssignToNode = {
    assert(tree.getType == VAR && tree.children.size == 1)
    val t = tree.children.head
    t.getType match {
      case IDENTIFIER => AssignToNode(t, IdentifierNode(t), ConstantNode(null, null), AssignmentDeclType.VarDecl)
      case ASSIGN => AssignToNode(t, IdentifierNode(t.head), EvalNode(t.children(1)), AssignmentDeclType.VarDecl)
    }
  }
}

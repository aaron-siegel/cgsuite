package org.cgsuite.lang.node

import org.antlr.runtime.tree.Tree
import org.cgsuite.exception.EvalException
import org.cgsuite.lang.parser.CgsuiteLexer.BIGRARROW
import org.cgsuite.lang.parser.RichTree.treeToRichTree
import org.cgsuite.lang._

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

  def lookupMethodWithImplicits(objectType: CgscriptType, methodId: Symbol, argTypes: Vector[CgscriptType]): Option[CgscriptClass#MethodProjection] = {

    objectType.baseClass.lookupInstanceMethod(methodId, argTypes, Map.empty, Some(objectType)) orElse {

      // Try various types of implicit conversions. This is a bit of a hack to handle
      // Rational -> DyadicRational -> Integer conversions in a few places. In later versions, this might be
      // replaced by a more elegant / general solution.

      argTypes.length match {

        case 0 =>
          val implicits = availableImplicits(objectType)
          val validImplicits = implicits find { implObjectType =>
            implObjectType.baseClass.lookupInstanceMethod(methodId, Vector.empty, Map.empty, Some(objectType)).isDefined
          }
          validImplicits flatMap { implObjectType =>
            implObjectType.baseClass.lookupInstanceMethod(methodId, Vector.empty, Map.empty, Some(objectType))
          }

        case 1 =>
          val implicits = {
            for {
              implObjectType <- availableImplicits(objectType)
              implArgType <- availableImplicits(argTypes.head)
            } yield {
              (implObjectType, implArgType)
            }
          }
          val validImplicits = implicits find { case (implObjectType, implArgType) =>
            implObjectType.baseClass.lookupInstanceMethod(methodId, Vector(implArgType), Map.empty, Some(objectType)).isDefined
          }
          validImplicits flatMap { case (implObjectType, implArgType) =>
            implObjectType.baseClass.lookupInstanceMethod(methodId, Vector(implArgType), Map.empty, Some(objectType))
          }

        case _ =>
          None

      }

    }

  }

  def availableImplicits(typ: CgscriptType): Vector[CgscriptType] = {

    typ match {
      case ConcreteType(CgscriptClass.CanonicalShortGame, _, _) => Vector(typ, CgscriptType(CgscriptClass.Uptimal))
      case ConcreteType(CgscriptClass.Rational, _, _) => Vector(typ, CgscriptType(CgscriptClass.DyadicRational), CgscriptType(CgscriptClass.Integer))
      case ConcreteType(CgscriptClass.DyadicRational, _, _) => Vector(typ, CgscriptType(CgscriptClass.Integer))
      case _ => Vector(typ)
    }

  }

}

object InfixOpNode {
  def apply(tree: Tree): FunctionCallNode = {
    val callSiteNode = DotNode(tree, EvalNode(tree.children.head), IdentifierNode(tree))
    FunctionCallNode(tree, callSiteNode, Vector(EvalNode(tree.children(1))), Vector(None))
  }
}

case class FunctionCallNode(
  tree: Tree,
  callSiteNode: EvalNode,
  argNodes: Vector[EvalNode],
  argNames: Vector[Option[IdentifierNode]]
  ) extends EvalNode {

  override val children = argNodes ++ argNames.flatten :+ callSiteNode

  var objectType: Option[CgscriptType] = _
  var elaboratedMethod: Option[CgscriptClass#MethodProjection] = _
  var isElaboratedInLocalScope: Boolean = _
  var isEval: Boolean = false
  var isExpandableArgumentPattern: Boolean = false

  override def elaborateImpl(domain: ElaborationDomain) = {

    val elaboratedMethodGroup = callSiteNode match {

      case idNode: IdentifierNode =>

        idNode.resolveAsLocalMember(domain.cls) match {
          case Some(methodGroup: CgscriptClass#MethodGroup) if !methodGroup.isPureAutoinvoke =>
            isElaboratedInLocalScope = true
            objectType = None
            Some(methodGroup)
          case Some(_) => None
          case None => idNode.resolveAsPackageMember(domain.cls) match {
            case Some(methodGroup: CgscriptClass#MethodGroup) if !methodGroup.isPureAutoinvoke =>
              isElaboratedInLocalScope = false
              objectType = None
              Some(methodGroup)
            case _ => None
          }
        }

      case dotNode: DotNode =>

        dotNode.doResolutionForElaboration(domain) match {
          case methodGroup: CgscriptClass#MethodGroup if !methodGroup.isPureAutoinvoke =>
            isElaboratedInLocalScope = false
            objectType = {
              if (dotNode.isElaboratedAsPackage)
                None
              else
                Some(dotNode.antecedent.ensureElaborated(domain))
            }
            Some(methodGroup)
          case _ => None
        }

      case _ => None

    }

    // Syntactic validation of arguments: check that named parameters appear
    // strictly after ordinary arguments
    val lastOrdinaryArgIndex = argNames lastIndexWhere { _.isEmpty }
    argNames take (lastOrdinaryArgIndex + 1) foreach {
      case None =>
      case Some(argNameNode) => throw EvalException(
        s"Named parameter `${argNameNode.id.name}` appears in earlier position than an ordinary argument",
        token = Some(argNameNode.token)
      )
    }

    // Syntactic validation of arguments: check for duplicate named parameter
    argNames.flatten groupBy { _.id } foreach { case (id, argNameNodes) =>
      if (argNameNodes.size > 1) {
        throw EvalException(
          s"Duplicate parameter name: `${id.name}`",
          token = Some(argNameNodes(1).token)
        )
      }
    }

    val argTypes = argNodes map {
      // If it's a function with an unspecified parameter, then we don't elaborate yet --
      // we'll try to do type-inference, then come back and elaborate later
      case procedureNode: ProcedureNode if procedureNode.hasUnspecifiedTypes =>
        ConcreteType(CgscriptClass.Procedure, Vector.fill(procedureNode.parametersNode.parameterNodes.size + 1)(UnspecifiedType))
      case node => node.ensureElaborated(domain)
    }

    val ordinaryArgumentTypes = argNodes.indices.toVector collect {
      case n if argNames(n).isEmpty => argTypes(n)
    }

    val namedArgumentTypes = {
      argNodes.indices collect {
        case n if argNames(n).nonEmpty => argNames(n).get.id -> argTypes(n)
      }
    }.toMap

    val elaboratedType = elaboratedMethodGroup match {

      case Some(methodGroup) =>
        // First try elaborating without implicits.
        val methodOpt = methodGroup.lookupMethod(ordinaryArgumentTypes, namedArgumentTypes, objectType)
        methodOpt match {

          case Some(method) =>

            // We were able to elaborate without implicits.
            // We might have generics or unbound type parameters and hence need to do a type substitution.
            elaboratedMethod = Some(method)
            val methodType = method.ensureElaborated()
            val substitutedType = objectType match {
              case Some(typ) => methodType.substituteAll(typ.baseClass.typeParameters zip typ.typeArguments)
              case None => methodType
            }
            // Substitute for unbound type parameters. That is, figure out which parameter types have
            // (necessarily unbound) type variables, and map them on to argument types. For example, if
            // there is a parameter type (List of `T) that maps on to an argument type (List of Game),
            // then it witnesses the substitution `T -> Game, and that substitution will be applied
            // to the return type.
            // (Example: consider MyMethod(x as List of `T) as `T
            //           if myVar has type (List of Game), then MyMethod(myVar) will elaborate to (Game)
            // TODO This won't work for named parameters
            substitutedType.substituteForUnboundTypeParameters(method.signatureProjection.types, argTypes)

          case None =>

            // Try elaborating with implicits. This will throw an exception if no elaboration is possible.
            val method = methodGroup.resolveToMethod(ordinaryArgumentTypes, namedArgumentTypes, objectType, withImplicits = true)
            elaboratedMethod = Some(method)
            method.ensureElaborated()
            // Note we DON'T allow implicits on generics (currently); hence we don't do type substitution here.
            // TODO This may fail in some edge cases where the same method has both generic parameters and non-generic
            // implicits; more attention is needed here eventually.

        }

      case None =>
        // TODO Named parameter validation for constructor args
        val callSiteType = callSiteNode.ensureElaborated(domain)
        if (callSiteType.baseClass == CgscriptClass.Class) {
          val cls = callSiteType.typeArguments.head.baseClass
          if (cls.isScript) {
            elaboratedMethod = None
            cls.scriptBody.ensureElaborated()
          } else {
            cls.constructor match {
              case Some(constructor) =>
                elaboratedMethod = None     // TODO
                constructor.ensureElaborated()
              case None =>
                throw EvalException(s"Class cannot be directly instantiated: ${callSiteType.typeArguments.head.baseClass.qualifiedName}")
            }
          }
        } else if (callSiteType.baseClass == CgscriptClass.Procedure) {
          // TODO Validate args? Type-infer the procedure?
          isExpandableArgumentPattern = true
          elaboratedMethod = None
          callSiteType.typeArguments.last
        } else {
          // Eval method
          isEval = true
          val evalMethod = callSiteType.baseClass.resolveInstanceMethod('Eval, ordinaryArgumentTypes, namedArgumentTypes, Some(callSiteType))
          elaboratedMethod = None     // TODO
          evalMethod.ensureElaborated()
        }

    }

    // Ok, now go back and elaborate any arguments that are functions with unspecified type parameters.
    elaboratedMethod foreach { method =>
      argNodes.zipWithIndex foreach { case (argNode, n) =>
        argNode match {
          case procedureNode: ProcedureNode if procedureNode.hasUnspecifiedTypes =>
            val inferredType = {
              argNames(n) match {
                case None => method.signatureProjection.types(n)
                case Some(name) => method.signatureProjection.types(method.method.parameters.indexWhere { _.name == name.id.name })
              }
            }
            val substitutedType = objectType match {
              case Some(typ) => inferredType.substituteAll(typ.baseClass.typeParameters zip typ.typeArguments)
              case None => inferredType
            }
            procedureNode.ensureElaborated(domain, Some(substitutedType))
          case _ =>
        }
      }
    }

    elaboratedType

  }

  override def collectMentionedClasses(classes: mutable.HashSet[CgscriptClass]): Unit = {

    addTypeToClasses(classes, elaboratedType)
    // Skip over dotNode or idNode  TODO We might not always want to do this
    val childNode = {
      if (elaboratedMethod.isEmpty)
        Some(callSiteNode)
      else {
        callSiteNode match {
          case _: IdentifierNode => None
          case dotNode: DotNode if dotNode.isElaboratedAsPackage => None
          case dotNode: DotNode => Some(dotNode.antecedent)
          case _ => Some(callSiteNode)
        }
      }
    }
    (argNodes ++ childNode) foreach { _.collectMentionedClasses(classes) }

    // There may be an implicit mention of a constants class, so we need to handle
    // that possibility separately.
    elaboratedMethod match {
      case Some(method) if method.declaringClass != null =>
        classes += method.declaringClass
      case _ =>
    }

  }

  override def emitScalaCode(context: CompileContext, emitter: Emitter): Unit = {

    emitter print "("

    elaboratedMethod match {

      case Some(method) if isElaboratedInLocalScope && method.isExternal && method.methodName != "EnclosingObject" =>
        emitter print s"_instance.${method.scalaName}"

      case Some(method) if isElaboratedInLocalScope =>
        emitter print method.scalaName

      case Some(method) if objectType.isEmpty =>
        emitter print (method.declaringClass.scalaClassdefName + "." + method.scalaName)

      case Some(method) =>
        callSiteNode.asInstanceOf[DotNode].antecedent.emitScalaCode(context, emitter)
        emitter print ("." + method.scalaName)

      case _ if isEval =>
        callSiteNode.emitScalaCode(context, emitter)
        emitter print ".eval"

      case _ =>
        callSiteNode.emitScalaCode(context, emitter)

    }

    emitter print "("

    def emitArgs(): Unit = {
      val namedNodes = argNodes zip argNames
      for (i <- namedNodes.indices) {
        namedNodes(i) match {
          case (node, None) => node.emitScalaCode(context, emitter)
          case (node, Some(nameNode)) =>
            emitter print (nameNode.id.name + " = { ")
            node.emitScalaCode(context, emitter)
            emitter print " }"
        }
        if (i < namedNodes.length - 1)
          emitter print ", "
      }
    }

    if (isExpandableArgumentPattern) {
      argNodes.length match {
        case 0 => emitter print "null" // Placeholder for nullary procedures
        case 1 => emitArgs()
        case _ => // Tupleize the inputs
          emitter print "("
          emitArgs()
          emitter print ")"
      }
    } else {
      emitArgs()
    }

    emitter print "))"

  }

  def toNodeStringPrec(enclosingPrecedence: Int) = {
    val argStr = argNodes map { _.toNodeString } mkString ", "
    if (OperatorPrecedence.Postfix <= enclosingPrecedence)
      s"${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr)"
    else
      s"(${callSiteNode.toNodeStringPrec(OperatorPrecedence.Postfix)}($argStr))"
  }

}

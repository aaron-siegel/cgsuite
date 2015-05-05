package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import scala.collection.mutable
import org.cgsuite.util.Profiler

class Domain(
  namespace: Namespace,
  contextObject: Option[Any] = None,
  contextMethod: Option[CgsuiteClass#Method] = None
  ) {

  def statementSequence(node: StatementSequenceNode): Any = {
    node.statements.foldLeft[Any](Nil) { (retval, n) => expression(n) }
  }

  def expression(node: Node): Any = {

    node match {

      case node@StatementSequenceNode(_, _) => statementSequence(node)
      case ConstantNode(_, const) => const
      case UnOpNode(tree, op, operand) => op(expression(operand))
      case BinOpNode(tree, op, operand1, operand2) =>
        val lhs = expression(operand1)
        val rhs = expression(operand2)
        Profiler.start('BinOp, tree.toString)
        val result = op(lhs, rhs)
        Profiler.stop('BinOp, tree.toString)
        result
      case NewBinOpNode(tree, op, operand1, operand2) =>
        val lhs = expression(operand1)
        val rhs = expression(operand2)
        Profiler.start('NewBinOp, tree.toString)
        val result = op(lhs, rhs)
        Profiler.stop('NewBinOp, tree.toString)
        result
      case MultiOpNode(tree, op, operands) => op(operands.map(expression))
      case MapPairNode(tree, from, to) => expression(from) -> expression(to)
      case IdentifierNode(tree, id) => lookup(id, tree.getToken)    // TODO Use symbol
      case GameSpecNode(tree, lo, ro, forceExplicit) => gameSpec(lo.flatMap(gameExpression), ro.flatMap(gameExpression), forceExplicit)
      case IfNode(tree, condition, ifNode, elseNode) =>
        if (boolean(condition))
          statementSequence(ifNode)
        else
          elseNode.map(expression).getOrElse(Nil)
      case node: LoopNode => loop(node)
      case ErrorNode(tree, msg) => throw InputException(toStringOutput(expression(msg)))
      case DotNode(tree, obj, IdentifierNode(_, id)) =>
        val x = expression(obj)
        resolve(x, id) getOrElse {
          throw InputException(s"Member not found: ${id.name} (in object of type ${CgsuiteClass.of(x)})")
        }
      case node@FunctionCallNode(tree) =>
        val callSite = expression(node.callSite)
        val args = node.args.map(expression)
        val optArgs = node.optArgs.map { case (IdentifierNode(_, id), value) => (id, expression(value)) }
        callSite match {
          case site: CallSite => site.call(args, optArgs)
          case _ => throw InputException("That is not a method or procedure.", token = Some(tree.token))
        }
      case AssignToNode(tree, IdentifierNode(_, id), value, isVarDeclaration) =>
        assignTo(id, expression(value), isVarDeclaration || contextObject.isEmpty, tree.token)

    }

  }

  def boolean(condition: Node): Boolean = {
    expression(condition) match {
      case (x: Boolean) => x
      case _ => sys.error("not a bool")
    }
  }

  def iterator(node: Node): Iterator[_] = {
    val result = expression(node)
    result match {
      case (x: Iterable[_]) => x.iterator
      case _ => sys.error(s"not a collection: $result")
    }
  }

  def loop(node: LoopNode): Any = {

    Profiler.start('PrepareLoop, node.tree.location())

    val forId = node.forId.map { _.id }
    var counter = node.from.map(expression).getOrElse(null)
    val toVal = node.to.map(expression)
    val byVal = node.by.map(expression).getOrElse(one)
    val iterator = node.in.map(this.iterator)

    Profiler.stop('PrepareLoop, node.tree.location())

    Profiler.start('Loop, node.tree.location())

    val result = {
      if (node.isYield) {
        mutable.MutableList[Any]()
      } else {
        null
      }
    }

    var continue = true
    do {

      iterator match {
        case Some(it) => {
          if (it.hasNext)
            namespace.put(forId.get, it.next(), declare = true)
          else
            continue = false
        }
        case None =>
          forId.foreach { namespace.put(_, counter, declare = true) }
          continue = toVal.forall { Ops.Leq(counter, _) }
      }

      if (continue) {
        continue = node.`while`.forall(boolean)
      }

      if (continue && node.where.forall(boolean)) {
        val r = expression(node.body)
        if (node.isYield) {
          r match {
            case it: Iterable[_] => result ++= it
            case x => result += x
          }
        }
        if (counter != null)
          counter = Ops.NewPlus(counter, byVal)
      }

    } while (continue)

    Profiler.stop('Loop, node.tree.location())

    if (node.isYield) {
      result.toSeq
    } else {
      Nil
    }

  }

  def lookup(id: Symbol, refToken: Token): Any = {

    val opt = try {
      lookup(id)
    } catch {
      case exc: InputException =>
        exc.addToken(refToken)
        throw exc
    }
    opt match {
      case Some(x) => x
      case None => throw InputException("That variable is not defined: " + id.name, token = Option(refToken))
    }

  }

  def lookup(id: Symbol): Option[Any] = {
    CgsuitePackage.lookupClass(id).map { _.classObject }
      .orElse(namespace.lookup(id))
      .orElse(contextObject flatMap { resolve(_, id) })   // TODO Use contextMethod
  }

  def gameExpression(node: Node) = {
    expression(node) match {
      case g: Game => Iterable(g)
      case sublist: Iterable[_] =>
        sublist map { x =>
          x match {
            case g: Game => g
            case _ => sys.error("must be a list of games")
          }
        }
      case _ => sys.error("must be a list of games")
    }
  }

  def gameSpec(lo: Seq[Game], ro: Seq[Game], forceExplicit: Boolean) = {

    if (!forceExplicit && (lo ++ ro).forall { _.isInstanceOf[CanonicalShortGame] }) {
      CanonicalShortGame(lo map { _.asInstanceOf[CanonicalShortGame] }, ro map { _.asInstanceOf[CanonicalShortGame] })
    } else {
      ExplicitGame(lo, ro)
    }

  }

  def resolve(x: Any, id: Symbol): Option[Any] = {

    /*
    // Shortcut resolver
    MethodRegistry.lookup(id, x) match {
      case Some(y) => y
      case None => sys.error("TODO: dynamic resolve")
    }
    */

    x match {
      case so: StandardObject => so.lookup(id)
      case _ =>
        CgsuiteClass.of(x).lookupMethod(id).map { method =>
          if (method.autoinvoke)
            method.call(x, Seq.empty, Map.empty)
          else
            InstanceMethod(x, method)
        }
    }

  }

  def assignTo(id: Symbol, x: Any, isVarDeclaration: Boolean, refToken: Token): Any = {

    try {
      // TODO Check for duplicate declaration
      namespace.put(id, x, declare = isVarDeclaration)
      x
    } catch {
      case exc: InputException =>
        exc.addToken(refToken)
        throw exc
    }

  }

  def toStringOutput(x: Any) = x.toString

}

package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.exception.InputException
import org.cgsuite.lang.parser.CgsuiteLexer._
import org.cgsuite.lang.parser.{MalformedParseTreeException, CgsuiteTree}
import scala.collection.JavaConversions._
import scala.collection.mutable
import org.cgsuite.util.{Grid, Coordinates}

class Domain(
  args: Map[String, Any] = Map.empty,
  contextObject: Option[Any] = None,
  contextMethod: Option[CgsuiteClass#Method] = None
  ) {

  val namespace = mutable.Map[String, Any]()

  def statementSequence(tree: CgsuiteTree): Any = statementSequence(StatementSequenceNode(tree))

  def statementSequence(node: StatementSequenceNode): Any = {

    node.statements.foldLeft[Any](Nil) { (retval, n) => expression(n) }

  }

  def expression(tree: CgsuiteTree): Any = expression(Node(tree))

  def expression(node: Node): Any = {

    node match {

      case node@StatementSequenceNode(_, _) => statementSequence(node)
      case ConstantNode(_, const) => const
      case UnOpNode(tree, op, operand) => op(expression(operand))
      case BinOpNode(tree, op, operand1, operand2) => op(expression(operand1), expression(operand2))
      case MultiOpNode(tree, op, operands) => op(operands.map(expression))
      case MapPairNode(tree, from, to) => expression(from) -> expression(to)
      case IdentifierNode(tree) => lookup(tree.getToken)    // TODO Use symbol
      case GameSpecNode(tree, lo, ro, forceExplicit) => gameSpec(lo.flatMap(gameExpression), ro.flatMap(gameExpression), forceExplicit)
      case IfNode(tree, condition, ifNode, elseNode) =>
        if (boolean(condition))
          statementSequence(ifNode)
        else
          elseNode.map(statementSequence).getOrElse(Nil)
      case node: LoopNode => loop(node)
      case ErrorNode(tree, msg) => throw InputException(toStringOutput(expression(msg)))
      case DotNode(tree, obj, id) =>
        val x = expression(obj)
        resolve(x, id.symbol) getOrElse {
          throw InputException(s"Member not found: ${id.symbol.name} (in object of type ${CgsuiteClass.of(x)})")
        }
      case node@FunctionCallNode(tree) =>
        val callSite = expression(node.callSite)
        val args = node.args.map(expression)
        val optArgs = node.optArgs.map { case (name, value) => (name.symbol.name, expression(value)) }
        callSite match {
          case site: CallSite => site.call(args, optArgs)
          case _ => throw InputException("That is not a method or procedure.", token = Some(tree.token))
        }
      case AssignToNode(tree, id, value) => assignTo(id.symbol, expression(value), tree.token)

    }

  }

  def boolean(condition: Node): Boolean = {
    expression(condition) match {
      case (x: Boolean) => x
      case _ => sys.error("not a bool")
    }
  }

  def iterator(node: Node): Iterator[_] = {
    expression(node) match {
      case (x: Iterable[_]) => x.iterator
      case _ => sys.error("not a collection")
    }
  }

  def loop(node: LoopNode): Any = {

    val forId = node.forId.map { _.symbol }
    var counter = node.from.map(expression).getOrElse(null)
    val toVal = node.to.map(expression)
    val byVal = node.by.map(expression).getOrElse(one)
    val iterator = node.in.map(this.iterator)
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
            put(forId.get, it.next())
          else
            continue = false
        }
        case None =>
          forId.foreach { put(_, counter) }
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
          counter = Ops.Plus(counter, byVal)
      }

    } while (continue)

    if (node.isYield) {
      result.toSeq
    } else {
      Nil
    }

  }

  def lookup(token: Token): Any = {

    val opt = try {
      lookup(token.getText)
    } catch {
      case exc: InputException =>
        exc.addToken(token)
        throw exc
    }
    opt match {
      case Some(x) => x
      case None => throw InputException("That variable is not defined: " + token.getText, token = Option(token))
    }

  }

  def lookup(id: String): Option[Any] = {
    CgsuitePackage.lookupClass(id).map { _.classObject }
      .orElse(args.get(id))
      .orElse(namespace.get(id))
      .orElse(contextObject flatMap { resolve(_, Symbol(id)) })
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
      case so: StandardObject => so.lookup(id.name)
      case _ =>
        CgsuiteClass.of(x).lookupMethod(id.name).map { method =>
          if (method.autoinvoke)
            method.call(x, Seq.empty, Map.empty)
          else
            InstanceMethod(x, method)
        }
    }

  }

  def assignTo(id: Symbol, x: Any, refToken: Token): Any = {

    try {
      put(id, x)
    } catch {
      case exc: InputException =>
        exc.addToken(refToken)
        throw exc
    }

  }

  def put(id: Symbol, x: Any): Any = {
    namespace.put(id.name, x)
    x
  }

  def toStringOutput(x: Any) = x.toString

}

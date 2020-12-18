package org.cgsuite.lang

import org.antlr.runtime.Token
import org.cgsuite.lang.node.EvalNode

object Ops {

  // Unary

  val Pos = PrefixUnOp("unary+", OperatorPrecedence.Neg, "+", Some { "+" + _ })
  val Neg = PrefixUnOp("unary-", OperatorPrecedence.Neg, "-", Some { "-" + _ })
  val PlusMinus = MethodUnOp("unary+-", OperatorPrecedence.Neg, "switch", Some { "+-" + _ })
  val Not = PrefixUnOp("not", OperatorPrecedence.Not, "!")
  val MakeNimber = MethodUnOp("unary*", OperatorPrecedence.Nim, "toNimber", Some { "*" + _ })
  val MakeUpMultiple = MethodUnOp("unary^", OperatorPrecedence.Nim, "toUp", Some { "^" + _ })
  val MakeDownMultiple = MethodUnOp("unaryv", OperatorPrecedence.Nim, "toDown", Some { "v" + _ })

  // Binary

  val Plus = InfixBinOp("+", OperatorPrecedence.Plus, "+")
  val Minus = InfixBinOp("-", OperatorPrecedence.Plus, "-")
  val OrdinalPlus = MethodBinOp(":", OperatorPrecedence.OrdinalSum, "ordinalSum")
  val Times = InfixBinOp("*", OperatorPrecedence.Mult, "*")
  val Div = InfixBinOp("/", OperatorPrecedence.Mult, "/")
  val Mod = InfixBinOp("%", OperatorPrecedence.Mult, "%")
  val Exp = MethodBinOp("^", OperatorPrecedence.Exp, "exp")
  val Equals = InfixBinOp("==", OperatorPrecedence.Relational, "==")
  val Neq = InfixBinOp("!=", OperatorPrecedence.Relational, "!=")
  val Leq       = BinOp("<=",  OperatorPrecedence.Relational) { (a, b) => s"$a <= $b" }
  val Geq       = BinOp(">=",  OperatorPrecedence.Relational) { (a, b) => s"$b <= $a" }
  val Lt        = BinOp("<",   OperatorPrecedence.Relational) { (a, b) => s"($a <= $b && !($b <= $a))"}
  val Gt        = BinOp("<",   OperatorPrecedence.Relational) { (a, b) => s"(!($a <= $b) && $b <= $a)"}
  val Confused  = BinOp("<>",  OperatorPrecedence.Relational) { (a, b) => s"(!($a <= $b) && !($b <= $a))"}
  val LConfused = BinOp("<|",  OperatorPrecedence.Relational) { (a, b) => s"(!($b <= $a))" }
  val GConfused = BinOp("|>",  OperatorPrecedence.Relational) { (a, b) => s"(!($a <= $b))" }
  val RefEquals = BinOp("===", OperatorPrecedence.Relational) { (a, b) => s"(($a <= $b) && ($b <= $a))" }
  val RefNeq    = BinOp("!==", OperatorPrecedence.Relational) { (a, b) => s"(!($a <= $b) || !($b <= $a))" }
  val Compare   = InfixBinOp("<=>", OperatorPrecedence.Relational, "???")
  val And = InfixBinOp("and", OperatorPrecedence.And, "&&")
  val Or = InfixBinOp("or", OperatorPrecedence.Or, "||")
  val Is = InfixBinOp("is", OperatorPrecedence.Is, "???")
  val MakeSides = InfixBinOp("&", OperatorPrecedence.Sidle, "&")
  val MakeCoordinates = BinOp("(,)", OperatorPrecedence.Primary, Some { (a, b) => s"($a, $b)" }) { (a, b) =>
    s"org.cgsuite.util.Coordinates($a, $b)"
  }
  val Range = InfixBinOp("..", OperatorPrecedence.Range, "???")
  val ArrayReference = BinOp("[]", OperatorPrecedence.Postfix, Some { (a, b) => s"$a[$b]" }) { (a, b) => s"$a($b)" }

}

object OperatorPrecedence {

  val Primary, Nim, Postfix, Exp, Neg, Mult, OrdinalSum, Sidle, Plus, Range, Infix, Relational,
  Is, Not, And, Or, As, FunctionDef, Assign, StatementSeq = next

  private var prec = 0

  private def next = {
    prec += 1
    prec
  }

}

trait UnOp {

  def name: String

  def precedence: Int

  def toOpStringOpt: Option[String => String]

  def emitScalaCode(context: CompileContext, emitter: Emitter, operand: EvalNode)

  val id = Symbol(name)

  val toOpString: String => String = { op =>
    toOpStringOpt match {
      case Some(fn) => fn(op)
      case None => s"$name $op"
    }
  }

}

case class PrefixUnOp(name: String, precedence: Int, scalaOp: String, toOpStringOpt: Option[String => String] = None) extends UnOp {

  override def emitScalaCode(context: CompileContext, emitter: Emitter, operand: EvalNode): Unit = {
    emitter print "("
    emitter print scalaOp
    operand.emitScalaCode(context, emitter)
    emitter print ")"
  }

}

case class MethodUnOp(name: String, precedence: Int, scalaMethod: String, toOpStringOpt: Option[String => String] = None) extends UnOp {

  override def emitScalaCode(context: CompileContext, emitter: Emitter, operand: EvalNode): Unit = {
    emitter print "("
    operand.emitScalaCode(context, emitter)
    emitter print "."
    emitter print scalaMethod
    emitter print ")"
  }

}

case class BinOp(name: String, precedence: Int, toOpStringOpt: Option[(String, String) => String] = None)(scalaCode: (String, String) => String) {

  val id = Symbol(name)

  val isRelational = {
    name match {
      case "<=" | ">=" | "<" | ">" | "<|" | "|>" | "<>" | "===" | "!==" => true
      case _ => false
    }
  }

  val baseId = if (isRelational) Symbol("<=") else id

  val toOpString: (String, String) => String = { (op1, op2) =>
    toOpStringOpt match {
      case Some(fn) => fn(op1, op2)
      case None => s"$op1 $name $op2"
    }
  }

  def emitScalaCode(context: CompileContext, emitter: Emitter, operand1: EvalNode, operand2: EvalNode, opToken: Token): Unit = {

    val tmp1 = context.newTempId()
    val tmp2 = context.newTempId()
    emitter print s"{ val $tmp1 = ("
    operand1.emitScalaCode(context, emitter)
    emitter print s"); val $tmp2 = ("
    operand2.emitScalaCode(context, emitter)
    emitter print "); "
    if (context.generateStackTraceInfo)
      emitter.printTry()
    emitter print scalaCode(tmp1, tmp2)
    if (context.generateStackTraceInfo)
      emitter.printCatch(opToken)
    emitter print " }"

  }

}

object InfixBinOp {

  def apply(name: String, precedence: Int, scalaOp: String, toOpStringOpt: Option[(String, String) => String] = None): BinOp = {
    BinOp(name, precedence, toOpStringOpt) { (a, b) => s"$a $scalaOp $b" }
  }

}

object MethodBinOp {

  def apply(name: String, precedence: Int, scalaMethod: String, toOpStringOpt: Option[(String, String) => String] = None): BinOp = {
    BinOp(name, precedence, toOpStringOpt) { (a, b) => s"$a.$scalaMethod($b)" }
  }

}

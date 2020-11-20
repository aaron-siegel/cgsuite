package org.cgsuite.lang

import org.cgsuite.lang.node.EvalNode
import org.cgsuite.output.{Output, StyledTextOutput}

object Ops {

  val Pos = PrefixUnOp("unary+", OperatorPrecedence.Neg, "+", Some { "+" + _ })

  val Neg = PrefixUnOp("unary-", OperatorPrecedence.Neg, "-", Some { "-" + _ })

  val PlusMinus = MethodUnOp("unary+-", OperatorPrecedence.Neg, "switch", Some { "+-" + _ })

  val Plus = InfixBinOp("+", OperatorPrecedence.Plus, "+")

  def toOutput(str: String) = new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, str)

  def outputSum(o1: Output, o2: Output) = {
    val result = new StyledTextOutput()
    result.appendOutput(o1)
    result.appendOutput(o2)
    result
  }

  val Minus = InfixBinOp("-", OperatorPrecedence.Plus, "-")

  val OrdinalPlus = MethodBinOp(":", OperatorPrecedence.OrdinalSum, "ordinalSum")

  val Times = InfixBinOp("*", OperatorPrecedence.Mult, "*")

  val Div = InfixBinOp("/", OperatorPrecedence.Mult, "/")

  val Mod = InfixBinOp("%", OperatorPrecedence.Mult, "%")

  val Exp = MethodBinOp("^", OperatorPrecedence.Exp, "exp")

  val Equals = InfixBinOp("==", OperatorPrecedence.Relational, "==")

  val Neq = InfixBinOp("!=", OperatorPrecedence.Relational, "!=")

  val Leq = InfixBinOp("<=", OperatorPrecedence.Relational, "<=")
  val Geq = CustomBinOp(">=", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    Leq.emitScalaCode(context, emitter, operand2, operand1)
  }
  val Lt = CustomBinOp("<", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(("
    Leq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print ") && !("
    Geq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val Gt = CustomBinOp(">", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(!("
    Leq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print ") && ("
    Geq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val Confused = CustomBinOp("<>", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(!("
    Leq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print ") && !("
    Geq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val LConfused = CustomBinOp("<|", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(!("
    Geq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val GConfused = CustomBinOp("|>", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(!("
    Leq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val RefEquals = CustomBinOp("===", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(("
    Leq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print ") && ("
    Geq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val RefNeq = CustomBinOp("!==", OperatorPrecedence.Relational) { (context, emitter, operand1, operand2) =>
    emitter print "(!("
    Leq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print ") || !("
    Geq.emitScalaCode(context, emitter, operand1, operand2)
    emitter print "))"
  }
  val Compare = InfixBinOp("<=>", OperatorPrecedence.Relational, "???")

  val Not = PrefixUnOp("not", OperatorPrecedence.Not, "!")
  val And = InfixBinOp("and", OperatorPrecedence.And, "&&")
  val Or = InfixBinOp("or", OperatorPrecedence.Or, "||")

  val Is = InfixBinOp("is", OperatorPrecedence.Is, "???")

  val MakeNimber = MethodUnOp("unary*", OperatorPrecedence.Nim, "toNimber", Some { "*" + _ })

  val MakeUpMultiple = MethodUnOp("unary^", OperatorPrecedence.Nim, "toUp", Some { "^" + _ })

  val MakeDownMultiple = MethodUnOp("unaryv", OperatorPrecedence.Nim, "toDown", Some { "v" + _ })

  val MakeSides = InfixBinOp("&", OperatorPrecedence.Sidle, "&")

  val MakeCoordinates = CustomBinOp("(,)", OperatorPrecedence.Primary, Some { "(" + _ + ", " + _ + ")" }) { (context, emitter, operand1, operand2) =>
    emitter print "org.cgsuite.util.Coordinates("
    operand1.emitScalaCode(context, emitter)
    emitter print ", "
    operand2.emitScalaCode(context, emitter)
    emitter print ")"
  }

  val Range = InfixBinOp("..", OperatorPrecedence.Range, "???")

  val ArrayReference = CustomBinOp("[]", OperatorPrecedence.Postfix, Some { _ + "[" + _ + "]" }) { (context, emitter, operand1, operand2) =>
    operand1.emitScalaCode(context, emitter)
    emitter print "("
    operand2.emitScalaCode(context, emitter)
    emitter print ")"
  }

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

trait BinOp {

  def name: String

  def precedence: Int

  def toOpStringOpt: Option[(String, String) => String]

  def emitScalaCode(context: CompileContext, emitter: Emitter, operand1: EvalNode, operand2: EvalNode)

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

}

case class InfixBinOp(name: String, precedence: Int, scalaOp: String, toOpStringOpt: Option[(String, String) => String] = None) extends BinOp {

  override def emitScalaCode(context: CompileContext, emitter: Emitter, operand1: EvalNode, operand2: EvalNode): Unit = {
    emitter print "("
    operand1.emitScalaCode(context, emitter)
    emitter print s" $scalaOp "
    operand2.emitScalaCode(context, emitter)
    emitter print ")"
  }

}

case class MethodBinOp(name: String, precedence: Int, scalaMethod: String, toOpStringOpt: Option[(String, String) => String] = None) extends BinOp {

  override def emitScalaCode(context: CompileContext, emitter: Emitter, operand1: EvalNode, operand2: EvalNode): Unit = {
    operand1.emitScalaCode(context, emitter)
    emitter print "."
    emitter print scalaMethod
    emitter print "("
    operand2.emitScalaCode(context, emitter)
    emitter print ")"
  }

}

case class CustomBinOp(name: String, precedence: Int, toOpStringOpt: Option[(String, String) => String] = None)
                      (emit: (CompileContext, Emitter, EvalNode, EvalNode) => Unit) extends BinOp {

  override def emitScalaCode(context: CompileContext, emitter: Emitter, operand1: EvalNode, operand2: EvalNode): Unit = {
    emit(context, emitter, operand1, operand2)
  }

}

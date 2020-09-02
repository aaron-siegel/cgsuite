package org.cgsuite.lang2

import org.antlr.runtime.tree.Tree
import org.cgsuite.core.Values._
import org.cgsuite.core._
import org.cgsuite.core.misere.MisereCanonicalGame
import org.cgsuite.dsl.IntegerIsIntegral
import org.cgsuite.exception.EvalException
import org.cgsuite.output.{Output, StyledTextOutput}
import org.cgsuite.util.{Coordinates, Grid, Strip}

import scala.collection.immutable.NumericRange
import scala.collection.mutable

object Ops {

  type MultiOp = Iterable[Any] => Any

  val Pos = UnOp("unary+", OperatorPrecedence.Neg, Some { "+" + _ }) { x => s"+$x" }

  val Neg = UnOp("unary-", OperatorPrecedence.Neg, Some { "-" + _ }) { x => s"-$x" }

  val PlusMinus = UnOp("unary+-", OperatorPrecedence.Neg, Some { "+-" + _ }) { x => s"$x.switch" }

  val Plus = BinOp("+", OperatorPrecedence.Plus) { (x, y) => s"$x + $y" }

  def toOutput(str: String) = new StyledTextOutput(StyledTextOutput.Style.FACE_MATH, str)

  def outputSum(o1: Output, o2: Output) = {
    val result = new StyledTextOutput()
    result.appendOutput(o1)
    result.appendOutput(o2)
    result
  }

  val Minus = BinOp("-", OperatorPrecedence.Plus) { (x, y) => s"$x - $y" }

  val OrdinalPlus = BinOp(":", OperatorPrecedence.OrdinalSum) { (x, y) => s"$x.ordinalSum($y)" }

  val Times = BinOp("*", OperatorPrecedence.Mult) { (x, y) => s"$x * $y" }

  val Div = BinOp("/", OperatorPrecedence.Mult) { (x, y) => s"$x / $y" }

  val Mod = BinOp("%", OperatorPrecedence.Mult) { (x, y) => s"$x % $y" }

  val Exp = BinOp("^", OperatorPrecedence.Exp) { (x, y) => s"$x.exp($y)" }

  val Equals = BinOp("==", OperatorPrecedence.Relational) { (x, y) => s"$x == $y" }

  val Neq = BinOp("!=", OperatorPrecedence.Relational) { (x, y) => s"$x != $y" }

  val RefEquals = BinOp("===", OperatorPrecedence.Relational) { (x, y) => s"$x == $y" }
  val RefNeq = BinOp("!==", OperatorPrecedence.Relational) { (x, y) => s"$x != $y" }
  val Leq = BinOp("<=", OperatorPrecedence.Relational) { (x, y) => s"$x <= $y" }
  val Geq = BinOp(">=", OperatorPrecedence.Relational) { (x, y) => s"$y <= $x" }
  val Lt = BinOp("<", OperatorPrecedence.Relational) { _ + " ??? " + _ }
  val Gt = BinOp(">", OperatorPrecedence.Relational) { _ + " ??? " + _ }
  val Confused = BinOp("<>", OperatorPrecedence.Relational) { _ + " ??? " + _ }
  val LConfused = BinOp("<|", OperatorPrecedence.Relational) { _ + " ??? " + _ }
  val GConfused = BinOp("|>", OperatorPrecedence.Relational) { _ + " ??? " + _ }
  val Compare = BinOp("<=>", OperatorPrecedence.Relational) { _ + " ??? " + _ }

  val Not = UnOp("not", OperatorPrecedence.Not) { x => s"(!$x)" }
  val And = BinOp("and", OperatorPrecedence.And) { (x, y) => s"$x && $y" }
  val Or = BinOp("or", OperatorPrecedence.Or) { (x, y) => s"$x || $y" }

  val Is = BinOp("is", OperatorPrecedence.Is) { _ + " ??? " + _ }

  val MakeNimber = UnOp("unary*", OperatorPrecedence.Nim, Some { "*" + _ }) { x => s"($x.toNimber)" }

  val MakeUpMultiple = UnOp("unary^", OperatorPrecedence.Nim, Some { "^" + _ }) { x => s"($x.toUp)" }

  val MakeDownMultiple = UnOp("unaryv", OperatorPrecedence.Nim, Some { "v" + _ }) { x => s"($x.toDown)" }

  val MakeSides = BinOp("&", OperatorPrecedence.Sidle) { (x, y) => s"($x & $y)" }

  val MakeCoordinates = BinOp("(,)", OperatorPrecedence.Primary, Some { "(" + _ + ", " + _ + ")" }) { (x, y) => s"org.cgsuite.util.Coordinates($x, $y)" }

  val Range = BinOp("..", OperatorPrecedence.Range) { _ + " ??? " + _ }

  val ArrayReference = BinOp("[]", OperatorPrecedence.Postfix, Some { _ + "[" + _ + "]" }) { (x, y) => s"$x($y)" }

}

object OperatorPrecedence {

  val Primary, Nim, Postfix, Exp, Neg, Mult, OrdinalSum, Sidle, Plus, Range, Infix, Relational,
  Is, Not, And, Or, FunctionDef, Assign, StatementSeq = next

  private var prec = 0

  private def next = {
    prec += 1
    prec
  }

}

case class UnOp(name: String, precedence: Int, toOpStringOpt: Option[String => String] = None)
               (val toScalaCode: String => String) {

  val id = Symbol(name)

  val toOpString: String => String = { op =>
    toOpStringOpt match {
      case Some(fn) => fn(op)
      case None => s"$name $op"
    }
  }

}

case class BinOp(name: String, precedence: Int, toOpStringOpt: Option[(String, String) => String] = None)
                (val toScalaCode: (String, String) => String) {

  val id = Symbol(name)

  val toOpString: (String, String) => String = { (op1, op2) =>
    toOpStringOpt match {
      case Some(fn) => fn(op1, op2)
      case None => s"$op1 $name $op2"
    }
  }

}

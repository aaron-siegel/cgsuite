package org.cgsuite.lang2

import java.io.PrintWriter

import ch.qos.logback.classic.{Level, Logger}
import org.cgsuite.core._
import org.cgsuite.core.impartial.{HeapRuleset, Periodicity, Spawning, TakeAndBreak}
import org.cgsuite.core.misere.{Genus, MisereCanonicalGame}
import org.cgsuite.exception.EvalException
import org.cgsuite.lang2.Node.treeToRichTree
import org.cgsuite.lang2.parser.ParserUtil
import org.cgsuite.output.{EmptyOutput, GridOutput, Output, StyledTextOutput}
import org.cgsuite.util._
import org.slf4j.LoggerFactory

import scala.collection.immutable.NumericRange
import scala.collection.mutable
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.{IMain, IR}

private[lang2] object CgscriptSystem {

  val baseSystemClasses: Seq[(String, Class[_])] = Seq(

    "cgsuite.lang.Object" -> classOf[AnyRef],
    "cgsuite.lang.Enum" -> classOf[AnyRef]

  )

  val typedSystemClasses: Seq[(String, Class[_])] = Seq(

    "cgsuite.lang.Class" -> classOf[Class[_]],
    "cgsuite.lang.Script" -> classOf[Script],

    "cgsuite.util.MutableList" -> classOf[mutable.ArrayBuffer[_]],
    "cgsuite.util.MutableSet" -> classOf[mutable.HashSet[_]],
    "cgsuite.util.MutableMap" -> classOf[mutable.HashMap[_,_]],

    "cgsuite.lang.Boolean" -> classOf[java.lang.Boolean],
    "cgsuite.lang.String" -> classOf[String],
    "cgsuite.lang.Coordinates" -> classOf[Coordinates],
    "cgsuite.lang.Range" -> classOf[NumericRange[_]],
    "cgsuite.lang.List" -> classOf[IndexedSeq[_]],
    "cgsuite.lang.Set" -> classOf[scala.collection.Set[_]],
    "cgsuite.lang.Map" -> classOf[scala.collection.Map[_,_]],
    "cgsuite.lang.MapEntry" -> classOf[(_,_)],
    "cgsuite.lang.Procedure" -> classOf[_ => _],
    "cgsuite.lang.System" -> classOf[System],
    "cgsuite.lang.Table" -> classOf[Table],
    "cgsuite.lang.Collection" -> classOf[Iterable[_]],
    "cgsuite.lang.InstanceClass" -> classOf[InstanceClass],
    "cgsuite.lang.InstanceMethod" -> classOf[InstanceMethod],

    "cgsuite.util.Strip" -> classOf[Strip],
    "cgsuite.util.Genus" -> classOf[Genus],
    "cgsuite.util.Grid" -> classOf[Grid],
    "cgsuite.util.Symmetry" -> classOf[Symmetry],
    "cgsuite.util.Thermograph" -> classOf[Thermograph],
    "cgsuite.util.Trajectory" -> classOf[Trajectory],
    "cgsuite.util.UptimalExpansion" -> classOf[UptimalExpansion],

    "cgsuite.ui.Explorer" -> classOf[Explorer],

    // The order is extremely important in the following hierarchies (most specific first)

    "cgsuite.util.output.EmptyOutput" -> classOf[EmptyOutput],
    "cgsuite.util.output.GridOutput" -> classOf[GridOutput],
    "cgsuite.util.output.TextOutput" -> classOf[StyledTextOutput],
    "cgsuite.lang.Output" -> classOf[Output],

    "game.Zero" -> classOf[Zero],
    "game.Integer" -> classOf[Integer],
    "game.GeneralizedOrdinal" -> classOf[GeneralizedOrdinal],
    "game.DyadicRational" -> classOf[DyadicRationalNumber],
    "game.Rational" -> classOf[RationalNumber],
    "game.SurrealNumber" -> classOf[SurrealNumber],
    "game.Nimber" -> classOf[Nimber],
    "game.Uptimal" -> classOf[Uptimal],
    "game.CanonicalShortGame" -> classOf[CanonicalShortGame],
    "game.Pseudonumber" -> classOf[Pseudonumber],
    "game.CanonicalStopper" -> classOf[CanonicalStopper],
    "game.StopperSidedValue" -> classOf[StopperSidedValue],
    "game.SidedValue" -> classOf[SidedValue],
    "game.NormalValue" -> classOf[NormalValue],

    "game.misere.MisereCanonicalGame" -> classOf[MisereCanonicalGame],

    "game.CompoundImpartialGame" -> classOf[CompoundImpartialGame],
    "game.CompoundGame" -> classOf[CompoundGame],
    "game.ExplicitGame" -> classOf[ExplicitGame],
    "game.NegativeGame" -> classOf[NegativeGame],

    "game.ImpartialGame" -> classOf[ImpartialGame],
    "game.Game" -> classOf[Game],

    "game.Player" -> classOf[Player],
    "game.Side" -> classOf[Side],
    "game.OutcomeClass" -> classOf[LoopyOutcomeClass],

    "game.heap.TakeAndBreak" -> classOf[TakeAndBreak],
    "game.heap.Spawning" -> classOf[Spawning],
    "game.heap.HeapRuleset" -> classOf[HeapRuleset],
    "game.heap.Periodicity" -> classOf[Periodicity]

  )

  val allSystemClasses = baseSystemClasses ++ typedSystemClasses

  var interpreter: IMain = _
  var domain: ElaborationDomain = _
  var debug: Boolean = _

  setDebug(true)

  if (debug)
    LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[Logger].setLevel(Level.DEBUG)

  initialize()

  def initialize(): Unit = {

    val settings = new Settings
    settings.usejavacp.value = true
    settings.deprecation.value = true

    interpreter = new IMain(settings, new PrintWriter(DebugOutput))
    domain = new ElaborationDomain(None)
    interpreter.interpret("import org.cgsuite.dsl._")
    interpreter.interpret("import org.cgsuite.lang2.CgscriptImplicits._")
    evaluate("game.heap.GenWythoff(1)")

  }

  def evaluate(str: String): Option[Any] = {

    evaluateOrException(str) match {
      case scala.Left(obj) => Option(obj) // Could be null
      case scala.Right(t) =>
        t.printStackTrace()
        None
    }

  }

  def evaluateOrException(str: String): Either[Any, Throwable] = {

    val code = {
      try {
        val tree = ParserUtil.parseScript(str)
        val node = StatementSequenceNode(tree.children.head, topLevel = true)
        node.ensureElaborated(domain)
        node.mentionedClasses foreach { _.ensureCompiled(interpreter) }
        Thread.sleep(10)
        node.toScalaCodeWithVarDecls(new CompileContext())
      } catch {
        case t: Throwable => return scala.Right(t)
      }
    }

    code foreach { case (line, varNameOpt) =>

      val wrappedLine =
        s"""val __result = try { scala.Left {
           |
           |$line
           |
           |} } catch {
           |  case t: Throwable => scala.Right(t)
           |}
           |""".stripMargin

      if (debug)
        println(wrappedLine)

      interpreter interpret wrappedLine match {
        case IR.Error | IR.Incomplete =>
          throw EvalException("Internal error.")
        case IR.Success =>
      }

      // TODO Don't interpret subsequent lines if we get an error
      val result = (interpreter valueOfTerm "__result").get.asInstanceOf[Either[Any, Throwable]]
      if (result.isLeft) {
        varNameOpt foreach { varName =>
          interpreter interpret s"val $varName = __result.left.get"
        }
      }

    }

    val result = (interpreter valueOfTerm "__result").get.asInstanceOf[Either[Any, Throwable]]
    result

  }

  def setDebug(debug: Boolean): Unit = {
    this.debug = debug
  }

  def isDebug: Boolean = debug

  object DebugOutput extends java.io.OutputStream {

    override def write(b: Int): Unit = {
      if (debug)
        Console.out.write(b)
    }

  }

}

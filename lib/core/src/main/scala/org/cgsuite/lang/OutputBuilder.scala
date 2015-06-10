package org.cgsuite.lang

import java.util

import org.cgsuite.core.Values
import org.cgsuite.output.{StyledTextOutput, Output, OutputTarget}

import scala.collection.immutable.NumericRange
import scala.language.implicitConversions


object OutputBuilder {

  implicit def iterableToRichIterable(collection: Iterable[_]): RichIterable = new RichIterable(collection)

  def toOutput(x: Any): Output = x match {
    case ot: OutputTarget => ot.toOutput
    case range: NumericRange[_] =>
      val sto = new StyledTextOutput()
      sto.appendMath(range.start.toString)
      sto.appendMath("..")
      sto.appendMath(range.end.toString)
      if (range.step != Values.one) {
        sto.appendMath("..")
        sto.appendMath(range.step.toString)
      }
      sto
    case nil: Seq[_] if nil.isEmpty => new StyledTextOutput(util.EnumSet.of(StyledTextOutput.Style.FACE_MATH), "nil")
    case list: Seq[_] => list mkOutput ("[", ",", "]")
    case set: Set[_] => set.toSeq sorted UniversalOrdering mkOutput ("{", ",", "}")
    case map: Map[_,_] if map.isEmpty =>
      val sto = new StyledTextOutput()
      sto.appendMath("{")
      sto.appendSymbol(StyledTextOutput.Symbol.BIG_RIGHT_ARROW)
      sto.appendMath("}")
      sto
    case map: Map[_,_] => map.toSeq.sortBy { case (key: Any, _) => key }(UniversalOrdering) mkOutput ("{", ", ", "}")
    case (key, value) =>
      val sto = new StyledTextOutput()
      sto.appendOutput(toOutput(key))
      sto.appendMath(" ")
      sto.appendSymbol(StyledTextOutput.Symbol.BIG_RIGHT_ARROW)
      sto.appendMath(" ")
      sto.appendOutput(toOutput(value))
      sto
    case str: String => new StyledTextOutput("\"" + str + "\"")
  }

  class RichIterable(collection: Iterable[_]) {
    def mkOutput(open: String, sep: String, close: String): StyledTextOutput = {
      val sto = new StyledTextOutput()
      sto.appendMath(open)
      var first = true
      collection foreach { element =>
        if (!first)
          sto.appendMath(sep)
        sto.appendOutput(toOutput(element))
        first = false
      }
      sto.appendMath(close)
      sto
    }
  }

}

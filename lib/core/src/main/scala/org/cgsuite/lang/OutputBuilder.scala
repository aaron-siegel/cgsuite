package org.cgsuite.lang

import org.cgsuite.output.{StyledTextOutput, Output, OutputTarget}


object OutputBuilder {

  implicit def iterableToRichIterable(collection: Iterable[_]): RichIterable = new RichIterable(collection)

  def toOutput(x: Any): Output = x match {
    case ot: OutputTarget => ot.toOutput
    case list: Seq[_] => list mkOutput ("[", ",", "]")
    case set: Set[_] => set mkOutput ("{", ",", "}")
    case map: Map[_,_] => map mkOutput ("{", ", ", "}")
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

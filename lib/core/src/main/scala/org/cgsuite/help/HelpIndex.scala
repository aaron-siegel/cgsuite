package org.cgsuite.help

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object HelpIndex {

  private val index = mutable.Map[String, ArrayBuffer[Result]]()

  {
    val inputStream = HelpIndex.getClass.getResourceAsStream("docs/search-index.csv")
    Source.fromInputStream(inputStream).getLines foreach { line =>
      val toks = line.split(',')
      val result = Result(toks(0), toks(1), toks(2))
      val tailComponentsToIgnore = toks(3).toInt
      val separatorIndices = result.displayName.indices filter { result.displayName(_) == '.' }
      for {
        sepIndex <- -1 +: separatorIndices.dropRight(tailComponentsToIgnore)
        n <- 1 until result.displayName.length - sepIndex - 1
      } {
        val key = result.displayName.substring(sepIndex + 1, sepIndex + 1 + n).toLowerCase
        index.getOrElseUpdate(key, ArrayBuffer()) += result
      }
    }
  }

  def lookup(key: String): Iterable[Result] = {
    index getOrElse (key.toLowerCase, Iterable.empty)
  }

  case class Result(displayName: String, displayHint: String, path: String)

}

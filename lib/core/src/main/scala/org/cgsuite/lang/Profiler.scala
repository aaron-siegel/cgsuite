package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.lang.node.EvalNode
import org.cgsuite.lang.parser.ParserUtil

import scala.collection.mutable

object Profiler {

  def main(args: Array[String]): Unit = {
    CgscriptClass.Object.ensureInitialized()
    profileStatement("""game.grid.Clobber("xoxo|oxox|xoxo").CanonicalForm""", "0")
    profileStatement("""game.grid.Clobber("xoxo|oxox|xoxo|ox..").CanonicalForm.StopCount""", "20101")
  }

  def profileStatement(statement: String, expectedResult: String): Unit = {
    // Warm-up
    CgscriptClass.clearAll()
    //evalForProfiler(statement, expectedResult, profile = false)
    CgscriptClass.clearAll()
    val withoutProfiling = evalForProfiler(statement, expectedResult, profile = false)
    CgscriptClass.clearAll()
    val withProfiling = evalForProfiler(statement, expectedResult, profile = true)
    Profiler.print(withProfiling - withoutProfiling)
    printf("Without Profiling: %10.1f ms\n", withoutProfiling/1000000.0)
    printf("With Profiling   : %10.1f ms\n", withProfiling/1000000.0)
  }

  def evalForProfiler(str: String, expectedResult: String, profile: Boolean): Long = {
    println(s"Evaluating with profile = $profile: $str")
    val domain = new EvaluationDomain(null, None, Some(mutable.AnyRefMap()))
    val tree = ParserUtil.parseStatement(str)
    println(tree.toStringTree)
    val node = EvalNode(tree)
    println(node)
    node.elaborate(ElaborationDomain.empty())
    Profiler.clear()
    Profiler.setEnabled(enabled = profile)
    val start = JSystem.nanoTime()
    Profiler.start(Symbol("Main"))
    val result = node.evaluate(domain).toString
    Profiler.stop(Symbol("Main"))
    val totalDuration = JSystem.nanoTime() - start
    Profiler.setEnabled(enabled = false)
    assert(
      result == expectedResult,
      s"Performance is great. Correctness is better. ($result != $expectedResult)"
    )
    totalDuration
  }

  private def _profilerAvgNanos = {
    (0 to 500).map { _ =>
      (0 to 5000).foreach { _ =>
        Profiler.start(Symbol("ProfileProfiler"))
        Profiler.stop(Symbol("ProfileProfiler"))
      }
      val result = Profiler.totals(Symbol("ProfileProfiler")).timing / 5000
      totals.clear()
      result
    }.sorted.apply(250)
  }

  var profilerAvgNanos: Long = -1L

  case class Total(count: Long, timing: Long, stackPops: Long)

  class Totals(var count: Long, var timing: Long)

  private var enabled = false
  private var whitelist = Set.empty[Symbol]
  private var lastEvent = 0L
  private val stack = mutable.Stack[(Symbol, Totals)]()
  private val totals = mutable.AnyRefMap[Symbol, Totals]()

  def setEnabled(enabled: Boolean): Unit = {
    this.enabled = enabled
    if (enabled && profilerAvgNanos == -1L) {
      whitelist = Set(Symbol("ProfileProfiler"))
      profilerAvgNanos = _profilerAvgNanos
      println("Average profiler latency: " + profilerAvgNanos + " ns")
      whitelist = Set.empty
    } else {
      whitelist = Set.empty
    }
  }

  def setWhitelist(symbols: Symbol*): Unit = {
    whitelist = Set(symbols : _*)
  }

  def start(key: Symbol): Unit = {
    if (enabled && (whitelist.isEmpty || whitelist.contains(key))) {
      val now = JSystem.nanoTime
      if (stack.nonEmpty) {
        stack.top._2.timing += now - lastEvent
      }
      lastEvent = now
      stack.push((key, totals.getOrElseUpdate(key, new Totals(0L, 0L))))
    }
  }

  def stop(key: Symbol): Unit = {
    if (enabled && (whitelist.isEmpty || whitelist.contains(key))) {
      val now = JSystem.nanoTime
      val (key2, totals) = stack.pop()
      assert(key == key2)
      totals.count += 1L
      totals.timing += now - lastEvent
      lastEvent = now
    }
  }

  def print(totalProfileOverhead: Long): Unit = {
    val totalEvents = totals.toSeq.map { _._2.count }.sum
    val latencyPerEvent = totalProfileOverhead / totalEvents
    printf("Latency per event : %10d ns\n", latencyPerEvent)
    totals.toSeq sortBy { case (_, t) => -(t.timing - t.count * latencyPerEvent) } foreach { case (symbol, t) =>
      printf(
        "%10.1f ms, %10d events (%10d ns each): %s\n",
        (t.timing - t.count * latencyPerEvent) / 1000000.0,
        t.count,
        t.timing / t.count - latencyPerEvent,
        symbol.name
      )
    }
  }

  def clear(): Unit = {
    totals.clear()
    stack.clear()
  }

}

package org.cgsuite.lang

import java.lang.{System => JSystem}

import org.cgsuite.lang.parser.ParserUtil

import scala.collection.mutable

object Profiler {

  def main(args: Array[String]) {
    CgscriptClass.Object.ensureLoaded()
    val statement = """game.grid.Clobber("xoxo|oxox|xoxo|ox..").CanonicalForm.StopCount"""
    // Warm-up
    evalForProfiler(statement, profile = false)
    CgscriptClass.clearAll()
    val withoutProfiling = evalForProfiler(statement, profile = false)
    CgscriptClass.clearAll()
    val withProfiling = evalForProfiler(statement, profile = true)
    Profiler.print(withProfiling - withoutProfiling)
    printf("Without Profiling: %10.1f ms\n", withoutProfiling/1000000.0)
    printf("With Profiling   : %10.1f ms\n", withProfiling/1000000.0)
  }

  def evalForProfiler(str: String, profile: Boolean): Long = {
    println(s"Evaluating with profile = $profile: $str")
    val domain = new Domain(null, None, Some(mutable.AnyRefMap()))
    val tree = ParserUtil.parseStatement(str)
    println(tree.toStringTree)
    val node = EvalNode(tree)
    println(node)
    node.elaborate(ElaborationDomain(None, Seq.empty, None))
    Profiler.clear()
    Profiler.setEnabled(enabled = profile)
    val start = JSystem.nanoTime()
    Profiler.start('Main)
    val result = node.evaluate(domain).asInstanceOf[org.cgsuite.core.Integer].intValue
    Profiler.stop('Main)
    val totalDuration = JSystem.nanoTime() - start
    Profiler.setEnabled(enabled = false)
    assert(
      result == 20101,
      s"Performance is great. Correctness is better. ($result != 20101)"
    )
    totalDuration
  }

  private def _profilerAvgNanos = {
    (0 to 500).map { _ =>
      (0 to 5000).foreach { _ =>
        Profiler.start('ProfileProfiler)
        Profiler.stop('ProfileProfiler)
      }
      val result = Profiler.totals('ProfileProfiler).timing / 5000
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

  def setEnabled(enabled: Boolean) {
    this.enabled = enabled
    if (enabled && profilerAvgNanos == -1L) {
      whitelist = Set('ProfileProfiler)
      profilerAvgNanos = _profilerAvgNanos
      println("Average profiler latency: " + profilerAvgNanos + " ns")
      whitelist = Set.empty
    } else {
      whitelist = Set.empty
    }
  }

  def setWhitelist(symbols: Symbol*) {
    whitelist = Set(symbols : _*)
  }

  def start(key: Symbol) {
    if (enabled && (whitelist.isEmpty || whitelist.contains(key))) {
      val now = JSystem.nanoTime
      if (stack.nonEmpty) {
        stack.top._2.timing += now - lastEvent
      }
      lastEvent = now
      stack.push((key, totals.getOrElseUpdate(key, new Totals(0L, 0L))))
    }
  }

  def stop(key: Symbol) {
    if (enabled && (whitelist.isEmpty || whitelist.contains(key))) {
      val now = JSystem.nanoTime
      val (key2, totals) = stack.pop()
      assert(key == key2)
      totals.count += 1L
      totals.timing += now - lastEvent
      lastEvent = now
    }
  }

  def print(totalProfileOverhead: Long) = {
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

  def clear() {
    totals.clear()
    stack.clear()
  }

}

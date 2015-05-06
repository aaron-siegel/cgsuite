package org.cgsuite.util

import org.cgsuite.core.CanonicalShortGameOps
import org.cgsuite.lang.parser.ParserUtil
import org.cgsuite.lang.{Namespace, Domain, CgsuiteClass, Node}
import scala.collection.mutable

object Profiler {

  def main(args: Array[String]) {
    // Warm-up
    evalForProfiler("""Clobber("xoxo|oxox|xoxo|ox..").CanonicalForm""", profile = false)
    CanonicalShortGameOps.reinit()
    CgsuiteClass.clearAll()
    val withoutProfiling = evalForProfiler("""Clobber("xoxo|oxox|xoxo|ox..").CanonicalForm""", profile = false)
    CanonicalShortGameOps.reinit()
    CgsuiteClass.clearAll()
    val withProfiling = evalForProfiler("""Clobber("xoxo|oxox|xoxo|ox..").CanonicalForm""", profile = true)
    Profiler.print(withProfiling - withoutProfiling)
    printf("Without Profiling: %10.1f ms\n", withoutProfiling/1000000.0)
    printf("With Profiling   : %10.1f ms\n", withProfiling/1000000.0)
  }

  def evalForProfiler(str: String, profile: Boolean): Long = {
    println(s"Evaluating with profile = $profile: $str")
    val domain = new Domain(Namespace.checkout(None, Map.empty))
    val tree = ParserUtil.parseStatement(str)
    val node = Node(tree)
    //println(tree.toStringTree)
    //println(node)
    Profiler.clear()
    Profiler.setEnabled(enabled = profile)
    val start = System.nanoTime()
    Profiler.start('Main)
    node.evaluate(domain)
    Profiler.stop('Main)
    val totalDuration = System.nanoTime() - start
    Profiler.setEnabled(enabled = false)
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
      val now = System.nanoTime
      if (stack.nonEmpty) {
        stack.top._2.timing += now - lastEvent
      }
      lastEvent = now
      stack.push((key, totals.getOrElseUpdate(key, new Totals(0L, 0L))))
    }
  }

  def stop(key: Symbol) {
    if (enabled && (whitelist.isEmpty || whitelist.contains(key))) {
      val now = System.nanoTime
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

package org.cgsuite.util

import scala.collection.mutable

object Profiler {

  private def _profilerAvgNanos = {
    (1 to 1000000).foreach { _ =>
      Profiler.start('ProfileProfiler, "Testing profiler")
      Profiler.stop('ProfileProfiler, "Testing profiler")
    }
    val total = totals(('ProfileProfiler, "Testing profiler"))
    total.timing / total.count
  }

  lazy val profilerAvgNanos = {
    _profilerAvgNanos  // Warm-up
    totals.remove(('ProfileProfiler, "Testing profiler"))
    val result = _profilerAvgNanos
    totals.remove(('ProfileProfiler, "Testing profiler"))
    result
  }

  case class Total(count: Long, timing: Long)

  private var enabled = false
  private var whitelist = Set.empty[Symbol]
  private val profiling = mutable.Map[(Symbol, String), Long]()
  private val totals = mutable.Map[(Symbol, String), Total]()

  def setEnabled(enabled: Boolean) {
    this.enabled = enabled
    if (enabled) {
      println("Average profiler latency: " + profilerAvgNanos + " ns")
    } else {
      whitelist = Set.empty
    }
  }

  def setWhitelist(symbols: Symbol*) {
    whitelist = Set(symbols : _*)
  }

  def start(symbol: Symbol, str: => String = "") = {
    if (enabled && (whitelist.isEmpty || whitelist.contains(symbol))) {
      if (profiling.contains((symbol, str))) {
        sys.error("duplicate profiler name: " + str)
      } else {
        profiling.put((symbol, str), System.nanoTime())
      }
    }
  }

  def stop(symbol: Symbol, str: => String = "") = {
    if (enabled && (whitelist.isEmpty || whitelist.contains(symbol))) {
      val duration = System.nanoTime - profiling.remove((symbol, str)).getOrElse { sys.error("invalid stop: " + str) }
      val prevTotal = totals.getOrElse((symbol, str), Total(0L, 0L))
      totals.put((symbol, str), Total(prevTotal.count + 1L, prevTotal.timing + duration))
    }
  }

  def print() = {
    totals.toSeq sortBy { -_._2.timing } foreach { case ((symbol, str), total) =>
      printf(
        "%10.1f ms, %10d events (%10d ns each): %s [%s]\n",
        (total.timing - total.count * profilerAvgNanos) / 1000000.0,
        total.count,
        (total.timing.toDouble / total.count).toLong,
        symbol.name,
        str
      )
    }
  }

  def clear() {
    profiling.clear()
    totals.clear()
  }

}

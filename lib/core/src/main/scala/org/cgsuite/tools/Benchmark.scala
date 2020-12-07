package org.cgsuite.tools

import java.io.{BufferedReader, File, FileOutputStream, InputStreamReader, PrintWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.cgsuite.lang.CgscriptSystem
import org.slf4j.LoggerFactory

object Benchmark {

  val instances = Vector(
    Instance("Initialization", "0"),
    Instance("Warmup", """game.grid.Amazons("x|o"); game.grid.Clobber("x|o"); game.heap.Nim(20); game.strip.ToadsAndFrogs("t.f");"""),
    Instance("Heated *14", """*14 Heat 1;"""),
    Instance("Big Output", """*12 Heat 1"""),
    Instance("Kayles", "game.heap.Kayles(40000).NimValue;"),
    Instance("Amazons", """game.grid.Amazons("x......|o......").CanonicalForm;"""),
  )
  val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss")

  def main(args: Array[String]): Unit = {

    run(args.length > 0 && args.head == "--console")

  }

  def run(consoleOnly: Boolean = true): Unit = {

    val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]
    rootLogger.detachAndStopAllAppenders()

    Benchmark(instances).run(consoleOnly)

  }

  def gitSha(): String = {
    val process = Runtime.getRuntime.exec("git log")
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))
    val line = reader.readLine()
    reader.close()
    val sha = line.split(' ')(1)
    sha
  }

  def gitBranch(): String = {
    val process = Runtime.getRuntime.exec("git status")
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))
    val line = reader.readLine()
    reader.close()
    val branch = line.stripPrefix("On branch ")
    branch
  }

  def gitHasLocalChanges(): Boolean = {
    val process = Runtime.getRuntime.exec("git status")
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))
    val line = reader.readLine()
    reader.close()
    line != null
  }

  case class Instance(name: String, command: String)

}

case class Benchmark(instances: Vector[Benchmark.Instance]) {

  def run(consoleOnly: Boolean = true): Unit = {

    val dateTime = Benchmark.dateFormat.format(LocalDateTime.now())
    var writer: PrintWriter = null

    val sha = Benchmark.gitSha()
    val branch = Benchmark.gitBranch()

    if (!consoleOnly) {
      if (Benchmark.gitHasLocalChanges()) {
        println("Git repo has local changes; please commit or stash (or re-run Benchmark with consoleOnly = true).")
        return
      }
      val localDir = new File("local")
      localDir.mkdir()
      val output = new File(localDir, s"CGSuite Benchmark $dateTime.txt")
      writer = new PrintWriter(new FileOutputStream(output))
    }

    println("CGSuite Benchmark\n")

    val systemInformation =
      s"""Date           : $dateTime
         |CGSuite Version: 2.0
         |Git Branch     : $branch
         |Git SHA        : $sha
         |Java Version   : ${util.Properties.javaVersion}
         |Scala Version  : ${util.Properties.versionNumberString}
         |OS             : ${System.getProperty("os.name")} ${System.getProperty("os.version")}
         |Heap Memory    : ${java.lang.Runtime.getRuntime.maxMemory >> 20} MB
         |""".stripMargin

    emitln(systemInformation)

    instances foreach { instance =>

      emit(f"${instance.name}%-15s: ")
      val startTime = System.currentTimeMillis()
      try {
        CgscriptSystem.evaluate(instance.command)
        val duration = (System.currentTimeMillis() - startTime) / 1000.0
        emitln(f"$duration%7.2f s")
      } catch {
        case _: Throwable =>
          emitln("Failed")
      }

    }

    if (!consoleOnly)
      writer.close()

    def emit(str: String): Unit = {
      print(str)
      if (!consoleOnly)
        writer.print(str)
    }

    def emitln(str: String): Unit = {
      println(str)
      if (!consoleOnly)
        writer.println(str)
    }

  }

}
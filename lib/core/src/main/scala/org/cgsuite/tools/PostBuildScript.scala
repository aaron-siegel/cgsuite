package org.cgsuite.tools

import better.files._
import org.cgsuite.help.HelpBuilder

object PostBuildScript {

  def main(args: Array[String]): Unit = {

    println("Running post-build script.")
    println("Replacing ${cgsuite.banner} in target dir.")
    replaceBanner()
    println("Generating documentation.")
    HelpBuilder.main(Array.empty)

  }

  def replaceBanner(): Unit = {
    val rawBanner = "etc/cgsuite-banner".toFile.lines mkString "\n"
    val banner = rawBanner.
      replace("${app.version}", org.cgsuite.lang.System.version).
      replace("${cgsuite.copyright}", org.cgsuite.lang.System.copyrightYear)
    val resourcesDir = "target/classes/org/cgsuite/lang/resources".toFile
    val files = resourcesDir.glob("**/*.cgs")
    for (file <- files) {
      val text = file.lines mkString "\n"
      val replaced = text.replace("/*${cgsuite.banner}*/", banner)
      file overwrite replaced
      file append "\n"
    }
  }

}

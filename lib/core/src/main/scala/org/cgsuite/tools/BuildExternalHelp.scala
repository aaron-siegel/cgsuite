package org.cgsuite.tools

import better.files.File.CopyOptions
import better.files._
import org.cgsuite.help.HelpBuilder

object BuildExternalHelp {

  val resourcesDir = "src/main/resources".toFile
  val docsDir = resourcesDir/"org/cgsuite/help/docs"
  val buildDir = "target/site/docs".toFile

  def main(args: Array[String]): Unit = {

    buildDir.createDirectories()
    val filesToCopy = docsDir.glob("*.{html,css}")
    implicit val copyOptions: CopyOptions = CopyOptions(overwrite = true)
    filesToCopy foreach { _.copyToDirectory(buildDir) }
    HelpBuilder(resourcesDir, buildDir, externalBuild = true).run()

  }

}

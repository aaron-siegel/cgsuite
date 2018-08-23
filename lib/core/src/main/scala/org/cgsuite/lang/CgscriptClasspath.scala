package org.cgsuite.lang

import better.files._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.collection.mutable

object CgscriptClasspath {

  private val logger = Logger(LoggerFactory.getLogger(classOf[CgscriptClass]))

  private[lang] val homeDir = java.lang.System.getProperty("user.home")

  private[lang] val classpath: Vector[File] = Vector(
    //getClass().getResource("resources"),
    homeDir/"CGSuite"
  )

  private[lang] val modifiedFiles = mutable.Set[File]()

  private[lang] def declareFolders(): Unit = {
    logger debug "Declaring folders."
    classpath foreach declareFolder
  }

  private[lang] def declareFolder(folder: File): Unit = {
    //new Monitor(folder).start()
    declareFolderR(CgscriptPackage.root, folder)
  }

  private[lang] def declareFolderR(pkg: CgscriptPackage, folder: File): Unit = {
    logger debug s"Declaring folder: $folder as package ${pkg.name}"
    folder.children foreach { file =>
      if (file.isDirectory) {
        declareFolderR(pkg.declareSubpackage(file.name), file)
      } else if (file.extension contains ".cgs") {
        pkg.declareClass(Symbol(file.nameWithoutExtension), Some(file.url), None, None)
      }
    }
  }

  def markAsModified(file: File): Unit = {
    println(s"Modified: $file")
    modifiedFiles.synchronized {
      modifiedFiles += file
    }
  }

  class Monitor(folder: File) extends FileMonitor(folder, recursive = true) {

    override def onCreate(file: File, count: Int): Unit = markAsModified(file)

    override def onModify(file: File, count: Int): Unit = markAsModified(file)

    override def onDelete(file: File, count: Int): Unit = markAsModified(file)

  }

}

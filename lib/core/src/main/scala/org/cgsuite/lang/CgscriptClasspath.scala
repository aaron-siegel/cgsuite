package org.cgsuite.lang

import better.files._
import io.methvin.better.files.RecursiveFileMonitor
import org.cgsuite.lang.CgscriptClass.logger

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object CgscriptClasspath {

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
    new Monitor(folder).start()
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
    modifiedFiles synchronized {
      logger debug s"Detected modified file: $file"
      modifiedFiles += file
    }
  }

  def reloadModifiedFiles(): Unit = {
    modifiedFiles synchronized {
      modifiedFiles foreach { file =>
        val classOpt = CgscriptPackage lookupClass file.url
        classOpt match {
          case Some(cls) =>
            logger debug s"Unloading ${cls.qualifiedName} at location $file"
            cls.unload()
          case None =>
            logger debug s"New class at location $file"
            // TODO
        }
      }
      modifiedFiles.clear()
    }
  }

  class Monitor(folder: File) extends RecursiveFileMonitor(folder) {

    override def onCreate(file: File, count: Int): Unit = markAsModified(file)

    override def onModify(file: File, count: Int): Unit = markAsModified(file)

    override def onDelete(file: File, count: Int): Unit = markAsModified(file)

  }

}

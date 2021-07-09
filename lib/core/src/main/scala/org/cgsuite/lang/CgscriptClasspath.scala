package org.cgsuite.lang

import java.nio.file.spi.FileSystemProvider
import java.util.Collections

import better.files._
import io.methvin.better.files.RecursiveFileMonitor
import org.cgsuite.lang.CgscriptClass.logger

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object CgscriptClasspath {

  private[cgsuite] val devBuildHome = Option(java.lang.System.getProperty("org.cgsuite.devbuild")) map { File(_) }

  private[cgsuite] val cgsuiteDir = java.lang.System.getProperty("user.home")/"CGSuite"

  private[cgsuite] val systemDir = {
    devBuildHome match {
      case Some(home) => home/"../lib/core/src/main/resources/org/cgsuite/lang/resources"
      case None =>
        // Search in the production jar.
        val uri = CgscriptSystem.getClass.getResource("resources").toURI
        if (uri.getScheme == "jar") {
          FileSystemProvider.installedProviders find { _.getScheme equalsIgnoreCase "jar" } foreach { provider =>
            provider.newFileSystem(uri, Collections.emptyMap[String, AnyRef])
          }
        }
        File(CgscriptSystem.getClass.getResource("resources").toURI)
    }
  }

  private[cgsuite] val classpath: Vector[File] = Vector(systemDir, cgsuiteDir)

  private[cgsuite] val modifiedFiles = mutable.Set[ModifiedFile]()

  private[cgsuite] def declareFolders(): Unit = {
    logger debug "Declaring folders."
    if (devBuildHome.isDefined)
      logger debug "This is a CGSuite developer build."
    logger debug s"System dir: $systemDir"
    logger debug s"User dir: $cgsuiteDir"
    classpath foreach declareFolder
  }

  private[cgsuite] def declareFolder(folder: File): Unit = {
    if (folder.fileSystem.provider.getScheme != "jar")
      new Monitor(folder).start()
    declareFolderR(CgscriptPackage.root, folder, folder)
  }

  private[cgsuite] def declareFolderR(pkg: CgscriptPackage, root: File, folder: File): Unit = {
    logger debug s"Declaring folder: $folder as package ${pkg.name}"
    folder.children foreach { file =>
      if (file.isDirectory) {
        declareFolderR(pkg.declareSubpackage(file.name stripSuffix "/"), root, file)
      } else if (file.extension exists { _.toLowerCase == ".cgs" }) {
        pkg.declareClass(Symbol(file.nameWithoutExtension), UrlClassDef(root, file.url), None)
      }
    }
  }

  def markAsModified(folder: File, file: File): Unit = {
    modifiedFiles synchronized {
      logger debug s"Detected modified file: $file (in folder $folder)"
      modifiedFiles += ModifiedFile(folder, file)
    }
  }

  def reloadModifiedFiles(): Unit = {
    modifiedFiles synchronized {
      val foldersToRedeclare = mutable.Set[File]()
      modifiedFiles foreach { case ModifiedFile(folder, file) =>
        val classOpt = CgscriptPackage lookupClass file.url
        classOpt match {
          case Some(cls) =>
            logger debug s"Unloading ${cls.qualifiedName} at location $file"
            cls.unload()
          case None if file.extension exists { _.toLowerCase == ".cgs" } =>
            foldersToRedeclare += folder
          case None =>
        }
      }
      foldersToRedeclare foreach declareFolder
      modifiedFiles.clear()
    }
  }

  class Monitor(folder: File) extends RecursiveFileMonitor(folder) {

    override def onCreate(file: File, count: Int): Unit = markAsModified(folder, file)

    override def onModify(file: File, count: Int): Unit = markAsModified(folder, file)

    override def onDelete(file: File, count: Int): Unit = markAsModified(folder, file)

  }

  case class ModifiedFile(folder: File, file: File)

}

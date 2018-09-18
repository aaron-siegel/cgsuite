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

  private[lang] val isDevBuild = java.lang.System.getProperty("org.cgsuite.devbuild") != null

  private[lang] val cgsuiteDir = java.lang.System.getProperty("user.home")/"CGSuite"

  private[lang] val systemDir = {
    if (isDevBuild) {
      ".."/"lib/core/src/main/resources/org/cgsuite/lang/resources"
    } else {
      val uri = getClass.getResource("resources").toURI
      if (uri.getScheme == "jar") {
        FileSystemProvider.installedProviders find { _.getScheme equalsIgnoreCase "jar" } foreach { provider =>
          provider.newFileSystem(uri, Collections.emptyMap[String, AnyRef])
        }
      }
      File(getClass.getResource("resources").toURI)
    }
  }

  private[lang] val classpath: Vector[File] = Vector(systemDir, cgsuiteDir)

  private[lang] val modifiedFiles = mutable.Set[ModifiedFile]()

  private[lang] def declareFolders(): Unit = {
    logger debug "Declaring folders."
    classpath foreach declareFolder
  }

  private[lang] def declareFolder(folder: File): Unit = {
    if (folder.fileSystem.provider.getScheme != "jar")
      new Monitor(folder).start()
    declareFolderR(CgscriptPackage.root, folder)
  }

  private[lang] def declareFolderR(pkg: CgscriptPackage, folder: File): Unit = {
    logger debug s"Declaring folder: $folder as package ${pkg.name}"
    folder.children foreach { file =>
      if (file.isDirectory) {
        declareFolderR(pkg.declareSubpackage(file.name stripSuffix "/"), file)
      } else if (file.extension exists { _.toLowerCase == ".cgs" }) {
        pkg.declareClass(Symbol(file.nameWithoutExtension), UrlClassDef(file.url), None)
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

package org.cgsuite.lang

import java.nio.file.spi.FileSystemProvider
import java.util.Collections

import better.files._
import io.methvin.better.files.RecursiveFileMonitor
import org.cgsuite.lang.CgscriptClass.logger

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters._

object CgscriptClasspath {

  val defaultStartupScript =
    """/*
      | * This is your CGSuite startup file.
      | *
      | * Any commands that you include in this file will be executed whenever
      | * you start CGSuite.
      | *
      | * You can re-run this script at any time without restarting CGSuite by
      | * entering the following command on the Worksheet:
      | *
      | * startup();
      | *
      | */
      |
      |// Uncomment the following line to implement the shortcut "C(g)" for
      |// the canonical form "g.CanonicalForm".
      |
      |// def C(g) := g.CanonicalForm;
      |""".stripMargin

  private[cgsuite] val devBuildHome = Option(java.lang.System.getProperty("org.cgsuite.devbuild")) map { File(_) }

  private[cgsuite] val classpathRoots = mutable.ArrayBuffer[File]()

  private[cgsuite] val systemDir = {
    devBuildHome match {
      case Some(home) => home/"../lib/core/src/main/resources/org/cgsuite/lang/resources"
      case None =>
        // Search in the production jar.
        val uri = getClass.getResource("resources").toURI
        if (uri.getScheme == "jar") {
          FileSystemProvider.installedProviders.asScala find { _.getScheme equalsIgnoreCase "jar" } foreach { provider =>
            provider.newFileSystem(uri, Collections.emptyMap[String, AnyRef])
          }
        }
        File(getClass.getResource("resources").toURI)
    }
  }

  private[cgsuite] val modifiedFiles = mutable.Set[ModifiedFile]()

  def declareSystemClasspathRoot(): Unit = {
    declareClasspathRoot(systemDir, ensureStartupScriptExists = false)
  }

  def declareClasspathRoot(folder: java.io.File, ensureStartupScriptExists: Boolean): Unit = {
    declareClasspathRoot(folder.toScala, ensureStartupScriptExists)
  }

  def declareClasspathRoot(folder: File, ensureStartupScriptExists: Boolean): Unit = {
    logger debug s"Declaring classpath root: $folder"
    classpathRoots += folder
    if (folder.fileSystem.provider.getScheme != "jar")
      new Monitor(folder).start()
    if (ensureStartupScriptExists) {
      val startupScriptFile = folder / "startup.cgs"
      try {
        if (!startupScriptFile.exists) {
          startupScriptFile overwrite defaultStartupScript
        }
      } catch {
        case exc: Exception => logger.warn(s"Could not create startup script $startupScriptFile: ${exc.getMessage}", exc)
      }
    }
    declareFolder(folder)
  }

  def copyExamples(dest: java.io.File): Unit = copyExamples(dest.toScala)

  def copyExamples(dest: File): Unit = {
    val uri = getClass.getResource("examples").toURI
    if (uri.getScheme == "jar") {
      FileSystemProvider.installedProviders.asScala find { _.getScheme equalsIgnoreCase "jar" } foreach { provider =>
        provider.newFileSystem(uri, Collections.emptyMap[String, AnyRef])
      }
    }
    val examplesDir = File(getClass.getResource("examples").toURI)
    examplesDir.children foreach { exampleFile =>
      val copyToFile = dest / exampleFile.name
      val in = exampleFile.newInputStream
      val text = scala.io.Source.fromInputStream(in).getLines() mkString "\n"
      in.close()
      copyToFile overwrite text
      copyToFile append "\n"
    }
  }

  private[cgsuite] def declareFolder(folder: File): Unit = {
    declareFolderR(CgscriptPackage.root, folder, folder)
  }

  private[cgsuite] def declareFolderR(pkg: CgscriptPackage, root: File, folder: File): Unit = {
    logger debug s"Declaring folder: $folder as package ${pkg.name}"
    folder.children foreach { file =>
      if (file.isDirectory) {
        declareFolderR(pkg.declareSubpackage(file.name stripSuffix "/"), root, file)
      } else if (file.extension exists { _.toLowerCase == ".cgs" }) {
        val url = folder.fileSystem.provider.getScheme match {
          case "jar" => getClass.getResource(file.toString)
          case _ => file.url
        }
        pkg.declareClass(Symbol(file.nameWithoutExtension), UrlClassDef(root, url), None)
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

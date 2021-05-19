package org.cgsuite.kernel

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.ServerSocket

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.{Level, LoggerContext}
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import org.cgsuite.core.{Game, Player}
import org.cgsuite.kernel.Kernel.logger
import org.cgsuite.lang.{CgscriptClasspath, CgscriptSystem, EvalUtil}
import org.cgsuite.output.Output
import org.cgsuite.util.{Explorer, UiHarness}
import org.slf4j.LoggerFactory

import scala.collection.mutable

object Kernel {

  private[kernel] val logger = com.typesafe.scalalogging.Logger(LoggerFactory.getLogger(classOf[Kernel]))

  def main(args: Array[String]): Unit = {

    configureLogging(debug = true)

    new Kernel().start()

  }

  def configureLogging(debug: Boolean): Unit = {

    val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]
    rootLogger.detachAndStopAllAppenders()

    val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

    val ple = new PatternLayoutEncoder
    ple.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n")
    ple.setContext(lc)
    ple.start()

    val fileAppender = new FileAppender[ILoggingEvent]()
    fileAppender.setFile("cgsuite-kernel.log")
    fileAppender.setEncoder(ple)
    fileAppender.setContext(lc)
    fileAppender.start()

    rootLogger.addAppender(fileAppender)
    rootLogger.setLevel(if (debug) Level.DEBUG else Level.INFO)
    rootLogger.setAdditive(false)

  }

}

class Kernel() {

  val out = new ObjectOutputStream(System.out)
  out.flush()
  logger.info("Output stream initialized.")
  val in = new ObjectInputStream(System.in)
  logger.info("Input stream initialized.")

  def start(): Unit = {

    logger.info("CGSuite Kernel is starting up.")

    UiHarness.setUiHarness(KernelUiHarness)
    CgscriptSystem.evaluate("0")

    logger.info("Kernel initialized.")

    while (true) {

      val request = in.readObject().asInstanceOf[KernelRequest]
      logger.info("Received request: " + request)

      request match {

        case InputKernelRequest(input) =>
          CgscriptClasspath.reloadModifiedFiles()
          val output = CgscriptSystem.evaluateAndProcessExceptions(input)
          sendWorksheetResponse(output, isFinal = true)

        case ExplorerKernelRequest(explorerId, nodeOrdinal, action) =>
          KernelUiHarness.routeExplorerAction(explorerId, nodeOrdinal, action)

      }

    }

/*
    val server = new ServerSocket(port)

    println("SERVER initialized.")
    val socket = server.accept()
    println("CLIENT connected.")
    val out = new ObjectOutputStream(socket.getOutputStream)
    val in = new ObjectInputStream(socket.getInputStream)

    while (true) {

      val request = in.readObject().asInstanceOf[KernelRequest]
      println("Received request: " + request.input)
      val output = CgscriptSystem.evaluateToOutput(request.input)
      val response = KernelResponse(output)
      out.writeObject(response)

    }
*/
  }

  def sendWorksheetResponse(output: Either[Vector[Output], Throwable], isFinal: Boolean): Unit = {

    val response = WorksheetKernelResponse(output.left.getOrElse(null), output.right.getOrElse(null), isFinal)
    send(response)

  }

  def send(response: KernelResponse): Unit = {

    logger.info("Writing response: " + response)
    out.writeObject(response)
    out.flush()

  }

  object KernelUiHarness extends UiHarness {

    private val knownExplorers = mutable.Map[String, Explorer]()

    override def createExplorer(g: Game): Explorer = {
      val explorer = new Explorer()
      val uuid = java.util.UUID.randomUUID().toString
      knownExplorers(uuid) = explorer
      send(NewExplorerKernelResponse(uuid, isFinal = false))
      val initialNode = explorer.addRootNode(g)
      send(ExplorerUpdatedKernelResponse(
        uuid,
        RootNodeCreatedExplorerUpdate(NodeInfo(initialNode.ordinal, initialNode.g.toOutput)),
        isFinal = false
      ))
      explorer
    }

    override def print(obj: AnyRef): Unit = {
      val output = EvalUtil.objectToOutput(obj)
      sendWorksheetResponse(scala.Left(output), isFinal = false)
    }

    def routeExplorerAction(explorerId: String, nodeOrdinal: Int, action: ExplorerAction.Value): Unit = {
      knownExplorers get explorerId foreach { explorer =>

        action match {
          case ExplorerAction.ExpandSensibleOptions =>
            val node = explorer.lookupNode(nodeOrdinal)
            val newLeftOptions = explorer.expandSensibleOptions(node, Player.Left) map { NodeInfo(_) }
            val newRightOptions = explorer.expandSensibleOptions(node, Player.Right) map { NodeInfo(_) }
            send(ExplorerUpdatedKernelResponse(
              explorerId,
              NodeExpandedExplorerUpdate(nodeOrdinal, newLeftOptions, newRightOptions),
              isFinal = true
            ))
        }

      }
    }

  }

}

package org.cgsuite.kernel

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.ServerSocket

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.{Level, LoggerContext}
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import org.cgsuite.kernel.Kernel.logger
import org.cgsuite.lang.CgscriptSystem
import org.slf4j.LoggerFactory

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

  def start(): Unit = {

    logger.info("CGSuite Kernel is starting up.")

    CgscriptSystem.evaluate("0")

    logger.info("Kernel initialized.")

    val out = new ObjectOutputStream(System.out)
    out.flush()
    logger.info("Output stream initialized.")
    val in = new ObjectInputStream(System.in)
    logger.info("Input stream initialized.")

    while (true) {

      val request = in.readObject().asInstanceOf[KernelRequest]
      logger.info("Received request: " + request.input)
      val output = CgscriptSystem.evaluateToOutput(request.input)
      val response = KernelResponse(output)
      logger.info("Writing response: " + response)
      out.writeObject(response)
      out.flush()

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

}
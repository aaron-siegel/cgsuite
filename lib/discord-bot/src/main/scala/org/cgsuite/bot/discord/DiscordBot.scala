package org.cgsuite.bot.discord

import java.awt.image.BufferedImage
import java.awt.{Dimension, Graphics2D}
import java.io.ByteArrayOutputStream
import java.lang.{System => JSystem}

import com.typesafe.scalalogging.Logger
import javax.imageio.ImageIO
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.{Activity, Message, User}
import net.dv8tion.jda.api.events.message.{GenericMessageEvent, MessageReceivedEvent, MessageUpdateEvent}
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.cgsuite.bot.discord.DiscordBot.logger
import org.cgsuite.core.CanonicalShortGame
import org.cgsuite.lang.{ReplUiHarness, System}
import org.cgsuite.output.{Output, StyledTextOutput}
import org.cgsuite.util.UiHarness
import org.slf4j.LoggerFactory

import scala.collection.mutable

/*
 Steps to install on a CentOS server:
 1. Obtain jdk-17_linux-x64.rpm from Oracle
 2. On remote machine: yum localinstall jdk-17_linux-x64.rpm
 3. On remote machine: yum install liberation-sans-fonts
 4. On remote machine: mkdir ~/CGSuite
 5. On dev machine: scripts/deploy-discord-bot.sh
 */

object DiscordBot {

  private val logger = Logger(LoggerFactory.getLogger(classOf[DiscordBot]))

  def main(args: Array[String]): Unit = {

    new DiscordBot(args(0)).start()

  }

}

class DiscordBot(token: String) extends ListenerAdapter {

  def start(): Unit = {

    logger info s"This is the CGSuite Discord Bot, version ${System.version}."

    UiHarness.setUiHarness(ReplUiHarness)

    System.evaluate("0", mutable.AnyRefMap())

    JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
      .addEventListeners(this)
      .setActivity(Activity.listening(">>:help for Help"))
      .build();

    logger info "Initialized."

  }

  override def onMessageReceived(event: MessageReceivedEvent): Unit = {

    processMessageEvent(event, event.getMessage, event.getAuthor)

  }

  override def onMessageUpdate(event: MessageUpdateEvent): Unit = {

    processMessageEvent(event, event.getMessage, event.getAuthor)

  }

  def processMessageEvent(event: GenericMessageEvent, message: Message, author: User): Unit = {

    val content = message.getContentRaw
    if (content startsWith ">>") {

      val command = (content stripPrefix ">>").trim
      if (command.isEmpty)
        return

      logger info s"Received command: $command"

      if (command startsWith ":") {
        processSpecial(event, message, author, command stripPrefix ":")
      } else {

        val start = JSystem.nanoTime
        try {
          val output = System.evaluate(command, mutable.AnyRefMap())
          processOutput(event, message, author, output)
        } catch {
          case exc: Throwable => exc.printStackTrace()
        }
        val totalDuration = JSystem.nanoTime - start
        logger info s"Completed in ${totalDuration / 1000000} ms"

      }

    }

  }

  def processSpecial(event: GenericMessageEvent, message: Message, author: User, str: String): Unit = {

    val response = str match {
      case "help" =>
          s"""Welcome to the EXPERIMENTAL CGSuite Discord bot, version ${System.version}.
             |I should be able to resolve most CGSuite commands. However, I
             |am brand new and have not really been tested; you have been
             |warned. Please report any issues to @asiegel.
             |```>>:help             Print this message
             |>>:version          Print CGSuite and system version info
             |>>command           Any valid CGSuite command; for example:
             |>>*5 + *6           Compute the sum of *5 and *6```
             |""".stripMargin
      case "version" =>
          s"""```CGSuite ${System.version}
             |Java ${java.lang.System.getProperty("java.version")}
             |${java.lang.System.getProperty("os.name")} ${java.lang.System.getProperty("os.version")}
             |Heap memory: ${java.lang.Runtime.getRuntime.maxMemory >> 20} MB
             |CanonicalShortGames recognized: ${CanonicalShortGame.gameCount}```
             |""".stripMargin
      case _ =>
        s"Unknown command: `$str`"
    }
    event.getChannel.sendMessage(response).queue()

  }

  def processOutput(event: GenericMessageEvent, message: Message, author: User, outputs: Vector[Output]): Unit = {

    val command = (message.getContentRaw stripPrefix ">>").trim
    event.getChannel.sendMessage(s"**${author.getName}**: `$command`").queue()

    outputs.foreach { output =>

      val dim = output match {
        case sto: StyledTextOutput =>
          new Dimension(768, sto.getSize(768, 1024, true).height min 512)
        case _ => output.getSize(768)
      }
      val image = new BufferedImage(dim.width + 24, dim.height + 24, BufferedImage.TYPE_INT_ARGB)
      val graphics = image.getGraphics.asInstanceOf[Graphics2D]
      graphics.translate(12, 12)
      output match {
        case sto: StyledTextOutput => sto.paint(graphics, 768, 1024, true)
        case _ => output.paint(graphics, 768)
      }

      val os = new ByteArrayOutputStream()
      ImageIO.write(image, "png", os)

      event.getChannel.sendFile(os.toByteArray, "response.png").queue()

    }

  }

}

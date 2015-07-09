package agilesitesng.wem

import akka.actor._
import java.net.URL

import argonaut.Argonaut._
import ch.qos.logback.classic.LoggerContext

/**
 * Created by msciab on 26/04/15.
 */
object Hub {

  /*
  def createLogger(name: String): Unit = {
    val lf = org.slf4j.LoggerFactory.getILoggerFactory
    val sa = new ch.qos.logback.classic.net.SocketAppender
    sa.setIncludeCallerData(true)
    sa.setRemoteHost("localhost")
    sa.setPort(4560)
    sa.setContext(lf.asInstanceOf[LoggerContext])
    val log = lf.getLogger(name).asInstanceOf[ch.qos.logback.classic.Logger]
    log.addAppender(sa)
    log.setLevel(ch.qos.logback.classic.Level.DEBUG)
    log.setAdditive(false)
  }*/


  import agilesitesng.wem.Protocol._

  def hubActor() = Props[HubActor]

  class HubActor extends Actor with ActorLogging with agilesites.Utils {


    def receive = init

    def init: Receive = {
      case Connect(url, user, pass) =>
         val wem = context.actorOf(Wem.wemActor(url, user, pass))
        log.debug("Connect @{} {}", user, url)
        context.become(running(wem))
        sender ! Status(OK)
    }

    def running(wem: ActorRef): Receive = {
      case Get(request) =>
        val sender = context.sender()
        log.debug("Get {} sender {}", request, sender)
        wem ! Wem.AskGet(sender, request)

      case Delete(request) =>
        val sender = context.sender()
        log.debug("Delete  {} sender {}", request, sender)
        wem ! Wem.AskDelete(sender, request)

      case Post(request, json) =>
        val sender = context.sender()
        if (json.isEmpty) {
          sender ! Protocol.Reply(jString("error: empty post"))
          log.debug("Post with empty body")
        } else {
          log.debug("Post {} sender {} json {}", request, sender, json)
          wem ! Wem.AskPost(sender, request, json.get.nospaces)
        }

      case Put(request, json) =>
        val sender = context.sender()
        if (json.isEmpty) {
          sender ! Protocol.Reply(jString("error: empty post"))
          log.debug("Put with empty body")
        } else {
          log.debug("Put {} sender {}", request, sender)
          wem ! Wem.AskPut(sender, request, json.get.nospaces)
        }

      case Disconnect() =>
        wem ! PoisonPill
        context.become(init)
        sender ! Status(OK)
    }

  }

}

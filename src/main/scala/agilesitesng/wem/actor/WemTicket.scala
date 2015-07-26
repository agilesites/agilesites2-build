package agilesitesng.wem.actor

import agilesitesng.wem.actor.Protocol.{WemMsg,WemGet,WemPut,WemPost,WemDelete}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.io.IO
import net.liftweb.json._
import spray.http._
import spray.httpx.RequestBuilding._

import scala.collection._

/**
 * Created by msciab on 25/04/15.
 */
object WemTicket {

  def actor(ticket: String, url: Option[java.net.URL] = None) = Props(classOf[WemActor], ticket, url)

  class WemActor(ticket: String, url: Option[java.net.URL] = None)
    extends Actor
    with Queuer
    with ActorLogging {

    implicit val system = context.system
    val http = IO(spray.can.Http)
    val jnu = url getOrElse new java.net.URL(context.system.settings.config.getString("akka.sites.url"))

    def receive: Receive = LoggingReceive {

      case WemGet(ref, what) =>
        val uri = s"${jnu.toString}/REST${what}?multiticket=${ticket}"
        val req = Get(Uri(uri)) ~>
          //addHeader("X-CSRF-Token", ticket) ~>
          addHeader("Accept", "application/json")
        log.debug("get {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)

      case WemDelete(ref, what) =>
        val uri = s"${jnu.toString}/REST${what}?multiticket=${ticket}"
        val req = Delete(Uri(uri)) ~>
          //addHeader("X-CSRF-Token", ticket) ~>
          addHeader("Accept", "application/json")
        log.debug("delete {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)

      case WemPost(ref, what, body) =>
        val uri = s"${jnu.toString}/REST${what}?multiticket=${ticket}"
        val req = HttpRequest(method = HttpMethods.POST, uri = uri,
          entity = HttpEntity(ContentTypes.`application/json`, body)) ~>
          addHeader("X-CSRF-Token", ticket) ~>
          addHeader("Accept", "application/json")
        log.debug("post {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)

      case WemPut(ref, what, body) =>
        val uri = s"${jnu.toString}/REST${what}?multiticket=${ticket}"
        val req = HttpRequest(method = HttpMethods.PUT, uri = uri,
          entity = HttpEntity(ContentTypes.`application/json`, body)) ~>
          addHeader("X-CSRF-Token", ticket) ~>
          addHeader("Accept", "application/json") ~>
          addHeader("Content-Type", "application/json")
        log.debug("put {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)
    }

    def waitForHttpReply(ref: ActorRef): Receive = {
      case res: HttpResponse =>
        val body = res.entity.asString
        log.debug("res={}", res.toString)

        ref ! Protocol.Reply(parse(body))
        context.unbecome()
        flushQueue
      case msg: WemMsg =>
        enqueue(msg)
    }

  }

}

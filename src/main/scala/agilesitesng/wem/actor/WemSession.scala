package agilesitesng.wem.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.io.IO
import net.liftweb.json._
import spray.http._
import spray.httpx.RequestBuilding._
import spray.http.Uri

/**
 * Created by msciab on 25/04/15.
 */
object WemSession {

  import Protocol.{WemMsg,WemGet,WemPost,WemPut,WemDelete}

  def actor(session: String, url: Option[java.net.URL] = None) = Props(classOf[WemSessionActor], session, url)

  class WemSessionActor(session: String, url: Option[java.net.URL] = None)
    extends Actor
    with Queuer
    with ActorLogging {

    implicit val system = context.system
    val http = IO(spray.can.Http)
    val jnu = url getOrElse new java.net.URL(context.system.settings.config.getString("akka.sites.url"))

    def receive: Receive = LoggingReceive {

      case WemGet(ref, what) =>
        val uri = s"${jnu.toString}/REST${what}"
        val req = Get(Uri(uri)) ~>
          addHeader("Accept", "application/json") ~>
          addHeader("Cookie", session)

        log.debug("get {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)

      case WemDelete(ref, what) =>
        val uri = s"${jnu.toString}/REST${what}"
        val req = Delete(Uri(uri)) ~>
          addHeader("Accept", "application/json") ~>
          addHeader("Cookie", session)

        log.debug("delete {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)

      case WemPost(ref, what, body) =>
        val uri = s"${jnu.toString}/REST${what}"
        val req = HttpRequest(method = HttpMethods.POST, uri = uri,
          entity = HttpEntity(ContentTypes.`application/json`, body)) ~>
          addHeader("Cookie", session) ~>
          addHeader("Accept", "application/json")

        log.debug("post {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)

      case WemPut(ref, what, body) =>
        val uri = s"${jnu.toString}/REST${what}"
        val req = HttpRequest(method = HttpMethods.PUT, uri = uri,
          entity = HttpEntity(ContentTypes.`application/json`, body)) ~>
          addHeader("Cookie", session) ~>
          addHeader("Accept", "application/json")

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

package agilesitesng.wem

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorLogging, Actor, ActorRef}
import akka.io.IO
import argonaut._, Argonaut._
import com.typesafe.config.ConfigException.Parse
import spray.http.{HttpResponse, FormData, Uri}
import spray.http.Uri.{Path, Host, Authority}
import spray.httpx.RequestBuilding._

import scala.collection._

/**
 * Created by msciab on 25/04/15.
 */
object Wem {

  // protocol
  trait WemMsg

  case class AskGet(ref: ActorRef, url: String) extends WemMsg

  case class AskPut(ref: ActorRef, url: String, json: Json) extends WemMsg

  case class AskDelete(ref: ActorRef, url: String) extends WemMsg

  case class Reply(json: Json)


  def wemActor(url: Option[java.net.URL] = None,
               username: Option[String] = None,
               password: Option[String] = None) = Props(classOf[WemActor], url, username, password)

  class WemActor(url: Option[java.net.URL], username: Option[String], password: Option[String])
    extends Actor with ActorLogging {

    implicit val system = context.system
    val http = IO(spray.can.Http)
    val jnu = url getOrElse new java.net.URL(context.system.settings.config.getString("akka.sites.url"))
    val user = username getOrElse context.system.settings.config.getString("akka.sites.user")
    val pass = password getOrElse system.settings.config.getString("akka.sites.pass")

    def receive = preLogin

    override def preStart {
      log.debug("******* preStart")
      // ask for a ticket

      val host = Authority(Host(jnu.getHost), jnu.getPort)
      val uri = Uri(jnu.getProtocol, authority = host, path = Path("/cas/v1/tickets"))
      http ! Post(uri, FormData(Seq("username" -> user, "password" -> pass)))
    }

    var queue = mutable.Queue.empty[WemMsg]

    def flushQueue {
      while (queue.nonEmpty)
        self ! queue.dequeue
    }

    def preLogin: Receive = {
      // receive a ticket
      case res: HttpResponse =>
        val headers = res.headers
        val location = headers.filter(_.name == "Location").headOption
        log.debug("HttpResponse Location: {} ({})", location, headers.map(_.value))
        if (location.nonEmpty) {
          http ! Post(Uri(location.get.value), FormData(Seq("service" -> "*")))
          log.debug("**** Reply with Location {}", location.get.value)
        } else {
          val ticket = res.entity.asString
          log.debug("**** Reply with Ticket {}", ticket)
          context.become(postLogin(ticket), false)
          flushQueue
        }

      case msg: WemMsg =>
        queue.enqueue(msg)
    }

    def postLogin(ticket: String): Receive = {
      case AskGet(ref, what) =>
        val uri = s"${jnu.toString}/REST${what}?multiticket=${ticket}"
        val req = Get(Uri(uri)) ~>
          //addHeader("X-CSRF-Token", ticket) ~>
          addHeader("Accept", "application/json")
        log.debug("!!!!!!! get {}", req.toString)
        http ! req
        context.become(waitForHttpReply(ref), false)
    }

    def waitForHttpReply(ref: ActorRef): Receive = {
      case res: HttpResponse =>
        val body = res.entity.asString
        log.debug("body={}", body)
        val json = argonaut.Parse.parse(body).getOrElse(jString("error: bad json"))
        ref ! Reply(json)
        context.unbecome()
        flushQueue
      case msg: WemMsg =>
        queue.enqueue(msg)
    }
  }

}

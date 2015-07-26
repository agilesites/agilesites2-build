package agilesitesng.wem.actor

import agilesitesng.wem.actor.Protocol.{Ticket, Connect}
import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import akka.event.LoggingReceive
import akka.io.IO
import spray.http.{HttpResponse, FormData, Uri}
import spray.http.Uri.{Path, Host, Authority}
import spray.httpx.RequestBuilding._

/**
 * Created by msciab on 25/07/15.
 */
object Cas1 {

  def actor() = Props[Cas1Actor]

  class Cas1Actor extends Actor with ActorLogging {

    implicit val system = context.system
    val http = IO(spray.can.Http)
    var requester: ActorRef = null

    def receive = LoggingReceive {
      case Connect(url, username, password, "1") =>

        val jnu = url getOrElse new java.net.URL(context.system.settings.config.getString("akka.sites.url"))
        val user = username getOrElse context.system.settings.config.getString("akka.sites.user")
        val pass = password getOrElse system.settings.config.getString("akka.sites.pass")
        val host = Authority(Host(jnu.getHost), jnu.getPort)
        val uri = Uri(jnu.getProtocol, authority = host, path = Path("/cas/v1/tickets"))
        val data = FormData(Seq("username" -> user, "password" -> pass))
        log.debug(s"sending a message to http ${uri}, ${data}, ${http}")
        http ! Post(uri, data)
        requester = context.sender()

      case res: HttpResponse =>
        val headers = res.headers
        val location = headers.filter(_.name == "Location").headOption
        log.debug("HttpResponse Location: {} ({})", location, headers.map(_.value))
        if (location.nonEmpty) {
          http ! Post(Uri(location.get.value), FormData(Seq("service" -> "*")))
          log.debug("Reply with Location {}", location.get.value)
        } else {
          val ticket = res.entity.asString
          log.debug("Reply with Ticket {}", ticket)
          System.out.println(ticket)
          requester ! Ticket(ticket)
        }

      case wtf => log.debug(wtf.toString)
    }
  }

}

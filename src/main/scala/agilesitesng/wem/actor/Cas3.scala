package agilesitesng.wem.actor

import java.net.URL

import agilesitesng.wem.actor.Protocol.{Session, Connect, Ticket}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.io.IO
import spray.http.Uri.{Query, Authority, Host, Path}
import spray.http.{FormData, HttpResponse, Uri}
import spray.httpx.RequestBuilding._

/**
 * Created by msciab on 25/07/15.
 */
object Cas3 {

  def actor() = Props[Cas3Actor]

  class Cas3Actor extends Actor with ActorLogging {

    implicit val system = context.system
    val http = IO(spray.can.Http)
    var requester: ActorRef = null
    var host: Authority = null
    var protocol: String = null
    var user: String = null
    var pass: String = null
    var path: String = null

    override def preRestart(reason:Throwable, message:Option[Any]){
      super.preRestart(reason, message)
      log.error(reason, "Unhandled exception for message: {}", message)
    }

    def receive = waitForConnect

    def waitForConnect: Receive = LoggingReceive {
      case Connect(url, username, password, "3") =>
        requester = context.sender()

        val jnu = url getOrElse new java.net.URL(context.system.settings.config.getString("akka.sites.url"))
        host = Authority(Host(jnu.getHost), jnu.getPort)
        protocol = jnu.getProtocol
        path = jnu.getPath + "/REST/sites"
        val uri = Uri(protocol, authority = host, path = Path(path))
        user = username getOrElse context.system.settings.config.getString("akka.sites.user")
        pass = password getOrElse context.system.settings.config.getString("akka.sites.pass")

        log.debug(s"sending a message to http ${uri} ")
        http ! Get(uri)
        context.become(step1redirectToCas)
    }

    def step1redirectToCas = LoggingReceive {
      case res: HttpResponse =>
        val location = res.headers.filter(_.name == "Location").headOption
        log.debug("HttpResponse Location: {} ", location)
        if (location.nonEmpty) {
          http ! Get(Uri(location.get.value))
          context.become(step2readForm(location.get.value))
        } else {
          log.warning("step1: no redirect")
          requester ! Session(None)
        }
    }


    def step2readForm(referer: String) = LoggingReceive {
      case res: HttpResponse =>
        val re = """(?s).*form.*action="(.*?)".*name="lt" value="(.*?)".*""".r
        val m = re.findFirstMatchIn(res.entity.asString)
        val c = res.headers.filter(_.name == "Set-Cookie").headOption

        log.debug("step2:")
        if (m.isDefined && c.isDefined) {
          val act = m.get.group(1)
          val lt = m.get.group(2)
          val ck = c.get.value.split(";").head
          log.debug(s"act=${act} lt=${lt} ck=${ck}")
          val data = FormData(Seq("username" -> user, "password" -> pass,
            "lt" -> lt, "_eventId" -> "submit"))
          val surl = protocol+":"+host+act
          log.debug(surl)
          val url = new URL(surl)
          val uri = Uri(protocol, authority = host, path = Path(url.getPath), query= Query(url.getQuery))
          val post = Post(uri, data) ~> addHeader("Cookie", ck) //~> addHeader("Referer", referer)
          http ! post
          context.become(step3requestTicket(ck))
        } else {
          log.debug(c.toString)
          println(res.entity.asString)
          requester ! Session(None)
        }

    }

    def step3requestTicket(session: String) = LoggingReceive {
      case res: HttpResponse =>
        val l = res.headers.filter(_.name == "Location").headOption
        log.debug("step3:")
        if (l.isDefined) {
          log.debug(s"redirect to ${l.get.value}")
          http ! Get(l.get.value)
          context.become(step4getJSessionId)
        } else {
          requester ! Session(None)
        }
    }

    def step4getJSessionId = LoggingReceive {
      case res: HttpResponse =>
        log.debug("step4:")
        val l = res.headers.filter(_.name == "Set-Cookie").headOption
        if (l.isDefined) {
          requester ! Session(l.get.value.split(";").headOption)
        } else {
          requester ! Session(None)
        }
        context.become(waitForConnect)
    }
  }
}


package samples

import akka.actor.{Actor, ActorRef, _}
import akka.io.IO
import spray.http.Uri.{Authority, Host, Path}
import spray.http._
import spray.httpx.RequestBuilding._

/**
 * Created by msciab on 12/04/15.
 */
object Login {

  case class DoLogin(ref: ActorRef, url: Option[String] = None, username: String = "", password: String= "")

  case class AskTicket(ref: ActorRef)

  case class Ticket(token: Option[String])

  // Mock Actor
  class MockLoginActor extends Actor {
    var ticket: Option[String] = None

    def receive = {

      case DoLogin(ref, _, username, password) =>
        if (username == password)
          ticket = Some("12345")
        else
          ticket = None

        ref ! Ticket(ticket)

      case AskTicket(ref: ActorRef) =>
        ref ! Ticket(ticket)
    }
  }

  class DispatchLoginActor extends Actor with ActorLogging {

    import dispatch.Defaults._
    import dispatch._

    var ticket: Option[String] = None

    def receive = {
      case DoLogin(ref, None, _, _) =>
        self ! DoLogin(ref,
          Some(context.system.settings.config.getString("akka.sites.url")),
          context.system.settings.config.getString("akka.sites.user"),
          context.system.settings.config.getString("akka.sites.pass"))

      case DoLogin(ref, Some(url), user, pass) =>
        // build the request
        val uri = new java.net.URL(url)
        val base = host(uri.getHost, uri.getPort)
        val req = base / "cas" / "v1" / "tickets" <<
          Map("username" -> user,
            "password" -> pass)

        // run the request then redirect to the second
        val res = Http(req).apply


        log.debug("**** got first answer")

        // prepare the new request from the redirect authorizing all the services
        val req1 = dispatch.url(res.getHeader("Location")) << Map("service" -> "*")

        // run the request
        val res1 = Http(req1 OK as.String).apply

        log.debug("**** got second answer {}", res1)

        // finally return the resulting ticket
        ticket = Some(res1)
        ref ! Ticket(ticket)

      case AskTicket(ref: ActorRef) =>
        ref ! Ticket(ticket)
    }
  }

  class LoginActor extends Actor with ActorLogging {

    // state
    var ticket = Option.empty[String]
    // in waitForTicket contains the sender
    var requester: ActorRef = null

    // vals
    import spray.http.FormData

    implicit val system = context.system
    val http = IO(spray.can.Http)

    def receive = {
      case DoLogin(ref, None, _, _) =>
        self ! DoLogin(ref,
          Some(context.system.settings.config.getString("akka.sites.url")),
          context.system.settings.config.getString("akka.sites.user"),
          context.system.settings.config.getString("akka.sites.pass"))

      case DoLogin(ref, Some(url), user, pass) =>
        log.debug("DoLogin {}", url)

        val jnu = new java.net.URL(url)
        val host = Authority(Host(jnu.getHost), jnu.getPort)
        val uri = Uri(jnu.getProtocol, authority = host, path = Path("/cas/v1/tickets"))

        requester = ref
        http ! Post(uri, FormData(Seq("username" -> user, "password" -> pass)))
        context.become(waitForTicket)

        //http ! HttpRequest(POST, uri, entity=HttpEntity(FormData(Seq("username" -> user, "password" -> pass))))

      case AskTicket(ref: ActorRef) =>
        ref ! Ticket(ticket)
    }

    def waitForTicket: Receive = {
      case res: HttpResponse =>
        val headers = res.headers
        val location = headers.filter(_.name == "Location").headOption
        log.debug("HttpResponse Location: {} ({})", location, headers.map(_.value))
        if (location.nonEmpty) {
          http ! Post(Uri(location.get.value), FormData(Seq("service" -> "*")))
          log.debug("**** Reply with Location {}", location.get.value)
        } else {
          ticket = Some(res.entity.asString)
          log.debug("**** Reply with Ticket {}", ticket)
          requester ! Ticket(ticket)
          context.become(receive)
        }
    }
  }

}
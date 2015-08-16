package agilesitesng.deploy.actor

import java.net.URL
import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import akka.io.IO
import spray.http.HttpHeaders.{Cookie, `Set-Cookie`}
import spray.http.{HttpResponse, Uri}
import spray.http.Uri.{Query, Path, Host, Authority}
import spray.httpx.RequestBuilding._

/**
 * Created by msciab on 04/08/15.
 */
object Services {

  import DeployProtocol._

  def actor() = Props[ServicesActor]

  class ServicesActor
    extends Actor
    with ActorLogging
    with ActorUtils {

    implicit val system = context.system

    val http = IO(spray.can.Http)

    def receive: Receive = preLogin(None, None,Cookie(Seq()), None)

    // build a get request
    def buildGet(op: String, params: Tuple2[String, String]*)(url: URL, cookie: Cookie) = buildGetMap(op, params.toMap)(url, cookie)

    // build a get request out of a map
    def buildGetMap(op: String, params: Map[String, String])
                   (url: URL, cookie: Cookie) = {

      val uri = Uri(url.getProtocol,
        authority = Authority(Host(url.getHost), url.getPort),
        path = Path(url.getPath + "/ContentServer"),
        query = Query(params + ("pagename" -> "AAAgileService") + ("op" -> op)))

      val req = Get(uri) ~> addHeaders(cookie)
      log.debug(s"buildGet=${req.toString}")
      req
    }

    def preLogin(origin: Option[ActorRef],
                 url: Option[URL],
                 cookie: Cookie,
                 authKey: Option[String]): Receive = LoggingReceive {

      case Ask(origin, ServiceLogin(url, username, password)) =>
        log.debug(s"${username} -> ${url}")
        http ! buildGet("login", "username" -> username, "password" -> password)(url, cookie)
        context.become(preLogin(Some(origin), Some(url), cookie, None))

      case res: HttpResponse =>
        val body = res.entity.asString.trim
        val headers = res.headers
        log.debug(s"body: ${body} headers: ${headers}")

        if (cookie.cookies.isEmpty) { // no cookie waiting for Set-Cookie
          // get Seq[HttpHeader] for Set-Cookie
          val cookies = headers.filter(_.isInstanceOf[`Set-Cookie`]).map(_.asInstanceOf[`Set-Cookie`].cookie).toSeq
          if (cookies.isEmpty || !body.equals("0")) {
            origin.get ! ServiceReply(s"KO code=${body} cookies=${cookies.mkString(";")}")
            context.unbecome()
          } else {
            val ncookie = Cookie(cookies)
            http ! buildGet("authkey")(url.get,ncookie)
            context.become(preLogin(origin, url, ncookie, None))
          }
        } else { // got cookie, lookign for authkey
          val authKey = body
          origin.get ! ServiceReply(s"OK ${authKey}")
          context.become(postLogin(url.get, cookie, authKey))
          flushQueue
        }
      case msg: Object => enqueue(msg)
    }

    def postLogin(url: URL, cookie: Cookie, authKey: String): Receive = LoggingReceive {
      case ServiceGet(op: String, args: Map[String, String]) =>
        http ! buildGetMap(op, args)(url, cookie)
        context.become(waitForReply(sender))
    }

    def waitForReply(requester: ActorRef) = LoggingReceive {
      case res: HttpResponse =>
        val body = res.entity.asString
        requester ! ServiceReply(body)
        context.unbecome()
        flushQueue
      case msg: Object => enqueue(msg)
    }

  }

}



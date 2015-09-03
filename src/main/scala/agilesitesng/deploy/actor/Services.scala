package agilesitesng.deploy.actor

import java.net.URL
import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import akka.io.IO
import spray.http.HttpHeaders.{Cookie, `Set-Cookie`}
import spray.http.{FormData, HttpResponse, Uri}
import spray.http.Uri.{Query, Path, Host, Authority}
import spray.httpx.RequestBuilding._

import scala.concurrent.Await
import scala.util.{Failure, Success}
import akka.pattern.ask

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

    def receive: Receive = preLogin(None, None, Cookie(Seq()), None)

    // build a get request
    def buildGet(op: String, params: Tuple2[String, String]*)
                (url: URL, cookie: Cookie) = buildGetMap(op, params.toMap)(url, cookie)

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

    def buildPostMap(op: String, params: Map[String, String])
                    (url: URL, cookie: Cookie, authKey: String) = {
      val uri = Uri(url.getProtocol,
        authority = Authority(Host(url.getHost), url.getPort),
        path = Path(url.getPath + "/ContentServer"))
      val data = params.toSeq ++
        Seq("pagename" -> "AAAgileService", "op" -> op, "_authkey_" -> authKey)
      val req = Post(uri, FormData(data)) ~> addHeaders(cookie)
      log.debug(s"buildPost=${data} ${cookie}")
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

        if (cookie.cookies.isEmpty) {
          // no cookie waiting for Set-Cookie
          // get Seq[HttpHeader] for Set-Cookie
          val cookies = headers.filter(_.isInstanceOf[`Set-Cookie`]).map(_.asInstanceOf[`Set-Cookie`].cookie).toSeq
          if (cookies.isEmpty || !body.equals("0")) {
            origin.get ! ServiceReply(s"KO code=${body} cookies=${cookies.mkString(";")}")
            context.unbecome()
          } else {
            val ncookie = Cookie(cookies)
            http ! buildGet("authkey")(url.get, ncookie)
            context.become(preLogin(origin, url, ncookie, None))
          }
        } else {
          // got cookie, lookign for authkey
          val authKey = body
          origin.get ! ServiceReply(s"OK ${authKey}")
          context.become(postLogin(url.get, cookie, authKey))
          flushQueue
        }
      case msg: Object => enqueue(msg)
    }

    def postLogin(url: URL, cookie: Cookie, authKey: String): Receive = LoggingReceive {

      case Ask(origin, ServiceLogin(url, username, password)) =>
        origin ! ServiceReply("OK - already logged in")

      // send a get request wait for an answer
      case ServiceGet(args) =>
        val op = args("op")
        if (op.isEmpty) {
          sender ! ServiceReply("ERROR: missing op")
        } else {
          val msg = buildGetMap(op, args - "op")(url, cookie)
          val origin = context.sender()

          Await.result(http ? msg, defaultDuration) match {
            case res: HttpResponse =>
              val body = res.entity.asString
              origin ! ServiceReply(body)
            case etc =>
              origin ! ServiceReply(s"ERROR: ${etc.toString}")
              throw new Exception("restarting")
          }
        }

      // send a post request wait for an answer
      case ServicePost(args) =>
        val op = args("op")
        if (op.isEmpty) {
          sender ! ServiceReply("ERROR: missing op")
        } else {
          val msg = buildPostMap(op, args - "op")(url, cookie, authKey)
          val origin = context.sender()
          Await.result(http ? msg, defaultDuration) match {
            case res: HttpResponse =>
              val body = res.entity.asString
              origin ! ServiceReply(body)
            case etc =>
              origin ! ServiceReply(s"ERROR: ${etc.toString}")
          }
        }
    }
  }

}



package samples

import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import spray.http.HttpMethods._
import spray.http._
import scala.concurrent.duration._

/**
 * Created by msciab on 14/04/15.
 */
object Ip {

  implicit val timeout: Timeout = Timeout(15.seconds)

  case class IpInit(http: ActorRef)
  case class IpRequest(requester: ActorRef)
  case class IpResponse(ip: String)

  class IpActor extends Actor {

    var http: ActorRef = null

    def receive = {
      case IpInit(ref) =>
        http = ref

      case IpRequest(requester) =>
        context.become(waitForAnswer(requester))
        http ! HttpRequest(GET, Uri("http://ip.jsontest.com"))
    }

    def waitForAnswer(requester: ActorRef) : Receive = {
      case res: HttpResponse =>
        requester ! IpResponse(res.entity.asString)
        context.become(receive)
    }
  }

}

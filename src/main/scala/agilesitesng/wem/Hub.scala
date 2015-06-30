package agilesitesng.wem

import akka.actor._
import java.net.URL

/**
 * Created by msciab on 26/04/15.
 */
object Hub {

  case class Init(url: Option[URL] = None, username: Option[String ]= None, password: Option[String] = None)
  case class Finish()

  case class Get(request: String)

  def hubActor() = Props[HubActor]

  class HubActor extends Actor with ActorLogging {

    import Wem._

    def receive = init

    def init: Receive = {
      case Init(url, user, pass) =>
        println(s"!!! init ${url}, ${user}, ${pass}")
        val wem = context.actorOf(wemActor(url, user, pass))
        context.become(running(wem))
        log.debug("**** Init {}", sender)
        sender ! "OK"
    }

    def running(wem: ActorRef): Receive = {
      case Get(request) =>
        println("Get "+request)
        val sender = context.sender()
        log.debug("**** Get {} sender {}", request, sender)
        wem ! AskGet(sender, request)
      case Finish() =>
        wem ! PoisonPill
        context.become(init)
        sender ! "OK"
    }


  }
}

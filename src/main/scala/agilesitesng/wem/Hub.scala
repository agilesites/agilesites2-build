package agilesitesng.wem

import akka.actor._
import net.liftweb.json._

/**
 * Created by msciab on 26/04/15.
 */
object Hub {

  import agilesitesng.wem.Protocol._

  def hubActor() = Props[HubActor]

  class HubActor extends Actor with ActorLogging with agilesites.Utils {

    def receive = init

    def init: Receive = {
      case Connect(url, user, pass) =>
         val wem = context.actorOf(Wem.wemActor(url, user, pass))
        log.debug("Connect @{} {}", user, url)
        context.become(running(wem))
        sender ! Status(OK)
    }

    def running(wem: ActorRef): Receive = {
      case Get(request) =>
        val sender = context.sender()
        log.debug("Get {} sender {}", request, sender)
        wem ! Wem.AskGet(sender, request)

      case Delete(request) =>
        val sender = context.sender()
        log.debug("Delete  {} sender {}", request, sender)
        wem ! Wem.AskDelete(sender, request)

      case Post(request, json) =>
        val sender = context.sender()
        val sjson = pretty(render(json))

        if (sjson.isEmpty) {
          sender ! Protocol.Reply(JString("error: empty post"))
          log.debug("Post with empty body")
        } else {

          log.debug("Post {} sender {} json {}", request, sender, json)
          wem ! Wem.AskPost(sender, request, sjson)
        }

      case Put(request, json) =>
        val sender = context.sender()
        val sjson = pretty(render(json))
        if (sjson.isEmpty) {
          sender ! Protocol.Reply(JString("error: empty post"))
          log.debug("Put with empty body")
        } else {
          log.debug("Put {} sender {}", request, sender)
          wem ! Wem.AskPut(sender, request, sjson)
        }

      case Disconnect() =>
        wem ! PoisonPill
        context.become(init)
        sender ! Status(OK)

      case Annotation(ann) =>
        println(" !!!! "+ann+ "!!!!")
        log.debug("!!!!"+ann)
    }

  }

}

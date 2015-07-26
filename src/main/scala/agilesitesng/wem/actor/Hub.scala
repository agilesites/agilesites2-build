package agilesitesng.wem.actor

import agilesitesng.wem.actor.Cas1.Cas1Actor
import agilesitesng.wem.actor.Protocol.Connect
import akka.actor._
import akka.event.LoggingReceive
import net.liftweb.json._

import scala.collection.mutable

/**
 * Created by msciab on 26/04/15.
 */
object Hub {


  def actor() = Props[HubActor]

  class HubActor extends Actor with ActorLogging with Queuer with agilesites.Utils {

    import Protocol._

    var url: Option[java.net.URL] = None

    val cas1Actor = context.actorOf(Cas1.actor())

    val cas3Actor = context.actorOf(Cas3.actor())

    def receive = preLogin


    def preLogin: Receive = LoggingReceive {
      case msg@Connect(url, user, pass, "1") =>
        this.url = url
        cas1Actor ! msg
        context.become(waitForId(sender))

      case msg@Connect(url, user, pass, "3") =>
        this.url = url
        cas3Actor ! msg
        context.become(waitForId(sender))
    }

    def waitForId(requestor: ActorRef) = LoggingReceive {
      case Ticket(ticket) =>
        log.info("creating a WemTicket actor")
        val wem = context.actorOf(WemTicket.actor(ticket, url))
        context.become(running(wem))
        requestor ! Status(OK)
        flushQueue

      case Session(Some(session)) =>
        log.info("creating a WemSession actor")
        val wem = context.actorOf(WemSession.actor(session, url))
        context.become(running(wem))
        requestor ! Status(OK)
        flushQueue

      case msg: Message => enqueue(msg)
    }


    def running(wem: ActorRef): Receive = LoggingReceive {
      case Get(request) =>
        val sender = context.sender()
        wem ! WemGet(sender, request)

      case Delete(request) =>
        val sender = context.sender()
        wem ! WemDelete(sender, request)

      case Post(request, json) =>
        val sender = context.sender()
        val sjson = pretty(render(json))

        if (sjson.isEmpty) {
          sender ! Protocol.Reply(JString("error: empty post"))
          log.debug("Post with empty body")
        } else {
          log.debug("Post {} sender {} json {}", request, sender, json)
          wem ! WemPost(sender, request, sjson)
        }

      case Put(request, json) =>
        val sender = context.sender()
        val sjson = pretty(render(json))
        if (sjson.isEmpty) {
          sender ! Protocol.Reply(JString("error: empty post"))
          log.debug("Put with empty body")
        } else {
          log.debug("Put {} sender {}", request, sender)
          wem ! WemPut(sender, request, sjson)
        }

      case Disconnect() =>
        wem ! PoisonPill
        context.become(preLogin)
        sender ! Status(OK)

      case Annotation(ann) =>
        println(" !!!! " + ann + "!!!!")
        log.debug("!!!!" + ann)
    }

  }

}

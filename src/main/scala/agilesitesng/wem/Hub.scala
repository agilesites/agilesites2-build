package agilesitesng.wem

import akka.actor.Actor.Receive
import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import argonaut.Json

/**
 * Created by msciab on 26/04/15.
 */
object Hub {

  case class Init()
  case class Get(request: String)

  class HubActor extends Actor with ActorLogging {

    import Wem._

    def receive = init

    def init: Receive = {
      case Init() =>
        val wem = context.actorOf(wemActor())
        context.become(running(wem))
        log.debug("**** Init {}", sender)

        sender ! "OK"
    }

    def running(wem: ActorRef): Receive = {
      case Get(request) =>
        //println("Get "+request)
        val sender = context.sender()
        log.debug("**** Get {} sender {}", request, sender)
        wem ! AskGet(sender, request)
    }


  }
}

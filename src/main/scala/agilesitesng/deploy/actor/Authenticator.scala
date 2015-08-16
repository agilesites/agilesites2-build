package agilesitesng.deploy.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import akka.io.IO

/**
 * Created by msciab on 04/08/15.
 */
object Authenticator {

  def actor() = Props[AuthenticatorActor]

  class AuthenticatorActor extends Actor with ActorLogging {

    implicit val system = context.system
    val http = IO(spray.can.Http)
    var requester: ActorRef = null

    def receive = LoggingReceive {
      case any => println(any)
    }
  }

}

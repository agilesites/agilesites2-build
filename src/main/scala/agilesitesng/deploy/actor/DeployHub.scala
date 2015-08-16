package agilesitesng.deploy.actor

import akka.actor.{ActorLogging, ActorRef, Actor, Props}
import akka.event.LoggingReceive

/**
 * Created by msciab on 05/08/15.
 */
object DeployHub {

  import DeployProtocol._

  def actor() = Props[DeployHubActor]

  class DeployHubActor
    extends Actor
    with ActorLogging
    with ActorUtils {

    def receive = loop(null, null)

    def loop(services: ActorRef, spoon: ActorRef): Receive = LoggingReceive {
      case msg@ServiceLogin(url, user, pass) =>
        val svc = context.actorOf(Services.actor())
        svc ! Ask(context.sender, msg)
        val spoon = context.actorOf(Spoon.actor(services))
        context.become(loop(services, spoon))
      case spm: SpoonMsg with Asking => spoon ! Ask(context.sender, spm)
      case spm: SpoonMsg => spoon ! spm
      case svm: ServiceMsg with Asking => services ! Ask(context.sender, svm)
    }

  }

}

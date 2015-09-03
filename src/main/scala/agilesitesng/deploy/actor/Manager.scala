package agilesitesng.deploy.actor

import java.net.URL

import akka.actor._
import akka.event.LoggingReceive

/**
 * The manager actor, creates Collector and Services, supervise services
 * Created by msciab on 05/08/15.
 */
object Manager {

  import DeployProtocol._

  def actor(url: URL, user: String, pass: String) = Props(classOf[ManagerActor], url, user, pass)

  class ManagerActor(url: URL, user: String, pass: String)
    extends Actor
    with ActorLogging
    with ActorUtils {

    var services: ActorRef = context.actorOf(Services.actor())
    val collector = context.actorOf(Collector.actor(services))

    override def preStart: Unit = {
      services ! Ask(self, ServiceLogin(url, user, pass))
      context.watch(services)
    }

    def receive = LoggingReceive {
      case Terminated(_) =>
        context.actorOf(Services.actor())
        preStart
      case ServiceReply(ticket) => println(s"logged in with ${ticket}")
      case spm: SpoonMsg with Asking => collector ! Ask(context.sender, spm)
      case spm: SpoonMsg => collector ! spm
      case svm: ServiceMsg with Asking => services ! Ask(context.sender, svm)
    }
  }

}

package agilesitesng.deploy.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive

/**
 * Created by msciab on 04/08/15.
 */
object Deployer {

  import Protocol._

  def actor() = Props[DeployerActor]

  class DeployerActor extends Actor with ActorLogging {

    implicit val system = context.system

    def receive = LoggingReceive {
      case Login(url, username, password) =>
        log.debug("Login!")
        println(">>> Login")


      case Deploy(file: String) =>
        log.debug(s"deploying ${file}")
        println(s">>> ${file}")
    }
  }

}

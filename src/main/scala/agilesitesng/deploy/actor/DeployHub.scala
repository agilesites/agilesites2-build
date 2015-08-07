package agilesitesng.deploy.actor

import akka.actor.{Actor, Props}

/**
 * Created by msciab on 05/08/15.
 */
object DeployHub {
  import Protocol._

  def actor() = Props[DeployHubActor]

  class DeployHubActor extends Actor {

    val deployer = context.actorOf(Deployer.actor())

    val spoon = context.actorOf(Spoon.actor())

    def receive = {
      case dm: DeployMsg with Asking =>  deployer ! Ask(context.sender, dm)
      case sm: SpoonMsg with Asking =>  spoon ! Ask(context.sender, sm)
      case dm: DeployMsg => deployer ! dm
      case sm: SpoonMsg  => spoon ! sm
    }

  }

}

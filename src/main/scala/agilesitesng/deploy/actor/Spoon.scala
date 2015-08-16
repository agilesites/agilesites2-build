package agilesitesng.deploy.actor

import java.io.File

import agilesitesng.deploy.model.SpoonModel
import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import akka.event.LoggingReceive
import spoon.Launcher
import scala.collection.{mutable, JavaConversions}

/**
 * Created by msciab on 05/08/15.
 */
object Spoon {

  import DeployProtocol._
  import JavaConversions._

  def actor(deployer: ActorRef) = Props(classOf[SpoonActor], deployer)

  class SpoonActor(deployer: ActorRef) extends Actor with ActorLogging {

    var count = 0

    def receive: Receive = config

    def config: Receive = LoggingReceive {
      case SpoonInit() =>
        println("--- spooon init ---")
        count = 0
        context.become(process)
    }

    def process: Receive = LoggingReceive {

      case SpoonData(model) =>
        println("--- spoon received " + model+ " ---")
        count = count +1

      case Ask(sender, SpoonRun(args)) =>
        sender ! SpoonReply(s"messages: ${count}")
        context.become(config)
    }
  }

}

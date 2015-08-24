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
object Collector {

  import DeployProtocol._

  //import JavaConversions._

  def actor(services: ActorRef) = Props(classOf[CollectorActor], services)

  class CollectorActor(services: ActorRef)
    extends Actor
    with ActorLogging
    with ActorUtils {

    var count = 0
    var answers = List.empty[String]

    def receive: Receive = config

    def config: Receive = LoggingReceive {
      case SpoonInit() =>
        println(">>> collector init")
        count = 0
        context.become(sending)
    }

    def sending: Receive = LoggingReceive {

      case SpoonData(model) =>
        val map = Decoder(model)
        println(s">>> collector sending ${map} ---")

        services ! ServicePost(map)
        count = count +1

      case Ask(origin, SpoonRun(args)) =>
        println(">>> collector run ---")
        context.become(replying(origin))
        flushQueue

      case etc: Object => enqueue(etc)
    }


    def replying(origin: ActorRef): Receive = LoggingReceive {
      case ServiceReply(msg) =>
        println(s"<<< collector reply #${count}")
        count = count - 1
        answers = msg :: answers

        if (count == 0) {
          origin ! SpoonReply(answers.reverse.mkString("\n-----\n"))
          answers = List.empty
          context.become(config)
          flushQueue
        }
      case etc: Object => enqueue(etc)
    }
  }

}

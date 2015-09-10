package agilesitesng.deploy.actor

import java.io.File
import java.net.URL

import spoon.Launcher
import agilesitesng.deploy.model.SpoonModel

import akka.actor._
import akka.event.LoggingReceive
import scala.collection.{mutable, JavaConversions}

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

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
    var decoder: Decoder = null

    def receive: Receive = config

    def config: Receive = LoggingReceive {
      case SpoonBegin(url: URL, site: String, user: String, pass: String) =>
        println(s">>> collector begin: ${url}")
        decoder = new Decoder(site, user, pass)
        count = 0
        context.become(sending(sender()))
        flushQueue

      case obj: Object => enqueue(obj)
    }

    def sending(origin: ActorRef): Receive = LoggingReceive {

      case SpoonData(model) =>
        val map = decoder(model)
        println(s">>> collector data: ${map} ---")
        services ! ServicePost(map)
        count = count +1

      case Ask(origin, SpoonEnd(args)) =>
        println(">>> collector end ---")
        //context.become(replying(origin))
        flushQueue

      case ServiceReply(msg) =>
        println(s"<<< collector reply #$count")
        origin ! SpoonReply(answers.reverse.mkString("\n-----\n"))

      case etc: Object => enqueue(etc)
    }


 /*   def replying(origin: ActorRef): Receive = LoggingReceive {
      case ServiceReply(msg) =>
        println(s"<<< collector reply #${count}")
        origin ! SpoonReply(answers.reverse.mkString("\n-----\n"))
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
*/  }

}

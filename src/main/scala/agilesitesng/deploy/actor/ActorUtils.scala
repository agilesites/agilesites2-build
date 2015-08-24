package agilesitesng.deploy.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import akka.util.Timeout
import scala.concurrent.duration._

import scala.collection.mutable
import scala.util.Failure
import scala.util.Success

/**
 * Created by msciab on 25/07/15.
 */
trait ActorUtils {
  this: Actor with ActorLogging =>


  // to queue not needed messages
  var queue = mutable.Queue.empty[Object]

  def flushQueue {
    while (queue.nonEmpty)
      self ! queue.dequeue
  }

  def enqueue(msg: Object) {
    queue.enqueue(msg)
  }

  def queueObject: Receive = {
    case msg: Object => enqueue(msg)
  }

  // to log exceptions
  override def preRestart(reason: Throwable, message: Option[Any]) {
    reason.printStackTrace()
    log.error(reason, "Unhandled exception for message: {}", message)
  }

  // default durations and timeout
  val defaultDuration = 5.seconds
  implicit val defaultTimeout = akka.util.Timeout.durationToTimeout(defaultDuration)

}

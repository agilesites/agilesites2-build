package agilesitesng.deploy.actor

import akka.actor.{ActorLogging, Actor}

import scala.collection.mutable

/**
 * Created by msciab on 25/07/15.
 */
trait ActorUtils {
  this: Actor with ActorLogging =>

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

  override def preRestart(reason: Throwable, message: Option[Any]) {
    reason.printStackTrace()
    log.error(reason, "Unhandled exception for message: {}", message)
  }
}

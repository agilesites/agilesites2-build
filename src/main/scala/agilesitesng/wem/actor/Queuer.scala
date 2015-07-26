package agilesitesng.wem.actor

import agilesitesng.wem.actor.Protocol.Message
import akka.actor.Actor

import scala.collection.mutable

/**
 * Created by msciab on 25/07/15.
 */
trait Queuer {
  this: Actor =>

  var queue = mutable.Queue.empty[Object]

  def flushQueue {
    while (queue.nonEmpty)
      self ! queue.dequeue
  }

  def enqueue(msg: Object) { queue.enqueue(msg)}
}

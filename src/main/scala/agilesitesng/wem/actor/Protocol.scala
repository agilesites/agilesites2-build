package agilesitesng.wem.actor

import java.net.URL

import akka.actor.ActorRef
import net.liftweb.json.JValue

/**
 * Created by msciab on 01/07/15.
 */
object Protocol extends Enumeration {

  /**
   * Error codes
   */
  type Protocol = Value

  val OK, Crash = Value

  /**
   * Classification
   */

  trait Message

  trait Control extends Message

  trait IO extends Message

  trait Source extends Message

  trait WemMsg extends Message


  /**
   * Protocol control
   */

  case class Connect(url: Option[URL] = None,
                     username: Option[String] = None,
                     password: Option[String] = None,
                     casVersion: String = "1") extends Control


  case class Ticket(ticket: String) extends Control

  case class Session(id: Option[String]) extends Control

  case class Status(code: Protocol, msg: String = "") extends Control

  case class Disconnect() extends Control

  /**
   * High Level requests to Hub
   */
  case class Get(request: String) extends IO

  case class Post(request: String, json: JValue) extends IO

  case class Put(request: String, json: JValue) extends IO

  case class Delete(request: String) extends IO

  case class Reply(json: JValue) extends IO

  /**
   * Actual  Wem messages to Wem actors
   *
   */

  case class WemGet(ref: ActorRef, url: String) extends WemMsg

  case class WemPut(ref: ActorRef, url: String, json: String) extends WemMsg

  case class WemPost(ref: ActorRef, url: String, json: String) extends WemMsg

  case class WemDelete(ref: ActorRef, url: String) extends WemMsg


  /**
   * Annotation processor
   */
  case class Annotation(message: String)


}

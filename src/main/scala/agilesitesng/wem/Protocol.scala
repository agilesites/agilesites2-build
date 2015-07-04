package agilesitesng.wem

import java.net.URL

import argonaut.Json

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


  /**
   * Protocol control
   */

  case class Connect(url: Option[URL] = None,
                     username: Option[String] = None,
                     password: Option[String] = None) extends Control

  case class Status(code: Protocol, msg: String = "") extends Control

  case class Disconnect() extends Control

  /**
   * Protocol I/O
   */
  case class Get(request: String) extends IO

  case class Post(request: String, json: Option[Json]) extends IO

  case class Put(request: String, json: Option[Json]) extends IO

  case class Delete(request: String) extends IO

  case class Reply(json: Json) extends IO

}

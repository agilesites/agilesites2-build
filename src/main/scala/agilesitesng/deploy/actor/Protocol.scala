package agilesitesng.deploy.actor

import java.io.File
import java.net.URL

import akka.actor.ActorRef

/**
 * Created by msciab on 04/08/15.
 */
object Protocol {

  trait Msg

  trait Asking

  trait DeployMsg extends Msg

  trait SpoonMsg extends Msg

  case class Ask(sender: ActorRef, message: Msg)

  case class Login(url: URL, username: String, password: String) extends DeployMsg

  case class AuthId(authid: String) extends DeployMsg

  case class Deploy(filename: String) extends DeployMsg

  case class SpoonInit(source: String, target: String, classpath: Seq[String]) extends SpoonMsg

  case class SpoonRun(args: Seq[String]) extends SpoonMsg with Asking

  case class SpoonReply(result: String) extends SpoonMsg

}

package agilesitesng.deploy.actor

import java.io.File
import java.net.URL

import agilesitesng.deploy.model.SpoonModel
import akka.actor.ActorRef

/**
 * Created by msciab on 04/08/15.
 */
object Protocol {

  trait Msg

  trait Asking

  trait DeployMsg extends Msg

  trait SpoonMsg extends Msg

  case class Ask(sender: ActorRef, message: Msg) extends Msg

  case class Login(url: URL, username: String, password: String) extends DeployMsg

  case class AuthId(authid: String) extends DeployMsg

  case class Deploy(filename: String) extends DeployMsg

  case class SpoonInit() extends SpoonMsg

  case class SpoonData(model: SpoonModel) extends SpoonMsg

  case class SpoonRun(args: String) extends SpoonMsg with Asking

  case class SpoonReply(result: String) extends SpoonMsg

}

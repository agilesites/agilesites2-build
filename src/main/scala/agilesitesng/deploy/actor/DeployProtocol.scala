package agilesitesng.deploy.actor

import java.io.File
import java.net.URL

import agilesitesng.deploy.model.SpoonModel
import akka.actor.ActorRef

/**
 * Created by msciab on 04/08/15.
 */
object DeployProtocol {

  trait Msg

  trait Asking

  trait ServiceMsg extends Msg

  trait SpoonMsg extends Msg

  case class Ask(sender: ActorRef, message: Msg) extends Msg

  case class SpoonBegin(url: URL, site: String, user: String, pass: String) extends SpoonMsg

  case class SpoonData(model: SpoonModel) extends SpoonMsg

  case class SpoonEnd(args: String) extends SpoonMsg with Asking

  case class SpoonReply(result: String) extends SpoonMsg

  case class ServiceLogin(url: URL, username: String, password: String) extends ServiceMsg

  case class ServiceGet(args: Map[String, String]) extends ServiceMsg with Asking

  case class ServicePost(args: Map[String, String]) extends ServiceMsg with Asking

  case class ServiceReply(result: String) extends ServiceMsg

}

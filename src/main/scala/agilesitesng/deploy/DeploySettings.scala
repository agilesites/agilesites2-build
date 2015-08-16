package agilesitesng.deploy

import agilesites.config.AgileSitesConfigKeys._
import agilesitesng.Utils
import agilesitesng.deploy.actor.DeployProtocol._
import agilesitesng.deploy.model.Spooler
import akka.pattern.ask
import akka.util.Timeout
import sbt.{AutoPlugin, _}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by msciab on 04/08/15.
 */
trait DeploySettings {
  this: AutoPlugin with Utils =>

  import NgDeployKeys._

  implicit val timeout = Timeout(10.seconds)

  val deployTask = deploy in ng := {

    val hub = ngDeployHub.value
    hub ! ServiceLogin(url(sitesUrl.value), sitesUser.value, sitesPassword.value)
    val spool = (spoon in ng).toTask("").value

    hub ! SpoonInit()
    val deployObjects = Spooler.load(readFile(spool))
    for (dobj <- deployObjects.deployObjects) {
      //println(s" sending ${dobj}")
      hub ! SpoonData(dobj)
    }
    val req = hub ? SpoonRun("")
    val SpoonReply(result) = Await.result(req, 10.seconds)

    println(s"result=${result}")

  }

  val serviceTask = service in ng := {
    val args = Def.spaceDelimited("<service> <args...>").parsed
    val hub = ngDeployHub.value

  }


  def deploySettings = Seq(deployTask)

}

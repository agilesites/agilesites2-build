package agilesitesng.deploy

import java.io.File

import _root_.spoon.Launcher
import agilesites.setup.AgileSitesSetupKeys._
import agilesitesng.Utils
import agilesitesng.deploy.model.Spooler
import akka.util.Timeout
import sbt._
import sbt.Keys._
import sbt.AutoPlugin
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import agilesitesng.deploy.actor.Protocol._
import agilesites.config.AgileSitesConfigKeys._

/**
 * Created by msciab on 04/08/15.
 */
trait DeploySettings {
  this: AutoPlugin with Utils =>

  import NgDeployKeys._

  implicit val timeout = Timeout(10.seconds)

  val deployTask = deploy in ng := {
    val hub = ngDeployHub.value
    hub ! Login(url(sitesUrl.value), sitesUser.value, sitesPassword.value)

    val spool = (spoon in ng).toTask("").value

    hub ! SpoonInit()
    val deployObjects = Spooler.load(readFile(spool))
    for (dobj <- deployObjects.deployObjects)
      hub ! SpoonData(dobj)

    val req = hub ? SpoonRun("")
    val SpoonReply(result) = Await.result(req, 10.seconds)

    println(s"result=${result}")

  }

  def deploySettings = Seq(deployTask)

}

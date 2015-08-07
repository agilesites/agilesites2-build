package agilesitesng.deploy

import java.io.File

import _root_.spoon.Launcher
import agilesites.setup.AgileSitesSetupKeys._
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
  this: AutoPlugin =>

  import NgDeployKeys._

  implicit val timeout = Timeout(10.seconds)

  val deployTask = deploy in ng := {
    val hub = ngDeployHub.value
    hub ! Login(url(sitesUrl.value), sitesUser.value, sitesPassword.value)
    val files = baseDirectory.value / "src" / "main" / "java" ** "*.java"
    for (file <- files.get) {
      println(file)
      hub ! Deploy(file.getAbsolutePath)
    }
  }

  /* tentativo di usare la Analysis
   val analysis = (compile in Compile).value
   println(analysis)
   val lastCompDate: Long = analysis.compilations.allCompilations.headOption.map(_.startTime()).getOrElse(0)
   println(lastCompDate)
   val modified = analysis.relations.allProducts.filter(x => x.lastModified > lastCompDate)
   modified.foreach(println)
   */


  def deploySettings = Seq(deployTask)

}

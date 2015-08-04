package agilesitesng.deploy

import agilesites.config.AgileSitesConfigPlugin
import agilesitesng.deploy.actor.Protocol.Deploy
import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys
import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

import scala.concurrent.duration._

object NgDeployPlugin
  extends AutoPlugin
  with ActorSettings
  with DeploySettings {

  override def requires = JvmPlugin && SbtWeb && AgileSitesConfigPlugin

  val autoImport = NgDeployKeys


  override def globalSettings: Seq[Setting[_]] = super.globalSettings ++ actorGlobalSettings

  override lazy val projectSettings = actorSettings ++ deploySettings
}
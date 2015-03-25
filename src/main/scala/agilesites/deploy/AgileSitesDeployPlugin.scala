package agilesites.deploy

import sbt._
import agilesites.config.AgileSitesConfigPlugin

object AgileSitesDeployPlugin
  extends AutoPlugin
  with DeploySettings
  with ScaffoldSettings {

  override def requires = AgileSitesConfigPlugin

  object autoImport {
    // upload target
    val asPackage = taskKey[Unit]("AgileSites package jar")
    val asPackageTarget = settingKey[Option[String]]("AgileSites Deploy Target")
    val asCopyStatics = taskKey[Unit]("AgileSites package jar")
    val asDeploy = taskKey[Unit]("Sites deploy")
  }

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.deploy.AgileSitesDeployPlugin.autoImport._

  override lazy val projectSettings = deploySettings

}
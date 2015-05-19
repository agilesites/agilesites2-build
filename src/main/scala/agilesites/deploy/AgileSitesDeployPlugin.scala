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
    val asPackageTarget = settingKey[Option[String]]("AgileSites deploy target")
    val asCopyStatics = taskKey[Unit]("AgileSites copy statics")
    val asDeploy = taskKey[Unit]("AgileSites deploy")
    val asUpload = taskKey[Unit]("AgileSites upload jar")
    val asPopulate = inputKey[Unit]("AgileSites populate")
  }

  override lazy val projectSettings = deploySettings

}
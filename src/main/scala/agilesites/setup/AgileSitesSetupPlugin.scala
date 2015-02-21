package agilesites.setup

import sbt._
import agilesites.build.deploy.{DeploySettings, SetupSettings}
import agilesites.build.scaffold.ScaffoldSettings
import agilesites.build.tool.{TomcatSettings, ToolsSettings}
import agilesites.build.util.{UtilSettings, VersionSettings}
import sbt.plugins.JvmPlugin

object AgileSitesSetupPlugin
  extends AutoPlugin
  with UtilSettings
  with RepositorySettings {

  override def requires = JvmPlugin

  object autoImport {

    val repositoryTarget = settingKey[File]("Where to deploy the repository")

    val repositoryBuild = taskKey[Unit]("collect and deploy")

  }

  import autoImport._

  override lazy val projectSettings = repositorySettings


}
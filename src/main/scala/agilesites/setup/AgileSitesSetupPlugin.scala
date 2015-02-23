package agilesites.setup

import agilesites.gui.GuiSettings
import sbt._
import agilesites.plugin.deploy.{DeploySettings, SetupSettings}
import agilesites.plugin.tool.{TomcatSettings, ToolsSettings}
import agilesites.util.{UtilSettings, VersionSettings}
import sbt.plugins.JvmPlugin

object AgileSitesSetupPlugin
  extends AutoPlugin
  with UtilSettings
  with InstallerSettings {

  override def requires = JvmPlugin

  object autoImport {

    val repositoryTarget = settingKey[File]("Where to deploy the repository")

    val repositoryBuild = taskKey[Unit]("collect and deploy")

  }

  import autoImport._

  override lazy val projectSettings = installerSettings

}
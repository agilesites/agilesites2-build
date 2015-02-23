package agilesites.plugin

import agilesites.gui.GuiSettings
import agilesites.plugin.deploy.{DeploySettings, SetupSettings}
import agilesites.plugin.scaffold.ScaffoldSettings
import agilesites.plugin.tool.{TomcatSettings, ToolsSettings}
import agilesites.util.{UtilSettings, VersionSettings}
import sbt._
import sbt.plugins.JvmPlugin

object AgileSitesPlugin
  extends AutoPlugin
  with UtilSettings
  with VersionSettings
  with SitesConfig
  with AgileSitesConfig
  with GuiSettings
  with ToolsSettings
  with TomcatSettings
  with SetupSettings
  with DeploySettings
  with ScaffoldSettings {

  override def requires = JvmPlugin

  //import autoImport._

  override lazy val projectSettings =
    versionSettings ++
      utilSettings ++
      sitesConfig ++
      agileSitesConfig ++
      guiSettings

  //override lazy val buildSettings = versionBuildSettings

  //override lazy val projectSettings = versionProjectSettings

  /*++
      configSettings ++
      toolsSettings ++
      tomcatSettings ++
      deploySettings ++
      scaffoldSettings ++
      setupSettings
   */
}
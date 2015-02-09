package agilesites.build

import sbt._
import sbt.plugins.JvmPlugin
import agilesites.build.scaffold.ScaffoldSettings
import agilesites.build.deploy.{SetupSettings, DeploySettings}
import agilesites.build.tool.{ToolsSettings,TomcatSettings}
import agilesites.build.util.{UtilSettings, VersionSettings}

object AgileSitesPlugin
  extends AutoPlugin
  with UtilSettings
  with VersionSettings
  with SitesConfig
  with AgileSitesConfig
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
      agileSitesConfig

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
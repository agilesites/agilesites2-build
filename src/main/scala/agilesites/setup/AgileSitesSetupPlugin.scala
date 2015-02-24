package agilesites.setup

import agilesites.config.{AgileSitesConfigPlugin, UtilSettings}
import sbt._

object AgileSitesSetupPlugin
  extends AutoPlugin
  with InstallerSettings
  with TomcatSettings
  with ToolsSettings {

  override def requires = AgileSitesConfigPlugin

  object autoImport {

  }

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

  override lazy val projectSettings = installerSettings ++ tomcatSettings ++ toolsSettings

}
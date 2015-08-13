package agilesites.setup

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._
import java.io.File

import sbt.plugins.JvmPlugin

object AgileSitesSetupPlugin
  extends AutoPlugin
  with InstallerSettings
  with TomcatSettings
  with WeblogicSettings
  with ToolsSettings
  with SetupSettings {

  override def requires = AgileSitesConfigPlugin && JvmPlugin

  val autoImport = AgileSitesSetupKeys

  import agilesites.setup.AgileSitesSetupKeys._

  override lazy val projectSettings = weblogicSettings ++
    tomcatSettings ++
    toolsSettings ++
    setupSettings ++
    installerSettings
}
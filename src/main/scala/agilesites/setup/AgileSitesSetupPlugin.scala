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

  override lazy val projectSettings = Seq(
    ivyConfigurations ++= Seq(config("run"), config("core"), config("api"), config("populate")),
    asTomcatClasspath <<= (update) map {
      report => report.select(configurationFilter("run"))
    }, asCoreClasspath <<= (update) map {
      report => report.select(configurationFilter("core"))
    }, asApiClasspath <<= (update) map {
      report => report.select(configurationFilter("api"))
    }, asPopulateClasspath <<= (update) map {
      report => report.select(configurationFilter("populate"))
    }) ++
    weblogicSettings ++
    tomcatSettings ++
    toolsSettings ++
    setupSettings ++
    installerSettings
}
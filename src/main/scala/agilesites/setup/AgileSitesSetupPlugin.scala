package agilesites.setup

import java.io.File

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

object AgileSitesSetupPlugin
  extends AutoPlugin
  with InstallerSettings
  with TomcatSettings
  with ToolsSettings
  with SetupSettings {

  override def requires = AgileSitesConfigPlugin

  val tomcatConfig = "tomcat"
  val tomcatVersion = "7.0.52"
  val hsqlVersion = "1.8.0.10"
  val pluginVersion = "2.0-M1"

  object autoImport {
    lazy val tomcatClasspath = taskKey[Seq[File]]("Tomcat Classpath")
    lazy val sitesInstall = taskKey[Unit]("Sites installation task")
    lazy val asInstall = taskKey[Unit]("AgileSites installation task")
    lazy val asSetup = taskKey[Unit]("AgileSites Setup (Offline)")
    lazy val server = inputKey[Unit]("Launch Local Sites")
  }

  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  override lazy val projectSettings = Seq(
    tomcatClasspath <<= (update) map {
      report => report.select(configurationFilter("tomcat"))
    }) ++ tomcatSettings ++ toolsSettings ++ setupSettings ++ installerSettings
}
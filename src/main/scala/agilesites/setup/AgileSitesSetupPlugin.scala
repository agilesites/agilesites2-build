package agilesites.setup

import java.io.File

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

object AgileSitesSetupPlugin
  extends AutoPlugin
  with InstallerSettings
  with TomcatSettings
  with ToolsSettings {

  override def requires = AgileSitesConfigPlugin

  val tomcatConfig = "tomcat"
  val tomcatVersion = "7.0.52"
  val hsqlVersion = "1.8.0.10"
  val pluginVersion = "2.0-M1"

  object autoImport {

    lazy val tomcatClasspath = taskKey[Seq[File]]("tomcat classpath")
    lazy val install = taskKey[Unit]("Sites installation task")
    lazy val server = inputKey[Unit]("Launch Local Sites")
  }

  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  override lazy val projectSettings = Seq(
    tomcatClasspath <<= (update) map {
      report => report.select(configurationFilter("tomcat"))
    }) ++ installerSettings ++ tomcatSettings ++ toolsSettings

}
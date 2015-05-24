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

  object autoImport {
    lazy val asTomcatClasspath = taskKey[Seq[File]]("Tomcat Classpath")
    lazy val asExtraClasspath = taskKey[Seq[File]]("AgileSites Extra Classpath")
    lazy val asPopulateClasspath = taskKey[Seq[File]]("AgileSites Populate Classpath")
    lazy val asExtraWebappClasspath = taskKey[Seq[File]]("AgileSites Extra Webapp Classpath")

    lazy val sitesInstall = taskKey[Unit]("Sites installation task")
    lazy val proxyInstall = taskKey[Unit]("Proxy installation task")

    lazy val asSetupOffline = taskKey[Unit]("AgileSites Setup (Offline)")
    lazy val asSetupOnline = taskKey[Unit]("AgileSites Setup (Offline)")
    lazy val asSetup = taskKey[Unit]("AgileSites installation task for local sites")
    lazy val asStatics = settingKey[String]("AgileSites extensions to be recognized as statics")

    lazy val asSetupWeblogic = taskKey[Unit]("AgileSites installation task for Weblogic")
    lazy val weblogicDeploy = inputKey[Unit]("Weblogic Webapp Deploy")
    lazy val weblogicRedeployCs = taskKey[Unit]("Weblogic Redeploy CS")
    lazy val weblogicRedeployPackage = taskKey[Unit]("Weblogic Redeploy CS")
    lazy val server = inputKey[Unit]("Launch Local Sites")
    lazy val cmov = inputKey[Unit]("WCS Catalog Mover")
  }

  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  override lazy val projectSettings = Seq(
    asTomcatClasspath <<= (update) map {
      report => report.select(configurationFilter("tomcat"))
    }, asExtraClasspath <<= (update) map {
      report => report.select(configurationFilter("extra"))
    }, asExtraWebappClasspath <<= (update) map {
      report => report.select(configurationFilter("extrawebapp"))
    }, asPopulateClasspath <<= (update) map {
      report => report.select(configurationFilter("populate"))
    }) ++
    weblogicSettings ++
    tomcatSettings ++
    toolsSettings ++
    setupSettings ++
    installerSettings
}
package agilesites.setup

import java.io.File

import sbt._

/**
 * Created by msciab on 04/07/15.
 */
object AgileSitesSetupKeys {

  lazy val asTomcatClasspath = taskKey[Seq[File]]("Tomcat Classpath")
  lazy val asCoreClasspath = taskKey[Seq[File]]("AgileSites Core Classpath")
  lazy val asApiClasspath = taskKey[Seq[File]]("AgileSites Api Classpath")
  lazy val asPopulateClasspath = taskKey[Seq[File]]("AgileSites Populate Classpath")

  lazy val sitesDownload = inputKey[Unit]("Sites download task")
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

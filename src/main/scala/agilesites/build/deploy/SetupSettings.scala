package agilesites.build.deploy

import agilesites.Configure
import agilesites.build.{AgileSitesConfig, SitesConfig}
import sbt.Keys._
import sbt._

trait SetupSettings {
  this: AutoPlugin with SitesConfig with AgileSitesConfig =>

  // configure futurentense.ini
  lazy val asConfigure = taskKey[Unit]("AgileSites Configure")
  
  lazy val asConfigureTask = asConfigure := {
    val cfg = new Configure(sitesHome.value, sitesShared.value, sitesWebapp.value)
    cfg.registerAssembler
    cfg.configure
  }

  // copy jars to destination folders
  lazy val asCopyJars = taskKey[Unit]("AgileSites Copy Jars")
  
  lazy val asCopyJarsTask = asCopyJars := {
    val coreDir = file(sitesWebapp.value) / "WEB-INF" / "lib"

    val apiDir = file(sitesShared.value) / "agilesites" / "lib"

    val homeDir = file(sitesHome.value)

    coreDir.mkdirs
    apiDir.mkdirs

    // files
    val coreFiles = for {
      file <- update.value.select(configurationFilter("core"))
    } yield {
      val out = IO.copyFile(file, coreDir)
      println("+++ ${out}")
      out
    }

    val apiFiles = for {
      file <- update.value.select(configurationFilter("api"))
    } yield {
      val out = IO.copyFile(file, apiDir)
      println("+++ ${out}")
      out
    }
  }

  val setupSettings = Seq(asConfigureTask, asCopyJarsTask)
}
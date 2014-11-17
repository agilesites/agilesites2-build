package agilesites.build

import sbt._
import Keys._
import agilesites.Configure

trait SetupSettings {
  this: Plugin with ConfigSettings =>

  // configure futurentense.ini
  lazy val configure = taskKey[Unit]("AgileSites Configure") in asConfig
  
  lazy val configureTask = configure := {
    val cfg = new Configure(sitesHome.value, sitesShared.value, sitesWebapp.value)
    cfg.registerAssembler
    cfg.configure
  }

  // copy jars to destination folders
  lazy val copyJars = taskKey[Unit]("AgileSites Copy Jars") in asConfig
  
  lazy val copyJarsTask = copyJars := {
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

  val setupSettings = Seq(configureTask, copyJarsTask)  
}
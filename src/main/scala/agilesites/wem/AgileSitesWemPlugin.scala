package agilesites.wem

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

object AgileSitesWemPlugin
  extends AutoPlugin
  with RestSettings {

  override def requires = AgileSitesConfigPlugin

  object autoImport {

    // Keys
    lazy val wemConfig = config("owem").hide
    lazy val ologin = taskKey[String]("WEM login")
    lazy val oget = inputKey[Unit]("WEM get")

  }

  import autoImport._

  override val projectConfigurations =  Seq(wemConfig)
  override val projectSettings = restSettings
}
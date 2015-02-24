package agilesites.wem

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

object AgileSitesWemPlugin
  extends AutoPlugin
  with RestSettings {

  override def requires = AgileSitesConfigPlugin

  object autoImport {
    lazy val asConfig = config("as")
    // Keys
    lazy val wemConfig = config("wem").hide
    lazy val login = taskKey[String]("WEM login")
    lazy val get = inputKey[Unit]("WEM get")

  }

  import autoImport._

  override val projectSettings =
    Seq(ivyConfigurations ++= Seq(asConfig)) ++
      restSettings
}
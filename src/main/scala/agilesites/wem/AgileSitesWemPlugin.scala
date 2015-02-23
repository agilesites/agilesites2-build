package agilesites.wem

import sbt._
import Keys._
import agilesites.util.UtilSettings
import agilesites.plugin.{SitesConfig, AgileSitesConfig}

object AgileSitesWemPlugin
  extends AutoPlugin
  with UtilSettings
  with AgileSitesConfig
  with SitesConfig
  with RestSettings {

  // configurations
  lazy val asConfig = config("as")

  override val buildSettings = Seq(
    ivyConfigurations ++= Seq(asConfig)) ++ restSettings
}
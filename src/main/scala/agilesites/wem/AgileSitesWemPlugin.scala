package agilesites.wem

import agilesites.build.{SitesConfig, AgileSitesConfig}
import agilesites.build.util.UtilSettings
import sbt._
import Keys._

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
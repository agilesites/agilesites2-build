package agilesites.web

import agilesites.plugin.{AgileSitesConfig, SitesConfig}
import agilesites.util.UtilSettings
import sbt._

object AgileSitesWebPlugin
  extends AutoPlugin
  with UtilSettings
  with AgileSitesConfig
  with SitesConfig
  with WebSettings {

  override lazy val projectSettings =
      webSettings
}
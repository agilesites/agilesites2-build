package agilesites.web

import agilesites.build.{AgileSitesConfig, SitesConfig}
import agilesites.build.util.UtilSettings
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
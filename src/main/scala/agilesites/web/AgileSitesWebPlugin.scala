package agilesites.web

import java.io.File

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

object AgileSitesWebPlugin
  extends AutoPlugin
  with WebSettings {

  override def requires = AgileSitesConfigPlugin

  import agilesites.config.AgileSitesConfigKeys._

  val autoImport = AgileSitesWebKeys
  import AgileSitesWebKeys._

  override lazy val projectSettings = Seq(
    webStatics := utilPropertyMap.value.getOrElse("web.static.ext", "js,json,css,map,gif,png,jpg,jpeg,ico,woff,woff2,ttf"),
    webStaticPrefix := utilPropertyMap.value.getOrElse("web.static.prefix", "/cs/Satellite/"),
    webFolder := baseDirectory.value / "src" / "main" / "assets",
    webIncludeFilter := AllPassFilter,
    webExcludeFilter := NothingFilter,
    webFingerPrintFilter := GlobFilter("*.css") | GlobFilter("*.html") | GlobFilter("*.js")
  ) ++ webSettings
}
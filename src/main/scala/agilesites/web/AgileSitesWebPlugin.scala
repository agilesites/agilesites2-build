package agilesites.web

import java.io.File

import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

object AgileSitesWebPlugin
  extends AutoPlugin
  with WebSettings {

  override def requires = AgileSitesConfigPlugin

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

  object autoImport {
    lazy val webStatics = settingKey[String]("Statics Extensions")
    lazy val webStaticPrefix = settingKey[String]("Web Prefix for statics and fingerprinting")
    lazy val webFolder = taskKey[File]("AgileSites assets folder ")
    lazy val webIncludeFilter = taskKey[FileFilter]("Web Assets to include")
    lazy val webExcludeFilter = taskKey[FileFilter]("Web Assets to exclude")
    lazy val webFingerPrintFilter = taskKey[FileFilter]("Web Assets to finger print")
    lazy val webPackage = taskKey[Seq[java.io.File]]("package web asset with finger printing")

  }

  import autoImport._

  override lazy val projectSettings = Seq(
    webStatics := utilPropertyMap.value.getOrElse("web.static.ext", "js,json,css,map,gif,png,jpg,jpeg,ico"),
    webStaticPrefix := utilPropertyMap.value.getOrElse("web.static.prefix", "/cs/Satellite/"),
    webFolder := baseDirectory.value / "src" / "main" / "assets",
    webIncludeFilter := AllPassFilter,
    webExcludeFilter := NothingFilter,
    webFingerPrintFilter := GlobFilter("*.css") | GlobFilter("*.html") | GlobFilter("*.js")
  ) ++ webSettings
}
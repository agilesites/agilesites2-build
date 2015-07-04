package agilesites.web

import java.io.File

import sbt._

/**
 * Created by msciab on 04/07/15.
 */
object AgileSitesWebKeys {
  lazy val webStatics = settingKey[String]("Statics Extensions")
  lazy val webStaticPrefix = settingKey[String]("Web Prefix for statics and fingerprinting")
  lazy val webFolder = taskKey[File]("AgileSites assets folder ")
  lazy val webIncludeFilter = taskKey[FileFilter]("Web Assets to include")
  lazy val webExcludeFilter = taskKey[FileFilter]("Web Assets to exclude")
  lazy val webFingerPrintFilter = taskKey[FileFilter]("Web Assets to finger print")
  lazy val webPackage = taskKey[Seq[java.io.File]]("package web asset with finger printing")
}

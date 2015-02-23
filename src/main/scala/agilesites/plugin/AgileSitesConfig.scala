package agilesites.plugin

import agilesites.util.UtilSettings
import sbt._

/**
 * Created by msciab on 08/02/15.
 */

trait AgileSitesConfig {
  this: AutoPlugin with UtilSettings =>

  // AgileSites Keys
  val asSites = settingKey[String]("The list of the sites enabled under AgileSites")
  val asUploadTarget = settingKey[Option[String]]("Upload Target")
  val asStatics = settingKey[String]("Statics Extensions")
  val asStaticPrefix = settingKey[String]("Web Prefix for statics and fingerprinting")

  val agileSitesConfig = Seq(
    asSites := utilPropertyMap.value.getOrElse("as.sites", ""),
    asStatics := utilPropertyMap.value.getOrElse("as.static.ext", "js,json,css,map,gif,png,jpg,jpeg,ico"),
    asStaticPrefix := utilPropertyMap.value.getOrElse("as.static.prefix", "/cs/Satellite/"),
    asUploadTarget := utilPropertyMap.value.get("as.upload.target"))
}

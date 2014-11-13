package agilesites.build

import sbt._
import Keys._

trait ConfigSettings {
  this: Plugin with UtilSettings =>

  // jars to be added to the wcs-setup
  val webappFilter = "agilesites2-core*" || "jcl-core*"

  // jars to be added to the library setup
  val setupFilter = "agilesites2-api*" || "junit*" || "hamcrest*"

  // Keys
  lazy val asConfig = config("as").hide

  // Keys
  val asSites = settingKey[String]("Sites Enabled under AgileSites")
  val asUploadTarget = settingKey[Option[String]]("Upload Target")
  val asStatics = settingKey[String]("Statics Extensions")
  val asStaticPrefix = settingKey[String]("Web Prefix for statics and fingerprinting")

  val sitesVersion = settingKey[String]("Sites or Fatwire Version Number")
  val sitesHome = settingKey[String]("Sites Home Directory")
  val sitesShared = settingKey[String]("Sites Shared Directory")
  val sitesWebapp = settingKey[String]("Sites Webapp Directory")
  val sitesUrl = settingKey[String]("Sites URL")
  val sitesUser = settingKey[String]("Sites user ")
  val sitesPassword = settingKey[String]("Sites user password")
  val sitesPort = settingKey[String]("Sites Port")
  val sitesHost = settingKey[String]("Sites Host")
  val sitesPopulateDir = settingKey[String]("Sites Populate Dir")
  val sitesEnvisionDir = settingKey[String]("Sites Envision Dir")

  val configSettings = Seq(
    ivyConfigurations += asConfig,
    asSites := asPropertyMap.value.getOrElse("as.sites", "Demo"),
    asStatics := asPropertyMap.value.getOrElse("as.static.ext", "js,json,css,map,gif,png,jpg,jpeg,ico"),
    asStaticPrefix := asPropertyMap.value.getOrElse("as.static.prefix", "/cs/Satellite/"),
    asUploadTarget := asPropertyMap.value.get("as.upload.target"),
    sitesVersion := asPropertyMap.value.getOrElse("sites.version", "11.1.1.8.0"),
    sitesHome := asPropertyMap.value.getOrElse("sites.home", "sites/home"),
    sitesShared := asPropertyMap.value.getOrElse("sites.shared", "sites/shared"),
    sitesWebapp := asPropertyMap.value.getOrElse("sites.webapp", "sites/webapps/cs"),
    sitesUrl := asPropertyMap.value.getOrElse("sites.url", "http://localhost:8181/cs"),
    sitesUser := asPropertyMap.value.getOrElse("sites.user", "fwadmin"),
    sitesPassword := asPropertyMap.value.getOrElse("sites.password", "xceladmin"),
    sitesPort := asPropertyMap.value.getOrElse("sites.port", "8181"),
    sitesHost := asPropertyMap.value.getOrElse("sites.host", "localhost"),
    sitesPopulateDir := asPropertyMap.value.getOrElse("sites.populate", (baseDirectory.value.getAbsoluteFile() / "home" / "export" / "populate").getAbsolutePath()),
    sitesEnvisionDir := asPropertyMap.value.getOrElse("sites.envision", (baseDirectory.value.getAbsoluteFile() / "home" / "export" / "envision").getAbsolutePath()))
}
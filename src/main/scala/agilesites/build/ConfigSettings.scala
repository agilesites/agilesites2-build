package agilesites.build

import sbt._
import Keys._

trait ConfigSettings {
  this: Plugin with UtilSettings =>

   // Keys
  lazy val asConfig = config("as")
  lazy val asCoreConfig = config("as-core")
  lazy val asApiConfig = config("as-api")

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
  val sitesAdminUser = settingKey[String]("Sites admin user ")
  val sitesAdminPassword = settingKey[String]("Sites admin password")
  val sitesPort = settingKey[String]("Sites Port")
  val sitesHost = settingKey[String]("Sites Host")
  val sitesPopulateDir = settingKey[String]("Sites Populate Dir")
  val sitesEnvisionDir = settingKey[String]("Sites Envision Dir")
  val satelliteWebapp = settingKey[String]("Sites Satellite Directory")
  val satelliteHome = settingKey[String]("Sites Satellite Home Directory")
  val satelliteUrl = settingKey[String]("Sites Satellite Url Directory")
  val satelliteUser = settingKey[String]("Satellite user ")
  val satellitePassword = settingKey[String]("Satellite password")

  val configSettings = Seq(
    ivyConfigurations ++= Seq(asConfig, asCoreConfig, asApiConfig),
    asSites := asPropertyMap.value.getOrElse("as.sites", ""),
    asStatics := asPropertyMap.value.getOrElse("as.static.ext", "js,json,css,map,gif,png,jpg,jpeg,ico"),
    asStaticPrefix := asPropertyMap.value.getOrElse("as.static.prefix", "/cs/Satellite/"),
    asUploadTarget := asPropertyMap.value.get("as.upload.target"),
    sitesVersion := asPropertyMap.value.getOrElse("sites.version", "11.1.1.8.0"),
    sitesHome := asPropertyMap.value.getOrElse("sites.home", file("home").getAbsolutePath),
    sitesShared := asPropertyMap.value.getOrElse("sites.shared", file("shared").getAbsolutePath),
    sitesWebapp := asPropertyMap.value.getOrElse("sites.webapp", (file("webapps") / "cs").getAbsolutePath),
    sitesUser := asPropertyMap.value.getOrElse("sites.user", "fwadmin"),
    sitesPassword := asPropertyMap.value.getOrElse("sites.password", "xceladmin"),
    sitesAdminUser := asPropertyMap.value.getOrElse("sites.admin.user", "ContentServer"),
    sitesAdminPassword := asPropertyMap.value.getOrElse("sites.admin.password", "password"),
    sitesPort := asPropertyMap.value.getOrElse("sites.port", "8181"),
    sitesHost := asPropertyMap.value.getOrElse("sites.host", "localhost"),
    sitesUrl := asPropertyMap.value.getOrElse("sites.url", s"http://${sitesHost.value}:${sitesPort.value}/cs"),
    sitesPopulateDir := asPropertyMap.value.getOrElse("sites.populate", (file(sitesHome.value) / "export" / "populate").getAbsolutePath),
    sitesEnvisionDir := asPropertyMap.value.getOrElse("sites.envision", (file(sitesHome.value) / "export" / "envision").getAbsolutePath),
    satelliteWebapp := asPropertyMap.value.getOrElse("satellite.webapp", (file(sitesWebapp.value).getParentFile / "ss").getAbsolutePath),
    satelliteHome := asPropertyMap.value.getOrElse("satellite.home", sitesHome.value),
    satelliteUrl := asPropertyMap.value.getOrElse("satellite.url", s"http://${sitesHost.value}:${sitesPort.value}/ss"),
    satelliteUser := asPropertyMap.value.getOrElse("satellite.user", "SatelliteServer"),
    satellitePassword := asPropertyMap.value.getOrElse("satellite.password", "password"))
}
package agilesites.plugin

import sbt._
import agilesites.util.UtilSettings

/**
 * Created by msciab on 08/02/15.
 */

trait SitesConfig {
  this: AutoPlugin with UtilSettings =>

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
  val sitesHello = taskKey[Option[String]]("Sites Hello")

  val sitesConfig = Seq(
    sitesVersion := utilPropertyMap.value.getOrElse("sites.version", "11.1.1.8.0"),
    sitesHome := utilPropertyMap.value.getOrElse("sites.home", file("home").getAbsolutePath),
    sitesShared := utilPropertyMap.value.getOrElse("sites.shared", file("shared").getAbsolutePath),
    sitesWebapp := utilPropertyMap.value.getOrElse("sites.webapp", (file("webapps") / "cs").getAbsolutePath),
    sitesUser := utilPropertyMap.value.getOrElse("sites.user", "fwadmin"),
    sitesPassword := utilPropertyMap.value.getOrElse("sites.password", "xceladmin"),
    sitesAdminUser := utilPropertyMap.value.getOrElse("sites.admin.user", "ContentServer"),
    sitesAdminPassword := utilPropertyMap.value.getOrElse("sites.admin.password", "password"),
    sitesPort := utilPropertyMap.value.getOrElse("sites.port", "8181"),
    sitesHost := utilPropertyMap.value.getOrElse("sites.host", "localhost"),
    sitesUrl := utilPropertyMap.value.getOrElse("sites.url", s"http://${sitesHost.value}:${sitesPort.value}/cs"),
    sitesPopulateDir := utilPropertyMap.value.getOrElse("sites.populate", (file(sitesHome.value) / "export" / "populate").getAbsolutePath),
    sitesEnvisionDir := utilPropertyMap.value.getOrElse("sites.envision", (file(sitesHome.value) / "export" / "envision").getAbsolutePath),
    satelliteWebapp := utilPropertyMap.value.getOrElse("satellite.webapp", (file(sitesWebapp.value).getParentFile / "ss").getAbsolutePath),
    satelliteHome := utilPropertyMap.value.getOrElse("satellite.home", sitesHome.value),
    satelliteUrl := utilPropertyMap.value.getOrElse("satellite.url", s"http://${sitesHost.value}:${sitesPort.value}/ss"),
    satelliteUser := utilPropertyMap.value.getOrElse("satellite.user", "SatelliteServer"),
    satellitePassword := utilPropertyMap.value.getOrElse("satellite.password", "password"),
    sitesHello := { helloSites(sitesUrl.value) })
}

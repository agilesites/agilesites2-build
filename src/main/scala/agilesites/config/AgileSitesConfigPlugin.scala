package agilesites.config

import sbt._, Keys._
import sbt.plugins.JvmPlugin

object AgileSitesConfigPlugin
  extends AutoPlugin
  with UtilSettings
  with VersionSettings {

  override def requires = JvmPlugin

  object autoImport {

    // where the property files are
    lazy val utilProperties = settingKey[Seq[String]]("AgileSites Property Files")

    // read all the properties in a single property map
    lazy val utilPropertyMap = settingKey[Map[String, String]]("AgileSites Property Map")

    val sitesFocus = settingKey[String]("Sites's sites currently under focus")

    val sitesVersion = settingKey[String]("Sites or Fatwire Version Number")
    val sitesDirectory = settingKey[File]("Sites installation folder")
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

    val sitesHello = taskKey[Option[String]]("Hello World, Sites!")

  }

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

  override lazy val projectSettings = Seq(

    utilProperties := Seq("agilesites.properties"),

    // focus on which site?
    sitesFocus := utilPropertyMap.value.getOrElse("sites.focus", "Demo"),

    // installation properties
    sitesDirectory := file(utilPropertyMap.value.getOrElse("sites.directory",
      (baseDirectory.value  /  "sites" ).getAbsolutePath)),
    sitesHome := utilPropertyMap.value.getOrElse("sites.home",
      (sitesDirectory.value / "home").getAbsolutePath),
    sitesShared := utilPropertyMap.value.getOrElse("sites.shared",
      (sitesDirectory.value / "shared").getAbsolutePath),
    sitesWebapp := utilPropertyMap.value.getOrElse("sites.webapp",
      (sitesDirectory.value / "webapps" / "cs").getAbsolutePath),

    // versions
    sitesVersion := utilPropertyMap.value.getOrElse("sites.version", "11.1.1.8.0"),
    sitesUser := utilPropertyMap.value.getOrElse("sites.user", "fwadmin"),
    sitesPassword := utilPropertyMap.value.getOrElse("sites.password", "xceladmin"),
    sitesAdminUser := utilPropertyMap.value.getOrElse("sites.admin.user", "ContentServer"),
    sitesAdminPassword := utilPropertyMap.value.getOrElse("sites.admin.password", "password"),

    sitesPort := utilPropertyMap.value.getOrElse("sites.port", "11880"),
    sitesHost := utilPropertyMap.value.getOrElse("sites.host", "localhost"),
    sitesUrl := utilPropertyMap.value.getOrElse("sites.url",
      s"http://${sitesHost.value}:${sitesPort.value}/cs"),
    sitesPopulateDir := utilPropertyMap.value.getOrElse("sites.populate",
      (file(sitesHome.value) / "export" / "populate").getAbsolutePath),
    sitesEnvisionDir := utilPropertyMap.value.getOrElse("sites.envision",
      (file(sitesHome.value) / "export" / "envision").getAbsolutePath),

    satelliteWebapp := utilPropertyMap.value.getOrElse("satellite.webapp",
      (file(sitesWebapp.value).getParentFile / "ss").getAbsolutePath),
    satelliteHome := utilPropertyMap.value.getOrElse("satellite.home", sitesHome.value),
    satelliteUser := utilPropertyMap.value.getOrElse("satellite.user", "SatelliteServer"),
    satellitePassword := utilPropertyMap.value.getOrElse("satellite.password", "password"),
    satelliteUrl := utilPropertyMap.value.getOrElse("satellite.url",
      s"http://${sitesHost.value}:${sitesPort.value}/ss"),

    sitesHello := {
      helloSites(sitesUrl.value)
    }) ++ utilSettings ++ versionSettings

}
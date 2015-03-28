package agilesites.config

import sbt.Keys._
import sbt._
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

    val sitesHello = taskKey[Option[String]]("Hello World, Sites!")
    val sitesFocus = settingKey[String]("Sites's sites currently under focus")

    val sitesVersion = settingKey[String]("Sites or Fatwire Version Number")
    val sitesDirectory = settingKey[File]("Sites installation folder")
    val sitesHome = settingKey[String]("Sites Home Directory")
    val sitesShared = settingKey[String]("Sites Shared Directory")
    val sitesWebapp = settingKey[String]("Sites Webapp Directory")
    val sitesWebappName = settingKey[String]("Sites Webapp Name")

    val sitesUrl = settingKey[String]("Sites URL")
    val sitesUser = settingKey[String]("Sites User ")
    val sitesPassword = settingKey[String]("Sites Password")
    val sitesAdminUser = settingKey[String]("Sites admin user ")
    val sitesAdminPassword = settingKey[String]("Sites admin password")
    val sitesPort = settingKey[String]("Sites Port")

    val sitesHost = settingKey[String]("Sites Host")
    val sitesPopulate = settingKey[String]("Sites Populate Dir")
    val sitesEnvision = settingKey[String]("Sites Envision Dir")

    val satelliteWebapp = settingKey[String]("Sites Satellite Directory")
    val satelliteHome = settingKey[String]("Sites Satellite Home Directory")
    val satelliteUrl = settingKey[String]("Sites Satellite Url Directory")
    val satelliteUser = settingKey[String]("Satellite user ")
    val satellitePassword = settingKey[String]("Satellite password")

    val weblogicUrl = settingKey[String]("Weblogic Url")
    val weblogicTargets = settingKey[String]("Weblogic Target")
    val weblogicUser = settingKey[String]("Weblogic User")
    val weblogicPassword = settingKey[String]("Weblogic Password")
    val weblogicServer = settingKey[File]("Weblogic Server")

  }

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

  val profile = Option(System.getProperty("profile")).map(Seq(_)).getOrElse(Nil)

  val propertyFiles = Seq("agilesites.dist.properties",
    "agilesites.properties",
    "agilesites.local.properties") ++ profile.map(x => s"agilesites.${x}.properties")


  override lazy val projectSettings = Seq(
    utilProperties := propertyFiles,
    // focus on which site?
    sitesFocus := utilPropertyMap.value.getOrElse("sites.focus", "Demo"),
    // installation properties
    sitesDirectory := file(utilPropertyMap.value.getOrElse("sites.directory",
      (baseDirectory.value / "sites").getAbsolutePath)),
    sitesHome := utilPropertyMap.value.getOrElse("sites.home",
      (sitesDirectory.value / "home").getAbsolutePath),
    sitesShared := utilPropertyMap.value.getOrElse("sites.shared",
      (sitesDirectory.value / "shared").getAbsolutePath),
    sitesWebapp := utilPropertyMap.value.getOrElse("sites.webapp",
      (sitesDirectory.value / "webapps" / "cs").getAbsolutePath),
    sitesWebappName := utilPropertyMap.value.getOrElse("sites.webapp.name",
      (file(sitesWebapp.value).getName)),

    sitesPopulate := utilPropertyMap.value.getOrElse("sites.populate",
      (baseDirectory.value / "export" / "populate").getAbsolutePath),
    sitesEnvision := utilPropertyMap.value.getOrElse("sites.envision",
      (baseDirectory.value / "export" / "envision").getAbsolutePath),

    // versions
    sitesVersion := utilPropertyMap.value.getOrElse("sites.version", "11.1.1.8.0"),
    sitesUser := utilPropertyMap.value.getOrElse("sites.user", "fwadmin"),
    sitesPassword := utilPropertyMap.value.getOrElse("sites.password", "xceladmin"),
    sitesAdminUser := utilPropertyMap.value.getOrElse("sites.admin.user", "ContentServer"),
    sitesAdminPassword := utilPropertyMap.value.getOrElse("sites.admin.password", "password"),

    sitesPort := utilPropertyMap.value.getOrElse("sites.port", "11800"),
    sitesHost := utilPropertyMap.value.getOrElse("sites.host", "localhost"),
    sitesUrl := utilPropertyMap.value.getOrElse("sites.url",
      s"http://${sitesHost.value}:${sitesPort.value}/cs"),

    satelliteWebapp := utilPropertyMap.value.getOrElse("satellite.webapp",
      (file(sitesWebapp.value).getParentFile / "ss").getAbsolutePath),
    satelliteHome := utilPropertyMap.value.getOrElse("satellite.home", sitesHome.value),
    satelliteUser := utilPropertyMap.value.getOrElse("satellite.user", "SatelliteServer"),
    satellitePassword := utilPropertyMap.value.getOrElse("satellite.password", "password"),
    satelliteUrl := utilPropertyMap.value.getOrElse("satellite.url",
      s"http://${sitesHost.value}:${sitesPort.value}/ss"),

    weblogicUser := utilPropertyMap.value.getOrElse("weblogic.user", "weblogic"),
    weblogicPassword := utilPropertyMap.value.getOrElse("weblogic.password", "password"),
    weblogicUrl := utilPropertyMap.value.getOrElse("weblogic.url", "t3://localhost:7001"),
    weblogicServer := file(utilPropertyMap.value.getOrElse("weblogic.server", "wlserver")),
    weblogicTargets := utilPropertyMap.value.getOrElse("weblogic.targets", "AdminServer"),

    sitesHello := {
      helloSites(sitesUrl.value)
    }) ++ utilSettings ++ versionSettings
}
package agilesites.config

import sbt._

/**
 * Created by msciab on 02/07/15.
 */
object AgileSitesConfigKeys {

  // read all the properties in a single property map
  lazy val utilProperties = settingKey[Seq[String]]("AgileSites Property Files")
  lazy val utilPropertyMap = settingKey[Map[String, String]]("AgileSites Property Map")
  lazy val uidPropertyMap = settingKey[Map[String, String]]("AgileSites UID Map")

  val sitesUrl = settingKey[String]("Sites URL")
  val sitesUser = settingKey[String]("Sites User ")
  val sitesPassword = settingKey[String]("Sites Password")
  val sitesAdminUser = settingKey[String]("Sites admin user ")
  val sitesAdminPassword = settingKey[String]("Sites admin password")
  val sitesPort = settingKey[String]("Sites Port")
  val sitesHost = settingKey[String]("Sites Host")

  val sitesHello = taskKey[Option[String]]("Hello World, Sites!")
  val sitesFocus = settingKey[String]("Sites's sites currently under focus")
  val sitesFocusId = settingKey[Long]("Sites's site id currently under focus")
  val sitesStatics = settingKey[String]("Comma separated list of extensions to be considered statics")

  val sitesVersion = settingKey[String]("Sites or Fatwire Version Number")
  val sitesDirectory = settingKey[File]("Sites installation folder")
  val sitesHome = settingKey[String]("Sites Home Directory")
  val sitesShared = settingKey[String]("Sites Shared Directory")
  val sitesWebapp = settingKey[String]("Sites Webapp Directory")
  val sitesWebappName = settingKey[String]("Sites Webapp Name")

  val sitesPopulate = settingKey[String]("Sites Populate Dir")
  val sitesEnvision = settingKey[String]("Sites Envision Dir")

  val casProtocol = settingKey[String]("Cas Protocol version")

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

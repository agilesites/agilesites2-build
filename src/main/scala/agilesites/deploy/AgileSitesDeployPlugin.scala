package agilesites.deploy

import sbt._
import agilesites.config.{VersionSettings, UtilSettings}
import agilesites.config.AgileSitesConfigPlugin

object AgileSitesDeployPlugin
  extends AutoPlugin
  with DeploySettings
  with ScaffoldSettings {

  override def requires = AgileSitesConfigPlugin

  object autoImport {
    // upload target
    val asUploadTarget = settingKey[Option[String]]("Upload Target")
  }

  import autoImport._
  import agilesites.config.AgileSitesConfigPlugin.autoImport._

  override lazy val projectSettings = Seq(
    asUploadTarget := utilPropertyMap.value.get("as.upload.target")
  )
}
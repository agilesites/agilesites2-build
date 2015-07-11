package agilesites.deploy

import agilesitesng.wem.AnnotationSettings
import sbt._
import agilesites.config.AgileSitesConfigPlugin

object AgileSitesDeployPlugin
  extends AutoPlugin
  with DeploySettings
  with AnnotationSettings
  with CopySettings {

  override def requires = AgileSitesConfigPlugin

  val autoImport = AgileSitesDeployKeys

  override lazy val projectSettings =
    deploySettings ++
      copySettings

}
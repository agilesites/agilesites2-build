package agilesites.deploy

import sbt._
import agilesites.config.AgileSitesConfigPlugin

object AgileSitesDeployPlugin
  extends AutoPlugin
  with DeploySettings
  with AnnotationSettings
  with CopySettings {

  override def requires = AgileSitesConfigPlugin

  object autoImport {
    // upload target
    val asPackage = taskKey[Unit]("AgileSites package jar")
    val asCopyStatics = taskKey[Unit]("AgileSites copy statics")
    val asDeploy = inputKey[Unit]("AgileSites deploy")
    val asPopulate = taskKey[Unit]("AgileSites populate")

    val asScpFromTo = settingKey[Option[(File, URL)]]("AgileSites scp from source file to target url (either scp:// or file://)")
    val asScp = taskKey[Unit]("AgileSites scp")

  }

  override lazy val projectSettings =
    deploySettings ++
      annotationSettings ++
      copySettings

}
package agilesites.generator

import agilesites.build.util.UtilSettings
import sbt._
import sbt.Keys._
import sbt.ConfigKey.configurationToKey
import agilesites.build.AgileSitesConfig

object AgileSitesGeneratorPlugin
  extends AutoPlugin
  with UtilSettings
  with AgileSitesConfig
  with CommonSettings
  with InstallerSettings
  with SpoonSettings {

  override val buildSettings =
    commonSettings ++
      installerSettings ++
      spoonSettings
}
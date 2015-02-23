package agilesites.generator

import sbt._
import sbt.Keys._
import sbt.ConfigKey.configurationToKey
import agilesites.plugin.AgileSitesConfig
import agilesites.util.UtilSettings

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
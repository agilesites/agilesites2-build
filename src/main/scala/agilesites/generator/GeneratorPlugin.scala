package agilesites.generator

import sbt._
import sbt.Keys._
import sbt.ConfigKey.configurationToKey
import agilesites.build.ConfigSettings

trait GeneratorPlugin
  extends CommonSettings
  with InstallerSettings
  with SpoonSettings {
  this: Plugin with ConfigSettings =>

  val generatorPlugin =
    commonSettings ++
      installerSettings ++
      spoonSettings
}
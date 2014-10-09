package agilesites.generator

import sbt._
import sbt.Keys._
import sbt.ConfigKey.configurationToKey

trait GeneratorSettings
  extends CommonSettings
  with InstallerSettings {
  this: Plugin =>

  val generatorSettings = commonSettings  ++ installerSettings
}
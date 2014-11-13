package agilesites.wem

import sbt._
import Keys._
import agilesites.build.ConfigSettings

trait WemPlugin
  extends RestSettings {
  this: Plugin with ConfigSettings =>

  // aggregate
  val wemPlugin = restSettings
}
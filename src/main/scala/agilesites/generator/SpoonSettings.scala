package agilesites.generator

import sbt._
import sbt.Keys._
import agilesites.build.ConfigSettings
import agilesites.build.UtilSettings
import sbt.ConfigKey.configurationToKey

trait SpoonSettings {
  this: Plugin with CommonSettings =>

  def spoonCmd = Command.args("spoon", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    val args = Array("-i", "src/main/java", "-p", "agilesites.jspgen.HelloProcessor")

    exec("spoon.Launcher" +: args, state.configuration.baseDirectory, cp)

    state
  }

  val spoonSettings = Seq(commands += spoonCmd)

}
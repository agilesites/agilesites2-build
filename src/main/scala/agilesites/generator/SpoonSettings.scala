package agilesites.generator

import agilesites.util.UtilSettings
import sbt.Keys._
import sbt._

trait SpoonSettings {
  this: AutoPlugin with UtilSettings =>

  def spoonCmd = Command.args("spoon", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    val args = Array("-i", "src/main/java", "-p", "agilesites.jspgen.HelloProcessor")

    exec("spoon.Launcher" +: args, state.configuration.baseDirectory, cp)

    state
  }

  val spoonSettings = Seq(commands += spoonCmd)

}
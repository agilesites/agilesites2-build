package agilesites.generator

import sbt.Keys._
import sbt._

trait SpoonSettings {
  this: AutoPlugin with CommonSettings =>

  def spoonCmd = Command.args("spoon", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    val args = Array("-i", "src/main/java", "-p", "agilesites.jspgen.HelloProcessor")

    exec("spoon.Launcher" +: args, state.configuration.baseDirectory, cp)

    state
  }

  val spoonSettings = Seq(commands += spoonCmd)

}
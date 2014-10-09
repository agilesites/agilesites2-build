package agilesites.generator

import sbt._
import sbt.Keys._
import agilesites.build.ConfigSettings

trait InstallerSettings {
  this: Plugin with CommonSettings =>

  def installCmd = Command.args("install", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    exec("agilesites.generator.gui.Main" +: args, state.configuration.baseDirectory, cp)

    state
  }

  val installerSettings =  Seq(commands ++= Seq(installCmd))

   // inConfig(asConfig) {
  //}

}
  

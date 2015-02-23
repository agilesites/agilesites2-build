package agilesites.generator

import agilesites.util.UtilSettings
import sbt._
import sbt.Keys._
import agilesites.plugin.AgileSitesConfig

trait InstallerSettings {
  this: AutoPlugin with UtilSettings =>

  def installCmd = Command.args("install", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    exec("Main" +: args, state.configuration.baseDirectory, cp)

    state
  }

  val installerSettings = Seq(commands ++= Seq(installCmd))

}
  

package agilesitesng

import agilesites.AgileSitesPlugin
import agilesites.config.AgileSitesConfigPlugin
import agilesitesng.deploy.NgDeployPlugin
import agilesitesng.js.NgJsPlugin
import agilesitesng.setup.NgSetupPlugin
import agilesitesng.wem.AgileSitesWemPlugin

import sbt._

object AgileSitesNgPlugin
  extends AutoPlugin {

  override def requires = AgileSitesPlugin &&
    NgJsPlugin &&
    NgSetupPlugin &&
    NgDeployPlugin

  /*
  def guiCmd = Command.args("gui", "<args>") { (state, args) =>
    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath
    exec("agilesites.gui.Main" +: args, state.configuration.baseDirectory, cp)
    state
  }*/

  //override lazy val projectSettings = Seq(commands ++= Seq(guiCmd))
}
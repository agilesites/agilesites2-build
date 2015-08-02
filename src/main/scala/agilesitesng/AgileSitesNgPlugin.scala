package agilesitesng

import agilesites.AgileSitesPlugin
import agilesites.config.AgileSitesConfigPlugin
import agilesitesng.install.AgileSitesInstallPlugin
import agilesitesng.js.AgileSitesJsPlugin
import agilesitesng.wem.AgileSitesWemPlugin

import sbt._

object AgileSitesNgPlugin
  extends AutoPlugin {

  override def requires = AgileSitesPlugin &&
    AgileSitesWemPlugin &&
    AgileSitesJsPlugin &&
    AgileSitesInstallPlugin

  /*
  def guiCmd = Command.args("gui", "<args>") { (state, args) =>
    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath
    exec("agilesites.gui.Main" +: args, state.configuration.baseDirectory, cp)
    state
  }*/

  //override lazy val projectSettings = Seq(commands ++= Seq(guiCmd))
}
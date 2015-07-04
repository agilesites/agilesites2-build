package agilesites

import agilesites.config.AgileSitesConfigPlugin
import agilesites.deploy.AgileSitesDeployPlugin
import agilesites.setup.AgileSitesSetupPlugin
import agilesites.web.AgileSitesWebPlugin
import sbt._
import sbt.plugins.JvmPlugin

object AgileSitesPlugin
  extends AutoPlugin
  with Utils {

  override def requires = JvmPlugin &&
    AgileSitesConfigPlugin &&
    AgileSitesDeployPlugin &&
    AgileSitesSetupPlugin &&
    AgileSitesWebPlugin

  /*
  def guiCmd = Command.args("gui", "<args>") { (state, args) =>
    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath
    exec("agilesites.gui.Main" +: args, state.configuration.baseDirectory, cp)
    state
  }*/

  //override lazy val projectSettings = Seq(commands ++= Seq(guiCmd))
}
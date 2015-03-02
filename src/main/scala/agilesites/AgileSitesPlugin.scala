package agilesites

import agilesites.config.AgileSitesConfigPlugin
import agilesites.deploy.AgileSitesDeployPlugin
import agilesites.setup.AgileSitesSetupPlugin
import agilesites.web.AgileSitesWebPlugin
import agilesites.wem.AgileSitesWemPlugin
import sbt.Keys._
import sbt._

object AgileSitesPlugin
  extends AutoPlugin
  with Utils {

  override def requires =
    AgileSitesConfigPlugin &&
      AgileSitesDeployPlugin &&
      AgileSitesSetupPlugin &&
      AgileSitesWebPlugin &&
      AgileSitesWemPlugin

  def guiCmd = Command.args("gui", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    exec("agilesites.gui.Main" +: args, state.configuration.baseDirectory, cp)
    state
  }

  def downloadCmd = Command.command("sitesDownload") { state =>

    import AgileSitesConfigPlugin.autoImport._
    val extracted: Extracted = Project.extract(state)
    val dir = extracted.get(sitesDirectory)

    exec(Seq("agilesites.gui.Main", "download", dir.getAbsolutePath),
      state.configuration.baseDirectory,
      extracted.currentUnit.classpath)

    state
  }

  override lazy val projectSettings = Seq(commands ++= Seq(guiCmd, downloadCmd))
}
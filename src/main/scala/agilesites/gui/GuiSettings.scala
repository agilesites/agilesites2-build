package agilesites.gui

import agilesites.util.UtilSettings
import sbt.Keys._
import sbt._

/**
 * Created by msciab on 19/02/15.
 */
trait GuiSettings {
  this: AutoPlugin with UtilSettings =>

  def guiCmd = Command.args("gui", "<args>") { (state, args) =>

    val ex = Project.extract(state)
    val cp = ex.currentUnit.classpath

    exec("agilesites.gui.Main" +: args, state.configuration.baseDirectory, cp)

    state
  }

  val guiSettings = Seq(commands ++= Seq(guiCmd))

}
package agilesites.setup

import agilesites.util.UtilSettings
import sbt._

/**
 * Created by msciab on 19/02/15.
 */
trait InstallerSettings {
  this: AutoPlugin with UtilSettings =>

  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  lazy val repositoryBuildTask = repositoryBuild := {
    println(repositoryTarget.value)
  }

  val installerSettings = Seq(
    repositoryBuildTask,
    repositoryTarget := file(System.getProperty("user.home")) / "Local" / "AgileSitesInstaller" / "repo")

}
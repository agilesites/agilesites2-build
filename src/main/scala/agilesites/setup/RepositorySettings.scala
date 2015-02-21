package agilesites.setup

import agilesites.build.util.UtilSettings
import sbt._

/**
 * Created by msciab on 19/02/15.
 */
trait RepositorySettings {
  this: AutoPlugin with UtilSettings =>

 import AgileSitesSetupPlugin.autoImport._

  lazy val repositoryBuildTask = repositoryBuild := {
    println(repositoryTarget.value)
  }

  val repositorySettings = Seq(repositoryBuildTask,
      repositoryTarget := file(System.getProperty("user.home")) / "Local" / "AgileSitesInstaller" / "repo"
  )


}
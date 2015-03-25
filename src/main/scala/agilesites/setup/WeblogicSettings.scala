package agilesites.setup

import agilesites.Utils
import sbt._

/**
 * Created by msciab on 24/03/15.
 */
trait WeblogicSettings extends Utils {

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  val weblogicDeployTask = weblogicDeploy := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val url = weblogicUrl.value
    val user = weblogicUser.value
    val password = weblogicPassword.value
    val targets = weblogicTargets.value
    val wlserver = weblogicServer.value
    val weblogicJar = wlserver / "server" / "lib" / "weblogic.jar"

    val forkOpt = ForkOptions(
      runJVMOptions = "-cp" :: weblogicJar.getAbsolutePath :: Nil,
      workingDirectory = Some(wlserver))

    Fork.java(forkOpt,
      Seq("weblogic.Deployer",
        "-adminurl", url,
        "-username", user,
        "-password", password,
        "-targets", targets) ++ args
    )
  }

  val weblogicSettings = Seq(weblogicDeployTask)
}

package agilesites.setup

import agilesites.Utils
import sbt._

/**
 * Created by msciab on 24/03/15.
 */
trait WeblogicSettings extends Utils {

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  def weblogicDeployer(args: String*)
                    (implicit url: String,
                     user: String, password: String,
                     targets: String, wlserver: File) = {

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

  val weblogicDeployTask = weblogicDeploy := {

    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed

    implicit val url = weblogicUrl.value
    implicit val user = weblogicUser.value
    implicit val password = weblogicPassword.value
    implicit val targets = weblogicTargets.value
    implicit val wlserver = weblogicServer.value

    weblogicDeployer(args: _*)(url, user, password, targets, wlserver)
  }

  val weblogicRedeployCsTask = weblogicRedeployCs := {

    implicit val url = weblogicUrl.value
    implicit val user = weblogicUser.value
    implicit val password = weblogicPassword.value
    implicit val targets = weblogicTargets.value
    implicit val wlserver = weblogicServer.value

    weblogicDeployer("-name", sitesWebappName.value, "-redeploy")(url, user, password, targets, wlserver)
  }

  val weblogicDeployPackageTask = weblogicDeployPackage := {

    implicit val url = weblogicUrl.value
    implicit val user = weblogicUser.value
    implicit val password = weblogicPassword.value
    implicit val targets = weblogicTargets.value
    implicit val wlserver = weblogicServer.value

    val pkg = (Keys.`package` in Compile).value
    weblogicDeployer("-name", Keys.name.value, "-deploy", pkg.getAbsolutePath)(url, user, password, targets, wlserver)
  }

  val weblogicSettings = Seq(weblogicDeployTask, weblogicRedeployCsTask, weblogicDeployPackageTask)
}

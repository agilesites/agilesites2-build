package agilesitesng.setup

import agilesites.config.{AgileSitesConfigKeys, AgileSitesConfigPlugin}
import agilesitesng.Utils
import com.typesafe.sbt.web.SbtWeb
import sbt._, Keys._

/**
 * Created by msciab on 04/08/15.
 */
object NgSetupPlugin
  extends AutoPlugin
  with ConcatSettings
  with TagSettings {

  val autoImport = NgSetupKeys

  override def requires = SbtWeb && AgileSitesConfigPlugin

  //override def trigger = AllRequirements

  import AgileSitesConfigKeys._

  val ngServiceTask = NgSetupKeys.ngService := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    if (args.size == 0) {
      println("usage: ngService <op> <key=value>")
    } else {
      val req = s"${sitesUrl.value}/ContentServer?pagename=AAAgileService" +
        s"&username=${sitesUser.value}&password=${sitesPassword.value}" +
        s"&site=${sitesFocus.value}&op=${args.head}${
          args.tail.map(s => if(s.indexOf("=")== -1) "value="+s else s ).mkString("&", "&", "")}"
      println(">>> " + req)
      println(httpCallRaw(req))
    }
  }

  override val projectSettings = Seq(ngServiceTask) ++ concatSettings ++ tagSettings

}

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


  override val projectSettings = concatSettings ++ tagSettings

}

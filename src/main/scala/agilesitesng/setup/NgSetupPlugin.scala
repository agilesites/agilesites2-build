package agilesitesng.setup

import agilesites.config.AgileSitesConfigPlugin
import com.typesafe.sbt.web.SbtWeb
import sbt.{AllRequirements, AutoPlugin}

/**
 * Created by msciab on 04/08/15.
 */
object NgSetupPlugin
  extends AutoPlugin
  with ConcatSettings
  with TagSettings
  {

  val autoImport = NgSetupKeys

  override def requires = SbtWeb && AgileSitesConfigPlugin

  override def trigger = AllRequirements


  override val projectSettings = concatSettings ++ tagSettings

}

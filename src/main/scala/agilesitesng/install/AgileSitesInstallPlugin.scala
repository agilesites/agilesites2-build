/**
 * Created by msciab on 26/06/15.
 */
package agilesitesng.install

import java.net.URL

import agilesites.Utils
import agilesites.config.{AgileSitesConfigKeys, AgileSitesConfigPlugin}
import agilesitesng.js._
import agilesitesng.wem.actor.{Hub, Protocol}
import akka.actor.ActorRef
import akka.pattern.gracefulStop
import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._

import scala.concurrent.duration._
import scala.io.Source

object AgileSitesInstallPlugin
  extends AutoPlugin
  with ConcatSettings
  with TagSettings
  {

  import AgileSitesConfigKeys._
  import AgileSitesInstallKeys._

  val autoImport = AgileSitesInstallKeys

  override def requires = SbtWeb && AgileSitesConfigPlugin

  override def trigger = AllRequirements


  override val projectSettings = concatSettings ++ tagSettings

}

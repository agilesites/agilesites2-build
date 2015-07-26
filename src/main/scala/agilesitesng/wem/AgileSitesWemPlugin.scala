/**
 * Created by msciab on 26/06/15.
 */
package agilesitesng.wem

import agilesitesng.wem.actor.{Protocol, Hub}
import sbt.Keys._
import sbt._
import scala.concurrent.duration._
import java.net.URL
import agilesites.Utils
import agilesitesng.js._
import akka.actor.ActorRef
import akka.pattern.gracefulStop
import com.typesafe.sbt.web.SbtWeb
import agilesites.config.{AgileSitesConfigKeys, AgileSitesConfigPlugin}


object AgileSitesWemPlugin
  extends AutoPlugin
  with AnnotationSettings
  with WemSettings
  with Utils {

  import AgileSitesConfigKeys._
  import AgileSitesWemKeys._

  val autoImport = AgileSitesWemKeys

  val wemHubKey = AttributeKey[ActorRef]("wem-hub")

  private def finish(state: State): State = {
    state.get(wemHubKey) map {
      hubActor =>
        hubActor ! Protocol.Disconnect()
        gracefulStop(hubActor, 1.second)
    }
    state.remove(wemHubKey)
  }

  private def init(url: java.net.URL, user: String, password: String, casVersion: String, state: State): State = {
    //createLogger("agilesitesng.wem")
    SbtWeb.withActorRefFactory(state, "agilesitesng.AgileSitesNgPlugin") {
      arf =>
        val hub = arf.actorOf(Hub.actor(), "Hub")
        val f = hub ! Protocol.Connect(Some(url), Some(user), Some(password), casVersion)
        val newState = state.put(wemHubKey, hub)
        //newState.addExitHook(s: sbt.State => finish(s))
        newState
    }
  }

  override def globalSettings: Seq[Setting[_]] = super.globalSettings ++
    Seq(
      onLoad in Global := (onLoad in Global).value andThen
        (init(new URL(sitesUrl.value), sitesUser.value, sitesPassword.value, casProtocol.value, _)),
      onUnload in Global := (onUnload in Global).value andThen
        (finish)
    )

  override def requires = SbtWeb && AgileSitesConfigPlugin && AgileSitesJsPlugin

  override def trigger = AllRequirements

  override val projectSettings = wemSettings ++
    annotationSettings ++
    Seq(hub <<= state map (_.get(wemHubKey).get))

}

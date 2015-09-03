package agilesitesng.deploy

import java.net.URL

import akka.pattern.ask
import agilesites.config.AgileSitesConfigKeys._
import agilesitesng.deploy.actor.DeployProtocol.{ServiceReply, ServiceLogin}
import agilesitesng.deploy.actor.{DeployHub, Services}
import akka.actor.{ActorRef, PoisonPill}
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._

import com.typesafe.sbt.web.SbtWeb
import sbt.Keys._
import sbt._

/**
 * Created by msciab on 04/08/15.
 */
trait ActorSettings {
  this: AutoPlugin =>

  import NgDeployKeys._

  val ngDeployHubKey = AttributeKey[ActorRef]("ng-deployer")

  private def finish(state: State): State = {
    state.get(ngDeployHubKey) map {
      ngActor =>
        ngActor ! PoisonPill
    }
    state.remove(ngDeployHubKey)
  }

  private def init(url: java.net.URL, user: String, pass: String, state: State): State = {
    //createLogger("agilesitesng.wem")
    SbtWeb.withActorRefFactory(state, "Ng") {
      arf =>
        val interval = 3.second
        implicit val timeout = Timeout(3.second)
        val hub = arf.actorOf(DeployHub.actor(), "DeployHub")
        val newState = state.put(ngDeployHubKey, hub)
        //newState.addExitHook(s: sbt.State => finish(s))
        newState
    }
  }

  def actorGlobalSettings: Seq[Setting[_]] = Seq(
    onLoad in Global := (onLoad in Global).value andThen
      (init(new URL(sitesUrl.value), sitesUser.value, sitesPassword.value, _)),
    onUnload in Global := (onUnload in Global).value andThen
      (finish)
  )

  val actorSettings = Seq(ngDeployHub <<= state map (_.get(ngDeployHubKey).get))

}

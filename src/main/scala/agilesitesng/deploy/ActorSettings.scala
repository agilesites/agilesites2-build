package agilesitesng.deploy

import java.net.URL

import agilesites.config.AgileSitesConfigKeys._
import agilesitesng.deploy.actor.{DeployHub, Deployer}
import agilesitesng.deploy.actor.Protocol.Login
import akka.actor.{ActorRef, PoisonPill}
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

  private def init(url: java.net.URL, user: String, password: String, state: State): State = {
    //createLogger("agilesitesng.wem")
    SbtWeb.withActorRefFactory(state, "Ngn") {
      arf =>
        println("************ NgSettings init ")
        val hub = arf.actorOf(DeployHub.actor(), "Deployer")
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

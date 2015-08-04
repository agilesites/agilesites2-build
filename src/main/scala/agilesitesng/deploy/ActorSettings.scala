package agilesitesng.deploy

import java.net.URL

import agilesites.config.AgileSitesConfigKeys._
import agilesitesng.deploy.actor.Deployer
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

  val ngDeployKey = AttributeKey[ActorRef]("ng-deployer")

  private def finish(state: State): State = {
    state.get(ngDeployKey) map {
      ngActor =>
        ngActor ! PoisonPill
    }
    state.remove(ngDeployKey)
  }

  private def init(url: java.net.URL, user: String, password: String, state: State): State = {
    //createLogger("agilesitesng.wem")
    SbtWeb.withActorRefFactory(state, "Ngn") {
      arf =>
        println("************ NgSettings init ")
        val deployer = arf.actorOf(Deployer.actor(), "Deployer")
        val f = deployer ! Login(url, user, password)
        val newState = state.put(ngDeployKey, deployer)
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

  val actorSettings = Seq(ngDeployer <<= state map (_.get(ngDeployKey).get))

}

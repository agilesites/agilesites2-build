/**
 * Created by msciab on 26/06/15.
 */
package agilesitesng

import agilesitesng.wem.Wem.Reply
import sbt._
import sbt.Keys._
import java.net.URL

import argonaut._, Argonaut._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import akka.actor.{PoisonPill, ActorRef}
import akka.util.Timeout
import akka.pattern.gracefulStop
import akka.pattern.ask

import com.typesafe.sbt.web.SbtWeb

import agilesites.config.AgileSitesConfigPlugin
import agilesitesng.wem._

object AgileSitesNgPlugin extends AutoPlugin {

  val wem = config("wem")
  val wemHubKey = AttributeKey[ActorRef]("wem-hub")
  implicit val timeout = Timeout(3.second)

  object autoImport {
    val hub = taskKey[ActorRef]("Wem Hub")
    val get = inputKey[String]("Wem Get")
  }

  import autoImport._
  import AgileSitesConfigPlugin.autoImport._


  private def finish(state: State): State = {
    state.get(wemHubKey) map {
      hubActor =>
        hubActor ! Hub.Finish()
        gracefulStop(hubActor, 1.second)
    }
    state.remove(wemHubKey)
  }

  private def init(url: java.net.URL, user: String, password: String, state: State): State = {
    SbtWeb.withActorRefFactory(state, "agilesitesng.AgileSitesNgPlugin") {
      arf =>
        val hub = arf.actorOf(Hub.hubActor())
        val f = hub ! Hub.Init(Some(url), Some(user), Some(password))
        val newState = state.put(wemHubKey, hub)
        // TODO
        //newState.addExitHook(s: sbt.State => finish(s))
        newState
    }
  }

  override def globalSettings: Seq[Setting[_]] = super.globalSettings ++
    Seq(
      onLoad in Global := (onLoad in Global).value andThen
        (init(new URL(sitesUrl.value), sitesUser.value, sitesPassword.value, _)),
      onUnload in Global := (onUnload in Global).value andThen
        (finish)
    )

  def getTask = get  := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val ref = (hub in wem).value
    //println(s"!!!!${ref}")
    if (args.size > 0) {
      println(">>>"+args.head)
      val rf = ref ? Hub.Get(args.head)
      val Reply(json) = Await.result(rf, 3.second).asInstanceOf[Reply]
      val r = json.spaces2
      println(r)
      json
    } else {
      s"""{ "error": "no args" }"""
    }
  }

  override def requires = SbtWeb && AgileSitesConfigPlugin

  override def trigger = AllRequirements

  override val projectConfigurations = Seq(wem)
  override val projectSettings = inConfig(wem) {
      Seq(hub  <<= state map (_.get(wemHubKey).get),
      getTask)
    }
}

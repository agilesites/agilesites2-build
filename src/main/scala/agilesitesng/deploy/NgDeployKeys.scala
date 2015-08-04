package agilesitesng.deploy

import akka.actor.{ActorRef, Actor}
import sbt._

/**
 * Created by msciab on 03/07/15.
 */
object NgDeployKeys {
  val ng = config("ng")
  val deploy = taskKey[Unit]("AgileSitesNg deploy")
  val ngDeployer = taskKey[ActorRef]("Actor for Deployment")
}

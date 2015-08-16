package agilesitesng.deploy

import akka.actor.{ActorRef, Actor}
import sbt._

/**
 * Created by msciab on 03/07/15.
 */
object NgDeployKeys {
  val ng = config("ng")
  val deploy = taskKey[Unit]("AgileSitesNg deploy")
  val spoon = inputKey[File]("invoke spoon")
  val service = inputKey[Unit]("invoke a service")
  val ngDeployHub = taskKey[ActorRef]("Actor for Deployment")
  val ngSpoonClasspath = taskKey[Seq[File]]("spoon classpath")
  val ngSpoonProcessorJars = settingKey[Seq[File]]("processors jar")
  val ngSpoonProcessors = settingKey[Seq[String]]("spoon processors")


}

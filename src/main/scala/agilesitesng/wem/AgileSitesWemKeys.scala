package agilesitesng.wem

import akka.actor.ActorRef
import sbt._

/**
 * Created by msciab on 02/07/15.
 */
object AgileSitesWemKeys {
  val wem  = config("wem")
  val hub = taskKey[ActorRef]("Wem Hub")
  val get = inputKey[String]("Wem Get")
  val post = inputKey[String]("Wem Post")
  val put = inputKey[String]("Wem Put")
  val delete = inputKey[String]("Wem delete")
  val setup = inputKey[String]("Wem setup")
  val ngProcessAnnotations = taskKey[Seq[File]]("Annotation processor")

  val ngSpoonSources = taskKey[Unit]("Spoon processor")
  val ngSpoonClasspath = taskKey[Seq[File]]("AgileSites Populate Classpath")
}

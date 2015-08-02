package agilesitesng.install

import akka.actor.ActorRef
import sbt._

/**
 * Created by msciab on 02/07/15.
 */
object AgileSitesInstallKeys {

  lazy val ngConcatJavaMap = settingKey[Map[File, PathFinder]]("map concatenation")

  lazy val ngTagWrapperGen = taskKey[Unit]("Generate Tag Wrappers")

  lazy val ngConcatJava = taskKey[Unit]("ng Concatenate Java source files")

}

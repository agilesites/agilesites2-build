package agilesites.build.util

import java.util.Properties

import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._

/**
 * Created by msciab on 08/02/15.
 */
trait UtilSettings extends Utils {
  this: AutoPlugin =>

  // where the property files are
  lazy val utilProperties = settingKey[Seq[String]]("AgileSites Property files")

  // read all the properties in a single property map
  lazy val utilPropertyMap = settingKey[Map[String, String]]("AgileSites Property Map")

  lazy val utilPropertyMapTask = utilPropertyMap := {
    val prp: Properties = new Properties
    for (prpFileName <- utilProperties.value) {
      val prpFile = file(prpFileName)

      if (!prpFile.exists) {
        //log.info("not found property file " + prpFile)
      } else {
        System.out.println("loading " + prpFile)
        prp.load(new java.io.FileInputStream(prpFile))
      }
    }
    val map = prp.asScala.toMap
    for ((k, v) <- map)
      println(s"${k}=${v}")

    map
  }

  // display a prompt with the project name
  lazy val utilShellPromptTask = shellPrompt in ThisBuild := {
    state => Project.extract(state).currentRef.project + "> "
  }
  val utilSettings = Seq(
    utilProperties := Seq("agilesites.properties"),
    utilShellPromptTask,
    utilPropertyMapTask
  )

}

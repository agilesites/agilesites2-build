package agilesites.config

import java.util.Properties

import agilesites.Utils
import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._

/**
 * Created by msciab on 08/02/15.
 */
trait UtilSettings extends Utils {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

   lazy val utilPropertyMapTask = utilPropertyMap := {
    val prp: Properties = new Properties
    for (prpFileName <- utilProperties.value) {
      val prpFile = file(prpFileName)
      if (!prpFile.exists) {
        System.out.println("not found property file " + prpFile)
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
    utilShellPromptTask,
    utilPropertyMapTask
  )
}

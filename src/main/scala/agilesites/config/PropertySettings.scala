package agilesites.config

import java.util.Properties

import agilesites.Utils
import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._

/**
 * Created by msciab on 08/02/15.
 */
trait PropertySettings extends Utils {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

  val profile = Option(System.getProperty("profile")).map(Seq(_)).getOrElse(Nil)

  val propertyFiles = Seq(
    "agilesites.dist.properties",
    "agilesites.properties",
    "agilesites.local.properties") ++
    profile.map(x =>
      s"agilesites.${x}.properties")

  lazy val utilPropertyMapTask = utilPropertyMap in Global := {
    val prp: Properties = new Properties
    for (prpFileName <- utilProperties.value) {
      val prpFile = baseDirectory.value / prpFileName
      if (prpFile.exists) {
        System.out.println(">>> " + prpFile)
        prp.load(new java.io.FileInputStream(prpFile))
      }
    }

    val map = prp.asScala.toMap
    for ((k, v) <- map)
      println(s"${k}=${v}")

    map
  }

  lazy val uidPropertyMapTask = uidPropertyMap in Global := {
    val prp: Properties = new Properties
    val prpFile = baseDirectory.value / "src" / "main" / "resources" / sitesFocus.value / "uid.properties"
    if (prpFile.exists) {
      System.out.println(">>> " + prpFile)
      prp.load(new java.io.FileInputStream(prpFile))
    }
    prp.asScala.toMap
  }

  // display a prompt with the project name
  lazy val utilShellPromptTask = shellPrompt in ThisBuild := {
    state =>
      Project.extract(state).currentRef.project +
        Option(System.getProperty("profile")).map("[" + _ + "]> ").getOrElse("> ")
  }

  val propertySettings = Seq(
    utilProperties in Global := propertyFiles,
    utilShellPromptTask,
    utilPropertyMapTask,
    uidPropertyMapTask
  )
}

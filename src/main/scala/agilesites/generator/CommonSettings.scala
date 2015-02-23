package agilesites.generator

import sbt._
import Keys._
import agilesites.plugin.AgileSitesConfig

trait CommonSettings { 
  this: AutoPlugin with AgileSitesConfig =>
    
  lazy val asJfxJar = taskKey[Seq[File]]("classpath generator")

  // Tasks
  val asJfxJarTask = asJfxJar := {
    val javaHome = file(System.getProperty("java.home"))
    val jfxJar = javaHome / "lib" / "jfxrt.jar"
    if (!jfxJar.exists)
      throw new RuntimeException("JavaFX not detected (needs Java runtime 7u06 or later): " + jfxJar.getPath)
    Seq(jfxJar)
  }

  val commonSettings = Seq(asJfxJarTask)

}
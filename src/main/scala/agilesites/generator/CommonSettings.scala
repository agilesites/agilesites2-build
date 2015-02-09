package agilesites.generator

import sbt._
import Keys._
import agilesites.build.AgileSitesConfig

trait CommonSettings { 
  this: AutoPlugin with AgileSitesConfig =>
    
  lazy val asJfxJar = taskKey[Seq[File]]("classpath generator")

  // Utils
  def exec(args: Seq[String], home: File, cp: Seq[File]) = {

    //for (p <- cp)
     // println(p)

    Fork.java(ForkOptions(
      runJVMOptions = "-cp" :: cp.map(_.getAbsolutePath).mkString(java.io.File.pathSeparator) :: Nil,
      workingDirectory = Some(home)), args)
  }

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
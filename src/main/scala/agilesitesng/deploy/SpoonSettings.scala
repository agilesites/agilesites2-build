package agilesitesng.deploy

import java.io.File

import sbt.Keys._
import sbt._

/**
 * Created by msciab on 06/08/15.
 */
trait SpoonSettings {
  this: AutoPlugin =>

  import NgDeployKeys._

  /* old spoon task
 val spoonTask = spoon in ng := {
   val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
   var source = baseDirectory.value / "src" / "main" / "java"
   val target = file("target") / "groovy"
   val classpath = (managedClasspath in Compile).value.files.map(_.getAbsolutePath)
   target.mkdirs()

   val hub = ngDeployHub.value
   hub ! SpoonInit(source.getAbsolutePath, target.getAbsolutePath, classpath)
   val f = hub ? SpoonRun(args)
   val SpoonReply(result) = Await.result(f, 10.seconds).asInstanceOf[SpoonReply]
   result
   file(".")
 }*/


  val spoonTask = spoon in ng := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    var source = baseDirectory.value / "src" / "main" / "java"
    val target = baseDirectory.value / "target" / "groovy"

    val spool = File.createTempFile("spoon", ".ser", baseDirectory.value / "target")

    target.mkdirs
    val sourceClasspath = (managedClasspath in Compile).value.files.map(_.getAbsolutePath).mkString(File.pathSeparator)
    val spoonClasspath = ngSpoonClasspath.value.map(_.getAbsolutePath).mkString(File.pathSeparator)
    val processors = ngSpoonProcessors.value.mkString(File.pathSeparator)

    val jvmOpts = Seq("-cp", spoonClasspath, s"-Dspoon.spool=${spool.getAbsolutePath}")
    val runOpts = Seq("spoon.Launcher",
      "--source-classpath", sourceClasspath,
      "--processors", processors,
      "-i", source.getAbsolutePath,
      "-o", target.getAbsolutePath
    ) ++ args

    //println(jvmOpts.mkString("\n"))
    //println(runOpts.mkString("\n"))

    val forkOpt = ForkOptions(
      runJVMOptions = jvmOpts,
      workingDirectory = Some(baseDirectory.value))

    Fork.java(forkOpt, runOpts)

    spool
    //target
  }

  val spoonSettings = Seq(ngSpoonClasspath <<= (update, ngSpoonProcessorJar) map { (report, extraJar) =>
    val jar = if (extraJar.nonEmpty && extraJar.get.exists())
      Seq(extraJar.get)
    else Seq()
    jar ++ report.select(configurationFilter("spoon"))
  }
    , ngSpoonProcessorJar := None
    , ngSpoonProcessors := Seq(
      "SiteAnnotation",
      "TemplateAnnotation",
      "CSElementAnnotation",
      "ContentDefinitionAnnotation")
      .map(x => s"agilesitesng.deploy.spoon.${x}Processor")
    , ivyConfigurations += config("spoon")
    , libraryDependencies ++= Seq(
      "net.openhft" % "spoon-core" % "4.3.0" % "spoon"
      , "org.scala-lang" % "scala-library" % "2.10.5" % "spoon")
    , spoonTask
  )
}

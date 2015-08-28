package agilesitesng.deploy

import java.io.File

import agilesites.AgileSitesConstants
import sbt.Keys._
import sbt._

/**
 * Created by msciab on 06/08/15.
 */
trait SpoonSettings {
  this: AutoPlugin =>

  import NgDeployKeys._

  val spoonTask = spoon in ng := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val uid = baseDirectory.value / "src" / "main" / "resources" / name.value / "uid.properties"
    val source = baseDirectory.value / "src" / "main" / "java"
    val target = baseDirectory.value / "target" / "groovy"
    val spool = baseDirectory.value / "target" / "spoon-spool.json"
    val extraJars = ngSpoonProcessorJars.value
    val log = streams.value.log

    target.mkdirs
    spool.getParentFile.mkdirs

    val sourceClasspath = (extraJars++(managedClasspath in Compile).value.files.map(_.getAbsolutePath)).mkString(File.pathSeparator)
    val spoonClasspath = (extraJars++ngSpoonClasspath.value.map(_.getAbsolutePath)).mkString(File.pathSeparator)
    val processors = ngSpoonProcessors.value.mkString(File.pathSeparator)

    val jvmOpts = Seq(
      "-cp", spoonClasspath,
      s"-Dspoon.spool=${spool.getAbsolutePath}",
      s"-Duid.properties=${uid.getAbsolutePath}",
      s"-Dspoon.outdir=${target.getAbsolutePath}"
    )
    val runOpts = Seq("agilesitesng.deploy.spoon.SpoonMain",
      "--source-classpath", sourceClasspath,
      "--processors", processors,
      "-i", source.getAbsolutePath,
      "-o", target.getAbsolutePath
    ) ++ args


    log.debug( (jvmOpts++runOpts).mkString("\n"))

    val forkOpt = ForkOptions(
      runJVMOptions = jvmOpts,
      workingDirectory = Some(baseDirectory.value))

    Fork.java(forkOpt, runOpts)

    spool
  }

  val spoonSettings = Seq(ngSpoonClasspath <<= (update, ngSpoonProcessorJars) map {
    (report, extraJars) =>
      extraJars ++ report.select(configurationFilter("spoon"))
  }
    , ngSpoonProcessorJars := Nil
    , ngSpoonProcessors := Seq(
       "SiteAnnotation"
      ,"AttributeEditorAnnotation"
      //,"AttributeAnnotation"
      //,"SiteEntryAnnotation"
      ,"TemplateAnnotation"
      /*,"CSElementAnnotation"
      ,"ContentDefinitionAnnotation"
      ,"ParentDefinitionAnnotation"*/)
      .map(x => s"agilesitesng.deploy.spoon.${x}Processor")
    , ivyConfigurations += config("spoon")
    , libraryDependencies ++= AgileSitesConstants.spoonDependencies
    , spoonTask
  )
}

package agilesites.deploy

import agilesites.Utils
import sbt.Keys._
import sbt._

trait AnnotationSettings extends Utils {
  this: AutoPlugin =>

  import agilesites.setup.AgileSitesSetupKeys._

  val processAnnotations = Def.task {

    val comp: Compiler.Compilers = (compilers in Compile).value
    val mcp: Seq[File] = (managedClasspath in Compile).value.files
    val dcp: Seq[File] = asTomcatClasspath.value.filter(_.getName.startsWith("agilesites2-build"))
    // (dependencyClasspath in Compile).value.files
    val src: File = (sourceDirectory in Compile).value
    val out: File = (sourceManaged in Compile).value
    val log = streams.value.log
    val in = (src ** "*.java").get
    val ids = baseDirectory.value / "src" / "main" / "resources" / name.value / "uid.properties"

    ids.getParentFile.mkdirs
    out.mkdirs

    val opt = Seq(
      "-proc:only",
      "-processor",
      "agilesites.IndexProcessor",
      s"-Asite=${name.value}",
      s"-Auid=${ids.getAbsolutePath}",
      "-s",
      out.getAbsolutePath)

    try {
      comp.javac(in, mcp ++ dcp, out, opt)(log)
    } catch {
      case ex: Throwable =>
        log.error(ex.getMessage)
    }
    Seq(out / name.value / "Index.java")
  }

  val annotationSettings = Seq(
    sourceGenerators in Compile ++= Seq(processAnnotations.taskValue)
  )
}
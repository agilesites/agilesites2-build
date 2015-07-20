package agilesitesng.wem

import sbt._
import sbt.Keys._
import scala.io.Source
import agilesites.Utils
import agilesitesng.wem.Protocol.Annotation

trait AnnotationSettings extends Utils {
  this: AutoPlugin =>

  val processAnnotations = Def.task {

    val comp: Compiler.Compilers = (compilers in Compile).value
    val mcp: Seq[File] = (managedClasspath in Compile).value.files
    val src: File = (sourceDirectory in Compile).value
    val out: File = (sourceManaged in Compile).value
    val log = streams.value.log
    val ids = baseDirectory.value / "src" / "main" / "resources" / name.value / "uid.properties"
    val in = (src ** "*.java").get
    ids.getParentFile.mkdirs
    out.mkdirs

    //println(mcp.mkString("\n"))
    val annotations = java.io.File.createTempFile("annotation", ".txt")
    val opt = Seq(
      "-proc:only",
      "-processor",
      "agilesites.IndexProcessor",
      s"-Asite=${name.value}",
      s"-Auid=${ids.getAbsolutePath}",
      s"-Aout=${annotations.getAbsolutePath}",
      "-s",
      out.getAbsolutePath)

    // collecting annotation with an annotation processor
    try {
      comp.javac(in, mcp, out, opt)(log)
    } catch {
      case ex: Throwable =>
        log.error(ex.getMessage)
    }

    val ref = (AgileSitesWemKeys.hub in AgileSitesWemKeys.wem).value
    for (ann <- Source.fromFile(annotations).getLines())
      ref ! Annotation(ann)

    Seq(out / name.value / "Index.java")
  }

  val annotationSettings = Seq(sourceGenerators in Compile ++= Seq(processAnnotations.taskValue))

}
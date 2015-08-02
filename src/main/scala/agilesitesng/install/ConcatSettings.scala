package agilesitesng.install

import java.io.{File, FileReader}
import java.util.Date

import agilesites.Utils
import sbt._
import sbt.Keys._
import agilesites.config._

import scala.io.Source
import scala.xml._

trait ConcatSettings extends Utils  {
  this: AutoPlugin =>

  import AgileSitesInstallKeys._
  import AgileSitesConfigKeys._

  lazy val ngConcatJavaTask = ngConcatJava := {

    //val base = baseDirectory.value / "src" / "main" / "java" / "agilesitesng" / "core" * "*.java"
    //val out = file("src") / "main" / "resources" / "aaagile" / "ElementCatalog" / "AAAgileLib.java"

    for ((output, input) <- ngConcatJavaMap.value) {

      val lines = for {
        file <- input.get
        line <- Source.fromFile(file).getLines()
      } yield line


      val thePackage = lines.filter(_.startsWith("package ")).head.toString
      val imports = lines.filter(_.startsWith("import ")).toSeq
      val bodies = lines.filter(x => !(x.startsWith("import ") || x.startsWith("package "))).toSeq
      val helloWorld = s"AAAgileServices ${version.value} built on ${new Date()}"
      val fw = new java.io.FileWriter(output)
      val result = (imports ++ bodies).mkString(s"${thePackage}\n\n", "\n",
        s"""
          |public class Hello { public String toString() { return "${helloWorld}"; } }
        """.stripMargin)
      println(result)
      fw.write(result)
      fw.close
    }
  }

  def concatSettings = Seq(ngConcatJavaTask,
    ngConcatJavaMap := Map.empty[File, PathFinder])
}
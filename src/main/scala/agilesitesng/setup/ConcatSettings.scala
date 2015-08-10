package agilesitesng.setup

import java.io.File
import java.util.Date

import agilesites.Utils
import sbt.Keys._
import sbt.{AutoPlugin, PathFinder}

import scala.io.Source

/**
 * Created by msciab on 04/08/15.
 */
trait ConcatSettings extends Utils {
  this: AutoPlugin =>

  import NgSetupKeys._

  /**
   * Concate java in a groovy scripts, fixing some stuff in the process
   * (decomment  all the lines ending with //+)
   * (add all the lines ending in //-)
   */
  lazy val ngConcatJavaTask = ngConcatJava := {

    //val base = baseDirectory.value / "src" / "main" / "java" / "agilesitesng" / "core" * "*.java"
    //val out = file("src") / "main" / "resources" / "aaagile" / "ElementCatalog" / "AAAgileLib.java"

    for ((output, input) <- ngConcatJavaMap.value) {

      val lines = for {
        file <- input.get
        line <- Source.fromFile(file).getLines()
      } yield line

      def removeMinus(line: String) = if(line.trim.endsWith("//-")) "\n" else line

      def deCommentPlus(line: String) = if(line.trim.startsWith("//+"))
        line.trim.substring(3)+"\n"
      else line

      val thePackage = lines.filter(_.startsWith("package ")).head.toString
      val imports = lines.filter(_.startsWith("import ")).toSeq
      val bodies = lines.filter(x => !(x.startsWith("import ") || x.startsWith("package "))).toSeq
      val helloWorld = s"Concat of ${thePackage} ${version.value} built on ${new Date()}"
      val fw = new java.io.FileWriter(output)
      val result = (imports ++ bodies)
        .map(removeMinus(_))
        .map(deCommentPlus(_))
        .mkString(s"${thePackage}\n\n", "\n",
       s"""
           |public class Version { public String toString() { return "${helloWorld}"; } }
        """.stripMargin)
      //println(result)
      fw.write(result)
      fw.close
    }
  }

  def concatSettings = Seq(ngConcatJavaTask,
    ngConcatJavaMap := Map.empty[File, PathFinder])
  //override def projectSettings = this.projectSettings ++ Seq(ngConcatJavaTask,
  //  ngConcatJavaMap := Map.empty[File, PathFinder])
}

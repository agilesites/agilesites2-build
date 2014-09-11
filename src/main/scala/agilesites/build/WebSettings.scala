package agilesites.build

import sbt._
import Keys._
import java.io.File
import agilesites.build.util.Utils
import scala.util.matching.Regex
import java.security.MessageDigest
import java.io.FileInputStream
import java.math.BigInteger
import java.util.Formatter
import java.util.regex.Pattern
import agilesites.build.util.WebUtil


trait WebSettings extends Utils with WebUtil {
  this: Plugin with ConfigSettings =>

  def fingerPrintMap(files: Seq[File], prefixLen: Int): Seq[(Regex, String)] =
    for (file <- files) yield {
      // normalize filename
      val normfile = deprefixNomalizeFile(file, prefixLen)
      // get md5 of it
      val md5sum = md5(file)
      // chreate a mapping from the normal to the finger printed version
      val hashedfile = assetHashedFilePath(normfile, md5sum)
      (new Regex(Pattern.quote(normfile)), hashedfile)
      
    }

  def replaceWithMap(src: File, replacements: Seq[(Regex, String)]) = {
    def rep(x: String, y: Tuple2[Regex, String]) = y._1.replaceAllIn(x, y._2)
    replacements.foldLeft(readFile(src))(rep _)
  }

  lazy val asWebFolder = taskKey[File]("AgileSites assets folder ")

  lazy val asWebIncludeFilter = taskKey[FileFilter]("Web Assets to include")

  lazy val asWebExcludeFilter = taskKey[FileFilter]("Web Assets to exclude")

  lazy val asWebFingerPrintFilter = taskKey[FileFilter]("Web Assets to finger print")

  lazy val asWebPackage = taskKey[Seq[java.io.File]]("package web asset with finger printing")

  val asWebPackageTask = asWebPackage := {
    val src = asWebFolder.value
    val tgt = (resourceManaged in Compile).value
    val log = streams.value.log

    val nsrc = src.getPath.length
    val ntgt = tgt.getPath.length
    val files = Seq(src).descendantsExcept(asWebIncludeFilter.value, asWebExcludeFilter.value).get.filter(_.isFile)
    val toFingerPrint = asWebFingerPrintFilter.value

    val fpMap = fingerPrintMap(files, nsrc)
    //println(fpMap)
    val destlist = for (file <- files if file.isFile) yield {
      val subfile = file.getPath.substring(nsrc)
      val dest = tgt / subfile
      //val destOrig = tgt / (subfile+".orig")
      //val destMap = tgt / (subfile+".map")
      if (toFingerPrint.accept(file)) {
        writeFile(dest, replaceWithMap(file, fpMap), log)
        //writeFile(destMap , fpMap.mkString("\n"), log)
        //println(s"*${dest} ${destMap} ")
        print("#")
      } else {
        //println(s">${subfile} ")
        IO.copyFile(file, dest)
        print(".")
      }
      dest
    }
    println()
    // write the index too
    val sitename = normalizeSiteName(asSites.value.split(",").head)
    val assetIndexFile = tgt /  sitename /"assets.txt"
    val assetIndexBody = destlist.map(_.getPath.substring(ntgt).replace(File.separator, "/")).mkString("\n")
    writeFile(assetIndexFile, assetIndexBody, log)
    if (log != null)
      log.info(s"copying #${destlist.size} files")
    destlist ++ Seq(assetIndexFile)
  }

  /*
  resourceGenerators in Compile += Def.task {
	val src = WebKeys.assets.value
	val tgt = (resourceManaged in Compile).value
	val log = streams.value.log
    recursiveCopy(src, tgt, log)(notLess) ++ 
    Seq(recursiveIndex(src, tgt / "telmore" / "assets.txt", log)(notLess))
  }.taskValue
*/

  val webSettings = Seq(
    asWebFolder := baseDirectory.value / "src" / "main" / "assets",
    asWebIncludeFilter := AllPassFilter,
    asWebExcludeFilter := NothingFilter,
    asWebFingerPrintFilter := GlobFilter("*.css") | GlobFilter("*.html") | GlobFilter("*.js"),
    asWebPackageTask)
}
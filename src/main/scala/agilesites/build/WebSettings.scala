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

  def fingerPrintMap(staticPrefix: String, files: Seq[File], prefixLen: Int): Seq[(Regex, (String, String))] =
    for (file <- files) yield {
      // normalize filename
      val normfile = deprefixNomalizeFile(file, prefixLen)
      // get md5 of it
      val md5sum = md5(file)
      // chreate a mapping from the normal to the finger printed version
      val hashedfile = staticPrefix + assetHashedFilePath(normfile, md5sum)
      val fileAndHash = s"/${normfile}\n${md5sum}"

      val re = new Regex(s"(\\.\\./)*\\Q${normfile}\\E")
      val r = ( re, fileAndHash -> hashedfile)
      //println(r)
      r
    }

  def replaceWithMap(src: File, replacements: Seq[(Regex, (String, String))]) = {
    def rep(x: String, y: Tuple2[Regex, Tuple2[String, String]]) = y._1.replaceAllIn(x, y._2._2)
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
    val pref = asStaticPrefix.value

    val nsrc = src.getPath.length
    val ntgt = tgt.getPath.length
    val files = Seq(src).descendantsExcept(asWebIncludeFilter.value, asWebExcludeFilter.value).get.filter(_.isFile)
    val toFingerPrint = asWebFingerPrintFilter.value

    val fpMap = fingerPrintMap(pref, files, nsrc)
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
    val assetIndexFile = tgt / sitename / "assets.txt"
    val assetIndexBody = fpMap.map(_._2._1).mkString("\n")
    //destlist.map(_.getPath.substring(ntgt).replace(File.separator, "/")).mkString("\n")
    writeFile(assetIndexFile, assetIndexBody, log)
    if (log != null)
      log.info(s"copying #${destlist.size} files")
    destlist ++ Seq(assetIndexFile)
  }

  val webSettings = Seq(
    asWebFolder := baseDirectory.value / "src" / "main" / "assets",
    asWebIncludeFilter := AllPassFilter,
    asWebExcludeFilter := NothingFilter,
    asWebFingerPrintFilter := GlobFilter("*.css") | GlobFilter("*.html") | GlobFilter("*.js"),
    asWebPackageTask)
}
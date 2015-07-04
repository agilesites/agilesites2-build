package agilesites.web

import java.io.File
import agilesites.Utils
import sbt.Keys._
import sbt._

import scala.util.matching.Regex

trait WebSettings extends Utils with WebUtil {
  this: AutoPlugin =>

  import AgileSitesWebKeys._
  import agilesites.config.AgileSitesConfigKeys._

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
      val r = (re, fileAndHash -> hashedfile)
      //println(r)
      r
    }

  def replaceWithMap(src: File, replacements: Seq[(Regex, (String, String))]) = {
    def rep(x: String, y: Tuple2[Regex, Tuple2[String, String]]) = y._1.replaceAllIn(x, y._2._2)
    replacements.foldLeft(readFile(src))(rep _)
  }

  val webPackageTask = webPackage := {
    val src = webFolder.value
    val tgt = (resourceManaged in Compile).value
    val log = streams.value.log
    val pref = webStaticPrefix.value

    val nsrc = src.getPath.length
    val ntgt = tgt.getPath.length
    val files = Seq(src).descendantsExcept(webIncludeFilter.value, webExcludeFilter.value).get.filter(_.isFile)
    val toFingerPrint = webFingerPrintFilter.value

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
    val sitename = normalizeSiteName(sitesFocus.value.split(",").head)
    val assetIndexFile = tgt / sitename / "assets.txt"
    val assetIndexBody = fpMap.map(_._2._1).mkString("\n")
    //destlist.map(_.getPath.substring(ntgt).replace(File.separator, "/")).mkString("\n")
    writeFile(assetIndexFile, assetIndexBody, log)
    if (log != null)
      log.info(s"copying #${destlist.size} files")
    destlist ++ Seq(assetIndexFile)
  }

  val webSettings = Seq(webPackageTask)
}
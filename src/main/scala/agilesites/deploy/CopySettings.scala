package agilesites.deploy

import java.net.URLDecoder

import agilesites.Utils
import com.ning.http.client.StringPart
import com.ning.http.multipart.FilePart
import dispatch.Defaults._
import dispatch._
import sbt.Keys._
import sbt._

trait CopySettings extends Utils {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.deploy.AgileSitesDeployPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  val asScpTask = asScp := {

    val log = streams.value.log
    val fromTo = asScpFromTo.value

    if (fromTo.nonEmpty) {

      val targetUri = fromTo.get._2.toURI
      val srcFile = fromTo.get._1
      val proto = targetUri.getScheme

      if (proto == "file") {
        val f = file(targetUri.getPath)
        f.getParentFile.mkdirs()
        IO.copyFile(srcFile, f)
        log.info("+++ " + f)
      } else if (proto == "scp") {
        val Array(user, pass) = targetUri.getUserInfo.split(":")
        val host = targetUri.getHost
        val path = targetUri.getPath
        val port = if (targetUri.getPort == -1) 22 else targetUri.getPort
        if (!ScpTo.scp(srcFile.getAbsolutePath(), user, pass, host, port, path))
          log.error("!!! cannot upload ")
        else
          log.info("+++ uploaded " + targetUri)
      } else {
        log.error("unknown protocol for asUploadTarget")
      }
    }
  }


  val asCopyStaticsTask = asCopyStatics := {
    val base = baseDirectory.value
    val tgt = sitesWebapp.value
    val s = streams.value
    val src = base / "src" / "main" / "static"
    s.log.debug(" from" + src)
    val l = recursiveCopy(src, file(tgt), s.log)(x => true)
    println("*** copied " + (l.size) + " static files")
  }

  val copyHtmlTask = Def.task {
    val base = baseDirectory.value
    val dstDir = (resourceManaged in Compile).value
    val s = streams.value

    val srcDir = base / "src" / "main" / "static"
    s.log.debug("copyHtml from" + srcDir)
    recursiveCopy(srcDir, dstDir, s.log)(isHtml)
  }


  val deploySettings = Seq(
    asCopyStaticsTask,
    asScpTask,
    asScpFromTo := None,
    resourceGenerators in Compile += copyHtmlTask.taskValue)
}
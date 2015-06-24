package agilesites.deploy

import dispatch._
import dispatch.Defaults._
import java.net.URLDecoder
import com.ning.http.client.StringPart
import com.ning.http.multipart.FilePart

import agilesites.Utils
import sbt.Keys._
import sbt._

trait AnnotationSettings extends Utils {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._
  import agilesites.deploy.AgileSitesDeployPlugin.autoImport._

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

  def uploadJar(uri: URL, jar: File, log: Logger, site: String, username: String, password: String) = {

    // shut up the SLF4J
    System.setProperty(org.slf4j.impl.SimpleLogger.LOG_KEY_PREFIX + "com.ning.http.client", "warn")

    val path = URLDecoder.decode(uri.getPath, "UTF-8").substring(1)
    val base = host(uri.getHost, uri.getPort)
    val req = base / path / "Satellite" <<? Map("pagename" -> "AAAgileSetup")
    import scala.collection.JavaConverters._

    // init, requesting the cookie
    val reqHello = req <<? Map("op" -> "init", "site" -> site, "username" -> username, "password" -> password)
    log.debug(reqHello.toRequest.getRawUrl)

    val cookies = Http(reqHello).apply.getCookies.asScala
    log.debug(s"hello ${cookies} ")

    // status, requesting the status of the jars and a key to post
    val reqKey = cookies.foldLeft(req)(_.addCookie(_)) <<? Map("op" -> "info")
    log.debug(reqKey.toRequest.getRawUrl)
    val key = Http(reqKey).apply.getResponseBody.trim
    log.debug(s"key ${key} ")

    // upload the jar
    val reqFile = req.setMethod("POST").
      setHeader("Content-Type", "multipart/form-data").
      addBodyPart(new FilePart("jar", jar)).
      addBodyPart(new StringPart("op", "upload")).
      addBodyPart(new StringPart("_authkey_", key)).
      addBodyPart(new StringPart("username", username)).
      addBodyPart(new StringPart("password", password)).
      addBodyPart(new StringPart("site", site))

    val reqFile1 = cookies.foldLeft(reqFile)(_.addCookie(_))
    val resFile = Http(reqFile1).apply.getResponseBody.trim
    log.info(s"${resFile}")
    resFile
  }

  // package jar task - build the jar and copy it  to destination 
  val asPackageTask = asPackage := {
    val jar = (Keys.`package` in Compile).value
    val log = streams.value.log
    val site = sitesFocus.value
    val targetUri = new java.net.URI(sitesUrl.value)
    val info: String = targetUri.getUserInfo
    val (user: String, pass: String) =
      if (info != null) info.split(":")
      else (sitesUser.value, sitesPassword.value)
    uploadJar(new URL(sitesUrl.value), jar, log, site, user, pass)
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

  val sitesCheck = Def.task {
    if (sitesHello.value.isEmpty)
      throw new Exception(s"Sites must be up and running at ${sitesUrl.value}.")
  }

  val asDeployTask = asDeploy := Def.sequential(
    sitesCheck,
    asCopyStatics,
    asPackage,
    asPopulate
  ).value

  // package upload
  val asUploadTask = asUpload := {
    uploadJar(
      new URL(sitesUrl.value),
      (Keys.`package` in Compile).value,
      streams.value.log,
      sitesFocus.value,
      sitesUser.value,
      sitesPassword.value)
  }

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

  val deploySettings = Seq(asPackageTask,
    asDeployTask,
    asCopyStaticsTask,
    asUploadTask,
    asScpTask,
    asScpFromTo := None,
    resourceGenerators in Compile += copyHtmlTask.taskValue,
    asPopulate := cmov.toTask(" import_all @src/main/populate").value,
    sourceGenerators in Compile ++= Seq(processAnnotations.taskValue))
}
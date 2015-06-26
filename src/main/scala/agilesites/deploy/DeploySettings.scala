package agilesites.deploy

import dispatch._
import dispatch.Defaults._
import java.net.URLDecoder
import com.ning.http.client.StringPart
import com.ning.http.multipart.FilePart
import java.net.URL
import java.io.File

import agilesites.Utils
import sbt.Keys._
import sbt._

trait DeploySettings extends Utils {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._
  import agilesites.deploy.AgileSitesDeployPlugin.autoImport._

  def uploadJar(uri: URL, jar: File, log: Logger, site: String, siteId: Long, username: String, password: String) = {

    // shut up the SLF4J
    System.setProperty(org.slf4j.impl.SimpleLogger.LOG_KEY_PREFIX + "com.ning.http.client", "warn")

    val path = URLDecoder.decode(uri.getPath, "UTF-8").substring(1)
    val base = host(uri.getHost, uri.getPort)
    val req = base / path / "Satellite" <<? Map("pagename" -> "AAAgileSetup")
    import scala.collection.JavaConverters._

    // init, requesting the cookie
    println("init")
    val reqInit = req <<? Map("op" -> "init",
      "site" -> site, "siteid" -> siteId.toString,
      "username" -> username, "password" -> password)
    log.debug(reqInit.toRequest.getRawUrl)
    val res = Http(reqInit).apply
    val cookies = res.getCookies.asScala
    log.debug(s"init ${res.getResponseBody} ")

    // status, requesting the status of the jars and a key to post
    println("status")
    val reqKey = cookies.foldLeft(req)(_.addCookie(_)) <<? Map("op" -> "status")
    log.debug(reqKey.toRequest.getRawUrl)
    val key = Http(reqKey).apply.getResponseBody.trim
    log.debug(s"status ${key} ")

    // upload the jar
    println("upload")
    val reqFile = req.setMethod("POST").
      setHeader("Content-Type", "multipart/form-data").
      addBodyPart(new FilePart("jar", jar)).
      addBodyPart(new StringPart("op", "upload")).
      addBodyPart(new StringPart("_authkey_", key)).
      addBodyPart(new StringPart("username", username)).
      addBodyPart(new StringPart("password", password)).
      addBodyPart(new StringPart("siteid", siteId.toString))

    val reqFile1 = cookies.foldLeft(reqFile)(_.addCookie(_))
    val resFile = Http(reqFile1).apply.getResponseBody.trim
    log.info(s"${resFile}")
    resFile
  }

  // package jar task - build the jar and copy it  to destination 
  val asPackageTask = asPackage := {

    if (sitesHello.value.isEmpty)
      throw new Exception(s"Sites must be up and running at ${sitesUrl.value}.")

    val jar = (Keys.`package` in Compile).value
    val log = streams.value.log
    val site = sitesFocus.value
    val siteId = sitesFocusId.value
    val targetUri = new java.net.URI(sitesUrl.value)
    val info: String = targetUri.getUserInfo
    val (user: String, pass: String) =
      if (info != null) info.split(":")
      else (sitesUser.value, sitesPassword.value)

    uploadJar(new URL(sitesUrl.value), jar, log, site, siteId, user, pass)
  }


  val asDeployTask = asDeploy := Def.sequential(
    asCopyStatics,
    asPackage,
    asPopulate
  ).value

  val deploySettings = Seq(asPackageTask,
    asDeployTask,
    asPackageTask,
    asPopulate := cmov.toTask(" import_all @src/main/populate").value
  )
}
package agilesites.deploy

import agilesites.Utils
import sbt.Keys._
import sbt._

trait DeploySettings extends Utils with DeployUtil {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._
  import agilesites.deploy.AgileSitesDeployPlugin.autoImport._

  // package jar task - build the jar and copy it  to destination 
  val asPackageTask = asPackage := {
    val jar = (Keys.`package` in Compile).value
    val log = streams.value.log
    val sites = sitesFocus.value
    asPackageTarget.value match {
      case Some(url) =>
        val targetUri = new java.net.URI(url)
        val proto = targetUri.getScheme
        if (proto == "file") {
          val f = file(targetUri.getPath)
          f.getParentFile.mkdirs()
          IO.copyFile(jar, f)
          log.info("+++ " + f)
        } else if (proto == "scp") {
          val Array(user, pass) = targetUri.getUserInfo.split(":")
          val host = targetUri.getHost
          val path = targetUri.getPath
          val port = if (targetUri.getPort == -1) 22 else targetUri.getPort
          if (!ScpTo.scp(jar.getAbsolutePath(), user, pass, host, port, path))
            log.error("!!! cannot upload ")
          else
            log.info("+++ uploaded " + url)
        } else if (proto == "http") {
          println(targetUri)
          val info: String = targetUri.getUserInfo
          val (user: String, pass: String) = if (info != null) info.split(":")
          else (sitesUser.value, sitesPassword.value)
          uploadJar(new URL(url), jar, log, sites, user, pass)
        } else {
          log.error("unknown protocol for asUploadTarget")
        }
      case None =>
        val destDir = file(sitesShared.value) / "agilesites"
        val destJar = file(sitesShared.value) / "agilesites" / jar.getName
        destDir.mkdir
        IO.copyFile(jar, destJar)
        log.info("+++ " + destJar.getAbsolutePath)
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

  val sitesCheck = Def.task {
    if (sitesHello.value.isEmpty)
      throw new Exception(s"Sites must be up and running at ${sitesUrl.value}.")
  }

  val asSetupInit = Def.task {
    streams.value.log.info(
      httpCall("Setup", "op=init&site=%s".format(sitesFocus.value),
        sitesUrl.value, sitesUser.value, sitesPassword.value))
  }

  val asSetupDeploy = Def.task {
    streams.value.log.info(
      httpCall("Setup", "op=deploy&site=%s".format(sitesFocus.value),
        sitesUrl.value, sitesUser.value, sitesPassword.value))
  }

  val st = new StringTokenizer("")


  val asDeployTask = asDeploy := Def.sequential(
    sitesCheck,
    asCopyStatics,
    asPackage,
    asSetupInit,
    asSetupDeploy,
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

    //log.info(in.mkString("in:", " ", ""))
    //log.info(mcp.mkString("mcp: ", " ", ""))
    //log.info(dcp.mkString("dcp: ", " ", ""))
    //log.info(opt.mkString("opt: ", " ", ""))

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
    asPackageTarget := Some(utilPropertyMap.value.getOrElse("as.package.target", sitesUrl.value)),
    resourceGenerators in Compile += copyHtmlTask.taskValue,
    asPopulate := cmov.toTask(" import_all @src/main/populate").value,
    sourceGenerators in Compile ++= Seq(processAnnotations.taskValue)
  )
}
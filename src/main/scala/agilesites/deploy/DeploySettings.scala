package agilesites.deploy

import agilesites.Utils
import sbt.Keys._
import sbt._

trait DeploySettings extends Utils {
  this: AutoPlugin =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.deploy.AgileSitesDeployPlugin.autoImport._

  // package jar task - build the jar and copy it  to destination 
  val asPackageTask = asPackage := {
    val jar = (Keys.`package` in Compile).value
    val log = streams.value.log
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

  /**
   * Extract the index of the classes annotated with the @Index annotation
   */
  def extractClassAndIndex(file: File): Option[Tuple2[String, String]] = {
    import scala.io._

    //println("***" + file)

    var packageRes: Option[String] = None;
    var indexRes: Option[String] = None;
    var classRes: Option[String] = None;
    val packageRe = """.*package\s+([\w\.]+)\s*;.*""".r;
    val indexRe = """.*@Index\(\"(.*?)\"\).*""".r;
    val classRe = """.*class\s+(\w+).*""".r;

    if (file.getName.endsWith(".java") || file.getName.endsWith(".scala"))
      for (line <- Source.fromFile(file).getLines) {
        line match {
          case packageRe(m) =>
            //println(line + ":" + m)
            packageRes = Some(m)
          case indexRe(m) =>
            //println(line + ":" + m)
            indexRes = Some(m)
          case classRe(m) =>
            //println(line + ":" + m)
            classRes = Some(m)
          case _ => ()
        }
      }

    if (packageRes.isEmpty || indexRes.isEmpty || classRes.isEmpty)
      None
    else {
      val t = (indexRes.get, packageRes.get + "." + classRes.get)
      Some(t)
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

  // generate index classes from sources
  val generateIndexTask = Def.task {
    val analysis = (compile in Compile).value
    val dstDir = (resourceManaged in Compile).value
    val s = streams.value

    val groupIndexed =
      analysis.apis.allInternalSources. // all the sources
        map(extractClassAndIndex(_)). // list of Some(index, class) or Nome
        flatMap(x => x). // remove None
        groupBy(_._1). // group by (index, (index, List(class)) 
        map { x => (x._1, x._2 map (_._2))}; // lift to (index, List(class))

    //println(groupIndexed)

    val l = for ((subfile, lines) <- groupIndexed) yield {
      val file = dstDir / subfile
      val body = lines mkString("# generated - do not edit\n", "\n", "\n# by AgileSites build\n")
      writeFile(file, body, s.log)
      file
    }
    l.toSeq
  }

  val copyHtmlTask = Def.task {
    val base = baseDirectory.value
    val dstDir = (resourceManaged in Compile).value
    val s = streams.value

    val srcDir = base / "src" / "main" / "static"
    s.log.debug("copyHtml from" + srcDir)
    recursiveCopy(srcDir, dstDir, s.log)(isHtml)
  }

  // copy resources to the webapp task
  val asDeployTask = asDeploy := {
    val log = streams.value.log
    val url = sitesUrl.value
    if (sitesHello.value.isEmpty) {
      log.error(s"Sites must be up and running as ${url}.")
    } else {
      asPackage.value
      asCopyStatics.value
      log.info(httpCall("Setup", "&sites=%s".format(sitesFocus.value), url, sitesUser.value, sitesPassword.value))
    }
  }

  val deploySettings = Seq(asPackageTask, asDeployTask, asCopyStaticsTask,
    asPackageTarget := utilPropertyMap.value.get("as.package.target"),
    (resourceGenerators in Compile) ++= Seq(generateIndexTask.taskValue, copyHtmlTask.taskValue))

}
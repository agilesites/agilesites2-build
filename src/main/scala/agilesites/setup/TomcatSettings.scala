package agilesites.setup

import java.io.File

import agilesites.Utils
import sbt.Keys._
import sbt._

trait TomcatSettings extends Utils {
  this: AutoPlugin =>

  lazy val serverStop = taskKey[Unit]("Start Local Sites")
  lazy val serverStart = taskKey[Unit]("Stop Local Sites")

  def tomcatOpts(cmd: String, base: File, home: File, port: Int, classpath: Seq[File], debug: Boolean) = {

    val bin = base / "bin"
    val homeBin = home / "bin"
    val temp = base / "temp"

    val cp = (Seq(bin, homeBin) ++ classpath).map(_.getAbsolutePath).mkString(File.pathSeparator)

    val debugSeq = if (debug)
      "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000" :: Nil
    else Nil

    val opts = "-cp" :: cp ::
      "-Djava.net.preferIPv4Stack=true" ::
      "-Djava.io.tmpdir=" + (temp.getAbsolutePath) ::
      "-Dfile.encoding=UTF-8" :: "-Duser.timezone=UTC" ::
      "-Dorg.owasp.esapi.resources=$BASE/bin" ::
      "-Xms256m" :: "-Xmx1024m" :: "-XX:MaxPermSize=256m" ::
      s"-Dorg.owasp.esapi.resources=${bin.getAbsolutePath}" ::
      "-Dnet.sf.ehcache.enableShutdownHook=true" ::
      debugSeq

    val args = Seq("agilesites.SitesServer") ++ (cmd match {
      case "start" => Seq(port.toString, base.getAbsolutePath)
      case "stop" => Seq("stop", port.toString)
      case "status" => Seq("status", port.toString)
    })

    val env = Map("CATALINA_HOME" -> base.getAbsolutePath);

    (opts, args, env)
  }

  def tomcatEmbedded(cmd: String, base: File, home: File, port: Int, classpath: Seq[File], debug: Boolean) = {

    val (opts, args, env) = tomcatOpts(cmd: String, base, home, port, classpath, debug)

    //println (opts)

    val forkOpt = ForkOptions(
      runJVMOptions = opts,
      envVars = env,
      workingDirectory = Some(base))

    Fork.java(forkOpt, args)
  }

  def tomcatScript(base: File, home: File, port: Int, classpath: Seq[File], debug: Boolean, log: Logger) = {
    val (opts, args, env) = tomcatOpts("start", base, home, port, classpath, debug)

    val (set, ext, prefix) = if (File.pathSeparatorChar == ':')
      ("export", "sh", "#!/bin/sh")
    else ("set", "bat", "@echo off")

    val vars = env.map(x => s"${set} ${x._1}=${x._2}").mkString("", "\n", "")

    val java = new File(System.getProperty("java.home")) / "bin" / "java"

    val script =
      s"""|${prefix}
          |cd ${base.getAbsolutePath}
          |${vars}
          |${java.getAbsolutePath} ${opts.mkString(" ")} ${args.mkString(" ")}
       """.stripMargin

    //println(script)

    writeFile(new File("server." + ext), script, log)
    println("+++ created server." + ext)
  }

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  lazy val serverTask = server := {

    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val classpath = tomcatClasspath.value
    val port = sitesPort.value.toInt
    val base = sitesDirectory.value
    val home = file(sitesHome.value)
    val url = sitesUrl.value
    val log = streams.value.log
    val cs = file("webapps") / "cs"
    val cas = file("webapps") / "cas"
    val debug = args.size == 2 && args(1) == "debug"

    val usage = "usage: start  [debug]|stop|status|script [debug]"

    val ftcs = file(sitesWebapp.value) / "WEB-INF" / "futuretense_cs"
    if (!ftcs.exists())
      println(s"Sites not installed in ${sitesWebapp.value}")
    else
      args.headOption match {
        case None => println(usage)

        case Some("status") =>
          tomcatEmbedded("status", base, home, port, classpath, debug)

        case Some("stop") =>
          tomcatEmbedded("stop", base, home, port, classpath, debug)

        case Some("start") =>
          val tomcat = new Thread() {
            override def run() {
              try {
                println(s"*** Local Sites Server starting in port ${port} ***")
                val tomcatProcess = tomcatEmbedded("start", base, home, port, classpath, debug)
              } catch {
                case e: Throwable =>
                  e.printStackTrace
                  println(s"!!! Cannot start (Sites Server already running?)\nError: ${e.getMessage()}")
              }
            }
          }
          tomcat.start
          Thread.sleep(3000);
          if (tomcat.isAlive()) {
            println(" *** Waiting for Local Sites Server startup to complete ***")
            println(httpCallRaw(url + "/HelloCS"))
          }

        case Some("script") =>
          tomcatScript(base, home, port, classpath, debug, log)

        case Some(thing) =>
          println(usage)
      }

  }

  val tomcatSettings = Seq(serverTask,
    serverStop := {
      server.toTask(" stop").value
    },
    serverStart := {
      server.toTask(" start").value
    })
}
package agilesites.setup

import java.io.File

import agilesites.Utils
import agilesites.config.AgileSitesConfigPlugin
import sbt.Keys._
import sbt._

trait TomcatSettings extends Utils {
  this: AutoPlugin =>

  def tomcatOpts(base: File, home: File, port: Int, classpath: Seq[File], debug: Boolean) = {

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
      "-Dnet.sf.ehcache.enableShutdownHook=true" ::
      "-Dorg.owasp.esapi.resources=$BASE/bin" ::
      "-Xms256m" :: "-Xmx1024m" :: "-XX:MaxPermSize=256m" ::
      s"-Dorg.owasp.esapi.resources=${bin.getAbsolutePath}" :: debugSeq

    val args = Seq("agilesites.SitesServer", port.toString, base.getAbsolutePath)

    val env = Map("CATALINA_HOME" -> base.getAbsolutePath);

    (opts, args, env)
  }

  def tomcatEmbedded(base: File, home: File, port: Int, classpath: Seq[File], debug: Boolean) = {

    val (opts, args, env) = tomcatOpts(base, home, port, classpath, debug)

    //println (opts)

    val forkOpt = ForkOptions(
      runJVMOptions = opts,
      envVars = env,
      workingDirectory = Some(base))

    Fork.java(forkOpt, args)
  }

  def tomcatScript(base: File, home: File, port: Int, classpath: Seq[File], debug: Boolean, log: Logger) = {
    val (opts, args, env) = tomcatOpts(base, home, port, classpath, debug)

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

    writeFile(new File("server."+ext), script, log)
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

    val usage = "usage: start [debug]|stop|status|script [debug]"

    args.headOption match {
      case None => println(usage)

      case Some("status") =>
        try {
          new java.net.ServerSocket(port + 1).close
          println("Local Sites Server not running")
        } catch {
          case e: Throwable =>
            println("Local Sites Server running")
        }

      case Some("stop") =>
        try {
          println("*** stopping Local Sites Server ***")
          def sock = new java.net.Socket("127.0.0.1", port + 1)
          sock.getInputStream.read
          sock.close
          println("*** stopped Local Server Sites ***")
        } catch {
          case e: Throwable =>
            // e.printStackTrace
            println("Local Sites Server not running")
        }

      case Some("start") =>

        val tomcat = new Thread() {
          override def run() {
            try {
              println(s"*** Local Sites Server starting in port ${port}***")
              val tomcatProcess = tomcatEmbedded(base, home, port, classpath, debug)
            } catch {
              case e: Throwable =>
                //e.printStackTrace
                println("!!! Local Sites Server already running")
            }
          }
        }
        tomcat.start
        Thread.sleep(3000);
        println(" *** Waiting for Local Sites Server startup to complete ***")
        println(httpCallRaw(url + "/HelloCS"))

      case Some("script") =>
        tomcatScript(base, home, port, classpath, debug, log)

      case Some(thing) =>
        println(usage)
    }

  }


  val tomcatSettings = Seq(serverTask)
}
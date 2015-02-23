package agilesites.plugin.tool

import java.io.File

import agilesites.plugin.{SitesConfig, AgileSitesConfig}
import agilesites.util.Utils
import sbt.Keys._
import sbt._

trait TomcatSettings extends Utils {
  this: AutoPlugin with SitesConfig with AgileSitesConfig =>

  lazy val tomcatEmbeddedClasspath = taskKey[Seq[File]]("tomcat classpath")
  val tomcatEmbeddedClasspathTask = tomcatEmbeddedClasspath <<= (update) map {
    report => report.select(configurationFilter("tomcat"))
  }

  def tomcatEmbedded(base: File, port: Int, classpath: Seq[File], debug: Boolean) = {

    import java.io.File.pathSeparator

    val classpathExt = classpath ++ Seq(file("bin"), file("bin") / "setup.jar", file("home") / "bin")

    val cp = classpathExt.map(_.getAbsolutePath).mkString(pathSeparator)
    val temp = base / "temp"
    val bin = base / "bin"
    temp.mkdirs

    val debugSeq = if (debug)
      "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000" :: Nil
    else Nil

    val opts = "-cp" :: cp ::
      "-Djava.io.tmpdir=" + (temp.getAbsolutePath) ::
      "-Xms256m" :: "-Xmx1024m" :: "-XX:MaxPermSize=256m" ::
      s"-Dorg.owasp.esapi.resources=${bin}" :: debugSeq

    val args = Seq(port.toString, base.getAbsolutePath)

    val cmd = opts ++ args

    println(opts)
    val forkOpt = ForkOptions(
      runJVMOptions = opts,
      envVars = Map("CATALINA_HOME" -> base.getAbsolutePath),
      workingDirectory = Some(base))
    Fork.java(forkOpt, "setup.SitesServer" +: args)
  }


  lazy val sitesServer = inputKey[Unit]("Launch Local Sites")

  lazy val sitesServerTask = sitesServer := {
    
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val classpath = tomcatEmbeddedClasspath.value
    val port = sitesPort.value.toInt
    val base = baseDirectory.value
    val home = sitesHome.value
    val url = sitesUrl.value
    val log = streams.value.log
    val cs = file("webapps") / "cs"
    val cas = file("webapps") / "cas"

    val usage = "usage: start [debug]|stop|status"

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

        // start tomcat
        val debug = args.size == 2 && args(1) == "debug"

        val tomcat = new Thread() {
          override def run() {
            try {
              println(s"*** Local Sites Server starting in port ${port}***")
              val tomcatProcess = tomcatEmbedded(base, port, classpath, debug)
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

      case Some(thing) =>
        println(usage)
    }
  }

  //val tomcatConfig = "tomcat"
  val tomcatVersion = "7.0.52"
  val hsqlVersion = "1.8.0.10"
  def tomcatDeps(tomcatConfig: String) = Seq(
    //"org.apache.tomcat" % "tomcat-catalina" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat.embed" % "tomcat-embed-core" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat.embed" % "tomcat-embed-logging-juli" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat.embed" % "tomcat-embed-jasper" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat" % "tomcat-jasper" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat" % "tomcat-jasper-el" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat" % "tomcat-jsp-api" % tomcatVersion % tomcatConfig,
    "org.apache.tomcat" % "tomcat-dbcp" % tomcatVersion % tomcatConfig,
    "org.hsqldb" % "hsqldb" % hsqlVersion % tomcatConfig, // database
    "org.apache.httpcomponents" % "httpclient" % "4.3.4")

  val tomcatSettings = Seq(
    ivyConfigurations += config("tomcat"),
    libraryDependencies ++= tomcatDeps("tomcat") ++ tomcatDeps("provided"),
    tomcatEmbeddedClasspathTask, sitesServerTask)
}
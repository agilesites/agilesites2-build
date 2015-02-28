package agilesites.setup

import java.io.{File, PipedInputStream, PipedOutputStream}

import agilesites.Utils
import sbt._

/**
 * Created by msciab on 19/02/15.
 */
trait InstallerSettings extends Utils {
  this: AutoPlugin with TomcatSettings =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._
  import agilesites.setup.AgileSitesSetupPlugin.autoImport._

  def initFolders(base: File): Unit = {

    val data = base / "shared" / "data"
    val metaInf = base / "webapps" / "cs" / "META-INF"
    val omInstall = base / "home" / "ominstallinfo"
    val temp = base / "temp"
    val logs = base / "logs"
    val work = base / "work"

    data.mkdirs()
    metaInf.mkdirs()
    omInstall.mkdirs()
    temp.mkdirs()
    logs.mkdirs()
    work.mkdirs()

    writeFile(metaInf / "context.xml",
      s"""<Context path="/cs">
    <Resource
    name="csDataSource"
    auth="Container"
    type="javax.sql.DataSource"
    maxActive="50"
    maxIdle="10"
    username="sa"
    password=""
    driverClassName="org.hsqldb.jdbcDriver"
    url="jdbc:hsqldb:${data.getAbsolutePath}/csDB"/>
  </Context>""", null)

  }

  def silentInstaller(base: File, host: String, port: String): Unit = {
    // $SETUP agilesites.SilentSites "$BASE" $BASE/misc/silentinstaller/generic_omii.ini $BASE/Sites/install.ini $BASE/Sites/omii.ini $HOST $PORT $DB
    agilesites.SilentSites.main(Array[String](
      base.getAbsolutePath,
      (base / "misc" / "silentinstaller" / "generic_omii.ini").getAbsolutePath,
      (base / "Sites" / "install.ini").getAbsolutePath,
      (base / "Sites" / "omii.ini").getAbsolutePath,
      host, port, "HSQLDB"))
  }

  // install sites until the deployment
  def installSitesPre(base: File) = {

    // prepare input and the process
    val po = new PipedOutputStream
    val pi = new PipedInputStream(po)
    val csi = Process(
      if (File.pathSeparatorChar == ':')
        Seq("bash", "csInstall.sh", "-silent")
      else Seq("cmd", "/c", "csInstall.bat", "-silent"),
      Some(base / "Sites"), "JAVA_HOME" -> System.getProperty("java.home"))

    // run the installer until the "press ENTER message"
    var stream = (csi #< pi).lines_!

    while (stream.head.indexOf("press ENTER.") == -1) {
      println(">" + stream.head)
      stream = stream.tail
    }
    println("!" + stream.head)
    println("======================")
    (stream, po)
  }


  def stopTomcat(port: Int): Unit = {
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
  }

  def startTomcat(base: File, home: File, port: Int, cp: Seq[File]) {
    val tomcat = new Thread() {
      override def run() {
        try {
          //tomcatEmbedded(base, file(sitesHome.value), sitesPort.value.toInt, tomcatClasspath.value, false)
          tomcatEmbedded(base, home, port, cp, false)
        } catch {
          case e: Throwable =>
            println("!!! Local Sites Server already running")
        }
      }
    }
    tomcat.start
    Thread.sleep(3000);
  }

  // complete the installation after the deployment
  def installSitesPost(stream: Stream[String], po: PipedOutputStream): Unit = {
    po.write('\n')
    //stream.foreach(println)
    val re = "(Install failed|Installation Finished Successfully)".r
    var str = stream
    while (re.findFirstIn(str.head).isEmpty) {
      println(str.head)
      str = str.tail
    }
    po.write('\n')
    println("======================")
  }

  lazy val installTask = install := {
    val base = sitesDirectory.value
    if (!(base / "Sites" / "install.ini").exists())
      throw new Exception(s"there is not WebCenter Sites installer in the ${base} folder")
    stopTomcat(sitesPort.value.toInt)

    // initialize
    initFolders(base)
    // configure the silent installer
    silentInstaller(base, sitesHost.value, sitesPort.value)
    // fist part of the installation
    var (stream, po) = installSitesPre(base)
    // switch to hsqldb
    agilesites.SwitchDb.main(Array(sitesHome.value, "HSQLDB"))
    //startServer
    startTomcat(base, file(sitesHome.value), sitesPort.value.toInt, tomcatClasspath.value)
    // wait the start complete
    sitesHello.value.nonEmpty
    // complete the installation
    installSitesPost(stream, po)
  }

  val installerSettings = Seq(installTask)
}
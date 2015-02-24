package agilesites.setup

import agilesites.Utils
import sbt._

/**
 * Created by msciab on 19/02/15.
 */
trait InstallerSettings {
  this: AutoPlugin with Utils  =>

  import agilesites.config.AgileSitesConfigPlugin.autoImport._

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
    password=" "
    driverClassName="org.hsqldb.jdbcDriver"
    url="jdbc:hsqldb:${data.getAbsolutePath}/csDB"/>
  </Context>""", null)

  }

  lazy val sitesInstallTask = sitesInstall := {
    val base = sitesInstallFolder.value
    if(! (base/"Sites"/"install.ini").exists())
      throw new Exception(s"there is not WebCenter Sites installer in the ${base} folder")
    initFolders(base)
  }

  val installerSettings = Seq(sitesInstallTask)
}
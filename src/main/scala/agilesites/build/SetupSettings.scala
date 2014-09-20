package agilesites.build
import sbt._
import Keys._

trait SetupSettings {
  this: Plugin with UtilSettings with ConfigSettings with ToolsSettings with DeploySettings =>

  def setupServletRequest(webapp: String, sites: String, sitesSeq: Seq[Tuple2[String, String]], statics: String) {

    val prpFile = file(webapp) / "WEB-INF" / "classes" / "ServletRequest.properties"

    val prp = new java.util.Properties
    prp.load(new java.io.FileReader(prpFile))

    // shift the url assembler to add agilesites as the first
    if (prp.getProperty("uri.assembler.1.shortform") != "agilesites") {

      val p1s = prp.getProperty("uri.assembler.1.shortform")
      val p1c = prp.getProperty("uri.assembler.1.classname")
      val p2s = prp.getProperty("uri.assembler.2.shortform")
      val p2c = prp.getProperty("uri.assembler.2.classname")
      val p3s = prp.getProperty("uri.assembler.3.shortform")
      val p3c = prp.getProperty("uri.assembler.3.classname")
      val p4s = prp.getProperty("uri.assembler.4.shortform")
      val p4c = prp.getProperty("uri.assembler.4.classname")

      if (p4s != null && p4s != "") prp.setProperty("uri.assembler.5.shortform", p4s)
      if (p4c != null && p4c != "") prp.setProperty("uri.assembler.5.classname", p4c)
      if (p3s != null && p4s != "") prp.setProperty("uri.assembler.4.shortform", p3s)
      if (p3s != null && p4s != "") prp.setProperty("uri.assembler.4.classname", p3c)

      prp.setProperty("uri.assembler.3.shortform", p2s)
      prp.setProperty("uri.assembler.3.classname", p2c)
      prp.setProperty("uri.assembler.2.shortform", p1s)
      prp.setProperty("uri.assembler.2.classname", p1c)
      prp.setProperty("uri.assembler.1.shortform", "agilesites")
      prp.setProperty("uri.assembler.1.classname", "wcs.core.Assembler")
    }

    for ((k, v) <- sitesSeq) {
      prp.setProperty("agilesites.site." + normalizeSiteName(k), v)
      prp.setProperty("agilesites.name." + normalizeSiteName(k), k)
    }

    prp.setProperty("agilesites.statics", statics)

    // store
    println("~ " + prpFile)
    prp.store(new java.io.FileWriter(prpFile),
      "updated by AgileSites setup")
  }

  // select jars for the setup offline
  def setupCopyJarsWeb(webapp: String, classpathFiles: Seq[File], version: String) {

    val destlib = file(webapp) / "WEB-INF" / "lib"

    val addJars = classpathFiles.filter(webappFilter accept _)

    val removeJars = destlib.listFiles.filter(webappFilter accept _)

    setupCopyJars(destlib, addJars, removeJars)

  }

  // select jars for the setup online
  def setupCopyJarsLib(shared: String, classpathFiles: Seq[File]) {

    val parentlib = file(shared) / "agilesites"

    val destlib = parentlib / "lib"

    destlib.mkdirs

    // jars to include when performing a setup
    val addJars = classpathFiles filter (setupFilter accept _)
    //println(addJars)

    // jars to remove when performing a setup
    val removeJars = destlib.listFiles
    //println(removeJars)  

    setupCopyJars(destlib, addJars, removeJars)

    for (file <- destlib.listFiles) {
      val parentfile = parentlib / file.getName
      if (parentfile.exists) {
        parentfile.delete
        println("- " + parentfile.getAbsolutePath)
      }
    }

  }

  // copy jars filtering and and remove
  def setupCopyJars(destlib: File, addJars: Seq[File], removeJars: Seq[File]) {

    // remove jars
    println("** removing old version of files **");
    for (file <- removeJars) {
      val tgt = destlib / file.getName
      tgt.delete
      println("- " + tgt.getAbsolutePath)
    }

    // add jars
    println("** installing new version of files **");
    for (file <- addJars) yield {
      val tgt = destlib / file.getName
      IO.copyFile(file, tgt)
      //println(file)
      println("+ " + tgt.getAbsolutePath)
    }

  }

  // configure futurentense.ini
  def setupFutureTenseIni(home: String, shared: String, sites: String, version: String, envision: String) {

    val prpFile = file(home) / "futuretense.ini"
    val prp = new java.util.Properties
    prp.load(new java.io.FileReader(prpFile))

    val jardir = file(shared) / "agilesites"

    prp.setProperty("agilesites.dir", jardir.getAbsolutePath);
    prp.setProperty("agilesites.poll", "1000");
    prp.setProperty("cs.csdtfolder", file(envision).getParentFile().getAbsolutePath())

    println("~ " + prpFile)
    prp.store(new java.io.FileWriter(prpFile),
      "updated by AgileSites setup")
  }

  lazy val asSetup = taskKey[Unit]("Sites Setup Offline")
  val asSetupTask = asSetup := {

    val classes = (classDirectory in Compile).value
    val classpath = (fullClasspath in Compile).value.files
    val sites = asSites.value
    val version = sitesVersion.value
    val home = sitesHome.value
    val shared = sitesShared.value
    val webapp = sitesWebapp.value
    val url = sitesUrl.value
    val statics = asStatics.value
    val envision = sitesEnvisionDir.value
    val hello = sitesHello.value

    if (!hello.isEmpty)
      throw new Exception("Web Center Sites must be offline.")

    println("*** Installing AgileSites for WebCenter Sites ***");

    val vhosts = (sites split ",").map { site =>
      (site, url + "/Satellite/" + normalizeSiteName(site))
    }.toSeq

    setupServletRequest(webapp, sites, vhosts, statics)
    setupFutureTenseIni(home, shared, sites, version, envision)

    // remove any other jar starting with agilesites-all-assembly 
    // remnants of the past
    val agilesitesDir = file(shared) / "agilesites"
    if (agilesitesDir.exists())
      for (f <- agilesitesDir.listFiles) {
        if (f.isFile && f.getName.startsWith("agilesites-all-assembly")) {
          f.delete
          println("--- " + f);
        }
      }
    else agilesitesDir.mkdirs()

    // installing jars
    setupCopyJarsWeb(webapp, classpath, version)
    setupCopyJarsLib(shared, classpath)

    println("""**** Setup Complete.
              |**** Please restart your application server.
              |**** You need to complete installation with "asDeploy".""".stripMargin)
  }

  lazy val asSetupOnline = taskKey[Unit]("Sites Populate Online")
  val asSetupOnlineTask = asSetupOnline := {

    val log = streams.value.log

    println("hello....")

    if (sitesHello.value.isEmpty)
      throw new Exception(s"Web Center Sites must be online at ${sitesUrl}.")

    val jar = (fullClasspath in Compile).value.files.filter(_.getName.startsWith("agilesites2-core")).head

    if (jar.exists()) {
      log.info(s"extracting aaagile from ${jar}")
      IO.delete(file(sitesPopulateDir.value) / "aaagile")
      val populateDir = file(sitesPopulateDir.value)
      IO.unzip(jar, populateDir, GlobFilter("aaagile/*"))
      if ((populateDir / "aaagile").exists())
        cmov.toTask(" import_all aaagile").value
      else
        log.error(s"cannot find aaagile dir in ${populateDir}")
    } else {
      log.error("cannot find agilesites2-core in classpath")
    }

  }

  val setupSettings = Seq(asSetupTask, asSetupOnlineTask)
}
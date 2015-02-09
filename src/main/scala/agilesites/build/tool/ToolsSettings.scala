package agilesites.build.tool

import java.io.File

import agilesites.build.{AgileSitesConfig, SitesConfig}
import agilesites.build.util.UtilSettings
import sbt.Keys._
import sbt._

trait ToolsSettings {
  this: AutoPlugin with UtilSettings with SitesConfig with AgileSitesConfig  =>

  // find the default workspace from sites
  def defaultWorkspace(sites: String) = normalizeSiteName(sites.split(",").head)

  lazy val cmovClasspath = taskKey[Seq[File]]("Sites Populate Classpath")
  lazy val cmovClasspathTask = cmovClasspath <<= (sitesHome, baseDirectory) map {
    (home, base) =>
      val h = file(home)
      Seq(base / "bin", h / "bin") ++
        (h * "*.jar").get ++
        (h / "Sun" ** "*.jar").get ++
        (h / "wem" ** "*.jar").get
  }

  lazy val cmov = inputKey[Unit]("WCS Catalog Mover")
  val cmovTask = cmov := {
    val args = Def.spaceDelimited("<arg>").parsed
    val log = streams.value.log
    if (args.length == 0) {
      println( s"""usage: cman <cmd> [<dir>] [<options>....]])
|<cmd> one of view, setup, import, import_all, export, export_all
|<dir> defaults to "core" under sites/export/populate
|<options> can be:
|-b base URL (defaults to ${sitesUrl.value}/CatalogManager)
|-u user name (defaults to ${sitesUser.value})
|-p password (defaults to ${sitesPassword.value})
|-s server name (optional)
|-t catalog name (can be repeated, export only)
|-f file to import
|-c catalog data directory (optional)
|-a ACL list (optional)
|-i ini file(s) to merge (optional)
          """.stripMargin)
    } else {

      val cp = (Seq(file("bin").getAbsoluteFile) ++ cmovClasspath.value).mkString(java.io.File.pathSeparator)
      val coreJar = (fullClasspath in Compile).value.files.filter(_.getName.startsWith("agilesites2-core")).head

      if (sitesHello.value.isEmpty)
        throw new Exception(s"Web Center Sites must be online as s{sitesUrl.value}.")

      val cmd = if (args(0) == "setup") {
        if (coreJar.exists()) {
          log.info(s"extracting aaagile")
          val populateDir = file(sitesPopulateDir.value)
          IO.delete(populateDir / "aaagile")
          IO.unzip(coreJar, populateDir, GlobFilter("aaagile/*"))
          if ((populateDir / "aaagile").exists())
            "import_all"
          else
            throw new Exception(s"cannot find aaagile dir in ${populateDir}")
        } else
          throw new Exception("cannot find agilesites2-core in classpath")
      } else
        args(0)

      val opts =
        if (cmd == "view") Seq()
        else {
          val set = args.toSet
          val dir =
            if (args.length > 1) file(sitesPopulateDir.value) / args(1)
            else file(sitesPopulateDir.value) / "aaagile"

          println(dir, dir.isDirectory())
          if (!dir.isDirectory)
            throw new Exception(s"not found ${dir.getAbsolutePath}")

          Seq("-d", dir.getAbsolutePath, "-x", cmd) ++ args.drop(2) ++
            (if (set("-b")) Seq() else Seq("-b", sitesUrl.value + "/CatalogManager")) ++
            (if (set("-u")) Seq() else Seq("-u", sitesUser.value)) ++
            (if (set("-p")) Seq() else Seq("-p", sitesPassword.value))
        }

      println(opts)

      Fork.java(ForkOptions(
        runJVMOptions = Seq("-cp", cp),
        workingDirectory = Some(baseDirectory.value)),
        "COM.FutureTense.Apps.CatalogMover" +: opts)

    }

  }

  lazy val csdtHome = settingKey[File]("CSDT Client Home")
  lazy val csdtClasspath = settingKey[Seq[File]]("CSDT Client Classpath")

  // interface to csdt from sbt
  lazy val csdt = inputKey[Unit]("Content Server Development Tool")
  val csdtTask = csdt := {
    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    val home = sitesHome.value
    val version = sitesVersion.value
    val url = sitesUrl.value
    val user = sitesUser.value
    val password = sitesPassword.value
    val sites = asSites.value
    val seljars = csdtClasspath.value
    val log = streams.value.log
    val envision = file(sitesEnvisionDir.value)
    val defaultSite = sites.split(",").head

    val defaultWorkspace = envision / defaultSite
    if (!defaultWorkspace.exists())
      defaultWorkspace.mkdir

    val workspaces = envision.listFiles.filter(_.isDirectory).map(_.getName)
    val workspaceSearch = (s"#${defaultSite}#" +: args).reverse.filter(_.startsWith("#")).head.substring(1)

    val workspace = if (!workspaceSearch.endsWith("#"))
      workspaces.filter(_.indexOf(workspaceSearch) != -1)
    else workspaces.filter(_ == workspaceSearch.init)

    val fromSites = (("!" + defaultSite) +: args).reverse.filter(_.startsWith("!")).head.substring(1)
    val toSites = (("^" + fromSites) +: args).reverse.filter(_.startsWith("^")).head.substring(1)

    if (args.size > 0 && args(0) == "raw") {
      Run.run("com.fatwire.csdt.client.main.CSDT",
        seljars, args.drop(1), streams.value.log)(runner.value)
    } else if (args.size == 0) {
      println( """usage: csdt <cmd> <selector> ... [#<workspace>[#]] [!<from-sites>] [^<to-sites>]
                 | <workspace> is a substring of available workspaces, use #workspace# for an exact match
                 |   default workspace is: %s
                 |   available workspaces are: %s
                 | <from-sites> and <to-sites> is a comma separated list of sites defined,
                 |   <from-sites> defaults to <workspace>,
                 |   <to-sites> defaults to <from-sites>
                 | <cmd> is one of 'listcs', 'listds', 'import', 'export', 'mkws'
                 | <selector> check developer tool documentation for complete syntax
                 |    you can use <AssetType>[:<id>] or a special form,
                 |    the special form are
                 |      @SITE @ASSET_TYPE @ALL_ASSETS @STARTMENU @TREETAB
                 |  and also additional @ALL for all of them
                 | """.stripMargin.format(defaultSite, workspaces.mkString("'", "', '", "'")))
    } else if (workspace.size == 0)
      println("workspace " + workspaceSearch + " not found - create it with mkws <workspace>")
    else if (workspace.size > 1)
      println("workspace " + workspaceSearch + " is ambigous")
    else {

      def processArgs(args: Seq[String]) = {
        if (args.size == 0 || args.size == 1) {
          println(
            """please specify what you want to export or use @ALL to export all
              | you can use <AssetType>[:<id>] or a special form,
              | the special form are
              |   @SITE @ASSET_TYPE @ALL_ASSETS @STARTMENU @TREETAB @ROLE
              |  and also additional @ALL meaning  all of them""".stripMargin)
          Seq()
        } else if (args.size == 2 && args(1) == "@ALL") {
          Seq("@SITE", "@ASSET_TYPE", "@ALL_ASSETS", "@STARTMENU", "@TREETAB", "@ROLE")
        } else {
          args.drop(1)
        }
      }

      val args1 = args.filter(!_.startsWith("#")).filter(!_.startsWith("!")).filter(!_.startsWith("^"))
      val firstArg = if (args1.size > 0) args1(0) else "listcs"
      val resources = firstArg match {
        case "listcs" => processArgs(args1)
        case "listds" => processArgs(args1)
        case "import" => processArgs(args1)
        case "export" => processArgs(args1)
        case "mkws" =>
          if (args1.size == 1) {
            println("please specify workspace name")
          } else {
            val ws = envision / args1(1)
            if (ws.exists)
              println("nothing to do - workspace " + args1(1) + " exists")
            else {
              ws.mkdirs
              if (ws.exists)
                println(" workspace " + args1(1) + " created")
              else
                println("cannot create workspace " + args1(1))
            }
          }
          Seq()
        case _ =>
          println("Unknown command")
          Seq()
      }

      for (res <- resources) {
        val cmd = Array(url + "/ContentServer",
          "username=" + user,
          "password=" + password,
          "cmd=" + firstArg,
          "resources=" + res,
          "fromSites=" + fromSites,
          "toSites=" + toSites,
          "datastore=" + workspace.head)

        log.debug(seljars.mkString("\n"))
        //s.log.debug(cmd.mkString(" "))
        Run.run("com.fatwire.csdt.client.main.CSDT", seljars, cmd, log)(runner.value)
      }
    }
  }



  val toolsSettings = Seq(cmovTask, csdtTask,
    csdtHome := baseDirectory.value / "sites" / "home" / "csdt" / "csdt-client",
    cmovClasspathTask,
    csdtClasspath := (csdtHome.value ** "*.jar").get)
}
package agilesitesng

import java.io.{File, FileReader, InputStream}
import java.net.URL

import sbt._

trait Utils {


  def readFile(s: String): String = readFile(new java.io.File(s))

  // read a file
  def readFile(f: File) = {
    val fr = new FileReader(f)
    val sb = new StringBuilder
    var c = fr.read
    while(c != -1) {
      sb.append(c.asInstanceOf[Char])
      c = fr.read
    }
    fr.close
    sb.toString
  }


  // read a file
  def readStream(is: InputStream) = {
    var sb = new StringBuilder
    var c = is.read
    while(c != -1) {
      sb.append(c.asInstanceOf[Char])
      c = is.read
    }
    is.close
    sb.toString
  }

  // get a wrapped property
  def prp(property: String) = {
    val r = System.getProperty(property)
    if (r == null)
      None
    else
      Some(r)
  }

  def writeFile(file: File, body: String, log: sbt.Logger) = {
    //println("*** %s%s****\n".format(file.toString, body))
    if (log != null)
      log.debug("+++ %s".format(file.toString))
    if (file.getParentFile != null)
      file.getParentFile.mkdirs
    val w = new java.io.FileWriter(file)
    w.write(body)
    w.close()
  }

  def writeFileFromResource(file: File, resource: String, log: sbt.Logger) = {

    println(">>> %s\n".format(resource))

    val is = this.getClass.getResourceAsStream(resource)
    val sc = new java.util.Scanner(is).useDelimiter("\\A");

    if(sc.hasNext())
      writeFile(file, sc.next, log)
    else
      writeFile(file, "", log)
  }

  // is an html file?
  def isHtml(f: File) = ("\\.html?$".r findFirstIn f.getName.toLowerCase).nonEmpty

  // is not a .less file?
  def notLess(f: File) = !f.getName.endsWith(".less")

  // copy files from a src dir to a target dir recursively 
  // filter files to copy
  def recursiveCopy(src: File, tgt: File, log: Logger)(sel: File => Boolean) = {
    val nSrc = src.getPath.length
    val cpList = (src ** "*").get.filterNot(_.isDirectory).filter(sel) map {
      x =>
        val dest = tgt / x.getPath.substring(nSrc)
        (x, dest)
    }
    if (log != null)
      log.info(s"copying #${cpList.size} files")
    IO.copy(cpList).toSeq
  }

  // write an index of the files in a subdirectory in the target file
  def recursiveIndex(srcDir: File, tgtFile: File, log: sbt.Logger)(sel: File => Boolean) = {
    val pLen = srcDir.getAbsolutePath.size
    val body = (srcDir ** "*").
      filter(_.isFile).filter(sel).get.
      map(_.getAbsolutePath.substring(pLen).replace(File.separatorChar, '/')).
      mkString("\n")
    writeFile(tgtFile, body, log)
    tgtFile
  }

  // simple http call returning the result as a string
  def httpCallRaw(req: String) = {
    val scan = new java.util.Scanner(new URL(req).openStream(), "UTF-8")
    val res = scan.useDelimiter("\\A").next()
    scan.close()
    //">>>%s\n%s<<<%s\n" format(req,res,req)
    res
  }

  // invoking the url (for comma separated options)
  def httpCall(op: String, option: String, url: String, user: String, pass: String, sites: String = null) = {

    // create a site list if is is not empty
    val siteList = if (sites == null) {
      List("")
    } else {
      (sites split (",") map { s => "&site=" + s}).toList
    }

    //println(siteList)
    val out = for (site <- siteList) yield {
      val req = "%s/Satellite?pagename=AAAgile%s&username=%s&password=%s%s%s"
        .format(url, op, user, pass, option, site)
      println(">>> " + req + "")
      httpCallRaw(req)
    }
    out mkString ""
  }


  // name says it all  
  def normalizeSiteName(s: String) = s.toLowerCase.replaceAll( """[^a-z0-9]+""", "")


  // check is sites is running
  def helloSites(url: String) = {
    try {
      val res = httpCallRaw(url + "/HelloCS")
      val rePrp = """(\d+\.\d+)\..*""".r
      val rePrp(javaVersion) = System.getProperty("java.version")
      val reWeb = """(?s).*java\.version=(\d+\.\d+)\..*""".r
      res match {
        case reWeb(sitesVersion) =>
          if (javaVersion != sitesVersion) {
            println( """*** WebCenter Sites use java %s and AgileSites uses java %s
                       |*** They are different major versions of Java.
                       |*** The compiler may generate incompatible bytecode
                       |*** Please set JAVA_HOME and use the same major java version for both
                       |***""".format(sitesVersion, javaVersion).stripMargin)
            None
          } else {
            println("WebCenter Sites running with java " + sitesVersion)
            Some(javaVersion)
          }
        case _ =>
          //println(" no match ")
          println("WebCenter Sites running")
          Some("unknown")
      }
    } catch {
      case ex: Throwable =>
        println("WebCenter Sites NOT running")
        None
    }
  }

  // Utils
  def exec(args: Seq[String], home: File, cp: Seq[File]) = {

    Fork.java(ForkOptions(
      runJVMOptions = "-cp" :: cp.map(_.getAbsolutePath).mkString(java.io.File.pathSeparator) :: Nil,
      workingDirectory = Some(home)), args)
  }
}
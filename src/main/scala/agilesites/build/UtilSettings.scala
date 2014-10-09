package agilesites.build

import sbt._
import Keys._
import agilesites.build.util.Utils
import java.nio.charset.Charset

trait UtilSettings extends Utils {
  this: Plugin with ConfigSettings =>

  // where the property files are
  val asProperties = settingKey[Seq[String]]("AgileSites Property files")

  // read all the properties in a single property map
  lazy val asPropertyMap = settingKey[Map[String, String]]("AgileSites Property Map")
  val asPropertyMapTask = asPropertyMap := {

    import java.util.Properties
    import scala.collection.JavaConverters._

    val prp = new Properties

    for (prpFileName <- asProperties.value) {
      val prpFile = file(prpFileName)

      if (!prpFile.exists) {
        //log.info("not found property file " + prpFile)
      } else {
        System.out.println("loading " + prpFile)
        prp.load(new java.io.FileInputStream(prpFile))
      }
    }
    val map = prp.asScala.toMap
    for ((k, v) <- map)
      println(s"${k}=${v}")
    map
  }

  // display a prompt with the project name
  val asShellPromptTask = shellPrompt in ThisBuild := {
    state => Project.extract(state).currentRef.project + "> "
  }

  // hello
  val sitesHello = taskKey[Option[String]]("Sites Hello")
  val sitesHelloTask = sitesHello := {
    try {
      val url = sitesUrl.value
      val res = httpCallRaw(url + "/HelloCS")

      val reprp = """(\d+\.\d+)\..*""".r
      val reprp(javaVersion) = System.getProperty("java.version")

      val reweb = """(?s).*java\.version=(\d+\.\d+)\..*""".r
      res match {
        case reweb(sitesVersion) =>

          if (javaVersion != sitesVersion) {
            println("""*** WebCenter Sites use java %s and AgileSites uses java %s
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

  //lazy val asGenerateVersionClass = taskKey[Seq[File]]("Generate a Version Class")

  val asGenerateVersionClass = Def.task {
    val vclass = name.value.replace('-', '_')    
    val vfile = (sourceManaged in Compile).value / "wcs" / "version" / (vclass + ".java")
    val date = new java.util.Date()
    IO.write(vfile, s"""package wcs.version;
public class ${vclass} { public static void main(String[] args) {
  System.out.println("Jar: ${name.value}\\nVersion: ${version.value}\\nTimestamp: ${date.getTime().toString}\\nDate: ${date.toString}\\n");
} }""", Charset.forName("UTF-8"), false)
    Seq(vfile)
  }
  
  
  val utilSettings = Seq(asShellPromptTask,
    asProperties := Seq("agilesites.properties"),
    asPropertyMapTask, sitesHelloTask,
    //artifactName := name.value + "-" + version.value,
    packageOptions in (Compile, packageBin) += Package.ManifestAttributes( java.util.jar.Attributes.Name.MAIN_CLASS -> s"wcs.version.${name.value.replace('-', '_')}" ),
    sourceGenerators in Compile += asGenerateVersionClass.taskValue)
}
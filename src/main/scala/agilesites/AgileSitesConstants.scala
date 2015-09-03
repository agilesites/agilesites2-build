package agilesites

import sbt._, Keys._

/**
 * Created by msciab on 13/08/15.
 */
object AgileSitesConstants {

  val agilesitesPlugin = Seq("com.sciabarra" % "agilesites2-build" % "11g-M4-SNAPSHOT" extra("scalaVersion" -> "2.10", "sbtVersion" -> "0.13"))

  val agilesitesLib = Seq("com.sciabarra" % "agilesitesng-nglib" % "11g-M4-SNAPSHOT")

  val spoonDependencies = Seq("net.openhft" % "spoon-core" % "4.3.0" % "spoon"
    , "org.scala-lang" % "scala-library" % "2.10.5" % "spoon"
    , "net.liftweb" % "lift-json_2.10" % "2.6" % "spoon"
    , "ch.qos.logback" % "logback-classic" % "1.1.3" % "spoon"
  )

  val tomcatVersion = "7.0.59"

  val tomcatDependencies = Seq("org.hsqldb" % "hsqldb" % "1.8.0.10"
    , "org.apache.tomcat.embed" % "tomcat-embed-core" % tomcatVersion
    , "org.apache.tomcat.embed" % "tomcat-embed-logging-juli" % tomcatVersion
    , "org.apache.tomcat.embed" % "tomcat-embed-jasper" % tomcatVersion
    , "org.apache.tomcat" % "tomcat-jasper" % tomcatVersion
    , "org.apache.tomcat" % "tomcat-jasper-el" % tomcatVersion
    , "org.apache.tomcat" % "tomcat-jsp-api" % tomcatVersion
    , "org.apache.tomcat" % "tomcat-dbcp" % tomcatVersion
    ) ++ agilesitesPlugin

}

val v = "11g-M1-SNAPSHOT"

name := "agilesites2-build"

organization := "com.sciabarra"

sbtPlugin := true

version := v

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-target:jvm-1.6")

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

ivyConfigurations += config("tomcat")

val tomcatVersion = "7.0.59"

libraryDependencies ++=  Seq(
   "org.apache.tomcat.embed" % "tomcat-embed-core" % tomcatVersion,
   "org.apache.tomcat.embed" % "tomcat-embed-logging-juli" % tomcatVersion,
   "org.apache.tomcat.embed" % "tomcat-embed-jasper" % tomcatVersion,
   "org.apache.tomcat" % "tomcat-jasper" % tomcatVersion,
   "org.apache.tomcat" % "tomcat-jasper-el" % tomcatVersion,
   "org.apache.tomcat" % "tomcat-jsp-api" % tomcatVersion,
   "org.apache.tomcat" % "tomcat-dbcp" % tomcatVersion,
   "org.hsqldb" % "hsqldb" % "1.8.0.10", // database
   "org.apache.httpcomponents" % "httpclient" % "4.3.4",
   "org.scalatest"           %% "scalatest"      % "2.2.0" % "test",
   "org.clapper"             %% "scalasti"       % "1.0.0",
   "net.databinder.dispatch" %% "dispatch-core"  % "0.11.2",
   "org.jsoup"               % "jsoup"           % "1.7.3",
   "com.jcraft"              % "jsch"            % "0.1.51",
   "commons-io"              % "commons-io"      % "2.4",
   "commons-httpclient"      % "commons-httpclient"   % "3.1")

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
    val nexus = "http://nexus.sciabarra.com/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "content/repositories/releases")
  }

publishArtifact in packageDoc := false

publishArtifact in Test := false

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

credentials += Credentials(Path.userHome / ".ivy2" / "credentials")

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
  "Nexus-sciabarra-releases" at "http://nexus.sciabarra.com/content/repositories/releases",
  "Nexus-sciabarra-snapshots" at "http://nexus.sciabarra.com/content/repositories/snapshots")

net.virtualvoid.sbt.graph.Plugin.graphSettings

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.1.2")

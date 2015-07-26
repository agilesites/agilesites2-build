val v = "11g-M3"

isSnapshot := v.endsWith("-SNAPSHOT")

name := "agilesites2-build"

organization := "com.sciabarra"

sbtPlugin := true

version := v

scalaVersion := "2.10.4"

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalacOptions ++= Seq("-feature", "-target:jvm-1.6", "-deprecation")

val tomcatVersion = "7.0.59"

libraryDependencies ++=
 Seq("ch.qos.logback"            % "logback-classic" % "1.1.3" % "test;compile"
    ,"org.hsqldb" % "hsqldb"     % "1.8.0.10"
    ,"org.apache.tomcat.embed"   % "tomcat-embed-core"         % tomcatVersion
    ,"org.apache.tomcat.embed"   % "tomcat-embed-logging-juli" % tomcatVersion
    ,"org.apache.tomcat.embed"   % "tomcat-embed-jasper"       % tomcatVersion
    ,"org.apache.tomcat"         % "tomcat-jasper"             % tomcatVersion
    ,"org.apache.tomcat"         % "tomcat-jasper-el"          % tomcatVersion
    ,"org.apache.tomcat"         % "tomcat-jsp-api"            % tomcatVersion
    ,"org.apache.tomcat"         % "tomcat-dbcp"               % tomcatVersion
    ,"org.apache.httpcomponents" % "httpclient"      % "4.3.6"
    ,"commons-io"                % "commons-io"      % "2.4"
    ,"org.scalatest"             %% "scalatest"      % "2.2.4" % "test"
    ,"net.databinder.dispatch"   %% "dispatch-core"  % "0.11.2"
    ,"org.jsoup"                 % "jsoup"           % "1.7.3"
    ,"com.jcraft"                % "jsch"            % "0.1.51"
    ,"com.typesafe.akka"         %% "akka-actor"     % "2.3.9"
    ,"com.typesafe.akka"         %% "akka-remote"    % "2.3.9"
    ,"com.typesafe.akka"         %% "akka-slf4j"     % "2.3.9"
    ,"com.typesafe.akka"         %% "akka-testkit"   % "2.3.9" % "test"
    ,"io.spray"                  %% "spray-can"      % "1.3.2"
    ,"io.spray"                  %% "spray-http"     % "1.3.2"
    ,"io.spray"                  %% "spray-httpx"    % "1.3.2"
    ,"org.scalaz"                %% "scalaz-core"    % "7.0.6"
    ,"io.argonaut"               %% "argonaut"       % "6.0.4"
    ,"net.liftweb"               %% "lift-json"      % "2.6"
    )

pomIncludeRepository := { _ => false }

publishMavenStyle := true

publishTo := {
    val nexus = "http://nexus.sciabarra.com/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "content/repositories/releases")
  }

publishArtifact in Test := false

publishArtifact in packageDoc := false

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

credentials += Credentials(Path.userHome / ".ivy2" / "credentials")

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
  "Nexus-sciabarra-releases" at "http://nexus.sciabarra.com/content/repositories/releases",
  "Nexus-sciabarra-snapshots" at "http://nexus.sciabarra.com/content/repositories/snapshots")

net.virtualvoid.sbt.graph.Plugin.graphSettings

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.1.2" exclude("org.slf4j", "slf4j-simple"))

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.0" exclude("org.slf4j", "slf4j-simple"))



//scalaxbSettings

//sourceGenerators in Compile += (scalaxb in Compile).taskValue

//packageName in (Compile, scalaxb) := "agilesites.wem.mapping"

//async in (Compile, scalaxb) := false

//xsdSource := baseDirectory.getParentFile / "lib" / file("/data/sites/bigbang/")

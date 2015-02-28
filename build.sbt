val v = "2.0-M1"

val tomcatConfig = config("tomcat")

val jfx = config("jfx")

//val tomcatVersion = "7.0.52"
//val hsqlVersion = "1.8.0.10"
//val setupVersion = "2.0-M1"

//def tomcatDeps(tomcatConfig: String) = Seq(
//   "org.apache.httpcomponents"  % "httpclient"                 % "4.3.4",
//    "org.apache.tomcat"         % "tomcat-catalina"            % tomcatVersion % tomcatConfig,
//    "org.apache.tomcat"         % "tomcat-dbcp"                % tomcatVersion % tomcatConfig,
//    "org.apache.tomcat.embed"   % "tomcat-embed-logging-log4j" % tomcatVersion % tomcatConfig,
//    "org.apache.tomcat.embed"   % "tomcat-embed-core"          % tomcatVersion % tomcatConfig,
//    "org.apache.tomcat.embed"   % "tomcat-embed-core"          % tomcatVersion % tomcatConfig,
//    "org.apache.tomcat.embed"   % "tomcat-embed-jasper"        % tomcatVersion % tomcatConfig,
//    "org.hsqldb"                % "hsqldb"                     % hsqlVersion   % tomcatConfig,
//    "com.sciabarra"             % "agilesites2-setup"          % setupVersion  % tomcatConfig)
//
//    //"commons-httpclient" 	  % "commons-httpclient" % "3.1",
//    //"commons-codec"      	  % "commons-codec" % "1.3",
//    //"commons-fileupload" 	  % "commons-fileupload" % "1.2",
//    //"commons-io"         	  % "commons-io" % "1.3.2",


val libDeps = Seq(
   "org.scalatest"           %% "scalatest"      % "2.2.0" % "test",
   "org.clapper"             %% "scalasti"       % "1.0.0",
   "org.scalafx"             %% "scalafx"        % "2.2.76-R11",
   "org.scalafx"             %% "scalafxml-core" % "0.2.1",
   "net.databinder.dispatch" %% "dispatch-core"  % "0.11.2",
   "org.jsoup"               % "jsoup"           % "1.7.3",
   "com.jcraft"              % "jsch"            % "0.1.51",
   "commons-io"              % "commons-io"      % "2.4",
   "fr.inria.gforge.spoon"   % "spoon-core"      % "2.3.1",
   "commons-httpclient"      % "commons-httpclient"   % "3.1")

val btSettings = bintrayPublishSettings ++ Seq(
	bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("sciabarra"),
	bintray.Keys.repository in bintray.Keys.bintray := "sbt-plugins",
	licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
	publishMavenStyle := false,
	publishArtifact in packageDoc := false,
	publishArtifact in Test := false)

val mySettings = Seq(name := "agilesites2-build",
	organization := "com.sciabarra",
	sbtPlugin := true,
	version := v,
	scalaVersion := "2.10.4",
	scalacOptions ++= Seq("-deprecation", "-feature"),
  ivyConfigurations += tomcatConfig,
	libraryDependencies ++= libDeps )//++ tomcatDeps("tomcat") ++ tomcatDeps("compile"))

val guiSettings = Seq(
    fork in jfx := true,
    mainClass in jfx := Some("agilesites.gui.Main"),
    unmanagedJars in jfx <<= unmanagedJars in Compile,
    unmanagedJars in Compile <+= Def.task {
    val javaHome = new File(System.getProperty("java.home"))
    val jfxJar = new File(javaHome, "lib/jfxrt.jar")
    if (!jfxJar.exists)
        throw new RuntimeException("JavaFX not detected (needs Java runtime 7u06 or later): " + jfxJar.getPath) // '.getPath' = full filename
    Attributed.blank(jfxJar)
  })

val plugin = project.in(file(".")).
	settings(btSettings: _*).
	settings(mySettings : _*).
  settings(guiSettings: _*)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

net.virtualvoid.sbt.graph.Plugin.graphSettings

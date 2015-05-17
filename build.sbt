val v = scala.io.Source.fromFile(file("version.txt")).getLines.next.trim

val tomcatConfig = config("tomcat")

val jfx = config("jfx")

val jfxJar = file(System.getProperty("java.home")) / "lib" / "jfxrt.jar"

val libDeps = Seq(
   "com.sciabarra"           % "agilesites2-setup" % "2.0.1",
   "org.scalafx"             %% "scalafx" % "2.2.76-R11",
   "org.scalafx"             %% "scalafxml-core" % "0.2.1",
   "org.scalatest"           %% "scalatest"      % "2.2.0" % "test",
   "org.clapper"             %% "scalasti"       % "1.0.0",
   "org.scalafx"             %% "scalafx"        % "2.2.76-R11",
   "org.scalafx"             %% "scalafxml-core" % "0.2.1",
   "net.databinder.dispatch" %% "dispatch-core"  % "0.11.2",
   "org.jsoup"               % "jsoup"           % "1.7.3",
   "com.jcraft"              % "jsch"            % "0.1.51",
   "commons-io"              % "commons-io"      % "2.4",
   //"fr.inria.gforge.spoon"   % "spoon-core"      % "2.3.1",
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
	libraryDependencies ++= libDeps )

val guiSettings = Seq(
    fork in jfx := true,
    mainClass in jfx := Some("agilesites.gui.Main"),
    unmanagedJars in jfx <<= unmanagedJars in Compile,
    unmanagedJars in Compile += Attributed.blank(jfxJar) )

val plugin = project.in(file(".")).
  settings(btSettings: _*).
  settings(mySettings : _*).
  settings(guiSettings: _*)

resolvers += Resolver.sonatypeRepo("releases")

resolvers += "Bintray" at "http://dl.bintray.com/content/sciabarra/maven"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

net.virtualvoid.sbt.graph.Plugin.graphSettings

scalacOptions += "-target:jvm-1.6"

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

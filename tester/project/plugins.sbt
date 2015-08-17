val plugin = project.in(file(".")).dependsOn(file("..").toURI)

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"


package agilesites.build

import sbt._
import Keys._

object AgileSitesPlugin
  extends Plugin
  with ConfigSettings
  with UtilSettings
  with ToolsSettings
  with TomcatSettings
  with SetupSettings
  with DeploySettings
  with ScaffoldSettings
  with WebSettings {

  override lazy val settings =
    configSettings ++
      utilSettings ++
      toolsSettings ++
      tomcatSettings ++
      deploySettings ++
      scaffoldSettings ++
      webSettings
}
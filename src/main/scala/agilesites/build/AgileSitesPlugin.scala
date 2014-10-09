package agilesites.build

import sbt._
import Keys._
import agilesites.generator.GeneratorSettings

object AgileSitesPlugin
  extends Plugin
  with ConfigSettings
  with UtilSettings
  with ToolsSettings
  with TomcatSettings
  with SetupSettings
  with DeploySettings
  with ScaffoldSettings
  with WebSettings
  with GeneratorSettings {

  override lazy val projectSettings =
    configSettings ++
      utilSettings ++
      toolsSettings ++
      tomcatSettings ++
      setupSettings ++
      deploySettings ++
      scaffoldSettings ++
      webSettings ++
      generatorSettings
}
package agilesites.build

import sbt._
import Keys._
import agilesites.generator.GeneratorPlugin
import agilesites.wem.WemPlugin

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
  with GeneratorPlugin
  with WemPlugin {

  override lazy val projectSettings =
    configSettings ++
      utilSettings ++
      toolsSettings ++
      tomcatSettings ++
      deploySettings ++
      scaffoldSettings ++
      webSettings ++
      generatorPlugin ++
      setupSettings ++
      wemPlugin
}
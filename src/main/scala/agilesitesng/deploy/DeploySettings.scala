package agilesitesng.deploy

import agilesitesng.deploy.actor.Protocol.Deploy
import sbt.AutoPlugin
import sbt._
import sbt.Keys._

/**
 * Created by msciab on 04/08/15.
 */
trait DeploySettings {
this: AutoPlugin =>
  import NgDeployKeys._

  val deployTask = deploy in ng := {

    /* tentativo di usare la Analysis
    val analysis = (compile in Compile).value
    println(analysis)
    val lastCompDate: Long = analysis.compilations.allCompilations.headOption.map(_.startTime()).getOrElse(0)
    println(lastCompDate)
    val modified = analysis.relations.allProducts.filter(x => x.lastModified > lastCompDate)
    modified.foreach(println)
    */

    val actor = ngDeployer.value
    val files = baseDirectory.value / "src" / "main" / "java" ** "*.java"
    for(file <- files.get) {
      println(file)
      actor ! Deploy(file.getAbsolutePath)
    }

  }

  def deploySettings = Seq(deployTask)

}

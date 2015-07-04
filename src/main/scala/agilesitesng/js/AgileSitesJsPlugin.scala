package agilesitesng.js

import sbt._, Keys._
import scala.concurrent.duration._
import agilesites.config.AgileSitesConfigPlugin
import sbt.plugins.JvmPlugin
import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys

object AgileSitesJsPlugin
  extends AutoPlugin {

  override def requires = JvmPlugin && SbtWeb && AgileSitesConfigPlugin

  val autoImport = AgileSitesJsKeys

  import autoImport._
  import SbtWeb.autoImport._
  import WebKeys._

  val jsTask = js := {

    val modules = (nodeModules in Assets).value

    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed

    println(args.mkString(" "))

    if (args.length == 0) {
      println("usage: script [args...]")
    } else {
      val engine = JsEngineKeys.EngineType.Trireme
      val script = args(0)
      println(s">>> ${script}");
      SbtJsTask.executeJs(
        state.value,
        engine,
        None, // using trireme no command needed
        Seq("node_modules"),
        file(script),
        args.tail,
        1.minutes
      )
      println(s"<<< ${script}");
    }
  }

  override lazy val projectSettings = Seq(jsTask)

}
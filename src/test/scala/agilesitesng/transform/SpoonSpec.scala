package agilesitesng.transform

import org.scalatest.FreeSpec
import spoon.Launcher


class SpoonSpec extends FreeSpec {
  "spoon" in {

    val launcher = new Launcher
    //launcher.run(Array("-h"))
    println(".....")
    launcher.run(Array(
      "-vvv",
      //"--source-classpath",
      //"/Users/msciab/.ivy2/local/com.sciabarra/agilesites2-build/scala_2.10/sbt_0.13/11g-M3/jars/agilesites2-build.jar",
      "-i", "src/test/java",
      "-o", "target/spoon",
      "-p", "agilesitesng.deploy.spoon.NotNullCheckerProcessor"))

  }
}
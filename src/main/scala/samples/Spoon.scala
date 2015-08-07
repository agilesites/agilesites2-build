package samples

import spoon.Launcher

/**
 * Created by msciab on 05/08/15.
 */
object Spoon extends App {
  val launcher = new Launcher
  //launcher.run(Array("-gui"))
  launcher.run(Array(
    "-v",
    "--source-classpath",
    "/Users/msciab/.ivy2/local/com.sciabarra/agilesites2-build/scala_2.10/sbt_0.13/11g-M3/jars/agilesites2-build.jar",
    "-i", "src/test/java",
    "-o", "target/spoon",
    "--gui"
    //"-p", "agilesitesng.deploy.spoon.NotNullCheckerProcessor"
  ))

}

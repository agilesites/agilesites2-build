package agilesitesng.deploy.spoon

import java.io.File

/**
 * Created by msciab on 18/08/15.
 */
trait SpoonUtils {

  val outdir = new java.io.File(System.getProperty("spoon.outdir"))

  def writeFileOutdir(path: String, body: String): String = {
    val pathSplit = path.split("\\.")
    val file = new File(outdir, pathSplit.init.mkString("", File.separator, s".${pathSplit.last}"))
    file.getParentFile.mkdirs
    writeFile(file, body)
    file.getAbsolutePath
  }

  def writeFile(file: File, body: String) = {
    //println("*** %s%s****\n".format(file.toString, body))
    if (file.getParentFile != null)
      file.getParentFile.mkdirs
    val w = new java.io.FileWriter(file)
    w.write(body)
    w.close()
  }

  def class2file(className: String) = {
    val base = new File(System.getProperty("spoon.outdir"))
    val file = new File(base, className.replace('.', File.separatorChar)+".java")
    file.getAbsolutePath
  }
}

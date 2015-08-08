package agilesitesng.deploy.spoon

import java.io.File
import spoon.Launcher
import agilesitesng.deploy.model.{Uid, Spooler}

/**
 * Created by msciab on 08/08/15.
 */
object SpoonMain extends App {

  def writeFile(file: File, body: String) = {
    //println("*** %s%s****\n".format(file.toString, body))
    if (file.getParentFile != null)
      file.getParentFile.mkdirs
    val w = new java.io.FileWriter(file)
    w.write(body)
    w.close()
  }
  // initialize spoon
  val out = new java.io.File(System.getProperty("spoon.spool"))
  val uid = new java.io.File(System.getProperty("uid.properties"))
  Uid.init(uid)

  // run and save the spool file
  val spoon = new Launcher();
  spoon.run(args)
  writeFile(out, Spooler.save)

  /*
  val factory = spoon.getFactory();
  // list all packages of the model
  for (p <- factory.Package().getAll()) {

    System.out.println("package: " + p.getQualifiedName());
  }
  // list all classes of the model
  for (s <- factory.Class().getAll()) {
    System.out.println("class: " + s.getQualifiedName());
  }*/
}

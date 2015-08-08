package agilesitesng.deploy.model

import java.io.File

import agilesites.UidGenerator

/**
 * Created by msciab on 08/08/15.
 */
object Uid {
  var uidGen: UidGenerator = null

  def init(file: File) = {
    uidGen = new UidGenerator(file.getAbsolutePath)
  }

  def init(): Unit = {
    if (uidGen == null)
      init(new java.io.File(System.getProperty("uid.properties")))
  }

  def generate(key: String) = {
    init()
    if (uidGen.add(key))
      uidGen.save()
    uidGen.get(key).toString.toLong
  }

}

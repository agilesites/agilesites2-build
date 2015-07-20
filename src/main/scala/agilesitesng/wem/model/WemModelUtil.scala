package agilesitesng.wem.model

import java.io.{InputStream, FileInputStream, FileReader, File}

/**
 * Created by msciab on 17/07/15.
 */
trait WemModelUtil {

  import WemModel._

  def blobFromFile(file: File): Blob = {
    val filename = file.getName
    val foldername = if (file.getParentFile != null) file.getParentFile.getName else ""
    Blob(filename, foldername, base64file(file))
  }

  def blobFromFile(filename: String): Blob = blobFromFile(new java.io.File(filename))

  def readStream(in: InputStream, len: Long): Array[Byte] = {
    val r = new Array[Byte](len.toInt)
    in.read(r)
    r
  }

  def readFile(file: File): Array[Byte] = {
    val fis = new FileInputStream(file)
    val res = readStream(fis, file.length)
    fis.close
    res
  }

  import Base64._

  def base64file(file: File): String = readFile(file).toBase64

}

package agilesitesng.deploy.model

import java.io.{InputStream, FileInputStream, File}
import agilesitesng.deploy.model.AssetModel.Blob

import scala.collection.immutable.HashMap

/**
 * Created by msciab on 17/07/15.
 */
trait ModelUtil {

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

  object Base64 {

    private[this] val zero = Array(0, 0).map(_.toByte)
    class B64Scheme(val encodeTable: IndexedSeq[Char]) {
      lazy val decodeTable = HashMap(encodeTable.zipWithIndex: _ *)
    }

    lazy val base64 = new B64Scheme(('A' to 'Z') ++ ('a' to 'z') ++ ('0' to '9') ++ Seq('+', '/'))
    lazy val base64Url = new B64Scheme(base64.encodeTable.dropRight(2) ++ Seq('-', '_'))

    implicit class Encoder(b: Array[Byte]) {

      lazy val pad = (3 - b.length % 3) % 3

      def toBase64(implicit scheme: B64Scheme = base64): String = {
        def sixBits(x: Array[Byte]): Array[Int] = {
          val a = (x(0) & 0xfc) >> 2
          val b = ((x(0) & 0x3) << 4) | ((x(1) & 0xf0) >> 4)
          val c = ((x(1) & 0xf) << 2) | ((x(2) & 0xc0) >> 6)
          val d = (x(2)) & 0x3f
          Array(a, b, c, d)
        }
        ((b ++ zero.take(pad)).grouped(3)
          .flatMap(sixBits)
          .map(scheme.encodeTable)
          .toArray
          .dropRight(pad) :+ "=" * pad)
          .mkString
      }
    }

    implicit class Decoder(s: String) {
      lazy val cleanS = s.reverse.dropWhile(_ == '=').reverse
      lazy val pad = s.length - cleanS.length

      def toByteArray(implicit scheme: B64Scheme = base64): Array[Byte] = {
        def threeBytes(s: String): Array[Byte] = {
          val r = s.map(scheme.decodeTable(_)).foldLeft(0)((a, b) => (a << 6) | b)
          Array((r >> 16).toByte, (r >> 8).toByte, r.toByte)
        }
        if (pad > 2 || s.length % 4 != 0) throw new java.lang.IllegalArgumentException("Invalid Base64 String:" + s)
        try {
          (cleanS + "A" * pad)
            .grouped(4)
            .map(threeBytes)
            .flatten
            .toArray
            .dropRight(pad)
        } catch {case e:NoSuchElementException => throw new java.lang.IllegalArgumentException("Invalid Base64 String:" + s) }
      }
    }

  }
}

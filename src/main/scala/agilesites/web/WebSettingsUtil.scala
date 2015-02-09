package agilesites.web

import java.io.{File, FileInputStream}
import java.security.MessageDigest
import java.util.Formatter

trait WebUtil {

  // remove the prefix and normalize using forward slashes - the initial slash is removed
  def deprefixNomalizeFile(file: File, prefixLen: Int) =
    file.getAbsolutePath().substring(prefixLen + 1).replace(File.separator, "/")

  def byteToHex(hash: Array[Byte]) = {
    val formatter = new Formatter();
    for (b <- hash)
      formatter.format("%02x": String, b: java.lang.Byte);
    val result = formatter.toString();
    formatter.close();
    result
  }

  def md5(file: File): String = {
    val digest = MessageDigest.getInstance("MD5");
    val buffer = new Array[Byte](1024 * 1024)
    val is = new FileInputStream(file)
    var len = is.read(buffer)
    while (len > 0) {
      digest.update(buffer, 0, len)
      len = is.read(buffer)
    }
    byteToHex(digest.digest())
  }

  def strTail(full: String, delim: Char) = {
    val pos = full.lastIndexOf(delim);
    if (pos == -1)
      full;
    else
      full.substring(pos + 1);
  }

  def strHead(full: String, delim: Char) = {
    val pos = full.lastIndexOf(delim);
    if (pos == -1)
      full;
    else
      full.substring(0, pos);
  }

  def assetName(filepath: String, hash: String) = {
    val ext = strTail(filepath, '.');
    val filename = strTail(filepath.replace(File.separatorChar, '/'),
      '/');
    val basename = strHead(filename, '.');
    basename + "_" + hash + "." + ext;
  }

  def assetHashedFilePath(filepath: String, hash: String) = {
    val basedir = strHead(filepath, '/');
    if (basedir.equals(filepath))
      assetName(filepath, hash)
    else
      basedir + "/" + assetName(filepath, hash);

  }
}

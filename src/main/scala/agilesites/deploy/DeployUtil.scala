package agilesites.deploy

import java.net.URLDecoder

import com.ning.http.client.StringPart
import com.ning.http.multipart.FilePart
import sbt._
import dispatch._
import dispatch.Defaults._

/**
 * Created by msciab on 10/04/15.
 */
trait DeployUtil {

  def uploadJar(uri: URL, jar: File, log: Logger, sites: String, username: String, password: String) = {
    val path = URLDecoder.decode(uri.getPath, "UTF-8").substring(1)
    val base = host(uri.getHost, uri.getPort)
    val req = base / path / "Satellite" <<? Map("pagename" -> "AAAgileSetup")
    import scala.collection.JavaConverters._

    // hello, requesting the cookie
    val reqHello = req <<? Map("op" -> "hello")
    log.debug(reqHello.toRequest.getRawUrl)

    val cookies = Http(reqHello).apply.getCookies.asScala
    log.debug(s"hello ${cookies} ")

    // key, requesting the key
    val reqKey = cookies.foldLeft(req)(_.addCookie(_)) <<? Map("op" -> "key")
    log.debug(reqKey.toRequest.getRawUrl)
    val key = Http(reqKey).apply.getResponseBody.trim
    log.debug(s"key ${key} ")

    // upload the jar
    val reqFile = req.setMethod("POST").
      setHeader("Content-Type", "multipart/form-data").
      addBodyPart(new FilePart("jar", jar)).
      addBodyPart(new StringPart("op", "upload")).
      addBodyPart(new StringPart("_authkey_", key)).
      addBodyPart(new StringPart("username", username)).
      addBodyPart(new StringPart("password", password)).
      addBodyPart(new StringPart("sites", sites))

    val reqFile1 = cookies.foldLeft(reqFile)(_.addCookie(_))
    val resFile = Http(reqFile1).apply.getResponseBody.trim
    log.info(s"${resFile}")
    resFile
  }

}

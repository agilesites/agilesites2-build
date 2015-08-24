package agilesitesng.deploy

import agilesites.config.AgileSitesConfigKeys._
import agilesitesng.Utils
import agilesitesng.deploy.actor.DeployProtocol._
import agilesitesng.deploy.model.Spooler
import agilesitesng.setup.NgSetupKeys
import agilesitesng.setup.NgSetupPlugin._
import akka.pattern.ask
import akka.util.Timeout
import sbt.{AutoPlugin, _}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by msciab on 04/08/15.
 */
trait DeploySettings {
  this: AutoPlugin with Utils =>

  import NgDeployKeys._

  implicit val timeout = Timeout(10.seconds)

  val loginTask = login in ng := {
    val surl = sitesUrl.value
    println(s">>> ServiceLogin(${surl}) ")
    val hub = ngDeployHub.value
    val r = hub ? ServiceLogin(url(surl), sitesUser.value, sitesPassword.value)
    val msg = try {
      val ServiceReply(msg) = Await.result(r, 3.second)
      msg
    } catch {
      case ex: Exception => "ERR: " + ex.getMessage
    }
    println(s"<<< ServiceReply(${msg})")

  }

  val deployTask = deploy in ng := {

    (login in ng).value

    val hub = ngDeployHub.value
    val spool = (spoon in ng).toTask("").value

    hub ! SpoonInit()
    val deployObjects = Spooler.load(readFile(spool))
    for (dobj <- deployObjects.deployObjects) {
      //println(s" sending ${dobj}")
      hub ! SpoonData(dobj)
    }
    val SpoonReply(result) = Await.result(hub ? SpoonRun(""), 10.seconds)

    println(result)

  }

  val serviceTask = service in ng := {

    val args: Seq[String] = Def.spaceDelimited("<arg>").parsed
    if (args.size == 0) {
      println("usage: ngService <op> <key=value>")
      "no args"
    } else {
      val opts = args.tail.map(s => if (s.indexOf("=") == -1) "value=" + s else s).mkString("&", "&", "")
      val req = s"${sitesUrl.value}/ContentServer?pagename=AAAgileService&op=${args.head}&username=${sitesUser.value}&password=${sitesPassword.value}${opts}"
      println(">>> " + req)
      val r = httpCallRaw(req)
      println(r)
      r
    }
  }

  def deploySettings = Seq(serviceTask, deployTask, loginTask)


}
